/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/* HEADER */
package com.sshtools.profile;

/**
 * Exception thrown when there are errors caused by handling of
 * a {@link ResourceProfile}.
 *
 * @author $Author: brett $
 */
public class ProfileException
    extends Exception {

// #ifdef JAVA1
/*
  private Throwable cause;
*/

// #else
  
  //  Using supers cause
      
// #endif

  /**
   * Construct a new ProfileException with no message
   */
  public ProfileException() {
    super();
  }

  /**
   * Construct a new ProfileException with a message.
   *
   * @param message message
   */
  public ProfileException(String message) {
    super(message);
  }

  /**
   * Construct a new ProfileException with a message.
   *
   * @param message message
   * @param cause cause
   */
  public ProfileException(String message, Throwable cause) {
    super(message);
    setCause(cause);
  }

  /**
   * Construct a new ProfileException with an underlying cause.
   *
   * @param cause cause
   */
  public ProfileException(Throwable cause) {
    super();
    setCause(cause);
  }
  
  private void setCause(Throwable cause) {
// #ifdef JAVA1
/*
   this.cause = cause;
*/

// #else
    initCause(cause);
        
// #endif
  }
  
  public Throwable getCause() {
// #ifdef JAVA1
/*
    return cause;
*/

// #else
    return super.getCause();      
// #endif
    
  }
}
