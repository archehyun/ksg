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
package com.ksg.adv.logic.poi;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IF_XLSManager 
{
	/**
	 * @param xlsFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String[][] getXLSData(String xlsFile) throws FileNotFoundException, IOException;
	/**
	 * @param index
	 * @return
	 */
	public String getData(int index);
	/**
	 * @param index
	 * @return
	 */
	public String[][] getDataArray(int index);
	
	public void readData(String xls) throws IOException;
	
	//public String checkVersion(String xlsFile);
	
	public boolean checkVersion2003(String string);

}
