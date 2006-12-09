package ec;

import com.imaginaryday.ec.gp.AbstractNode;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.gp.nodes.Add;
import com.imaginaryday.ec.gp.nodes.And;
import com.imaginaryday.ec.gp.nodes.BooleanConstant;
import com.imaginaryday.ec.gp.nodes.Constant;
import com.imaginaryday.ec.gp.nodes.GreaterThan;
import com.imaginaryday.ec.gp.nodes.IfThenElse;
import com.imaginaryday.ec.gp.nodes.LessThan;
import com.imaginaryday.ec.gp.nodes.Not;
import com.imaginaryday.ec.main.nodes.DirectionPair;
import com.imaginaryday.ec.main.nodes.DotProduct;
import com.imaginaryday.ec.main.nodes.EnemyHeading;
import com.imaginaryday.ec.main.nodes.FiringPair;
import com.imaginaryday.ec.main.nodes.MakeDirectionPair;
import com.imaginaryday.ec.main.nodes.MakeFiringPair;
import com.imaginaryday.ec.main.nodes.Rammed;
import com.imaginaryday.ec.main.nodes.RammedAge;
import com.imaginaryday.ec.main.nodes.ScannedEnemy;
import com.imaginaryday.ec.main.nodes.ScannedEnemyAge;
import com.imaginaryday.ec.main.nodes.VectorFromHeading;
import com.imaginaryday.ec.main.nodes.VectorHeading;
import com.imaginaryday.ec.main.nodes.VectorLength;
import com.imaginaryday.ec.main.nodes.VectorToEnemy;
import com.imaginaryday.util.Stuff;
import com.imaginaryday.util.Tuple;
import com.imaginaryday.util.VectorUtils;
import javolution.context.PoolContext;
import org.jscience.mathematics.numbers.Float64;
import org.jscience.mathematics.vectors.VectorFloat64;
import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.SkippedTurnEvent;
import robocode.WinEvent;

import java.util.logging.Logger;

/**
 * User: jlowens Date: Oct 30, 2006 Time: 6:13:08 PM
 */
public class ECJAgent extends AdvancedRobot {
    private static Logger log = Logger.getLogger(ECJAgent.class.getName());
// comment out because robocode is a piece of shit
//    static {
//        Handler h = new ConsoleHandler();
//        h.setLevel(Level.FINEST);
//        GPAgent.log.addHandler(h);
//        GPAgent.log.setLevel(Level.FINEST);
//    }

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
    private boolean scannedEnemy = false;
    private static final int BULLET_HIT_AGE_LIMIT = 50;
    private static final int WALL_HIT_AGE_LIMIT = 50;
    private static final int RAMMED_AGE_LIMIT = 50;
    private static final int SCANNED_AGE_LIMIT = 50;
    private boolean forward;

    public ECJAgent() {
        radarTree = initRadarTree();
        turretTree = initTurretTree();
        firingTree = initFiringTree();
        directionTree = initDirectionTree();

        if (radarTree != null) this.radarTree.setOwner(this);
        if (turretTree != null) this.turretTree.setOwner(this);
        if (firingTree != null) this.firingTree.setOwner(this);
        if (directionTree != null) this.directionTree.setOwner(this);
    }

    protected Node initDirectionTree() {
        return new MakeDirectionPair()
                .attach(0,new VectorToEnemy())
                .attach(1,new IfThenElse()
                        .attach(0,new Not().attach(0,new Rammed()))
                        .attach(1,new BooleanConstant(true))
                        .attach(2,new IfThenElse()
                                .attach(0,new LessThan()
                                            .attach(0,new RammedAge())
                                            .attach(1,new Constant(10)))
                                .attach(1,new BooleanConstant(false))
                                .attach(2,new BooleanConstant(true))));
    }

    protected Node initFiringTree() {
        return new MakeFiringPair()
                .attach(0, new And()
                            .attach(0, new ScannedEnemy())
                            .attach(1, new LessThan()
                                        .attach(0,new VectorLength().attach(0, new VectorToEnemy()))
                                        .attach(1,new Constant(100))))
                .attach(1, new Constant(3));
    }

    protected Node initTurretTree() {
        return new VectorHeading().attach(0,new VectorToEnemy());
    }
    protected Node initRadarTree() {
        return new IfThenElse()
                .attach(0, new Not().attach(0,new ScannedEnemy()))
                .attach(1, new Constant(6*Math.PI))
                .attach(2, new IfThenElse()
                    .attach(0, new GreaterThan()
                                .attach(0,new ScannedEnemyAge())
                                .attach(1,new Constant(2)))
                    .attach(1, new Add()
                                .attach(0,new VectorHeading().attach(0,new VectorToEnemy()))
                                .attach(1,new IfThenElse()
                                            .attach(0,new GreaterThan()
                                                        .attach(0,new DotProduct()
                                                                    .attach(0, new VectorFromHeading()
                                                                                .attach(0,new EnemyHeading()))
                                                                    .attach(1, new VectorToEnemy()))
                                                        .attach(1,new Constant(0)))
                                            .attach(1,new Constant(0.1))
                                            .attach(2,new Constant(-0.1))))
                    .attach(2, new VectorHeading().attach(0,new VectorToEnemy())));
    }

    public ECJAgent(Node radarTree, Node turretTree, Node firingTree, Node directionTree) {
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
    public boolean isGoingForward() {
        return forward;
    }


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

        public static <A, B> ECJAgent.p<A, B> n(A a, B b) {
            return new ECJAgent.p<A, B>(a, b);
        }
    }

