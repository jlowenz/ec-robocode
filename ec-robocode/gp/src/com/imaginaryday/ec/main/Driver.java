package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.GeneticOperators;
import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.gp.NodeFactory;
import com.imaginaryday.ec.gp.TreeFactory;
import com.imaginaryday.ec.main.nodes.*;
import com.imaginaryday.ec.rcpatches.GPBattleResults;
import com.imaginaryday.ec.rcpatches.GPBattleTask;
import com.imaginaryday.util.SpaceFinder;
import info.javelot.functionalj.Function1;
import info.javelot.functionalj.Function1Impl;
import info.javelot.functionalj.FunctionException;
import info.javelot.functionalj.tuple.Pair;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;
import org.jscience.mathematics.vectors.VectorFloat64;

import java.io.*;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
//            nf.loadNode(CurrentVector.class);
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
            nf.loadNode(MakePair.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private JavaSpace space = null;

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Random rand = new Random();

    private Date endDate;

    private int numGenerations = 100;
    private int generationCount = 0; // ??
    private int treeDepth = 8;
    private final int alpha = 0;
    private final int beta = 2;
    private double elitismPercentage = 0.1;
    private double crossoverProbability = .6;
    private double mutationProbability = .1;
    private int testFreq = 5;
    private int populationSize = 40; // KEEP THIS EVEN! yeah, it's a hack, deal with it :-/
    private boolean readPopulation = false;
    private String popFile = "";
    private String progLogFile = "progressLog";
    private String generationLogFile = "generationLog";
    private FileOutputStream output = null;
    private Map<Member, List<GPBattleResults>> resultMap = null;

    public Driver() {
        try {
            output = new FileOutputStream(generationLogFile, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        }
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
            } else {
                System.out.println("Not understood: " + args[i]);
                return;
            }
        }

        d.run();

    }

    public List<Member> genInitialPopulation() {
        List<Member> members = new ArrayList<Member>();

        NodeFactory nf = NodeFactory.getInstance();
        TreeFactory tf = new TreeFactory(nf);

        for (int i = 0; i < populationSize; i++) {
            Member m = new Member(0, i);
            m.setMoveProgram(tf.generateRandomTree(treeDepth, VectorFloat64.class));
            m.setRadarProgram(tf.generateRandomTree(treeDepth, Number.class));
            m.setShootProgram(tf.generateRandomTree(treeDepth, Pair.class));
            m.setTurretProgram(tf.generateRandomTree(treeDepth, Number.class));
//            m.setName(new StringBuilder().append("Bot.").append(generationCount).append(".").append(i).toString());
            members.add(m);
        }
        return members;
    }

    public void run() {
        ProgressTester progressTester;
        try {
            space = new SpaceFinder().getSpace();

            if (space == null) {
                return;
            }

            progressTester = new ProgressTester(space, progLogFile);
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Could not find space");
            return;
        }

        /*
         * Generate initial population
         */
        List<Member> population;
        if (readPopulation) {
            population = readPopulation(popFile);
        } else {
            population = genInitialPopulation();
        }
        /*
         * Main loop of the evolutionary algorithm.
         */
        Date currentDate = new Date();
        while (/* endDate.compareTo(currentDate) > 0 && */ generationCount < numGenerations) {

            resultMap = new HashMap<Member, List<GPBattleResults>>();
            for (Member m : population) {
                resultMap.put(m, new ArrayList<GPBattleResults>());
            }

            /*
            * Build coevolutionary battle set and submit
            */
            int numBattles = submitBattles(population);

            /*
             * collect results.
             */
            collectResults(population, numBattles);
            printResults();

            /*
             * Periodically measure against the canned bots
             */
            if (generationCount != 0 && (generationCount % testFreq == 0)) {
                progressTester.testProgress(population, generationCount);
            }
            persistPopulation(generationCount, population);
            /*
             * Perform selection and generate the next generation
             */
             population = selectAndBreed(population);
            ++generationCount;
        }
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    private void persistPopulation(int generation, List<Member> population) {
        logger.info("Persisting population for generation " + generation);
        StringBuilder sb = new StringBuilder().append("population").append(generation).append(".objs");

        OutputStream os = null;
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

    private static class Rank {

        public Member member;
        public int rank;

        public Rank(Member member, int rank) {
            this.member = member;
            this.rank = rank;
        }
    }

    public List<Member> selectAndBreed(List<Member> oldPopulation) {
        Function1<Double, Rank> probDist = new Function1Impl<Double, Rank>() {
            public Double call(Rank rank) throws FunctionException {
                double a = alpha;
                double b = beta;
                double r = rank.rank;
                double P = populationSize;
                return (a + (r / (P - 1) * (b - a))) / P;
            }
        };
        // rank members
        List<Rank> rankedPopulation = rankMembers(oldPopulation);

        double P = populationSize; // 40
        int count = (int)(P-Math.ceil(P*elitismPercentage)); // 40 - 4.0 = 36
        // sample members
        List<Member> newPopulation = stochasticUniversalSampling(rankedPopulation, probDist, count); // 36
        for (Member m : newPopulation) {
            m.setGeneration(m.getGeneration()+1);
        }
        // crossover/recombine
        newPopulation = crossover(newPopulation);
        // mutation
        newPopulation = mutate(newPopulation);


        // elitism (top elitismPercentage)
        for (int i = count; i < populationSize; i++) {
            newPopulation.add(new Member(oldPopulation.get(i)));
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

            m.setMoveProgram((rand.nextDouble() < mutationProbability) ? ops.mutate(move) : move);
            m.setTurretProgram((rand.nextDouble() < mutationProbability) ? ops.mutate(turret) : turret);
            m.setRadarProgram((rand.nextDouble() < mutationProbability) ? ops.mutate(radar) : radar);
            m.setShootProgram((rand.nextDouble() < mutationProbability) ? ops.mutate(firing) : firing);
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
                Node moveA = m.getMoveProgram();
                Node turretA = m.getTurretProgram();
                Node radarA = m.getRadarProgram();
                Node firingA = m.getShootProgram();

                Node moveB = n.getMoveProgram();
                Node turretB = n.getTurretProgram();
                Node radarB = n.getRadarProgram();
                Node firingB = n.getShootProgram();

                Pair<Node, Node> move = ops.crossover(moveA, moveB);
                Pair<Node, Node> turret = ops.crossover(turretA, turretB);
                Pair<Node, Node> radar = ops.crossover(radarA, radarB);
                Pair<Node, Node> firing = ops.crossover(firingA, firingB);

                m.setMoveProgram(move.getFirst());
                n.setMoveProgram(move.getSecond());
                m.setTurretProgram(turret.getFirst());
                n.setTurretProgram(turret.getSecond());
                m.setRadarProgram(radar.getFirst());
                n.setRadarProgram(radar.getSecond());
                m.setShootProgram(firing.getFirst());
                n.setShootProgram(firing.getSecond());
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
    private List<Member> stochasticUniversalSampling(List<Rank> p, Function1<Double, Rank> pr, int count) {
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
            for (int j = 0; j < c; j++) samples.add(new Member(r.member));
        }
        return samples;
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
                while (res == null) {
                    res = (GPBattleResults) space.takeIfExists(template, null, 0);
                    if (res == null) Thread.sleep(2000);
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
                logger.info("Collected " + retrieved + " of " + numBattles + " results");
            }
        }
    }

    private void printResults() {
        StringBuffer sb = new StringBuffer();

        for (java.util.Map.Entry<Member, List<GPBattleResults>> e : resultMap.entrySet()) {

            for (GPBattleResults r : e.getValue()) {
                sb.append(r.getSummary_CSV()).append('\n');
            }
        }

        try {
            output.write(sb.toString().getBytes());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();  //Todo change body of catch statement use File | Settings | File Templates.
        }

    }

    public int submitBattles(List<Member> population) {

        int battle = 0;
        for (int i = 0; i < population.size(); ++i) {

            for (int j = i; j < population.size(); ++j) {

                GPBattleTask task = new GPBattleTask(generationCount, battle, population.get(i), population.get(j));

// submit
                try {
                    logger.info("Submitting: " + task.toString());
                    space.write(task, null, Long.MAX_VALUE);
                    battle ++;
                } catch (TransactionException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }
        return battle;
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
