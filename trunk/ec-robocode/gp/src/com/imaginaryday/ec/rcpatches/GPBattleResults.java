package com.imaginaryday.ec.rcpatches;

import net.jini.core.entry.Entry;
import robocode.control.RobotResults;

/**
 * @author rbowers
 *         Date: Nov 2, 2006
 *         Time: 10:30:42 PM
 */
public class GPBattleResults implements Entry {

    public Integer generation;
    public Integer battle;
    public String robot1;
    public String robot2;
    public Double fitness1;
    public Double fitness2;

    public Integer rank1;
    public Integer score1;
    public Integer survival1;
    public Integer lastSurvivorBonus1;
    public Integer bulletDamage1;
    public Integer bulletDamageBonus1;
    public Integer ramDamage1;
    public Integer ramDamageBonus1;

    public Integer rank2;
    public Integer score2;
    public Integer survival2;
    public Integer lastSurvivorBonus2;
    public Integer bulletDamage2;
    public Integer bulletDamageBonus2;
    public Integer ramDamage2;
    public Integer ramDamageBonus2;

    public GPBattleResults() {
    }

    public GPBattleResults(GPBattleTask task, double fitness1, double fitness2,
                           RobotResults results1, RobotResults results2) {
        this.generation = task.generation;
        this.battle = task.battle;
        this.robot1 = task.robot1;
        this.robot2 = task.robot2;
        this.fitness1 = fitness1;
        this.fitness2 = fitness2;

        this.rank1 = results1.getRank();
        this.score1 = results1.getScore();
        this.survival1 = results1.getSurvival();
        this.lastSurvivorBonus1 = results1.getLastSurvivorBonus();
        this.bulletDamage1 = results1.getBulletDamage();
        this.bulletDamageBonus1 = results1.getBulletDamageBonus();
        this.ramDamage1 = results1.getRamDamage();
        this.ramDamageBonus1 = results1.getRamDamageBonus();

        this.rank2 = results2.getRank();
        this.score2 = results2.getScore();
        this.survival2 = results2.getSurvival();
        this.lastSurvivorBonus2 = results2.getLastSurvivorBonus();
        this.bulletDamage2 = results2.getBulletDamage();
        this.bulletDamageBonus2 = results2.getBulletDamageBonus();
        this.ramDamage2 = results2.getRamDamage();
        this.ramDamageBonus2 = results2.getRamDamageBonus();

        System.err.println(this);
    }

    public int getGeneration() {
        return generation;
    }

    public String getRobot1() {
        return robot1;
    }

    public String getRobot2() {
        return robot2;
    }

    public double getFitness1() {
        return fitness1;
    }

    public double getFitness2() {
        return fitness2;
    }

    public void setFitness1(double fitness1) {
        this.fitness1 = fitness1;
    }

    public void setFitness2(double fitness2) {
        this.fitness2 = fitness2;
    }


    public String toString() {
        return new StringBuilder().append("GPBattleResults [").append(generation).append(":")
                .append(battle).append("] ")
                .append(robot1).append(":").append(fitness1).append("    ").append(robot2).append(":")
                .append(fitness2).toString();
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("    vs. ").append(robot2);
        sb.append(" F: ").append(fitness1).append("|").append(fitness2);
        sb.append(" R: ").append(rank1).append("|").append(rank2);
        sb.append(" Sc: ").append(score1).append("|").append(score2);
        sb.append(" Su: ").append(survival1).append("|").append(survival2);
        sb.append(" Lsb: ").append(lastSurvivorBonus1).append("|").append(lastSurvivorBonus2);
        sb.append(" Bd: ").append(bulletDamage1).append("|").append(bulletDamage2);
        sb.append(" Bdb: ").append(bulletDamageBonus1).append("|").append(bulletDamageBonus2);
        sb.append(" Rd: ").append(ramDamage1).append("|").append(ramDamage2);
        sb.append(" Rdb: ").append(ramDamageBonus1).append("|").append(ramDamageBonus2);
        return sb.toString();


    }
}
