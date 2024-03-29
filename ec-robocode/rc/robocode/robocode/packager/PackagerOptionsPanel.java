/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Replaced deprecated show() method with setVisible(true)
 *     - Code cleanup
 *******************************************************************************/
package robocode.packager;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.URL;
import java.util.Vector;
import robocode.dialog.*;

import robocode.util.*;
import robocode.repository.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class PackagerOptionsPanel extends WizardPanel {
	RobotPackager robotPackager;
	JCheckBox includeSource;
	EventHandler eventHandler = new EventHandler();

	JLabel authorLabel;
	JTextField authorField;
	JLabel descriptionLabel;
	JTextArea descriptionArea;
	JLabel versionLabel;
	JTextField versionField;
	JLabel versionHelpLabel;
	JLabel webpageLabel;
	JTextField webpageField;
	JLabel webpageHelpLabel;

	class EventHandler implements ComponentListener, KeyListener, DocumentListener {
		int count = 0;

		public void insertUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void changedUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void removeUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void componentMoved(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {}

		public void componentShown(ComponentEvent e) {
			Vector<FileSpecification> selectedRobots = robotPackager.getRobotSelectionPanel().getSelectedRobots(); 

			if (selectedRobots != null && (selectedRobots.size() == 1)) {
				FileSpecification fileSpecification = (FileSpecification) selectedRobots.elementAt(0);
				String v = fileSpecification.getVersion();

				if (v == null || v.equals("")) {
					getVersionHelpLabel().setVisible(false);
					v = "1.0";
				} else {
					if (v.length() == 10) {
						v = v.substring(0, 9);
					}
					v += "*";
					getVersionHelpLabel().setVisible(true);
				}
				getVersionField().setText(v);
				String d = fileSpecification.getDescription();

				if (d == null) {
					d = "";
				}
				getDescriptionArea().setText(d);
				String a = fileSpecification.getAuthorName();

				if (a == null) {
					a = "";
				}
				getAuthorField().setText(a);
				URL u = fileSpecification.getWebpage();

				if (u == null) {
					getWebpageField().setText("");
				} else {
					getWebpageField().setText(u.toString());
				}

				String filepath = fileSpecification.getFilePath();

				if (filepath != null && filepath.indexOf(".") > 0) {
					String htmlfn = filepath.substring(0, filepath.lastIndexOf(".")) + ".html";

					getWebpageHelpLabel().setText(
							"(You may also leave this blank, and simply create the file: " + htmlfn + ")");
				} else {
					getWebpageHelpLabel().setText("");
				}
				
				getVersionLabel().setVisible(true);
				getVersionField().setVisible(true);
				getAuthorLabel().setVisible(true);
				getAuthorField().setVisible(true);
				getWebpageLabel().setVisible(true);
				getWebpageField().setVisible(true);
				getWebpageHelpLabel().setVisible(true);
				getDescriptionLabel().setText(
						"Please enter a short description of your robot (up to 3 lines of 72 chars each).");
			} else if (selectedRobots.size() > 1) {
				getVersionLabel().setVisible(false);
				getVersionField().setVisible(false);
				getVersionHelpLabel().setVisible(false);
				getAuthorLabel().setVisible(false);
				getAuthorField().setVisible(false);
				getWebpageLabel().setVisible(false);
				getWebpageField().setVisible(false);
				getWebpageHelpLabel().setVisible(false);
				getDescriptionLabel().setText(
						"Please enter a short description of this robot collection (up to 3 lines of 72 chars each).");
				if (getDescriptionArea().getText() == null || getDescriptionArea().getText().equals("")) {
					getDescriptionArea().setText("(Example)This robot comes from the ... robot collection\n");
				}
			}
		}

		public void componentResized(ComponentEvent e) {}

		public void keyPressed(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {}

		public void keyTyped(KeyEvent e) {}
	}

	public JPanel robotListPanel;

	public PackagerOptionsPanel(RobotPackager robotPackager) {
		super();
		this.robotPackager = robotPackager;
		initialize();
	}

	public JCheckBox getIncludeSource() {
		if (includeSource == null) {
			includeSource = new JCheckBox("Include source", true);
		}
		return includeSource;
	}

	private void initialize() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel label = new JLabel("It is up to you whether or not to include the source when you distribute your robot.");

		label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(label);
	
		label = new JLabel(
				"If you include the source, other people will be able to look at your code and learn from it.");
		label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(label);
	
		getIncludeSource().setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(getIncludeSource());
	
		label = new JLabel(" ");
		label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(label);

		add(getVersionLabel());

		JPanel p = new JPanel();

		p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		p.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		getVersionField().setAlignmentX(JLabel.LEFT_ALIGNMENT);
		getVersionField().setMaximumSize(getVersionField().getPreferredSize());
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, getVersionField().getPreferredSize().height));
		p.add(getVersionField());
		p.add(getVersionHelpLabel());
		add(p);
	
		label = new JLabel(" ");
		label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(label);
	
		add(getDescriptionLabel());
	
		JScrollPane scrollPane = new JScrollPane(getDescriptionArea(), JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPane.setMaximumSize(scrollPane.getPreferredSize());
		scrollPane.setMinimumSize(new Dimension(100, scrollPane.getPreferredSize().height));
		scrollPane.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(scrollPane);

		label = new JLabel(" ");
		label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(label);

		add(getAuthorLabel());

		getAuthorField().setAlignmentX(JTextField.LEFT_ALIGNMENT);
		getAuthorField().setMaximumSize(getAuthorField().getPreferredSize());
		add(getAuthorField());

		label = new JLabel(" ");
		label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(label);

		add(getWebpageLabel());

		getWebpageField().setAlignmentX(JTextField.LEFT_ALIGNMENT);
		getWebpageField().setMaximumSize(getWebpageField().getPreferredSize());
		add(getWebpageField());

		getWebpageHelpLabel().setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(getWebpageHelpLabel());

		JPanel panel = new JPanel();

		panel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(panel);
		addComponentListener(eventHandler);
	}

	public boolean isReady() {
		if (getVersionLabel().isVisible()) {
			if (getVersionField().getText().equals("")) {
				return false;
			}
			if (getVersionField().getText().indexOf(",") >= 0) {
				return false;
			}
			if (getVersionField().getText().indexOf(" ") >= 0) {
				return false;
			}
			if (getVersionField().getText().indexOf("*") >= 0) {
				return false;
			}
			if (getVersionField().getText().indexOf("(") >= 0) {
				return false;
			}
			if (getVersionField().getText().indexOf(")") >= 0) {
				return false;
			}
			if (getVersionField().getText().indexOf("[") >= 0) {
				return false;
			}
			if (getVersionField().getText().indexOf("]") >= 0) {
				return false;
			}
			if (getVersionField().getText().indexOf("{") >= 0) {
				return false;
			}
			if (getVersionField().getText().indexOf("}") >= 0) {
				return false;
			}
		}
		if (getDescriptionArea().getText().equals("")) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("options");

		frame.setSize(new Dimension(500, 300));
		frame.getContentPane().add(new PackagerOptionsPanel(null));
		frame.pack();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private JLabel getAuthorLabel() {
		if (authorLabel == null) {
			authorLabel = new JLabel("Please enter your name. (optional)");
			authorLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		}
		return authorLabel;
	}

	public JTextField getAuthorField() {
		if (authorField == null) {
			authorField = new JTextField(40);
		}
		return authorField;
	}

	public JLabel getDescriptionLabel() {
		if (descriptionLabel == null) {
			descriptionLabel = new JLabel("");
			descriptionLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		}
		return descriptionLabel;
	}

	public JTextArea getDescriptionArea() {
		if (descriptionArea == null) {
			LimitedDocument doc = new LimitedDocument(3, 72);

			descriptionArea = new JTextArea(doc, null, 3, 72);
			doc.addDocumentListener(eventHandler);
		}
		return descriptionArea;
	}

	private JLabel getVersionLabel() {
		if (versionLabel == null) {
			versionLabel = new JLabel(
					"Please enter a version number for this robot (up to 10 chars, no spaces,commas,asterisks, or brackets).");
			versionLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		}
		return versionLabel;
	}

	public JTextField getVersionField() {
		if (versionField == null) {
			LimitedDocument doc = new LimitedDocument(1, 10);

			versionField = new JTextField(doc, null, 10);
			doc.addDocumentListener(eventHandler);
		}
		return versionField;
	}

	public JLabel getVersionHelpLabel() {
		if (versionHelpLabel == null) {
			versionHelpLabel = new JLabel("<-- Make sure to delete the asterisk and type in a new version number");
		}
		return versionHelpLabel;
	}

	public JLabel getWebpageLabel() {
		if (webpageLabel == null) {
			webpageLabel = new JLabel("Please enter a URL for your robot's webpage. (optional)");
			webpageLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		}
		return webpageLabel;
	}

	public JTextField getWebpageField() {
		if (webpageField == null) {
			webpageField = new JTextField(40);
		}
		return webpageField;
	}

	public JLabel getWebpageHelpLabel() {
		if (webpageHelpLabel == null) {
			webpageHelpLabel = new JLabel("");
		}
		return webpageHelpLabel;
	}
}
