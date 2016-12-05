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

import javax.swing.table.DefaultTableModel;

/**
 * @author archehyun
 *
 */
public class KSGTableModel extends DefaultTableModel{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean isEdit=false;
	public KSGTableModel()
	{
		super();
	}
	
	public KSGTableModel(boolean isEdit)
	{
		super();
		this.isEdit=isEdit;
	}
	
	
	public KSGTableModel(String[] columNames, int rowCount) {
		super(columNames,rowCount);
	}
	public boolean isCellEditable(int row, int column) {
		return isEdit;
	}
}
