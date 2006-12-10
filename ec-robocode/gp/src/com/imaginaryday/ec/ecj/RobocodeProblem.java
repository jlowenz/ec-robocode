package com.imaginaryday.ec.ecj;

import com.imaginaryday.ec.rcpatches.GPRobocodeManager;
import com.imaginaryday.util.Tuple;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Problem;
import ec.coevolve.GroupedProblemForm;
import robocode.battle.Battle;
import robocode.battle.BattleProperties;
import robocode.battlefield.BattleField;
import robocode.battlefield.DefaultBattleField;
import robocode.control.BattleSpecification;
import robocode.control.RobocodeListener;
import robocode.control.RobotResults;
import robocode.security.SecureInputStream;
import robocode.security.SecurePrintStream;
import robocode.util.Constants;
import robocode.util.Utils;

import java.io.File;
import java.rmi.RMISecurityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 10:59:42 AM<br>
 * </b>
 */
public class RobocodeProblem extends Problem implements GroupedProblemForm {
    private transient GPRobocodeManager manager;
    private transient BattleProperties battleProperties;

    private Battle battle;

    public void preprocessPopulation(final EvolutionState state, Population pop) {
        if (manager == null) {
            manager = new GPRobocodeManager(false, new ECJRobocodeListener());
            battleProperties = new BattleProperties();
            try {
                if (System.getProperty("WORKINGDIRECTORY") != null) {
                    Constants.setWorkingDirectory(new File(System.getProperty("WORKINGDIRECTORY")));
                }
                Thread.currentThread().setName("Application Thread");
                System.setSecurityManager(new RMISecurityManager());

                SecurePrintStream sysout = new SecurePrintStream(System.out, true, "System.out");
                SecurePrintStream syserr = new SecurePrintStream(System.err, true, "System.err");
                SecureInputStream sysin = new SecureInputStream(System.in, "System.in");

                System.setOut(sysout);
                if (!System.getProperty("debug", "false").equals("true")) {
                    System.setErr(syserr);
                }
                System.setIn(sysin);
            } catch (Throwable e) {
                Utils.log(e);
            }
        }
    }

    public void postprocessPopulation(final EvolutionState state, Population pop) {
        
    }

    private void startNewBattle(List<ECJRobotClassManager> battlingRobotsVector) {
        Utils.log("Preparing battle...");
        if (battle != null) {
            battle.stop();
        }

        BattleField battleField = new DefaultBattleField(800,600);

        battle = new Battle(battleField, manager);
        battle.setExitOnComplete(false);

        // Only used when controlled by RobocodeEngine
        battle.setBattleSpecification(null);

        // Set stuff the view needs to know
        battle.setProperties(battleProperties);

        Thread battleThread = new Thread(Thread.currentThread().getThreadGroup(), battle);

        battleThread.setPriority(Thread.NORM_PRIORITY);
        battleThread.setName("Battle Thread");
        battle.setBattleThread(battleThread);

        for (ECJRobotClassManager robot : battlingRobotsVector) {
            battle.addRobot(robot);
        }

        battleThread.setUncaughtExceptionHandler(new ExceptionHandler());
        battleThread.start();
        try {
            battleThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        public void uncaughtException(Thread thread, Throwable throwable) {
            System.err.println("Exception in thread " + thread.toString());
            System.err.println("Stack trace:");
            throwable.printStackTrace();
        }
    }

    private transient RobotResults[] results;
    public void evaluate(final EvolutionState state,
                         final Individual[] ind,  // the individuals to evaluate together
                         final boolean[] updateFitness,  // should this individuals' fitness be updated?
                         final boolean countVictoriesOnly,  // update fitnesses only to reflect victories, rather than spreads
                         final int threadnum) {
        results = null;
        List<ECJRobotClassManager> robots = new ArrayList<ECJRobotClassManager>();
        Map<String, Tuple.Two<RobocodeIndividual,Boolean>> blah = new HashMap<String, Tuple.Two<RobocodeIndividual, Boolean>>();
        blah.put(((RobocodeIndividual)ind[0]).getName(), Tuple.two((RobocodeIndividual) ind[0], updateFitness[0]));
        blah.put(((RobocodeIndividual)ind[1]).getName(), Tuple.two((RobocodeIndividual) ind[1], updateFitness[1]));
        robots.add(new ECJRobotClassManager((RobocodeIndividual) ind[0]));
        robots.add(new ECJRobotClassManager((RobocodeIndividual) ind[1]));

        // run the battle
        startNewBattle(robots); // this blocks until the battle thread is complete

        // the listener should have been called here
        if (results != null)
        {
            RobocodeFitness rf;
            Tuple.Two<RobocodeIndividual,Boolean> p = blah.get(results[0].getName());
            RobocodeIndividual i = p.first();
            if (p.second()) {
                rf = (RobocodeFitness) i.fitness;
                rf.addResults(state.numGenerations, results[0], results[1]);
            }
            if (!countVictoriesOnly) {
                p = blah.get(results[1].getName());
                i = p.first();
                if (p.second()) {
                    rf = (RobocodeFitness) i.fitness;
                    rf.addResults(state.numGenerations, results[1], results[0]);
                }
            }
        }
    }

    private class ECJRobocodeListener implements RobocodeListener {
        public void battleComplete(BattleSpecification battle, RobotResults[] results) {

        }
        public void battleAborted(BattleSpecification battle) {
            
        }
        public void battleMessage(String message) {
        }
    }
}
