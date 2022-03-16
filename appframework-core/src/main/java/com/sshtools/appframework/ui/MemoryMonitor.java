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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author brett
 */
@SuppressWarnings("serial")
public class MemoryMonitor extends JPanel implements ActionListener {

	private static JFrame frame;

	public static void showMemoryMonitor() {
		if (frame == null) {
			frame = new JFrame("Memory monitor");
			frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent evt) {
					frame.setVisible(false);
				}
			});
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(new MemoryMonitor(), BorderLayout.CENTER);
			frame.pack();
		}
		frame.setVisible(true);
	}

	private JProgressBar memoryGauge;

	public MemoryMonitor() {
		memoryGauge = new JProgressBar(0, 100) {

			@Override
			public Dimension getMinimumSize() {
				return new Dimension(100, 16);
			}

			@Override
			public Dimension getSize() {
				return getMinimumSize();
			}
		};
		memoryGauge.setBorder(BorderFactory.createLineBorder(Color.black));
		memoryGauge.setBackground(Color.white);
		memoryGauge.setForeground(Color.red);
		memoryGauge.setStringPainted(true);
		memoryGauge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				System.gc();
			}
		});
		add(memoryGauge);
		javax.swing.Timer timer = new javax.swing.Timer(2000, this);
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				doUpdate();
			}
		});
	}

	void doUpdate() {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		StringBuffer buf = new StringBuffer();
		buf.append("Memory    Total=");
		buf.append(total);
		buf.append(" Free=");
		buf.append(free);
		long used = total - free;
		buf.append(" Used=");
		buf.append(used);
		int gauge = (int) (((double) used / (double) total) * 100d);
		memoryGauge.setValue(gauge);
		memoryGauge.setToolTipText(buf.toString());
		buf.setLength(0);
		buf.append(gauge);
		buf.append("% (");
		buf.append((int) (used / 1024d / 1024d));
		buf.append("M)");
		memoryGauge.setString(buf.toString());
	}
}