/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.modifier;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class FilterModifier extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final String description = "RTShifter Filter";
	private static final String FILTER_ID = "org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter";
	private int millisecondsToShift;

	public FilterModifier(IChromatogramSelection chromatogramSelection, int millisecondsToShift) {
		super(chromatogramSelection);
		this.millisecondsToShift = millisecondsToShift;
	}

	@Override
	public void execute(IProgressMonitor monitor) {

		if(getChromatogramSelection() != null) {
			IChromatogramSelection chromatogramSelection = getChromatogramSelection();
			boolean isShiftAllScans = PreferenceSupplier.getIsShiftAllScans();
			ISupplierFilterSettings chromatogramFilterSettings = new SupplierFilterSettings(millisecondsToShift, isShiftAllScans);
			final IChromatogramFilterProcessingInfo processingInfo = ChromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, FILTER_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
		}
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * The doOperation calls the execute of the chromatogram modifier
		 * method.<br/> Why not doing it directly? Model and GUI should be
		 * handled separately.
		 */
		try {
			monitor.beginTask("RTShifter Filter", IProgressMonitor.UNKNOWN);
			getChromatogramSelection().getChromatogram().doOperation(this, monitor);
		} finally {
			monitor.done();
		}
	}
}
