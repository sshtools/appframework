/**
 * Maverick Application Framework - Application framework
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.google.code.gtkjfilechooser;

import static com.google.code.gtkjfilechooser.I18N.i18n;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/**
	 * The given date as Julian day number. The Julian day number is the amount
	 * of day since 1/1/1. Useful to compute days difference.
	 * 
	 * @param date
	 *            The date to convert.
	 * @return The Julian day number.
	 */
	public static long toJulianDayNumber(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int Y = cal.get(Calendar.YEAR);
		int M = cal.get(Calendar.MONTH) + 1; // jan=1, feb=2,...
		int D = cal.get(Calendar.DAY_OF_MONTH);

		return (1461 * (Y + 4800 + (M - 14) / 12)) / 4
		+ (367 * (M - 2 - 12 * ((M - 14) / 12))) / 12
		- (3 * ((Y + 4900 + (M - 14) / 12) / 100)) / 4 + D - 32075;
	}

	/**
	 * Format a date in a more human readable way, according the following set
	 * of rules:
	 * 
	 * <ul>
	 * <li><i>Today:</i> shows just the time (HH:MM)</li>
	 * <li><i>Yesterday:</i> shows "Yesterday at HH:MM"</li>
	 * <li><i>Days from last week: </i> show the locale day of week</li>
	 * <li><i>Any other date: </i> format is using {@link DateFormat#getDateInstance()}</li>
	 * </ul>
	 * 
	 * @param date The {@link Date} to format.
	 * @return The "prettified" date.
	 */
	public static String toPrettyFormat(Date date) {
		return toPrettyFormat(date, new Date());
	}

	/**
	 * Package visibility for testing
	 */
	static String toPrettyFormat(Date d, Date today) {
		long days_diff = toJulianDayNumber(today) - toJulianDayNumber(d);

		if (days_diff == 0) {
			// Today: show just the time (HH:MM)
			return String.format("%tR", d);
		}

		if (days_diff == 1) {
			// Yesterday
			String mgsstr = i18n("Yesterday at %H:%M");
			return new Strftime(mgsstr).format(d);
		}

		if (days_diff > 1 && days_diff < 7) {
			// Days from last week
			return String.format("%tA", d);
		}

		// Any other date
		return DateFormat.getDateInstance(DateFormat.SHORT).format(d);
	}
}
