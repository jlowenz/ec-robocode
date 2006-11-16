package com.imaginaryday.util;

import static com.imaginaryday.util.Stuff.clampZero;
import org.jscience.mathematics.vectors.VectorFloat64;
import org.jscience.mathematics.numbers.Float64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 3:11:32 PM<br>
 * </b>
 */
public class VectorUtils {
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
        return VectorFloat64.valueOf(clampZero(Math.sin(headingRadians)), clampZero(Math.cos(headingRadians)));
    }

    public static double vecLength(VectorFloat64 vec) {
        return clampZero(vec.normValue());
    }

    public static VectorFloat64 normalize(VectorFloat64 vec) {
        return vec.times(Float64.valueOf(1.0/vec.normValue()));
    }
}
