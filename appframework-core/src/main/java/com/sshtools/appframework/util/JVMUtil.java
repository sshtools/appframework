/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.util;

/**
 *
 *
 * @author $author$
 */
public class JVMUtil {
  /**
   *
   *
   * @return
   */
  public static int getMajorVersion() {
    return 1;
  }

  /**
   *
   *
   * @return
   */
  public static int getMinorVersion() {
    return 4;
  }

  /**
   *
   *
   * @param args
   */
  public static void main(String[] args) {
    System.getProperties().list(System.out);
    System.out.println("Major=" + getMajorVersion());
    System.out.println("Minor=" + getMinorVersion());
  }
}
// end class Base64
