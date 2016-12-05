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
package com.ksg.view.comp;

import javax.swing.JTable;

import org.apache.log4j.Logger;

import com.ksg.model.KSGObserver;

public abstract class KSGTable extends JTable implements KSGObserver {

	public static final int TABLE_TYPE_ADV=0;
	public static final int TABLE_TYPE_ERROR=1;
	public static final int ADV_TYPE = 0;
	public static final int TABLE_TYPE = 1;
	protected int tableindex;
	protected Logger 			logger = Logger.getLogger(getClass());
	public void setTableIndex(int index) {
		tableindex = index;
		
	}
	public int getTableIndex() {
		// TODO Auto-generated method stub
		return tableindex;
	}
	

}
