package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.GeneticOperators;
import static com.imaginaryday.ec.gp.GeneticOperators.pseudoRoot;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.gp.NodeFactory;
import com.imaginaryday.ec.gp.TreeFactory;
import com.imaginaryday.ec.main.nodes.*;
import com.imaginaryday.ec.rcpatches.GPBattleResults;
import com.imaginaryday.ec.rcpatches.GPBattleTask;
import com.imaginaryday.util.F;
import com.imaginaryday.util.ServiceFinder;
import com.imaginaryday.util.Tuple;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author rbowers
 *         Date: Nov 5, 2006
 *         Time: 3:48:23 PM
 */
public class Driver implements Runnable {

    static {
        NodeFactory nf = NodeFactory.getInstance();
        try {
            nf.loadNode(BulletBearing.class);
            nf.loadNode(BulletEnergy.class);
            nf.loadNode(BulletSpeed.class);
            nf.loadNode(CurrentRadarHeading.class);
            nf.loadNode(CurrentTurretHeading.class);
            nf.loadNode(VectorConstant.class);
            nf.loadNode(EnemyEnergy.class);
            nf.loadNode(EnemyHeading.class);
            nf.loadNode(EnemySpeed.class);
            nf.loadNode(EnergyLevel.class);
            nf.loadNode(GunHeat.class);
            nf.loadNode(HitByBullet.class);
            nf.loadNode(HitByBulletAge.class);
            nf.loadNode(HitWall.class);
            nf.loadNode(HitWallAge.class);
            nf.loadNode(IsMoving.class);
            nf.loadNode(IsRadarMoving.class);
            nf.loadNode(IsTurretMoving.class);
            nf.loadNode(Rammed.class);
            nf.loadNode(RammedAge.class);
            nf.loadNode(RammerBearing.class);
            nf.loadNode(RamMyFault.class);
            nf.loadNode(RotateVector.class);
            nf.loadNode(ScaleVector.class);
            nf.loadNode(ScannedEnemy.class);
            nf.loadNode(ScannedEnemyAge.class);
            nf.loadNode(VectorHeading.class);
            nf.loadNode(VectorLength.class);
            nf.loadNode(VectorToEnemy.class);
            nf.loadNode(VectorToForwardWall.class);
            nf.loadNode(VectorToNearestWall.class);
            nf.loadNode(MakeFiringPair.class);
            nf.loadNode(FiringPairConstant.class);
            nf.loadNode(MakeDirectionPair.class);
            nf.loadNode(DirectionPairConstant.class);
            nf.loadNode(DotProduct.class);
            nf.loadNode(NegateVector.class);
            nf.loadNode(VectorFromHeading.class);
            nf.loadNode(GoingForward.class);
            nf.loadNode(MySpeed.class);
//            nf.loadNode(CurrentVector.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        GeneticOperators go = GeneticOperators.getInstance();
        go.addMutation(new VectorConstantMutation());
        go.addMutation(new FiringPairConstantMutation());
    }

    private JavaSpace space = null;

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Random rand = new Random();

    private Date endDate;

    private static DecimalFormat df = new DecimalFormat("00");
    private int numGenerations = 601;
    private int numRandomGenerations = 24;
    private int generationCount = 0;
    private int treeDepth = 5;
    private final double alpha = .5;
    private final double beta = 1.5;
	private int eliteCount = 1;
    private int cullCount = 1;
    private double crossoverProbability = 0.95;
    private double mutationProbability = 0.05;
    private int testFreq = 5;
    private int populationSize = 25;
    private boolean readPopulation = false;
    private String popFile = "";
    private String progLogFile = System.getProperty("user.home") + System.getProperty("file.separator") + "progress.log";
    private String generationLogFile = System.getProperty("user.home") + System.getProperty("file.separator") + "battle.log";
    private String robotLogFile = System.getProperty("user.home") + System.getProperty("file.separator") + "robots.log";
    private String fitnessLog = System.getProperty("user.home") + System.getProperty("file.separator") + "fitness.log";
    private Writer battleWriter = null;
    private Writer robots = null;
    private Writer fitnessWriter = null;
//    private Map<Member, List<GPBattleResults>> resultMap = null;
    private TransactionManager transactionManager;

    // used to retry missing tasks on timeout
    private static final int MAX_TAKE_COUNT = 150;

    public Driver() {
        try {
            battleWriter = new FileWriter(generationLogFile, true);
            robots = new FileWriter(robotLogFile, true);
            fitnessWriter = new FileWriter(fitnessLog, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
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
            } else if (args[i].equals("-r") && (i < args.length + 1)) {
                d.popFile = args[i + 1];
                d.readPopulation = true;
                i++;
            } else if (args[i].equals("-pl") && (i < args.length + 1)) {
                d.progLogFile = args[i + 1];
                i++;
            } else if (args[i].equals("-pl") && (i < args.length + 1)) {
                d.generationLogFile = args[i + 1];
                i++;
            } else if (args[i].equals("-rand") && (i < args.length + 1)) {
                d.numRandomGenerations = Integer.parseInt(args[i + 1]);
                i++;
            } else {
                System.out.println("Not understood: " + args[i]);
                return;
            }
        }

        d.run();

    }

    public List<Member> genInitialPopulation(int gen, int popSize) {
        List<Member> members = new ArrayList<Member>();

        NodeFactory nf = NodeFactory.getInstance();
        TreeFactory tf = new TreeFactory(nf);

        for (int i = 0; i < popSize; i++) {
            Member m = new Member(gen, i);
            m.setMoveProgram(tf.generateRandomTree(treeDepth, DirectionPair.class));
            m.setRadarProgram(tf.generateRandomTree(treeDepth, Number.class));
            m.setShootProgram(tf.generateRandomTree(treeDepth, FiringPair.class));
            m.setTurretProgram(tf.generateRandomTree(treeDepth, Number.class));
            members.add(m);
        }
        return members;
    }

    private int getNumTasks(int popSize) {
        return (popSize * (popSize - 1)) / 2 + popSize;
    }

    public void run() {
        ProgressTester progressTester;
        FinalBotTester finalBotTester;

        while (transactionManager == null) {
            try {
                System.err.println("Looking for TM");
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

        try {
            System.err.println("Looking for space");
            space = new ServiceFinder().getSpace();

            if (getSpace() == null) {
                return;
            }
            System.err.println("Looking for space");
            progressTester = new ProgressTester(space, progLogFile, transactionManager);
            finalBotTester = new FinalBotTester(space, progLogFile, transactionManager);
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Could not find space");
            return;
        }

        /*
         * Get initial population
         */
        List<Member> population;
        Map<Member,List<GPBattleResults>> results;
        if (readPopulation) {
            // this population already has fitness values
            population = readPopulation(popFile);
            generationCount++;
        } else {
            // this population doesn't have fitness values
            long start = System.currentTimeMillis();
            logger.info("Calculating fitness of initial population...");
            population = genInitialPopulation(0, populationSize);
            results = calculateFitness(population, 0);
            printResults(battleWriter, robots, fitnessWriter, 0, results);
            persistPopulation(0, population);
            logger.info("execution time: " + getTime(start, System.currentTimeMillis()));
            generationCount = 1;
        }

        /*
         * Do random generations creation
         */
        while (generationCount < numRandomGenerations) {
            long start = System.currentTimeMillis();
            logger.info("Calculating new random population...");
            List<Rank> rankedPopulation = rankMembers(population);
            List<Rank> top = rankedPopulation.subList(rankedPopulation.size()-generationCount,
                                                      rankedPopulation.size());
            population = genInitialPopulation(generationCount, populationSize-generationCount);
            population.addAll(F.map(new F.lambda1<Member, Rank>() {
                protected Member _call(Rank A) {
                    return A.member;
                }
            }, top));
            results = calculateFitness(population, generationCount);
            printResults(battleWriter, robots,  fitnessWriter, generationCount, results);
            persistPopulation(generationCount, population);
            logger.info("Gen " + generationCount + " execution time: " + getTime(start, System.currentTimeMillis()));
            generationCount++;
        }

        /*
         * Main loop of the evolutionary algorithm.
         */
        while (generationCount < numGenerations) {
            long genStart = System.currentTimeMillis();
            // Perform selection and generate the next generation
            population = selectAndBreed(population, generationCount, eliteCount, alpha, beta);
            // calculate the fitness of the new population
            results = calculateFitness(population, generationCount);
            // record the fitness
            printResults(battleWriter, robots, fitnessWriter, generationCount, results);
            // Periodically measure against the canned bots
            if ((generationCount % testFreq) == 0) {
                progressTester.testProgress(population, generationCount);
            }
            // snapshot the population
            persistPopulation(generationCount, population);
            logger.info("Generation " + generationCount + " execution time: " +
                    getTime(genStart, System.currentTimeMillis()));
            generationCount++;
        }

        // Test the population against the entire set of bots.
        logger.info("Starting final test");
        finalBotTester.testProgress(population, generationCount);
        logger.info("Done with final test");

        try {
            battleWriter.close();
            robots.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTime(long start, long end)
    {
        double delta = (double) end - start;
        logger.fine("Delta milliseconds: " + delta);
        double secs = delta * 0.001;
        double mins = Math.floor(secs / 60.0);
        secs = secs - (mins * 60.0);
        return df.format(mins) + ":" + df.format(secs);
    }

    private Map<Member,List<GPBattleResults>> calculateFitness(List<Member> population, int gen) {
        /*
        * Build coevolutionary battle set and submit
        */
        GPBattleTask[] taskArray = submitBattles(population, gen);

        /*
         * collect results.
         */
        return collectResults(population, taskArray);
    }

    private List<Member> readPopulation(String filename) {
        try {
            logger.info("reading " + filename);
            InputStream is = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(is);
            Snapshot snapshot = (Snapshot) ois.readObject();
            this.generationCount = snapshot.getGeneration();
            this.populationSize = snapshot.getPopSize();
            return snapshot.getPopulation();
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void persistPopulation(int generation, List<Member> population) {
        logger.info("Persisting population for generation " + generation);
        StringBuilder sb = new StringBuilder().append("population").append(generation).append(".objs");

        OutputStream os;
        try {
            os = new FileOutputStream(sb.toString());
            ObjectOutputStream oos = new ObjectOutputStream(os);
            Snapshot snapshot = new Snapshot(generation, population.size(), population);
            oos.writeObject(snapshot);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JavaSpace getSpace() {
        return space;
    }

    private static class Rank {

        public Member member;
        public int rank;

        public Rank(Member member, int rank) {
            this.member = member;
            this.rank = rank;
        }
    }

    public List<Member> selectAndBreed(final List<Member> oldPopulation,
                                       int gen,
                                       int numElite,
                                       final double alpha,
                                       final double beta) {
        F.lambda1<Double, Rank> probDist = new F.lambda1<Double, Rank>() {
            public Double _call(Rank rank) {
                double r = rank.rank;
                double P = oldPopulation.size();
                return (alpha + (r / (P - 1) * (beta - alpha))) / P;
            }
        };
        // rank members
        List<Rank> rankedPopulation = rankMembers(oldPopulation);

        double P = oldPopulation.size(); // 24
        int count = (int) (P - numElite); // 24 - 2 = 22
        // sample members

        // SUS implementation is order preserving
        List<Member> newPopulation = stochasticUniversalSampling(rankedPopulation, probDist, count); // 22
        for (Member m : newPopulation) {
            m.setGeneration(gen+1);
        }

        // culling
        if (cullCount > 0) {
            List<Member> newMaterial = genInitialPopulation(gen+1, cullCount);
            for (int i = 0; i < cullCount; i++) {
                newPopulation.remove(0);
            }
            newPopulation.addAll(newMaterial);
        }

        // crossover/recombine
        newPopulation = crossover(newPopulation);
        // mutation
        newPopulation = mutate(newPopulation);

        // elitism (top elitismPercentage)
        for (int i = count; i < P; i++) {
            newPopulation.add(new Member(rankedPopulation.get(i).member));
        }

        if (newPopulation.size() != oldPopulation.size()) throw new RuntimeException("Bad population size!");
        return newPopulation;
    }

    private List<Member> mutate(List<Member> selection) {
        GeneticOperators ops = GeneticOperators.getInstance();

        for (ListIterator<Member> li = selection.listIterator(); li.hasNext();) {
            Member m = li.next();

            Node move = m.getMoveProgram();
            Node turret = m.getTurretProgram();
            Node radar = m.getRadarProgram();
            Node firing = m.getShootProgram();

            if (rand.nextDouble() < mutationProbability) {
                m.setMoveProgram(ops.mutate(pseudoRoot(move)).getChild(0));
                m.setTurretProgram(ops.mutate(pseudoRoot(turret)).getChild(0));
                m.setRadarProgram(ops.mutate(pseudoRoot(radar)).getChild(0));
                m.setShootProgram(ops.mutate(pseudoRoot(firing)).getChild(0));
            }
        }

        return selection;
    }


    private List<Member> crossover(List<Member> selection) {
        GeneticOperators ops = GeneticOperators.getInstance();

        List<Member> replacements = new ArrayList<Member>();
        if (selection.size() % 2 == 1) { // this MAKES it even!
            int which = rand.nextInt(selection.size());
            replacements.add(selection.get(which));
            selection.remove(which);
        }

        while (selection.size() > 0) {
            // pick 2 random parents
            Member m = selection.get(rand.nextInt(selection.size()));
            selection.remove(m);
            Member n = selection.get(rand.nextInt(selection.size()));
            selection.remove(n);

            if (rand.nextDouble() < crossoverProbability) {
                Node moveA = pseudoRoot(m.getMoveProgram());
                Node turretA = pseudoRoot(m.getTurretProgram());
                Node radarA = pseudoRoot(m.getRadarProgram());
                Node firingA = pseudoRoot(m.getShootProgram());

                Node moveB = pseudoRoot(n.getMoveProgram());
                Node turretB = pseudoRoot(n.getTurretProgram());
                Node radarB = pseudoRoot(n.getRadarProgram());
                Node firingB = pseudoRoot(n.getShootProgram());

                Tuple.Two<Node, Node> move = ops.crossover(moveA, moveB);
                Tuple.Two<Node, Node> turret = ops.crossover(turretA, turretB);
                Tuple.Two<Node, Node> radar = ops.crossover(radarA, radarB);
                Tuple.Two<Node, Node> firing = ops.crossover(firingA, firingB);

                m.setMoveProgram(move.getFirst().getChild(0));
                n.setMoveProgram(move.getSecond().getChild(0));
                m.setTurretProgram(turret.getFirst().getChild(0));
                n.setTurretProgram(turret.getSecond().getChild(0));
                m.setRadarProgram(radar.getFirst().getChild(0));
                n.setRadarProgram(radar.getSecond().getChild(0));
                m.setShootProgram(firing.getFirst().getChild(0));
                n.setShootProgram(firing.getSecond().getChild(0));
            }
            replacements.add(m);
            replacements.add(n);
        }

        return replacements;
    }

    private List<Rank> rankMembers(List<Member> oldPopulation) {
        List<Member> pop = new ArrayList<Member>();
        pop.addAll(oldPopulation);
        Collections.sort(pop);
        List<Rank> ranks = new ArrayList<Rank>();
        int i = 0;
        for (Member m : pop) ranks.add(new Rank(m, i++));
        return ranks;
    }

    /**
     * Sample the population for parents to be used during recombination/mutation.
     *
     * @param pr    a probability function
     * @param count the number of parents to sample from the population
     * @param p     the current population
     * @return a list of sampled parents based on the probability function
     */
    private List<Member> stochasticUniversalSampling(List<Rank> p, F.lambda1<Double, Rank> pr, int count) {
        List<Member> samples = new ArrayList<Member>();
        double u = rand.nextDouble() * (1.0 / (double) count);
        double sum = 0.0;
        for (Rank r : p) {
            int c = 0; // # of children assigned to indiv i
            sum = sum + pr.call(r);
            while (u < sum) {
                c++;
                u = u + 1.0 / (double) count;
            }
            logger.fine("Rank " + r.rank + " gets " + c + " individuals!");
            for (int j = 0; j < c; j++) samples.add(new Member(r.member));
        }
        return samples;
    }

    private Map<Member,List<GPBattleResults>> collectResults(List<Member> population, GPBattleTask[] taskArray) {
        int takeCount = 0;
        int retrieved = 0;
        Entry template = new GPBattleResults();

        Map<Member, List<GPBattleResults>> resultMap = new HashMap<Member, List<GPBattleResults>>();
        for (Member m : population) {
            resultMap.put(m, new LinkedList<GPBattleResults>());
        }

        while (retrieved < taskArray.length) {
            GPBattleResults res = null;
            Transaction tran = null;
            try {
                while (res == null) {
                    if (takeCount >= MAX_TAKE_COUNT) {
                        // the problem with this is that the tasks could still be in the space
                        // with NO workers to work on them!
                        resubmitTasks(taskArray);
                        takeCount = 0;
                    }
                    tran = TransactionFactory.create(transactionManager, 60000).transaction;
                    res = (GPBattleResults) getSpace().takeIfExists(template, tran, 500);
                    if (res == null) {
                        tran.commit();
                        Thread.sleep(2000);
                        takeCount++;
                    } else {
                        takeCount = 0;
                        tran.commit();
                        // got a result - check to make sure it's not a duplicate
                        if (taskArray[res.battle] != null) {
                            // NOT a duplicate
                            taskArray[res.battle] = null; // record that we've received the results for this task
                        } else {
                            // it WAS a duplicate - null it out and look for more tasks
                            res = null;
                        }
                    }
                }
                logger.info(res.toString());
            } catch (RemoteException e) {
                try {
                    tran.abort();
                } catch (UnknownTransactionException e1) {
                    e1.printStackTrace();
                } catch (CannotAbortException e1) {
                    e1.printStackTrace();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            } catch (Exception e) {
                try {
                    tran.abort();
                } catch (UnknownTransactionException e1) {
                    e1.printStackTrace();
                } catch (CannotAbortException e1) {
                    e1.printStackTrace();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

            if (res != null) {
                for (Member member : population) {
                    if (member.getName().equals(res.getRobot1())) {
                        member.addFitness(res.getFitness1());
                        // only add once
                        List<GPBattleResults> r2 = resultMap.get(member);
                        r2.add(res);
                    }
                    if (member.getName().equals(res.getRobot2())) {
                        member.addFitness(res.getFitness2());
                    }
                }
                ++retrieved;
                logger.info("Collected " + retrieved + " of " + taskArray.length + " results");
            }
        }
        return resultMap;
    }

    private void resubmitTasks(GPBattleTask[] taskArray) {
        logger.warning("RESUBMITTING TASKS at " + new Date());
        for (GPBattleTask t : taskArray) {
            if (t != null) {
                try {
                    if (getSpace().readIfExists(t, null, 0) != null) {
                        submitBattle(t);
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

    private void printResults(Writer battleWriter,
                              Writer robots,
                              Writer fitnessWriter,
                              int gen,
                              Map<Member,List<GPBattleResults>> results) {

        try {
            StringBuilder sb = new StringBuilder();
            for (java.util.Map.Entry<Member, List<GPBattleResults>> e : results.entrySet()) {
                for (GPBattleResults r : e.getValue()) {
                    sb.append(r.getSummary_CSV()).append('\n');
                }
            }
            battleWriter.write(sb.toString());
            battleWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Member> l = new ArrayList<Member>(results.keySet());
        Collections.sort(l, new Comparator<Member>() {
            public int compare(Member o, Member o1) {
                return o.getName().compareTo(o1.getName());
            }
        });

        try {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(gen).append(",");
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                Member m = (Member) iter.next();
                sb2.append(Double.toString(m.getFitness()));
                if (iter.hasNext()) {
                    sb2.append(',');
                } else {
                    sb2.append('\n');
                }
            }
            robots.write(sb2.toString());
            robots.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            StringBuilder sb3 = new StringBuilder();
            double minFitness = Double.MAX_VALUE;
            double maxFitness = Double.MIN_VALUE;
            double sumFitness = 0.0;
            for (Member m : results.keySet()) {
                double f = m.getFitness();

                if (f > maxFitness) maxFitness = f;
                else if (f < minFitness) minFitness = f;

                sumFitness += f;
            }
            double meanFitness = sumFitness / (double)results.keySet().size();
            sb3.append(minFitness).append(',').append(meanFitness).append(',').append(maxFitness).append('\n');
            fitnessWriter.write(sb3.toString());
            fitnessWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void submitBattle(GPBattleTask t) {
        try {
            logger.info("Submitting: " + t.shortString());
            Transaction tran = TransactionFactory.create(transactionManager, 60000).transaction;
            Lease l = getSpace().write(t, tran, Lease.FOREVER);
            tran.commit();
            if (l.getExpiration() != Lease.FOREVER) {
                logger.warning("Lease returned is not FOREVER: " + l.getExpiration());
            }
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public GPBattleTask[] submitBattles(List<Member> population, int gen) {
        GPBattleTask[] taskArray = new GPBattleTask[getNumTasks(population.size())];
        int battle = 0;
        for (int i = 0; i < population.size(); ++i) {
            for (int j = i; j < population.size(); ++j) {
                GPBattleTask task = new GPBattleTask(gen, battle, population.get(i), population.get(j));
                taskArray[battle] = task; // record the task for replay later
                submitBattle(task);
                battle++;
            }

        }
        return taskArray;
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
