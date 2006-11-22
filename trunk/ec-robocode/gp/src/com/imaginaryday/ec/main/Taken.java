package com.imaginaryday.ec.main;

import net.jini.core.entry.Entry;

/**
 * @author Ronald A. Bowers
 * @version 1.0
 */
public class Taken implements Entry {
    public String id;
    public Integer generation;
    public Integer battle;

    public Taken() {
        this(null, null, null);
    }

    public Taken(String id, Integer generation, Integer battle) {
        this.id = id;
        this.generation = generation;
        this.battle = battle;
    }

}
