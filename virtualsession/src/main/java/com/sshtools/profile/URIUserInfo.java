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
package com.sshtools.profile;

/**
 * 
 */
public class URIUserInfo {
  private String domain;
  private String userName;
  private char[] password;
  
  public URIUserInfo(String domain, String userName, char[] password) {
    this.domain = domain;
    this.userName = userName;
    this.password = password;
  }
  
  public URIUserInfo(String userInfo) {
    if(userInfo != null) {
	    int idx = userInfo.lastIndexOf(':');
	    if(idx != -1) {
	      userName = userInfo.substring(0, idx);
	      password = userInfo.substring(idx + 1).toCharArray();
	    }
	    else {
	      userName = userInfo;
	    }
	    idx = userName.indexOf('+');
	    if(idx != -1) {
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
  
  public String toUserInfoString() {
    StringBuffer buf = new StringBuffer();
    if(domain != null && !domain.trim().equals("")) {
      buf.append(domain.trim());
      buf.append("+");
    }
    if(userName != null && !userName.trim().equals("")) {
      buf.append(userName.trim());
    }
    if(password != null && password.length > 0) {
      buf.append(":");
      buf.append(new String(password));
    }
    
    return buf.toString();
  }
}