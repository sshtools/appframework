/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import org.freedesktop.icons.DefaultIconService;
import org.freedesktop.icons.IconTheme;

public class NonFreedesktopIconService extends DefaultIconService {

	private SshToolsApplication application;
	private IconTheme defaultTheme;

	public NonFreedesktopIconService(SshToolsApplication application) throws IOException, ParseException {
		super();
		this.application = application;
	}

	@Override
	protected IconTheme getDefaultTheme() {
		if (defaultTheme == null) {
			File file = new File(application.getApplicationPreferencesDirectory(), "default-theme");
			if (!file.exists()) {
				file.mkdirs();
			}
			URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
			if (location.getProtocol().equals("file")) {
				try {
					checkAndAddBase(new File(location.toURI()));
				} catch (Exception e) {
					throw new Error(e);
				}
			} else if (location.getProtocol().equals("jar")) {

			}
			defaultTheme = getEntity("default-tango-theme");
		}
		return defaultTheme;
	}
}
