/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/*
 */
package com.sshtools.appframework.ui;

import javax.swing.Icon;

import com.sshtools.appframework.api.ui.SshToolsConnectionTab;
import com.sshtools.profile.SchemeHandler;

/**
 *
 */
public abstract class SshToolsSchemeHandler extends SchemeHandler {

	private String category;
	private int weight;
	private Icon icon;
	private boolean internal;

	/**
	 * Construct a new SchemeHandler
	 * 
	 * @param name scheme name
	 * @param description description
	 * @param weight weight
	 * @param category category
	 * @param icon icon
	 */
	public SshToolsSchemeHandler(String name, String description, int weight, String category, Icon icon, boolean internal) {
		super(name, description);
		this.weight = weight;
		this.category = category;
		this.icon = icon;
		this.internal = internal;
	}

	public abstract SshToolsConnectionTab[] createTabs();

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
