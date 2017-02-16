/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ui.service.swt.charts.line;

import org.eclipse.chemclipse.ui.service.swt.charts.IPointSeriesSettings;
import org.eclipse.swt.graphics.Color;

public interface ILineSeriesSettings extends IPointSeriesSettings {

	int getAntialias();

	/**
	 * SWT.DEFAULT, SWT.ON, SWT.OFF
	 * 
	 * @param antialias
	 */
	void setAntialias(int antialias);

	boolean isEnableArea();

	void setEnableArea(boolean enableArea);

	Color getLineColor();

	void setLineColor(Color lineColor);

	int getLineWidth();

	void setLineWidth(int lineWidth);

	boolean isEnableStack();

	void setEnableStack(boolean enableStack);

	boolean isEnableStep();

	void setEnableStep(boolean enableStep);
}