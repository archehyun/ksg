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
package com.ksg.quark;

import java.util.Iterator;
import java.util.Vector;

public class XTGRow extends XTGDoc
{
	Vector cells = new Vector();
	String prefix;
	int index;
	public XTGRow(int index) {
		logger.debug("XTGRow  생성:"+index);
		this.index=index;
	}
	public void addXTGCell(XTGCell cell)
	{
		cells.add(cell);	
	}

	public void createXTGCell(String[] cells) {
		if(cells==null)
			return;
		if(cells.length==0)
			return;
		logger.debug(index+"번 row,"+cells.length+" cell생성");
		for(int i=0;i<cells.length;i++)
		{
			XTGCell cell = new XTGCell(cells[i]);
			this.addXTGCell(cell);
		}
	}
	@Override
	public String getPreFix() {
		// TODO Auto-generated method stub
		return prefix;
	}
	@Override
	public String parseDoc() {

		String message = new String();
		Iterator iter = cells.iterator();
		message=this.getPreFix()+"\t";
		while(iter.hasNext())
		{
			XTGCell cell=(XTGCell) iter.next();
			message+=cell.parseDoc();
			if(iter.hasNext())
				message+="\t";
		}
		return message;
	}
	@Override
	public void setADVData(String data) {
		// TODO Auto-generated method stub

	}
	public void setADVData(String[] data) {
		if(data.length>cells.size())
		{
			logger.debug("row size가 다릅니다.\n"+"data:"+data.length+",cells:"+cells.size());
			//TODO ROW 사이즈가 다를때 룰 작성
			return;
		}
		logger.debug("새로운 row data 적용:"+data.length);
		
		for(int i=0;i<data.length;i++)
		{
			XTGCell cell = (XTGCell) cells.get(i);
			cell.setData(data[i]);
		}
	}
	@Override
	public void setPreFix(String preFix) {
		prefix=preFix;

	}
	@Override
	public void setQuarkData(String data) {
		// TODO Auto-generated method stub

	}

}
