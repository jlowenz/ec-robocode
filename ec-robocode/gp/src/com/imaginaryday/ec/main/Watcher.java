package com.imaginaryday.ec.main;

import net.jini.space.JavaSpace;
import net.jini.core.entry.Entry;
import com.imaginaryday.util.ServiceFinder;
import com.imaginaryday.ec.rcpatches.GPBattleResults;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * @author Ronald A. Bowers
 * @version 1.0
 */
public class Watcher implements Runnable {

    public Watcher() {

    }

    public static void main(String[] args) {
        new Thread(new Watcher());
    }

    class TaskThing {

        public Integer getBattle() {
            return battle;
        }

        public Integer getGeneration() {
            return generation;
        }

        Integer battle;
        Integer generation;

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final TaskThing taskThing = (TaskThing) o;

            if (!battle.equals(taskThing.battle)) return false;
            if (!generation.equals(taskThing.generation)) return false;

            return true;
        }


        public String toString() {
            return "TaskThing{" +
                    "battle=" + battle +
                    ", generation=" + generation +
                    '}';
        }

        public int hashCode() {
            int result;
            result = battle.hashCode();
            result = 29 * result + generation.hashCode();
            return result;
        }

        public TaskThing(Integer generation, Integer battle) {
            this.battle = battle;
            this.generation = generation;
        }
    }

    public void run() {
        try {
            JavaSpace space = new ServiceFinder().getSpace();
            boolean done = false;

            Map<String, List<TaskThing>> bob = new HashMap<String, List<TaskThing>>();

            Entry takenTemplate = new Taken();
            Entry resultsTemplate = new GPBattleResults();
            while(!done) {

                Taken taken = (Taken)space.takeIfExists(takenTemplate, null, 0);

                if (taken != null) {
                    List<TaskThing> ltt = bob.get(taken.id);

                    if (ltt == null) {
                        ltt = new ArrayList<TaskThing>();
                        bob.put(taken.id, ltt);
                    } else {
                        if (ltt.size() != 0) {
                            System.out.println("WARNING - Worker took another task! " + taken.id );
                            System.out.println("WARNING - Worker holds");
                            for (TaskThing fred : ltt) {
                                 System.out.print(fred);
                            }
                        }
                    }
                    ltt.add(new TaskThing(taken.generation, taken.battle));

                    System.out.println(new StringBuilder().append("Worker ").append(taken.id)
                            .append(" took ").append(taken.generation).append(":").append(taken.battle).toString());
                }

                GPBattleResults gpbr = (GPBattleResults)space.readIfExists(resultsTemplate, null, 0);

                if (gpbr != null) {
                    List<TaskThing> ltt = bob.get(gpbr.id);

                    if (ltt == null) {
                        System.out.println("WARNING - Tasked returned without a previous take");
                        ltt = new ArrayList<TaskThing>();
                        bob.put(gpbr.id, ltt);
                    } else {
                        for (int i = 0 ; i < ltt.size(); ++i ) {
                            TaskThing t = ltt.get(i);
                            if (t.getBattle().equals(gpbr.battle) && t.getGeneration().equals(gpbr.generation)) {
                                System.out.println(new StringBuilder().append("Worker ").append(taken.id)
                            .append(" finished ").append(taken.generation)
                                        .append(":").append(taken.battle).toString());
                                ltt.remove(i);
                            }
                        }
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        }
    }
}
