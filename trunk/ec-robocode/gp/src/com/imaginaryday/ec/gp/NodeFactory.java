package com.imaginaryday.ec.gp;

import com.imaginaryday.ec.gp.nodes.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jlowens
 * Date: Oct 28, 2006
 * Time: 3:50:22 PM
 */
public class NodeFactory {
    private static NodeFactory _instance;

    private Map<String, Class> nodesByName;
    private Map<Class, Class> nodesByOutputType;
    private Map<Class[], Class> nodesByInputType;

    private NodeFactory()
    {
        nodesByName = new HashMap<String, Class>();
        nodesByOutputType = new HashMap<Class, Class>();
        nodesByInputType = new HashMap<Class[], Class>();

        try {
            loadType(Constant.class);
            loadType(Add.class);
            loadType(Divide.class);
            loadType(Multiply.class);
            loadType(Subtract.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void loadType(Class<? extends Node> aClass) throws IllegalAccessException, InstantiationException {
        Node n = aClass.newInstance();
        nodesByName.put(n.getName(), aClass);
        nodesByOutputType.put(n.getOutputType(), aClass);
        nodesByInputType.put(n.getInputTypes(), aClass);
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
}
