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
package org.eclipse.chemclipse.converter.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.processing.DataCategory;

public class MethodProcessSupport_5_Test extends MethodProcessSupportTestCase {

	public void test1() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD, DataCategory.VSD};
		List<DataCategory[]> processCategories = new ArrayList<>();
		processCategories.add(new DataCategory[]{DataCategory.CSD, DataCategory.MSD});
		processCategories.add(new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD});
		processCategories.add(new DataCategory[]{DataCategory.CSD}); // Match
		processCategories.add(new DataCategory[]{DataCategory.CSD, DataCategory.VSD});
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(1, dataCategories.length);
		assertEquals("CSD", dataCategories[0].name());
	}

	public void test2() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD, DataCategory.VSD};
		List<DataCategory[]> processCategories = new ArrayList<>();
		processCategories.add(new DataCategory[]{DataCategory.CSD, DataCategory.MSD});
		processCategories.add(new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD});
		processCategories.add(new DataCategory[]{DataCategory.MSD}); // Match
		processCategories.add(new DataCategory[]{DataCategory.CSD, DataCategory.VSD});
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(1, dataCategories.length);
		assertEquals("MSD", dataCategories[0].name());
	}

	public void test3() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD, DataCategory.VSD};
		List<DataCategory[]> processCategories = new ArrayList<>();
		processCategories.add(new DataCategory[]{DataCategory.CSD, DataCategory.MSD});
		processCategories.add(new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD});
		processCategories.add(new DataCategory[]{DataCategory.MSD}); // First Match
		processCategories.add(new DataCategory[]{DataCategory.CSD});
		processCategories.add(new DataCategory[]{DataCategory.CSD, DataCategory.VSD});
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(1, dataCategories.length);
		assertEquals("MSD", dataCategories[0].name());
	}
}