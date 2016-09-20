/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
/*
 * Copyright 1999, 2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.gtkjfilechooser;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts dates to strings using the same format specifiers as <a href="http://www.gnu.org/software/libc/manual/html_node/Formatting-Calendar-Time.html#Formatting-Calendar-Time"
 * >strftime</a>
 *
 * Note: This does not mimic strftime perfectly.  Certain strftime commands, 
 *       are not supported, and will convert as if they were literals.
 *
 *       Certain complicated commands, like those dealing with the week of the year
 *       probably don't have exactly the same behavior as strftime.
 *
 *       These limitations are due to use SimpleDateTime.  If the conversion was done
 *       manually, all these limitations could be eliminated.
 * 
 * @author Bip Thelin
 * @author Dan Sandberg
 * @author Costantino Cerbo
 */

public class Strftime extends DateFormat {
	protected static Map<Character, String> translate;

	/**
	 * The delegate {@link SimpleDateFormat}
	 */
	private final SimpleDateFormat simpleDateFormat;

	/**
	 * Initialize our pattern translation
	 */
	static {
		translate = new HashMap<Character, String>();

		// The abbreviated weekday name according to the current locale. 
		translate.put('a', "EEE");

		// The full weekday name according to the current locale. 
		translate.put('A', "EEEE");

		// The abbreviated month name according to the current locale. 
		translate.put('b', "MMM");

		// The full month name according to the current locale. 
		translate.put('B', "MMMM");

		// The preferred calendar time representation for the current locale. 
		// Examples: it_IT: lun 26 ott 2009 22:51:39 CET, en_US: Mon 26 Oct 2009 10:51:39 PM CET, de_DE: Mo 26 Okt 2009 22:51:39 CET
		translate.put('c', "EEE d MMM yyyy HH:mm:ss z");

		// The century of the year. This is equivalent to the greatest integer not greater than the year divided by 100. 
		// translate.put('C',"the century of the year");

		translate.put('d', "dd");
		translate.put('D', "MM/dd/yy");
		translate.put('e', "dd"); // will show as '03' instead of ' 3'
		translate.put('F', "yyyy-MM-dd");
		translate.put('g', "yy");
		translate.put('G', "yyyy");
		translate.put('H', "HH");
		translate.put('h', "MMM");
		translate.put('I', "hh");
		translate.put('j', "DDD");
		translate.put('k', "HH"); // will show as '07' instead of ' 7'
		translate.put('l', "hh"); // will show as '07' instead of ' 7'
		translate.put('m', "MM");
		translate.put('M', "mm");
		translate.put('n', "\n");
		translate.put('p', "a");
		translate.put('P', "a"); // will show as pm instead of PM
		translate.put('r', "hh:mm:ss a");
		translate.put('R', "HH:mm");
		// There's no way to specify this with SimpleDateFormat
		// translate.put("s","seconds since ecpoch");
		translate.put('S', "ss");
		translate.put('t', "\t");
		translate.put('T', "HH:mm:ss");
		// There's no way to specify this with SimpleDateFormat
		// translate.put('u',"day of week ( 1-7 )");

		// There's no way to specify this with SimpleDateFormat
		// translate.put('U',"week in year with first sunday as first day...");

		translate.put('V', "ww"); // I'm not sure this is always exactly the
		// same

		// There's no way to specify this with SimpleDateFormat
		// translate.put('W',"week in year with first monday as first day...");

		// There's no way to specify this with SimpleDateFormat
		// translate.put('w',"E");
		translate.put('X', "HH:mm:ss");
		translate.put('x', "MM/dd/yy");
		translate.put('y', "yy");
		translate.put('Y', "yyyy");
		translate.put('Z', "z");
		translate.put('z', "Z");
		translate.put('%', "%");
	}

	/**
	 * Create an instance of this date formatting class
	 * 
	 * @see #Strftime(String, Locale )
	 */
	public Strftime(String origFormat) {
		this(origFormat, Locale.getDefault());
	}

	/**
	 * Create an instance of this date formatting class
	 * 
	 * @param origFormat
	 *            the strftime-style formatting string
	 * @param locale
	 *            the locale to use for locale-specific conversions
	 */
	public Strftime(String origFormat, Locale locale) {
		String convertedFormat = convertDateFormat(origFormat);
		simpleDateFormat = new SimpleDateFormat(convertedFormat, locale);

		// According to the DateFormat javadoc the instance variable 'calendar'
		// and
		// 'numberFormat' should be initialized in subclasses.
		setCalendar(simpleDateFormat.getCalendar());
		setNumberFormat(simpleDateFormat.getNumberFormat());
	}

	/**
	 * Search the provided pattern and get the C standard Date/Time formatting
	 * rules and convert them to the Java equivalent.
	 * 
	 * @param pattern
	 *            The pattern to search
	 * @return The modified pattern
	 */
	private String convertDateFormat(String pattern) {
		boolean inside = false;
		boolean mark = false;
		boolean modifiedCommand = false;

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);

			if (c == '%' && !mark) {
				mark = true;
			} else {
				if (mark) {
					if (modifiedCommand) {
						// don't do anything--we just wanted to skip a char
						modifiedCommand = false;
						mark = false;
					} else {
						inside = translateCommand(buf, pattern, i, inside);
						// It's a modifier code
						if (c == 'O' || c == 'E') {
							modifiedCommand = true;
						} else {
							mark = false;
						}
					}
				} else {
					if (!inside && c != ' ') {
						// We start a literal, which we need to quote
						buf.append("'");
						inside = true;
					}

					buf.append(c);
				}
			}
		}

		if (buf.length() > 0) {
			char lastChar = buf.charAt(buf.length() - 1);

			if (lastChar != '\'' && inside) {
				buf.append('\'');
			}
		}
		return buf.toString();
	}

	private String quote(String str, boolean insideQuotes) {
		String retVal = str;
		if (!insideQuotes) {
			retVal = '\'' + retVal + '\'';
		}
		return retVal;
	}

	/**
	 * Try to get the Java Date/Time formatting associated with the C standard
	 * provided.
	 * 
	 * @param buf
	 *            The buffer
	 * @param pattern
	 *            The date/time pattern
	 * @param index
	 *            The char index
	 * @param oldInside
	 *            Flag value
	 * @return True if new is inside buffer
	 */
	private boolean translateCommand(StringBuffer buf, String pattern, int index,
			boolean oldInside) {
		char firstChar = pattern.charAt(index);
		boolean newInside = oldInside;

		// O and E are modifiers, they mean to present an alternative
		// representation of the next char
		// we just handle the next char as if the O or E wasn't there
		if (firstChar == 'O' || firstChar == 'E') {
			if (index + 1 < pattern.length()) {
				newInside = translateCommand(buf, pattern, index + 1, oldInside);
			} else {
				buf.append(quote("%" + firstChar, oldInside));
			}
		} else {
			String command = translate.get(firstChar);

			// If we don't find a format, treat it as a literal
			if (command == null) {
				buf.append(quote("%" + firstChar, oldInside));
			} else {
				// If we were inside quotes, close the quotes
				if (oldInside) {
					buf.append('\'');
				}
				buf.append(command);
				newInside = false;
			}
		}
		return newInside;
	}

	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo,
			FieldPosition fieldPosition) {
		return simpleDateFormat.format(date, toAppendTo, fieldPosition);
	}

	@Override
	public Date parse(String text, ParsePosition pos) {
		return simpleDateFormat.parse(text, pos);
	}
}
