/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.google.code.gtkjfilechooser;

public class Wildcard {
	static final public char ZERO_MORE_CHARS_PATTERN = '*';
	static final public char SINGLE_CHAR_PATTERN = '?';
	static final private char END_CHAR = '\0';

	/**
	 * <p>
	 * Check if pattern string matches text string.
	 * </p>
	 * 
	 * Patterns supported:
	 * <ul>
	 * <li>'*' any zero or more characters</li>
	 * <li>'?' any one character</li>
	 * </ul>
	 */
	static public boolean matches(String pattern, String text) {
		// add sentinel so don't need to worry about *'s at end of pattern
		text += END_CHAR;
		pattern += END_CHAR;

		int N = pattern.length();

		boolean[] states = new boolean[N + 1];
		boolean[] old = new boolean[N + 1];
		old[0] = true;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			states = new boolean[N + 1]; // initialized to false
			for (int j = 0; j < N; j++) {
				char p = pattern.charAt(j);

				// hack to handle *'s that match 0 characters
				if (old[j] && (p == ZERO_MORE_CHARS_PATTERN)) old[j + 1] = true;

				if (old[j] && (p == c)) states[j + 1] = true;
				if (old[j] && (p == SINGLE_CHAR_PATTERN)) states[j + 1] = true;
				if (old[j] && (p == ZERO_MORE_CHARS_PATTERN)) states[j] = true;
				if (old[j] && (p == ZERO_MORE_CHARS_PATTERN)) states[j + 1] = true;
			}
			old = states;
		}
		return states[N];
	}

}
