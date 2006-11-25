package com.imaginaryday.ec.main.nodes;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.RoboNode;
import com.imaginaryday.util.Stuff;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 9, 2006<br>
 * Time: 10:48:31 PM<br>
 * </b>
 */
public class RammerBearing extends RoboNode {
    private static final long serialVersionUID = 1684284567021442484L;
    protected Node[] children() {return NONE;}
    public String getName() {return "rammerBearing";}
    public Class getInputType(int id) {return null;}
    public Class getOutputType() {return Number.class;}
    public Object evaluate() {
        double bearing = Stuff.clampZero(getOwner().getRammerBearing());
        assert !(Double.isNaN(bearing) || Double.isInfinite(bearing)) : "rammer bearing was bad!";
        return bearing;
    }
}
