/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.google.code.gtkjfilechooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface ActionDispatcher {

	public void addActionListener(ActionListener l);

	public void removeActionListener(ActionListener l);

	public void fireActionEvent(ActionEvent e);

	void removeAllActionListeners();
}
