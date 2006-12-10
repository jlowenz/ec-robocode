package com.imaginaryday.ec.ecj;
import com.imaginaryday.ec.rcpatches.GPRobotSpecification;
import ec.ECJAgent;
import ec.gp.GPTree;
import robocode.Robot;
import robocode.peer.robot.RobotClassManager;
import robocode.repository.RobotSpecification;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author rbowers
 *         Date: Nov 1, 2006
 *         Time: 9:18:03 PM
 */
public class ECJRobotClassManager extends RobotClassManager {

    private RobocodeIndividual ind;
    private GPTree moveProgram;
    private GPTree turretProgram;
    private GPTree radarProgram;
    private GPTree shootProgram;

    private RobotSpecification robotSpecification;

    public ECJRobotClassManager(RobocodeIndividual ind)
    {
        super(new GPRobotSpecification("ECJBot", "sadasd"));
        setRobotClass(ECJAgent.class);
        setName(ind.getName());
        this.ind = ind;
        moveProgram = ind.trees[0];
        turretProgram = ind.trees[1];
        radarProgram = ind.trees[2];
        shootProgram = ind.trees[3];
    }

    public GPTree getTurretProgram() {
        return turretProgram;
    }


    public GPTree getShootProgram() {
        return shootProgram;
    }


    public GPTree getMoveProgram() {
        return moveProgram;
    }

    public GPTree getRadarProgram() {
        return radarProgram;
    }


    @Override
    public Robot getRobotInstance() throws IllegalAccessException, InstantiationException {
        ECJAgent robot = null;

        Class<ECJAgent> c = getRobotClass();
        try {
            Constructor<ECJAgent> ctor = c.getConstructor(GPTree.class,GPTree.class,GPTree.class,GPTree.class);
            robot = ctor.newInstance(radarProgram, turretProgram, shootProgram, moveProgram);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return robot;
    }

    public RobotSpecification getRobotSpecification() {
        return this.robotSpecification;
    }


}
