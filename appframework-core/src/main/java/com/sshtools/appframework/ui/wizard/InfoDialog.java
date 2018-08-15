/* HEADER */
package com.sshtools.appframework.ui.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.sshtools.ui.swing.ResourceIcon;
import com.sshtools.ui.swing.UIUtil;

public class InfoDialog extends JDialog {
	FlowLayout flowLayout1 = new FlowLayout();
	InfoPanel infopanel = new InfoPanel();

	/**
	 * Creates a new InfoDialog object.
	 */
	public InfoDialog() {
		super();
		try {
			jbInit();
			// infopanel.setVisible(true);
			getContentPane().add(infopanel);
			setSize(300, 200);
			// setResizable(false);
			UIUtil.positionComponent(SwingConstants.CENTER, this);
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.getContentPane().setLayout(flowLayout1);
	}
}

class InfoPanel extends JPanel {
	BorderLayout borderLayout1 = new BorderLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	BorderLayout borderLayout3 = new BorderLayout();
	JButton buttonok = new JButton("OK");
	JTextPane finishSummary = new JTextPane();
	JLabel iconLabel = new JLabel();
	JPanel iconPanel = new JPanel();
	JPanel panelButtons = new JPanel();
	ResourceIcon resicon = new ResourceIcon(InfoDialog.class, "/images/dialog-information.png");
	JScrollPane summaryScrollPane = new JScrollPane();
	private int xWidth;
	private int yHeight;

	/**
	 * Creates a new InfoPanel object.
	 */
	public InfoPanel() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setLayout(borderLayout1);
		setBackground(Color.white);
		// The icon panel
		iconLabel.setIcon(resicon);
		iconPanel.add(iconLabel, null);
		panelButtons.add(buttonok);
		// The wizard details summary
		finishSummary.setBorder(null);
		// finishSummary.setLineWrap(true);
		// finishSummary.setWrapStyleWord(true);
		// The scrollpane to which we will add our summary text area
		summaryScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		JTextArea maintext = new JTextArea();
		JLabel titletext = new JLabel();
		JCheckBox check = new JCheckBox("Don't show this message again");
		JPanel checkpanel = new JPanel();
		checkpanel.add(check, BorderLayout.CENTER);
		titletext.setText("This is the title of the window");
		// titletext.setFont(My3sp.MY3SP_FONT.deriveFont(Font.BOLD));
		maintext.setText("This is an information message.  Please make sure you listen carefully");
		maintext.setWrapStyleWord(true);
		maintext.setLineWrap(true);
		summaryScrollPane.getViewport().add(maintext);
		summaryScrollPane.setBorder(null);
		summaryScrollPane.setBackground(Color.lightGray);
		maintext.setBorder(null);
		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new BorderLayout());
		mainpanel.add(titletext, BorderLayout.NORTH);
		mainpanel.add(summaryScrollPane, BorderLayout.CENTER);
		mainpanel.add(checkpanel, BorderLayout.SOUTH);
		// iconLabel.setLayout(borderLayout3);
		this.add(iconPanel, BorderLayout.WEST);
		this.add(mainpanel, BorderLayout.CENTER);
		this.add(panelButtons, BorderLayout.SOUTH);
		summaryScrollPane.getViewport().add(finishSummary, null);
	}
}