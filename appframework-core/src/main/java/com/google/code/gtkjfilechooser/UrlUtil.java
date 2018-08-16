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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

public class UrlUtil {

	static public String decode(String str) {
		try {
			return URLDecoder.decode(str, Charset.defaultCharset().toString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	static public String encode(String str) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			int dec = ch;

			// ASCII Control characters
			if (dec >= 0 && dec <= 31) {
				sb.append("%").append(Integer.toHexString(dec));
				continue;
			}

			// Non-ASCII characters
			if (dec >= 127) {
				sb.append("%").append(Integer.toHexString(dec));
				continue;
			}

			// Reserved and unsafe characters
			switch (dec) {
			case '$':
			case '&':
			case '+':
			case ',':
			case ':':
			case ';':
			case '=':
			case '?':
			case '@':
			case ' ':
			case 34: // Quotation marks
			case '<':
			case '>':
			case '#':
			case '%':
			case '{':
			case '}':
			case '|':
			case 92: // Backslash
			case '^':
			case '~':
			case '[':
			case ']':
			case '`':
				sb.append("%").append(Integer.toHexString(dec));
				continue;
			}

			// Append the char as it is
			sb.append(ch);
		}

		return sb.toString();
	}
}
