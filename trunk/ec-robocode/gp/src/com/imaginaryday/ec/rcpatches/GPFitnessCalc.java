package com.imaginaryday.ec.rcpatches;

import robocode.control.RobotResults;

/**
 * @author rbowers
 *         Date: Nov 8, 2006
 *         Time: 9:35:54 PM
 */
public class GPFitnessCalc {

    private static double numBulletsFiredFactor = 1.0;
    private static double numBulletsDodgedFactor = 1.0;

    public static double getFitness(RobotResults robot, RobotResults opponent) {
        double numBulletsDodged = opponent.getNumBulletsFired() - robot.getNumBulletHits();
        return (numBulletsDodgedFactor * numBulletsDodged) +
               (numBulletsFiredFactor * robot.getNumBulletsFired());
    }
}
