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
package com.ksg.print.logic.quark.v8;

import com.ksg.print.logic.quark.quarkfactory.XTGCell;
import com.ksg.print.logic.quark.quarkfactory.XTGFactory;
import com.ksg.print.logic.quark.quarkfactory.XTGPage;
import com.ksg.print.logic.quark.quarkfactory.XTGRow;
import com.ksg.print.logic.quark.quarkfactory.XTGTable;

public class XTGFactoryV8 extends XTGFactory{

	@Override
	public XTGCell createCell(String datas) {
		
		String data=datas.trim();
		String prefix = null;
		String endfix = null;
		if(data.startsWith("<"))
		{
			int ind=data.indexOf(">");
			
			prefix=data.substring(0,ind+1);
			data=data.substring(ind+1);
		}
		
		if(data.endsWith(">"))
		{
			int ind=data.lastIndexOf("<");
			endfix=data.substring(ind,data.length());
			data=data.substring(0,ind);
		}
		return new XTGCellV8(data, prefix, endfix);
	}

	@Override
	public XTGPage createPage() {
		// TODO Auto-generated method stub
		return new XTGPageV8();
	}

	@Override
	public XTGTable createTable(String info,int pageIndex) {
		// TODO Auto-generated method stub
		return new XTGTableV8(info,pageIndex);
	}

	@Override
	public XTGRow createRow(String data) {
		// TODO Auto-generated method stub
		return null;
	}

	
}

