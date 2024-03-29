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
package robocode.util;


import java.awt.*;
import javax.swing.text.*;


/**
 * @author Mathew A. Nelson (original)
 */
@SuppressWarnings("serial")
public class LimitedDocument extends PlainDocument {
	int maxRows = Integer.MAX_VALUE;
	int maxCols = Integer.MAX_VALUE;

	public LimitedDocument() {
		super();
	}

	public LimitedDocument(int maxRows, int maxCols) {
		super();
		this.maxRows = maxRows;
		this.maxCols = maxCols;
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		Element rootElement = getDefaultRootElement();
		int i = str.indexOf("\n");
		int newlines = 0;

		while (i < str.length() && i >= 0) {
			newlines++;
			i = str.indexOf("\n", i + 1);
		}
	
		int currentLines = rootElement.getElementCount();

		if (newlines > 0 && currentLines + newlines > maxRows) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}

		int lineIndex = rootElement.getElementIndex(offs);
		boolean done = false;

		int carry = rootElement.getElement(lineIndex).getEndOffset() - offs - 1;
		int lineStart = 0;

		while (!done) {
			int lineEnd = str.indexOf("\n", lineStart);

			if (lineEnd == -1 || lineEnd == str.length()) {
				if (lineStart == 0) {
					carry = 0;
					lineEnd = str.length();
				} else {
					lineEnd = str.length() + carry;
				}
				done = true;
			}
			int lineLen = lineEnd - lineStart;
			// Increment for last line...
			int currentLen;

			if (lineStart > 0 && done == false) {
				currentLen = 0;
			} else {
				if (lineStart > 0 && done == true) {
					lineIndex++;
				}
				Element currentLine = rootElement.getElement(lineIndex);

				if (currentLine != null) {
					currentLen = currentLine.getEndOffset() - currentLine.getStartOffset();
				} else {
					currentLen = 1;
				}
				if (lineStart == 0) {
					currentLen -= carry;
				}
			}
			if (lineLen + currentLen > maxCols + 1) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			
			lineStart = lineEnd + 1;
		}

		super.insertString(offs, str, a);
	}
}
