package com.imaginaryday.util;

import net.jini.core.entry.Entry;

/**
 * @author Ronald A. Bowers
 * @version 1.0
 */
public class PoisonPill implements Entry {
    public String id;

    public PoisonPill() {
        
    }

    public PoisonPill(String id) {
        this.id = id;
    }

}
