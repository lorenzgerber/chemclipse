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
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSamples<V extends IVariable, S extends ISample> implements ISamples<V, S> {

	private List<S> samples;
	private List<V> variables;

	public AbstractSamples() {

		samples = new ArrayList<>();
		variables = new ArrayList<>();
	}

	@Override
	public List<S> getSamples() {

		return samples;
	}

	@Override
	public List<V> getVariables() {

		return variables;
	}
}