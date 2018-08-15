/*
 */
package com.sshtools.appframework.api.ui;

public class ActionMenu implements Comparable<ActionMenu> {
	private String displayName;

	private int mnemonic;

	private String name;

	private int weight;

	public ActionMenu(String name, String displayName, int mnemonic, int weight) {
		this.name = name;
		this.displayName = displayName;
		this.mnemonic = mnemonic;
		this.weight = weight;
	}

	@Override
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
	 * @return Returns the mnemonic.
	 */
	public int getMnemonic() {
		return mnemonic;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the weight.
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param displayName
	 *            The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @param mnemonic
	 *            The mnemonic to set.
	 */
	public void setMnemonic(int mnemonic) {
		this.mnemonic = mnemonic;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param weight
	 *            The weight to set.
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return getName();
	}
}