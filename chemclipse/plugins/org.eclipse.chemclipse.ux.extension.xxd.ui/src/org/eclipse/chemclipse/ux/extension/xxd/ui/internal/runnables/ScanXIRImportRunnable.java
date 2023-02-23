/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.xir.converter.core.ScanConverterXIR;
import org.eclipse.chemclipse.xir.model.core.ISpectrumXIR;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ScanXIRImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ScanXIRImportRunnable.class);
	//
	private File file;
	private ISpectrumXIR scanXIR = null;

	public ScanXIRImportRunnable(File file) {

		this.file = file;
	}

	public ISpectrumXIR getScanXIR() {

		return scanXIR;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(ExtensionMessages.importScan, IProgressMonitor.UNKNOWN);
			IProcessingInfo<?> processingInfo = ScanConverterXIR.convert(file, monitor);
			scanXIR = (ISpectrumXIR)processingInfo.getProcessingResult();
		} catch(Exception e) {
			logger.error(e);
		} finally {
			monitor.done();
		}
	}
}
