package com.imaginaryday.ec.main;

import net.jini.space.JavaSpace;
import net.jini.core.entry.Entry;
import com.imaginaryday.util.SpaceFinder;
import com.imaginaryday.util.PoisonPill;

import java.rmi.RMISecurityManager;

/**
 * @author Ronald A. Bowers
 * @version 1.0
 */
public class Killer {


    public static void main(String[] args) {
        System.setSecurityManager(new RMISecurityManager());
        String id = null;
        if (args.length != 1) {
            System.out.println("Usage: com.imaginaryday.ec.main.killer worker_id");
            System.exit(1);
        } else {
            id = args[0];
        }
        Killer killer = new Killer(id);

    }

    private Killer(String id) {
        try {
            JavaSpace space = new SpaceFinder().getSpace();
            Entry pill = new PoisonPill(id);
            space.write(pill, null, 120000);
            System.out.println(new StringBuilder().append("Submitted task to kill ")
                    .append(id).toString());
        } catch (Exception e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        }

    }

}
