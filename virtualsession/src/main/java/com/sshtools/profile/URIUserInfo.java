/**
 * Maverick Virtual Session - Framework for a tabbed user interface of connections to some local or remote resources.
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
	 * @param userInfo user info
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