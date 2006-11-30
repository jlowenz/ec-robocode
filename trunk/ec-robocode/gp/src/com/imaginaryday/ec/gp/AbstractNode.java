package com.imaginaryday.ec.gp;

import static com.imaginaryday.util.Collections.toList;
import com.imaginaryday.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: jlowens
 * Date: Oct 28, 2006
 * Time: 4:22:09 PM
 */
public abstract class AbstractNode implements Node {
    private static final long serialVersionUID = 5193080878842310275L;
    private static final AtomicLong ID = new AtomicLong(0);

    protected transient Object owner;
    static protected Node[] NONE = new Node[0];
    protected abstract Node[] children();
    protected Tuple.Two<Class,List<Class>> type;
    protected long id = ID.getAndIncrement();

    public <T extends Node> T copy() {
        // TODO: solve this with a new abstract method (e.g. create)
        try {
            //noinspection unchecked
            T root = (T)getClass().newInstance();
            copyChildren(root);
            return root;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
        }
        return null;
    }

    protected void copyChildren(Node n) throws VetoTypeInduction {
        int i = 0;
        for (Node c : children()) {
            n.attach(i++, c.copy());
        }
    }

    public void setOwner(Object owner) {
        if (this.owner == null || !this.owner.equals(owner)) {
            this.owner = owner;
		    for (Node n : children()) n.setOwner(this.owner);
        }
    }

    public Object getOwner() {
        return owner;
    }

    public Node getChild(int i) {
        return children()[i];
    }
    public List<Node> childList() {
        return toList(children());
    }

    public void induceOutputType(Class type) throws VetoTypeInduction {}

    public Node attach(int id, Node n) throws VetoTypeInduction {
        n.induceOutputType(getInputType(id));
        children()[id] = n;
        return this;
    }

    public List<Class> getInputTypes() {
        List<Class> types = new ArrayList<Class>();
        for (int i = 0; i < getInputCount(); i++) types.add(getInputType(i));
        return types;
    }

    public Tuple.Two<Class,List<Class>> getType() {
        if (type == null) type = Tuple.pair(getOutputType(), getInputTypes());
        return type;
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getName()).append(" ");
	    Node children[] = children();
	    for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                sb.append(children[i].toString());
                if (i < children.length-1) sb.append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    public String toStringEval() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getName()).append(" ");
	    Node children[] = children();
	    for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                sb.append(((AbstractNode)children[i]).toStringEval());
                if (i < children.length-1) sb.append(" ");
            }
        }
        sb.append(")");
        try {
            return sb.toString() + " ==> " + evaluate();
        } catch (Throwable e) {
            return sb.toString() + " ******* ";
        }
    }

    public String toCodeString(Set<Class> imports) {
        StringBuilder sb = new StringBuilder();
        imports.add(getClass());
        sb.append("new ").append(getClass().getSimpleName()).append("(").append(getConstructorParam()).append(")");
        for (int i = 0; i < getInputCount(); i++) {
            sb.append(".attach(").append(i).append(",").append(((AbstractNode)getChild(i)).toCodeString(imports)).append(")");
        }
        return sb.toString();
    }


    public String debugString(Set<Node> set) {
        StringBuilder sb = new StringBuilder();
        if (set.contains(this)) {
            sb.append("( ####### CYCLE: ").append(id).append(" ####### )");
            return sb.toString();
        } else {
            set.add(this);
            sb.append("(").append(getName()).append(" ").append(id).append(" ");
            Node children[] = children();
            for (int i = 0; i < children.length; i++) {
                if (children[i] != null) {
                    sb.append(children[i].debugString(set));
                    if (i < children.length-1) sb.append(" ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
    protected String getConstructorParam() { return ""; }


	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractNode that = (AbstractNode) o;

		if (id != that.id) return false;

		return true;
	}

	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}
