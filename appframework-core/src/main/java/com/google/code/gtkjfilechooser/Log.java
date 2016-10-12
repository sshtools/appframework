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

	static final private Logger LOG = Logger.getLogger(GtkFileChooserUI.class.getName());

	/**
	 * Set false for production code
	 */
	static final private boolean DEBUG = false;

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

	static public void debug(Object... msgs) {
		if (LOG.isLoggable(Level.FINEST)) {
			StringBuilder sb = new StringBuilder();
			appendMessages(sb, msgs);
			LOG.finest(sb.toString());
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

	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Log.debug("ciao");
			}
		}).start();
	}

}
