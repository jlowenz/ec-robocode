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
 *     Luis Crespo
 *     - Added getCurrentScore()
 *     Flemming N. Larsen
 *     - Bugfix: scoreDeath() incremented totalFirsts even if the robot was
 *       already a winner, where scoreWinner() has already been called previously
 *******************************************************************************/
package robocode.peer.robot;


import robocode.peer.RobotPeer;
import robocode.peer.TeamPeer;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo, Flemming N. Larsen (current)
 */
public class RobotStatistics implements robocode.peer.ContestantStatistics {
	boolean noScoring;
	private double bulletDamageScore;
	private double rammingDamageScore;
	private double survivalScore;
	private double winnerScore;
	private double totalWinnerScore;
	private double killedEnemyRammingScore;
	private double killedEnemyBulletScore;
	private double totalScore;
	private double totalBulletDamageScore;
	private double totalBulletDamageDealt;
	private double totalBulletDamageReceived;
	private double totalRammingDamageScore;
	private double totalSurvivalScore;
	private double totalKilledEnemyRammingScore;
	private double totalKilledEnemyBulletScore;
	private RobotPeer robotPeer;
	private TeamPeer teamPeer;
	private double rammingDamageDealt;
	private double totalRammingDamageDealt;
	private double bulletDamageDealt;
	private double bulletDamageReceived;
	private double rammingDamageReceived;
	private double totalRammingDamageReceived;
    public double scanRadians;

	private double energy = 0.0;
    private int battleLength = 0;
    private double distanceTravelled;
    private int numScanEvents;
    private int numBulletsFired;
    private int numBulletHits;
    private int numWallsHit;

    private int totalFirsts;
	private int totalSeconds;
	private int totalThirds;

	private double robotDamage[] = null;
    private int movementPenalty = 0;
    private boolean cellsEntered[];

    /**
	 * RobotStatistics constructor comment.
	 */
	public RobotStatistics(RobotPeer robotPeer) {
		super();
		this.robotPeer = robotPeer;
		this.teamPeer = robotPeer.getTeamPeer();
        this.cellsEntered = new boolean[48];
    }

	public void damagedByBullet(double damage) {
		bulletDamageReceived += damage;
	}

	public void damagedByRamming(double damage) {
		rammingDamageReceived += damage;
	}


    public int getMovementPenalty() {
        return movementPenalty;
    }
    public double getEnergy() {
		return energy;
	}

	public int getBattleLength() {
        return battleLength;
    }

    public int getNumBulletsFired() {
        return numBulletsFired;
    }

    public int getNumBulletHits() {
        return numBulletHits;
    }

    public void generateTotals() {
		totalBulletDamageScore += bulletDamageScore;
		totalRammingDamageScore += rammingDamageScore;
		totalSurvivalScore += survivalScore;
		totalKilledEnemyBulletScore += killedEnemyBulletScore;
		totalKilledEnemyRammingScore += killedEnemyRammingScore;
		totalWinnerScore += winnerScore;
		totalBulletDamageDealt += bulletDamageDealt;
		totalBulletDamageReceived += bulletDamageReceived;
		totalRammingDamageDealt += rammingDamageDealt;
		totalRammingDamageReceived += rammingDamageReceived;
		totalScore = totalBulletDamageScore + totalRammingDamageScore + totalSurvivalScore
				+ totalKilledEnemyRammingScore + totalKilledEnemyBulletScore + totalWinnerScore;
	}

	private double[] getRobotDamage() {
		if (robotDamage == null) {
			robotDamage = new double[robotPeer.getBattle().getRobots().size()];
			for (int i = 0; i < robotPeer.getBattle().getRobots().size(); i++) {
				robotDamage[i] = 0.0;
			}
		}
		return robotDamage;
	}

