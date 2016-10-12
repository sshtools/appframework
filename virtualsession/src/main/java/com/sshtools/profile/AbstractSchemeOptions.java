/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.profile;

public abstract class AbstractSchemeOptions implements SchemeOptions, Cloneable {

	private String schemeName;

	protected AbstractSchemeOptions(String schemeName) {
		this.schemeName = schemeName;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getScheme() {
		return schemeName;
	}

    public boolean isAppropriateForScheme(String schemeName) {
        return schemeName.equalsIgnoreCase(getScheme());
    }

}
