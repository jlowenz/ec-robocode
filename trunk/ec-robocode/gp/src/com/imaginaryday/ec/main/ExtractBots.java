package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.AbstractNode;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
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
                CtClass cc = pool.getAndRename("ec.GPAgent", "ec.GPAgent_" + m.getGeneration() + "_" + m.getId());
                CtField radar = cc.getField("radarTree");
                CtField turret = cc.getField("turretTree");
                CtField firing = cc.getField("firingTree");
                CtField direction = cc.getField("directionTree");
                cc.removeField(radar);
                cc.removeField(turret);
                cc.removeField(firing);
                cc.removeField(direction);
                Set<Class> imports = new HashSet<Class>();
                cc.addField(radar, CtField.Initializer.byExpr(((AbstractNode)m.getRadarProgram()).toCodeString(imports)));
                cc.addField(turret, CtField.Initializer.byExpr(((AbstractNode)m.getTurretProgram()).toCodeString(imports)));
                cc.addField(firing, CtField.Initializer.byExpr(((AbstractNode)m.getShootProgram()).toCodeString(imports)));
                cc.addField(direction, CtField.Initializer.byExpr(((AbstractNode)m.getMoveProgram()).toCodeString(imports)));
                for (Class c : imports) pool.importPackage(c.getName());
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
        }
    }
}
