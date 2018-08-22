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
