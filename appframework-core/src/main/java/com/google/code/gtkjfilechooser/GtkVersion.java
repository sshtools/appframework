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
/*
 * Copyright 2010 Costantino Cerbo.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact me at c.cerbo@gmail.com if you need additional information or
 * have any questions.
 */
package com.google.code.gtkjfilechooser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * <p>
 * Version Information for GTK+
 * </p>
 * <p>
 * We get the GTK version from the shared library file, for example
 * "libgtk-x11-2.0.so.0.600.7" is GTK+ 2.6.7, "libgtk-x11-2.0.so.0.1600.1" is
 * GTK+ 2.16.1 an so on...
 * </p>
 * <p> See <a href="http://library.gnome.org/devel/gtk/unstable/gtk-Feature-Test-Macros.html">gtk-Feature-Test-Macros.html</a>
 * and <a href="http://git.gnome.org/cgit/gtk+/tree/gtk/gtkversion.h.in">gtkversion.h.in</a> 
 * 
 * 
 * @author Costantino Cerbo
 * 
 */
public class GtkVersion {
	static int gtkMajorVersion;
	static int gtkMicroVersion;
	static int gtkMinorVersion;
	static {
		gtkMajorVersion = 2;
		gtkMinorVersion = Short.MAX_VALUE;
		gtkMicroVersion = Short.MAX_VALUE;

		try {
			String[] files = new File("/usr/lib").list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("libgtk-x11-2.0.so.0.");
				}
			});

			if (files != null) {
				String lib = files[0];

				int i1 = lib.lastIndexOf('.');
				int i0 = lib.lastIndexOf('.', i1 - 1);

				String minor = lib.substring(i0 + 1, i1);
				String micro = lib.substring(i1 + 1);

				gtkMinorVersion = Integer.parseInt(minor) / 100;
				gtkMicroVersion = Integer.parseInt(micro);
			}
		} catch (Throwable e) {
			Log.debug(e.getMessage());
		}
	}

	/**
	 * Checks that the GTK+ library in use is compatible (the same or newer)
	 * with the given version.
	 * 
	 * @param major required major
	 * @param minor required minor
	 * @param micro required micro
	 * @return {@code null} if the GTK+ library is compatible with the given
	 *         version, or a string describing the version mismatch. The
	 *         returned string is owned by GTK+ and should not be modified or
	 *         freed.
	 */
	static public boolean check(int major, int minor, int micro) {
		return (gtkMajorVersion > (major)
				|| (gtkMajorVersion == (major) && gtkMinorVersion > (minor)) || (gtkMajorVersion == (major)
				&& gtkMinorVersion == (minor) && gtkMicroVersion >= (micro)));

	}

	static public String getCurrent() {
		return gtkMajorVersion + "." + gtkMinorVersion + "." + gtkMicroVersion;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(GtkVersion.getCurrent());
	}

}
