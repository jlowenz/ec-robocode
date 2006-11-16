package com.imaginaryday.ec.gp;

import info.javelot.functionalj.*;
import info.javelot.functionalj.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 6:17:22 PM<br>
 * </b>
 */
public class GeneticOperators {
    private static Logger log = Logger.getLogger(GeneticOperators.class.getName());
    private static Random rand = new Random();
    private int mutationProbability = 10;
    private int crossoverProbability = 60;
    private int copyProbability = 10;
    private int numCrossoverFailures = 0;

    private static GeneticOperators _instance = new GeneticOperators();

    protected GeneticOperators() {
    }

    public static GeneticOperators getInstance() {
        return _instance;
    }

    public int getCopyProbability() {
        return copyProbability;
    }

    public void setCopyProbability(int copyProbability) {
        this.copyProbability = copyProbability;
    }

    public int getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(int mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public int getCrossoverProbability() {
        return crossoverProbability;
    }

    public void setCrossoverProbability(int crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    public int getNumCrossoverFailures() {
        return numCrossoverFailures;
    }

    private void notifyCrossoverFailed(Node a, Node b) {
        numCrossoverFailures++;
        log.warning("could not crossover nodes: ");
        log.warning("A: " + a);
        log.warning("B: " + b);
    }

    // argh - there must be a pattern to handle this situation - if only I had simple anonymous functions / closures!
    public Pair<Node, Node> crossover(Node parA, Node parB) {
        if (parA == null || parB == null) {
            notifyCrossoverFailed(parA, parB);
            return new Pair<Node,Node>(parA, parB);
        }
        Node childA = parA.copy();
        Node childB = parB.copy();

        Link la = null, lb = null;
        for (int i = 0; i < 3; i++) {
            la = randomSubtree(childA); // this may return EMPTY
            if (la == Link.EMPTY) { la = null; break; }
            lb = randomSubtree(childB, la.child.getOutputType()); // this may return EMPTY
            if (lb != Link.EMPTY) break;
        }
        if (la == Link.EMPTY || lb == Link.EMPTY) {
            notifyCrossoverFailed(childA, childB);
            return new Pair<Node, Node>(childA, childB);
        }

        try {
            swap(la, lb);
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
            // todo: ack what to do with this?
        }

        return new Pair<Node, Node>(childA, childB);
    }

    private static void swap(Link la, Link lb) throws VetoTypeInduction {
        la.parent.attach(la.childIndex, lb.child);
        lb.parent.attach(lb.childIndex, la.child);
    }

    private enum Mutation {
        GROW(new Grow()), SHRINK(new Shrink()), CYCLE(new Cycle()), SWITCH(new Switch());
        private static Random rand = new Random();
        private MutationFunc func;

        private Mutation(MutationFunc f) {
            func = f;
        }

        public MutationFunc get() {
            return func;
        }

        public static Mutation randomMutation() {
            return values()[rand.nextInt(values().length)];
        }
    }

    public Node mutate(Node parent) {
        Node child = parent.copy();
        Mutation.randomMutation().get().mutate(child);
        return child;
    }


    public interface MutationFunc {
        void mutate(Node node);
    }

    private static TreeFactory tf = new TreeFactory(NodeFactory.getInstance());

    public static class Grow implements MutationFunc {
        public void mutate(Node node) {
            Link link = randomSubtree(node);
            if (link == Link.EMPTY) return;
            try {
                Node newSubtree = tf.grow(0, treeDepth(link.parent) + 1, link.child.getOutputType());
                link.parent.attach(link.childIndex, newSubtree);
            } catch (VetoTypeInduction vetoTypeInduction) {
                vetoTypeInduction.printStackTrace();
            }
        }
    }

    public static class Shrink implements MutationFunc {
        public void mutate(Node node) {
            Link link = randomSubtree(node);
            if (link == Link.EMPTY) return;
            try {
                Node newSubtree = tf.grow(0, treeDepth(link.parent) - 1, link.child.getOutputType());
                link.parent.attach(link.childIndex, newSubtree);
            } catch (VetoTypeInduction vetoTypeInduction) {
                vetoTypeInduction.printStackTrace();
            }
        }
    }

    public static class Cycle implements MutationFunc {
        public void mutate(Node node) {
            Link link = randomSubtree(node);
            if (link == Link.EMPTY) return;
            try {
                NodeFactory nf = NodeFactory.getInstance();
                List<Node> children = link.child.childList();
                Node replacement = nf.randomReplacement(link.child);
                if (replacement != null) {
                    int i = 0;
                    for (Node c : children)
                        replacement.attach(i++, c);
                }
                link.parent.attach(link.childIndex, replacement);
            } catch (VetoTypeInduction vetoTypeInduction) {
                vetoTypeInduction.printStackTrace();
            }
        }
    }

    public static class Switch implements MutationFunc {
        public void mutate(Node node) {
            for (int i = 0; i < 3; i++) {
                Link la = randomSubtree(node);
                if (la == Link.EMPTY) return;
                Link lb = randomSubtree(node, la.child.getOutputType());
                if (lb == Link.EMPTY) continue;
                if (!la.equals(lb)) {
                    try {
                        swap(la, lb);
                    } catch (VetoTypeInduction vetoTypeInduction) {
                        vetoTypeInduction.printStackTrace();
                    }
                }
            }
        }
    }

    private static Link randomSubtree(Node root) {
        int links = countLinks(root);
        if (links == 0) return Link.EMPTY;
        return selectLink(root, rand.nextInt(countLinks(root)));
    }

    private static Link randomSubtree(Node root, final Class outputType) {
        Function2<Boolean, Link, Integer> f = new Function2Impl<Boolean, Link, Integer>() {
            public Boolean call(Link link, Integer integer) throws FunctionException {
                return link.child.getOutputType().equals(outputType);
            }
        };
        List<Link> l = new ArrayList<Link>();
        filterLinks(l, root, 0, f);
        if (l.size() == 0) return Link.EMPTY;
        return l.get(rand.nextInt(l.size()));
    }


    private static class Link {
        public static final Link EMPTY = new Link(null, 0, null);
        public Node parent;
        public int childIndex;
        public Node child;

        public Link(Node parent, int index, Node child) {
            this.parent = parent;
            this.child = child;
            this.childIndex = index;
        }

        public boolean equals(Link l) {
            return (parent == l.parent) && (childIndex == l.childIndex) && (child == l.child);
        }
    }

    private static Link selectLink(Node parent, final int i) {
        Function2<Boolean, Link, Integer> f = new Function2Impl<Boolean, Link, Integer>() {
            public Boolean call(Link link, Integer integer) throws FunctionException {
                return i == integer;
            }
        };
        List<Link> l = new ArrayList<Link>();
        filterLinks(l, parent, 0, f);
        return l.get(0);
    }

    private static int filterLinks(List<Link> l, Node parent, int count, Function2<Boolean, Link, Integer> pred) {
        int id = count;
        int i = 0;
        for (Node n : parent.childList()) {
            Link link = new Link(parent, i++, n);
            if (pred.call(link, id)) l.add(link);
            id = filterLinks(l, n, id + 1, pred);
        }
        return id;
    }

    private static int countLinks(Node root) {
        int i = 0;
        if (root != null) {
            for (int j = 0; j < root.getInputCount(); j++) {
                i++;
                i += countLinks(root.getChild(j));
            }
        }
        return i;
    }

    private static int treeDepth(Node root) {
        if (root == null) return 0;
        return _treeDepth(root, 1);
    }

    private static int _treeDepth(Node root, int current) {
        int d = 0;
        for (Node n : root.childList()) {
            d = Math.max(d, _treeDepth(n, current + 1));
        }
        return d;
    }
}
