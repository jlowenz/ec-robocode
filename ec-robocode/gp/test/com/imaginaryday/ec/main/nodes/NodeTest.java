package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.gp.NodeFactory;
import com.imaginaryday.ec.gp.TreeFactory;
import com.imaginaryday.ec.gp.VetoTypeInduction;
import com.imaginaryday.ec.gp.nodes.Constant;
import com.imaginaryday.ec.main.GPAgent;
import info.javelot.functionalj.tuple.Pair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jscience.mathematics.vectors.VectorFloat64;
import robocode.*;

import java.awt.*;
import java.io.File;
import java.util.Random;
import java.util.Vector;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 12, 2006<br>
 * Time: 5:08:58 PM<br>
 * </b>
 */
public class NodeTest extends TestCase {

    static {
        NodeFactory nf = NodeFactory.getInstance();
        try {
            nf.loadNode(BulletBearing.class);
            nf.loadNode(BulletEnergy.class);
            nf.loadNode(BulletSpeed.class);
            nf.loadNode(CurrentRadarHeading.class);
            nf.loadNode(CurrentTurretHeading.class);
            nf.loadNode(CurrentVector.class);
            nf.loadNode(EnemyEnergy.class);
            nf.loadNode(EnemyHeading.class);
            nf.loadNode(EnemySpeed.class);
            nf.loadNode(EnergyLevel.class);
            nf.loadNode(GunHeat.class);
            nf.loadNode(HitByBullet.class);
            nf.loadNode(HitByBulletAge.class);
            nf.loadNode(HitWall.class);
            nf.loadNode(HitWallAge.class);
            nf.loadNode(IsMoving.class);
            nf.loadNode(IsRadarMoving.class);
            nf.loadNode(IsTurretMoving.class);
            nf.loadNode(Rammed.class);
            nf.loadNode(RammedAge.class);
            nf.loadNode(RammerBearing.class);
            nf.loadNode(RamMyFault.class);
            nf.loadNode(RotateVector.class);
            nf.loadNode(ScaleVector.class);
            nf.loadNode(ScannedEnemy.class);
            nf.loadNode(ScannedEnemyAge.class);
            nf.loadNode(VectorHeading.class);
            nf.loadNode(VectorLength.class);
            nf.loadNode(VectorToEnemy.class);
            nf.loadNode(VectorToForwardWall.class);
            nf.loadNode(VectorToNearestWall.class);
            nf.loadNode(MakePair.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }    

    public NodeTest() {
	}

	public NodeTest(String string) {
		super(string);
	}

	public void testNodes()
	{
        NodeFactory nf = NodeFactory.getInstance();
        Node root = nf.create("add");
        try {
            root.attach(0, nf.create("mul").attach(0, nf.create("const", 5))
                                           .attach(1, nf.create("const", 12)))
                .attach(1, nf.create("const", 42));
        } catch (VetoTypeInduction vetoTypeInduction) {
            fail("vetoed type induction");
            vetoTypeInduction.printStackTrace();
        }

        assertEquals(102.0, root.evaluate());
    }

    public void testTreeCreation()
    {
        NodeFactory nf = NodeFactory.getInstance();
        TreeFactory tf = new TreeFactory(nf);

        GPAgent robot = new robot();

        for (int i = 0; i < 10000; i++) {
            Node root = tf.generateRandomTree(6, Number.class);
            root.setOwner(robot);
            Object result = root.evaluate();
            System.out.println(root);
            System.out.println(result);

            root = tf.generateRandomTree(6, Boolean.class);
            root.setOwner(robot);
            result = root.evaluate();
            System.out.println(root);
            System.out.println(result);

            root = tf.generateRandomTree(6, VectorFloat64.class);
            root.setOwner(robot);
            result = root.evaluate();
            System.out.println(root);
            System.out.println(result);

            root = tf.generateRandomTree(6, Pair.class);
            root.setOwner(robot);
            result = root.evaluate();
            System.out.println(root);
            System.out.println(result);
        }
    }

    public void testRotateVector() {
        RotateVector rv = new RotateVector();
        try {
            rv.attach(0,new VectorConstant(VectorFloat64.valueOf(0,1)));
            rv.attach(1,new Constant(Math.PI/2.0));
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
        }
        VectorFloat64 vec = (VectorFloat64) rv.evaluate();
        assertEquals(1.0, vec.getValue(0), 0.00001);
        assertEquals(0.0, vec.getValue(1), 0.00001);
    }

    public void testVectorHeading() {
        VectorHeading up = new VectorHeading();
        VectorHeading down = new VectorHeading();
        VectorHeading left = new VectorHeading();
        VectorHeading right = new VectorHeading();
        try {
            up.attach(0,new VectorConstant(VectorFloat64.valueOf(0,1)));
            down.attach(0,new VectorConstant(VectorFloat64.valueOf(0,-1)));
            left.attach(0,new VectorConstant(VectorFloat64.valueOf(-1,0)));
            right.attach(0,new VectorConstant(VectorFloat64.valueOf(1,0)));
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
        }
        assertEquals(0.0, ((Number)up.evaluate()).doubleValue(), 0.00001);
        assertEquals(Math.PI, ((Number)down.evaluate()).doubleValue(), 0.00001);
        assertEquals(Math.PI*1.5, ((Number)left.evaluate()).doubleValue(), 0.00001);
        assertEquals(Math.PI/2.0, ((Number)right.evaluate()).doubleValue(), 0.00001);
    }

    public void testScaleVector() {
        ScaleVector sv = new ScaleVector();
        try {
            sv.attach(0, new VectorConstant(VectorFloat64.valueOf(0,1)));
            sv.attach(1, new Constant(5.0));
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
        }
        VectorFloat64 vec = (VectorFloat64) sv.evaluate();
        assertEquals(0.0, vec.getValue(0), 0.000001);
        assertEquals(5.0, vec.getValue(1), 0.000001);
    }

    public static Test suite()
	{
		TestSuite s = new TestSuite();
		s.addTest(new NodeTest("testNodes"));
        s.addTest(new NodeTest("testTreeCreation"));
        s.addTest(new NodeTest("testRotateVector"));
        s.addTest(new NodeTest("testVectorHeading"));
        s.addTest(new NodeTest("testScaleVector"));
        return s;
	}

    private static class robot extends GPAgent {
        Random rand = new Random();


        public robot() {
            super(null,null,null,null);
        }


        @Override
        public double getHeadingRadians() {
            return rand.nextDouble();
        }
        @Override
        public void setTurnLeftRadians(double radians) {
        }
        @Override
        public void setTurnRightRadians(double radians) {
        }
        @Override
        public void turnLeftRadians(double radians) {
        }
        @Override
        public void turnRightRadians(double radians) {
        }
        @Override
        public double getGunHeadingRadians() {
            return rand.nextDouble();
        }
        @Override
        public double getRadarHeadingRadians() {
            return rand.nextDouble();
        }
        @Override
        public void setTurnGunLeftRadians(double radians) {
        }
        @Override
        public void setTurnGunRightRadians(double radians) {
        }
        @Override
        public void setTurnRadarLeftRadians(double radians) {
        }
        @Override
        public void setTurnRadarRightRadians(double radians) {
        }
        @Override
        public void turnGunLeftRadians(double radians) {
        }
        @Override
        public void turnGunRightRadians(double radians) {
        }
        @Override
        public void turnRadarLeftRadians(double radians) {
        }
        @Override
        public void turnRadarRightRadians(double radians) {
        }
        @Override
        public double getGunTurnRemainingRadians() {
            return rand.nextDouble();
        }
        @Override
        public double getRadarTurnRemainingRadians() {
            return rand.nextDouble();
        }
        @Override
        public double getTurnRemainingRadians() {
            return rand.nextDouble();
        }
        @Override
        public double getDistanceRemaining() {
            return rand.nextDouble();
        }
        @Override
        public Vector<BulletHitBulletEvent> getBulletHitBulletEvents() {
            return super.getBulletHitBulletEvents();
        }
        @Override
        public Vector<BulletHitEvent> getBulletHitEvents() {
            return super.getBulletHitEvents();
        }
        @Override
        public Vector<BulletMissedEvent> getBulletMissedEvents() {
            return super.getBulletMissedEvents();
        }
        @Override
        public File getDataDirectory() {
            return super.getDataDirectory();
        }
        @Override
        public File getDataFile(String filename) {
            return super.getDataFile(filename);
        }
        @Override
        public long getDataQuotaAvailable() {
            return super.getDataQuotaAvailable();
        }
        @Override
        public int getEventPriority(String eventClass) {
            return super.getEventPriority(eventClass);
        }
        @Override
        public double getGunTurnRemaining() {
            return rand.nextDouble();
        }
        @Override
        public Vector<HitByBulletEvent> getHitByBulletEvents() {
            return super.getHitByBulletEvents();
        }
        @Override
        public Vector<HitRobotEvent> getHitRobotEvents() {
            return super.getHitRobotEvents();
        }
        @Override
        public Vector<HitWallEvent> getHitWallEvents() {
            return super.getHitWallEvents();
        }
        @Override
        public double getRadarTurnRemaining() {
            return rand.nextDouble();
        }
        @Override
        public Vector<RobotDeathEvent> getRobotDeathEvents() {
            return super.getRobotDeathEvents();
        }
        @Override
        public Vector<ScannedRobotEvent> getScannedRobotEvents() {
            return super.getScannedRobotEvents();
        }
        @Override
        public double getTurnRemaining() {
            return rand.nextDouble();
        }
        @Override
        public boolean isAdjustGunForRobotTurn() {
            return super.isAdjustGunForRobotTurn();
        }
        @Override
        public boolean isAdjustRadarForGunTurn() {
            return super.isAdjustRadarForGunTurn();
        }
        @Override
        public void onCustomEvent(CustomEvent event) {
            super.onCustomEvent(event);
        }
        @Override
        public void removeCustomEvent(Condition condition) {
            super.removeCustomEvent(condition);
        }
        @Override
        public void setEventPriority(String eventClass, int priority) {
            super.setEventPriority(eventClass, priority);
        }
        @Override
        public void setInterruptible(boolean interruptible) {
            super.setInterruptible(interruptible);
        }
        @Override
        public void setMaxTurnRate(double newMaxTurnRate) {
            super.setMaxTurnRate(newMaxTurnRate);
        }
        @Override
        public void setMaxVelocity(double newMaxVelocity) {
            super.setMaxVelocity(newMaxVelocity);
        }
        @Override
        public void setResume() {
            super.setResume();
        }
        @Override
        public void setStop() {
            super.setStop();
        }
        @Override
        public void setStop(boolean overwrite) {
            super.setStop(overwrite);
        }
        @Override
        public void setTurnGunLeft(double degrees) {
            super.setTurnGunLeft(degrees);
        }
        @Override
        public void setTurnGunRight(double degrees) {
            super.setTurnGunRight(degrees);
        }
        @Override
        public void setTurnRadarLeft(double degrees) {
            super.setTurnRadarLeft(degrees);
        }
        @Override
        public void setTurnRadarRight(double degrees) {
            super.setTurnRadarRight(degrees);
        }
        @Override
        public void waitFor(Condition condition) {
            super.waitFor(condition);
        }
        @Override
        public boolean isAdjustRadarForRobotTurn() {
            return super.isAdjustRadarForRobotTurn();
        }
        @Override
        public void onDeath(DeathEvent event) {
            super.onDeath(event);
        }
        @Override
        public void onSkippedTurn(SkippedTurnEvent event) {
            super.onSkippedTurn(event);
        }

        @Override
        public double getEnergy() {
            return rand.nextDouble();
        }
        @Override
        public void ahead(double distance) {
            super.ahead(distance);
        }
        @Override
        public void back(double distance) {
            super.back(distance);
        }
        @Override
        public double getBattleFieldHeight() {
            return 1024;
        }
        @Override
        public double getBattleFieldWidth() {
            return 1024;
        }
        @Override
        public double getHeading() {
            return rand.nextDouble();
        }
        @Override
        public double getHeight() {
            return 40;
        }
        @Override
        public String getName() {
            return super.getName();
        }
        @Override
        public double getWidth() {
            return 40;
        }
        @Override
        public double getX() {
            return rand.nextDouble() * (1024 - 80) + 40;
        }
        @Override
        public double getY() {
            return rand.nextDouble() * (1024 - 80) + 40;
        }
        @Override
        public void turnLeft(double degrees) {
            super.turnLeft(degrees);
        }
        @Override
        public void turnRight(double degrees) {
            super.turnRight(degrees);
        }
        @Override
        public void doNothing() {
            super.doNothing();
        }
        @Override
        public void fire(double power) {

        }
        @Override
        public Bullet fireBullet(double power) {
            return null;
        }
        @Override
        public double getGunCoolingRate() {
            return 1.0;
        }
        @Override
        public double getGunHeading() {
            return rand.nextDouble();
        }
        @Override
        public double getGunHeat() {
            return rand.nextDouble();
        }
        @Override
        public int getNumRounds() {
            return 10;
        }
        @Override
        public int getOthers() {
            return super.getOthers();
        }
        @Override
        public double getRadarHeading() {
            return rand.nextDouble();
        }
        @Override
        public int getRoundNum() {
            return super.getRoundNum();
        }
        @Override
        public long getTime() {
            return super.getTime();
        }
        @Override
        public double getVelocity() {
            return rand.nextDouble();
        }
        @Override
        public void resume() {
            super.resume();
        }
        @Override
        public void scan() {

        }
        @Override
        public void setAdjustGunForRobotTurn(boolean newAdjustGunForRobotTurn) {
            super.setAdjustGunForRobotTurn(newAdjustGunForRobotTurn);
        }
        @Override
        public void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
            super.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
        }
        @Override
        public void setColors(Color robotColor, Color gunColor, Color radarColor) {
            super.setColors(robotColor, gunColor, radarColor);
        }
        @Override
        public void setColors(Color robotColor, Color gunColor, Color radarColor, Color bulletColor, Color scanColor) {
            super.setColors(robotColor, gunColor, radarColor, bulletColor, scanColor);
        }
        @Override
        public void setAllColors(Color color) {
            super.setAllColors(color);
        }
        @Override
        public void setBodyColor(Color color) {
            super.setBodyColor(color);
        }
        @Override
        public void setGunColor(Color color) {
            super.setGunColor(color);
        }
        @Override
        public void setRadarColor(Color color) {
            super.setRadarColor(color);
        }
        @Override
        public void setBulletColor(Color color) {
            super.setBulletColor(color);
        }
        @Override
        public void setScanColor(Color color) {
            super.setScanColor(color);
        }
        @Override
        public void stop() {
            super.stop();
        }
        @Override
        public void stop(boolean overwrite) {
            super.stop(overwrite);
        }
        @Override
        public void turnGunLeft(double degrees) {
            super.turnGunLeft(degrees);
        }
        @Override
        public void turnGunRight(double degrees) {
            super.turnGunRight(degrees);
        }
        @Override
        public void turnRadarLeft(double degrees) {
            super.turnRadarLeft(degrees);
        }
        @Override
        public void turnRadarRight(double degrees) {
            super.turnRadarRight(degrees);
        }
    }
}