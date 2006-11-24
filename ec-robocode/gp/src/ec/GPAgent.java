package ec;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.gp.NodeFactory;
import com.imaginaryday.ec.gp.VetoTypeInduction;
import com.imaginaryday.ec.gp.nodes.Constant;
import com.imaginaryday.ec.gp.nodes.GreaterThan;
import com.imaginaryday.ec.gp.nodes.IfThenElse;
import com.imaginaryday.ec.main.nodes.*;
import com.imaginaryday.util.Stuff;
import static com.imaginaryday.util.Stuff.clampZero;
import com.imaginaryday.util.VectorUtils;
import info.javelot.functionalj.tuple.Pair;
import org.jscience.mathematics.numbers.Float64;
import org.jscience.mathematics.vectors.VectorFloat64;
import robocode.*;
import robocode.exception.DeathException;

import java.util.logging.Logger;

/**
 * User: jlowens Date: Oct 30, 2006 Time: 6:13:08 PM
 */
public class GPAgent extends AdvancedRobot {
    private static Logger log = Logger.getLogger(GPAgent.class.getName());
// comment out because robocode is a piece of shit
//    static {
//        Handler h = new ConsoleHandler();
//        h.setLevel(Level.INFO);
//        GPAgent.log.addHandler(h);
//        GPAgent.log.setLevel(Level.INFO);
//    }

    private static final boolean DEBUG = false;

    private Node radarTree;
    private Node turretTree;
    private Node firingTree;
    private Node directionTree;
    private boolean alive = true;
    private VectorFloat64 vectorToEnemy = VectorFloat64.valueOf(0, 1);
    private int scannedEnemyAge = 0;
    private double enemyHeading = 0.0;
    private double enemySpeed = 0.0;
    private double enemyEnergy = 0.0;
    private boolean recentlyRammed = false;
    private boolean recentlyHitByBullet = false;
    private int bulletHitAge;
    private double bulletBearing = 0.0;
    private double bulletEnergy = 0.0;
    private double bulletSpeed = 0.0;
    private boolean recentlyHitWall = false;
    private int wallHitAge = 0;
    private VectorFloat64 vectorToForwardWall = VectorFloat64.valueOf(0, 1);
    private VectorFloat64 vectorToNearestWall = VectorFloat64.valueOf(0, 1);
    private int rammedAge = 0;
    private boolean myFault = false;
    private double rammerBearing = 0.0;
    private boolean scannedEnemy = true;

    public GPAgent() {
        System.err.println("frakking A");
        NodeFactory nf = NodeFactory.getInstance();
        radarTree = new Constant(20*Math.PI/2.0);
        try {
            turretTree = new VectorHeading().attach(0, new VectorToEnemy());
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
        }
        Node n = new MakePair();
        try {
            n.attach(0, nf.create("boolConst", true))
             .attach(1, nf.create("const", 1.0));
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace(); 
        }

        firingTree = n;
        try {
            directionTree = new IfThenElse().attach(0, new GreaterThan().attach(0, new EnemySpeed())
                                                                        .attach(1, new Constant(0.0)))
                                            .attach(1, new VectorToEnemy())
                                            .attach(2, new VectorToNearestWall());
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
        }

        radarTree.setOwner(this);
	    turretTree.setOwner(this);
	    firingTree.setOwner(this);
	    directionTree.setOwner(this);
    }

    public GPAgent(Node radarTree, Node turretTree, Node firingTree, Node directionTree) {
        this.radarTree = radarTree;
        this.turretTree = turretTree;
        this.firingTree = firingTree;
        this.directionTree = directionTree;

        if (radarTree != null) this.radarTree.setOwner(this);
        if (turretTree != null) this.turretTree.setOwner(this);
        if (firingTree != null) this.firingTree.setOwner(this);
        if (directionTree != null) this.directionTree.setOwner(this);
    }

    public Node getRadarTree() { return radarTree;}
    public Node getTurretTree() {return turretTree;}
    public Node getFiringTree() {return firingTree;}
    public Node getDirectionTree() {return directionTree;}

//    public VectorFloat64 getCurrentVector() {return movementVector;}

    public VectorFloat64 getVectorToForwardWall() {return vectorToForwardWall;}
    public VectorFloat64 getVectorToNearWall() {return vectorToNearestWall;}

