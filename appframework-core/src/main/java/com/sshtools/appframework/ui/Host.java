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

package com.sshtools.appframework.ui;

import java.util.Map;

public class Host {

  private String host;

  private Map keys;

  public Host(String host, Map keys) {
    this.host = host;
    this.keys = keys;

  }

  /**
   * @return Returns the host.
   */

  public String getHost() {
    return host;

  }

  /**
   * @param host The host to set.
   */

  public void setHost(String host) {
    this.host = host;

  }

  /**
   * @return Returns the keys.
   */

  public Map getKeys() {
    return keys;

  }

  /**
   * @param keys The keys to set.
   */

  public void setKeys(Map keys) {
    this.keys = keys;

  }

}