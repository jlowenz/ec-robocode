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
 *******************************************************************************/
package robocode.peer;


/**
 * @author Mathew A. Nelson (original)
 */
public class TeamStatistics implements ContestantStatistics {

	private TeamPeer teamPeer;

	public TeamStatistics(TeamPeer teamPeer) {
		this.teamPeer = teamPeer;
	}

	/*
	 * @see ContestantStatistics#getTotalBulletDamageDealt()
	 */
	public double getTotalBulletDamageDealt() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalBulletDamageDealt();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalBulletDamageReceived()
	 */
	public double getTotalBulletDamageReceived() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalBulletDamageReceived();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalBulletDamageScore()
	 */
	public double getTotalBulletDamageScore() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalBulletDamageScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalFirsts()
	 */
	public int getTotalFirsts() {
		int d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalFirsts();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalKilledEnemyBulletScore()
	 */
	public double getTotalKilledEnemyBulletScore() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalKilledEnemyBulletScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalKilledEnemyRammingScore()
	 */
	public double getTotalKilledEnemyRammingScore() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalKilledEnemyRammingScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalRammingDamageDealt()
	 */
	public double getTotalRammingDamageDealt() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalRammingDamageDealt();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalRammingDamageReceived()
	 */
	public double getTotalRammingDamageReceived() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalRammingDamageReceived();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalRammingDamageScore()
	 */
	public double getTotalRammingDamageScore() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalRammingDamageScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalScore()
	 */
	public double getTotalScore() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalSeconds()
	 */
	public int getTotalSeconds() {
		int d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalSeconds();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalSurvivalScore()
	 */
	public double getTotalSurvivalScore() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalSurvivalScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalThirds()
	 */
	public int getTotalThirds() {
		int d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalThirds();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalWinnerScore()
	 */
	public double getTotalWinnerScore() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getTotalWinnerScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getCurrentScore()
	 */
	public double getCurrentScore() {
		double d = 0;

		for (int i = 0; i < teamPeer.size(); i++) {
			d += teamPeer.elementAt(i).getRobotStatistics().getCurrentScore();
		}
		return d;
	}

    public void scoreScanEvent() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void scoreDistanceTravelled(double distance) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getDistanceTravelled() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getScanEvents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void scoreWallHit() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumWallsHit() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
