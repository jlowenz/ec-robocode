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


/**
 * Contains results for a robot when a battle completes.
 * This class is returned from {@link RobocodeListener#battleComplete(robocode.control.BattleSpecification, robocode.control.RobotResults[])}
 *
 * @author Mathew A. Nelson (original)
 */
public class RobotResults {
	private RobotSpecification robot;
    private String name;
    private int rank;
	private int score;
	private int survival;
	private int lastSurvivorBonus;
	private int bulletDamage;
	private int bulletDamageBonus;
	private int ramDamage;
	private int ramDamageBonus;
	private int firsts;
	private int seconds;
	private int thirds;
    private int numBulletsFired;
    private int numBulletHits;

    public RobotResults(
			RobotSpecification robot,
            String name,
            int rank,
			int score,
			int survival,
			int lastSurvivorBonus,
			int bulletDamage,
			int bulletDamageBonus,
			int ramDamage,
			int ramDamageBonus,
			int firsts,
			int seconds,
			int thirds,
            int numBulletsFired,
            int numBulletHits
            ) {
        this.name = name;
        this.robot = robot;
		this.rank = rank;
		this.score = score;
		this.survival = survival;
		this.lastSurvivorBonus = lastSurvivorBonus;
		this.bulletDamage = bulletDamage;
		this.bulletDamageBonus = bulletDamageBonus;
		this.ramDamage = ramDamage;
		this.ramDamageBonus = ramDamageBonus;
		this.firsts = firsts;
		this.seconds = seconds;
		this.thirds = thirds;
        this.numBulletsFired = numBulletsFired;
        this.numBulletHits = numBulletHits;
    }


    public int getNumBulletsFired() {
        return numBulletsFired;
    }
    public int getNumBulletHits() {
        return numBulletHits;
    }

    /**
	 * Gets the robot these results are for.
	 * 
	 * @return the robot these results are for.
	 */
	public RobotSpecification getRobot() {
		return robot;
	}

    public String getName() {
            return name;
        }    

    /**
	 * Gets the rank of this robot in the results.
	 * 
	 * @return the rank of this robot in the results.
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Gets the total score of this robot.
	 * 
	 * @return the total score of this robot.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Gets the survival score of this robot.
	 * 
	 * @return the survival score of this robot.
	 */
	public int getSurvival() {
		return survival;
	}

	/**
	 * Gets the last survivor bonus of this robot.
	 * 
	 * @return the last survivor bonus of this robot.
	 */
	public int getLastSurvivorBonus() {
		return lastSurvivorBonus;
	}

	/**
	 * Gets the bullet damage score of this robot.
	 * 
	 * @return the bullet damage score of this robot.
	 */
	public int getBulletDamage() {
		return bulletDamage;
	}

	/**
	 * Gets the bullet damage bonus of this robot.
	 * 
	 * @return the bullet damage bonus of this robot.
	 */
	public int getBulletDamageBonus() {
		return bulletDamageBonus;
	}

	/**
	 * Gets the ram damage score of this robot.
	 * 
	 * @return the ram damage score of this robot.
	 */
	public int getRamDamage() {
		return ramDamage;
	}

	/**
	 * Gets the ram damage bonus of this robot.
	 * 
	 * @return the ram damage bonus of this robot.
	 */
	public int getRamDamageBonus() {
		return ramDamageBonus;
	}

	/**
	 * Gets the number of times this robot placed first.
	 * 
	 * @return the number of times this robot placed first.
	 */
	public int getFirsts() {
		return firsts;
	}

	/**
	 * Gets the number of times this robot placed second.
	 * 
	 * @return the number of times this robot placed second.
	 */
	public int getSeconds() {
		return seconds;
	}

	/**
	 * Gets the number of times this robot placed third.
	 * 
	 * @return the number of times this robot placed third.
	 */
	public int getThirds() {
		return thirds;
	}
}
