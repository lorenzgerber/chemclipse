/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - getting rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import javafx.embed.swt.FXCanvas;
import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class ScorePlot3D {

	private ScorePlot3DAxes axes;
	private FXCanvas fxCanvas;
	private final double rotateModifier = 10;
	private ScorePlot3DScatter scatter;
	private ScorePlot3DSettings settings;

	public ScorePlot3D(Composite parent, int style) {
		/*
		 * JavaFX init
		 */
		fxCanvas = new FXCanvas(parent, style);
		fxCanvas.setLayoutData(null);
		settings = new ScorePlot3DSettings(800);
		axes = new ScorePlot3DAxes(settings);
		axes.buildAxes();
		scatter = new ScorePlot3DScatter(settings);
		/*
		 * update scene after resize
		 */
		parent.addListener(SWT.Resize, (event) -> createScene());
		createScene();
	}

	public void removeData() {

		scatter = new ScorePlot3DScatter(settings);
		createScene();
		createScene();
	}

	@SuppressWarnings("rawtypes")
	public void setInput(EvaluationPCA evaluationPCA) {

		if(evaluationPCA != null) {
			IResultsPCA resultsPCA = evaluationPCA.getResults();
			ScorePlot3DSettings.setSettings(settings, resultsPCA);
			ScorePlot3DSettings.setAxesEnhanced(settings, 800);
			axes = new ScorePlot3DAxes(settings);
			axes.buildAxes();
			scatter = new ScorePlot3DScatter(settings, resultsPCA);
			createScene();
		}
	}

	public void updateSelection() {

		scatter.updateSelection();
		fxCanvas.redraw();
	}

	public ScorePlot3DSettings getSettings() {

		return settings;
	}

	private void createScene() {

		/*
		 * create data
		 */
		Group root = new Group();
		AmbientLight ambientlight = new AmbientLight();
		Group mainGroup = new Group();
		/*
		 * set camera
		 */
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setTranslateZ(-4000);
		camera.setNearClip(0.01);
		camera.setFarClip(10000.0);
		root.getChildren().addAll(mainGroup, ambientlight, camera);
		Group objects = new Group();
		objects.getChildren().addAll(scatter.getScarter(), axes);
		Rotate rotate = new Rotate(180, 0, 0, 0, Rotate.X_AXIS);
		objects.getTransforms().add(rotate);
		mainGroup.getChildren().add(objects);
		/*
		 * built header
		 */
		BorderPane borderPane = new BorderPane();
		/*
		 * build central subscene, which contains chart
		 */
		Point sizeScene = fxCanvas.getParent().getSize();
		SubScene mainScene;
		mainScene = new SubScene(root, sizeScene.x, sizeScene.y - borderPane.getHeight(), true, SceneAntialiasing.BALANCED);
		mainScene.setFill(Color.WHITE);
		mainScene.setCamera(camera);
		makeZoomable(mainScene, mainGroup);
		mousePressedOrMoved(mainScene, mainGroup);
		/*
		 * create scene
		 */
		BorderPane pane;
		pane = new BorderPane(mainScene, borderPane, null, null, null);
		pane.layout();
		Scene scene = new Scene(pane, sizeScene.x, sizeScene.y);
		fxCanvas.setScene(scene);
		pane.setCenter(mainScene);
		/*
		 * adjust size composite
		 */
		fxCanvas.getParent().layout(true);
	}

	private void makeZoomable(SubScene scene, Group group) {

		final double MAX_SCALE = 200000.0;
		final double MIN_SCALE = 0.1;
		scene.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {

				double delta = 1.2;
				double scale = group.getScaleX();
				if(event.getDeltaY() < 0) {
					scale /= delta;
				} else {
					scale *= delta;
				}
				scale = (scale < MAX_SCALE ? scale : MAX_SCALE);
				scale = (MIN_SCALE < scale ? scale : MIN_SCALE);
				group.setScaleX(scale);
				group.setScaleY(scale);
			}
		});
	}

	private void mousePressedOrMoved(SubScene sceneRoot, Group group) {

		Rotate xRotate = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
		Rotate yRotate = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
		group.getTransforms().addAll(xRotate, yRotate);
		sceneRoot.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {

			private double mouseXold = 0;
			private double mouseYold = 0;

			@Override
			public void handle(MouseEvent event) {

				if(event.getEventType() == MouseEvent.MOUSE_PRESSED || event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
					double mouseXnew = event.getSceneX();
					double mouseYnew = event.getSceneY();
					if(event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
						double pitchRotate = xRotate.getAngle() + (mouseYnew - mouseYold) / rotateModifier;
						xRotate.setAngle(pitchRotate);
						double yawRotate;
						double a = Math.abs((pitchRotate % 360));
						if(a < 90 || a > 270) {
							yawRotate = yRotate.getAngle() - (mouseXnew - mouseXold) / rotateModifier;
						} else {
							yawRotate = yRotate.getAngle() + (mouseXnew - mouseXold) / rotateModifier;
						}
						yRotate.setAngle(yawRotate);
					}
					mouseXold = mouseXnew;
					mouseYold = mouseYnew;
				}
			}
		});
	}
}
