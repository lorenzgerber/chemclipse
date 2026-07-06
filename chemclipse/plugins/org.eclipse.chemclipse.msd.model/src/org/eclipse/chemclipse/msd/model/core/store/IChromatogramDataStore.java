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

import java.io.Closeable;

/**
 * Compact storage backend for MSD chromatogram ion data.
 * Scan indices are zero-based. The storeIndex on AbstractScanMSD corresponds
 * directly to the scanIndex parameter here.
 */
public interface IChromatogramDataStore extends Closeable {

	int getScanCount();

	int getIonCount(int scanIndex);

	double getMz(int scanIndex, int ionIndex);

	float getIntensity(int scanIndex, int ionIndex);

	/** Bulk read of m/z values into dest. Returns number of values written. */
	int getMzValues(int scanIndex, double[] dest);

	/** Bulk read of intensity values into dest. Returns number of values written. */
	int getIntensityValues(int scanIndex, float[] dest);

	/** Sum of all intensities for the scan — tight primitive loop, no IIon created. */
	float getTotalSignal(int scanIndex);

	/** Finalise after import: trim arrays, mark read-only. */
	void seal();

	/** Release backing resources (file handle, temp file). */
	@Override
	void close();
}
