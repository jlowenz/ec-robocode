package com.imaginaryday.ec.gp;

import com.imaginaryday.ec.gp.nodes.BooleanConstant;
import com.imaginaryday.ec.gp.nodes.Constant;
import com.imaginaryday.util.F;
import com.imaginaryday.util.Tuple;
import org.jscience.mathematics.functions.FunctionException;

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
    public static Random rand = new Random();
    private int mutationProbability = 10;
    private int crossoverProbability = 60;
    private int copyProbability = 10;
    private int numCrossoverFailures = 0;

    private List<MutationFunc> mutations = new ArrayList<MutationFunc>();

    private static GeneticOperators _instance = new GeneticOperators();

    protected GeneticOperators() {
        mutations.add(new Cycle());
        mutations.add(new Grow());
        mutations.add(new Shrink());
        mutations.add(new Switch());
        mutations.add(new ConstantMutation());
        mutations.add(new BooleanMutation());
    }

    private static class PR extends AbstractNode {
        private Node[] child = new Node[1];
        public PR(Node root) {
            child[0] = root;
        }
        protected Node[] children() {
            return child;
        }
        @Override
        public <T extends Node> T copy() {
            return (T) new PR(child[0]);
        }
        public String getName() {
            return "PseudoRoot";
        }
        public Class getInputType(int id) {
            return child[0].getOutputType();
        }
        public Class getOutputType() {
            return null;
        }
    }

    public static Node pseudoRoot(final Node root)
    {
        return new PR(root);
    }


    public void addMutation(MutationFunc mf)
    {
        mutations.add(mf);
    }

    public static GeneticOperators getInstance() {
        return _instance;
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
    // TODO: should crossover actually handle single element trees? how?
    public Tuple.Two<Node, Node> crossover(Node parA, Node parB) {
        if (parA == null || parB == null) {
            notifyCrossoverFailed(parA, parB);
            return Tuple.pair(parA, parB);
        }
        Node childA = parA.copy();
        Node childB = parB.copy();

        Link la = null, lb = null;
        for (int i = 0; i < 3; i++) {
            la = randomSubtree(childA);
            lb = randomSubtree(childB, la.child.getOutputType()); // this may return EMPTY
            if (lb != Link.EMPTY) break;
        }
        if (lb == Link.EMPTY) {
            notifyCrossoverFailed(childA, childB);
            return Tuple.pair(childA, childB);
        }
        try {
            swap(la, lb);
        } catch (VetoTypeInduction vetoTypeInduction) {
            vetoTypeInduction.printStackTrace();
            // todo: ack what to do with this?
        }

        return Tuple.pair(childA, childB);
    }

    private static void swap(Link la, Link lb) throws VetoTypeInduction {
        if (la == null || lb == null) throw new IllegalArgumentException("links cannot be null");

        if (la.parent != null) la.parent.attach(la.childIndex, lb.child);
        else
        lb.parent.attach(lb.childIndex, la.child);
    }

    public Node mutate(Node parent) {
        Node child = parent.copy();
        // TODO: mutation **needs** to handle single node trees!!!! or we'll never get out of that hole
        randomMutation().mutate(child);
        return child;
    }

    private MutationFunc randomMutation() {
        return mutations.get(rand.nextInt(mutations.size()));
    }
    
    public static interface MutationFunc {
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
                int depth = treeDepth(link.parent);
                if (depth >= 2) {
                    Node newSubtree = tf.grow(0, depth - 1, link.child.getOutputType());
                    link.parent.attach(link.childIndex, newSubtree);
                }
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
                    link.parent.attach(link.childIndex, replacement);
                }
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

    public static class ConstantMutation implements MutationFunc {
        public void mutate(Node node) {
            List<Link> constantNodes = new ArrayList<Link>();
            filterLinks(constantNodes, node, 0, new F.lambda2<Boolean, Link, Integer>() {
                public Boolean _call(Link link, Integer integer) throws FunctionException {
                    return (link.child instanceof Constant);
                }
            });
            if (constantNodes.size() > 0) {
                Link l = constantNodes.get(rand.nextInt(constantNodes.size()));
                try {
                    double xp = ((Number)l.child.evaluate()).doubleValue() + (rand.nextDouble() * 2.0 - 1.0);
                    l.parent.attach(l.childIndex, new Constant(xp));
                } catch (VetoTypeInduction vetoTypeInduction) {
                    vetoTypeInduction.printStackTrace();
                }
            }
        }
    }

    public static class BooleanMutation implements MutationFunc {
        public void mutate(Node node) {
            List<Link> boolLinks = new ArrayList<Link>();
            filterLinks(boolLinks, node, 0, new F.lambda2<Boolean, Link, Integer>() {
                public Boolean _call(Link link, Integer integer) throws FunctionException {
                    return link.child instanceof BooleanConstant;
                }
            });
            if (boolLinks.size() > 0) {
                Link l = boolLinks.get(rand.nextInt(boolLinks.size()));
                try {
                    l.parent.attach(l.childIndex, new BooleanConstant(rand.nextBoolean()));
                } catch (VetoTypeInduction vetoTypeInduction) {
                    vetoTypeInduction.printStackTrace();
                }
            }
        }
    }

    public static Link randomSubtree(Node root) {
        int links = countLinks(root);
        if (links == 0) { return Link.EMPTY; }
        return selectLink(root, rand.nextInt(countLinks(root)));
    }

    public static Link randomSubtree(Node root, final Class outputType) {
        F.lambda2<Boolean, Link, Integer> f = new F.lambda2<Boolean, Link, Integer>() {
            public Boolean _call(Link link, Integer integer) throws FunctionException {
                return link.child.getOutputType().equals(outputType);
            }
        };
        List<Link> l = new ArrayList<Link>();
        filterLinks(l, root, 0, f);
        if (l.size() == 0) return Link.EMPTY;
        return l.get(rand.nextInt(l.size()));
    }


    public static class Link {
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

    public static Link selectLink(Node parent, final int i) {
        F.lambda2<Boolean, Link, Integer> f = new F.lambda2<Boolean, Link, Integer>() {
            public Boolean _call(Link link, Integer integer) throws FunctionException {
                return i == integer;
            }
        };
        List<Link> l = new ArrayList<Link>();
        filterLinks(l, parent, 0, f);
        return l.get(0);
    }

    public static int filterLinks(List<Link> l, Node parent, int count, F.lambda2<Boolean, Link, Integer> pred)
    {
        int id = count;
        int i = 0;
        for (Node n : parent.childList()) {
            Link link = new Link(parent, i++, n);
            if (n == null) throw new RuntimeException("node from childlist null!");
            if (pred.call(link, id)) l.add(link);
            id = filterLinks(l, n, id + 1, pred);
        }
        return id;
    }

    public static int countLinks(Node root) {
        int i = 0;
        if (root != null) {
            for (int j = 0; j < root.getInputCount(); j++) {
                i++;
                i += countLinks(root.getChild(j));
            }
        }
        return i;
    }

    public static int treeDepth(Node root) {
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
