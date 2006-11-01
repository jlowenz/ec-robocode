package com.imaginaryday.ec.main;

import robocode.AdvancedRobot;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * User: jlowens
 * Date: Oct 30, 2006
 * Time: 6:13:08 PM
 */
public class GPAgent extends AdvancedRobot {

    VectorFloat64 currentVector;
    VectorFloat64 currentTarget;


    public VectorFloat64 getCurrentVector() {
        return currentVector;
    }

    public VectorFloat64 getCurrentTarget() {
        return currentTarget;
    }

    public void run() {
        
    }
	
}
