package com.imaginaryday.ec.rcpatches;

import net.jini.core.entry.Entry;

/**
 * @author rbowers
 *         Date: Nov 2, 2006
 *         Time: 10:30:42 PM
 */
public class GPBattleResults implements Entry {

    public Integer generation;
    private String robot1;
    private String robot2;
    private Double fitness1;
    private Double fitness2;

    public GPBattleResults() {
    }

    public GPBattleResults(GPBattleTask task, double fitness1, double fitness2) {
        this.generation = task.generation;
        this.robot1 = task.robot1;
        this.robot2 = task.robot2;
        this.fitness1 = fitness1;
        this.fitness2 = fitness2;
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

}