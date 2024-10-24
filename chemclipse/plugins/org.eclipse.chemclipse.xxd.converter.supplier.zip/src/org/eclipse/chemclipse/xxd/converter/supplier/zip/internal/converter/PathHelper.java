/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.zip.internal.converter;

import java.io.File;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;

public class PathHelper {

	private static final Logger logger = Logger.getLogger(PathHelper.class);
	public static final String ZIP_SUPPLIER = "org.eclipse.chemclipse.msd.converter.supplier.zip";
	private static final String IMPORT = "Import";
	private static final String EXPORT = "Export";

	public static File getStoragePathImport() {

		return getStoragePath(IMPORT);
	}

	public static File getStoragePathExport() {

		return getStoragePath(EXPORT);
	}

	/**
	 * Returns the file object (directory) where the chromatogram instances can
	 * temporarily be stored.
	 * 
	 * @return File
	 */
	private static File getStoragePath(String directory) {

		StringBuilder builder = new StringBuilder();
		builder.append(ApplicationSettings.getSettingsDirectory().getAbsolutePath());
		builder.append(File.separator);
		builder.append(ZIP_SUPPLIER);
		builder.append(File.separator);
		builder.append(directory);
		//
		File file = new File(builder.toString());
		/*
		 * Create the directory if it not exists.
		 */
		if(!file.exists()) {
			if(!file.mkdirs()) {
				logger.warn("The temporarily zip converter directory could not be created: " + file.getAbsolutePath());
			}
		}
		return file;
	}

	/**
	 * DO NOT CALL THIS METHOD IF YOU NOT REALLY KNOW WHAT YOU ARE DOING.<br/>
	 * Cleans all temporarily stored files in the storage directory.<br/>
	 * This method will be called on bundle start and stop.
	 */
	public static void cleanStoragePathImport() {

		File directory = getStoragePath(IMPORT);
		deleteFiles(directory);
	}

	/**
	 * DO NOT CALL THIS METHOD IF YOU NOT REALLY KNOW WHAT YOU ARE DOING.<br/>
	 * Cleans all temporarily stored files in the storage directory.<br/>
	 * This method will be called on bundle start and stop.
	 */
	public static void cleanStoragePathExport() {

		File directory = getStoragePath(EXPORT);
		deleteFiles(directory);
	}

	/**
	 * Deletes the given directory recursively.
	 * 
	 * @param directory
	 */
	private static void deleteFiles(File directory) {

		/*
		 * Delete all files in all directories.
		 */
		for(File file : directory.listFiles()) {
			if(file.isDirectory()) {
				deleteFiles(file);
			} else {
				if(!file.delete()) {
					logger.warn("The file " + file + "could not be deleted.");
				}
			}
		}
		/*
		 * Delete the directory if all files have been removed.
		 */
		if(!directory.delete()) {
			logger.warn("The directory " + directory + "could not be deleted.");
		}
	}
}