    private <T> T argmin(ECJAgent.p<Double, T>... pair) {
        ECJAgent.p<Double, T> _min = pair[0];
        for (ECJAgent.p<Double, T> m : pair) {
            if (m.first < _min.first) {
                _min = m;
            }
        }
        return _min.second;
    }

    private <T> ECJAgent.p<Double, T> pairmin(ECJAgent.p<Double, T>... ps) {
        ECJAgent.p<Double, T> _min = ps[0];
        for (ECJAgent.p<Double, T> m : ps) {
            if (m.first < _min.first) _min = m;
        }
        return _min;
    }

    @SuppressWarnings("unchecked")
    public void run() {
        log.fine("run()");
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        log.fine("starting pseudo-infinite loop");
        while (alive) {
            try {
                PoolContext.enter();
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
                switch (argmin(ECJAgent.p.n(x, ECJAgent.Wall.LEFT), ECJAgent.p.n(y, ECJAgent.Wall.BOTTOM), ECJAgent.p.n(toRight, ECJAgent.Wall.RIGHT), ECJAgent.p.n(toTop, ECJAgent.Wall.TOP))) {
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
                Tuple.Two<Boolean, Number> firing;

                try {
                    radarDirection = ((Number) radarTree.evaluate()).doubleValue();
                } catch (Throwable e) {
                    e.printStackTrace();
                    log.finest(((AbstractNode) radarTree).toStringEval());
                }

                // get absolute turret heading
                try {
                    turretDirection = ((Number) turretTree.evaluate()).doubleValue();
                } catch (Throwable e) {
                    e.printStackTrace();
                    log.finest(((AbstractNode) turretTree).toStringEval());
                }

                // determine if we need to fire
                try {
                    firing = (FiringPair) firingTree.evaluate();
                } catch (Throwable t ) {
                    t.printStackTrace();
                    log.finest(((AbstractNode) firingTree).toStringEval());
                    firing = new FiringPair(false, 0.0);
                }

                // get absolute robot heading and velocity
                DirectionPair movementPair = null;
                try {
                    movementPair = (DirectionPair) directionTree.evaluate();
                } catch (Throwable t ) {
	                t.printStackTrace();
                    log.finest(((AbstractNode) directionTree).toStringEval());
                }

                // process firing directive
                if (firing.getFirst()) {
                    setFire(firing.getSecond().doubleValue());
                }

                // process radar directive
                ECJAgent.p<Double, Turn> r = calculateTurn(getRadarHeadingRadians(), radarDirection);
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

	            double heading = VectorUtils.toAngle(movementPair.first());
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
                double dist = Stuff.clampZero(movementPair.first().normValue());
                if (dist > 1.0) {
                    if (movementPair.second()) {
                        setAhead(dist);
                        forward = true;
                    } else {
                        setBack(dist);
                        forward = false;
                    }
                }

                if (bulletHitAge > BULLET_HIT_AGE_LIMIT) resetBulletHit();
                if (wallHitAge > WALL_HIT_AGE_LIMIT) resetWallHit();
                if (rammedAge > RAMMED_AGE_LIMIT) resetRammed();
                if (scannedEnemyAge > SCANNED_AGE_LIMIT) resetScanned();

                if (recentlyHitByBullet) bulletHitAge++;
                if (recentlyHitWall) wallHitAge++;
                if (recentlyRammed) rammedAge++;
                if (scannedEnemy) scannedEnemyAge++;

            } catch (Throwable e) {
                e.printStackTrace();
                log.severe("EXCEPTION in GPAgent: " + e.toString());
            } finally {
                try {
                    execute();
                } catch (Exception e) { alive = false; }
                PoolContext.exit();
            }
        }
    }
    private void resetScanned() {
        scannedEnemyAge = 0;
        scannedEnemy = false;
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


    private ECJAgent.p<Double, Turn> calculateTurn(final double current, final double dest) {
        return (current > dest) ?
                pairmin(ECJAgent.p.n(Stuff.clampZero(current - dest), ECJAgent.Turn.LEFT), ECJAgent.p.n(Stuff.clampZero(2 * Math.PI - current + dest), ECJAgent.Turn.RIGHT)) :
                pairmin(ECJAgent.p.n(Stuff.clampZero(2 * Math.PI - dest + current), ECJAgent.Turn.LEFT), ECJAgent.p.n(Stuff.clampZero(dest - current), ECJAgent.Turn.RIGHT));
    }


    private VectorFloat64 vecToWall(double x,
                                    double y,
                                    double w,
                                    double h,
                                    double headingDegrees,
                                    double robotRadius,
                                    VectorFloat64 dir) {

        double dist;
        double angle = VectorUtils.toAngle(dir);
        headingDegrees = Math.toDegrees(angle);
        double cornerAngle;
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

        if (Stuff.clampZero(dist - robotRadius) < 0.0) {
            return dir;
        } else {
            return dir.times(Float64.valueOf(Stuff.clampZero(dist - robotRadius)));
        }
    }


	public void onSkippedTurn(SkippedTurnEvent event) {
		super.onSkippedTurn(event);    //To change body of overridden methods use File | Settings | File Templates.
		System.err.println("SKIPPED TURN!!!!");
	}

	public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        scannedEnemy = true;
        scannedEnemyAge = 0;
        double bearing = event.getBearingRadians();
	    bearing = Stuff.modHeading(bearing + getHeadingRadians());
        double dist = Stuff.clampZero(event.getDistance()) - (getWidth()*.5);
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
        recentlyRammed = true;
        rammedAge = 0;
        myFault = event.isMyFault();
        rammerBearing = event.getBearingRadians();
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
        wallHitAge = 0;
        recentlyHitWall = true;
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
