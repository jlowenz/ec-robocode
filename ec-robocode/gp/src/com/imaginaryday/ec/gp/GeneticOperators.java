package com.imaginaryday.ec.gp;

import info.javelot.functionalj.*;
import info.javelot.functionalj.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 6:17:22 PM<br>
 * </b>
 */
public class GeneticOperators {
    private Random rand = new Random();
    private int mutationProbability = 10;
    private int crossoverProbability = 60;
    private int copyProbability = 10;
    

    private static GeneticOperators _instance = new GeneticOperators();

    protected GeneticOperators() {}

    public static GeneticOperators getInstance() { return _instance; }

    public int getCopyProbability() {
        return copyProbability;
    }
    public void setCopyProbability(int copyProbability) {
        this.copyProbability = copyProbability;
    }
    public  int getMutationProbability() {
        return mutationProbability;
    }
    public  void setMutationProbability(int mutationProbability) {
        this.mutationProbability = mutationProbability;
    }
    public  int getCrossoverProbability() {
        return crossoverProbability;
    }
    public  void setCrossoverProbability(int crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    public  Pair<Node,Node> crossover(Node parA, Node parB)
    {
        Node childA = parA.copy();
        Node childB = parB.copy();

        link la = randomSubtree(childA);
        link lb = randomSubtree(childB, la.child.getOutputType());

        try {
            swap(la, lb);
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
            // todo: ack what to do with this?
        }

        return new Pair<Node, Node>(childA, childB);
    }

    private void swap(link la, link lb) throws VetoTypeInduction {
        la.parent.attach(la.childIndex, lb.child);
        lb.parent.attach(lb.childIndex, la.child);
    }

    public Node mutate(Node parent)
    {
        return null;
    }


    public  interface Mutation {
        Node mutate(Node parent);
    }

    public  class Grow implements Mutation
    {
        public Node mutate(Node parent) {
            return null;
        }
    }

    public  class Shrink implements Mutation
    {
        public Node mutate(Node parent) {
            return null;
        }
    }

    public  class Cycle implements Mutation
    {
        public Node mutate(Node parent) {
            return null;
        }
    }

    public  class Switch implements Mutation
    {
        public Node mutate(Node parent) {
            return null;
        }
    }

    private link randomSubtree(Node root)
    {
        return selectLink(root, rand.nextInt(countLinks(root)));
    }

    private link randomSubtree(Node root, final Class outputType)
    {
        Function2<Boolean,link,Integer> f = new Function2Impl<Boolean, link, Integer>() {
            public Boolean call(link link, Integer integer) throws FunctionException {
                return link.child.getOutputType().equals(outputType);
            }
        };
        List<link> l = new ArrayList<link>();
        filterLinks(l, root, 0, f);
        return l.get(rand.nextInt(l.size()));
    }


    private static class link {
        public Node parent;
        public int childIndex;
        public Node child;

        public link(Node parent, int index, Node child) {
            this.parent = parent;
            this.child = child;
            this.childIndex = index;
        }
    }

    private link selectLink(Node parent, final int i) {
        Function2<Boolean,link,Integer> f = new Function2Impl<Boolean, link, Integer>() {
            public Boolean call(link link, Integer integer) throws FunctionException {
                return i == integer;
            }
        };
        List<link> l = new ArrayList<link>();
        filterLinks(l, parent, 0, f);
        return l.get(0);
    }

    private int filterLinks(List<link> l, Node parent, int count, Function2<Boolean,link,Integer> pred) {
        int id = count;
        int i = 0;
        for (Node n : parent.childList()) {
            link link = new link(parent, i++, n);
            if (pred.call(link, id)) l.add(link);
            id = filterLinks(l, n, id + 1, pred);
        }
        return id;
    }

    private int countLinks(Node root) {
        int i = 0;
        if (root != null) {
            for (int j = 0; j < root.getInputCount(); j++) {
                i++;
                i += countLinks(root.getChild(j));
            }
        }
        return i;
    }
}
