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
 *     - MAX was turned into a constant called MAX_SIZE
 *     - Removed synchronization from getCount()
 *******************************************************************************/
package robocode.io;


import java.io.*;


/**
 * @author Mathew A. Nelson (original)
 */
public class TeamMessageSizer extends OutputStream {
	private final static long MAX_SIZE = 32768;

	private long count = 0;

	public synchronized void write(int b) throws IOException {
		count++;
		if (count > MAX_SIZE) {
			throw new IOException("You have exceeded " + MAX_SIZE + " bytes this turn.");
		}
	}

	public synchronized void resetCount() {
		count = 0;
	}

	public long getCount() {
		return count;
	}
}