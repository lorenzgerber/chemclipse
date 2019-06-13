/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.io.File;

import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.xxd.process.preferences.PreferenceSupplier;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChromatogramExportSettings implements IProcessSettings {

	public static final String VARIABLE_CHROMATOGRAM_NAME = "{chromatogram_name}";
	public static final String VARIABLE_EXTENSION = "{extension}";
	@JsonProperty(value = "Export Folder", defaultValue = "")
	@FileSettingProperty(onlyDirectory = true)
	private File exportFolder;
	@JsonProperty(value = "Filename", defaultValue = VARIABLE_CHROMATOGRAM_NAME + VARIABLE_EXTENSION)
	private String filenamePattern = VARIABLE_CHROMATOGRAM_NAME + VARIABLE_EXTENSION;

	public File getExportFolder() {

		if(exportFolder == null) {
			String folder = PreferenceSupplier.getChromatogramExportFolder();
			if(folder != null && !folder.isEmpty()) {
				return new File(folder);
			}
		}
		return exportFolder;
	}

	public void setExportFolder(File exportFolder) {

		this.exportFolder = exportFolder;
	}

	public String getFileNamePattern() {

		if(filenamePattern == null) {
			// for backward compatibility
			return VARIABLE_CHROMATOGRAM_NAME;
		}
		return filenamePattern;
	}
}
