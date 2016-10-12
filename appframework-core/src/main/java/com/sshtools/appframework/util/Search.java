/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.util;

/**
 *
 *
 * @author $author$
 */
public class Search {
  /**
   *
   *
   * @param str
   * @param query
   *
   * @return
   *
   * @throws IllegalArgumentException
   */
  public static boolean matchesWildcardQuery(String str, String query) throws
      IllegalArgumentException {
    int idx = query.indexOf("*");

    if (idx > -1) {
      // We have a wildcard search
      if ( (idx > 0) && (idx < (query.length() - 1))) {
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
