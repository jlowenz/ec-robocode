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
package robocode;


/**
 * A prebuilt condition you can use that indicates your radar has finished rotating.
 *
 * @see robocode.Condition
 *
 * @author Mathew A. Nelson
 */
public class RadarTurnCompleteCondition extends Condition {
	private AdvancedRobot robot = null;

	/**
	 * Creates a new RadarTurnCompleteCondition with default priority.
	 */
	public RadarTurnCompleteCondition(AdvancedRobot r) {
		super();
		this.robot = r;
	}

	/**
	 * Creates a new RadarTurnCompleteCondition with the specified priority.
	 */
	public RadarTurnCompleteCondition(AdvancedRobot r, int priority) {
		super();
		this.robot = r;
		this.priority = priority;
	}

	/**
	 * Tests if the radar has stopped turning.
	 */
	public boolean test() {
		return (robot.getRadarTurnRemaining() == 0);
	}
}
