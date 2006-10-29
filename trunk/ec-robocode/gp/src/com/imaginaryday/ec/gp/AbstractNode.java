package com.imaginaryday.ec.gp;

/**
 * User: jlowens
 * Date: Oct 28, 2006
 * Time: 4:22:09 PM
 */
public abstract class AbstractNode implements Node {

    protected abstract Node[] children();

    public Node attach(int id, Node n) {
        children()[id] = n;
        return this;
    }

    public Class[] getInputTypes() {
        Class[] types = new Class[getInputCount()];
        for (int i = 0; i < getInputCount(); i++) types[i] = getInputType(i);
        return types;
    }

    public int getInputCount() {
        return children().length;
    }

    public Object evaluate() {
        throw new RuntimeException("Unimplemented evaluate() method!");
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getName()).append(" ");
        for (Node n : children()) sb.append(sb.toString()).append(" ");
        sb.append(")");
        return sb.toString();
    }
}
