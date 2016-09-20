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
