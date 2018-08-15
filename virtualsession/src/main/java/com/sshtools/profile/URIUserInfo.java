/*
 */
package com.sshtools.profile;

/**
 * 
 */
public class URIUserInfo {
	private String domain;
	private String userName;
	private char[] password;

	/**
	 * User info
	 * 
	 * @param domain domain
	 * @param userName username
	 * @param password password
	 */
	public URIUserInfo(String domain, String userName, char[] password) {
		this.domain = domain;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * User info
	 * 
	 * @param userInfo
	 */
	public URIUserInfo(String userInfo) {
		if (userInfo != null) {
			int idx = userInfo.lastIndexOf(':');
			if (idx != -1) {
				userName = userInfo.substring(0, idx);
				password = userInfo.substring(idx + 1).toCharArray();
			} else {
				userName = userInfo;
			}
			idx = userName.indexOf('+');
			if (idx != -1) {
				domain = userName.substring(0, idx);
				userName = userName.substring(idx + 1);
			}
		}
	}

	/**
	 * @return Returns the domain.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @return Returns the password.
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return a string with encoded domain, user and password
	 */
	public String toUserInfoString() {
		StringBuffer buf = new StringBuffer();
		if (domain != null && !domain.trim().equals("")) {
			buf.append(domain.trim());
			buf.append("+");
		}
		if (userName != null && !userName.trim().equals("")) {
			buf.append(userName.trim());
		}
		if (password != null && password.length > 0) {
			buf.append(":");
			buf.append(new String(password));
		}
		return buf.toString();
	}
}