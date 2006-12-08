package com.imaginaryday.sim;

import com.imaginaryday.sim.geometry.BoundingBox;
import org.jscience.mathematics.vectors.Vector;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 7, 2006<br>
 * Time: 5:55:56 PM<br>
 * </b>
 */
public interface Entity {
    BoundingBox getBoundingBox();
    Vector getPosition();
    Vector getOrientation();
    boolean collides(Entity e);
    void doTimeStep(Environment env);
}
