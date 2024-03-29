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
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;


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
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private GPBattleTask task;
    private JavaSpace space;
    private TransactionManager transactionManager;
    private LinkedList<GPBattleTask> workingTasks = new LinkedList<GPBattleTask>();
    private Transaction battleTx = null;
    private Lock btl = new ReentrantLock();
    private RobotClassManager ralph;
    private RobotClassManager alice;

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
        map.put("Splinter", new StandardRobotClassManager(new GPRobotSpecification("Splinter", "12"),
                kc.nano.Splinter.class));
        map.put("RaikoMicro", new StandardRobotClassManager(new GPRobotSpecification("RaikoMicro", "13"),
                jam.micro.RaikoMicro.class));
        map.put("Komarious", new StandardRobotClassManager(new GPRobotSpecification("Komarious", "14"),
                voidious.mini.Komarious.class));
        map.put("Dookious", new StandardRobotClassManager(new GPRobotSpecification("Dookious", "15"),
                voidious.Dookious.class));
        map.put("NanoAndrew", new StandardRobotClassManager(new GPRobotSpecification("NanoAndrew", "16"),
                ahf.NanoAndrew.class));
        map.put("ChumbaWumba", new StandardRobotClassManager(new GPRobotSpecification("ChumbaWumba", "17"),
                amk.ChumbaWumba.class));
        map.put("AspidMovement", new StandardRobotClassManager(new GPRobotSpecification("AspidMovement", "18"),
                apv.AspidMovement.class));      
        map.put("SilverSurfer", new StandardRobotClassManager(new GPRobotSpecification("SilverSurfer", "20"),
                axeBots.SilverSurfer.class));
        map.put("Freya", new StandardRobotClassManager(new GPRobotSpecification("Freya", "21"),
                bvh.fry.Freya.class));
        map.put("Wodan", new StandardRobotClassManager(new GPRobotSpecification("Wodan", "22"),
                bvh.mini.Wodan.class));
        map.put("Chiva", new StandardRobotClassManager(new GPRobotSpecification("Chiva", "23"),
                cf.mini.Chiva.class));
        map.put("Merkava", new StandardRobotClassManager(new GPRobotSpecification("Merkava", "24"),
                cjk.Merkava.class));
        map.put("Smoke", new StandardRobotClassManager(new GPRobotSpecification("Smoke", "25"),
                cx.micro.Smoke.class));
        map.put("Nimrod", new StandardRobotClassManager(new GPRobotSpecification("Nimrod", "26"),
                cx.mini.Nimrod.class));
        map.put("Smog", new StandardRobotClassManager(new GPRobotSpecification("Smog", "27"),
                cx.nano.Smog.class));
        map.put("Virgin", new StandardRobotClassManager(new GPRobotSpecification("Virgin", "28"),
                dft.Virgin.class));
        map.put("Eve", new StandardRobotClassManager(new GPRobotSpecification("Eve", "29"),
                dmp.nano.Eve.class));
        map.put("Evader", new StandardRobotClassManager(new GPRobotSpecification("Evader", "30"),
                aw.Evader.class));
        map.put("Bandit", new StandardRobotClassManager(new GPRobotSpecification("Bandit", "31"),
                fnc.bandit.Bandit.class));
        map.put("Wolverine", new StandardRobotClassManager(new GPRobotSpecification("Wolverine", "32"),
                gg.Wolverine.class));
        map.put("Sonda", new StandardRobotClassManager(new GPRobotSpecification("Sonda", "33"),
                jasolo.Sonda.class));
        map.put("Hawkwing", new StandardRobotClassManager(new GPRobotSpecification("Hawkwing", "34"),
                jep.nano.Hawkwing.class));
        map.put("Terrible", new StandardRobotClassManager(new GPRobotSpecification("Terrible", "35"),
                jep.Terrible.class));
        map.put("PerceptBot", new StandardRobotClassManager(new GPRobotSpecification("PerceptBot", "36"),
                kcn.percept.PerceptBot.class));
        map.put("Predator", new StandardRobotClassManager(new GPRobotSpecification("Predator", "37"),
                lorneswork.Predator.class));
        map.put("UnderDark", new StandardRobotClassManager(new GPRobotSpecification("UnderDark", "38"),
                matt.UnderDark3.class));
        map.put("Pasta", new StandardRobotClassManager(new GPRobotSpecification("Pasta", "39"),
                md.Pasta.class));
        map.put("DustBunny", new StandardRobotClassManager(new GPRobotSpecification("DustBunny", "40"),
                mld.DustBunny.class));
        map.put("Ascendant", new StandardRobotClassManager(new GPRobotSpecification("Ascendant", "41"),
                mue.Ascendant.class));
        map.put("Hyperion", new StandardRobotClassManager(new GPRobotSpecification("Hyperior", "42"),
                mue.Hyperion.class));
        map.put("CrazyKitten", new StandardRobotClassManager(new GPRobotSpecification("CrazyKitten", "43"),
                muf.CrazyKitten.class));
        map.put("Predator-2", new StandardRobotClassManager(new GPRobotSpecification("Predator-2", "44"),
                myl.micro.Predator.class));
        map.put("Movement", new StandardRobotClassManager(new GPRobotSpecification("Movement", "45"),
                mz.Movement.class));
        map.put("NanoGod", new StandardRobotClassManager(new GPRobotSpecification("NanoGod", "46"),
                mz.NanoGod.class));
        map.put("Ugluk", new StandardRobotClassManager(new GPRobotSpecification("Ugluk", "47"),
                pedersen.Ugluk.class));
        map.put("Grumpy", new StandardRobotClassManager(new GPRobotSpecification("Grumpy", "48"),
                peterPark.StationaryGrumpy.class));
        map.put("GloomyDark", new StandardRobotClassManager(new GPRobotSpecification("GloomyDark", "49"),
                pez.gloom.GloomyDark.class));
        map.put("Mako", new StandardRobotClassManager(new GPRobotSpecification("Mako", "50"),
                pez.mako.Mako.class));
        map.put("Ali", new StandardRobotClassManager(new GPRobotSpecification("Ali", "51"),
                pez.rumble.Ali.class));
        map.put("DarkFinal", new StandardRobotClassManager(new GPRobotSpecification("DarkFinal", "52"),
                pi.Dark.class));
        map.put("Fusion", new StandardRobotClassManager(new GPRobotSpecification("Fusion", "53"),
                radnor.FUSiON.class));
        map.put("RamRod", new StandardRobotClassManager(new GPRobotSpecification("RamRod", "54"),
                radnor.RamRod.class));
        map.put("Mirror", new StandardRobotClassManager(new GPRobotSpecification("Mirror", "55"),
                stelo.Mirror.class));
        map.put("MirrorNano", new StandardRobotClassManager(new GPRobotSpecification("MirrorNano", "56"),
                stelo.MirrorNano.class));
        map.put("Squirtle", new StandardRobotClassManager(new GPRobotSpecification("Squirtle", "57"),
                tobe.micro.Squirtle.class));
        map.put("Charon", new StandardRobotClassManager(new GPRobotSpecification("Charon", "58"),
                tobe.mini.Charon.class));
        map.put("Pandora", new StandardRobotClassManager(new GPRobotSpecification("Pandora", "59"),
                tobe.Pandora.class));
        map.put("Squirrel", new StandardRobotClassManager(new GPRobotSpecification("Squirrel", "60"),
                tobe.Squirrel.class));
        map.put("TheArtOfWar", new StandardRobotClassManager(new GPRobotSpecification("TheArtOfWar", "61"),
                tzu.TheArtOfWar.class));
        map.put("Wisdom", new StandardRobotClassManager(new GPRobotSpecification("Wisdom", "63"),
                whind.Wisdom.class));

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

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                btl.lock();
                try {
                    if (battleTx != null) {
                        try {
                            battleTx.abort();
                            battleTx = null;
                        } catch (UnknownTransactionException e) {
                            e.printStackTrace();
                        } catch (CannotAbortException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    btl.unlock();
                }
                System.err.println("shutdown hook running!");
            }
        });

        boolean done = false;
        Entry taskTemplate = new GPBattleTask();
        Utils.log((id != null) ? id : "null, bitch");
        long deltaT = 0;
        long startTime;
        long txTime = 120000; // Start at 2 minutes

        while (!done) {
            startTime = System.currentTimeMillis();
            txTime = Math.max(txTime, deltaT * 3);

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
                try {
                    btl.lock();
                    battleTx = TransactionFactory.create(getTransactionManager(), txTime).transaction;
                } finally {
                    btl.unlock();
                }
            } catch (LeaseDeniedException e) {
                e.printStackTrace();
                continue;
            } catch (RemoteException e) {
                e.printStackTrace();
                continue;
            }

            Utils.log("Looking for task");
            try {
                task = (GPBattleTask) space.takeIfExists(taskTemplate, battleTx, 3000);
                if (task != null) workingTasks.addFirst(task);
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

                Utils.log("robot1 = " + task.robot1);
                if (standardBots.keySet().contains(task.robot1)) {
                    ralph = standardBots.get(task.robot1);
                    ralph.setName(task.robot1); // Ron - it works!
                } else {
                    gpcm1.setName(task.robot1);
                    gpcm1.setMoveProgram(task.getMoveProgram1());
                    gpcm1.setTurretProgram(task.getTurretProgram1());
                    gpcm1.setRadarProgram(task.getRadarProgram1());
                    gpcm1.setShootProgram(task.getShootProgram1());
                    ralph = gpcm1;
                }

                Utils.log("robot2 = " + task.robot2);
                if (standardBots.keySet().contains(task.robot2)) {
                    alice = standardBots.get(task.robot2);
                    alice.setName(task.robot2);
                } else {
                    gpcm2.setName(task.robot2);
                    gpcm2.setMoveProgram(task.getMoveProgram2());
                    gpcm2.setTurretProgram(task.getTurretProgram2());
                    gpcm2.setRadarProgram(task.getRadarProgram2());
                    gpcm2.setShootProgram(task.getShootProgram2());
                    alice = gpcm2; // WE WERE RUNNING THE SAME DAMN BOTS AGAINST EACH OTHER!
                }

                rl.setBattleTask(task);
                Vector<RobotClassManager> battlingRobotsVector = new Vector<RobotClassManager>();
                battlingRobotsVector.add(ralph);
                battlingRobotsVector.add(alice);
                try {
                    startNewBattle(battlingRobotsVector, false, null);
                } catch (Throwable e) {
                    e.printStackTrace();
                    logger.severe("Error during battle! on worker " + id);
                    try {
                        battleTx.abort();
                    } catch (UnknownTransactionException e1) {
                        e1.printStackTrace();
                    } catch (CannotAbortException e1) {
                        e1.printStackTrace();
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
                task.done = true;
            }

            deltaT = System.currentTimeMillis() - startTime;
            Utils.log("Battle fought in " + deltaT / 1000 + " seconds");
        }

    }


    class ResultForwarder implements RobocodeListener {

        private GPBattleTask battleTask;

        ResultForwarder() {
        }

        public void battleComplete(BattleSpecification battle, RobotResults[] results) {

            if (results != null && results.length == 2) {

                GPBattleResults res = new GPBattleResults(id, battleTask,
                        GPFitnessCalc.getFitness(battleTask.generation, results[0], results[1]),
                        GPFitnessCalc.getFitness(battleTask.generation, results[1], results[0]),
                        results[0], results[1]);

                Utils.log(res.toString());
                try {
                    JavaSpace space = getSpace();
                    if (space != null) {
                        space.write(res, battleTx, Lease.FOREVER);
                        battleTx.commit();

                        if (workingTasks.size() > 1) {
                            logger.severe("WORKER (" + id + ") !!!!!!! stomping on more than 1 working task!");
                            for (GPBattleTask wt : workingTasks) {
                                if (workingTasks.getLast().battle != res.battle) {
                                    logger.severe("WORKER (" + id + "): result (" + res + ") doesn't match working task: " + wt);
                                } else {
                                    logger.severe("WORKER (" + id + "): found task: " + wt);
                                }
                            }
                            workingTasks.clear();
                        } else {
                            workingTasks.removeLast();
                        }
                    } else {
                        System.err.println("Null space!");
                        new RuntimeException("Null Space!!").printStackTrace();
                        battleTx.abort();
                    }
                } catch (TransactionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Utils.log("Bad results array");
                try {
                    battleTx.abort();
                } catch (UnknownTransactionException e) {
                    e.printStackTrace();
                } catch (CannotAbortException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            try {
                btl.lock();
                battleTx = null;
            } finally {
                btl.unlock();
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
            battle.addRobot(battlingRobotsVector.elementAt(i));
        }

        battleThread.setUncaughtExceptionHandler(new ExceptionHandler());
        battleThread.start();
        try {
            battleThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    Transaction getBattleTx() {
        return battleTx;
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
                return extension.equalsIgnoreCase(".battle");
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
//        Vector<RobotPeer> vr = battle.getRobots();
//        Vector<RobotPeer> orderedRobots = new Vector<RobotPeer>();
//        if (vr.elementAt(0).getRobotClassManager() == ralph && vr.elementAt(1).getRobotClassManager() == alice) {
//            orderedRobots.add(0, vr.elementAt(0));
//            orderedRobots.add(1, vr.elementAt(1));
//        } else {
//            orderedRobots.add(0, vr.elementAt(1));
//            orderedRobots.add(1, vr.elementAt(0));
//        }

        robocode.control.RobotResults results[] = new robocode.control.RobotResults[orderedRobots.size()];

        for (int i = 0; i < results.length; i++) {
            RobotStatistics stats = orderedRobots.elementAt(i).getRobotStatistics();

            String name = orderedRobots.get(i).getRobotClassManager().getName(); // this is the name we passed in
            results[i] = new robocode.control.RobotResults(
                    orderedRobots.elementAt(i).getRobotClassManager().getControlRobotSpecification(),
                    name, (i + 1), stats);
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
            try {
                getBattleTx().abort();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}
