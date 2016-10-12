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

import java.io.IOException;

/**
 *
 *
 * @author $Author: brett $
 */
public abstract class SchemeHandler {
  private String name;
  private String description;

  /**
   * Construct a new SchemeHandler
   *
   * @param name scheme name
   * @param description description
   */
  public SchemeHandler(String name, String description) {
    this.name = name;
    this.description = description;
  }

  /**
   * Create a {@link SchemeOptions} appropriate for this scheme
   *
   * @return scheme options
   */
  public abstract SchemeOptions createSchemeOptions();

  /**
   * Create the {@link ProfileTransport} appropriate for this scheme
   *
   * @return profile transport
   */
  public abstract ProfileTransport createProfileTransport(ResourceProfile
      profile) throws ProfileException, IOException, AuthenticationException;

  /**
   * Get the scheme name
   *
   * @return scheme name
   */
  public String getName() {
    return name;
  }

  /**
   * Get the scheme description
   *
   * @return scheme description
   */
  public String getDescription() {
    return description;
  }
}