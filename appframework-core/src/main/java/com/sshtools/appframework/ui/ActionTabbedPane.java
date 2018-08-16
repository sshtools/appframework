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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.sshtools.ui.swing.ActionButton;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.DnDTabbedPane;
import com.sshtools.ui.swing.TabDraggedListener;

/**
 * An extension of <em>JTabbedPane</em> that renders a close graphic onto each
 * tab.
 * 
 * @author $Author: brett $
 */
public class ActionTabbedPane extends DnDTabbedPane {
	class CloseTabAction extends AppAction {

		private TabHeader header;

		public CloseTabAction(TabHeader header) {
			super();
			this.header = header;
			putValue(NAME, "Close Tab");
			// putValue(SMALL_ICON, new CloseIcon(UIManager
			// .getFont("ToolTip.font").getSize()));
			// putValue(SMALL_ICON, new
			// ImageIcon(getClass().getResource("tab-close.png")));
			Icon icon = UIManager.getIcon("InternalFrame.closeIcon");
			if (icon == null
					|| icon.getClass().getName().endsWith("$EmptyFrameIcon")) {
				putValue(SMALL_ICON,
						new ImageIcon(getClass().getResource("tab-close.png")));
			} else {
				putValue(SMALL_ICON, icon);
			}
			putValue(SHORT_DESCRIPTION, "Close this tab");
			putValue(LONG_DESCRIPTION, "Close this tab");
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			int idx = getTabIndex(header);
			if (getSelectedIndex() != idx) {
				setSelectedIndex(idx);
			}
			if (closeAction != null && closeAction.isEnabled()) {
				closeAction.actionPerformed(evt);
			}
		}
	}
	class CloseTabIcon implements Icon {
		private Icon fileIcon;

		private int height;

		private int width;

		private int x_pos;

		private int y_pos;

		public CloseTabIcon(Icon fileIcon) {
			this.fileIcon = fileIcon;
			width = fileIcon == null ? 16 : fileIcon.getIconWidth();
			height = fileIcon == null ? 16 : fileIcon.getIconHeight();
		}

		public Rectangle getBounds() {
			return new Rectangle(x_pos, y_pos, width, height);
		}

		@Override
		public int getIconHeight() {
			return height;
		}

		@Override
		public int getIconWidth() {
			return width;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			this.x_pos = x;
			this.y_pos = y;
			if (fileIcon != null) {
				fileIcon.paintIcon(c, g, x, y);
			}
		}
	}
	class TabHeader extends JPanel {

		private ActionButton closeTabButton;
		private JLabel label;

		TabHeader(String text, Icon icon) {
			super(new BorderLayout(0, 0));
			setOpaque(false);
			label = new JLabel(text, icon, SwingConstants.CENTER);
			label.setBorder(null);
//			label.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			label.setFocusable(false);
			setBorder(null);
			MouseAdapter listener = new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (closeTabButton.isVisible()
							&& (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
						selectIfRequired();
						showPopup(TabHeader.this);
					} else if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
						selectIfRequired();
					}
				}

				private void selectIfRequired() {
					int thisTabIndex = getTabIndex(TabHeader.this);
					if (getSelectedIndex() != thisTabIndex) {
						setSelectedIndex(thisTabIndex);
					}
				}

			};
			addMouseListener(listener);
			// label.addMouseListener(listener);

