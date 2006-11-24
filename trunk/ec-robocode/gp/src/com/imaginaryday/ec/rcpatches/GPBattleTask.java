package com.imaginaryday.ec.rcpatches;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.Member;
import net.jini.core.entry.Entry;

import java.rmi.MarshalledObject;
import java.io.IOException;

/**
 * @author rbowers
 *         Date: Nov 2, 2006
 *         Time: 9:59:10 PM
 */
public class GPBattleTask implements Entry {
    public Integer generation;
    public Integer battle;

    public String robot1;
    public String robot2;

    public MarshalledObject moveProgram1;
    public MarshalledObject turretProgram1;
    public MarshalledObject radarProgram1;
    public MarshalledObject shootProgram1;

    public MarshalledObject moveProgram2;
    public MarshalledObject turretProgram2;
    public MarshalledObject radarProgram2;
    public MarshalledObject shootProgram2;

    public Boolean done = Boolean.FALSE;

    public GPBattleTask(int generation, int battle, Member member1, Member member2) {
        this.generation = generation;
        this.battle = battle;

        robot1 = member1.getName();
        try {
            moveProgram1 = new MarshalledObject(member1.getMoveProgram());
            turretProgram1 = new MarshalledObject(member1.getTurretProgram());
            radarProgram1 = new MarshalledObject(member1.getRadarProgram());
            shootProgram1 = new MarshalledObject(member1.getShootProgram());

            robot2 = member2.getName();
            moveProgram2 = new MarshalledObject(member2.getMoveProgram());
            turretProgram2 = new MarshalledObject(member2.getTurretProgram());
            radarProgram2 = new MarshalledObject(member2.getRadarProgram());
            shootProgram2 = new MarshalledObject(member2.getShootProgram());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GPBattleTask() {

    }

    public GPBattleTask(int generation, int battle, Member member1, String standardBotName) {
        this.generation = generation;
        this.battle = battle;

        robot1 = member1.getName();
        try {
            moveProgram1 = new MarshalledObject(member1.getMoveProgram());
            turretProgram1 = new MarshalledObject(member1.getTurretProgram());
            radarProgram1 = new MarshalledObject(member1.getRadarProgram());
            shootProgram1 = new MarshalledObject(member1.getShootProgram());
        } catch (IOException e) {
            e.printStackTrace();
        }

        robot2 = standardBotName;
        moveProgram2 = null;
        turretProgram2 = null;
        radarProgram2 = null;
        shootProgram2 = null;
    }

    public String shortString() {

        return new StringBuilder().append("BattleTask[").append(generation).append(" | ")
                .append(battle).append("] ").append(robot1).append(" vs ")
                .append(robot2).toString();
    }

    @Override
    public String toString() {
        return new StringBuilder().append("GPBattleTask{").append("battle=").append(battle)
                .append(", generation=").append(generation).append(", robot1='").append(robot1)
                .append('\'').append(", robot2='").append(robot2).append('\'').append(", moveProgram1=")
                .append(getMoveProgram1()).append(", turretProgram1=").append(getTurretProgram1()).append(", radarProgram1=")
                .append(getRadarProgram1()).append(", shootProgram1=").append(getShootProgram1()).append(", moveProgram2=")
                .append(getMoveProgram2()).append(", turretProgram2=").append(getTurretProgram2()).append(", radarProgram2=")
                .append(getRadarProgram2()).append(", shootProgram2=").append(getShootProgram2()).append(", done=")
                .append(done).append('}').toString();
    }

    public Integer getBattle() {
        return battle;
    }

    public Boolean getDone() {
        return done;
    }

    public Integer getGeneration() {
        return generation;
    }

    public Node getMoveProgram1() {
        try {
            return (Node)moveProgram1.get();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public Node getMoveProgram2() {
        try {
            return (Node) moveProgram2.get();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public Node getRadarProgram1() {
        try {
            return (Node) radarProgram1.get();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public Node getRadarProgram2() {
        try {
            return (Node)radarProgram2.get();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public String getRobot1() {
        return robot1;
    }

    public String getRobot2() {
        return robot2;
    }

    public Node getShootProgram1() {
        try {
            return (Node)shootProgram1.get();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public Node getShootProgram2() {
        try {
            return (Node)shootProgram2.get();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public Node getTurretProgram1() {
        try {
            return (Node)turretProgram1.get();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public Node getTurretProgram2() {
        try {
            return (Node) turretProgram2.get();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
