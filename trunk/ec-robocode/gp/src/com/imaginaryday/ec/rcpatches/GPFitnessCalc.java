package com.imaginaryday.ec.rcpatches;

import robocode.control.RobotResults;

/**
 * @author rbowers
 *         Date: Nov 8, 2006
 *         Time: 9:35:54 PM
 */
public class GPFitnessCalc {
    private static final double ACCURACY_SCALE = 5.0;
    private static final double EFFICIENCY_SCALE = 4.0;
    private static double minBattleLength = Double.MAX_VALUE;
    private static final double SHORT_BATTLE_SCALE = 1.1;

    public static double getFitness(int numGenerations, RobotResults robot, RobotResults opponent) {

        // Add a bonus for accuracy
        double accuracy;
        if (robot.getNumBulletsFired() > 0) {
            accuracy = (double) robot.getNumBulletHits() / (double) robot.getNumBulletsFired();
        } else {
            accuracy = 0;
        }
        double accuracyBonus = ACCURACY_SCALE * accuracy * robot.getBulletDamage();

        /*
         * Add a bonus for scan efficiency.
         * Efficiency is getting one scan even per battle "tick"
         */
        double scanEfficiency;
        if (robot.getNumScanEvents() > 0 && robot.getBattleLength() > 0) {
            double revs = robot.getBattleLength();
            scanEfficiency = (double) robot.getNumScanEvents() / revs;
        } else {
            scanEfficiency = 0.0;
        }
        double scanEfficiencyBonus = EFFICIENCY_SCALE * scanEfficiency * robot.getBulletDamage();

        minBattleLength = Math.min(minBattleLength, robot.getBattleLength());
        double shortBattle;
        if (robot.getBattleLength() > 0) {
            shortBattle = minBattleLength / robot.getBattleLength();
        } else {
            shortBattle = 0.0;
        }
        double shortBattleBonus = SHORT_BATTLE_SCALE * shortBattle * robot.getBulletDamage();

        double damageFitness = robot.getBulletDamage() + robot.getBulletDamageBonus() + robot.getRamDamage()
                + robot.getRamDamageBonus() + accuracyBonus + scanEfficiencyBonus + shortBattleBonus;

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

    public static double getMinBattleLength() {
        return minBattleLength;
    }
}
