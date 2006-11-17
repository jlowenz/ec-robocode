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


    public String toString() {
        return "PoisonPill{" +
                "id='" + id + '\'' +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PoisonPill that = (PoisonPill) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }
}