    public void scoreDistanceTravelled(double distance) {
        distanceTravelled += distance;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    public void scoreScanEvent() {
        ++numScanEvents;
    }

    public int getScanEvents() {
        return numScanEvents;
    }

    public void scoreWallHit() {
        ++numWallsHit;
    }

    public int getNumWallsHit() {
        return numWallsHit;
    }

    public double getScanRadians() {
        return scanRadians;
    }

    public double scoreScanRadians(double radians) {
        return scanRadians += radians;
    }

    public double getTotalBulletDamageDealt() {
        return totalBulletDamageDealt;
    }

	public double getTotalBulletDamageReceived() {
		return totalBulletDamageReceived;
	}

	public double getTotalBulletDamageScore() {
		return totalBulletDamageScore;
	}

	public int getTotalFirsts() {
		return totalFirsts;
	}

	public double getTotalKilledEnemyBulletScore() {
		return totalKilledEnemyBulletScore;
	}

	public double getTotalKilledEnemyRammingScore() {
		return totalKilledEnemyRammingScore;
	}

	public double getTotalRammingDamageDealt() {
		return totalRammingDamageDealt;
	}

	public double getTotalRammingDamageReceived() {
		return totalRammingDamageReceived;
	}

	public double getTotalRammingDamageScore() {
		return totalRammingDamageScore;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	public double getTotalSurvivalScore() {
		return totalSurvivalScore;
	}

	public int getTotalThirds() {
		return totalThirds;
	}

	public double getTotalWinnerScore() {
		return totalWinnerScore;
	}

	public void initializeRound() {
		bulletDamageScore = 0;
		rammingDamageScore = 0;
		killedEnemyRammingScore = 0;
		killedEnemyBulletScore = 0;
		survivalScore = 0;
		winnerScore = 0;
		bulletDamageDealt = 0;
		bulletDamageReceived = 0;
		rammingDamageDealt = 0;
		rammingDamageReceived = 0;

		noScoring = false;

		robotDamage = null;
	}

	private boolean isTeammate(int robot) {
		return (teamPeer != null
				&& teamPeer == ((RobotPeer) robotPeer.getBattle().getRobots().elementAt(robot)).getTeamPeer());
	}

	public void scoreBulletDamage(int robot, double damage) {
		if (isTeammate(robot)) {
			return;
		}
		if (!noScoring) {
			getRobotDamage()[robot] += damage;
			bulletDamageScore += damage;
		}
		bulletDamageDealt += damage;
        numBulletHits++;
    }

	public void scoreDeath(int enemiesRemaining) {
		if (enemiesRemaining == 0 && !robotPeer.isWinner()) {
			totalFirsts++;
		}
		if (enemiesRemaining == 1) {
			totalSeconds++;
		} else if (enemiesRemaining == 2) {
			totalThirds++;
		}
	}

	public void scoreKilledEnemyBullet(int robot) {
		if (isTeammate(robot)) {
			return;
		}

		if (!noScoring) {
			double bonus = 0;

			if (teamPeer == null) {
				bonus = getRobotDamage()[robot] * .2;
			} else {
				for (int i = 0; i < teamPeer.size(); i++) {
					bonus += teamPeer.elementAt(i).getRobotStatistics().getRobotDamage()[robot] * .2;
				}
			}

			robotPeer.out.println(
					"SYSTEM: Bonus for killing " + ((RobotPeer) robotPeer.getBattle().getRobots().elementAt(robot)).getName()
					+ ": " + (int) (bonus + .5));
			killedEnemyBulletScore += bonus;
		}
	}

	public void scoreKilledEnemyRamming(int robot) {
		if (isTeammate(robot)) {
			return;
		}
		if (!noScoring) {
			double bonus = 0;

			if (teamPeer == null) {
				bonus = getRobotDamage()[robot] * .3;
			} else {
				for (int i = 0; i < teamPeer.size(); i++) {
					bonus += teamPeer.elementAt(i).getRobotStatistics().getRobotDamage()[robot] * .3;
				}
			}
			robotPeer.out.println(
					"SYSTEM: Ram bonus for killing "
							+ ((RobotPeer) robotPeer.getBattle().getRobots().elementAt(robot)).getName() + ": " + (int) (bonus + .5));
			killedEnemyRammingScore += bonus;
		}
	}

	public void scoreRammingDamage(int robot, double damage) {
		if (isTeammate(robot)) {
			return;
		}
		if (!noScoring) {
			getRobotDamage()[robot] += damage;
			rammingDamageScore += 2.0 * damage;
		}
		rammingDamageDealt += damage;
	}

	public void scoreSurvival() {
		if (!noScoring) {
			survivalScore += 50;
		}
	}

	public void scoreWinner() {
		if (!noScoring) {
			int enemyCount = robotPeer.getBattle().getRobots().size() - 1;

			if (teamPeer != null) {
				enemyCount -= (teamPeer.size() - 1);
			}
			winnerScore += 10 * enemyCount;
			if (teamPeer != null && !robotPeer.isTeamLeader()) {
				return;
			}
			totalFirsts++;
		}
	}

    public void scoreBulletFired() {
        numBulletsFired++;
    }

    public void scoreFirsts() {
		if (!noScoring) {
			totalFirsts++;
		}
	}

	public void setNoScoring(boolean noScoring) {
		this.noScoring = noScoring;
		if (noScoring) {
			bulletDamageScore = 0;
			rammingDamageScore = 0;
			killedEnemyRammingScore = 0;
			killedEnemyBulletScore = 0;
			survivalScore = 0;
			winnerScore = 0;
			bulletDamageDealt = 0;
			bulletDamageReceived = 0;
			rammingDamageDealt = 0;
			rammingDamageReceived = 0;
		}
	}

	public double getCurrentScore() {
		return bulletDamageScore + rammingDamageScore + survivalScore + killedEnemyRammingScore + killedEnemyBulletScore
				+ winnerScore;
	}

    public void updateTimeStep() {
        battleLength++;
    }

	public void recordEnergy(double energy) {
		this.energy = energy;
	}
    public void scoreMovementPenalty() {
        movementPenalty++;
    }

    public int getCellsEntered() {
        int count = 0;
        for (boolean b : cellsEntered) {
            if (b) ++count;
        }
        return count;
    }

    public void cellEntered(double x, double y) {
        int xi = (int)Math.floor(x);
        int yi = (int)Math.floor(y);
        int index = (xi / 100) * 6 + (yi / 100);
        cellsEntered[index] = true;
    }

    public boolean wasCellEntered(double x, double y) {
        int xi = (int)Math.floor(x);
        int yi = (int)Math.floor(y);
        int index = (xi / 100) * 6 + (yi / 100);
        return cellsEntered[index];
    }
}
