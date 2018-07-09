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
package com.ksg.schedule.logic;

import com.ksg.domain.ShippersTable;

public class PortIndexNotMatchException extends ArrayIndexOutOfBoundsException {
	
	public PortIndexNotMatchException() {
		super();
	}
	public ShippersTable table;
	public PortIndexNotMatchException(ShippersTable table) {
		super();
		this.table= table;
	}

}
