/**
 * Maverick Application Framework - Application framework
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sshtools.appframework.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.sshtools.appframework.api.ui.MultilineLabel;
import com.sshtools.ui.Option;
import com.sshtools.ui.OptionChooser;
import com.sshtools.ui.swing.OptionDialog;


public class TellMeAgainPane
    extends JPanel {
    /**
     * Show a 'Tell me again' dialog. <code>null</code> may be passed for
     * checkBoxMessage (uses 'Tell me about this again'). Options may be
     * specified.
     *
     * @param parent parent component
     * @param checkBoxMessage message to use for the checkbox (defaults to
     * @param property property
     * @param text text
     * @param options options
     * @return option selected option
     * @param title dialog title
     * @param icon icon
     */
    public static Option showTellMeAgainDialog(
        JComponent parent, String checkBoxMessage,
        String property, String text, Option[] options,
        String title, Icon icon) {
        //
        if (text == null) {
            throw new IllegalArgumentException("text argument may not be null");
        }

        if (property == null) {
            throw new IllegalArgumentException(
                "property argument may not be null");
        }

        //  If the property is 'false', then don't show the dialog
        if (!PreferencesStore.getBoolean(property, true)) {
            return null;
        }

        //
        TellMeAgainPane t = new TellMeAgainPane(property, text,
                                                checkBoxMessage);
        t.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        //  Show the dialog
        return OptionDialog.prompt(parent, OptionChooser.QUESTION, title, 
        		t, options, options[0], null, icon);
    }
    /**
     * Show a 'Tell me again' dialog. <code>null</code> may be passed for
     * checkBoxMessage (uses 'Tell me about this again').
     *
     * @param parent parent component
     * @param checkBoxMessage message to use for the checkbox (defaults to
     * @param property property
     * @param text text
     * @param title dialog title
     * @param icon icon
     */
    public static void showTellMeAgainDialog(JComponent parent,
                                             String checkBoxMessage,
                                             String property,
                                             String text, String title,
                                             Icon icon) {
        showTellMeAgainDialog(parent, checkBoxMessage, property, text,
                              Option.CHOICES_CLOSE, title, icon);
    }

    //  Private instance variables
    private String property;

    private JCheckBox tellMeAgainCheckBox;

    /*
     * Can't be instantiated
     */
    private TellMeAgainPane(String property,
                            String text, String checkBoxText) {
        super(new BorderLayout(0, 6));

        this.property = property;

        //  The check box
        tellMeAgainCheckBox = new JCheckBox( (checkBoxText == null)
                                            ? "Tell me about this again" :
                                            checkBoxText, true);
        tellMeAgainCheckBox.setBorder(BorderFactory.createEmptyBorder(8, 4, 8,
            4));
        tellMeAgainCheckBox.setHorizontalAlignment(SwingConstants.CENTER);

        //
        JPanel p = new JPanel(new BorderLayout());
        MultilineLabel ml = new MultilineLabel(text);
        ml.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
        p.add(ml, BorderLayout.CENTER);
        p.add(tellMeAgainCheckBox, BorderLayout.SOUTH);

        //  Build this panel
        add(p, BorderLayout.CENTER);
    }
}
