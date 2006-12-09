package com.imaginaryday.ec.ecj;

import com.imaginaryday.ec.main.nodes.DirectionPair;
import com.imaginaryday.ec.main.nodes.FiringPair;
import ec.gp.GPData;
import org.jscience.mathematics.vectors.VectorFloat64;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 11:40:22 AM<br>
 * </b>
 */
public class PolyData extends GPData {
    public double d;
    public boolean b;
    public VectorFloat64 v;
    public DirectionPair dp;
    public FiringPair fp;


    public GPData copyTo(final GPData gpd) {
        PolyData pd = (PolyData) gpd;
        pd.d = d;
        pd.b = b;
        pd.v = v;
        pd.dp = dp;
        pd.fp = fp;
        return pd;
    }
}