    public boolean getHitWall() { return recentlyHitWall; }
    public int getHitWallAge() { return wallHitAge; }

    public boolean getHitByBullet() { return recentlyHitByBullet; }
    public int getHitByBulletAge() { return bulletHitAge; }
    public double getBulletBearing() { return bulletBearing; }
    public double getBulletEnergy() { return bulletEnergy; }
    public double getBulletSpeed() { return bulletSpeed; }

    public boolean getRammed() {return recentlyRammed;}
    public int getRammedAge() {return rammedAge;}
    public boolean isMyFault() { return myFault; }
    public double getRammerBearing() { return rammerBearing; }

    public boolean getScannedEnemy() { return scannedEnemy; }
    public int getScannedEnemyAge() { return scannedEnemyAge; }
    public VectorFloat64 getVectorToEnemy() {return vectorToEnemy;}
    public double getEnemyHeading() {return enemyHeading;}
    public double getEnemySpeed() {return enemySpeed;}
    public double getEnemyEnergy() {return enemyEnergy;}


    private static enum Wall {
        LEFT, BOTTOM, RIGHT, TOP
    }

    private enum Turn {
        LEFT, RIGHT
    }

    private static class p<A, B> {
        public A first;
        public B second;

        public p(A first, B second) {
            super();
            this.first = first;
            this.second = second;
        }

        public static <A, B> p<A, B> n(A a, B b) {
            return new p<A, B>(a, b);
        }
    }

    private <T> T argmin(p<Double, T>... pair) {
        p<Double, T> _min = pair[0];
        for (p<Double, T> m : pair) {
            if (m.first < _min.first) {
                _min = m;
            }
        }
        return _min.second;
    }

    private <T> p<Double, T> pairmin(p<Double, T>... ps) {
        p<Double, T> _min = ps[0];
        for (p<Double, T> m : ps) {
            if (m.first < _min.first) _min = m;
        }
        return _min;
    }

