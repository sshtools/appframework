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

package com.sshtools.appframework.ui.wizard;

import java.awt.Component;

/**
 * 
 * 
 * @author $author$
 */
public interface WizardPage {
	public void show(WizardPanel wizard);

	/**
	 * 
	 * 
	 * @return
	 */
	public Component getPageComponent();

	/**
	 * 
	 * 
	 * @return
	 */
	public String getPageTitle();

	/**
	 * 
	 * 
	 * @return
	 */
	public String getPageDescription();

	/**
	 * 
	 * 
	 * @throws ValidationException
	 */
	public void validatePage() throws ValidationException;

	public void apply();
}
