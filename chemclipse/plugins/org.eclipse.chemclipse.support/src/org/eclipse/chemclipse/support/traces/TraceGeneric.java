/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.traces;

public class TraceGeneric extends AbstractTrace {

	public int getTrace() {

		return getValueAsInt();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getTrace());
		builder.append(getScaleFactorAsString());
		//
		return builder.toString();
	}
}