    @SuppressWarnings("unchecked")
    public void run() {
        GPAgent.log.fine("run()");
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        GPAgent.log.fine("starting pseudo-infinite loop");
        while (alive) {
            try {
// gather instantaneous sensor data
                double bfheight = getBattleFieldHeight();
                double bfwidth = getBattleFieldWidth();

                double x = getX();
                double y = getY();
                double toRight = bfwidth - x;
                double toTop = bfheight - y;

                double rwidth = getWidth();
                double rwidth2 = rwidth / 2.0;
                double rheight = getHeight();
                double rheight2 = rheight / 2.0;
                double robotRadius = Math.sqrt(rheight2 * rheight2 + rwidth2 * rwidth2);

                // find vector to nearest wall
                switch (argmin(GPAgent.p.n(x, Wall.LEFT), GPAgent.p.n(y, Wall.BOTTOM), GPAgent.p.n(toRight,
                        Wall.RIGHT), GPAgent.p.n(toTop, Wall.TOP))) {
                    case LEFT:
                        vectorToNearestWall = VectorFloat64.valueOf(-1, 0).times(Float64.valueOf(Math.abs(x - rwidth2)));
                        break;
                    case RIGHT:
                        vectorToNearestWall = VectorFloat64.valueOf(1, 0).times(Float64.valueOf(toRight - rwidth2));
                        break;
                    case TOP:
                        vectorToNearestWall = VectorFloat64.valueOf(0, 1).times(Float64.valueOf(toTop - rheight2));
                        break;
                    case BOTTOM:
                        vectorToNearestWall = VectorFloat64.valueOf(0, -1).times(Float64.valueOf(y - rheight2));
                        break;
                }

                // find the vector to the forward wall
                vectorToForwardWall = vecToWall(x, y, bfwidth, bfheight, getHeading(), robotRadius, VectorUtils.vecFromDir(getHeadingRadians()));

                // get absolute radar heading
                double radarDirection = 0;
                double turretDirection = 0;
                Pair<Boolean, Number> firing = null;

                try {
                    radarDirection = ((Number) radarTree.evaluate()).doubleValue();
                } catch (Throwable e) {
                    GPAgent.log.finest(((AbstractNode) radarTree).toStringEval());
                }

                // get absolute turret heading
                try {
                    turretDirection = ((Number) turretTree.evaluate()).doubleValue();
                } catch (Throwable e) {
                    GPAgent.log.finest(((AbstractNode) turretTree).toStringEval());
                }

                // determine if we need to fire
                try {
                    firing = (Pair<Boolean, Number>) firingTree.evaluate();
                } catch (Throwable t ) {
                    GPAgent.log.finest(((AbstractNode) firingTree).toStringEval());
                    firing = new Pair<Boolean, Number>(false, 0.0);
                }

                // get absolute robot heading and velocity
	            VectorFloat64 movementVector = null;
                try {
                    movementVector = (VectorFloat64) directionTree.evaluate();
                } catch (Throwable t ) {
	                System.err.println("frak");
	                t.printStackTrace();
                    GPAgent.log.finest(((AbstractNode) directionTree).toStringEval());
                }

                // process firing directive
                if (firing.getFirst()) {
                    setFire(firing.getSecond().doubleValue());
                }

                // process radar directive
                p<Double, Turn> r = calculateTurn(getRadarHeadingRadians(), radarDirection);
                switch (r.second) {
                    case LEFT:
                        setTurnRadarLeftRadians(r.first);
                        break;
                    case RIGHT:
                        setTurnRadarRightRadians(r.first);
                        break;
                }

                // process turret directive
                r = calculateTurn(getGunHeadingRadians(), turretDirection);
                switch (r.second) {
                    case LEFT:
                        setTurnGunLeftRadians(r.first);
                        break;
                    case RIGHT:
                        setTurnGunRightRadians(r.first);
                        break;
                }

                // process heading/speed directive
	            // the movementVector is supposed to be an

	            double heading = VectorUtils.toAngle(movementVector);
                r = calculateTurn(getHeadingRadians(), heading);
                switch (r.second) {
                    case LEFT:
                        setTurnLeftRadians(r.first);
                        break;
                    case RIGHT:
                        setTurnRightRadians(r.first);
                        break;
                }
                // set speed
                double scale;
                double turnRemaining = getTurnRemaining();
                if (turnRemaining < 15) {
                    scale = 1.0;
                } else if (turnRemaining < 30) {
                    scale = 0.9;
                } else if (turnRemaining < 45) {
                    scale = 0.8;
                } else if (turnRemaining < 60) {
                    scale = 0.7;
                } else if (turnRemaining < 75) {
                    scale = 0.6;
                } else if (turnRemaining < 90) {
                    scale = 0.5;
                } else if (turnRemaining < 105) {
                    scale = 0.4;
                } else if (turnRemaining < 120) {
                    scale = 0.3;
                } else if (turnRemaining < 135) {
                    scale = 0.2;
                } else {
                    scale = 0.1;
                }
                setMaxVelocity(scale * 8.0);
                double dist = clampZero(movementVector.normValue());
                // TODO: able to move backwards?
                if (dist > 1.0) {
                    setAhead(dist);
                }

                try {
                    execute();
                } catch (DeathException e) { alive = false; }

                if (bulletHitAge > 100) resetBulletHit();
                if (wallHitAge > 100) resetWallHit();
                if (rammedAge > 100) resetRammed();

                if (recentlyHitByBullet) bulletHitAge++;
                if (recentlyHitWall) wallHitAge++;
                if (recentlyRammed) rammedAge++;
            } catch (Throwable e) {
                e.printStackTrace();
                GPAgent.log.severe("EXCEPTION in GPAgent: " + e.toString());
            } finally {
                try {
                    execute();
                } catch (Exception e) { alive = false; }
            }
        }
    }

    private void resetRammed() {
        rammedAge = 0;
        recentlyRammed = false;
    }

    private void resetWallHit() {
        wallHitAge = 0;
        recentlyHitWall = false;
    }

    private void resetBulletHit() {
        bulletHitAge = 0;
        recentlyHitByBullet = false;
    }


    private p<Double, Turn> calculateTurn(final double current, final double dest) {
        return (current > dest) ?
                pairmin(GPAgent.p.n(clampZero(current - dest), GPAgent.Turn.LEFT), GPAgent.p.n(clampZero(2 * Math.PI - current + dest), GPAgent.Turn.RIGHT)) :
                pairmin(GPAgent.p.n(clampZero(2 * Math.PI - dest + current), GPAgent.Turn.LEFT), GPAgent.p.n(clampZero(dest - current), GPAgent.Turn.RIGHT));
    }


