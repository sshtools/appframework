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

import static com.google.code.gtkjfilechooser.I18N._;

/**
 * To an {@link ActionPath} corresponds no location, but is like an button to
 * execute an action.
 * 
 * @author c.cerbo
 * 
 */
public class ActionPath implements Path {

	private static final long serialVersionUID = 1L;

	static final public int RECENTLY_USED_PANEL_ID = 1001;
	static final public int SEARCH_PANEL_ID = 1002;

	static public final ActionPath SEARCH = new ActionPath(_("Search"), SEARCH_PANEL_ID, "search", "actions/stock_search");
	static public final ActionPath RECENTLY_USED = new ActionPath(_("Recently Used"), RECENTLY_USED_PANEL_ID, "recently_used", "actions/document-open-recent");

	private String name;
	private int id;
	private String action;
	private String iconName;

	public ActionPath(String name, int id, String action, String iconName) {
		super();
		this.name = name;
		this.id = id;
		this.action = action;
		this.iconName = iconName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	@Override
	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

}
