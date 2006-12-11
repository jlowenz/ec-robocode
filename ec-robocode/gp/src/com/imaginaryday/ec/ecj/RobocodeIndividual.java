package com.imaginaryday.ec.ecj;

import ec.gp.GPIndividual;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 10:49:04 PM<br>
 * </b>
 */
public class RobocodeIndividual extends GPIndividual {
    private static final long serialVersionUID = -5926571587862038051L;
    private static AtomicInteger ID = new AtomicInteger();
    private int id = ID.getAndIncrement();
    private String name = null;
    private int gen = 0;


    public void setName(String name) {
        this.name = name;
    }
    public void setGen(int gen) {
        this.gen = gen;
        name = null;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
        name = null;
    }
    public String getName() {
        if (name == null) {
            name = "ECJAgent_" + id; // hmm how to get gen
        }
        return name;
    }
    public int getGen() {
        return gen;
    }

    @Override
    public Object clone() {
        RobocodeIndividual ind = (RobocodeIndividual) super.clone();
        ind.setId(ID.getAndIncrement());
        return ind;
    }


    @Override
    public GPIndividual lightClone() {
        RobocodeIndividual ind = (RobocodeIndividual) super.lightClone();
        ind.setId(ID.getAndIncrement());
        return ind;
    }
}
