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
