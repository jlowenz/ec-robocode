package com.imaginaryday.ec.main;

import java.io.Serializable;
import java.util.List;

/**
 * @author rbowers
 *         Date: Nov 15, 2006
 *         Time: 10:57:20 PM
 */
public class Snapshot implements Serializable {

    private int generation;
    private int popSize;
    private List<Member> population;
    private static final long serialVersionUID = -8165357450899418342L;

    public int getGeneration() {
        return generation;
    }

    public int getPopSize() {
        return popSize;
    }

    public List<Member> getPopulation() {
        return population;
    }

    public Snapshot(int generation, int popSize, List<Member> population) {
        this.generation = generation;
        this.popSize = popSize;
        this.population = population;
    }
    
}
