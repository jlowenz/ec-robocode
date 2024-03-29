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
 *******************************************************************************/
package robocode.io;


import java.io.IOException;
import java.io.InputStream;


/**
 * @author Mathew A. Nelson (original)
 */
public class BufferedPipedInputStream extends InputStream {

	private BufferedPipedOutputStream out;
	
	protected BufferedPipedInputStream(BufferedPipedOutputStream out) {
		this.out = out;
	}
	
	public int read() throws IOException {
		return out.read();
	}

	public int read(byte b[], int off, int len) throws IOException {
		return out.read(b, off, len);
	}
	
	public int available() {
		return out.available();
	}
}
