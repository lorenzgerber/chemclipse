/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.swt.ISelectionHandler;
import org.eclipse.chemclipse.ux.extension.ui.swt.WelcomeTile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class WelcomeView {

	private static final String PERSPECTIVE_DATA_ANALYSIS = "org.eclipse.chemclipse.ux.extension.xxd.ui.perspective.main";
	private static final String PERSPECTIVE_QUANTITATION = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.perspective";
	private static final String PERSPECTIVE_LOGGING = "org.eclipse.chemclipse.logging.ui.perspective.main";
	private static final String PERSPECTIVE_MSD = "org.eclipse.chemclipse.chromatogram.msd.perspective.ui.perspective.main";
	private static final String PERSPECTIVE_CSD = "org.eclipse.chemclipse.ux.extension.csd.ui.perspective.main";
	private static final String PERSPECTIVE_WSD = "org.eclipse.chemclipse.ux.extension.wsd.ui.perspective.main";
	//
	// private static final String CSS_ID = "org-eclipse-chemclipse-ux-extension-ui-views-welcomeview-background";
	/*
	 * Context and services
	 */
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;
	@Inject
	private IEventBroker eventBroker;
	/*
	 * Main
	 */
	private List<WelcomeTile> welcomeTiles;

	private class Component1 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_DATA_ANALYSIS);
		}
	}

	private class Component2 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_QUANTITATION);
		}
	}

	private class Component3 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_LOGGING);
		}
	}

	private class Component4 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_DATA_ANALYSIS);
			Display.getCurrent().asyncExec(new Runnable() {

				@Override
				public void run() {

					try {
						URL url = new URL(Platform.getInstallLocation().getURL() + "DemoChromatogram.ocb");
						File file = new File(url.getFile());
						if(file.exists()) {
							/*
							 * Get the editor part stack.
							 */
							MPartStack partStack = (MPartStack)modelService.find("org.eclipse.e4.primaryDataStack", application);
							/*
							 * Create the input part and prepare it.
							 */
							MPart part = MBasicFactory.INSTANCE.createInputPart();
							part.setElementId("org.eclipse.chemclipse.ux.extension.msd.ui.part.chromatogramEditor");
							part.setContributionURI("bundleclass://org.eclipse.chemclipse.ux.extension.msd.ui/org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD");
							part.setObject(file.getAbsolutePath());
							part.setIconURI("platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif");
							part.setLabel(file.getName());
							part.setTooltip("Chromatogram - Detector Type: MSD");
							part.setCloseable(true);
							/*
							 * Add it to the stack and show it.
							 */
							partStack.getChildren().add(part);
							partService.showPart(part, PartState.ACTIVATE);
						}
					} catch(Exception e) {
						System.out.println(e);
					}
				}
			});
		}
	}

	private class Component5 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_MSD);
		}
	}

	private class Component6 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_CSD);
		}
	}

	private class Component7 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_WSD);
		}
	}

	@Inject
	public WelcomeView(Composite parent) {
		welcomeTiles = new ArrayList<WelcomeTile>();
		initializeContent(parent);
	}

	@Focus
	public void setFocus() {

	}

	private void initializeContent(Composite parent) {

		parent.setLayout(new FillLayout());
		//
		Composite composite = new Composite(parent, SWT.NONE);
		// composite.setData(CSSSWTConstants.CSS_ID_KEY, CSS_ID);
		// composite.setBackground(Colors.WHITE);
		composite.setLayout(new GridLayout(4, false));
		//
		createContent(composite);
	}

	private void createContent(Composite parent) {

		/*
		 * Important for a transparent background
		 * of the contained components.
		 */
		parent.setBackgroundMode(SWT.INHERIT_FORCE);
		//
		Image imageDataAnalysis = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_DATA_ANALYSIS, IApplicationImage.SIZE_128x128);
		//
		WelcomeTile composite1 = new WelcomeTile(parent, SWT.NONE);
		WelcomeTile composite2 = new WelcomeTile(parent, SWT.NONE);
		WelcomeTile composite3 = new WelcomeTile(parent, SWT.NONE);
		WelcomeTile composite4 = new WelcomeTile(parent, SWT.NONE);
		WelcomeTile composite5 = new WelcomeTile(parent, SWT.NONE);
		WelcomeTile composite6 = new WelcomeTile(parent, SWT.NONE);
		WelcomeTile composite7 = new WelcomeTile(parent, SWT.NONE);
		//
		initializeTile(composite1, 2, 2, new Component1(), imageDataAnalysis, "Data Analysis", "This is the main perspective. Most of the work is performed here.");
		initializeTile(composite2, 1, 1, new Component2(), null, "Quantitation", "Used for ISTD and ESTD quantitation");
		initializeTile(composite3, 1, 1, new Component3(), null, "Logging", "Have a look at the log files.");
		initializeTile(composite4, 2, 1, new Component4(), null, "Demo", "Load a demo chromatogram.");
		initializeTile(composite5, 2, 1, new Component5(), null, "MSD", "Mass Selective Detector (Quadrupole, IonTrap, TandenMS, HighRes)");
		initializeTile(composite6, 1, 1, new Component6(), null, "CSD", "Current Selective Detector (FID, PPD, ...)");
		initializeTile(composite7, 1, 1, new Component7(), null, "WSD", "Wavelength Selective Detector (UV/Vis, VWD, DAD)");
	}

	private void initializeTile(WelcomeTile welcomeTile, int horizontalSpan, int verticalSpan, ISelectionHandler selectionHandler, Image image, String section, String description) {

		welcomeTiles.add(welcomeTile);
		//
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = horizontalSpan;
		gridData.verticalSpan = verticalSpan;
		welcomeTile.setLayoutData(gridData);
		//
		welcomeTile.setSelectionHandler(selectionHandler);
		welcomeTile.setContent(image, section, description);
		//
		welcomeTile.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {

				highlightComposite(welcomeTile);
			}
		});
	}

	private void highlightComposite(WelcomeTile welcomeTileHighlight) {

		for(WelcomeTile welcomeTile : welcomeTiles) {
			if(welcomeTile == welcomeTileHighlight) {
				welcomeTile.setActive();
			} else {
				welcomeTile.setInactive();
			}
		}
	}

	/**
	 * The method is available in the org.eclipse.chemclipse.rcp.app.ui package too,
	 * but it can't be referenced, because app.ui depends on the logging
	 * package.
	 * Furthermore, this is only a workaround, because setting an initial
	 * perspective doesn't work.
	 */
	private void switchPerspective(String perspectiveId) {

		MUIElement element = modelService.find(perspectiveId, application);
		if(element instanceof MPerspective) {
			MPerspective perspective = (MPerspective)element;
			partService.switchPerspective(perspective);
			if(eventBroker != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, perspective.getLabel());
			}
		}
	}
}