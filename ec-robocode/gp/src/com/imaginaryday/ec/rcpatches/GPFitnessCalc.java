package com.imaginaryday.ec.rcpatches;

import robocode.control.RobotResults;

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
    private static double winFactor = 100.0;

    /*
    public static double getFitness(int numGenerations, RobotResults robot, RobotResults opponent) {
        double numBulletsDodged = opponent.getNumBulletsFired() - robot.getNumBulletHits();
        double r1 = Stuff.clampZero(robot.getDistanceTravelled());
        double r2 = Stuff.clampZero(opponent.getDistanceTravelled());
        double distRatio = (r1 > 0.0) ? (r1 / (r1+r2)) : 0;

        double accScaling = Math.pow(10.0, 0.002*numGenerations);

        double accuracy;
        if (robot.getNumBulletsFired() > 0) {
            accuracy = opponent.getNumBulletHits() / robot.getNumBulletsFired();
        } else {
            accuracy = 0;
        }

        return (winFactor * robot.getFirsts()) +
                (numBulletsDodgedFactor * numBulletsDodged) +
                (numBulletsFiredFactor * robot.getNumBulletsFired()) +
                (numWallsHitFactor * robot.getNumWallsHit()) +
                (distanceTraveledFactor * distRatio) +
                (numScanEventsFactor * robot.getNumScanEvents()) +
                (accuracyFactor * accuracy * accScaling);
    }
    */

    public static double getFitness(int numGenerations, RobotResults robot, RobotResults opponent) {

        // Add a bonus for accuracy
        double accuracy;
        if (robot.getNumBulletsFired() > 0) {
            accuracy = opponent.getNumBulletHits() / robot.getNumBulletsFired();
        } else {
            accuracy = 0;
        }
        double accuracyBonus = 3 * accuracy * robot.getBulletDamage();

        /*
         * Add a bonus for scan efficiency.
         * Efficiency is getting more than one scan event per complete rotation.
         */
        double scanEfficiency;
        if (robot.getNumScanEvents() > 0 && robot.getScanRadians() > 0.0) {
            scanEfficiency = (((double)robot.getNumScanEvents() / (robot.getScanRadians() / (2.0 * Math.PI))) - 1.0);
            scanEfficiency /= 8.0; // scale by 1/8 so that we need to get 8 events per rotation to get a _good_ bonus
        } else {
            scanEfficiency = 0.0;
        }
        double scanEfficiencyBonus = scanEfficiency * robot.getBulletDamage();

        double damageFitness = robot.getBulletDamage() + robot.getBulletDamageBonus() + robot.getRamDamage()
                + robot.getRamDamageBonus() + accuracyBonus + scanEfficiencyBonus
                - opponent.getBulletDamage() - opponent.getRamDamage() ;

        double d = (robot.getDistanceTravelled() < 1) ? 1.0 : robot.getDistanceTravelled();
        double b = (robot.getNumBulletsFired() < 1) ? 1.0 : robot.getNumBulletHits();
        double s = (robot.getNumScanEvents() < 1) ? 1.0 : robot.getNumScanEvents();

        double tweekFitness = Math.log(d/10.0) + Math.log(b) + Math.log(s);

        double winningFraction = (double)robot.getFirsts() / 10.0;

        return  winningFraction * (damageFitness + tweekFitness);
    }

}
