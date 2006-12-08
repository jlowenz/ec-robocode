package com.imaginaryday.sim;

import java.util.List;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 7, 2006<br>
 * Time: 5:51:36 PM<br>
 * </b>
 */
public interface Environment {
    void addEntity(Entity e);
    void removeEntity(Entity e);

    List<Entity> getAllEntities();

}
