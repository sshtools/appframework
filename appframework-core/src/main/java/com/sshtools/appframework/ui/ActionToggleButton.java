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
/* HEADER */
package com.sshtools.appframework.ui;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.sshtools.ui.swing.AppAction;

/**
 * Extension of a <code>JToggleButton</code> that is built from an
 * {@link com.sshtools.ui.swing.AppAction}and is more suitable for a toolbar
 * (i.e. focus painting is disable, margin is descreased etc).
 */
public class ActionToggleButton extends JToggleButton {
	// Private statics
	private final static Insets INSETS = new Insets(0, 6, 0, 6);
	private static final long serialVersionUID = 4070359229035765928L;
	private boolean enablePlasticWorkaround;
	// Private instance variables
	private boolean hideText;
	private boolean useToolIcon;

	/**
	 * Creates a new ActionToggleButton object from an AppAction. The key for
	 * the icon value will be {@link AppAction#MEDIUM_ICON}. If the action has a
	 * property with a name of {@link AppAction#TEXT_ON_TOOLBAR}and a value of
	 * <code>Boolean.TRUE</code> then text text will be shown on the button.
	 * 
	 * @param action action
	 */
	public ActionToggleButton(AppAction action) {
		this(action, true);
	}

	/**
	 * Creates a new ActionToggleButton object from an AppAction. If useToolIcon
	 * is true, then the icon will be retrieved using the
	 * {@link AppAction#MEDIUM_ICON}key instead of {@link AppAction#SMALL_ICON}.
	 * If the action has a property with a name of
	 * {@link AppAction#TEXT_ON_TOOLBAR}and a value of <code>Boolean.TRUE</code>
	 * then text text will be shown on the button.
	 * 
	 * @param action action
	 * @param useToolIcon use MEDIUM_ICON instead of SMALL_ICON
	 */
	public ActionToggleButton(AppAction action, boolean useToolIcon) {
		this(action, useToolIcon, true);
	}

	/**
	 * Creates a new ActionToggleButton object from an AppAction. If useToolIcon
	 * is true, then the icon will be retrieved using the
	 * {@link AppAction#MEDIUM_ICON}key instead of {@link AppAction#SMALL_ICON}.
	 * If <code>showSelectiveText</code> is <code>true</code> and the action has
	 * a property with a name of {@link AppAction#TEXT_ON_TOOLBAR}and a value of
	 * <code>Boolean.TRUE</code> then text text will be shown on the
	 * 
	 * @param action action	
	 * @param useToolIcon use MEDIUM_ICON instead of SMALL_ICON
	 * @param showSelectiveText show 'selective' text.
	 */
	public ActionToggleButton(AppAction action, boolean useToolIcon, boolean showSelectiveText) {
		super();
		init(action, useToolIcon, showSelectiveText);
	}

	@Override
	public Insets getMargin() {
		return INSETS;
	}

	@Override
	public String getText() {
		return hideText ? null : super.getText();
	}

	@Override
	public boolean isFocusTraversable() {
		return false;
	}

	@Override
	public boolean isRequestFocusEnabled() {
		return false;
	}

	/**
	 * Set whether the button text for this component should be shown
	 * 
	 * @param hideText hide button text
	 */
	public void setHideText(boolean hideText) {
		if (this.hideText != hideText) {
			firePropertyChange("hideText", this.hideText, hideText);
		}
		this.hideText = hideText;
		this.setHorizontalTextPosition(SwingConstants.RIGHT);
		repaint();
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		if (selected) {
			setBorderPainted(true);
			if (!enablePlasticWorkaround) {
				setContentAreaFilled(true);
			}
		} else {
			setBorderPainted(false);
			setContentAreaFilled(enablePlasticWorkaround);
		}
	}

	@Override
	protected void actionPropertyChanged(Action action, String propertyName) {
		super.actionPropertyChanged(action, propertyName);
		if (Action.SMALL_ICON.equals(propertyName) || AppAction.MEDIUM_ICON.equals(propertyName)
				|| AppAction.LARGE_ICON.equals(propertyName) || propertyName == null) {
			if (useToolIcon) {
				setIcon((Icon) action.getValue(AppAction.MEDIUM_ICON));
				repaint();
			}
		}
	}

	@Override
	protected void configurePropertiesFromAction(Action a) {
		super.configurePropertiesFromAction(a);
		if (useToolIcon)
			setIcon((Icon) a.getValue(AppAction.MEDIUM_ICON));
	}

	private void init(AppAction a, boolean useToolIcon, boolean showText) {
		this.useToolIcon = useToolIcon;
		setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		enablePlasticWorkaround = UIManager.getLookAndFeel().getClass().getName().startsWith("com.jgoodies.looks.plastic.");
		setAction(a);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (isEnabled() && !isSelected()) {
					setBorderPainted(true);
					if (!enablePlasticWorkaround) {
						setContentAreaFilled(true);
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (!isSelected()) {
					setBorderPainted(false);
					setContentAreaFilled(enablePlasticWorkaround);
				}
			}
		});
		setBorderPainted(false);
		setContentAreaFilled(enablePlasticWorkaround);
		if (a != null && a.getValue(Action.ACCELERATOR_KEY) != null) {
			setMnemonic(0);
			registerKeyboardAction(a, (KeyStroke) a.getValue(Action.ACCELERATOR_KEY), JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
		if (a != null && useToolIcon) {
			setIcon((Icon) a.getValue(AppAction.MEDIUM_ICON));
		}
		if (Boolean.TRUE.equals(a.getValue(AppAction.TEXT_ON_TOOLBAR)) && showText) {
			setHideText(false);
		} else {
			setHideText(true);
		}
		a.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName() == null || AppAction.IS_SELECTED.equals(evt.getPropertyName()))
					setSelected(Boolean.TRUE.equals(evt.getNewValue()));
			}
		});
		setSelected(Boolean.TRUE.equals(a.getValue(AppAction.IS_SELECTED)));
	}
}