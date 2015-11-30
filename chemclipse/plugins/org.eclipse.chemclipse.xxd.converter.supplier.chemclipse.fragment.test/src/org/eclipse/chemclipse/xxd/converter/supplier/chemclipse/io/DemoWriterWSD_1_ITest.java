/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.io;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramOverviewImportConverterProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.converter.processing.chromatogram.IChromatogramWSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.IVendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.IVendorScanSignal;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram.VendorScanSignal;
import org.eclipse.core.runtime.NullProgressMonitor;

public class DemoWriterWSD_1_ITest extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		//
	}

	public void testReader_1() throws Exception {

		// tests the reader & writer for wsd
		String export = null;
		assertNotNull("Please specify an export path.", export);
		File file = new File(export);
		//
		IVendorChromatogram chromatogram = new VendorChromatogram();
		chromatogram.setConverterId("");
		int retentionTime = 500;
		int scanInterval = 1000;
		chromatogram.setScanDelay(retentionTime);
		chromatogram.setScanInterval(scanInterval);
		//
		for(int i = 0; i < 100; i++) {
			IVendorScan scan = new VendorScan();
			scan.setRetentionTime(retentionTime);
			for(int j = 250; j < 350; j++) {
				IVendorScanSignal scanSignal = new VendorScanSignal();
				scanSignal.setWavelength(j);
				scanSignal.setAbundance(getAbundance());
				scan.addScanSignal(scanSignal);
			}
			chromatogram.addScan(scan);
			retentionTime += scanInterval;
		}
		//
		ChromatogramConverterWSD.convert(file, chromatogram, "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse", new NullProgressMonitor());
		//
		IChromatogramWSDImportConverterProcessingInfo processingInfo = ChromatogramConverterWSD.convert(file, "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse", new NullProgressMonitor());
		IChromatogramOverviewImportConverterProcessingInfo processingInfo_overview = ChromatogramConverterWSD.convertOverview(file, "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse", new NullProgressMonitor());
		//
		IVendorChromatogram read_chromatogram = (VendorChromatogram)processingInfo.getChromatogram();
		//
		IVendorChromatogram read_overview = (VendorChromatogram)processingInfo_overview.getChromatogramOverview();
		//
		int _retentionTime;
		int wavelength;
		float abundance;
		//
		// String id = read_chromatogram.getConverterId();
		// int scanDelay = read_chromatogram.getScanDelay();
		// int scanInterval = read_chromatogram.getScanInterval();
		//
		// assertEquals(id, chromatogram.getConverterId());
		// assertEquals(scanDelay, chromatogram.getScanDelay());
		// assertEquals(scanInterval, chromatogram.getScanInterval());
		//
		for(int i = 1; i <= 100; i++) {
			IVendorScan scan_read = (VendorScan)read_chromatogram.getScan(i);
			IVendorScan scan_orig = (VendorScan)chromatogram.getScan(i);
			_retentionTime = scan_read.getRetentionTime();
			assertEquals(_retentionTime, scan_orig.getRetentionTime());
			// add rententionTime to Data Structure
			//
			for(int j = 0; j < 100; j++) {
				IVendorScanSignal scanSignal_read = (VendorScanSignal)scan_read.getScanSignal(j);
				IVendorScanSignal scanSignal_orig = (VendorScanSignal)scan_orig.getScanSignal(j);
				wavelength = scanSignal_read.getWavelength();
				abundance = scanSignal_read.getAbundance();
				assertEquals(wavelength, scanSignal_orig.getWavelength());
				assertEquals(abundance, scanSignal_orig.getAbundance());
				// scan.removeScanSignal(j); // expects an int
			}
		}
		//
		System.out.println("Trying overview test.");
		for(int i = 1; i <= 100; i++) {
			IVendorScan scan_read = (VendorScan)read_overview.getScan(i);
			IVendorScan scan_orig = (VendorScan)chromatogram.getScan(i);
			_retentionTime = scan_read.getRetentionTime();
			assertEquals(_retentionTime, scan_orig.getRetentionTime());
			// add rententionTime to Data Structure
			//
			for(int j = 0; j < 100; j++) {
				IVendorScanSignal scanSignal_read = (VendorScanSignal)scan_read.getScanSignal(j);
				IVendorScanSignal scanSignal_orig = (VendorScanSignal)scan_orig.getScanSignal(j);
				wavelength = scanSignal_read.getWavelength();
				abundance = scanSignal_read.getAbundance();
				assertEquals(wavelength, scanSignal_orig.getWavelength());
				assertEquals(abundance, scanSignal_orig.getAbundance());
				// scan.removeScanSignal(j); // expects an int
			}
		}
		System.out.println("Done");
	}

	private float getAbundance() {

		float abundance = (float)Math.random();
		if(abundance == 0) {
			abundance = 0.56f;
		}
		return abundance;
	}
}
