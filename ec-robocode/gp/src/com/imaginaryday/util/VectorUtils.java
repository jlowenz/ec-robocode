package com.imaginaryday.util;

import static com.imaginaryday.util.Stuff.clampZero;
import org.jscience.mathematics.numbers.Float64;
import org.jscience.mathematics.vectors.VectorFloat64;

import java.util.Random;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 3:11:32 PM<br>
 * </b>
 */
public class VectorUtils {
    private static final Random rand = new Random();
    public static double toAngle(VectorFloat64 movementVector2) {
        VectorFloat64 norm = movementVector2.times(Float64.valueOf(1.0 / movementVector2.normValue()));
        final double x = norm.getValue(0);
        final double y = norm.getValue(1);
        assert !(Double.isNaN(x) || Double.isInfinite(x)) : "x is bad! from: " + norm;
        assert !(Double.isNaN(y) || Double.isInfinite(y)) : "y is bad! from: " + norm;
        if (x >= 0) {
            if (y >= 0) {
                return clampZero(Math.atan2(x, y));
            } else {
                return clampZero(Math.PI - Math.atan2(x, -y));
            }
        } else {
            if (y >= 0) {
                return clampZero(2 * Math.PI - Math.atan2(-x, y));
            } else {
                return clampZero(Math.PI + Math.atan2(-x, -y));
            }
        }
    }

    public static VectorFloat64 vecFromDir(double headingRadians) {
        return VectorFloat64.valueOf(clampZero(Math.sin(Stuff.modHeading(headingRadians))), clampZero(Math.cos(Stuff.modHeading(headingRadians))));
    }

    public static double vecLength(VectorFloat64 vec) {
        return clampZero(vec.normValue());
    }

    public static VectorFloat64 normalize(VectorFloat64 vec) {
        return vec.times(Float64.valueOf(1.0/vec.normValue()));
    }

    private static double filterZero(double v) {
        return (Stuff.nearZero(v)) ? 0.0 : v;
    }

    public static VectorFloat64 randomVector() {
        VectorFloat64 val = VectorFloat64.valueOf(filterZero(rand.nextInt(3)-1), filterZero(rand.nextInt(3)-1));
        while (val.getValue(0) == 0.0 && val.getValue(1) == 0.0) {
            val = VectorFloat64.valueOf(filterZero(rand.nextInt(3)-1), filterZero(rand.nextInt(3)-1));
        }
        val = val.times(Float64.valueOf(1.0/val.normValue()));
        return val;
    }
}
