/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.print.logic.quark;

import java.io.FileWriter;
import java.io.IOException;


public class XTGFileManager {
	public void createXTGFile(String source) throws IOException
	{
		char buffer[] = new char[source.length()];
		source.getChars(0,source.length(),buffer,0);
		FileWriter f0 = new FileWriter("file1.txt");
		for(int i=0;i<buffer.length;i++)
		{
			f0.write(buffer[i]);
		}
		f0.close();
	}

}
