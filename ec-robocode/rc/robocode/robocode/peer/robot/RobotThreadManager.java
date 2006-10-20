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
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer.robot;


import robocode.peer.RobotPeer;
import robocode.util.RobocodeDeprecated;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobotThreadManager {
	private RobotPeer robotPeer;
	private Thread runThread;
	private ThreadGroup runThreadGroup;
	private boolean disabled;
	private long cpuTime;

	public RobotThreadManager(RobotPeer robotPeer) {
		this.robotPeer = robotPeer;
		runThreadGroup = new ThreadGroup(robotPeer.getName());
		runThreadGroup.setMaxPriority(Thread.NORM_PRIORITY);
	}

	public void forceStop() {
		if (runThread != null && runThread.isAlive()) {
			try {
				runThread.setPriority(Thread.MIN_PRIORITY);
				runThread.interrupt();
				runThread.join(5000);
			} catch (Exception e) {}
			robotPeer.setRunning(false);
			robotPeer.getRobotStatistics().setNoScoring(true);
			if (runThread.isAlive()) {
				RobocodeDeprecated.stopThread(this, runThread);
			}
			try {
				runThread.join(5000);
			} catch (InterruptedException e) {} catch (Exception e) {}
			if (runThread.isAlive()) {
				Utils.log("Warning!  Unable to stop thread: " + runThread.getName());
			} else {
				robotPeer.out.println("SYSTEM: This robot has been stopped.  No score will be generated.");
				Utils.log(robotPeer.getName() + " has been stopped.  No score will be generated.");
			}
		}

		Thread[] threads = new Thread[10];
		int numThreads = runThreadGroup.enumerate(threads);

		if (numThreads == 1 && threads[0] == runThread) {
			return;
		}
		
		if (numThreads != 0) {
			robotPeer.getRobotStatistics().setNoScoring(true);
			robotPeer.out.println(
					"SYSTEM:  You still have " + numThreads + " running threads.  No score will be generated.");
		}
		for (int i = 0; i < numThreads; i++) {
			try {
				threads[i].setPriority(Thread.MIN_PRIORITY);
				RobocodeDeprecated.stopThread(this, threads[i]);
			} catch (Exception e) {}
		}
		for (int i = 0; i < numThreads; i++) {
			try {
				threads[i].join(1000);
			} catch (InterruptedException e) {
				robotPeer.out.println("SYSTEM:  Thread: " + threads[i].getName() + " join interrupted."); 
				Utils.log("Thread: " + threads[i].getName() + " join interrupted.");
			} catch (Exception e) {}
			if (threads[i].isAlive()) {
				Utils.log("Warning! Unable to stop thread: " + threads[i].getName());
			} else {
				robotPeer.out.println("SYSTEM:  Thread: " + threads[i].getName() + " has been stopped.");
				Utils.log("Thread: " + threads[i].getName() + " has been stopped.");
			}
		}
	}

	public ThreadGroup getThreadGroup() {
		return runThreadGroup;
	}

	public void cleanup() {
		try {
			runThreadGroup.destroy();
		} catch (Exception e) {
			Utils.log("Warning, could not destroy " + runThreadGroup.getName(), e);
		}
	}

	public void start() {
		if (disabled) {
			Utils.log("Not starting robot: " + robotPeer.getName() + " due to JVM bug.");
			return;
		}

		try {
			runThread = new Thread(runThreadGroup, robotPeer, robotPeer.getName());
			runThread.setDaemon(true);
			runThread.setPriority(Thread.NORM_PRIORITY);
			runThread.start();
		} catch (Exception e) {
			Utils.log("Exception starting thread: " + e);
		}
	}

	public void waitForStop() {
		if (runThread == null) {
			return;
		}
		runThread.interrupt();
		for (int j = 0; j < 20 && runThreadGroup.activeCount() > 0; j++) {
			if (j == 10) {
				Utils.log("Waiting for robot: " + robotPeer.getName() + " to stop");
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		if (runThread.isAlive()) {
			Utils.log(robotPeer.getName() + " is not stopping.  Forcing a stop.");
			forceStop();
		}
		if (runThreadGroup.activeCount() > 0) {
			if (!System.getProperty("NOSECURITY", "false").equals("true")) {
				Utils.log("Robot " + robotPeer.getName() + " has threads still running.  Forcing a stop.");
				forceStop();
			} else {
				Utils.log(
						"Robot " + robotPeer.getName()
						+ " has threads still running.  Not stopping them because security is off.");
			}
		}
	}

	/**
	 * Gets the runThread.
	 * 
	 * @return Returns a Thread
	 */
	public Thread getRunThread() {
		return runThread;
	}

	/**
	 * Gets the disabled.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getDisabled() {
		return disabled;
	}

	/**
	 * Sets the disabled.
	 * 
	 * @param disabled The disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Gets the cpuTime.
	 * 
	 * @return Returns a double
	 */
	public long getCpuTime() {
		return cpuTime;
	}

	/**
	 * Sets the cpuTime.
	 * 
	 * @param cpuTime The cpuTime to set
	 */
	public void addCpuTime(long cpuTime) {
		this.cpuTime += cpuTime;
	}

	/**
	 * Sets the cpuTime.
	 * 
	 * @param cpuTime The cpuTime to set
	 */
	public void resetCpuTime() {
		this.cpuTime = 0;
	}
}
