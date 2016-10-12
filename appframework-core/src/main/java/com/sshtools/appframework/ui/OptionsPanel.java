/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.ui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;

import com.sshtools.ui.Option;
import com.sshtools.ui.OptionChooser;
import com.sshtools.ui.swing.OptionDialog;
import com.sshtools.ui.swing.SideBarTabber;

/**
 * 
 * 
 * @author $author$
 */

public class OptionsPanel

extends JPanel {

    //

    /**  */

    //
    private SideBarTabber tabber;

    /**
     * Creates a new OptionsPanel object.
     * 
     * @param tabs to show
     */

    public OptionsPanel(OptionsTab[] optionalTabs) {
        super();
        tabber = new SideBarTabber();
        tabber.setFixedToolBarWidth(72);
        if (optionalTabs != null) {
            for (int i = 0; i < optionalTabs.length; i++) {
                optionalTabs[i].reset();
                addTab(optionalTabs[i]);
            }
        }
        // Build this panel
        setLayout(new GridLayout(1, 1));
        add(tabber);

    }

    /**
     * 
     * 
     * @return
     */

    public boolean validateTabs() {
        return tabber.validateTabs();

    }

    /**
   *
   */

    public void applyTabs() {
        tabber.applyTabs();

    }

    /**
     * 
     * 
     * @param tab
     */

    public void addTab(OptionsTab tab) {
        tabber.addTab(tab);

    }

    /**
   *
   */

    public void reset() {
        for (int i = 0; i < tabber.getTabCount(); i++) {
            ((OptionsTab) tabber.getTabAt(i)).reset();
        }

    }

    /**
     * 
     * 
     * @param parent
     * @param tabs tabs
     * 
     * @return
     */

    public static boolean showOptionsDialog(Component parent, OptionsTab[] tabs) {
        final OptionsPanel opts = new OptionsPanel(tabs);
        opts.reset();
        Option opt = OptionDialog.prompt(parent, OptionChooser.UNCATEGORISED, 
            Messages.getString("OptionsPanel.Options"), opts, Option.CHOICES_OK_CANCEL, Option.CHOICE_CANCEL);
        if(opt.equals(Option.CHOICE_OK)) {
            opts.applyTabs();
            return true;
        }
        return false;

    }

}