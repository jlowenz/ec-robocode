package com.imaginaryday.ec.gp;

/**
 * User: jlowens
 * Date: Oct 28, 2006
 * Time: 4:22:09 PM
 */
public abstract class AbstractNode implements Node {

	protected transient Object owner;
    protected abstract Node[] children();

	public void setOwner(Object owner) {
        if (this.owner == null || !this.owner.equals(owner)) {
            this.owner = owner;
		    for (Node n : children()) n.setOwner(this.owner);
        }
    }

    public Object getOwner() {
        return owner;
    }

    public void induceOutputType(Class type) throws VetoTypeInduction {}

    public Node attach(int id, Node n) throws VetoTypeInduction {
        n.induceOutputType(getOutputType());
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

    public boolean isTerminal() {
        return getInputCount() == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getName()).append(" ");
	    Node children[] = children();
	    for (int i = 0; i < children.length; i++) {
            sb.append(children[i].toString());
		    if (i < children.length-1) sb.append(" ");
	    }
        sb.append(")");
        return sb.toString();
    }
}
