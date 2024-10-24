/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.cas;

/*
 * https://en.wikipedia.org/wiki/CAS_Registry_Number
 * 7732-18-5
 * [1]-[2]-[3]
 * [1]: 1-7 digits
 * [2]: 2 digits
 * [3]: checksum
 * = 10^9 possibilities: 1000000000
 */
public class CasSupport {

	public static final int MAX_ID = 1000000000;
	public static final String CAS_DEFAULT = "0-00-0";
	//
	private static final String SEPARATOR = "-";
	private static final char SEPARATOR_CHAR = '-';

	/**
	 * Calculates the checksum for a given CAS prefix.
	 * E.g.:
	 * ---
	 * 100-42-5: 100-42- => 5
	 * 71-43-2: 71-43- => 2
	 * ---
	 * If the casPrefix doesn't end with "-", then "" is returned.
	 * 
	 * @param cas
	 * @return String
	 */
	public static String calculateChecksum(String casPrefix) {

		String checksumValue = "";
		if(casPrefix != null && casPrefix.endsWith(SEPARATOR)) {
			String[] parts = casPrefix.split(SEPARATOR);
			if(parts.length == 2) {
				int checksum = calculateChecksumFromPrefix(casPrefix.replace(SEPARATOR, ""));
				checksumValue = Integer.toString(checksum);
			}
		}
		//
		return checksumValue;
	}

	/**
	 * Returns if the given CAS# is valid.
	 * 
	 * @param cas
	 * @return {@link Boolean}
	 */
	public static boolean isValid(String cas) {

		return verifyChecksum(cas);
	}

	public static boolean verifyChecksum(String cas) {

		/*
		 * 0-00-0
		 */
		if(cas != null) {
			cas = cas.trim();
			int length = cas.length();
			if(length >= 6) {
				long count = cas.chars().filter(c -> c == SEPARATOR_CHAR).count();
				if(count == 2) {
					if(cas.charAt(length - 2) == SEPARATOR_CHAR && cas.charAt(length - 5) == SEPARATOR_CHAR) {
						try {
							int casNumber = Integer.parseInt(cas.replaceAll(SEPARATOR, ""));
							return verifyChecksum(casNumber);
						} catch(NumberFormatException e) {
							//
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean verifyChecksum(int cas) {

		if(cas >= 0 && cas <= MAX_ID) {
			/*
			 * The last char is the checksum.
			 */
			String casNumber = Integer.toString(cas);
			int length = casNumber.length() - 1;
			int checksum = calculateChecksumFromPrefix(casNumber.substring(0, length));
			//
			char character = casNumber.charAt(length);
			if(Character.isDigit(character)) {
				int casChecksum = Integer.parseInt(Character.toString(character));
				return casChecksum == checksum;
			}
		}
		//
		return false;
	}

	private static int calculateChecksumFromPrefix(String casPrefix) {

		int length = casPrefix.length();
		int factor = length;
		int sum = 0;
		for(int i = 0; i < length; i++) {
			char character = casPrefix.charAt(i);
			if(Character.isDigit(character)) {
				int value = Integer.parseInt(Character.toString(character));
				sum += value * factor;
				factor--;
			}
		}
		//
		return (sum % 10);
	}

	/**
	 * Returns a formatted CAS# if cas
	 * - doesn't contain "-"
	 * - consists only of digits
	 * - has a length of >= 4
	 * There is one special case. If cas == "0", then "0-00-0" is returned.
	 * 
	 * @param cas
	 * @return String
	 */
	public static String format(String cas) {

		/*
		 * 0-00-0
		 */
		if(cas != null) {
			if("0".equals(cas)) {
				cas = CAS_DEFAULT;
			} else if(!cas.contains(SEPARATOR)) {
				if(cas.matches("(\\d+)")) {
					int length = cas.length();
					if(length >= 4) {
						StringBuilder builder = new StringBuilder();
						//
						builder.append(cas.substring(0, length - 3));
						builder.append(SEPARATOR);
						builder.append(cas.substring(length - 3, length - 1));
						builder.append(SEPARATOR);
						builder.append(cas.substring(length - 1, length));
						//
						cas = builder.toString();
					}
				}
			}
		} else {
			cas = CAS_DEFAULT;
		}
		//
		return cas;
	}
}