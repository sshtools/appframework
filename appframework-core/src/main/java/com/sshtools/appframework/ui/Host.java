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