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


/**
 * @author Mathew A. Nelson (original)
 */
public interface Wizard {
	public abstract void back();

	public abstract Component getCurrentPanel();

	public abstract WizardController getWizardController();

	WizardListener getWizardListener();

	public abstract boolean isBackAvailable();

	public abstract boolean isNextAvailable();

	public abstract boolean isReady();

	public abstract void next();

	void setWizardControllerOnPanel(WizardPanel panel);
}
