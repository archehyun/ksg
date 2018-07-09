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
package com.ksg.adv.logic.model;

import com.ksg.common.view.comp.FileInfo;

public class SheetInfo extends FileInfo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String sheetName;
	public String toString()
	{
		return sheetName;
	}

}
