package com.imaginaryday.ec.main;

import com.imaginaryday.ec.rcpatches.GPBattleResults;
import com.imaginaryday.ec.rcpatches.GPBattleTask;
import com.imaginaryday.util.ServiceFinder;
import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;

/**
 * @author Ronald A. Bowers
 * @version 1.0
 */
public class Watcher implements Runnable {

    public Watcher() {

    }

    public static void main(String[] args) {
        new Thread(new Watcher()).start();
    }

    class TaskListener implements RemoteEventListener {

        public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
            System.out.println("TaskListener: " + remoteEvent.toString());
        }
    }

    class ResultListener implements RemoteEventListener {

        public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
            System.out.println("ResultListener: " + remoteEvent.toString());
        }
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
            JavaSpace space = null;
            ServiceFinder f = new ServiceFinder();
            while (space == null) {
                System.out.println("Looking for space");
                space = f.getSpace();
            }
            boolean done = false;

            Entry taskTemplate = new GPBattleTask();
            Entry resultsTemplate = new GPBattleResults();

            space.notify(taskTemplate,null, new TaskListener(), Lease.FOREVER, null);
            space.notify(resultsTemplate, null, new ResultListener(), Lease.FOREVER, null);


            while(!done) {
                System.out.println("Stayin Alive!");
                Thread.sleep(10000);
            }

        } catch (Exception e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        }
    }
}
