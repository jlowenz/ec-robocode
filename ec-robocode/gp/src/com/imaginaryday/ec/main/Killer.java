package com.imaginaryday.ec.main;

import net.jini.space.JavaSpace;
import net.jini.core.entry.Entry;
import com.imaginaryday.util.SpaceFinder;
import com.imaginaryday.util.PoisonPill;
import com.imaginaryday.util.Stuff;

import java.rmi.RMISecurityManager;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ronald A. Bowers
 * @version 1.0
 */
public class Killer {

    public static void main(String[] args) {
        System.setSecurityManager(new RMISecurityManager());
        if (args.length < 1) {
            System.out.println("Usage: com.imaginaryday.ec.main.killer <num_procs> machine0..n");
            System.exit(1);
        }
        Killer killer = new Killer(Integer.parseInt(args[0]), Stuff.slice(1,args));
    }

    private Killer(int n, List<String> machines) {
        for (String m : machines) {
            try {
                for (int i = 0; i < n; i++) {
                    String id = m + System.getProperty("user.name") + i;
                    JavaSpace space = new SpaceFinder().getSpace();
                    Entry pill = new PoisonPill(id);
                    space.write(pill, null, 120000);
                    System.out.println(new StringBuilder().append("Submitted task to kill ")
                            .append(id).toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
