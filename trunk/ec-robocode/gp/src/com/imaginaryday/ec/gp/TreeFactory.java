package com.imaginaryday.ec.gp;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 29, 2006<br>
 * Time: 12:22:25 PM<br>
 * </b>
 */
public class TreeFactory {
    private NodeFactory nf;

    public TreeFactory(NodeFactory nf) {
        this.nf = nf;
    }

    public Node generateRandomTree() {
        return null;
    }

    /**
     * Grow a tree with the specified maxDepth - depth.
     * Based on Koza's method: "ramped half-and-half".
     *
     * @param depth         the "start" depth of this tree
     * @param maxDepth      the absolute "end" depth of this tree
     * @param parentType    the type of output the root node must provide. null is a wildcard (i.e. any output)
     * @return a randomly generated strongly-typed tree up to the maxDepth - depth
     */
    public Node grow(int depth, int maxDepth, Class parentType) {
        if (depth == maxDepth) return nf.randomTerminal(parentType);
        else {
            Node n = nf.randomNode(parentType);
            if (n.isTerminal()) return n;
            else {
                for (int i = 0; i < n.getInputCount(); i++) {
                    Class type = n.getInputType(i);
                    Node child = grow(depth+1, maxDepth, type);
                    n.attach(i, child);
                }
                return n;
            }
        }
    }
}
