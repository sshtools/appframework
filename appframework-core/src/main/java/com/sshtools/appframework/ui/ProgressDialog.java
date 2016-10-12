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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.UIUtil;

public class ProgressDialog extends JDialog {
    // Private instance variables
    private JProgressBar progressBar;
    private JLabel value1;
    private JLabel label1;
    private boolean canceled;
    private String geometryPropertyName;
    private JPanel progressPanel;
    private boolean indeterminate1;
    private Thread updateThread;
    private boolean stopUpdateThread;
    private BlockingQueue updates;
    private BlockingQueue waitQueue;
    private int progress;

    public ProgressDialog(JDialog parent, Icon icon, boolean modal, AppAction action, String label1Text, String title,
                          String geometryPropertyName, boolean closeEnabled) {
        super(parent, title, modal);
        init(icon, action, label1Text, geometryPropertyName, closeEnabled);
    }

    public ProgressDialog(JFrame parent, Icon icon, boolean modal, AppAction action, String label1Text, String title,
                          String geometryPropertyName, boolean closeEnabled) {
        super(parent, title, modal);
        init(icon, action, label1Text, geometryPropertyName, closeEnabled);
    }

    public static ProgressDialog createDialog(Component parent, Icon icon, String geometryPropertyName, String label1Text,
                    String title, boolean closeEnabled) {
        return createDialog(parent, icon, geometryPropertyName, null, label1Text, title, true, closeEnabled);
    }

    public static ProgressDialog createDialog(Component parent, Icon icon, String geometryPropertyName, AppAction action,
                    String label1Text, String title, boolean modal, boolean closeEnabled) {
        Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
        ProgressDialog progressDialog = null;
        if ((w != null) && w instanceof JDialog) {
            progressDialog = new ProgressDialog((JDialog) w, icon, modal, action, label1Text, title, geometryPropertyName, closeEnabled);
        } else if ((w != null) && w instanceof JFrame) {
            progressDialog = new ProgressDialog((JFrame) w, icon, modal, action, label1Text, title, geometryPropertyName, closeEnabled);
        } else {
            progressDialog = new ProgressDialog((JFrame) null, icon, modal, action, label1Text, title, geometryPropertyName, closeEnabled);
        }
        return progressDialog;
    }

    public void cancel() {
        canceled = true;
    }

    private void init(Icon icon, AppAction action, String label1Text, String geometryPropertyName, boolean closeEnabled) {
        this.geometryPropertyName = geometryPropertyName;
        //
        progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        progressBar = new JProgressBar(0, 100);
        setIndeterminate(true);
        progressBar.setStringPainted(true);
        JPanel s = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        UIUtil.jGridBagAdd(s, label1 = new JLabel(label1Text), gbc, GridBagConstraints.RELATIVE);
        gbc.weightx = 1.0;
        UIUtil.jGridBagAdd(s, value1 = new JLabel(), gbc, GridBagConstraints.REMAINDER);
        gbc.weightx = 0.0;
        progressPanel.add(s, BorderLayout.NORTH);
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        p.add(progressBar, BorderLayout.NORTH);
        progressPanel.add(p, BorderLayout.SOUTH);
        //
        JPanel x = new JPanel(new BorderLayout());
        x.add(progressPanel, BorderLayout.CENTER);
        if (action != null) {
            JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER));
            b.add(new JButton(action));
            x.add(b, BorderLayout.SOUTH);
        }
        getContentPane().setLayout(new BorderLayout());
        if (icon != null) {
            IconWrapperPanel iw = new IconWrapperPanel(icon, x);
            getContentPane().add(iw, BorderLayout.CENTER);
        } else {
            getContentPane().add(x, BorderLayout.CENTER);
        }
        // Postion and size
        Rectangle r = PreferencesStore.getRectangle(geometryPropertyName, null);
        if (r != null) {
            setBounds(r);
        } else {
            pack();
            UIUtil.positionComponent(SwingConstants.CENTER, this);
        }
        if(closeEnabled) {
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    completeProgress();
                }
            });
        }
        updates = new ArrayBlockingQueue(100, true);
        waitQueue = new ArrayBlockingQueue(3, true);
    }

    public void saveGeometry() {
        PreferencesStore.putRectangle(geometryPropertyName, ProgressDialog.this.getBounds());
    }

    public void setVisible(boolean visible) {
        boolean wasVisible = isVisible();
        if (wasVisible != visible) {
            if (isVisible() && !visible) {
                saveGeometry();
            }
            super.setVisible(visible);
            if (!visible) {
                // notify anyone waiting on our lock just in case
                stopUpdateThread = true;
            }
        }
    }

    public JComponent getMainComponent() {
        return progressPanel;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setLabelText(String text) {
        label1.setText(text);
    }

    public void setIndeterminate(boolean indeterminate) {
        try {
            progressBar.getClass().getMethod("setIndeterminate", new Class[] {
                boolean.class
            }).invoke(progressBar, new Object[] {
                new Boolean(indeterminate)
            });
        } catch (Exception e) {
            progressBar.setValue(0);
        }
        this.indeterminate1 = indeterminate;
    }

    public void setString(String text) {
        progressBar.setString(text);
    }

    public void setStringPainted(boolean stringPainted) {
        progressBar.setStringPainted(stringPainted);
    }

    public boolean isStringPainted() {
        return progressBar.isStringPainted();
    }

    public void setValueText(String text) {
        value1.setText(text);
    }

    public void setProgressMaximum(int max) {
        progressBar.setMaximum(max);
    }

    public void setProgressMinimum(int min) {
        progressBar.setMinimum(min);
    }

    public void setValueToolTipText(String text) {
        value1.setToolTipText(text);
    }

    public int getProgressMaximum() {
        return progressBar.getMaximum();
    }

    public int getProgressValue() {
        return progressBar.getValue();
    }

    public boolean isProgressIndeterminate() {
        return indeterminate1;
    }

    public void completeProgress() {
        if (updateThread != null) {
            updateThread.interrupt();
        }
    }

    public void resetProgress() {
        progress = 0;
        updates.clear();
    }

    public void updateProgress(final int progress, final String text, boolean wait) {
        if (updateThread == null) {
            stopUpdateThread = false;
            updateThread = new UpdateThread();
        }
        Update update = new Update(text, progress);
        try {
            updates.put(update);
            if (wait) {
                waitQueue.take();
            }
        } catch (InterruptedException e1) {
        }
    }

    public void packHeight() {
        Container parent = getParent();
        if (parent != null && parent.getPeer() == null) {
            parent.addNotify();
        }
        if (getPeer() == null) {
            addNotify();
        }
        setSize(new Dimension(getSize().width, getPreferredSize().height));
        validate();
    }

    public void reset() {
        canceled = false;
    }

    class Update {
        String text;
        int progress;

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

        public void run() {
            try {
                while (!stopUpdateThread) {
                    consume(updates.take());
                }
            } catch (Exception e) {
            }
            updateThread = null;
            setVisible(false);
        }

        void consume(Object o) throws InterruptedException, InvocationTargetException {
            final Update update = (Update) o;
            while (progress < update.progress) {
                progress++;
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        progressBar.setValue(progress);
                    }
                });
            }
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    setLabelText(update.text);
                }
            });
            if (updates.size() == 0) {
                waitQueue.put(Boolean.TRUE);
            }
        }
    }
}