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
package com.ksg.adv.logic.parser;

import java.util.List;
import java.util.Vector;

public abstract class XLSParser extends XLSReader{
	
	public abstract String getData();
	
	public abstract Vector getXLSData();

	public abstract int getSearchedTableCount();

	public abstract  void setPreData(List li);

	public abstract  Vector getXLSTableInfoList();

	
}
