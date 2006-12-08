package com.imaginaryday.sim;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 7, 2006<br>
 * Time: 5:31:04 PM<br>
 * </b>
 */
public interface Simulation extends Runnable {
    void setup();
    boolean doTimeStep();
    void cleanup();
}