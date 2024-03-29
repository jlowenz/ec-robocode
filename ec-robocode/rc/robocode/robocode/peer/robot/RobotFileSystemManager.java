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
 *     - Ported to Java 5.0
 *******************************************************************************/
package robocode.peer.robot;


import java.io.*;
import java.util.Vector;

import robocode.peer.RobotPeer;
import robocode.RobocodeFileOutputStream;


/**
 * @author Mathew A. Nelson (original)
 */
public class RobotFileSystemManager {
	RobotPeer robotPeer;
	public long quotaUsed = 0;
	public boolean quotaMessagePrinted = false;
	public Vector<RobocodeFileOutputStream> streams = new Vector<RobocodeFileOutputStream>(); 
	long maxQuota = 0;

	/**
	 * RobotFileSystemHandler constructor comment.
	 */
	public RobotFileSystemManager(RobotPeer robotPeer, long maxQuota) {
		this.robotPeer = robotPeer;
		this.maxQuota = maxQuota;
	}

	public void addStream(RobocodeFileOutputStream s) throws IOException {
		if (s == null) {
			throw new SecurityException("You may not add a null stream.");
		}
		if (!streams.contains(s)) {
			if (streams.size() < 5) {
				streams.add(s);
			} else {
				throw new IOException(
						"You may only have 5 streams open at a time.\n Make sure you call close() on your streams when you are finished with them.");
			}
		}
	}

	public synchronized void adjustQuota(long len) throws IOException {
		quotaUsed += len;
	}

	public void checkQuota() throws IOException {
		checkQuota(0);
	}

	public void checkQuota(long numBytes) throws IOException {
		if (numBytes < 0) {
			throw new IndexOutOfBoundsException("checkQuota on negative numBytes!");
		}
		if (quotaUsed + numBytes <= maxQuota) {
			adjustQuota(numBytes);
			return;
		}
		if (!quotaMessagePrinted) {
			robotPeer.out.println("SYSTEM: You have reached your filesystem quota of: " + maxQuota + " bytes.");
			quotaMessagePrinted = true;
		}
		throw new IOException("You have reached your filesystem quota of: " + maxQuota + " bytes.");
	}

	public long getMaxQuota() {
		return maxQuota;
	}

	public long getQuotaUsed() {
		return quotaUsed;
	}

	public File getReadableDirectory() {
		if (robotPeer.getRobotClassManager().getRobotClassLoader().getRootPackageDirectory() == null) {
			return null;
		}
		try {
			return new File(robotPeer.getRobotClassManager().getRobotClassLoader().getRootPackageDirectory()).getCanonicalFile();
		} catch (java.io.IOException e) {
			return null;
		}
	}

	public File getWritableDirectory() {
		if (robotPeer.getRobotClassManager().getRobotClassLoader().getClassDirectory() == null) {
			return null;
		}
		try {
			File dir = new File(robotPeer.getRobotClassManager().getRobotClassLoader().getClassDirectory(), robotPeer.getRobotClassManager().getClassNameManager().getShortClassName() + ".data").getCanonicalFile();

			return dir;
		} catch (java.io.IOException e) {
			return null;
		}
	}

	public void initializeQuota() {
		File dataDirectory = getWritableDirectory();

		if (dataDirectory == null) {
			quotaUsed = maxQuota;
			return;
		}
		if (!dataDirectory.exists()) {
			this.quotaUsed = 0;
			return;
		}
		quotaMessagePrinted = false;
		File[] dataFiles = dataDirectory.listFiles();

		quotaUsed = 0;
		for (int i = 0; i < dataFiles.length; i++) {
			quotaUsed += dataFiles[i].length();
		}
	}

	public boolean isReadable(String fileName) {
		File allowedDirectory = getReadableDirectory();

		if (allowedDirectory == null) {
			return false;
		}

		File attemptedFile = null;

		try {
			attemptedFile = new File(fileName).getCanonicalFile();
		} catch (java.io.IOException e) {
			return false;
		}

		if (attemptedFile.getParent().indexOf(allowedDirectory.toString()) == 0) {
			String fs = attemptedFile.toString();
			int dataIndex = fs.indexOf(".data", allowedDirectory.toString().length());

			if (dataIndex >= 0) {
				if (isWritable(fileName) || attemptedFile.equals(getWritableDirectory())) {
					return true;
				} else {
					throw new java.security.AccessControlException(
							"Preventing " + Thread.currentThread().getName() + " from access to: " + fileName
							+ ": You may not read another robot's data directory.");
				}
			}
			return true;
		}
		
		return false;
	}

	public boolean isWritable(String fileName) {
		File allowedDirectory = getWritableDirectory();

		if (allowedDirectory == null) {
			return false;
		}

		File attemptedFile = null;

		try {
			attemptedFile = new File(fileName).getCanonicalFile();
		} catch (java.io.IOException e) {
			return false;
		}

		if (attemptedFile.getParentFile().equals(allowedDirectory)) {
			return true;
		}
	
		return false;
	}

	public void removeStream(RobocodeFileOutputStream s) {
		if (s == null) {
			throw new SecurityException("You may not remove a null stream.");
		}
		if (streams.contains(s)) {
			streams.remove(s);
		}
	}
}
