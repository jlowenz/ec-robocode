/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Matthew Reeder
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Minor optimizations
 *******************************************************************************/
package robocode.editor;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.regex.*;


/**
 * @author Matthew Reeder (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class FindReplaceDialog extends JDialog implements ActionListener {
	private JTextField findField;
	private JTextField replaceField;
	private JButton findNextButton;
	private JButton replaceButton;
	private JButton replaceAllButton;
	private JButton toggleReplaceButton;
	private JButton closeButton;
	private JCheckBox caseSensitiveCheckBox;
	private JCheckBox wholeWordCheckBox;
	private JRadioButton regexButton;
	private JRadioButton wildCardsButton;
	private JRadioButton literalButton;
	private JLabel findLabel;
	private JLabel replaceLabel;
	private boolean initLoc;
	private RobocodeEditor window;

	public FindReplaceDialog(RobocodeEditor parentFrame) {
		super(parentFrame, false);
		window = parentFrame;
		JPanel bigPanel = new JPanel();

		bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.X_AXIS));
		JPanel leftPanel = new JPanel();

		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		JPanel findReplacePanel = new JPanel();

		findReplacePanel.setLayout(new BoxLayout(findReplacePanel, BoxLayout.X_AXIS));
		JPanel labelsPanel = new JPanel();

		labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
		labelsPanel.add(getFindLabel());
		labelsPanel.add(getReplaceLabel());
		findReplacePanel.add(labelsPanel);
		JPanel fieldsPanel = new JPanel();

		fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
		fieldsPanel.add(getFindField());
		fieldsPanel.add(getReplaceField());
		findReplacePanel.add(fieldsPanel);
		leftPanel.add(findReplacePanel);
		JPanel optionsPanel = new JPanel();

		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
		JPanel checkboxPanel = new JPanel();

		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
		checkboxPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Options"));
		checkboxPanel.add(getCaseSensitiveCheckBox());
		checkboxPanel.add(getWholeWordCheckBox());
		checkboxPanel.setAlignmentY(TOP_ALIGNMENT);
		optionsPanel.add(checkboxPanel);
		JPanel radioPanel = new JPanel();

		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		radioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Use:"));
		radioPanel.add(getLiteralButton());
		radioPanel.add(getWildCardsButton());
		radioPanel.add(getRegexButton());
		radioPanel.setAlignmentY(TOP_ALIGNMENT);
		ButtonGroup buttonGroup = new ButtonGroup();

		buttonGroup.add(getLiteralButton());
		buttonGroup.add(getWildCardsButton());
		buttonGroup.add(getRegexButton());
		optionsPanel.add(radioPanel);
		leftPanel.add(optionsPanel);
		leftPanel.setAlignmentY(TOP_ALIGNMENT);
		bigPanel.add(leftPanel);
		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(getFindNextButton());
		buttonPanel.add(getToggleReplaceButton());
		buttonPanel.add(getReplaceButton());
		buttonPanel.add(getReplaceAllButton());
		buttonPanel.add(getCloseButton());
		buttonPanel.setAlignmentY(TOP_ALIGNMENT);
		bigPanel.add(buttonPanel);
		setContentPane(bigPanel);
	}

	public void showDialog(boolean showReplace) {
		getReplaceLabel().setVisible(showReplace);
		getReplaceField().setVisible(showReplace);
		getReplaceButton().setVisible(showReplace);
		getReplaceAllButton().setVisible(showReplace);
		if (showReplace) {
			setTitle("Replace");
			getRootPane().setDefaultButton(getReplaceButton());
			getToggleReplaceButton().setText("Find...");
			getToggleReplaceButton().setMnemonic('d');
			getToggleReplaceButton().setDisplayedMnemonicIndex(3);
		} else {
			setTitle("Find");
			getRootPane().setDefaultButton(getFindNextButton());
			getToggleReplaceButton().setText("Replace...");
			getToggleReplaceButton().setMnemonic('R');
			getToggleReplaceButton().setDisplayedMnemonicIndex(0);
		}
		pack();
		if (!initLoc) {
			Rectangle bounds = window.getBounds();
			Dimension size = getSize();

			setLocation((int) (bounds.getX() + (bounds.getWidth() - size.getWidth()) / 2),
					(int) (bounds.getY() + (bounds.getHeight() - size.getHeight()) / 2));
			initLoc = true;
		}
		setVisible(true);
	}

	public JLabel getFindLabel() {
		if (findLabel == null) {
			findLabel = new JLabel();
			findLabel.setText(" Find:");
			findLabel.setDisplayedMnemonicIndex(3);
		}
		return findLabel;
	}

	public JLabel getReplaceLabel() {
		if (replaceLabel == null) {
			replaceLabel = new JLabel();
			replaceLabel.setText(" Replace:");
			replaceLabel.setDisplayedMnemonicIndex(3);
		}
		return replaceLabel;
	}

	public JTextField getFindField() {
		if (findField == null) {
			findField = new JTextField();
			findField.setFocusAccelerator('n');
			findField.addActionListener(this);
		}
		return findField;
	}

	public JTextField getReplaceField() {
		if (replaceField == null) {
			replaceField = new JTextField();
			replaceField.setFocusAccelerator('p');
			replaceField.addActionListener(this);
		}
		return replaceField;
	}

	public JButton getFindNextButton() {
		if (findNextButton == null) {
			findNextButton = new JButton();
			findNextButton.setText("Find Next");
			findNextButton.setMnemonic('F');
			findNextButton.setDisplayedMnemonicIndex(0);
			findNextButton.setDefaultCapable(true);
			findNextButton.addActionListener(this);
		}
		return findNextButton;
	}

	public JButton getReplaceButton() {
		if (replaceButton == null) {
			replaceButton = new JButton();
			replaceButton.setText("Replace");
			replaceButton.setMnemonic('R');
			replaceButton.setDisplayedMnemonicIndex(0);
			replaceButton.setDefaultCapable(true);
			replaceButton.addActionListener(this);
		}
		return replaceButton;
	}

	public JButton getReplaceAllButton() {
		if (replaceAllButton == null) {
			replaceAllButton = new JButton();
			replaceAllButton.setText("Replace All");
			replaceAllButton.setMnemonic('A');
			replaceAllButton.setDisplayedMnemonicIndex(8);
			replaceAllButton.addActionListener(this);
		}
		return replaceAllButton;
	}

	public JButton getToggleReplaceButton() {
		if (toggleReplaceButton == null) {
			toggleReplaceButton = new JButton();
			toggleReplaceButton.addActionListener(this);
		}
		return toggleReplaceButton;
	}

	public JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("Close");
			closeButton.setMnemonic('e');
			closeButton.setDisplayedMnemonicIndex(4);
			closeButton.addActionListener(this);
		}
		return closeButton;
	}

	public JCheckBox getCaseSensitiveCheckBox() {
		if (caseSensitiveCheckBox == null) {
			caseSensitiveCheckBox = new JCheckBox();
			caseSensitiveCheckBox.setText("Case Sensitive");
			caseSensitiveCheckBox.setMnemonic('C');
			caseSensitiveCheckBox.setDisplayedMnemonicIndex(0);
			caseSensitiveCheckBox.addActionListener(this);
		}
		return caseSensitiveCheckBox;
	}

	public JCheckBox getWholeWordCheckBox() {
		if (wholeWordCheckBox == null) {
			wholeWordCheckBox = new JCheckBox();
			wholeWordCheckBox.setText("Whole Word");
			wholeWordCheckBox.setMnemonic('W');
			wholeWordCheckBox.setDisplayedMnemonicIndex(0);
			wholeWordCheckBox.addActionListener(this);
		}
		return wholeWordCheckBox;
	}

	public JRadioButton getLiteralButton() {
		if (literalButton == null) {
			literalButton = new JRadioButton();
			literalButton.setText("Literal");
			literalButton.setMnemonic('L');
			literalButton.setSelected(true);
			literalButton.setDisplayedMnemonicIndex(0);
			literalButton.addActionListener(this);
		}
		return literalButton;
	}

	public JRadioButton getWildCardsButton() {
		if (wildCardsButton == null) {
			wildCardsButton = new JRadioButton();
			wildCardsButton.setText("Wild Cards");
			wildCardsButton.setMnemonic('i');
			wildCardsButton.setDisplayedMnemonicIndex(1);
			wildCardsButton.addActionListener(this);
		}
		return wildCardsButton;
	}

	public JRadioButton getRegexButton() {
		if (regexButton == null) {
			regexButton = new JRadioButton();
			regexButton.setText("Regular Expressions");
			regexButton.setMnemonic('x');
			regexButton.setDisplayedMnemonicIndex(9);
			regexButton.addActionListener(this);
		}
		return regexButton;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == getFindNextButton()) {
			findNext();
		} else if (source == getReplaceButton()) {
			doReplacement();
			findNext();
		} else if (source == getReplaceAllButton()) {
			doReplaceAll();
		} else if (source == getCloseButton()) {
			setVisible(false);
		} else if (source == getToggleReplaceButton()) {
			showDialog(!getReplaceButton().isVisible());
		} else if (source instanceof JTextField) {
			getRootPane().getDefaultButton().doClick();
		}
	}

	private Pattern getCurrentPattern() {
		String pattern = getFindField().getText();
		int flags = Pattern.DOTALL;

		if (!getRegexButton().isSelected()) {
			String newpattern = "";

			// 'quote' the pattern
			for (int i = 0; i < pattern.length(); i++) {
				if ("\\[]^$&|().*+?{}".indexOf(pattern.charAt(i)) >= 0) {
					newpattern += '\\';
				}
				newpattern += pattern.charAt(i);
			}
			// make "*" .* and "?" .
			if (getWildCardsButton().isSelected()) {
				newpattern = newpattern.replaceAll("\\\\\\*", ".+?");
				newpattern = newpattern.replaceAll("\\\\\\?", ".");
			}
			pattern = newpattern;
		}
		if (!getCaseSensitiveCheckBox().isSelected()) {
			flags |= Pattern.CASE_INSENSITIVE;
		}
		if (getWholeWordCheckBox().isSelected()) {
			pattern = "\\b" + pattern + "\\b";
		}
		return Pattern.compile(pattern, flags);
	}

	public void findNext() {
		EditWindow currentWindow = window.getActiveWindow();

		if (currentWindow == null || getFindField().getText().length() == 0) {
			// launch error dialog?
			return;
		}
		Pattern p = getCurrentPattern();
		JEditorPane editorPane = currentWindow.getEditorPane();

		// for some reason, getText() trims off \r but the indexes in
		// the editor pane don't.
		String text = editorPane.getText().replaceAll("\\r", "");
		Matcher m = p.matcher(text);
		int index = editorPane.getSelectionEnd();

		if (!m.find(index)) {
			if (!m.find()) {
				// No match found
				return;
			}
		}
		editorPane.setSelectionStart(m.start());
		editorPane.setSelectionEnd(m.end());
	}

	public void doReplacement() {
		EditWindow currentWindow = window.getActiveWindow();

		if (currentWindow == null || getFindField().getText().length() == 0) {
			// launch error dialog?
			return;
		}
		JEditorPane editorPane = currentWindow.getEditorPane();
		String text = editorPane.getSelectedText();

		if (text == null) {
			// no selection
			return;
		}
		Matcher m = getCurrentPattern().matcher(text);

		if (m.matches()) {
			String replacement = getReplaceField().getText();

			if (getRegexButton().isSelected()) {
				replacement = m.replaceFirst(replacement);
			}
			editorPane.replaceSelection(replacement);
		}
	}

	public void doReplaceAll() {
		EditWindow currentWindow = window.getActiveWindow();

		if (currentWindow == null || getFindField().getText().length() == 0) {
			// launch error dialog?
			return;
		}
		JEditorPane editorPane = currentWindow.getEditorPane();
		String text = editorPane.getText();

		String replacement = getReplaceField().getText(); 

		editorPane.setText(getCurrentPattern().matcher(text).replaceAll(replacement));
	}
}
