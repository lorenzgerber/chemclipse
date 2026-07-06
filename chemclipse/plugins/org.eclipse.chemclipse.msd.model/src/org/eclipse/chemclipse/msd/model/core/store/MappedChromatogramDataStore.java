/*******************************************************************************
 * Copyright (c) 2025 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.store;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * File-backed store using memory-mapped I/O.
 * Ion data lives in the OS page cache — zero Java heap pressure.
 * Supports files larger than 2 GB via multiple 1 GB mapped segments.
 *
 * Binary file layout (big-endian):
 * <pre>
 * HEADER (24 bytes)
 *   int  magic        = 0x4D534430 ('MSD0')
 *   int  version      = 1
 *   int  scanCount
 *   long indexOffset  (byte offset to SCAN INDEX section)
 *   int  reserved     = 0
 *
 * ION DATA  (interleaved, 12 bytes per ion)
 *   double mz         (8 bytes)
 *   float  abundance  (4 bytes)
 *
 * SCAN INDEX  at indexOffset  (scanCount x 16 bytes each)
 *   long dataOffset  (byte offset of scan's ion block)
 *   int  ionCount
 *   int  reserved    = 0
 * </pre>
 */
public class MappedChromatogramDataStore implements IChromatogramDataStore {

	static final int MAGIC = 0x4D534430;
	static final int VERSION = 1;
	static final int HEADER_SIZE = 24;
	/**
	 * 1 GB segments — chosen as a multiple of 12 (ion record size) so no ion
	 * record ever straddles a buffer boundary.
	 */
	static final long SEGMENT_SIZE = 89_478_485L * 12L; // 1_073_741_820 bytes

	private MappedByteBuffer[] buffers;
	private long[] bufferBase;
	private long[] scanOffsets;
	private int[] scanLengths;
	private int scanCount;
	private Path tempPath;

	MappedChromatogramDataStore(Path dataFile) throws IOException {

		tempPath = dataFile;
		try(FileChannel channel = FileChannel.open(dataFile, StandardOpenOption.READ)) {
			// Read header
			ByteBuffer header = ByteBuffer.allocate(HEADER_SIZE);
			header.order(ByteOrder.BIG_ENDIAN);
			channel.read(header, 0);
			header.flip();
			int magic = header.getInt();
			if(magic != MAGIC) {
				throw new IOException("Invalid data store file (bad magic: 0x" + Integer.toHexString(magic) + ")");
			}
			int version = header.getInt();
			if(version != VERSION) {
				throw new IOException("Unsupported data store version: " + version);
			}
			scanCount = header.getInt();
			long indexOffset = header.getLong();
			// header.getInt(); // reserved — already consumed by position
			// Read scan index into Java arrays (small, always in heap)
			scanOffsets = new long[scanCount];
			scanLengths = new int[scanCount];
			if(scanCount > 0) {
				ByteBuffer indexBuf = ByteBuffer.allocate(scanCount * 16);
				indexBuf.order(ByteOrder.BIG_ENDIAN);
				channel.read(indexBuf, indexOffset);
				indexBuf.flip();
				for(int i = 0; i < scanCount; i++) {
					scanOffsets[i] = indexBuf.getLong();
					scanLengths[i] = indexBuf.getInt();
					indexBuf.getInt(); // reserved
				}
			}
			// Map the entire file in SEGMENT_SIZE segments
			long fileSize = channel.size();
			int numBuffers = (int)((fileSize + SEGMENT_SIZE - 1) / SEGMENT_SIZE);
			buffers = new MappedByteBuffer[numBuffers];
			bufferBase = new long[numBuffers];
			for(int i = 0; i < numBuffers; i++) {
				long start = (long)i * SEGMENT_SIZE;
				long length = Math.min(SEGMENT_SIZE, fileSize - start);
				buffers[i] = channel.map(FileChannel.MapMode.READ_ONLY, start, length);
				buffers[i].order(ByteOrder.BIG_ENDIAN);
				bufferBase[i] = start;
			}
		}
		// channel closed — mappings remain valid on Unix (POSIX mmap semantics)
	}

	private double readDouble(long globalOffset) {

		int idx = bufferIndex(globalOffset);
		return buffers[idx].getDouble((int)(globalOffset - bufferBase[idx]));
	}

	private float readFloat(long globalOffset) {

		int idx = bufferIndex(globalOffset);
		return buffers[idx].getFloat((int)(globalOffset - bufferBase[idx]));
	}

	private int bufferIndex(long globalOffset) {

		return Math.min((int)(globalOffset / SEGMENT_SIZE), buffers.length - 1);
	}

	@Override
	public int getScanCount() {

		return scanCount;
	}

	@Override
	public int getIonCount(int scanIndex) {

		return scanLengths[scanIndex];
	}

	@Override
	public double getMz(int scanIndex, int ionIndex) {

		// ion record layout: [double mz (8)] [float abundance (4)] = 12 bytes
		return readDouble(scanOffsets[scanIndex] + (long)ionIndex * 12L);
	}

	@Override
	public float getIntensity(int scanIndex, int ionIndex) {

		return readFloat(scanOffsets[scanIndex] + (long)ionIndex * 12L + 8L);
	}

	@Override
	public int getMzValues(int scanIndex, double[] dest) {

		long base = scanOffsets[scanIndex];
		int count = scanLengths[scanIndex];
		for(int i = 0; i < count; i++) {
			dest[i] = readDouble(base + (long)i * 12L);
		}
		return count;
	}

	@Override
	public int getIntensityValues(int scanIndex, float[] dest) {

		long base = scanOffsets[scanIndex];
		int count = scanLengths[scanIndex];
		for(int i = 0; i < count; i++) {
			dest[i] = readFloat(base + (long)i * 12L + 8L);
		}
		return count;
	}

	@Override
	public float getTotalSignal(int scanIndex) {

		long base = scanOffsets[scanIndex];
		int count = scanLengths[scanIndex];
		float sum = 0f;
		for(int i = 0; i < count; i++) {
			sum += readFloat(base + (long)i * 12L + 8L);
		}
		return sum;
	}

	@Override
	public void seal() {

		// already read-only after construction
	}

	@Override
	public void close() {

		buffers = null;
		if(tempPath != null) {
			try {
				Files.deleteIfExists(tempPath);
			} catch(IOException e) {
				// best-effort; deleteOnExit is the safety net
			}
			tempPath = null;
		}
	}
}
