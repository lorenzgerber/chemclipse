/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.vsd.model.core;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakModel;

public abstract class AbtractPeakModelVSD extends PeakModel implements IPeakModelVSD {

	private static final long serialVersionUID = -7270343994063667596L;

	public AbtractPeakModelVSD(IScan peakMaximum, IPeakIntensityValues peakIntensityValues, float startBackgroundAbundance, float stopBackgroundAbundance, int b) throws IllegalArgumentException, PeakException {

		super(peakMaximum, peakIntensityValues, startBackgroundAbundance, stopBackgroundAbundance);
	}
}