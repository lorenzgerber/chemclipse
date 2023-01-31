/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v10.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * List and descriptions of instrument configurations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstrumentConfigurationListType", propOrder = {"instrumentConfiguration"})
public class InstrumentConfigurationListType {

	@XmlElement(required = true)
	protected List<InstrumentConfigurationType> instrumentConfiguration;
	@XmlAttribute(name = "count", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	protected BigInteger count;

	public List<InstrumentConfigurationType> getInstrumentConfiguration() {

		if(instrumentConfiguration == null) {
			instrumentConfiguration = new ArrayList<>();
		}
		return this.instrumentConfiguration;
	}

	public BigInteger getCount() {

		return count;
	}

	public void setCount(BigInteger value) {

		this.count = value;
	}
}
