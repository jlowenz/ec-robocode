package com.imaginaryday.ec.rcpatches;

import robocode.Robot;
import robocode.peer.robot.RobotClassManager;
import robocode.repository.RobotSpecification;

/**
 * @author rbowers
 *         Date: Nov 12, 2006
 *         Time: 1:57:51 PM
 */
public class StandardRobotClassManager extends RobotClassManager {

    private Class<? extends Robot> robotClass;
    private RobotSpecification robotSpecification;

    public StandardRobotClassManager(RobotSpecification robotSpecification,
                                     Class<? extends Robot> clazz) {
        super(robotSpecification);
        this.robotClass = clazz;
        this.robotSpecification = robotSpecification;
    }

    @Override
    public Robot getRobotInstance() throws IllegalAccessException, InstantiationException {
        Robot robot;
        robot = (Robot) (getRobotClass().newInstance());
        return robot;
    }

    public RobotSpecification getRobotSpecification() {
        return this.robotSpecification;
    }
}
