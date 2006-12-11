package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.AbstractNode;
import ec.AgentBrains;
import ec.GPAgent;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private static void write(String filename, AgentBrains brains) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(brains);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        if (args.length < 2) {
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

        String botName = "GPAgent";
        if (args.length > 2) {
            botName = args[2];
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
                CtClass cc = pool.makeClass("ec." + botName + "_" + m.getGeneration() + "_" + m.getId(), gpagent);
                cc.getClassPool().importPackage("ec");

                System.out.println("Creating class: " + cc.getName());

                Set<Class> imports = new HashSet<Class>();

                ((AbstractNode) m.getRadarProgram()).toCodeString(imports);
                ((AbstractNode) m.getTurretProgram()).toCodeString(imports);
                ((AbstractNode) m.getShootProgram()).toCodeString(imports);
                ((AbstractNode) m.getMoveProgram()).toCodeString(imports);
                for (Class c : imports) cc.getClassPool().importPackage(c.getPackage().getName());
                write(dest.getPath() +
                        System.getProperty("file.separator") + 
                        "ec" +
                        System.getProperty("file.separator") +
                        cc.getName() +
                        ".obj",
                        new AgentBrains(m.getRadarProgram(),
                                m.getTurretProgram(),
                                m.getShootProgram(),
                                m.getMoveProgram()));

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