			enableEvents(AWTEvent.FOCUS_EVENT_MASK | AWTEvent.ACTION_EVENT_MASK
					| AWTEvent.MOUSE_MOTION_EVENT_MASK
					| AWTEvent.MOUSE_EVENT_MASK);
			enableEvents(AWTEvent.KEY_EVENT_MASK);
			setFocusable(true);
			add(label, BorderLayout.CENTER);
			closeTabButton = new ActionButton(new CloseTabAction(this));
			closeTabButton.setHideText(true);
			add(closeTabButton, BorderLayout.EAST);
		}

		public void setCanClose(boolean canClose) {
			closeTabButton.setVisible(canClose);
		}
	}
	private Vector actions;

	private AppAction closeAction;

	private boolean dragging;

	private JPopupMenu popup;

	public ActionTabbedPane() {
		super(JTabbedPane.SCROLL_TAB_LAYOUT);
		init();
	}

	public void addAction(Action action) {
		if (!actions.contains(action)) {
			actions.add(action);
		}
	}

	@Override
	public void addTab(String title, Component component) {
		this.addTab(title, component, null);
	}

	public void addTab(String title, Component component, Icon extraIcon) {
		int idx = getTabCount();
		super.addTab(title, extraIcon == null ? null : new CloseTabIcon(
				extraIcon), component);
		if (!dragging) {
			TabHeader header = new TabHeader(title, extraIcon);
			doSetTabComponentAt(idx, header);
		}
	}

	@Override
	public void addTab(String title, Icon extraIcon, Component component,
			String toolTip) {
		int idx = getTabCount();
		super.addTab(title, extraIcon == null ? null : new CloseTabIcon(
				extraIcon), component, toolTip);
		if (!dragging) {
			TabHeader header = new TabHeader(title, extraIcon);
			doSetTabComponentAt(idx, header);
		}
	}

	public void insertTab(String title, Component component, Icon extraIcon,
			int idx) {
		super.insertTab(title, extraIcon == null ? null : new CloseTabIcon(
				extraIcon), component, null, idx);
		if (!dragging) {
			TabHeader header = new TabHeader(title, extraIcon);
			doSetTabComponentAt(idx, header);
		}
	}

	public void insertTab(String title, Component component, int idx) {
		this.insertTab(title, component, null, idx);
	}

	@Override
	public void insertTab(String title, Icon icon, Component component, String tip, int index) {
    	super.insertTab(title, icon, component, tip, index);
		if (!dragging) {
			TabHeader header = new TabHeader(title, icon);
			doSetTabComponentAt(index, header);
		}
    }

	public void removeAction(Action action) {
		actions.remove(action);
	}

	public void removeAllActions() {
		actions.removeAllElements();
	}

	@Override
	public void removeTabAt(int index) {
		super.removeTabAt(index);
	}

    public void setCanClose(int idx, boolean canClose) {
		try {
			Object th = getClass().getMethod("getTabComponentAt", int.class)
					.invoke(this, idx);
			if(th != null)
				th.getClass().getMethod("setCanClose", boolean.class)
					.invoke(th, canClose);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCloseAction(AppAction closeAction) {
		this.closeAction = closeAction;
	}

	@Override
	public void setIconAt(int index, Icon icon) {
		super.setIconAt(index, new CloseTabIcon(icon));
		getTabHeader(index).label.setIcon(icon);
	}

	@Override
	public void setTitleAt(int index, String title) {
		// TODO Auto-generated method stub
		super.setTitleAt(index, title);
		getTabHeader(index).label.setText(title);
	}

//	public void mouseEntered(MouseEvent e) {
//	}
//
//	public void mouseExited(MouseEvent e) {
//	}
//
//	public void mousePressed(MouseEvent e) {
//	}
//
//	public void mouseReleased(MouseEvent e) {
//	}

	TabHeader getTabHeader(int index) {
		return (TabHeader) getTabComponentAt(index);
//		for(int i = 0 ; i < getTabCount(); i++) {
//			Component c = getTabComponentAt(index);
//		}
//		return null;
	}
	
	int getTabIndex(TabHeader header) {
		for(int i = 0 ; i < getTabCount(); i++) {
			if(getTabComponentAt(i) == header) {
				return i;
			};
		}
		return -1;
	}
	
	private void doSetTabComponentAt(int idx, TabHeader header) {
		try {
			getClass().getMethod("setTabComponentAt", int.class,
					Component.class).invoke(this, idx, header);
		} catch (Exception e) {
		}
	}

	private void init() {
		actions = new Vector();

		addTabDraggedListener(new TabDraggedListener() {

			@Override
			public void tabbedMoved(int oldIndex, int newIndex) {
				dragging = false;
//				TabHeader oh = headers.remove(oldIndex);
//				if (oldIndex < newIndex) {
//					headers.add(newIndex - 1, oh);
//				} else {
//					headers.add(newIndex, oh);
//				}
			}

			@Override
			public void tabbedMoving(int oldIndex, int newIndex) {
				dragging = true;
			}

			@Override
			public void tabDetached(int index, Point point) {
			}
		});
	}

	private void showPopup(Component c) {
		if (getSelectedIndex() != -1) {
			if (popup == null) {
				popup = new JPopupMenu(""); //$NON-NLS-1$
			}
			popup.setLabel(getTitleAt(getSelectedIndex()));
			popup.invalidate();
			popup.removeAll();
			Action action;
			for (Enumeration en = actions.elements(); en.hasMoreElements();) {
				action = (Action) en.nextElement();
				if (action != null)
					popup.add(action);
			}
			popup.validate();
			popup.show(c, 10, 10);
		}
	}
}