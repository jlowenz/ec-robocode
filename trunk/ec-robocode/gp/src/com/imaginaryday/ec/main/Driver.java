package com.imaginaryday.ec.main;

import com.imaginaryday.ec.rcpatches.GPBattleResults;
import com.imaginaryday.ec.rcpatches.GPBattleTask;
import com.imaginaryday.util.SpaceFinder;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author rbowers
 *         Date: Nov 5, 2006
 *         Time: 3:48:23 PM
 */
public class Driver implements Runnable {

    private JavaSpace space = null;

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private final ExecutorService executor;
    private Date endDate;
    private int numGenerations;
    private int generationCount;
    private int testFreq = 5;
    private int populationSize = 11;
    private String[] sampleBots = new String[]{"sample.Corners", "sample.Crazy",
            "sample.Fire", "sample.MyFirstRobot",
            "sample.RamFire", "sample.SittingDuck", "sample.SpinBot",
            "sample.Target", "sample.Tracker", "sample.TrackFire", "sample.Walls"};

    public Driver() {
        executor = new ThreadPoolExecutor(4, 16, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    }

    public static void main(String[] args) {
        System.setSecurityManager(new RMISecurityManager());
        Driver d = new Driver();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-e") && (i < args.length + 1)) {
                try {
                    DateFormat df = new SimpleDateFormat();
                    d.setEndDate(df.parse(args[i + 1]));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
                i++;
            } else if (args[i].equals("-g") && (i < args.length + 1)) {
                int ng = Integer.parseInt(args[i + 1]);
                d.setNumGenerations(ng);
                i++;
            } else if (args[i].equals("-f") && (i < args.length + 1)) {
                int tf = Integer.parseInt(args[i + 1]);
                d.setTestFreq(tf);
                i++;
            } else if (args[i].equals("-p") && (i < args.length + 1)) {
                int pop = Integer.parseInt(args[i + 1]);
                d.setPopulationSize(pop);
                i++;
            } else {
                System.out.println("Not understood: " + args[i]);
                return;
            }
        }


        d.run();

    }

    public List<Member> genInitialPopulation() {
        List<Member> initialPop = new ArrayList<Member>();
        int counter = 0;
        for (String bot : sampleBots) {
            Member m = new Member(generationCount, counter++);
            m.setName(bot);
            initialPop.add(m);
        }

        return initialPop;
    }

    public void run() {

        try {
            space = new SpaceFinder().getSpace();

            if (space == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Could not find space");
            return;
        }

        /*
         * Generate initial population
         */
        List<Member> population = genInitialPopulation();

        /*
         * Main loop of the evolutionary algorithm.
         */
        Date currentDate = new Date();
        while (/* endDate.compareTo(currentDate) > 0 && */ generationCount <= numGenerations) {

            /*
             * Build coevolutionary battle set and submit
             */
            int numBattles = submitBattles(population);

            /*
             * collect results.
             */
            collectResults(population, numBattles);

            /*
             * Periodically measure against the canned bots
             */
            if (generationCount % testFreq == 0) {

            }

            /*
             * Perform selection and generate the next generation
             */
            population = selectAndBreed(population);


            currentDate = new Date();
        }


    }


    public List<Member> selectAndBreed(List<Member> oldPopulation) {


        return new ArrayList<Member>();

    }

    class FitnessComparator implements Comparator<Member> {

        public int compare(Member one, Member two) {
            if (one == null) throw new IllegalArgumentException("one is null");
            if (two == null) throw new IllegalArgumentException("two is null");

            if (one.getFitness() <= two.getFitness()) return -1;
            else return 1;
        }

    }


    public void collectResults(List<Member> population, int numBattles) {

        int retrieved = 0;
        Entry template = new GPBattleResults();


        while (retrieved < numBattles) {

            GPBattleResults res = null;
            try {
                while (res == null)
                    res = (GPBattleResults) space.takeIfExists(template, null, 120000);
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

    public int submitBattles(List<Member> population) {

        int battles = 0;
        for (int i = 0; i < population.size(); ++i) {

            for (int j = i; j < population.size(); ++j) {


                GPBattleTask task = new GPBattleTask(population.get(i), population.get(j));

// submit
                try {
                    logger.info("Submitting: " + task.toString());
                    space.write(task, null, Long.MAX_VALUE);
                    battles ++;
                } catch (TransactionException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }
        return battles;
    }


    public void setTestFreq(int testFreq) {
        this.testFreq = testFreq;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getNumGenerations() {
        return numGenerations;
    }

    public void setNumGenerations(int numGenerations) {
        this.numGenerations = numGenerations;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

}
