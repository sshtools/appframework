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

import java.lang.reflect.Array;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.code.gtkjfilechooser.ui.GtkFileChooserUI;


/**
 * Naive logger with stack strace. Set DEBUG to false for production code.
 * 
 * @author c.cerbo
 * 
 */
public class Log {

	/**
	 * Set false for production code
	 */
	static final private boolean DEBUG = false;

	static final private Logger LOG = Logger.getLogger(GtkFileChooserUI.class.getName());

	static public void debug(Object... msgs) {
		if (LOG.isLoggable(Level.FINEST)) {
			StringBuilder sb = new StringBuilder();
			appendMessages(sb, msgs);
			LOG.finest(sb.toString());
		}
	}

	static public void debug0(Object... msgs) {
		if (DEBUG) {
			String location = getInvokingLocation();

			StringBuilder sb = new StringBuilder();
			sb.append(location);
			sb.append(": ");

			appendMessages(sb, msgs);

			System.out.println(sb);
		}
	}

	static public void log(Level level, Object... msgs) {
		if (LOG.isLoggable(Level.WARNING)) {
			StringBuilder sb = new StringBuilder();
			appendMessages(sb, msgs);
			LOG.log(level, sb.toString());
		}
	}

	static public void log(Level level, Throwable thrown, Object... msgs) {
		if (LOG.isLoggable(Level.WARNING)) {
			StringBuilder sb = new StringBuilder();
			appendMessages(sb, msgs);
			LOG.log(level, sb.toString(), thrown);
		}
	}

	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Log.debug("ciao");
			}
		}).start();
	}

	private static void appendMessages(StringBuilder sb, Object... msgs) {
		for (Object msg : msgs) {
			if (msg == null) {
				sb.append("null");
			} else if (msg.getClass().isArray()) {
				int len = Array.getLength(msg);
				for (int i = 0; i < len; i++) {
					sb.append(Array.get(msg, i));
					if (i != (len - 1)) {
						sb.append(", ");
					}
				}
			} else {
				sb.append(String.valueOf(msg));
			}
		}
	}

	private static String getInvokingLocation() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		String location = null;
		for (int i = 0; i < stackTrace.length; i++) {
			StackTraceElement s = stackTrace[i];
			if (Log.class.getName().equals(s.getClassName())
					&& "debug".equals(s.getMethodName())) {
				StackTraceElement next = stackTrace[i + 1];
				location = next.getClassName() + "." + next.getMethodName() + "("
				+ next.getFileName() + ":" + next.getLineNumber() + ")";
			}
		}
		return location;
	}

}
