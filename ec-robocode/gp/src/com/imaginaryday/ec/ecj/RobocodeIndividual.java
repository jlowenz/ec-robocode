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
    private String name;
    private int gen;


    public void setName(String name) {
        this.name = name;
    }
    public void setGen(int gen) {
        this.gen = gen;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getGen() {
        return gen;
    }
}
