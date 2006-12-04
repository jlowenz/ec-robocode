package com.imaginaryday.ec.rcpatches;

import com.imaginaryday.util.Stuff;
import robocode.control.RobotResults;

/**
 * @author rbowers
 *         Date: Nov 8, 2006
 *         Time: 9:35:54 PM
 */
public class GPFitnessCalc {

    public static double getFitness(int numGenerations, RobotResults robot, RobotResults opponent) {

        // Add a bonus for accuracy
        double accuracy;
        if (robot.getNumBulletsFired() > 0) {
            accuracy = (double) robot.getNumBulletHits() / (double) robot.getNumBulletsFired();
        } else {
            accuracy = 0;
        }
        double accuracyBonus = 5 * accuracy * robot.getBulletDamage();

        /*
         * Add a bonus for scan efficiency.
         * Efficiency is getting more than one scan event per complete rotation.
         */
        double scanEfficiency;
        if (robot.getNumScanEvents() > 0 && robot.getScanRadians() > 0.0) {
	        double revs = robot.getScanRadians() / (2.0 * Math.PI);
	        revs = (Stuff.nearZero(revs)) ? 1.0 : revs;
            scanEfficiency = ((double) robot.getNumScanEvents() - revs) / revs;
            scanEfficiency /= 4.0; // scale by 1/8 so that we need to get 8 events per rotation to get a _good_ bonus
        } else {
            scanEfficiency = 0.0;
        }
        double scanEfficiencyBonus = scanEfficiency * robot.getBulletDamage();

        double damageFitness = robot.getBulletDamage() + robot.getBulletDamageBonus() + robot.getRamDamage()
                + robot.getRamDamageBonus() + accuracyBonus + scanEfficiencyBonus;

        double d = (robot.getDistanceTravelled() < 1) ? 1.0 : robot.getDistanceTravelled();
        double b = (robot.getNumBulletsFired() < 1) ? 1.0 : robot.getNumBulletsFired();
        double s = (robot.getNumScanEvents() < 1) ? 1.0 : robot.getNumScanEvents();

        double tweekFitness = Math.log(d) + Math.log(b) + Math.log(s);

        double winningFraction = (double) robot.getFirsts() / 10.0;
        double doSomethingBonus;
        if ((robot.getDistanceTravelled() > 0 ||
                robot.getNumBulletsFired() > 0 ||
                robot.getNumScanEvents() > 0)) doSomethingBonus = 5.0;
        else doSomethingBonus = 0.0;

        return winningFraction * (damageFitness + tweekFitness) + doSomethingBonus;
    }

}
