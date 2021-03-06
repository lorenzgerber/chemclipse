/*******************************************************************************
 * Copyright (c) 2013, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for MALDI
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.io.IFormat;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.io.MassSpectrumReaderVersion105;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.xxd.converter.supplier.io.exception.UnknownVersionException;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		final IMassSpectraReader massSpectraReader = getReader(file);
		if(massSpectraReader != null) {
			return massSpectraReader.read(file, monitor);
		} else {
			return null;
		}
	}

	public static IMassSpectraReader getReader(final File file) throws IOException {

		IMassSpectraReader massSpectraReader = null;
		//
		final FileReader fileReader = new FileReader(file);
		final char[] charBuffer = new char[350];
		fileReader.read(charBuffer);
		fileReader.close();
		//
		final String header = new String(charBuffer);
		if(header.contains(IFormat.V_105)) {
			massSpectraReader = new MassSpectrumReaderVersion105(IFormat.CONTEXT_PATH_V_105);
		} else {
			throw new UnknownVersionException();
		}
		//
		return massSpectraReader;
	}
}
