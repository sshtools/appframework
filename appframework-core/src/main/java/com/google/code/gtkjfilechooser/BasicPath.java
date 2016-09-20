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

import static com.google.code.gtkjfilechooser.I18N._;

import java.io.File;

import com.google.code.gtkjfilechooser.FreeDesktopUtil.WellKnownDir;


/**
 * Basic location in a file system: home dir, desktop and root.
 * 
 * @author c.cerbo
 *
 */
public class BasicPath implements Path {

	static public final BasicPath HOME = new BasicPath(System.getProperty("user.name"), System.getProperty("user.home"), "places/user-home");
	static public BasicPath DESKTOP;
	static {
		File desktopPath = FreeDesktopUtil.getWellKnownDirPath(WellKnownDir.DESKTOP);
		if (desktopPath != null) {
			DESKTOP = new BasicPath(desktopPath.getName(), desktopPath.getAbsolutePath(), "places/user-desktop");	
		}		
	}

	static public final BasicPath ROOT = new BasicPath(_("File System"), "/", "gtk-harddisk"); 

	private static final long serialVersionUID = 1L;

	protected String location;
	protected String name;
	protected String iconName;

	public BasicPath() {
		super();
	}

	public BasicPath(String name, String location, String iconName) {
		this();
		this.name = name;
		this.location = location;
		this.iconName = iconName;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getIconName() {
		return iconName;
	}

}
