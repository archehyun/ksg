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
package com.ksg.quark.logic.quarkfactory;

import java.util.Vector;

public abstract class XTGPage {
	protected Vector tables = new Vector();
	public abstract String makeXTG();
	public void addItem(XTGItem item)
	{
		tables.add(item);
	}

}
