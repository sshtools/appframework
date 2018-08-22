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

import static com.google.code.gtkjfilechooser.I18N.i18n;

import java.io.File;

import com.google.code.gtkjfilechooser.FreeDesktopUtil.WellKnownDir;


/**
 * Basic location in a file system: home dir, desktop and root.
 * 
 * @author c.cerbo
 *
 */
public class BasicPath implements Path {

	static public BasicPath DESKTOP;
	static public final BasicPath HOME = new BasicPath(System.getProperty("user.name"), System.getProperty("user.home"), "places/user-home");
	static public final BasicPath ROOT = new BasicPath(i18n("File System"), "/", "gtk-harddisk");

	private static final long serialVersionUID = 1L; 

	static {
		File desktopPath = FreeDesktopUtil.getWellKnownDirPath(WellKnownDir.DESKTOP);
		if (desktopPath != null) {
			DESKTOP = new BasicPath(desktopPath.getName(), desktopPath.getAbsolutePath(), "places/user-desktop");	
		}		
	}

	protected String iconName;
	protected String location;
	protected String name;

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
	public String getIconName() {
		return iconName;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getName() {
		return name;
	}

}
