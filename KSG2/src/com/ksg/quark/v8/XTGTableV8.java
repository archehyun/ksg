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
package com.ksg.quark.v8;

import java.util.Iterator;

import com.ksg.quark.XTGRow;
import com.ksg.quark.quarkfactory.XTGTable;

public class XTGTableV8 extends XTGTable {

	

	public XTGTableV8(String info,int pageIndex) {
		super(info,pageIndex);

	}

	@Override
	public String makeXTG() {
		String message = new String();
		if(pageIndex==0)
			message=info;
		Iterator iter =xtgRow.iterator();
		while(iter.hasNext())
		{
			XTGRow row = (XTGRow) iter.next();
			message+=row.parseDoc();
			if(iter.hasNext())
				message+="\n";
		}
		return message;
	}

}