    private VectorFloat64 vecToWall(double x,
                                    double y,
                                    double w,
                                    double h,
                                    double headingDegrees,
                                    double robotRadius,
                                    VectorFloat64 dir) {
        double m = dir.getValue(1) / dir.getValue(0);
        double tx;
        double ty;

        double dist = 0.0;
        double angle = VectorUtils.toAngle(dir);
        headingDegrees = Math.toDegrees(angle);
        double cornerAngle = 0;
        if (headingDegrees >= 0 && headingDegrees < 90) {
            cornerAngle = VectorUtils.toAngle(VectorFloat64.valueOf(w-x, h-y));
            if (angle <= cornerAngle) {
                // intersects top wall
                dist = (h-y)/Math.cos(angle);
            } else {
                dist = (w-x)/Math.cos(Math.PI/2.0-angle);
            }
        } else if (headingDegrees >= 90 && headingDegrees < 180) {
            cornerAngle = VectorUtils.toAngle(VectorFloat64.valueOf(w-x,0-y));
            if (angle <= cornerAngle) {
                dist = (w-x)/Math.cos(Math.cos(angle-Math.PI/2.0));
            } else {
                dist = (y)/Math.cos(Math.PI-angle);
            }
        } else if (headingDegrees >= 180 && headingDegrees < 270) {
            cornerAngle = VectorUtils.toAngle(VectorFloat64.valueOf(-x,-y));
            if (angle <= cornerAngle) {
                dist = (y)/Math.cos(angle-Math.PI);
            } else {
                dist = (x)/Math.cos(3.0*Math.PI/2.0-angle);
            }
        } else {
            cornerAngle = VectorUtils.toAngle(VectorFloat64.valueOf(-x,h-y));
            if (angle <= cornerAngle) {
                dist = -x/Math.cos(angle - 3.0*Math.PI/2.0);
            } else {
                dist = (h-y)/Math.cos(2.0*Math.PI-angle);
            }
        }

//        System.err.println("(" + x + "," + y + ")");
//        System.err.println("head  : " + headingDegrees);
//        System.err.println("angle : " + Math.toDegrees(angle));
//        System.err.println("corner: " + Math.toDegrees(cornerAngle));
//        System.err.println("dist  : " + dist);

        if (clampZero(dist - robotRadius) < 0.0) {
            return dir;
        } else {
            return dir.times(Float64.valueOf(clampZero(dist - robotRadius)));
        }
    }


    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        scannedEnemy = true;
        scannedEnemyAge = 0;
        double bearing = event.getBearingRadians();
	    bearing = Stuff.modHeading(bearing + getHeadingRadians());
        double dist = clampZero(event.getDistance()) - getWidth();
        enemyHeading = event.getHeadingRadians();
        enemySpeed = event.getVelocity();
        enemyEnergy = event.getEnergy();

        // calculate a vector to the enemy
        vectorToEnemy = VectorUtils.vecFromDir(bearing);
        if (dist > 0.0) {
            vectorToEnemy = vectorToEnemy.times(Float64.valueOf(dist));
        }
//        System.err.println("vec to enemy: " + vectorToEnemy);
//        System.err.println("dist to enemy: " + dist);
//        System.err.println("remaining move: " + getDistanceRemaining());
    }

    public void onHitRobot(HitRobotEvent event) {
        super.onHitRobot(event);
        rammedAge = 0;
        myFault = event.isMyFault();
        rammerBearing = event.getBearingRadians();
        System.err.println("hit robot (" + ((myFault) ? "my fault" : "not at fault") + ")");
    }

    public void onHitByBullet(HitByBulletEvent event) {
        super.onHitByBullet(event);
        bulletHitAge = 0;
        recentlyHitByBullet = true;
        bulletEnergy = event.getPower();
        bulletBearing = event.getBearingRadians();
        bulletSpeed = event.getVelocity();
    }

    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        System.err.println("hit wall");
        wallHitAge = 0;
        recentlyHitWall = true;
        System.err.println("dist to forward wall: " + VectorUtils.vecLength(getVectorToForwardWall()));
        System.err.println("dist to nearest wall: " + VectorUtils.vecLength(getVectorToNearWall()));                
    }

    public void onRobotDeath(RobotDeathEvent event) {
        super.onRobotDeath(event);
        alive = false;
    }

    public void onWin(WinEvent event) {
        super.onWin(event);
        alive = false;
    }

}
