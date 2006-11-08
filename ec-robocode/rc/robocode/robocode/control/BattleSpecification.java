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
package robocode.control;


import robocode.battle.*;


/**
 * Defines a battle
 * 
 * @author Mathew A. Nelson (original)
 */
public class BattleSpecification {
	private BattlefieldSpecification battlefield;
	private RobotSpecification robots[];
	private BattleProperties battleProperties;
	
	/**
	 * Creates a BattleSpecification with the given number of rounds, battlefield, and robots
	 * inactivityTime defaults to 450.  gunCoolingRate defaults to .1
	 * @param numRounds Number of rounds in this battle
	 * @param battlefieldSpecification The battlefield
	 * @param robotSpecifications Robots in this battle
	 */
	public BattleSpecification(int numRounds, BattlefieldSpecification battlefield, RobotSpecification robotSpecifications[]) {
		this(numRounds, 450, .1, battlefield, robotSpecifications);
	}
  
	/**
	 * Creates a BattleSpecification with the given number of rounds, inactivityTime, gunCoolingRate, battlefield, and robots
	 * @param numRounds Number of rounds in this battle
	 * @param inactivityTime Number of ticks in which 10 energy must be lost
	 * @param gunCoolingRate Gun cooling rate
	 * @param battlefieldSpecification The battlefield
	 * @param robotSpecifications Robots in this battle
	 */
	public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, BattlefieldSpecification battlefield, RobotSpecification robots[]) {
		battleProperties = new BattleProperties();
		battleProperties.setNumRounds(numRounds);
		battleProperties.setInactivityTime(inactivityTime);
		battleProperties.setGunCoolingRate(gunCoolingRate);
		battleProperties.setBattlefieldWidth(battlefield.getWidth());
		battleProperties.setBattlefieldHeight(battlefield.getHeight());
		String robotString = "";

		for (int i = 0; i < robots.length; i++) {
			if (robots[i] == null) {
				break;
			}
			if (robots[i].getVersion() != null && !robots[i].getVersion().equals("")) {
				robotString += robots[i].getClassName() + " " + robots[i].getVersion();
			} else {
				robotString += robots[i].getClassName();
			}
			if (i < robots.length - 1) {
				robotString += ",";
			}
		}
		battleProperties.setSelectedRobots(robotString);

		this.robots = robots;	
	}

	/**
	 * Gets the inactivityTime for this battle
	 * 
	 * @return the inactivityTime for this battle
	 */
	public long getInactivityTime() {
		return battleProperties.getInactivityTime();
	}
	
	/**
	 * Gets the gunCoolingRate for this battle
	 * 
	 * @return the gunCoolingRate for this battle
	 */
	public double getGunCoolingRate() {
		return battleProperties.getGunCoolingRate();
	}
	
	/**
	 * Gets the battleField for this battle
	 * 
	 * @return the battleField for this battle
	 */
	public BattlefieldSpecification getBattlefield() {
		return battlefield;
	}
	
	/**
	 * Gets the number of rounds in this battle
	 * 
	 * @return the number of rounds in this battle
	 */
	public int getNumRounds() {
		return battleProperties.getNumRounds();
	}
	
	/**
	 * Gets the robots participating in this battle
	 * 
	 * @return the robots participating in this battle
	 */
	public RobotSpecification[] getRobots() {
		return robots;
	}

	/**
	 * Used by Robocode, you do not need to use this.
	 * This method may not exist in a future version of Robocode.
	 *
	 * @deprecated
	 */
	public BattleProperties getBattleProperties() {
		return battleProperties;
	}
}
