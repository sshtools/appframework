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

import static com.google.code.gtkjfilechooser.I18N.i18n;

/**
 * To an {@link ActionPath} corresponds no location, but is like an button to
 * execute an action.
 * 
 * @author c.cerbo
 * 
 */
public class ActionPath implements Path {

	static final public int RECENTLY_USED_PANEL_ID = 1001;
	static public final ActionPath RECENTLY_USED = new ActionPath(i18n("Recently Used"), RECENTLY_USED_PANEL_ID, "recently_used", "actions/document-open-recent");

	static final public int SEARCH_PANEL_ID = 1002;
	static public final ActionPath SEARCH = new ActionPath(i18n("Search"), SEARCH_PANEL_ID, "search", "actions/stock_search");

	private static final long serialVersionUID = 1L;

	private String action;
	private String iconName;
	private int id;
	private String name;

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

	@Override
	public String getIconName() {
		return iconName;
	}

	public int getId() {
		return id;
	}

	@Override
	public String getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public void setName(String name) {
		this.name = name;
	}

}
