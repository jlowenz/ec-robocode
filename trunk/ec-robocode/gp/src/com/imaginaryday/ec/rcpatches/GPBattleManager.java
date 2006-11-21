/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Removed getBattleView().setDoubleBuffered(false) as RobocodeNG uses
 *       BufferStrategy
 *     - Replaced FileSpecificationVector, RobotPeerVector, and
 *       RobotClassManagerVector with plain Vector
 *     - Added check for if GUI is enabled before using graphical components
 *     - Added restart() method
 *     - Ported to Java 5
 *     - Code cleanup
 *     Luis Crespo
 *     - Added debug step feature, including the nextTurn(), shouldStep(),
 *       startNewRound()
 *******************************************************************************/
package com.imaginaryday.ec.rcpatches;


import com.imaginaryday.util.PoisonPill;
import com.imaginaryday.util.ServiceFinder;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
import robocode.battle.Battle;
import robocode.battle.BattleProperties;
import robocode.battle.BattleResultsTableModel;
import robocode.battlefield.BattleField;
import robocode.battlefield.DefaultBattleField;
import robocode.control.BattleSpecification;
import robocode.control.RobocodeListener;
import robocode.control.RobotResults;
import robocode.manager.BattleManager;
import robocode.manager.RobocodeManager;
import robocode.peer.RobotPeer;
import robocode.peer.robot.RobotClassManager;
import robocode.peer.robot.RobotStatistics;
import robocode.util.Constants;
import robocode.util.Utils;

