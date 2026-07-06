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

import java.util.Arrays;

/**
 * Heap-backed store using flat primitive arrays.
 * Eliminates Java object overhead (~3x memory reduction vs List&lt;IIon&gt;).
 * Immutable after seal().
 */
public class HeapChromatogramDataStore implements IChromatogramDataStore {

	private double[] mzFlat;
	private float[] intensitiesFlat;
	private int[] scanOffsets;
	private int[] scanLengths;
	private int scanCount;
	private int capacity;
	private int totalIons;
	private int currentScan = -1;
	private boolean sealed = false;

	HeapChromatogramDataStore(int estimatedScanCount, int estimatedTotalIons) {

		mzFlat = new double[Math.max(estimatedTotalIons, 1)];
		intensitiesFlat = new float[Math.max(estimatedTotalIons, 1)];
		scanOffsets = new int[Math.max(estimatedScanCount, 1)];
		scanLengths = new int[Math.max(estimatedScanCount, 1)];
		capacity = mzFlat.length;
	}

	void beginScan(int scanIndex) {

		if(scanIndex >= scanOffsets.length) {
			int newLen = Math.max(scanIndex + 1, (int)(scanOffsets.length * 1.5));
			scanOffsets = Arrays.copyOf(scanOffsets, newLen);
			scanLengths = Arrays.copyOf(scanLengths, newLen);
		}
		currentScan = scanIndex;
		scanOffsets[scanIndex] = totalIons;
		scanLengths[scanIndex] = 0;
	}

	void addIon(double mz, float abundance) {

		ensureIonCapacity(1);
		mzFlat[totalIons] = mz;
		intensitiesFlat[totalIons] = abundance;
		totalIons++;
		scanLengths[currentScan]++;
	}

	void addIons(double[] mzs, float[] abundances, int count) {

		ensureIonCapacity(count);
		System.arraycopy(mzs, 0, mzFlat, totalIons, count);
		System.arraycopy(abundances, 0, intensitiesFlat, totalIons, count);
		totalIons += count;
		scanLengths[currentScan] += count;
	}

	void endScan(int scanIndex) {

		// ion counts already tracked during addIon/addIons
	}

	private void ensureIonCapacity(int additional) {

		int required = totalIons + additional;
		if(required <= capacity) {
			return;
		}
		int newCapacity = Math.max(required, (int)(capacity * 1.5) + 1);
		mzFlat = Arrays.copyOf(mzFlat, newCapacity);
		intensitiesFlat = Arrays.copyOf(intensitiesFlat, newCapacity);
		capacity = newCapacity;
	}

	@Override
	public void seal() {

		if(sealed) {
			return;
		}
		scanCount = (currentScan < 0) ? 0 : currentScan + 1;
		mzFlat = Arrays.copyOf(mzFlat, totalIons);
		intensitiesFlat = Arrays.copyOf(intensitiesFlat, totalIons);
		scanOffsets = Arrays.copyOf(scanOffsets, scanCount);
		scanLengths = Arrays.copyOf(scanLengths, scanCount);
		sealed = true;
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

		return mzFlat[scanOffsets[scanIndex] + ionIndex];
	}

	@Override
	public float getIntensity(int scanIndex, int ionIndex) {

		return intensitiesFlat[scanOffsets[scanIndex] + ionIndex];
	}

	@Override
	public int getMzValues(int scanIndex, double[] dest) {

		int offset = scanOffsets[scanIndex];
		int length = scanLengths[scanIndex];
		System.arraycopy(mzFlat, offset, dest, 0, length);
		return length;
	}

	@Override
	public int getIntensityValues(int scanIndex, float[] dest) {

		int offset = scanOffsets[scanIndex];
		int length = scanLengths[scanIndex];
		System.arraycopy(intensitiesFlat, offset, dest, 0, length);
		return length;
	}

	@Override
	public float getTotalSignal(int scanIndex) {

		int offset = scanOffsets[scanIndex];
		int length = scanLengths[scanIndex];
		float sum = 0f;
		for(int i = 0; i < length; i++) {
			sum += intensitiesFlat[offset + i];
		}
		return sum;
	}

	@Override
	public void close() {

		// no resources to release for heap store
	}
}
