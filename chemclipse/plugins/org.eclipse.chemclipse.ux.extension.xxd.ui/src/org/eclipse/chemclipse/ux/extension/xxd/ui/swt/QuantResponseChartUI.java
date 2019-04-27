/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignals;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.IEquation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.CalibrationChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageQuantitationAxes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.CalibrationChartSupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.LineChart;

public class QuantResponseChartUI extends Composite {

	private CalibrationChartSupport calibrationChartSupport = new CalibrationChartSupport();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private Composite toolbarInfo;
	private Label labelInfo;
	private CalibrationChart calibrationChart;
	private IQuantitationCompound quantitationCompound;

	public QuantResponseChartUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void update(IQuantitationCompound quantitationCompound) {

		this.quantitationCompound = quantitationCompound;
		setQuantitationCompound();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		toolbarInfo = createToolbarInfo(composite);
		createCalibrationChart(composite);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createButtonToggleToolbarInfo(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfo = new Label(composite, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				calibrationChart.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createToggleLegendMarkerButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend marker.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_LEGEND_MARKER, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				calibrationChart.togglePositionLegendVisibility();
				calibrationChart.redraw();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chart");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageQuantitationAxes()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void createCalibrationChart(Composite parent) {

		calibrationChart = new CalibrationChart(parent, SWT.NONE);
		calibrationChart.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void applySettings() {

		calibrationChart.modifyAxes(true);
		setQuantitationCompound();
	}

	private void reset() {

		setQuantitationCompound();
	}

	private void setQuantitationCompound() {

		labelInfo.setText("");
		calibrationChart.deleteSeries();
		//
		if(quantitationCompound != null) {
			//
			labelInfo.setText("Quantitation Compound: " + quantitationCompound.getName());
			//
			List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
			ILineSeriesData lineSeriesDataPoints;
			ILineSeriesData lineSeriesDataEquation;
			//
			IColorScheme colors = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_CALIBRATION));
			calibrationChart.setConcentrationLabel(quantitationCompound.getConcentrationUnit());
			boolean useCrossZero = quantitationCompound.isCrossZero();
			CalibrationMethod calibrationMethod = quantitationCompound.getCalibrationMethod();
			IResponseSignals concentrationResponseEntries = quantitationCompound.getResponseSignals();
			Set<Double> signals = concentrationResponseEntries.getSignalSet();
			for(double signal : signals) {
				/*
				 * Signals
				 */
				Color color = colors.getColor();
				String label = Double.toString(signal);
				List<IResponseSignal> signalEntries = concentrationResponseEntries.getList(signal);
				lineSeriesDataPoints = calibrationChartSupport.getLineSeriesData(signalEntries, label, color);
				if(lineSeriesDataPoints != null) {
					/*
					 * Data Points
					 */
					lineSeriesDataList.add(lineSeriesDataPoints);
					/*
					 * Equation
					 */
					IEquation equation = concentrationResponseEntries.getLinearEquation(signal, useCrossZero);
					Point pointMin = new Point(0, 0);
					double x = Arrays.stream(lineSeriesDataPoints.getSeriesData().getXSeries()).max().getAsDouble();
					double y = Arrays.stream(lineSeriesDataPoints.getSeriesData().getYSeries()).max().getAsDouble();
					double xMax = x + x / 20.0d;
					double yMax = y + y / 20.0d;
					Point pointMax = new Point(xMax, yMax);
					lineSeriesDataEquation = calibrationChartSupport.getLineSeriesData(signalEntries, equation, calibrationMethod, useCrossZero, pointMin, pointMax, label, color);
					if(lineSeriesDataEquation != null) {
						lineSeriesDataList.add(lineSeriesDataEquation);
					}
				}
				colors.incrementColor();
			}
			//
			calibrationChart.addSeriesData(lineSeriesDataList, LineChart.NO_COMPRESSION);
		}
	}
}
