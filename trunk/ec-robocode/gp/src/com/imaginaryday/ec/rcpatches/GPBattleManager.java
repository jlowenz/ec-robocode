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


import com.imaginaryday.util.SpaceFinder;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
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
import robocode.repository.FileSpecification;
import robocode.repository.RobotSpecification;
import robocode.repository.TeamSpecification;
import robocode.util.Constants;
import robocode.util.Utils;

import javax.swing.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
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

    public void startNewBattle(BattleProperties battleProperties, boolean exit) {
        this.battleProperties = battleProperties;

        // Load ClassManagers need 2 GPRobots.
        GPRobotClassManager gpcm1 = new GPRobotClassManager();
        GPRobotClassManager gpcm2 = new GPRobotClassManager();

         

        /*
        * Get prebuilts from the system and build a map of them
        */
        Vector<FileSpecification> robotSpecificationsVector = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsVector(
                false, false, false, false, false, false);
        Utils.log("num fs: " + robotSpecificationsVector.size());
        for (FileSpecification fs : robotSpecificationsVector) fs.toString();

        Map<String, RobotClassManager> standardBots = new HashMap<String, RobotClassManager>();


        if (battleProperties.getSelectedRobots() != null) {
            StringTokenizer tokenizer;
            String sr =  battleProperties.getSelectedRobots();
            Utils.log("Selected robots = " + battleProperties.getSelectedRobots());
            tokenizer = new StringTokenizer(battleProperties.getSelectedRobots(), ",");

            while (tokenizer.hasMoreTokens()) {
                String bot = tokenizer.nextToken();

                for (int i = 0; i < robotSpecificationsVector.size(); i++) {
                    FileSpecification currentFileSpecification = (FileSpecification) robotSpecificationsVector.elementAt(
                            i);

                    if (currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
                        if (currentFileSpecification instanceof RobotSpecification) {
                            RobotSpecification current = (RobotSpecification) currentFileSpecification;
                            standardBots.put(current.getName(), new RobotClassManager(current));
                            break;
                        } else if (currentFileSpecification instanceof TeamSpecification) {
                            System.err.println("Teams not supported");
                        }
                    } else {
                        Utils.log(currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion() +
                        " != " + bot);
                    }
                }
            }
            Utils.log("Standard bots");
            Utils.log(standardBots.toString());
        } else {
            Utils.log("Selected robots = null!");
            return;
        }

        JavaSpace space = null;
        try {
            space = new SpaceFinder().getSpace();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (space == null) {
            System.err.println("Frak");
            return;
        }

        ResultForwarder rl = new ResultForwarder(space);
        manager.setListener(rl);

        boolean done = false;
        while (!done) {
            Utils.log("Looking for task");
            Entry taskTemplate = new GPBattleTask();
            GPBattleTask task = null;
            try {
                task = (GPBattleTask) space.take(taskTemplate, null, 120000);
                Utils.log((task == null) ? "null" : task.toString());
            } catch (UnusableEntryException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (TransactionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (RemoteException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            if (task == null) continue;

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


            }
        }


    }

    class ResultForwarder implements RobocodeListener {

        private GPBattleTask battleTask;
        private JavaSpace space;

        ResultForwarder(JavaSpace space) {
            this.space = space;
        }

        public void battleComplete(BattleSpecification battle, RobotResults[] results) {

            if (results != null && results.length == 2) {
                GPBattleResults res = new GPBattleResults(battleTask,
                        GPFitnessCalc.getFitness(results[0]),
                        GPFitnessCalc.getFitness(results[1]));


                try {
                    space.write(res, null, 1000000);
                } catch (TransactionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
                Utils.log("Bad results array");
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

        if (manager.isGUIEnabled()) {
            manager.getWindowManager().getRobocodeFrame().getBattleView().setBattleField(battleField);
        }
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
        if (manager.isGUIEnabled()) {
            manager.getWindowManager().getRobocodeFrame().getBattleView().setVisible(true);
            manager.getWindowManager().getRobocodeFrame().getBattleView().setInitialized(false);
        }

        for (int i = 0; i < battlingRobotsVector.size(); i++) {
            battle.addRobot((RobotClassManager) battlingRobotsVector.elementAt(i));
        }

        if (manager.isGUIEnabled()) {
            manager.getWindowManager().getRobocodeFrame().getRobocodeMenuBar().getBattleSaveAsMenuItem().setEnabled(true);
            manager.getWindowManager().getRobocodeFrame().getRobocodeMenuBar().getBattleSaveMenuItem().setEnabled(true);

            if (manager.getWindowManager().getRobocodeFrame().getPauseResumeButton().getText().equals("Resume")) {
                manager.getWindowManager().getRobocodeFrame().pauseResumeButtonActionPerformed();
            }

            manager.getRobotDialogManager().setActiveBattle(battle);
        }
        battleThread.start();
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
            RobotStatistics stats = ((RobotPeer) orderedRobots.elementAt(i)).getRobotStatistics();

            results[i] = new robocode.control.RobotResults(
                    ((RobotPeer) orderedRobots.elementAt(i)).getRobotClassManager().getControlRobotSpecification(), (i + 1),
                    (int) stats.getTotalScore(), (int) stats.getTotalSurvivalScore(), (int) stats.getTotalWinnerScore(),
                    (int) stats.getTotalBulletDamageScore(), (int) stats.getTotalKilledEnemyBulletScore(),
                    (int) stats.getTotalRammingDamageScore(), (int) stats.getTotalKilledEnemyRammingScore(),
                    stats.getTotalFirsts(), stats.getTotalSeconds(), stats.getTotalThirds());
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
}
