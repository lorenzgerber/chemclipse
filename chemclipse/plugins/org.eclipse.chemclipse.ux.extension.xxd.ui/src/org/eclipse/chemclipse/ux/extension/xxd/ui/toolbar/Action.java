/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

import org.eclipse.chemclipse.support.text.ILabel;

public enum Action implements ILabel {
	SHOW("Show"), //
	HIDE("Hide");

	private String label = "";

	private Action(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public String id() {

		return label.toLowerCase();
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}