/**
 * Maverick Virtual Session - Framework for a tabbed user interface of connections to some local or remote resources.
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
package com.sshtools.profile;

/**
 * Abstract implementation of {@link SchemeOptions}
 */
public abstract class AbstractSchemeOptions implements SchemeOptions, Cloneable {
	private String schemeName;

	protected AbstractSchemeOptions(String schemeName) {
		this.schemeName = schemeName;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String getScheme() {
		return schemeName;
	}

	@Override
	public boolean isAppropriateForScheme(String schemeName) {
		return schemeName.equalsIgnoreCase(getScheme());
	}
}
