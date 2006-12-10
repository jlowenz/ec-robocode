package com.imaginaryday.ec.rcpatches;

import robocode.control.RobotResults;

/**
 * @author rbowers
 *         Date: Nov 8, 2006
 *         Time: 9:35:54 PM
 */
public class GPFitnessCalc {
    private static final double ACCURACY_SCALE = 100;
    private static final double EFFICIENCY_SCALE = 100;
    private static final double SHORT_BATTLE_SCALE = 1.1;
    private static final double SURVIVAL_SCALE = 2.0;
    private static final double BULLET_PENALTY_SCALE = -1.0;
    private static final double MOVEMENT_PENALTY = -0.01;
    private static final double UNRESPONDING_PENALTY_SCALE = 100;
    private static final double CELL_VISITING_SCALE = 100;

    private static double minBattleLength = Double.MAX_VALUE;

    public static double getFitness(int numGenerations, RobotResults robot, RobotResults opponent) {
        minBattleLength = Math.min(minBattleLength, robot.getBattleLength());
        return getNewFitness(numGenerations, robot, opponent);
    }

    private static double getNewFitness(int numGenerations, RobotResults robot, RobotResults opponent) {
        // Add a bonus for accuracy
        double accuracy = 0.0;
        if (robot.getNumBulletsFired() > 0) {
            accuracy = (double) robot.getNumBulletHits() / (double) robot.getNumBulletsFired();
        }
        double accuracyBonus = ACCURACY_SCALE * accuracy;

        /*
             * Add a bonus for scan efficiency.
           * Efficiency is getting one scan even per battle "tick"
           */
        double scanEfficiency = 0.0;
        if (robot.getNumScanEvents() > 0 && robot.getBattleLength() > 0) {
            double revs = robot.getBattleLength();
            scanEfficiency = (double) robot.getNumScanEvents() / revs;
        }
        double scanEfficiencyBonus = EFFICIENCY_SCALE * scanEfficiency;

        double coverageFraction;
        coverageFraction = robot.getCellsEntered() / 48;
        double motionBonus = coverageFraction * CELL_VISITING_SCALE;

        double damageFitness = robot.getBulletDamage() + robot.getBulletDamageBonus() + robot.getRamDamage()
                + robot.getRamDamageBonus() + accuracyBonus + scanEfficiencyBonus + motionBonus;

        double d = (robot.getDistanceTravelled() < 1) ? 1.0 : robot.getDistanceTravelled();
        double b = (robot.getNumBulletsFired() < 1) ? 1.0 : robot.getNumBulletsFired();
        double s = (robot.getNumScanEvents() < 1) ? 1.0 : robot.getNumScanEvents();

        double tweekFitness = Math.log(d) + Math.log(b) + Math.log(s);

        double doSomethingBonus = 0.0;
        if ((robot.getDistanceTravelled() > 0 ||
                robot.getNumBulletsFired() > 0 ||
                robot.getNumScanEvents() > 0)) doSomethingBonus = 5.0;

        double unrespondingPenalty = (robot.getMaxUnrespondedHits() - 1) * UNRESPONDING_PENALTY_SCALE;

        return damageFitness + tweekFitness + doSomethingBonus - unrespondingPenalty;
    }

    private static double getScanFitness(int numGenerations, RobotResults robot, RobotResults opponent) {
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

    private static double getLastFitness(int numGenerations, RobotResults robot, RobotResults opponent) {
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

        double coverageFraction;
        if (robot.getCellsEntered() > 0) {
            coverageFraction = robot.getCellsEntered() / 48;
        } else {
            coverageFraction = 0;
        }
        double motionBonus = coverageFraction * robot.getBulletDamage() * CELL_VISITING_SCALE;

        double damageFitness = robot.getBulletDamage() + robot.getBulletDamageBonus() + robot.getRamDamage()
                + robot.getRamDamageBonus() + accuracyBonus + scanEfficiencyBonus + motionBonus;

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

        double unrespondingPenalty = (robot.getMaxUnrespondedHits() - 1) * UNRESPONDING_PENALTY_SCALE;

        return damageFitness + tweekFitness + doSomethingBonus - unrespondingPenalty;
    }


    private static double getJasonsFitness(int numGenerations, RobotResults robot, RobotResults opponent) {
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
        double scanEfficiencyBonus = EFFICIENCY_SCALE * scanEfficiency * (robot.getBulletDamage() + (robot.getNumScanEvents() > 0 ? 100 : 0));

        // bonus for survival
        double survivalBonus = SURVIVAL_SCALE * robot.getEnergy() * 0.01 * robot.getBulletDamage(); // they start with 100,
        double bulletHitPenalty = BULLET_PENALTY_SCALE * opponent.getNumBulletHits();

        // penalty for not moving...
        double movementPenalty = MOVEMENT_PENALTY * robot.getMovementPenalty();

        double damageFitness = robot.getBulletDamage() + robot.getBulletDamageBonus() + robot.getRamDamage()
                + robot.getRamDamageBonus() + accuracyBonus + scanEfficiencyBonus + survivalBonus + bulletHitPenalty + movementPenalty;

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


    private static double getScanAndSurviveFitness(int numGenerations, RobotResults robot, RobotResults opponent) {
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

        // bonus for survival
        double survivalBonus = SURVIVAL_SCALE * robot.getEnergy() * 0.01 * robot.getBulletDamage(); // they start with 100,

        double damageFitness = robot.getBulletDamage() + robot.getBulletDamageBonus() + robot.getRamDamage()
                + robot.getRamDamageBonus() + accuracyBonus + scanEfficiencyBonus + survivalBonus;

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


    private static double getMinBattleFitness(int numGenerations, RobotResults robot, RobotResults opponent) {

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
