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
 *     - Added setPaintEnabled() and setSGPaintEnabled() in constructor
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import robocode.peer.*;
import robocode.manager.*;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class RobotButton extends JButton implements ActionListener {
	private RobotPeer robotPeer;
	private RobotDialog robotDialog;
	private RobotDialogManager robotDialogManager;

	/**
	 * RobotButton constructor
	 */
	public RobotButton(RobotDialogManager robotDialogManager, RobotPeer robotPeer) {
		this.robotPeer = robotPeer;
		this.robotDialogManager = robotDialogManager;
		initialize();
		robotDialog = robotDialogManager.getRobotDialog(robotPeer.getName(), false);
		if (robotDialog != null) {
			robotDialog.setRobotPeer(robotPeer);
			robotPeer.setPaintEnabled(robotDialog.isPaintEnabled());
			robotPeer.setSGPaintEnabled(robotDialog.isSGPaintEnabled());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (robotDialog == null) {
			robotDialog = robotDialogManager.getRobotDialog(robotPeer.getName(), true);
			robotDialog.setTitle(robotPeer.getName());
			robotDialog.setRobotPeer(robotPeer);
			if (robotDialog.isVisible() && robotDialog.getState() == JFrame.NORMAL) {
				;
			} else {
				Utils.packPlaceShow(robotDialog);
			}
		} else {
			robotDialog.setVisible(true);
		}
	}

	// TODO: FNL: Remove?
	public void cleanup() {}

	public RobotDialog getRobotDialog() {
		return robotDialog;
	}

	public RobotPeer getRobotPeer() {
		return robotPeer;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setText(robotPeer.getShortName());
		setToolTipText(robotPeer.getRobotClassManager().getClassNameManager().getUniqueFullClassNameWithVersion());
		addActionListener(this);
		setPreferredSize(new Dimension(110, 25));
		setMinimumSize(new Dimension(110, 25));
		setMaximumSize(new Dimension(110, 25));
		setHorizontalAlignment(SwingConstants.LEFT);
		setMargin(new Insets(0, 0, 0, 0));
	}
}
