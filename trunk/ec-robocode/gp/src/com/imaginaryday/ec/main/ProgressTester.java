package com.imaginaryday.ec.main;

import com.imaginaryday.ec.rcpatches.GPBattleResults;
import com.imaginaryday.ec.rcpatches.GPBattleTask;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.io.*;
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
    private final TransactionManager transactionManager;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Writer summaryWriter;
    private Writer detailsWriter;
    private static final int MAX_TAKE_COUNT = 60;
    private GPBattleTask[] taskArray;


    public ProgressTester(JavaSpace space, String filename, TransactionManager transactionManager) {

        this.space = space;
        this.transactionManager = transactionManager;
        try {
            summaryWriter = new FileWriter("progressSummary.log", false);
            detailsWriter = new FileWriter("progressDetails.log", false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
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

        int numBattles = sampleBots.length * population.size();
        taskArray = new GPBattleTask[numBattles];
        int battle = 0;
        for (Member m : population) {
            for (String s : sampleBots) {
                GPBattleTask task = new GPBattleTask(generation, battle, m, s);
                taskArray[battle] = task;
                submitTask(task);
                battle++;
            }
        }

        return battle;
    }

    private Map<Member, List<GPBattleResults>> collectResults(List<Member> population, int numBattles) {
        int takeCount = 0;
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
                while (res == null) {
                    if (takeCount >= MAX_TAKE_COUNT) {
                        resubmitTasks();
                        takeCount = 0;
                    }
                    Transaction t = TransactionFactory.create(transactionManager, 30000).transaction;
                    res = (GPBattleResults) space.takeIfExists(template, t, 0);
                    if (res == null) {
                        Thread.sleep(2000);
                        takeCount++;
                        t.commit();
                    } else {
                        takeCount = 0;
                        t.commit();
                        if (taskArray[res.battle] != null) {
                            // not a duplicate
                            taskArray[res.battle] = null;
                        } else {
                            // was a duplicate
                            res = null;
                        }
                    }
                }
                logger.info(res.toString());
            } catch (UnusableEntryException e) {
                e.printStackTrace();
            } catch (TransactionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (LeaseDeniedException e) {
                e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
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

    private void submitTask(GPBattleTask task) {
        try {
            logger.info(task.shortString());
            Transaction tran = TransactionFactory.create(transactionManager, 60000).transaction;
            space.write(task, tran, Lease.FOREVER);
            tran.commit();
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LeaseDeniedException e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        }
    }

    private void resubmitTasks() {
        for (GPBattleTask t : taskArray) {
            if (t != null) {
                try {
                    if (space.readIfExists(t, null, 0) == null) {
                        submitTask(t);
                    }
                } catch (UnusableEntryException e) {
                    e.printStackTrace();
                } catch (TransactionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private void printResults(Map<Member, List<GPBattleResults>> results) {
        printDetails(results);
        printSummary(results);
    }

    private void printDetails(Map<Member, List<GPBattleResults>> results) {
        StringBuffer sb = new StringBuffer();

        for (java.util.Map.Entry<Member, List<GPBattleResults>> e : results.entrySet()) {

            for (GPBattleResults r : e.getValue()) {
                sb.append(r.getSummary_CSV()).append('\n');
            }
        }
        logger.info(sb.toString());
        try {
            detailsWriter.write(sb.toString());
            detailsWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        }

    }

    private void printSummary(Map<Member, List<GPBattleResults>> results) {
        StringBuffer sb = new StringBuffer();

                int maxBeaten = 0;
                int totalBeaten = 0;
                int battles = 0;

                for (Member m : results.keySet()) {
                    int beaten = 0;
                    for (GPBattleResults r : results.get(m)) {
                        if (r.score1 > r.score2) {
                            ++beaten;
                        }
                        ++battles;
                    }
                    if (beaten > maxBeaten) maxBeaten = beaten;
                    totalBeaten += beaten;
                }
                double wp = (double)totalBeaten / (double)battles;
                sb.append(maxBeaten).append(',').append(wp).append('\n');
                try {
                    summaryWriter.write(sb.toString());
                    summaryWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
                }

    }

}
