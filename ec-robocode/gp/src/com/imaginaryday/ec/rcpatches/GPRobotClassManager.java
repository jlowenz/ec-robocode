package com.imaginaryday.ec.rcpatches;

import com.imaginaryday.ec.gp.Node;
import robocode.Robot;
import robocode.peer.TeamPeer;
import robocode.peer.robot.RobotClassManager;

import robocode.repository.RobotSpecification;

/**
 * @author rbowers
 *         Date: Nov 1, 2006
 *         Time: 9:18:03 PM
 */
public class GPRobotClassManager extends RobotClassManager {

    private Node moveProgram;
    private Node turretProgram;
    private Node radarProgram;
    private Node shootProgram;
    private RobotSpecification robotSpecification;

    public GPRobotClassManager() {
        super(new GPRobotSpecification("ECBot", "fred"));
        this.setRobotClass(GPRobot.class);
        robotSpecification = new GPRobotSpecification("No name yet", "Bob");
    }

    public GPRobotClassManager(RobotSpecification robotSpecification) {
        super(robotSpecification);
    }

    public GPRobotClassManager(RobotSpecification robotSpecification, TeamPeer teamManager) {
        super(robotSpecification, teamManager);
    }

    public Node getTurretProgram() {
        return turretProgram;
    }

    public void setTurretProgram(Node aimProgram) {
        this.turretProgram = aimProgram;
    }

    public Node getShootProgram() {
        return shootProgram;
    }

    public void setShootProgram(Node shootProgram) {
        this.shootProgram = shootProgram;
    }

    public Node getMoveProgram() {
        return moveProgram;
    }

    public void setMoveProgram(Node moveProgram) {
        this.moveProgram = moveProgram;
    }


    @Override
    public Robot getRobotInstance() throws IllegalAccessException, InstantiationException {
        GPRobot robot = null;

        robot = (GPRobot) (getRobotClass().newInstance());
        robot.setMoveProgram(moveProgram);
        robot.setTurretProgram(turretProgram);
        robot.setShootProgram(shootProgram);
        robot.setRadarProgram(radarProgram);
        robot.initialize();
        return robot;
    }

    public RobotSpecification getRobotSpecification() {
        return this.robotSpecification;
    }


    public Node getRadarProgram() {
        return radarProgram;
    }

    public void setRadarProgram(Node radarProgram) {
        this.radarProgram = radarProgram;
    }

}
