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
package com.sshtools.appframework.api.ui;

public class ActionMenu implements Comparable<ActionMenu> {
	private int weight;

	private int mnemonic;

	private String name;

	private String displayName;

	public ActionMenu(String name, String displayName, int mnemonic, int weight) {
		this.name = name;
		this.displayName = displayName;
		this.mnemonic = mnemonic;
		this.weight = weight;
	}

	public int compareTo(ActionMenu o) {
		int i = new Integer(weight).compareTo(new Integer(o.weight));
		return (i == 0) ? displayName.compareTo(o.displayName) : i;
	}

	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return Returns the mnemonic.
	 */
	public int getMnemonic() {
		return mnemonic;
	}

	/**
	 * @param mnemonic
	 *            The mnemonic to set.
	 */
	public void setMnemonic(int mnemonic) {
		this.mnemonic = mnemonic;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the weight.
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            The weight to set.
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String toString() {
		return getName();
	}
}