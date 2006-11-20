package com.imaginaryday.ec.rcpatches;

import robocode.control.RobotResults;

import java.util.logging.Logger;

import com.imaginaryday.util.Stuff;

/**
 * @author rbowers
 *         Date: Nov 8, 2006
 *         Time: 9:35:54 PM
 */
public class GPFitnessCalc {
    private static double numBulletsFiredFactor = 1.5;
    private static double numBulletsDodgedFactor = 1.0;
    private static double numWallsHitFactor = -2.0;
    private static double distanceTraveledFactor = 100.0;
    private static double numScanEventsFactor = 2.0;
    private static double accuracyFactor = 100.0;

    public static double getFitness(int numGenerations, RobotResults robot, RobotResults opponent) {
        double numBulletsDodged = opponent.getNumBulletsFired() - robot.getNumBulletHits();
        double r1 = Stuff.clampZero(robot.getDistanceTravelled());
        double r2 = Stuff.clampZero(opponent.getDistanceTravelled());
        double distRatio = (r1 > 0.0) ? (r1 / (r1+r2)) : 0;

        double accScaling = Math.pow(10.0, 0.002*numGenerations);
        double accuracy = opponent.getNumBulletHits() / robot.getNumBulletsFired();

        return (numBulletsDodgedFactor * numBulletsDodged) +
               (numBulletsFiredFactor * robot.getNumBulletsFired()) +
               (numWallsHitFactor * robot.getNumWallsHit()) +
               (distanceTraveledFactor * distRatio) +
               (numScanEventsFactor * robot.getNumScanEvents()) +
               (accuracyFactor * accuracy * accScaling);
    }
}
