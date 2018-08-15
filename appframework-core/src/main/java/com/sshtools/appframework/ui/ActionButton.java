/* HEADER */
package com.sshtools.appframework.ui;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.sshtools.ui.swing.AppAction;

public class ActionButton extends JButton {
	// Private statics
	private final static Insets INSETS = new Insets(2, 2, 2, 2);
	private boolean enablePlasticWorkaround;
	// Private instance variables
	private boolean hideText;
	private boolean useToolIcon;

	/**
	 * Creates a new ToolButton object from an AppAction. The key for
	 * the icon value will be {@link AppAction#MEDIUM_ICON}. If the
	 * action has a property with a name of
	 * {@link AppAction#TEXT_ON_TOOLBAR}and a value of
	 * <code>Boolean.TRUE</code> then text text will be shown on the button.
	 * 
	 * @param action action
	 */
	public ActionButton(AppAction action) {
		this(action, true);
	}

	/**
	 * Creates a new ToolButton object from an AppAction. If useToolIcon
	 * is true, then the icon will be retrieved using the
	 * {@link AppAction#MEDIUM_ICON}key instead of
	 * {@link AppAction#SMALL_ICON}. If the action has a property with a
	 * name of {@link AppAction#TEXT_ON_TOOLBAR}and a value of
	 * <code>Boolean.TRUE</code> then text text will be shown on the button.
	 * 
	 * @param action action
	 * @param useToolIcon use MEDIUM_ICON instead of SMALL_ICON
	 */
	public ActionButton(AppAction action, boolean useToolIcon) {
		this(action, useToolIcon, true);
	}

	/**
	 * Creates a new ToolButton object from an AppAction. If useToolIcon
	 * is true, then the icon will be retrieved using the
	 * {@link AppAction#MEDIUM_ICON}key instead of
	 * {@link AppAction#SMALL_ICON}. If <code>showSelectiveText</code>
	 * is <code>true</code> and the action has a property with a name of
	 * {@link AppAction#TEXT_ON_TOOLBAR}and a value of
	 * <code>Boolean.TRUE</code> then text text will be shown on the
	 * 
	 * @param action action
	 * @param useToolIcon use MEDIUM_ICON instead of SMALL_ICON
	 * @param showSelectiveText show 'selective' text.
	 */
	public ActionButton(AppAction action, boolean useToolIcon, boolean showSelectiveText) {
		super();
		init(action, useToolIcon, showSelectiveText);
	}

	@Override
	public Insets getMargin() {
		return INSETS;
	}

	//
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.AbstractButton#getText()
	 */
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

	private void init(AppAction a, boolean useToolIcon, boolean showText) {
		this.useToolIcon = useToolIcon;
		enablePlasticWorkaround = UIManager.getLookAndFeel().getClass().getName().startsWith("com.jgoodies.looks.plastic.");
		setAction(a);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (isEnabled()) {
					setBorderPainted(true);
					if (!enablePlasticWorkaround) {
						setContentAreaFilled(true);
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBorderPainted(false);
				setContentAreaFilled(enablePlasticWorkaround);
			}
		});
		setBorderPainted(false);
		setContentAreaFilled(enablePlasticWorkaround);
		if (a != null && a.getValue(Action.ACCELERATOR_KEY) != null) {
			setMnemonic(0);
			registerKeyboardAction(a, (KeyStroke) a.getValue(Action.ACCELERATOR_KEY), JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
		if (useToolIcon) {
			setIcon((Icon) a.getValue(AppAction.MEDIUM_ICON));
		}
		if (Boolean.TRUE.equals(a.getValue(AppAction.TEXT_ON_TOOLBAR)) && showText) {
			setHideText(false);
		} else {
			setHideText(true);
		}
	}
}