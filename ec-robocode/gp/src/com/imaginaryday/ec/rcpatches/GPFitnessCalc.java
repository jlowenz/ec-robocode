package com.imaginaryday.ec.rcpatches;

import robocode.control.RobotResults;

/**
 * @author rbowers
 *         Date: Nov 8, 2006
 *         Time: 9:35:54 PM
 */
public class GPFitnessCalc {

    public static double getFitness(RobotResults res) {        
        return (double)res.getBulletDamageBonus();
    }
}
