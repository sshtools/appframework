package com.sshtools.appframework.ui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class LinkLabel extends JLabel {

	private boolean hover;
	private String originalText;

	public LinkLabel() {
		super();
	}

	public LinkLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public LinkLabel(Icon image) {
		super(image);
	}

	public LinkLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public LinkLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public LinkLabel(String text) {
		super(text);
	}

	{
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				calcText(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				calcText(false);
			}

			@Override
			public void mouseClicked(MouseEvent evt) {
				onClicked();
			}
		});
	}

	protected void calcText(boolean hover) {
		if (hover != this.hover) {
			if (hover) {
				originalText = getText();
				setText("<html><u>" + stripHtml(originalText) + "</u></html>");
			} else
				setText(originalText);
			this.hover = hover;
		}
	}

	private String stripHtml(String text) {
		if (text.startsWith("<html>"))
			text = text.substring(6);
		if (text.endsWith("</html>"))
			text = text.substring(0, text.length() - 7);
		return text;
	}

	protected void onClicked() {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (hover) {
			Insets i = getBorder() == null ? new Insets(0, 0, 0, 0) : getBorder().getBorderInsets(this);
			g.setColor(getForeground());
			System.out.println(String.format("XX %d,%d,%d,%d", i.left, getHeight() - i.bottom, getWidth() - i.right,
					getHeight() - i.bottom));
			g.drawLine(i.left, getHeight() - i.bottom, getWidth() - i.right, getHeight() - i.bottom);
		}
	}

}
