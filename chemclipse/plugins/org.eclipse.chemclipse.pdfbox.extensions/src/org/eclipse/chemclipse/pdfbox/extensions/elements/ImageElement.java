/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pdfbox.extensions.elements;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ImageElement extends AbstractReferenceElement<ImageElement> {

	private PDImageXObject image;
	private float width = 0.0f;
	private float height = 0.0f;

	public ImageElement(float x, float y) {
		setX(x);
		setY(y);
	}

	public PDImageXObject getImage() {

		return image;
	}

	public ImageElement setImage(PDImageXObject image) {

		this.image = image;
		return this;
	}

	public float getWidth() {

		return width;
	}

	public ImageElement setWidth(float width) {

		this.width = width;
		return this;
	}

	public float getHeight() {

		return height;
	}

	public ImageElement setHeight(float height) {

		this.height = height;
		return this;
	}
}
