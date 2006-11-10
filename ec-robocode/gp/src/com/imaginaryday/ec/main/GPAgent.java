package com.imaginaryday.ec.main;

import static com.imaginaryday.util.VectorUtils.toAngle;
import static com.imaginaryday.util.VectorUtils.vecFromDir;
import info.javelot.functionalj.tuple.Pair;
import org.jscience.mathematics.numbers.Float64;
import org.jscience.mathematics.vectors.VectorFloat64;
import robocode.*;

/**
 * User: jlowens Date: Oct 30, 2006 Time: 6:13:08 PM
 */
public class GPAgent extends AdvancedRobot {

    private RoboNode radarTree;
    private RoboNode turretTree;
    private RoboNode firingTree;
    private RoboNode directionTree;
    private VectorFloat64 movementVector = VectorFloat64.valueOf(0, 0);
    private boolean alive = true;
    private VectorFloat64 vectorToEnemy = VectorFloat64.valueOf(0, 0);
    private int scannedEnemyAge;
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
    private VectorFloat64 vectorToForwardWall = VectorFloat64.valueOf(0, 0);
    private VectorFloat64 vectorToNearestWall = VectorFloat64.valueOf(0, 0);
    private int rammedAge;
    private boolean myFault;
    private double rammerBearing;
    private boolean scannedEnemy;

    public GPAgent(RoboNode radarTree, RoboNode turretTree, RoboNode firingTree, RoboNode directionTree) {
        this.radarTree = radarTree;
        this.turretTree = turretTree;
        this.firingTree = firingTree;
        this.directionTree = directionTree;
    }

    public RoboNode getRadarTree() { return radarTree;}
    public RoboNode getTurretTree() {return turretTree;}
    public RoboNode getFiringTree() {return firingTree;}
    public RoboNode getDirectionTree() {return directionTree;}

    public VectorFloat64 getCurrentVector() {return movementVector;}

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


    private static enum Wall { LEFT, BOTTOM, RIGHT, TOP }
    private static enum Turn { LEFT, RIGHT }

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
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        while (alive) {
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
            double robotRadius = Math.sqrt(rheight2 * rheight2 / (rwidth2 * rwidth2));

            // find vector to nearest wall
            switch (argmin(p.n(x, Wall.LEFT), p.n(y, Wall.BOTTOM), p.n(toRight,
                    Wall.RIGHT), p.n(toTop, Wall.TOP))) {
                case LEFT:
                    vectorToNearestWall = VectorFloat64.valueOf(-1, 0).times(Float64.valueOf(x - robotRadius));
                    break;
                case RIGHT:
                    vectorToNearestWall = VectorFloat64.valueOf(1, 0).times(Float64.valueOf(toRight - robotRadius));
                    break;
                case TOP:
                    vectorToNearestWall = VectorFloat64.valueOf(0, 1).times(Float64.valueOf(toTop - robotRadius));
                    break;
                case BOTTOM:
                    vectorToNearestWall = VectorFloat64.valueOf(0, -1).times(Float64.valueOf(y - robotRadius));
                    break;
            }

            // find the vector to the forward wall
            vectorToForwardWall = vecToWall(x, y, bfwidth, bfheight, getHeading(), robotRadius, vecFromDir(getHeadingRadians()));

            // get absolute radar heading
            double radarDirection = (Double) radarTree.evaluate();

            // get absolute turret heading
            double turretDirection = (Double) turretTree.evaluate();

            // determine if we need to fire
            Pair<Boolean, Double> firing = (Pair<Boolean, Double>) firingTree.evaluate();

            // get absolute robot heading and velocity
            movementVector = (VectorFloat64) directionTree.evaluate();

            // process firing directive
            if (firing.getFirst()) {
                setFire(firing.getSecond());
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
            r = calculateTurn(getHeadingRadians(), toAngle(movementVector));
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
            setAhead(movementVector.normValue());

            execute();

            if (bulletHitAge > 100) resetBulletHit();
            if (wallHitAge > 100) resetWallHit();
            if (rammedAge > 100) resetRammed();

            if (recentlyHitByBullet) bulletHitAge++;
            if (recentlyHitWall) wallHitAge++;
            if (recentlyRammed) rammedAge++;
        }
    }
    private void resetRammed() {
        rammedAge = 0; recentlyRammed = false;
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
        return (current < dest) ? pairmin(p.n(current - dest, Turn.LEFT), p.n(2 * Math.PI - current + dest, Turn.RIGHT)) :
                pairmin(p.n(2 * Math.PI - dest + current, Turn.LEFT), p.n(dest - current, Turn.RIGHT));
    }


    private VectorFloat64 vecToWall(double x,
                                    double y,
                                    double w,
                                    double h,
                                    double headingDegrees,
                                    double robotRadius,
                                    VectorFloat64 dir) {
        double m = dir.getValue(1) / dir.getValue(0);
        double tx = 0.0;
        double ty = 0.0;

        if (headingDegrees >= 0 && headingDegrees < 90) {
            // intersect top or right wall
            tx = x / (1.0 - m);
            if (tx > w) { // intersects right wall
                tx = (w - x) / m;
                ty = y / (1.0 - m);
            } else { // intersects top wall
                ty = (h - y) / m;
            }
        } else if (headingDegrees >= 90 && headingDegrees < 180) {
            // intersects right or bottom wall
            ty = y / (1.0 - m);
            if (ty < 0) { // intersects bottom
                tx = x / (1.0 - m);
                ty = -y / m;
            } else { // intersects right
                tx = (w - x) / m;
            }
        } else if (headingDegrees >= 180 && headingDegrees < 270) {
            // intersects bottom or left wall
            ty = -y / m;
            if (ty < 0) { // intersects left wall
                tx = -x / m;
                ty = y / (1.0 - m);
            } else {
                tx = x / (1.0 - m);
            }
        } else {
            // intersects left or top
            ty = y / (1.0 - m);
            if (ty > h) { // intersects top wall
                tx = x / (1.0 - m);
                ty = (h - y) / m;
            } else { // intersects left wall
                tx = -x / m;
            }
        }
        return dir.times(Float64.valueOf(Math.sqrt(tx * tx + ty * ty) - robotRadius));
    }



    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        scannedEnemy = true;
        scannedEnemyAge = 0;
        double bearing = event.getBearingRadians();
        double dist = event.getDistance();
        enemyHeading = event.getHeadingRadians();
        enemySpeed = event.getVelocity();
        enemyEnergy = event.getEnergy();

        // calculate a vector to the enemy
        vectorToEnemy = vecFromDir(bearing).times(Float64.valueOf(dist - (getWidth())));
    }

    public void onHitRobot(HitRobotEvent event) {
        super.onHitRobot(event);
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
