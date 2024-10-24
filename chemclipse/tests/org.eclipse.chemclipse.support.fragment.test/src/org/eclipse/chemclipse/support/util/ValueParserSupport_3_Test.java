/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.util;

import junit.framework.TestCase;

public class ValueParserSupport_3_Test extends TestCase {

	private ValueParserSupport support = new ValueParserSupport();
	private String[] values;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		values = new String[]{Boolean.toString(true)};
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(1, values.length);
	}

	public void test2() {

		assertEquals(true, support.parseBoolean(values, 0));
	}

	public void test3() {

		assertEquals(true, support.parseBoolean(values, 0, false));
	}

	public void test4() {

		assertEquals(true, support.parseBoolean(values, 0, true));
	}

	public void test5() {

		assertEquals("true", support.parseString(values, 0));
	}

	public void test6() {

		assertEquals("true", support.parseString(values, 0, ""));
	}

	public void test7() {

		assertEquals("true", support.parseString(values, 0, "Test"));
	}

	public void test8() {

		assertEquals(0.0f, support.parseFloat(values, 0));
	}

	public void test9() {

		assertEquals(0.0f, support.parseFloat(values, 0, 0.0f));
	}

	public void test10() {

		assertEquals(1.0f, support.parseFloat(values, 0, 1.0f));
	}

	public void test11() {

		assertEquals(0.0d, support.parseDouble(values, 0));
	}

	public void test12() {

		assertEquals(0.0d, support.parseDouble(values, 0, 0.0d));
	}

	public void test13() {

		assertEquals(1.0d, support.parseDouble(values, 0, 1.0d));
	}
}
