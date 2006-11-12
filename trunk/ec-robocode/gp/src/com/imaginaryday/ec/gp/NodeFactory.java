package com.imaginaryday.ec.gp;

import com.imaginaryday.ec.gp.nodes.*;
import info.javelot.functionalj.tuple.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * User: jlowens
 * Date: Oct 28, 2006
 * Time: 3:50:22 PM
 */
public class NodeFactory {
    private static NodeFactory _instance;

    private Map<String, Class> nodesByName;
    private Map<Class, List<Class>> nonterminalsByOutputType;
    private Map<List<Class>, List<Class>> nonterminalsByInputType;
    private Map<Class, List<Class>> terminalsByOutputType;
    private Map<List<Class>, List<Class>> terminalsByInputType;
	private Map<Class, List<Class>> nodesByOutputType;
	private Map<List<Class>, List<Class>> nodesByInputType;
    private Map<Pair<Class,List<Class>>,List<Class>> nodesByType;
    private List<Class> nodes;
    private Random rand = new Random();

    private NodeFactory()
    {
        nodesByName = new HashMap<String, Class>();
        nonterminalsByOutputType = new HashMap<Class, List<Class>>();
        nonterminalsByInputType = new HashMap<List<Class>, List<Class>>();
        terminalsByInputType = new HashMap<List<Class>, List<Class>>();
        terminalsByOutputType = new HashMap<Class, List<Class>>();
	    nodesByOutputType = new HashMap<Class, List<Class>>();
	    nodesByInputType = new HashMap<List<Class>, List<Class>>();
        nodesByType = new HashMap<Pair<Class, List<Class>>, List<Class>>();
        nodes = new ArrayList<Class>();

        try {
            loadNode(Constant.class);
            loadNode(BooleanConstant.class);
            loadNode(Add.class);
            loadNode(Divide.class);
            loadNode(Multiply.class);
            loadNode(Subtract.class);
            loadNode(And.class);
            loadNode(GreaterThan.class);
            loadNode(LessThan.class);
            loadNode(IfThenElse.class);
            loadNode(Not.class);
            loadNode(Or.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void loadNode(Class<? extends Node> aClass) throws IllegalAccessException, InstantiationException {
        Node n = aClass.newInstance();
        nodes.add(aClass);
        nodesByName.put(n.getName(), aClass); // single namespace for nodes
        putByType(n, aClass);
        putByOutputType(n.getOutputType(), aClass);
        putByInputType(n.getInputTypes(), aClass);
    }

    private void putByType(Node n, Class aClass) {
        List<Class> list = getList(nodesByType, new Pair<Class,List<Class>>(n.getOutputType(),n.getInputTypes()));
        list.add(aClass);
    }

    private void putByInputType(List<Class> inputTypes, Class<? extends Node> aClass) {
        List<Class> allNodes;
        Node n = create(aClass);
        if (n.getInputCount() == 0) // is terminal
            allNodes = getList(terminalsByInputType, inputTypes);
        else
            allNodes = getList(nonterminalsByInputType, inputTypes);
        allNodes.add(aClass);
	    getList(nodesByInputType, inputTypes).add(aClass);
    }

    private <T> List<Class> getList(Map<T, List<Class>> byInputType, T inputTypes) {
        List<Class> allNodes = byInputType.get(inputTypes);
        if (allNodes == null) { allNodes = new ArrayList<Class>(); byInputType.put(inputTypes, allNodes); }
        return allNodes;
    }

    private void putByOutputType(Class outputType, Class<? extends Node> aClass) {
        List<Class> allNodes;
        Node n = create(aClass);
        if (n.getInputCount() == 0) // is terminal
            allNodes = getList(terminalsByOutputType, outputType);
        else
            allNodes = getList(nonterminalsByOutputType, outputType);       
        allNodes.add(aClass);
	    getList(nodesByOutputType, outputType).add(aClass);
    }

    public synchronized static NodeFactory getInstance() {
        if (_instance == null) _instance = new NodeFactory();
        return _instance;
    }

    public Node create(String name) {
        return create(nodesByName.get(name));
    }

    public Node create(Class c, Object ... args) {
        assert c != null;
        try {
            Class cargs[] = new Class[args.length];
            for (int i = 0; i < args.length; i++) cargs[i] = args[i].getClass();
            Constructor ctor = c.getConstructor(cargs);
            return (Node)ctor.newInstance(args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Could not create node of class: " + c.toString() + ", perhaps it wasn't configured?");
    }

    public Node create(String s, Object val) {
        return create(nodesByName.get(s), val);
    }

    @SuppressWarnings({"unchecked"})
    public Node randomTerminal(Class parentType) {
        if (parentType == null) { // TODO: refactor out this logic
            List<Class>[] all = terminalsByOutputType.values().toArray(new List[0]);
            List<Class> sel = all[rand.nextInt(all.length)];
            return create(sel.get(rand.nextInt(sel.size())));
        } else {
            List<Class> all = terminalsByOutputType.get(parentType);
            return create(all.get(rand.nextInt(all.size())));
        }
    }

    public Node randomNode(Class parentType) {
        if (parentType == null) {
            return create(nodes.get(rand.nextInt(nodes.size())));
        } else {
            List<Class> all = nodesByOutputType.get(parentType);
            return create(all.get(rand.nextInt(all.size())));  
        }
    }

	@SuppressWarnings({"unchecked"})
    public Node randomNonterminal(Class parentType) {
		if (parentType == null) {
			List<Class>[] all = nonterminalsByOutputType.values().toArray(new List[0]);
			List<Class> sel = all[rand.nextInt(all.length)];
			return create(sel.get(rand.nextInt(sel.size())));
		} else {
			List<Class> all = nonterminalsByOutputType.get(parentType);
			return create(all.get(rand.nextInt(all.size())));
		}
	}
    public Node randomReplacement(Node child) {
        List<Class> nodes = getList(nodesByType, child.getType());
        return create(nodes.get(rand.nextInt(nodes.size())));
    }
}
