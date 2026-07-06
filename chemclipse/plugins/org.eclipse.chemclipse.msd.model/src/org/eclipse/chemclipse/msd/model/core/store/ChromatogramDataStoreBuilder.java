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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Converter-facing builder for IChromatogramDataStore.
 * Replaces per-format VendorScanProxy patterns with a single generic call sequence.
 * Scans must be added in ascending order. MRM/tandem scans (IIonTransition != null)
 * are excluded — use addIon(IIon) on AbstractScanMSD directly for those.
 */
public class ChromatogramDataStoreBuilder {

	public enum StoreType {
		HEAP, MAPPED
	}

	private final StoreType storeType;
	private final HeapChromatogramDataStore heap;

	public ChromatogramDataStoreBuilder(int estimatedScanCount, StoreType storeType) {

		this.storeType = storeType;
		int estimatedIonsPerScan = 500;
		heap = new HeapChromatogramDataStore(estimatedScanCount, estimatedScanCount * estimatedIonsPerScan);
	}

	public ChromatogramDataStoreBuilder(int estimatedScanCount, int estimatedIonsPerScan, StoreType storeType) {

		this.storeType = storeType;
		heap = new HeapChromatogramDataStore(estimatedScanCount, estimatedScanCount * estimatedIonsPerScan);
	}

	public void beginScan(int scanIndex) {

		heap.beginScan(scanIndex);
	}

	public void addIon(double mz, float abundance) {

		heap.addIon(mz, abundance);
	}

	public void addIons(double[] mzs, float[] abundances, int count) {

		heap.addIons(mzs, abundances, count);
	}

	public void endScan(int scanIndex) {

		heap.endScan(scanIndex);
	}

	/**
	 * Seals and returns the finished store.
	 * For MAPPED, writes ion data to a temp file and returns a MappedChromatogramDataStore.
	 */
	public IChromatogramDataStore build() throws IOException {

		heap.seal();
		if(storeType == StoreType.HEAP) {
			return heap;
		}
		// MAPPED: write heap contents to temp file, then open as mapped store
		return writeMappedStore(heap);
	}

	private static IChromatogramDataStore writeMappedStore(HeapChromatogramDataStore src) throws IOException {

		Path temp = Files.createTempFile("chemclipse-msd-", ".bin");
		temp.toFile().deleteOnExit();
		int scanCount = src.getScanCount();
		// Scan offset tracking (byte offsets in the file)
		long[] scanFileOffsets = new long[scanCount];
		try(OutputStream fos = Files.newOutputStream(temp, StandardOpenOption.WRITE);
				DataOutputStream out = new DataOutputStream(fos)) {
			// Write placeholder header (24 bytes)
			out.writeInt(MappedChromatogramDataStore.MAGIC);
			out.writeInt(MappedChromatogramDataStore.VERSION);
			out.writeInt(scanCount);
			out.writeLong(0L); // indexOffset placeholder
			out.writeInt(0); // reserved
			long bytePos = MappedChromatogramDataStore.HEADER_SIZE;
			// Write ion data scan by scan
			double[] mzBuf = new double[0];
			float[] abBuf = new float[0];
			for(int s = 0; s < scanCount; s++) {
				int ionCount = src.getIonCount(s);
				if(ionCount > mzBuf.length) {
					mzBuf = new double[ionCount];
					abBuf = new float[ionCount];
				}
				src.getMzValues(s, mzBuf);
				src.getIntensityValues(s, abBuf);
				scanFileOffsets[s] = bytePos;
				for(int i = 0; i < ionCount; i++) {
					out.writeDouble(mzBuf[i]);
					out.writeFloat(abBuf[i]);
				}
				bytePos += (long)ionCount * 12L;
			}
			// bytePos is now at the scan index position; write it
			for(int s = 0; s < scanCount; s++) {
				out.writeLong(scanFileOffsets[s]);
				out.writeInt(src.getIonCount(s));
				out.writeInt(0); // reserved
			}
		}
		// Patch the indexOffset field in the header (at byte offset 12: int+int+int)
		try(FileChannel ch = FileChannel.open(temp, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
			// Recompute indexOffset: header(24) + all ion data
			long totalIons = 0;
			for(int s = 0; s < scanCount; s++) {
				totalIons += src.getIonCount(s);
			}
			long indexOffset = MappedChromatogramDataStore.HEADER_SIZE + totalIons * 12L;
			java.nio.ByteBuffer patch = java.nio.ByteBuffer.allocate(8);
			patch.order(ByteOrder.BIG_ENDIAN);
			patch.putLong(indexOffset);
			patch.flip();
			ch.write(patch, 12); // offset 12 = after magic(4)+version(4)+scanCount(4)
		}
		return new MappedChromatogramDataStore(temp);
	}
}
