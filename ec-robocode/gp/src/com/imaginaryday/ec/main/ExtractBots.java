package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.AbstractNode;
import ec.GPAgent;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 24, 2006<br>
 * Time: 8:17:44 PM<br>
 * </b>
 */
public class ExtractBots {
    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Usage: ExtractBots <pop_file> <to_dir>");
            System.out.println("  - don't forget to set the classpath properly");
            System.exit(0);
        }
        File popFile = new File(args[0]);
        if (!popFile.exists()) {
            System.err.println("File doesn't exist: " + popFile);
            System.exit(-1);
        }
        File dest = new File(args[1]);
        if (!dest.exists()) {
            dest.mkdirs();
        }

        FileInputStream fr = null;
        try {
            fr = new FileInputStream(popFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(fr);
            Snapshot ss = (Snapshot) ois.readObject();
            List<Member> members = ss.getPopulation();
            ClassPool pool = ClassPool.getDefault();
            for (Member m : members) {
                CtClass gpagent = pool.get("ec.GPAgent");
                CtClass node = pool.get("com.imaginaryday.ec.gp.Node");
                CtClass cc = pool.makeClass("ec.GPAgent_" + m.getGeneration() + "_" + m.getId(), gpagent);
                cc.getClassPool().importPackage("ec");                        

                System.out.println("Creating class: " + cc.getName());

                Set<Class> imports = new HashSet<Class>();

                String expr = ((AbstractNode)m.getRadarProgram()).toCodeString(imports);
                for (Class c : imports) cc.getClassPool().importPackage(c.getPackage().getName());
                CtMethod radar = CtNewMethod.make(Modifier.PROTECTED,
                        node,
                        "initRadarTree",
                        new CtClass[0],
                        new CtClass[0],
                        "{ return " + expr + "; }", cc);
                cc.addMethod(radar);

                expr = ((AbstractNode)m.getTurretProgram()).toCodeString(imports);
                for (Class c : imports) cc.getClassPool().importPackage(c.getPackage().getName());
                CtMethod turret = CtNewMethod.make(Modifier.PROTECTED,
                        node,
                        "initTurretTree",
                        new CtClass[0],
                        new CtClass[0],
                        "{ return " + expr + "; }", cc);
                cc.addMethod(turret);

                expr = ((AbstractNode)m.getShootProgram()).toCodeString(imports);
                for (Class c : imports) cc.getClassPool().importPackage(c.getPackage().getName());
                CtMethod firing = CtNewMethod.make(Modifier.PROTECTED,
                        node,
                        "initFiringTree",
                        new CtClass[0],
                        new CtClass[0],
                        "{ return " + expr + "; }", cc);
                cc.addMethod(firing);

                expr = ((AbstractNode)m.getMoveProgram()).toCodeString(imports);
                for (Class c : imports) cc.getClassPool().importPackage(c.getPackage().getName());
                CtMethod direction = CtNewMethod.make(Modifier.PROTECTED,
                        node,
                        "initDirectionTree",
                        new CtClass[0],
                        new CtClass[0],
                        "{ return " + expr + "; }", cc);
                cc.addMethod(direction);

                cc.stopPruning(true);
                Class c = pool.toClass(cc);
                GPAgent agent = (GPAgent) c.newInstance();
                System.out.println(agent.getClass());
                cc.stopPruning(false);
                cc.writeFile(dest.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
