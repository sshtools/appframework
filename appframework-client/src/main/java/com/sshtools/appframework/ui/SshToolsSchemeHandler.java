/**
 * Maverick Client Application Framework - Framework for 'client' applications.
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
 */
package com.sshtools.appframework.ui;

import java.util.List;

import javax.swing.Icon;

import com.sshtools.appframework.api.ui.SshToolsConnectionTab;
import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.SchemeHandler;

/**
 * @param <T> type of transport
 */
public abstract class SshToolsSchemeHandler<T extends ProfileTransport<?>> extends SchemeHandler<T> {

	private String category;
	private Icon icon;
	private boolean internal;
	private int weight;
	private Icon largeIcon;

	/**
	 * Construct a new SchemeHandler
	 * 
	 * @param name scheme name
	 * @param description description
	 * @param weight weight
	 * @param category category
	 * @param icon icon
	 * @param internal internal scheme
	 */
	public SshToolsSchemeHandler(String name, String description, int weight, String category, Icon icon, boolean internal) {
		super(name, description);
		this.weight = weight;
		this.category = category;
		this.icon = icon;
		this.internal = internal;
	}

	public abstract List<SshToolsConnectionTab<T>> createTabs();

	/**
	 * Get the category of scheme.
	 * 
	 * @return category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Get the icon for the scheme
	 * 
	 * @return scheme icon
	 */
	public Icon getIcon() {
		return icon;
	}


	/**
	 * Get the large icon for the scheme
	 * 
	 * @return large scheme icon
	 */
	public Icon getLargeIcon() {
		return largeIcon;
	}

	protected void setLargeIcon(Icon largeIcon) {
		this.largeIcon = largeIcon;
	}

	/**
	 * The scheme may return a weight to determine its position in a list of
	 * schemes. This may be used for example in a graphical environment where
	 * the user must select a scheme from a list. The lower the number, the
	 * higher up the list the scheme occurs. If two schemes have the same
	 * weight, the result is unpredicatable.
	 * 
	 * @return weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Get if this is an internal scheme handler and should be hidden from the
	 * user.
	 * 
	 * @return internal
	 */
	public boolean isInternal() {
		return internal;
	}

	protected void setInternal(boolean internal) {
		this.internal = internal;
	}
}
