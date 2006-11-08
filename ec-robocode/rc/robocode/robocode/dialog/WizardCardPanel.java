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
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class WizardCardPanel extends JPanel implements Wizard {
	private WizardController wizardController;
	private CardLayout cardLayout = null;
	public int currentIndex = 0;
	private WizardListener listener;
	EventHandler eventHandler = new EventHandler();

	public class EventHandler implements ContainerListener {
		public void componentRemoved(ContainerEvent e) {}

		public void componentAdded(ContainerEvent e) {
			if (e.getChild() instanceof WizardPanel) {
				setWizardControllerOnPanel((WizardPanel) e.getChild());
				getWizardController().stateChanged(new ChangeEvent(e.getChild()));
			}
		}
	}

	/**
	 * WizardCardLayout constructor
	 * 
	 * @param listener WizardListener
	 */
	public WizardCardPanel(WizardListener listener) {
		this.listener = listener;
		initialize();
	}

	public void back() {
		currentIndex--;
		getWizardController().stateChanged(null);
		getCardLayout().previous(this);
	}

	public java.awt.CardLayout getCardLayout() {
		if (cardLayout == null) {
			cardLayout = new CardLayout();
		}
		return cardLayout;
	}

	public Component getCurrentPanel() {
		return getComponent(currentIndex);
	}

	public WizardController getWizardController() {
		if (wizardController == null) {
			wizardController = new WizardController(this);
		}
		return wizardController;
	}

	public WizardListener getWizardListener() {
		return listener;
	}

	public void initialize() {
		this.setLayout(getCardLayout());
		this.addContainerListener(eventHandler);
	}

	public boolean isBackAvailable() {
		return (currentIndex > 0);
	}

	public boolean isCurrentPanelReady() {
		Component c = getCurrentPanel();

		if (c instanceof WizardPanel) {
			return ((WizardPanel) c).isReady();
		} else {
			return true;
		}
	}

	public boolean isNextAvailable() {
		return ((currentIndex < getComponentCount() - 1) && isCurrentPanelReady());
	}

	public boolean isReady() {
		for (int i = 0; i < getComponentCount(); i++) {
			if (!((WizardPanel) getComponent(i)).isReady()) {
				return false;
			}
		}
		return true;
	}

	public void next() {
		currentIndex++;
		getWizardController().stateChanged(null);
		getCardLayout().next(this);
	}

	public void setWizardControllerOnPanel(WizardPanel panel) {
		panel.setWizardController(getWizardController());
	}
}
