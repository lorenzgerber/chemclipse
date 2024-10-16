/*******************************************************************************
 * Copyright (c) 2015, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to API Changes
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractPeaks;
import org.eclipse.chemclipse.model.core.IPeak;

public class Peaks extends AbstractPeaks<IPeak> {

	public Peaks() {
		super(IPeak.class);
	}
}
