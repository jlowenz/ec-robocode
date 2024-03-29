package com.imaginaryday.ec.gp;

import java.util.Random;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 29, 2006<br>
 * Time: 12:22:25 PM<br>
 * </b>
 */
public class TreeFactory {
    private NodeFactory nf;
	private Random rand = new Random();

    public TreeFactory(NodeFactory nf) {
        this.nf = nf;
    }

    public Node generateRandomTree(int maxDepth, Class parentType) {
	    Node root;
	    try {
			if (rand.nextBoolean()) {
			    root = grow(0, maxDepth, parentType);
			} else {
			    root = full(0, maxDepth, parentType);
			}
	        return root;
		} catch (VetoTypeInduction e) {	
			e.printStackTrace();
		}
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
     * @throws VetoTypeInduction 
     */
    public Node grow(int depth, int maxDepth, Class parentType) throws VetoTypeInduction {
	    return _grow(depth, maxDepth, parentType, false);
    }

	private Node _grow(int depth, int maxDepth, Class parentType, boolean full) throws VetoTypeInduction
	{
		if (depth == maxDepth) return nf.randomTerminal(parentType);
		else {
		    Node n = (full) ? nf.randomNonterminal(parentType) : nf.randomNode(parentType);
            assert n.getOutputType() != null : "output type is NULL!";
            if (n.isTerminal()) return n;
		    else {
		        for (int i = 0; i < n.getInputCount(); i++) {
		            Class type = n.getInputType(i);
		            Node child = _grow(depth+1, maxDepth, type, full);
                    try {
                        n.attach(i, child);
                    } catch (VetoTypeInduction vetoTypeInduction) {
                        System.err.println(n);
                        System.err.println(child);
                        vetoTypeInduction.printStackTrace();
                        throw vetoTypeInduction;
                    }
                }
		        return n;
		    }
		}
	}

	/**
	 * Grow a full tree
	 * @throws VetoTypeInduction 
	 */
	public Node full(int depth, int maxDepth, Class parentType) throws VetoTypeInduction
	{
		return _grow(depth, maxDepth, parentType, true);
	}
}
