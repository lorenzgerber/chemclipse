/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - added a title field
 * Lorenz Gerber - added Opls Target Group field
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IPreprocessingSettings;

public interface IAnalysisSettings {

	void setTitle(String title);

	String getTitle();

	void setNumberOfPrincipalComponents(int numberOfPrincipalComponents);

	int getNumberOfPrincipalComponents();

	Algorithm getAlgorithm();

	void setAlgorithm(Algorithm algorithm);

	String getOplsTargetGroupName();

	void setOplsTargetGroupName(String groupName);

	boolean isRemoveUselessVariables();

	void setRemoveUselessVariables(boolean removeUselessVariables);

	LabelOptionPCA getLabelOptionPCA();

	void setLabelOptionPCA(LabelOptionPCA labelOptionPCA);

	void setPreprocessingSettings(IPreprocessingSettings preprocessingSettings);

	IPreprocessingSettings getPreprocessingSettings();

	void setColorScheme(String colorScheme);

	String getColorScheme();
}