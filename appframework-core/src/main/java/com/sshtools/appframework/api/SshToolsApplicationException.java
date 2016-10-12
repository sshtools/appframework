/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.api;

import java.lang.reflect.Method;

/**
 *
 *
 * @author $author$
 */

public class SshToolsApplicationException

    extends Exception {

  /**
   * Creates a new SshToolsApplicationException object.
   */

  public SshToolsApplicationException() {
    this(null, null);

  }

  /**
   * Creates a new SshToolsApplicationException object.
   *
   * @param msg
   */

  public SshToolsApplicationException(String msg) {
    this(msg, null);

  }

  /**
   * Creates a new SshToolsApplicationException object.
   *
   * @param cause
   */

  public SshToolsApplicationException(Throwable cause) {
    this(null, cause);

  }

  /**
   * Creates a new SshToolsApplicationException object.
   *
   * @param msg
   * @param cause
   */

  public SshToolsApplicationException(String msg, Throwable cause) {
    super(msg);
    if (cause != null) {
      try {
        Method m = getClass().getMethod("initCause",
                                        new Class[] {Throwable.class});
        m.invoke(this, new Object[] {cause});
      }
      catch (Exception e) {
      }
    }

  }

}
