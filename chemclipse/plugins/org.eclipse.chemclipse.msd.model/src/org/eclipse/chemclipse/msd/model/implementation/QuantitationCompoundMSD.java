/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.quantitation.AbstractQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;

public class QuantitationCompoundMSD extends AbstractQuantitationCompoundMSD implements IQuantitationCompoundMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 2376307650470986106L;

	public QuantitationCompoundMSD(String name, String concentrationUnit, int retentionTime) {
		super(name, concentrationUnit, retentionTime);
	}
}
