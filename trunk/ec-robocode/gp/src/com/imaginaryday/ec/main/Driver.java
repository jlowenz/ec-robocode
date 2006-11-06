package com.imaginaryday.ec.main;

import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;
import org.jini.rio.resources.client.JiniClient;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import robocode.gp.GPBattleResults;
import robocode.gp.GPBattleTask;

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
    private int testFreq;


    public Driver() {
        executor = new ThreadPoolExecutor(4, 16, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());


    }


    public static void main(String[] args) {
        Driver d = new Driver();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-ed") && (i < args.length + 1)) {
                try {
                    DateFormat df = new SimpleDateFormat();
                    d.setEndDate(df.parse(args[i + 1]));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
                i++;
            } else if (args[i].equals("-ng") && (i < args.length + 1)) {
                int ng = Integer.parseInt(args[i+1]);
                d.setNumGenerations(ng);
                i++;
            } else if (args[i].equals("-tf") && (i < args.length + 1)) {
                int tf = Integer.parseInt(args[i+1]);
                d.setTestFreq(tf);
                i++;
            } else {
                System.out.println("Not understood: " + args[i]);
                return;
            }
        }


        d.run();

    }

    public void run() {

        try {
            space = new SpaceFinder().getSpace();
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Could not find space");
            return;
        }

        /*
         * Generate initial population
         */

        /*
         * Main loop of the evolutionary algorithm.
         */
        Date currentDate = new Date();
        while (endDate.compareTo(currentDate) > 0 && generationCount <= numGenerations) {

            /*
             * Build coevolutionary battle set and submit
             */
            int numBattles = submitBattles(null/* Todo Replace with real battle */);

            /*
             * collect results.
             */
            Set<GPBattleResults> results = collectResults(numBattles);

            /*
             * Periodically measure against the canned bots
             */
            if (generationCount % testFreq == 0) {

            }

            /*
             * Perform selection and generate the next generation
             */

            currentDate = new Date();
        }






    }

    public Set<GPBattleResults> collectResults(int numBattles) {
        Set<GPBattleResults> resultSet = new TreeSet<GPBattleResults>();
        Entry template = new GPBattleResults();
        while (resultSet.size()  < numBattles) {
                 GPBattleResults res = null;
            try {
               res = (GPBattleResults) space.takeIfExists(template, null, 10000);
            } catch (UnusableEntryException e) {
                e.printStackTrace();
            } catch (TransactionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (res == null)  {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            resultSet.add(res);

        }
        return resultSet;
    }

    public int submitBattles(List specifications) {

        int battles = 0;
        for (int i = 0; i < specifications.size(); ++i) {

            for (int j = i; j < specifications.size();  ++i) {


                GPBattleTask task = new GPBattleTask();
                // Todo Set parameters on Battle Task

                // submit
                try {
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


    public void compileResults() {






    }


    public void setTestFreq(int testFreq) {
        this.testFreq = testFreq;
    }

    class SpaceFinder extends JiniClient {

        public SpaceFinder() throws Exception {
            super();
        }

        public JavaSpace getSpace() {
            ServiceTemplate template = new ServiceTemplate(null, new Class[]{JavaSpace.class}, null);

            for (ServiceRegistrar r : this.getRegistrars()) {
                JavaSpace js = null;
                try {
                    js = (JavaSpace) r.lookup(template);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (js != null) return js;
            }
            return null;
        }
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
}
