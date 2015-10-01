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
