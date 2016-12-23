/*******************************************************************************
 * Copyright (c) 2012, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ChromatogramComparisonResult;
import org.eclipse.chemclipse.model.identifier.ChromatogramLibraryInformation;
import org.eclipse.chemclipse.model.identifier.IChromatogramComparisonResult;
import org.eclipse.chemclipse.model.identifier.IChromatogramLibraryInformation;
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.IPeakLibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.chromatogram.IChromatogramTargetMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramTarget;
import org.eclipse.chemclipse.msd.model.implementation.IntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.MassSpectrometer;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.MassSpectrumType;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class ChromatogramReader_0701 extends AbstractChromatogramReader implements IChromatogramMSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader_0701.class);

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSD chromatogram = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				monitor.subTask(IConstants.IMPORT_CHROMATOGRAM);
				chromatogram = readFromZipFile(zipFile, file, monitor);
			}
		} finally {
			zipFile.close();
		}
		//
		return chromatogram;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramOverview chromatogramOverview = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				chromatogramOverview = readOverviewFromZipFile(zipFile, monitor);
			}
		} finally {
			zipFile.close();
		}
		//
		return chromatogramOverview;
	}

	private IChromatogramMSD readFromZipFile(ZipFile zipFile, File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = new VendorChromatogram();
		/*
		 * Read the chromatographic information.
		 */
		readScans(zipFile, chromatogram, monitor);
		readPeaks(zipFile, chromatogram, monitor);
		readArea(zipFile, chromatogram, monitor);
		readIdentification(zipFile, chromatogram, monitor);
		readHistory(zipFile, chromatogram, monitor);
		readMiscellaneous(zipFile, chromatogram, monitor);
		//
		setAdditionalInformation(file, chromatogram, monitor);
		//
		return chromatogram;
	}

	private IChromatogramOverview readOverviewFromZipFile(ZipFile zipFile, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_TIC);
		//
		IVendorChromatogram chromatogram = new VendorChromatogram();
		readScansOverview(dataInputStream, chromatogram, monitor);
		//
		dataInputStream.close();
		//
		return chromatogram;
	}

	private void readScansOverview(DataInputStream dataInputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		IVendorScan massSpectrum;
		IVendorIon ion;
		/*
		 * Retention Times - Total Signals
		 */
		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; scan++) {
			massSpectrum = new VendorScan();
			int retentionTime = dataInputStream.readInt(); // Retention Time
			float abundance = dataInputStream.readFloat(); // Total Signal
			try {
				ion = new VendorIon(AbstractIon.TIC_ION, abundance);
				massSpectrum.setRetentionTime(retentionTime);
				massSpectrum.addIon(ion);
				chromatogram.addScan(massSpectrum);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
	}

	private void setAdditionalInformation(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) {

		chromatogram.setConverterId(IFormat.CONVERTER_ID);
		chromatogram.setFile(file);
		// Delay
		int startRetentionTime = chromatogram.getStartRetentionTime();
		int scanDelay = startRetentionTime;
		chromatogram.setScanDelay(scanDelay);
		// Interval
		int endRetentionTime = chromatogram.getStopRetentionTime();
		int scanInterval = endRetentionTime / chromatogram.getNumberOfScans();
		chromatogram.setScanInterval(scanInterval);
	}

	private void readScans(ZipFile zipFile, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_SCANS);
		/*
		 * Scans
		 */
		int scans = dataInputStream.readInt();
		for(int scan = 1; scan <= scans; scan++) {
			monitor.subTask(IConstants.IMPORT_SCAN + scan);
			IVendorScan massSpectrum = readMassSpectrum(dataInputStream);
			chromatogram.addScan(massSpectrum);
		}
		//
		dataInputStream.close();
	}

	private void readPeaks(ZipFile zipFile, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_PEAKS);
		//
		int numberOfPeaks = dataInputStream.readInt(); // Number of Peaks
		for(int i = 1; i <= numberOfPeaks; i++) {
			monitor.subTask(IConstants.IMPORT_PEAK + i);
			try {
				IChromatogramPeakMSD peak = readPeak(dataInputStream, chromatogram, monitor);
				chromatogram.addPeak(peak);
			} catch(IllegalArgumentException e) {
				logger.warn(e);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
		dataInputStream.close();
	}

	private IChromatogramPeakMSD readPeak(DataInputStream dataInputStream, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException, IllegalArgumentException, PeakException {

		String detectorDescription = readString(dataInputStream); // Detector Description
		String integratorDescription = readString(dataInputStream); // Integrator Description
		String modelDescription = readString(dataInputStream); // Model Description
		PeakType peakType = PeakType.valueOf(readString(dataInputStream)); // Peak Type
		//
		float startBackgroundAbundance = dataInputStream.readFloat(); // Start Background Abundance
		float stopBackgroundAbundance = dataInputStream.readFloat(); // Stop Background Abundance
		//
		IPeakMassSpectrum peakMaximum = readPeakMassSpectrum(dataInputStream);
		//
		int numberOfRetentionTimes = dataInputStream.readInt(); // Number Retention Times
		IPeakIntensityValues intensityValues = new PeakIntensityValues(Float.MAX_VALUE);
		for(int i = 1; i <= numberOfRetentionTimes; i++) {
			int retentionTime = dataInputStream.readInt(); // Retention Time
			float relativeIntensity = dataInputStream.readFloat(); // Intensity
			intensityValues.addIntensityValue(retentionTime, relativeIntensity);
		}
		intensityValues.normalize();
		//
		IPeakModelMSD peakModel = new PeakModelMSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		IChromatogramPeakMSD peak = new ChromatogramPeakMSD(peakModel, chromatogram);
		peak.setDetectorDescription(detectorDescription);
		peak.setIntegratorDescription(integratorDescription);
		peak.setModelDescription(modelDescription);
		peak.setPeakType(peakType);
		//
		List<IIntegrationEntry> integrationEntries = readIntegrationEntries(dataInputStream);
		peak.setIntegratedArea(integrationEntries, integratorDescription);
		//
		readPeakIdentificationTargets(dataInputStream, peak, monitor);
		//
		return peak;
	}

	private List<IIntegrationEntry> readIntegrationEntries(DataInputStream dataInputStream) throws IOException {

		List<IIntegrationEntry> integrationEntries = new ArrayList<IIntegrationEntry>();
		int numberOfIntegrationEntries = dataInputStream.readInt(); // Number Integration Entries
		for(int i = 1; i <= numberOfIntegrationEntries; i++) {
			double ion = dataInputStream.readDouble(); // m/z
			double integratedArea = dataInputStream.readDouble(); // Integrated Area
			IIntegrationEntryMSD integrationEntry = new IntegrationEntryMSD(ion, integratedArea);
			integrationEntries.add(integrationEntry);
		}
		return integrationEntries;
	}

	private void readPeakIdentificationTargets(DataInputStream dataInputStream, IPeakMSD peak, IProgressMonitor monitor) throws IOException {

		int numberOfPeakTargets = dataInputStream.readInt(); // Number Peak Targets
		for(int i = 1; i <= numberOfPeakTargets; i++) {
			//
			String identifier = readString(dataInputStream); // Identifier
			//
			String casNumber = readString(dataInputStream); // CAS-Number
			String comments = readString(dataInputStream); // Comments
			String miscellaneous = readString(dataInputStream); // Miscellaneous
			String name = readString(dataInputStream); // Name
			//
			float matchFactor = dataInputStream.readFloat(); // Match Factor
			float reverseMatchFactor = dataInputStream.readFloat(); // Reverse Match Factor
			float probability = dataInputStream.readFloat(); // Probability
			//
			IPeakLibraryInformation libraryInformation = new PeakLibraryInformation();
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setName(name);
			IPeakComparisonResult comparisonResult = new PeakComparisonResult(matchFactor, reverseMatchFactor, 0.0f, 0.0f, probability);
			try {
				IPeakTarget identificationEntry = new PeakTarget(libraryInformation, comparisonResult);
				identificationEntry.setIdentifier(identifier);
				peak.addTarget(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	private void readArea(ZipFile zipFile, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_AREA);
		//
		String chromatogramIntegratorDescription = readString(dataInputStream); // Chromatogram Integrator Description
		List<IIntegrationEntry> chromatogramIntegrationEntries = readIntegrationEntries(dataInputStream);
		chromatogram.setChromatogramIntegratedArea(chromatogramIntegrationEntries, chromatogramIntegratorDescription);
		//
		String backgroundIntegratorDescription = readString(dataInputStream); // Background Integrator Description
		List<IIntegrationEntry> backgroundIntegrationEntries = readIntegrationEntries(dataInputStream);
		chromatogram.setBackgroundIntegratedArea(backgroundIntegrationEntries, backgroundIntegratorDescription);
		//
		dataInputStream.close();
	}

	private void readIdentification(ZipFile zipFile, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_IDENTIFICATION);
		//
		int numberOfTargets = dataInputStream.readInt(); // Number of Targets
		for(int i = 1; i <= numberOfTargets; i++) {
			//
			String identifier = readString(dataInputStream); // Identifier
			//
			String casNumber = readString(dataInputStream); // CAS-Number
			String comments = readString(dataInputStream); // Comments
			String miscellaneous = readString(dataInputStream); // Miscellaneous
			String name = readString(dataInputStream); // Name
			//
			float matchFactor = dataInputStream.readFloat(); // Match Factor
			float reverseMatchFactor = dataInputStream.readFloat(); // Reverse Match Factor
			float probability = dataInputStream.readFloat(); // Probability
			//
			IChromatogramLibraryInformation libraryInformation = new ChromatogramLibraryInformation();
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setName(name);
			IChromatogramComparisonResult comparisonResult = new ChromatogramComparisonResult(matchFactor, reverseMatchFactor, 0.0f, 0.0f, probability);
			try {
				IChromatogramTargetMSD identificationEntry = new ChromatogramTarget(libraryInformation, comparisonResult);
				identificationEntry.setIdentifier(identifier);
				chromatogram.addTarget(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
		//
		dataInputStream.close();
	}

	private void readHistory(ZipFile zipFile, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_HISTORY);
		//
		IEditHistory editHistory = chromatogram.getEditHistory();
		int numberOfEntries = dataInputStream.readInt(); // Number of entries
		for(int i = 1; i <= numberOfEntries; i++) {
			long time = dataInputStream.readLong(); // Date
			String description = readString(dataInputStream); // Description
			//
			Date date = new Date(time);
			IEditInformation editInformation = new EditInformation(date, description);
			editHistory.add(editInformation);
		}
		//
		dataInputStream.close();
	}

	private void readMiscellaneous(ZipFile zipFile, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_MISC);
		//
		long time = dataInputStream.readLong(); // Date
		String miscInfo = readString(dataInputStream); // Miscellaneous Info
		String operator = readString(dataInputStream); // Operator
		//
		Date date = new Date(time);
		chromatogram.setDate(date);
		chromatogram.setMiscInfo(miscInfo);
		chromatogram.setOperator(operator);
		//
		dataInputStream.close();
	}

	private IVendorScan readMassSpectrum(DataInputStream dataInputStream) throws IOException {

		String massSpectrometer = readString(dataInputStream); // Mass Spectrometer
		String massSpectrumType = readString(dataInputStream); // Mass Spectrum Type
		IVendorScan massSpectrum = new VendorScan();
		/*
		 * MassSpectrometer and MassSpectrumType
		 * Convert from the 0.7.0 enum format to short value.
		 */
		massSpectrum.setMassSpectrometer(getMassSpectrometerValue(massSpectrometer));
		massSpectrum.setMassSpectrumType(getMassSpectrumTypeValue(massSpectrumType));
		int retentionTime = dataInputStream.readInt(); // Retention Time
		float retentionIndex = dataInputStream.readFloat(); // Retention Index
		massSpectrum.setRetentionTime(retentionTime);
		massSpectrum.setRetentionIndex(retentionIndex);
		int numberOfIons = dataInputStream.readInt(); // Number of ions
		for(int i = 1; i <= numberOfIons; i++) {
			/*
			 * Read Ions
			 */
			try {
				double mz = dataInputStream.readDouble(); // m/z
				float abundance = dataInputStream.readFloat(); // Abundance
				IVendorIon ion = new VendorIon(mz, abundance);
				massSpectrum.addIon(ion);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
		return massSpectrum;
	}

	private IPeakMassSpectrum readPeakMassSpectrum(DataInputStream dataInputStream) throws IOException {

		String massSpectrometer = readString(dataInputStream); // Mass Spectrometer
		String massSpectrumType = readString(dataInputStream); // Mass Spectrum Type
		IPeakMassSpectrum massSpectrum = new PeakMassSpectrum();
		/*
		 * MassSpectrometer and MassSpectrumType
		 * Convert from the 0.7.0 enum format to short value.
		 */
		massSpectrum.setMassSpectrometer(getMassSpectrometerValue(massSpectrometer));
		massSpectrum.setMassSpectrumType(getMassSpectrumTypeValue(massSpectrumType));
		int retentionTime = dataInputStream.readInt(); // Retention Time
		float retentionIndex = dataInputStream.readFloat(); // Retention Index
		massSpectrum.setRetentionTime(retentionTime);
		massSpectrum.setRetentionIndex(retentionIndex);
		int numberOfIons = dataInputStream.readInt(); // Number of ions
		for(int i = 1; i <= numberOfIons; i++) {
			/*
			 * Read Ions
			 */
			try {
				double mz = dataInputStream.readDouble(); // m/z
				float abundance = dataInputStream.readFloat(); // Abundance
				IVendorIon ion = new VendorIon(mz, abundance);
				massSpectrum.addIon(ion);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
		return massSpectrum;
	}

	/**
	 * Legacy.
	 * Convert between Enum and short value.
	 * 
	 * @param massSpectrometer
	 * @return
	 */
	private short getMassSpectrometerValue(String massSpectrometer) {

		MassSpectrometer detector = MassSpectrometer.valueOf(massSpectrometer);
		short value = 1; // MS1
		if(detector == MassSpectrometer.MS2) {
			value = 2;
		} else if(detector == MassSpectrometer.MS3) {
			value = 3;
		}
		return value;
	}

	/**
	 * Legacy.
	 * Convert between Enum and short value.
	 * 
	 * @param massSpectrometer
	 * @return
	 */
	private short getMassSpectrumTypeValue(String massSpectrumType) {

		MassSpectrumType msType = MassSpectrumType.valueOf(massSpectrumType);
		short value = 0; // centroid
		if(msType == MassSpectrumType.PROFILE) {
			value = 1;
		}
		return value;
	}

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		if(version.equals(IFormat.VERSION_0701)) {
			isValid = true;
		}
		//
		dataInputStream.close();
		//
		return isValid;
	}
}
