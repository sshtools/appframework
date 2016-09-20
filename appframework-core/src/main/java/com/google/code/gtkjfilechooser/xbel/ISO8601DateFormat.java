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
package com.google.code.gtkjfilechooser.xbel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Parse and format ISO 8601 dates.
 * 
 * @see http://www.w3.org/TR/NOTE-datetime
 * @author c.cerbo
 *
 */
public class ISO8601DateFormat {
	private DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * Format a date in ISO 8601 (for example "1994-11-05T08:15:30-05:00" or
	 * "2009-09-06T16:59:39Z").
	 * 
	 * @param date
	 *            The date to format.
	 * @return the ISO 8601 format date.
	 */
	public String format(Date date) {
		String d = fmt.format(date); 
		return d.substring(0, 22) + ":" + d.substring(22);
	}

	/**
	 * Parse a date in ISO 8601 format (for example "1994-11-05T08:15:30-05:00"
	 * or "2009-09-06T16:59:39Z").
	 * 
	 * @param source
	 *            A <code>String</code> whose beginning should be parsed.
	 * @return A <code>Date</code> parsed from the string.
	 * @exception ParseException
	 *                if the beginning of the specified string cannot be parsed.
	 */
	public Date parse(String source) throws ParseException {
		return fmt.parse(preprocess(source));
	}

	private String preprocess(String s) throws ParseException {

		if (s.charAt(s.length() - 1) == 'Z') {
			// handle a date with the special UTC designator ("Z"). 

			// remove the Z and concat +0000
			s = s.substring(0, s.length() - 1) + "+0000";
		} else {
			// handle the case with time zone designator (+hh:mm or -hh:mm)

			s = s.substring(0, s.length() - 3) + s.substring(s.length() - 2);
		}

		return s;
	}

}
