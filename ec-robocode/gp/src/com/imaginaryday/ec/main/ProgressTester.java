package com.imaginaryday.ec.main;

import com.imaginaryday.ec.rcpatches.GPBattleResults;
import com.imaginaryday.ec.rcpatches.GPBattleTask;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: rbowers
 * Date: Nov 14, 2006
 * Time: 5:35:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressTester {

    private JavaSpace space;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private FileOutputStream output;


    public ProgressTester(JavaSpace space, String filename) {

        this.space = space;
        try {
            output = new FileOutputStream(filename, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        }
    }


    public void testProgress(final List<Member> population, final int generation) {
        List<Member> newPop = clonePopulation(population);
        int battles = submitBattles(newPop, generation);
        Map<Member, List<GPBattleResults>> results = collectResults(newPop, battles);
        printResults(results);
    }

    private List<Member> clonePopulation(final List<Member> population) {
        List<Member> newPop = new ArrayList<Member>(population.size());

        for (Member m : population) {
            newPop.add(new Member(m));
        }
        return newPop;
    }

    private int submitBattles(List<Member> population, int generation) {
        String[] sampleBots = new String[]{"Corners", "Crazy", "Fire", "MyFirstRobot",
                "RamFire", "SittingDuck", "SpinBot", "Target", "Tracker", "TrackFire", "Walls"};

        int battle = 0;
        for (Member m : population) {
            for (String s : sampleBots) {
                GPBattleTask task = new GPBattleTask(generation, battle, m, s);

                try {
                    logger.info(task.toString());
                    space.write(task, null, Long.MAX_VALUE);

                    battle ++;
                } catch (TransactionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }
        return battle;
    }

    private Map<Member, List<GPBattleResults>> collectResults(List<Member> population, int numBattles) {

        int retrieved = 0;
        Entry template = new GPBattleResults();

        Map<Member, List<GPBattleResults>> results = null;
        results = new HashMap<Member, List<GPBattleResults>>();
        for (Member m : population) {
            results.put(m, new ArrayList<GPBattleResults>());
        }

        while (retrieved < numBattles) {

            GPBattleResults res = null;
            try {
                while (res == null)
                    res = (GPBattleResults) space.takeIfExists(template, null, 120000);
                logger.info(res.toString());
            } catch (UnusableEntryException e) {
                e.printStackTrace();
            } catch (TransactionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (res != null) {
                for (Member member : results.keySet()) {
                    if (member.getName().equals(res.robot1)) {
                        List<GPBattleResults> r2 = results.get(member);
                        r2.add(res);
                        break;
                    }
                }
                logger.info("Collected " + retrieved + " of " + numBattles + " results");
                ++retrieved;
            }

        }
        return results;
    }

    private void printResults2(Map<Member, List<GPBattleResults>> results, int generation) {
        StringBuffer output = new StringBuffer();

        // Header
        output.append('\n').append("Results for generation ").append(generation).append('\n');

        for (java.util.Map.Entry<Member, List<GPBattleResults>> e : results.entrySet()) {

            output.append("Robot ").append(e.getKey().getName()).append('\n');
            output.append("------------------------------MOVE PROGRAM--------------------------------\n");
            output.append(e.getKey().getMoveProgram().toString());
            output.append("------------------------------TURRET PROGRAM------------------------------\n");
            output.append(e.getKey().getTurretProgram().toString());
            output.append("------------------------------SHOOT PROGRAM-------------------------------\n");
            output.append(e.getKey().getShootProgram().toString());
            output.append("------------------------------RADAR PROGRAM-------------------------------\n");
            output.append(e.getKey().getRadarProgram().toString());
            output.append("--------------------------------------------------------------------------\n");

            for (GPBattleResults r : e.getValue()) {
                output.append(r.getSummary()).append('\n');
            }
        }

        logger.info(output.toString());

    }

    private void printResults(Map<Member, List<GPBattleResults>> results) {
        StringBuffer sb = new StringBuffer();

        for (java.util.Map.Entry<Member, List<GPBattleResults>> e : results.entrySet()) {

            for (GPBattleResults r : e.getValue()) {
                sb.append(r.getSummary_CSV()).append('\n');
            }
        }
        logger.info(sb.toString());
        try {
            output.write(sb.toString().getBytes());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        }

    }
}
