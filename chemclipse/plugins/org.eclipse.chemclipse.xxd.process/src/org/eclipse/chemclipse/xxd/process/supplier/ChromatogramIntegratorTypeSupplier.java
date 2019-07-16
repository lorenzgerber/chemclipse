/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add datatypes to supplier
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.IChromatogramIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.IChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramIntegratorTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	private static final DataType[] DATA_TYPES = new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD};
	public static final String CATEGORY = "Chromatogram Integrator";
	private static final Logger logger = Logger.getLogger(ChromatogramIntegratorTypeSupplier.class);

	public ChromatogramIntegratorTypeSupplier() {
		super(CATEGORY);
		try {
			IChromatogramIntegratorSupport support = ChromatogramIntegrator.getChromatogramIntegratorSupport();
			for(String processorId : support.getAvailableIntegratorIds()) {
				IChromatogramIntegratorSupplier supplier = support.getIntegratorSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, DATA_TYPES);
				processorSupplier.setName(supplier.getIntegratorName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoIntegratorAvailableException e) {
			logger.warn(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processSettings instanceof IChromatogramIntegrationSettings) {
			return ChromatogramIntegrator.integrate(chromatogramSelection, (IChromatogramIntegrationSettings)processSettings, processorId, monitor);
		} else {
			return ChromatogramIntegrator.integrate(chromatogramSelection, processorId, monitor);
		}
	}
}
