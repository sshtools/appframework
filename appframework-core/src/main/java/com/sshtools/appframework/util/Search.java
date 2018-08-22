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
package com.sshtools.appframework.util;

public class Search {
	public static boolean matchesWildcardQuery(String str, String query) throws IllegalArgumentException {
		int idx = query.indexOf("*");
		if (idx > -1) {
			// We have a wildcard search
			if ((idx > 0) && (idx < (query.length() - 1))) {
				throw new IllegalArgumentException(
						"Wildcards not supported in middle of query string; use either 'searchtext*' or '*searchtext'");
			}
			if (idx == (query.length() - 1)) {
				return str.startsWith(query.substring(0, idx));
			}
			return str.endsWith(query.substring(idx + 1));
		}
		if (str.equalsIgnoreCase(query)) {
			return true;
		}
		return false;
	}
}
