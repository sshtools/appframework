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
