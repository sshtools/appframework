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
import java.io.FileReader;
import java.util.Properties;

/**
 * @author Costantino Cerbo
 * 
 */
public class Platform {

	public enum PlatformEnum {
		REDHAT, SUSE, UBUNTU, SOLARIS, UNKNOWN
	};

	/**
	 * Returns the linux plaform for the current system.
	 * 
	 * @return The linux plaform for the current system.
	 */
	static public PlatformEnum getPlatform() {
		if (isRedhat()) {
			return PlatformEnum.REDHAT;
		} else if (isSuSE()) {
			return PlatformEnum.SUSE;
		} else if (isUbuntu()) {
			return PlatformEnum.UBUNTU;
		} else if (isSolaris()) {
			return PlatformEnum.SOLARIS;
		}

		return PlatformEnum.UNKNOWN;
	}

	static public boolean isRedhat() {
		if (new File("/etc/fedora-release").exists()) {
			return true;
		}

		if (new File("/etc/redhat-release").exists()) {
			return true;
		}

		if (System.getProperty("os.version").indexOf(".fc") != -1) {
			return true;
		}

		if (System.getProperty("os.version").indexOf(".el") != -1) {
			return true;
		}

		return false;
	}

	static public boolean isSuSE() {
		if (new File("/etc/SuSE-release").exists()) {
			return true;
		}

		return false;
	}

	static public boolean isUbuntu() {
		if (new File("/etc/lsb-release").exists()) {
			Properties props = new Properties();
			try {
				props.load(new FileReader(new File("/etc/lsb-release")));
			} catch (Exception e) {
				return false;
			}
			
			if ("Ubuntu".equals(props.getProperty("DISTRIB_ID"))) {
				return true;	
			}			
		}
		return false;
	}

	static public boolean isSolaris() {
		if (System.getProperty("os.name").indexOf("SunOS") != -1) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		System.out.println(getPlatform());

	}
}
