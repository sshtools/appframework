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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 *
 * @author $author$
 */
public class X11Util {
	// Logger
	/**  */
	static byte[] table = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66 };

	/**
	 * Create an X11 cookie.
	 *
	 * @param displayNumber display number
	 * @return cookie
	 */
	public static String createCookie(String displayNumber) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < 16; i++) {
			int r = (int) (Math.random() * 256);
			String h = Integer.toHexString(r);
			if (h.length() == 1) {
				b.append(0);
			}
			b.append(h);
		}
		return b.toString();
	}

	/**
	 * Get the X11 cookie.
	 *
	 * @param displayNumber display number
	 * @return cookie
	 * @throws IOException on I/O error
	 */
	public static String getCookie(int displayNumber) throws IOException {
		Process process = null;
		InputStream in = null;
		InputStream err = null;
		OutputStream out = null;
		// try {
		byte[] foo = new byte[16];
		GeneralUtil.getRND().nextBytes(foo);
		byte[] bar = new byte[32];
		for (int i = 0; i < 16; i++) {
			bar[2 * i] = table[(foo[i] >>> 4) & 0xf];
			bar[(2 * i) + 1] = table[(foo[i]) & 0xf];
		}
		return new String(bar);
		/*
		 * String cmd = "xauth list :" + displayNumber; log.debug("Executing " +
		 * cmd); process = Runtime.getRuntime().exec(cmd); IOStreamConnector
		 * connect = new IOStreamConnector( err = process.getErrorStream(),
		 * System.out); BufferedReader reader = new BufferedReader( new
		 * InputStreamReader(in = process.getInputStream())); out =
		 * process.getOutputStream(); String line = null; String cookie = null;
		 * while( ( line = reader.readLine() ) != null) { log.debug(line);
		 * StringTokenizer t = new StringTokenizer(line); try { String host =
		 * t.nextToken(); String type = t.nextToken(); String value =
		 * t.nextToken(); if(cookie == null) { cookie = value;
		 * log.debug("Using cookie " + cookie); } } catch(Exception e) {
		 * log.error("Unexpected response from xauth.", e); } } return cookie; }
		 * finally { IOUtil.closeStream(in); IOUtil.closeStream(err);
		 * IOUtil.closeStream(out); }
		 */
	}
}
