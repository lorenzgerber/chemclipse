/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.massspectrum;

import org.eclipse.chemclipse.converter.core.AbstractExportConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.MassSpectrumExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

/**
 * @author eselmeister
 */
public abstract class AbstractMassSpectrumExportConverter extends AbstractExportConverter implements IMassSpectrumExportConverter {

	@Override
	public IMassSpectrumExportConverterProcessingInfo validate(IScanMSD massSpectrum) {

		IMassSpectrumExportConverterProcessingInfo processingInfo = new MassSpectrumExportConverterProcessingInfo();
		if(massSpectrum == null) {
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Mass Spectra Export", "The is no mass spectrum to export.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}

	@Override
	public IMassSpectrumExportConverterProcessingInfo validate(IMassSpectra massSpectra) {

		IMassSpectrumExportConverterProcessingInfo processingInfo = new MassSpectrumExportConverterProcessingInfo();
		if(massSpectra == null) {
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Mass Spectra Export", "The are no mass spectra to export.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}
}
