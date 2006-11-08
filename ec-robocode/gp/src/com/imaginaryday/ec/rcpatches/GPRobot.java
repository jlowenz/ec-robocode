package com.imaginaryday.ec.rcpatches;

import com.imaginaryday.ec.gp.Node;
import robocode.AdvancedRobot;

/**
 * @author rbowers
 *         Date: Nov 1, 2006
 *         Time: 9:34:32 PM
 */
public class GPRobot extends AdvancedRobot {
    private Node moveProgram;
    private Node turretProgram;
    private Node radarProgram;
    private Node shootProgram;

    public Object getTurretProgram() {
        return turretProgram;
    }

    public void setTurretProgram(Node turretProgram) {
        this.turretProgram = turretProgram;
    }

    public Object getMoveProgram() {
        return moveProgram;
    }

    public void setMoveProgram(Node moveProgram) {
        this.moveProgram = moveProgram;
    }

    public Object getShootProgram() {
        return shootProgram;
    }

    public Node getRadarProgram() {
        return radarProgram;
    }

    public void setRadarProgram(Node radarProgram) {
        this.radarProgram = radarProgram;
    }

    public void setShootProgram(Node shootProgram) {
        this.shootProgram = shootProgram;
    }

    public void initialize() {

    }

}
