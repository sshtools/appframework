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

import java.lang.reflect.Method;

/**
 * <p>$Id: ApplicationException.java,v 1.1.2.1 2010-04-30 22:04:39 brett Exp $</p>
 * 
 * <p>Exception to be extended by all exceptions within the application framework.
 * It uses reflection to determine if an underlying exception may by set 
 * (1.4+ JDKs only)</p> 
 *
 * @author $Author: brett $
 */
public class ApplicationException
    extends Exception {
    
  /**
   * Creates a new ApplicationException.
   */
  public ApplicationException() {
    this(null, null);
  }

  /**
   * Creates a new ApplicationException.
   *
   * @param msg message
   */
  public ApplicationException(String msg) {
    this(msg, null);
  }

  /**
   * Creates a new ApplicationException. This cause will only
   * be available in 1.4+ JDKs.
   *
   * @param cause underlying cause.
   */
  public ApplicationException(Throwable cause) {
    this(null, cause);
  }

  /**
   * Creates a new ApplicationException. The cause will only
   * be available in 1.4+ JDKs.
   *
   * @param msg message
   * @param cause underlying cause
   */
  public ApplicationException(String msg, Throwable cause) {
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