import javax.swing.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class GPBattleManager extends BattleManager {

    private BattleProperties battleProperties = new BattleProperties();
    private String battleFilename;
    private String battlePath;
    private Battle battle;
    private boolean battleRunning;
    private int pauseCount;
    private String resultsFile;
    private RobocodeManager manager;
    private int stepTurn;
//    private Logger logger = Logger.getLogger(this.getClass().getName());
    private GPBattleTask task;
    private JavaSpace space;
    private TransactionManager transactionManager;
    private Transaction battleTransaction = null;

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public JavaSpace getSpace() {
        return space;
    }


    /**
     * Steps for a single turn, then goes back to paused
     */
    public void nextTurn() {
        if (battleRunning) {
            stepTurn = battle.getCurrentTime() + 1;
        }
    }

    /**
     * If the battle is paused, this method determines if it should perform one turn and then stop again.
     *
     * @return true if the battle should perform one turn, false otherwise
     */
    public boolean shouldStep() {
        // This code assumes it is called only if the battle is paused.
        return stepTurn > battle.getCurrentTime();
    }

    /**
     * This method should be called to inform the battle manager that a new round is starting
     */
    public void startNewRound() {
        stepTurn = 0;
    }

    public GPBattleManager(RobocodeManager manager) {
        super(manager);
        this.manager = manager;
    }

    public void stop(boolean showResultsDialog) {
        if (getBattle() != null) {
            getBattle().stop(showResultsDialog);
        }
    }

    public void restart() {
        stop(false);
        startNewBattle(battleProperties, false);
    }

    private Map<String, RobotClassManager> loadStandardBots() {
        Map<String, RobotClassManager> map = new ConcurrentHashMap<String, RobotClassManager>();

        map.put("Corners", new StandardRobotClassManager(new GPRobotSpecification("Corners", "1"),
                sample.Corners.class));
        map.put("Crazy", new StandardRobotClassManager(new GPRobotSpecification("Crazy", "2"),
                sample.Crazy.class));
        map.put("Fire", new StandardRobotClassManager(new GPRobotSpecification("Fire", "3"),
                sample.Fire.class));
        map.put("MyFirstRobot", new StandardRobotClassManager(new GPRobotSpecification("MyFirstRobot", "4"),
                sample.MyFirstRobot.class));
        map.put("RamFire", new StandardRobotClassManager(new GPRobotSpecification("RamFire", "5"),
                sample.RamFire.class));
        map.put("SittingDuck", new StandardRobotClassManager(new GPRobotSpecification("SittingDuck", "6"),
                sample.SittingDuck.class));
        map.put("SpinBot", new StandardRobotClassManager(new GPRobotSpecification("SpinBot", "7"),
                sample.SpinBot.class));
        map.put("Target", new StandardRobotClassManager(new GPRobotSpecification("Target", "8"),
                sample.Target.class));
        map.put("Tracker", new StandardRobotClassManager(new GPRobotSpecification("Tracker", "9"),
                sample.Tracker.class));
        map.put("TrackFire", new StandardRobotClassManager(new GPRobotSpecification("TrackFire", "10"),
                sample.TrackFire.class));
        map.put("Walls", new StandardRobotClassManager(new GPRobotSpecification("Walls", "11"),
                sample.Walls.class));

        return map;
    }


    public void startNewBattle(BattleProperties battleProperties, boolean exit) {
        this.battleProperties = battleProperties;

        // Load ClassManagers need 2 GPRobots.
        GPRobotClassManager gpcm1 = new GPRobotClassManager();
        GPRobotClassManager gpcm2 = new GPRobotClassManager();

        Map<String, RobotClassManager> standardBots = loadStandardBots();

        space = null;

        ResultForwarder rl = new ResultForwarder();
        manager.setListener(rl);

        final JavaSpace space1 = space;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            private GPBattleManager bm = GPBattleManager.this;

            public void run() {
                System.err.println("shutdown hook running!");
                Utils.log("Writing unfinished task back to space!");
                GPBattleTask t = bm.getTask();
                if (t != null && !t.done) {
                    try {
                        JavaSpace space1 = getSpace();
                        if (space1 != null) {
                            space1.write(t, null, Lease.FOREVER);
                            Utils.log("Done");
                        }
                    } catch (TransactionException e) {
                        Utils.log("%%%%%%%%%%%%%%%%%%%%%% failed to replace TASK");
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        Utils.log("%%%%%%%%%%%%%%%%%%%%%% failed to replace TASK");
                        e.printStackTrace();
                    }
                } else {
                    Utils.log("No unfinished task!");
                }
            }
        });

        boolean done = false;
        Entry taskTemplate = new GPBattleTask();
        Entry pillTemplate = new PoisonPill(id);
        Utils.log((id != null) ? id : "null, bitch");
        while (!done) {

            while (transactionManager == null) {
                try {
                    transactionManager = new ServiceFinder().getTransactionManager();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (transactionManager == null) {
                    System.err.println("No TransactionManager, will look again");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            while (space == null) {
                try {
                    space = new ServiceFinder().getSpace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (space == null) {
                    System.err.println("No space, will look again");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                PoisonPill pill;
                Transaction t = TransactionFactory.create(transactionManager, 60000).transaction;

                pill = (PoisonPill) space.takeIfExists(pillTemplate, t, 0);
                if (pill != null) {
                    Utils.log("Pill received " + pill.toString());
                    if (pill.id.equals(id)) {
                        Utils.log("Pill matches");
                        t.commit();
                        return;
                    } else {
                        t.abort();
                    }
                    done = true;
                    continue;
                }
            } catch (UnusableEntryException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (TransactionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (RemoteException e) {
                space = null;
                System.err.println("Lost connection to space");
                continue;
            } catch (LeaseDeniedException e) {
                e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
            }

            try {
                battleTransaction = TransactionFactory.create(getTransactionManager(), 60000).transaction;
            } catch (LeaseDeniedException e) {
                e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
                continue;
            } catch (RemoteException e) {
                e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
                continue;
            }

            Utils.log("Looking for task");
            try {
                task = (GPBattleTask) space.take(taskTemplate, battleTransaction, 3000);
                Utils.log((task == null) ? "null" : task.shortString());
            } catch (UnusableEntryException e) {
                e.printStackTrace();
            } catch (TransactionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                space = null;
                System.err.println("Lost connection to space");
                continue;
            }

            if (task == null) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            if (task.done != null && task.done) done = true;
            else {
                RobotClassManager ralph;
                RobotClassManager alice;

                Utils.log("robot1 = " + task.robot1);
                if (standardBots.keySet().contains(task.robot1)) {
                    ralph = standardBots.get(task.robot1);
                } else {
                    gpcm1.setMoveProgram(task.moveProgram1);
                    gpcm1.setTurretProgram(task.turretProgram1);
                    gpcm1.setRadarProgram(task.radarProgram1);
                    gpcm1.setShootProgram(task.shootProgram1);
                    ralph = gpcm1;
                }

                Utils.log("robot2 = " + task.robot2);
                if (standardBots.keySet().contains(task.robot2)) {
                    alice = standardBots.get(task.robot2);
                } else {
                    gpcm2.setMoveProgram(task.moveProgram2);
                    gpcm2.setTurretProgram(task.turretProgram2);
                    gpcm2.setRadarProgram(task.radarProgram1);
                    gpcm2.setShootProgram(task.shootProgram2);
                    alice = gpcm1;
                }
                rl.setBattleTask(task);
                Vector<RobotClassManager> battlingRobotsVector = new Vector<RobotClassManager>();
                battlingRobotsVector.add(ralph);
                battlingRobotsVector.add(alice);
                startNewBattle(battlingRobotsVector, false, null);
                task.done = true;
            }
        }

    }

    private GPBattleTask getTask() {
        return task;
    }

    class ResultForwarder implements RobocodeListener {

        private GPBattleTask battleTask;

        ResultForwarder() {
        }

        public void battleComplete(BattleSpecification battle, RobotResults[] results) {

            if (results != null && results.length == 2) {
                GPBattleResults res = new GPBattleResults(battleTask,
                        GPFitnessCalc.getFitness(battleTask.generation, results[0], results[1]),
                        GPFitnessCalc.getFitness(battleTask.generation, results[1], results[0]),
                        results[0], results[1]);

                Utils.log(res.toString());
                try {
                    JavaSpace space = getSpace();
                    if (space != null) {
                        Lease l = space.write(res, null, Lease.FOREVER);
                        if (l.getExpiration() != Lease.FOREVER) {
                            System.err.println("Lease returned was not FOREVER: " + l);
                        }
                        battleTransaction.commit();
                    } else {
                        System.err.println("Null space!");
                        new RuntimeException("Null Space!!").printStackTrace();
                        battleTransaction.abort();
                    }
                } catch (TransactionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Utils.log("Bad results array");
                try {
                    battleTransaction.abort();
                } catch (UnknownTransactionException e) {
                    e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
                } catch (CannotAbortException e) {
                    e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
                } catch (RemoteException e) {
                    e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
                }
            }

        }

        public void battleAborted(BattleSpecification battle) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void battleMessage(String message) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void setBattleTask(GPBattleTask battleTask) {
            this.battleTask = battleTask;
        }
    }


    private void startNewBattle(Vector<RobotClassManager> battlingRobotsVector, boolean exitOnComplete,
                                robocode.control.BattleSpecification battleSpecification) {

        Utils.log("Preparing battle...");
        if (battle != null) {
            battle.stop();
        }

        BattleField battleField = new DefaultBattleField(battleProperties.getBattlefieldWidth(),
                battleProperties.getBattlefieldHeight());

        battle = new Battle(battleField, manager);
        battle.setExitOnComplete(exitOnComplete);

        // Only used when controlled by RobocodeEngine
        battle.setBattleSpecification(battleSpecification);

        // Set stuff the view needs to know
        battle.setProperties(battleProperties);

        Thread battleThread = new Thread(Thread.currentThread().getThreadGroup(), battle);

        battleThread.setPriority(Thread.NORM_PRIORITY);
        battleThread.setName("Battle Thread");
        battle.setBattleThread(battleThread);
        /*
        if (!System.getProperty("NOSECURITY", "false").equals("true")) {
            ((RobocodeSecurityManager) System.getSecurityManager()).addSafeThread(battleThread);
            ((RobocodeSecurityManager) System.getSecurityManager()).setBattleThread(battleThread);
        }
        */


        for (int i = 0; i < battlingRobotsVector.size(); i++) {
            battle.addRobot((RobotClassManager) battlingRobotsVector.elementAt(i));
        }

        battleThread.setUncaughtExceptionHandler(new ExceptionHandler());
        battleThread.start();
        try {
            battleThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getBattleFilename() {
        return battleFilename;
    }

    public void setBattleFilename(String newBattleFilename) {
        battleFilename = newBattleFilename;
    }

    public boolean isPaused() {
        return (pauseCount != 0);
    }

    public void pauseBattle() {
        pauseCount++;
    }

    public String getBattlePath() {
        if (battlePath == null) {
            battlePath = System.getProperty("BATTLEPATH");
            if (battlePath == null) {
                battlePath = "battles";
            }
            battlePath = new File(Constants.cwd(), battlePath).getAbsolutePath();
        }
        return battlePath;
    }

    public void saveBattle() {
        pauseBattle();
        saveBattleProperties();
        resumeBattle();
    }

    public void saveBattleAs() {
        pauseBattle();
        File f = new File(getBattlePath());

        JFileChooser chooser;

        chooser = new JFileChooser(f);

        javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return false;
                }
                String fn = pathname.getName();
                int idx = fn.lastIndexOf('.');
                String extension = "";

                if (idx >= 0) {
                    extension = fn.substring(idx);
                }
                if (extension.equalsIgnoreCase(".battle")) {
                    return true;
                }
                return false;
            }

            public String getDescription() {
                return "Battles";
            }
        };

        chooser.setFileFilter(filter);
        int rv = chooser.showSaveDialog(manager.getWindowManager().getRobocodeFrame());

        if (rv == JFileChooser.APPROVE_OPTION) {
            battleFilename = chooser.getSelectedFile().getPath();
            int idx = battleFilename.lastIndexOf('.');
            String extension = "";

            if (idx > 0) {
                extension = battleFilename.substring(idx);
            }
            if (!(extension.equalsIgnoreCase(".battle"))) {
                battleFilename += ".battle";
            }
            saveBattleProperties();
        }
        resumeBattle();
    }

    public void saveBattleProperties() {
        if (battleProperties == null) {
            Utils.log("Cannot save null battle properties");
            return;
        }
        if (battleFilename == null) {
            saveBattleAs();
            return;
        }
        try {
            FileOutputStream out = new FileOutputStream(battleFilename);

            battleProperties.store(out, "Battle Properties");
        } catch (IOException e) {
            Utils.log("IO Exception saving battle properties: " + e);
        }
    }

    public void loadBattleProperties() {
        try {
            FileInputStream in = new FileInputStream(battleFilename);

            getBattleProperties().load(in);
        } catch (FileNotFoundException e) {
            Utils.log("No file " + battleFilename + " found, using defaults.");
        } catch (IOException e) {
            Utils.log("IO Exception reading " + battleFilename + ": " + e);
        }
    }

    public Battle getBattle() {
        return battle;
    }

    public void setOptions() {
        if (battle != null) {
            battle.setOptions();
        }
    }

    public BattleProperties getBattleProperties() {
        if (battleProperties == null) {
            battleProperties = new BattleProperties();
        }
        return battleProperties;
    }

    public void clearBattleProperties() {
        battleProperties = null;
    }

    public void resumeBattle() {
        // Resume is done after a short delay,
        // so that a user switching from menu to menu won't cause
        // a lot of flickering and/or "single frame" battle unpauses
        /*
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                }
                pauseCount--;
                if (pauseCount < 0) {
                    pauseCount = 0;
                }
            }
        }).start();
        */
    }

    public boolean isBattleRunning() {
        return battleRunning;
    }

    public void setBattle(Battle newBattle) {
        battle = newBattle;
    }

    public void setBattleRunning(boolean newBattleRunning) {
        battleRunning = newBattleRunning;
    }

    public void setResultsFile(String newResultsFile) {
        resultsFile = newResultsFile;
    }

    public String getResultsFile() {
        return resultsFile;
    }

    public void sendResultsToListener(Battle battle, robocode.control.RobocodeListener listener) {

        for (RobotPeer r : battle.getRobots()) {
            Utils.log("robot " + r.getName());
        }

        Vector<RobotPeer> orderedRobots = new Vector<RobotPeer>(battle.getRobots());

        Collections.sort(orderedRobots);

        robocode.control.RobotResults results[] = new robocode.control.RobotResults[orderedRobots.size()];

        for (int i = 0; i < results.length; i++) {
            RobotStatistics stats = orderedRobots.elementAt(i).getRobotStatistics();

            results[i] = new robocode.control.RobotResults(
                    orderedRobots.elementAt(i).getRobotClassManager().getControlRobotSpecification(),
                    orderedRobots.elementAt(i).getName(), (i + 1), stats);
        }
        listener.battleComplete(battle.getBattleSpecification(), results);
    }

    public void printResultsData(Battle battle) {
        PrintStream out;
        boolean close = false;

        if (getResultsFile() == null) {
            out = System.out;
        } else {
            File f = new File(getResultsFile());

            try {
                out = new PrintStream(new FileOutputStream(f));
                close = true;
            } catch (IOException e) {
                Utils.log(e);
                return;
            }
        }

        BattleResultsTableModel resultsTable = new BattleResultsTableModel(battle);

        resultsTable.print(out);
        if (close) {
            out.close();
        }
    }

    /**
     * Gets the manager.
     *
     * @return Returns a RobocodeManager
     */
    public RobocodeManager getManager() {
        return manager;
    }

    class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        public void uncaughtException(Thread thread, Throwable throwable) {
            System.err.println("Exception in thread " + thread.toString());
            System.err.println("Stack trace:");
            throwable.printStackTrace();
        }
    }
}
