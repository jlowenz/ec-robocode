package com.imaginaryday.ec.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

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
            for (Member m : members) {
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
