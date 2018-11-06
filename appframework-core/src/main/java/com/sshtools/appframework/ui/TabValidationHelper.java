package com.sshtools.appframework.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.maverick.di.InfoBubble;
import com.sshtools.ui.swing.TabValidationException;

public class TabValidationHelper {
	private JComponent errorParent;
	private JLabel errorLabel;
	private InfoBubble bubble;
	private Timer bubbleTimer;

	public void handleTabValidationException(TabValidationException tve) {
		JComponent component = tve.getComponent();
		component.requestFocusInWindow();
		Window w = SwingUtilities.getWindowAncestor(component);
		Container root = null;
		if (w instanceof JFrame) {
			JFrame f = (JFrame) w;
			errorParent = (JComponent) f.getGlassPane();
			root = f.getContentPane();
		} else if (w instanceof JDialog) {
			JDialog f = (JDialog) w;
			errorParent = (JComponent) f.getGlassPane();
			root = f.getContentPane();
		} else
			throw tve;
		errorParent.setLayout(null);
		errorLabel = new JLabel(IconStore.getInstance().getIcon("process-stop", 12));
		errorParent.add(errorLabel);
		Point p = getRelativePosition(component, root);
		errorLabel.setBounds(p.x - 14, p.y + ((component.getHeight() - 12) / 2), 12, 12);
		errorLabel.setPreferredSize(errorLabel.getSize());
		errorLabel.setToolTipText(getMessageText(tve));
		showInfoBox(tve);
		errorParent.revalidate();
		errorParent.setVisible(true);
	}

	private String getMessageText(TabValidationException tve) {
		if (StringUtils.isBlank(tve.getMessage()))
			if (tve.getCause() != null && StringUtils.isNotBlank(tve.getCause().getMessage()))
				return tve.getCause().getMessage();
			else
				return "This is a required field.";
		else
			return tve.getMessage();
	}

	void showInfoBox(TabValidationException tve) {
		if (bubble != null)
			hideBubble();
		bubble = new InfoBubble("<html>" + StringEscapeUtils.escapeHtml(getMessageText(tve)) + "</html>",
				IconStore.getInstance().getIcon("process-stop", 48), tve.getComponent(), new Point(0, 0));
		bubble.show();
		bubbleTimer = new Timer(10000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bubble != null)
					bubble.hide();
			}
		});
		bubbleTimer.start();
	}

	private void hideBubble() {
		if (bubble != null) {
			bubble.hide();
			bubble = null;
			if (bubbleTimer != null)
				bubbleTimer.stop();
		}
	}

	public void clearErrors() {
		if (errorLabel != null) {
			errorParent.invalidate();
			errorParent.remove(errorLabel);
			errorParent.validate();
			errorParent.repaint();
			errorLabel = null;
			errorParent = null;
		}
	}

	Point getRelativePosition(Component c, Container container) {
		Point p = new Point();
		while (c != null && c != container) {
			Point rel = c.getLocation();
			p.translate(rel.x, rel.y);
			c = c.getParent();
		}
		return p;
	}
}
