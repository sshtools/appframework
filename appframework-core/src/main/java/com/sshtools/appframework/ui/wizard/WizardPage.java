/* HEADER */
package com.sshtools.appframework.ui.wizard;

import java.awt.Component;

public interface WizardPage {
	void apply();

	Component getPageComponent();

	String getPageDescription();

	String getPageTitle();

	void show(WizardPanel wizardPanel);

	void validatePage() throws ValidationException;
}
