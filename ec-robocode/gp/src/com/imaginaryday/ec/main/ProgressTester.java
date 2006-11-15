package com.imaginaryday.ec.main;

import com.imaginaryday.ec.rcpatches.GPBattleResults;
import com.imaginaryday.ec.rcpatches.GPBattleTask;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
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


    public ProgressTester(JavaSpace space) {

        this.space = space;
    }

    public void testProgress(final List<Member> population, final int generation) {
        List<Member> newPop = clonePopulation(population);
        int battles = submitBattles(newPop, generation);
        collectResults(newPop, battles);
        printResults(newPop);
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
                    space.write(task, null, 120000);
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

    private void collectResults(List<Member> population, int numBattles) {

        int retrieved = 0;
        Entry template = new GPBattleResults();


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
                for (Member member : population) {

                    if (member.getName().equals(res.getRobot1())) {
                        member.addFitness(res.getFitness1());
                    }
                    /*
                    * use 2 ifs because it is possible for a robot
                    * to compete against itself
                    */
                    if (member.getName().equals(res.getRobot2())) {
                        member.addFitness(res.getFitness2());
                    }
                }
            }
        }

    }

    private void printResults(List<Member> population, int generation) {

    }
}
