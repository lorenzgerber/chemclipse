/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.support;

import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractSupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileIdentifier;

public class MassSpectrumDatabaseIdentifier extends AbstractSupplierFileIdentifier implements ISupplierFileIdentifier {

	public MassSpectrumDatabaseIdentifier() {
		super(MassSpectrumConverter.getMassSpectrumConverterSupport().getSupplier());
	}

	@Override
	public String getType() {

		return TYPE_DATABASE_MSD;
	}
}
