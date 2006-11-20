package com.imaginaryday.util;

import java.util.List;
import java.util.ArrayList;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 15, 2006<br>
 * Time: 9:19:20 PM<br>
 * </b>
 */
public class Stuff {
    public static double ZERO_TOLERANCE = 0.000001;
    public static double MIN_REASONABLE = 0.000001;
    public static double MAX_REASONABLE = 1.0e6;

    public static boolean nearZero(double val) {
        return java.lang.Math.abs(val) < ZERO_TOLERANCE;
    }

    public static double clampZero(double val) {
        return (nearZero(val)) ? 0.0 : val;
    }

    public static double modHeading(double v) {
        if (v > 2*Math.PI) v = v - Math.floor(v / 2*Math.PI)*2*Math.PI;
        else if (v < -2*Math.PI) v = v - Math.ceil(v / 2*Math.PI)*2*Math.PI;
        if (v < 0.0) return 2*Math.PI + v;
        return v;
    }

    public static boolean isReasonable(double val) {
        double d = Math.abs(val);
        return d == 0.0 || (d > MIN_REASONABLE && d < MAX_REASONABLE);
    }

    public static <T> List<T> slice(int i, T[] args) {
        List<T> l = new ArrayList<T>();
        for (int j = i; j < args.length; j++) {
            l.add(args[j]);
        }
        return l;
    }

}
