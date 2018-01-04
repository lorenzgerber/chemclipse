/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.series;

import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.MultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.Series;

import junit.framework.TestCase;

public class MultipleSeries_2_Test extends TestCase {

	private ISeries series;
	private double[] xSeries;
	private double[] ySeries;
	private String id;
	private IMultipleSeries multipleSeries;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		multipleSeries = new MultipleSeries();
		// ------------------------------
		xSeries = new double[0];
		ySeries = new double[0];
		id = "FirstId";
		series = new Series(xSeries, ySeries, id);
		multipleSeries.add(series);
		// ------------------------------
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		multipleSeries = null;
	}

	public void testMultipleSeries_1() {

		assertEquals("Size", 1, multipleSeries.getMultipleSeries().size());
	}

	public void testMultipleSeries_2() {

		assertEquals("XMin", 0.0d, multipleSeries.getXMin());
	}

	public void testMultipleSeries_3() {

		assertEquals("XMax", 0.0d, multipleSeries.getXMax());
	}

	public void testMultipleSeries_4() {

		assertEquals("YMin", 0.0d, multipleSeries.getYMin());
	}

	public void testMultipleSeries_5() {

		assertEquals("YMax", 0.0d, multipleSeries.getYMax());
	}
}
