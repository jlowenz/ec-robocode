package com.imaginaryday.ec.main;

import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.WinEvent;
import org.jscience.mathematics.vectors.VectorFloat64;

import java.util.Map;
import java.util.HashMap;

import info.javelot.functionalj.tuple.Pair;

/**
 * User: jlowens
 * Date: Oct 30, 2006
 * Time: 6:13:08 PM
 */
public class GPAgent extends AdvancedRobot {

	private RoboNode radarTree;
	private RoboNode turretTree;
	private RoboNode firingTree;
	private RoboNode directionTree;

    private VectorFloat64 movementVector = VectorFloat64.valueOf(0,0);
	private double turretDirection = 0.0;
	private double radarDirection = 0.0;
	private Pair<Boolean,Double> firing = new Pair<Boolean, Double>(false, 0.0);

	private boolean alive = true;
	private VectorFloat64 vectorToEnemy = VectorFloat64.valueOf(0,0);
	private double enemyHeading = 0.0;
	private double enemySpeed = 0.0;

	private boolean recentlyRammed = false;
	private double rammerHeading = 0.0;
	private boolean ramFault = false;

	private boolean recentlyHitByBullet = false;
	private double bulletHeading = 0.0;
	private double bulletEnergy = 0.0;
	private double bulletSpeed = 0.0;

	private boolean recentlyHitWall = false;

	private VectorFloat64 vectorToForwardWall = VectorFloat64.valueOf(0,0);
	private VectorFloat64 vectorToNearestWall = VectorFloat64.valueOf(0,0);

	public VectorFloat64 getCurrentVector() {
	    return movementVector;
	}

    public void run() {
	    setAdjustGunForRobotTurn(true);
	    setAdjustRadarForGunTurn(true);
	    setAdjustRadarForRobotTurn(true);
	    while (alive) {
		    // gather instantaneous sensor data
		    double bfheight = getBattleFieldHeight();
		    double bfwidth = getBattleFieldWidth();
		    double x = getX();
		    double y = getY();
		    double rwidth = getWidth();
		    double rheight = getHeight();
		    double heading = getHeading();
		    double speed = getVelocity();

		    


		    // get absolute radar heading
		    radarDirection = (Double)radarTree.evaluate();

		    // get absolute turret heading
		    turretDirection = (Double)turretTree.evaluate();

		    // determine if we need to fire
		    firing = (Pair<Boolean,Double>)firingTree.evaluate();

		    // get absolute robot heading and velocity
		    movementVector = (VectorFloat64)directionTree.evaluate();

		    // process radar directive

		    // process turret directive

		    // process firing directive

		    // process heading/speed directive
	    }
    }

	public void onRobotDeath(RobotDeathEvent event) {
		super.onRobotDeath(event);
		alive = false;
	}

	public void onWin(WinEvent event) {
		super.onWin(event);
		alive = false;
	}
}
