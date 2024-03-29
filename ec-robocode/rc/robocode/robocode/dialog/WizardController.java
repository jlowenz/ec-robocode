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
 *     Matthew Reeder
 *     - Added keyboard mnemonics to standard wizard buttons
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class WizardController extends JPanel implements ChangeListener {

	private EventHandler eventHandler = new EventHandler();

	private JButton backButton;
	private JButton nextButton;
	private JButton finishButton;
	private JButton cancelButton;
	private boolean focusOnEnabled;

	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == WizardController.this.getFinishButton()) {
				finishButtonActionPerformed();
			}
			if (e.getSource() == WizardController.this.getCancelButton()) {
				cancelButtonActionPerformed();
			}
			if (e.getSource() == WizardController.this.getNextButton()) {
				nextButtonActionPerformed();
			}
			if (e.getSource() == WizardController.this.getBackButton()) {
				backButtonActionPerformed();
			}
		}
	}

	private void backButtonActionPerformed() {
		wizard.back();
	}

	private void cancelButtonActionPerformed() {
		wizard.getWizardListener().cancelButtonActionPerformed();
	}

	private void finishButtonActionPerformed() {
		wizard.getWizardListener().finishButtonActionPerformed();
	}

	/**
	 * Return the backButton
	 * 
	 * @return JButton
	 */
	private JButton getBackButton() {
		if (backButton == null) {
			backButton = new JButton();
			backButton.setText("Back");
			backButton.setMnemonic('B');
			backButton.setDisplayedMnemonicIndex(0);
			backButton.addActionListener(eventHandler);
		}
		return backButton;
	}

	/**
	 * Return the cancelButton
	 * 
	 * @return JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.setMnemonic('C');
			cancelButton.setDisplayedMnemonicIndex(0);
			cancelButton.addActionListener(eventHandler);
		}
		return cancelButton;
	}

	/**
	 * Return the finishButton
	 * 
	 * @return JButton
	 */
	private JButton getFinishButton() {
		if (finishButton == null) {
			finishButton = new JButton();
			finishButton.setText("Finish");
			finishButton.setMnemonic('F');
			finishButton.setDisplayedMnemonicIndex(0);
			finishButton.addActionListener(eventHandler);
		}
		return finishButton;
	}

	public void setFinishButtonText(String text) {
		getFinishButton().setText(text);
	}

	public void setFinishButtonTextAndMnemonic(String text, char mnemonic, int mnemonicIndex) {
		getFinishButton().setText(text);
		getFinishButton().setMnemonic(mnemonic);
		getFinishButton().setDisplayedMnemonicIndex(mnemonicIndex);
	}

	/**
	 * Return the nextButton
	 * 
	 * @return JButton
	 */
	private JButton getNextButton() {
		if (nextButton == null) {
			nextButton = new JButton();
			nextButton.setText("Next");
			nextButton.setMnemonic('N');
			nextButton.setDisplayedMnemonicIndex(0);
			nextButton.addActionListener(eventHandler);
		}
		return nextButton;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		add(getBackButton());
		add(getNextButton());
		add(new JLabel("     "));
		add(getFinishButton());
		add(getCancelButton());
	}

	private void nextButtonActionPerformed() {
		wizard.next();
	}

	private void setBackButtonEnabled(boolean enabled) {
		getBackButton().setEnabled(enabled);
	}

	public void setFinishButtonEnabled(boolean enabled) {
		getFinishButton().setEnabled(enabled);
		if (focusOnEnabled && enabled) {
			getFinishButton().requestFocus();
		}
	}

	private void setNextButtonEnabled(boolean enabled) {
		getNextButton().setEnabled(enabled);
	}

	private Wizard wizard = null;

	protected WizardController(Wizard wizard) {
		super();
		this.wizard = wizard;
		initialize();
		stateChanged(null);
	}

	public void stateChanged(ChangeEvent e) {
		setBackButtonEnabled(wizard.isBackAvailable());
		setNextButtonEnabled(wizard.isNextAvailable());
		setFinishButtonEnabled(wizard.isReady());
	}

	/**
	 * Gets the focusOnEnabled.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getFocusOnEnabled() {
		return focusOnEnabled;
	}

	/**
	 * Sets the focusOnEnabled.
	 * 
	 * @param focusOnEnabled The focusOnEnabled to set
	 */
	public void setFocusOnEnabled(boolean focusOnEnabled) {
		this.focusOnEnabled = focusOnEnabled;
	}
}
