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
/**
 * 
 */
package com.sshtools.appframework.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.GradientPanel;
import com.sshtools.ui.swing.ResourceIcon;
import com.sshtools.ui.swing.WrappingLabel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class MessagePanel extends GradientPanel {

	public interface Listener {
		void cancelled();
	}

	public enum Type {
		error(new Color(255, 68, 59), Color.white,
				new ResourceIcon("/images/error-24x24.png")), hidden(null, null, null), information(
						new Color(59, 133, 255), Color.white, new ResourceIcon("/images/information-24x24.png")), progress(new Color(251, 255, 139), Color.black, null);

		Color background;
		Color foreground;
		Icon icon;

		Type(Color background, Color foreground, Icon icon) {
			this.background = background;
			this.foreground = foreground;
			this.icon = icon;
		}
	}

	class CancelAction extends AppAction {

		CancelAction() {
			super("Cancel");
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			super.actionPerformed(evt);
			cancelled = true;
			for (int i = MessagePanel.this.listeners.size() - 1; i >= 0; i--) {
				MessagePanel.this.listeners.get(i).cancelled();
			}
		}

	}
	class Update {
		int progress;
		String text;

		Update(String text, int progress) {
			this.text = text;
			this.progress = progress;
		}
	}
	class UpdateThread extends Thread {
		UpdateThread() {
			super("ProgressUpdateThread");
			start();
			progress = 0;
		}

		@Override
		public void run() {
			try {
				while (!stopUpdateThread) {
					consume(updates.take());
				}
			} catch (Exception e) {
			}
			updateThread = null;
		}

		void consume(Object o) throws InterruptedException, InvocationTargetException {
			final Update update = (Update) o;
			while (!stopUpdateThread && progress < update.progress) {
				progress += 2;
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						progressBar.setValue(progress);
					}
				});
			}
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					progressBar.setValue(update.progress);
					doSetMessage(update.text);
				}
			});
			if (updates.size() == 0) {
				try {
					waitQueue.put(Boolean.TRUE);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	protected static void appendException(Throwable exception, int level, StringBuffer buf, boolean details) {
		try {
			if (((exception != null) && (exception.getMessage() != null)) && (exception.getMessage().length() > 0)) {
				if (details && (level > 0)) {
					buf.append("\n \nCaused by ...\n"); //$NON-NLS-1$
				}
				buf.append(exception.getMessage());
			}
			if (details) {
				if (exception != null) {
					if ((exception.getMessage() != null) && (exception.getMessage().length() == 0)) {
						buf.append("\n \nCaused by ..."); //$NON-NLS-1$
					} else {
						buf.append("\n \n"); //$NON-NLS-1$
					}
				}
				StringWriter sw = new StringWriter();
				if (exception != null) {
					exception.printStackTrace(new PrintWriter(sw));
				}
				buf.append(sw.toString());
			}
			try {
				java.lang.reflect.Method method = exception.getClass().getMethod("getCause", new Class[] {}); //$NON-NLS-1$
				Throwable cause = (Throwable) method.invoke(exception, (Object[]) null);
				if (cause != null) {
					appendException(cause, level + 1, buf, details);
				}
			} catch (Exception e) {
			}
		} catch (Throwable ex) {
		}
	}
	private JPanel actionsPanel;
	private boolean cancelled;
	private JLabel icon;
	private List<Listener> listeners = new ArrayList<Listener>();
	private Object lock = new Object();
	private WrappingLabel message;
	private int progress;
	private JProgressBar progressBar;

	private JPanel progressPanel;

	private boolean stopUpdateThread;

	private BlockingQueue<Update> updates;

	private Thread updateThread;

	private BlockingQueue<Boolean> waitQueue;
	private Type type;
	private AppAction[] actions;

	public MessagePanel() {
		this(Type.hidden);
	}

	public MessagePanel(Type type) {
		super(new MigLayout("wrap 3, hidemode 3", "[][fill,grow][]", "[][]"));
		setOpaque(true);

		add(icon = new JLabel());
		add(message = new WrappingLabel(), "growx");
		message.setBackground(Color.green);
		message.setOpaque(true);
		actionsPanel = new JPanel(new MigLayout());
		actionsPanel.setOpaque(false);
		add(actionsPanel);
		progressPanel = new JPanel(new BorderLayout());
		progressPanel.setOpaque(false);
		progressBar = new JProgressBar();
		progressPanel.add(progressBar, BorderLayout.CENTER);
		add(progressPanel, "span 2, wrap");

		updates = new ArrayBlockingQueue<>(100, true);
		waitQueue = new ArrayBlockingQueue<>(3, true);

	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void error(String mesg, Throwable exception) {
		setType(Type.error);
		StringBuffer buf = new StringBuffer();
		if (mesg != null) {
			buf.append(mesg.replace("\n", " ") + ""); //$NON-NLS-1$
		}

		// Search up the exception chain until we get a message
		while (exception != null) {
			String message = exception.getLocalizedMessage();
			if (message == null) {
				message = exception.getMessage();
			}
			if (message != null) {
				message = message.trim().replace("\n", " ");
				buf.append(message);
				if (!message.trim().endsWith(".")) {
					buf.append(". ");
				}
			}
			exception = exception.getCause();
		}

		// appendException(exception, 0, buf, false);/
		setMessage(buf.toString());
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public AppAction[] getActions() {
		return actions;
	}

	public void setActions(AppAction[] actions) {
		invalidate();
		actionsPanel.removeAll();
		this.actions = actions;
		if (actions != null) {
			for (AppAction action : actions) {
				JButton button = new JButton(action);
				actionsPanel.add(button);
			}
		}
		actionsPanel.setVisible(actionsPanel.getComponentCount() > 0);
		validate();
		repaint();
	}
	
	public synchronized void setIcon(Icon icon) {
		clearUpdates();
		this.icon.setIcon(icon);
	}

	public void setMessage(String message) {
		clearUpdates();
		doSetMessage(message);
	}

	public String getMessage() {
		return message.getText();
	}

	public void setProgressMaximum(int max) {
		progressBar.setMaximum(max);
	}

	public void setProgressValue(int val) {
		progressBar.setValue(val);
		clearUpdates();
	}

	public void setType(Type type) {
		this.type = type;
		invalidate();
		setBackground(type.background);
		setForeground(type.foreground);
		setVisible(!type.equals(Type.hidden));
		setIcon(type.icon);
		progressPanel.setVisible(type.equals(Type.progress));
		actionsPanel.setVisible(false);
		validate();
		repaint();
		if (type.equals(Type.progress)) {
			setActions(new AppAction[] { new CancelAction() });
		} else {
			setActions(null);
		}
	}

	public Type getType() {
		return type;
	}

	public void uncancel() {
		cancelled = false;
	}

	public void updateProgress(int progress, String message, boolean wait) {
		synchronized (lock) {
			if (updateThread == null) {
				stopUpdateThread = false;
				updateThread = new UpdateThread();
			}
		}
		Update update = new Update(message, progress);
		waitQueue.clear();
		try {
			updates.put(update);
			if (wait && updates.size() > 0) {
				waitQueue.take();
			}
		} catch (InterruptedException e1) {
		}
	}

	protected void doSetMessage(String message) {
		getParent().invalidate();
		this.message.setText(message);
		getParent().validate();
		getParent().repaint();
	}

	protected void clearUpdates() {
		if (updateThread != null) {
			try {
				stopUpdateThread = true;
				Update update = null;
				updateThread.interrupt();
				while (updates.size() > 0) {
					update = (Update) updates.take();
				}
				if (update != null) {
					progressBar.setValue(update.progress);
					message.setText(update.text);
				}
				updates.clear();
			} catch (InterruptedException e) {
			}
		}
	}
}