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
package com.ksg.print.logic.quark.v1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;

@SuppressWarnings("unchecked")
public class XTGPage extends XTGDoc {

	private String company;
	HashMap<String, XTGTable> map;

	Vector keyList =new Vector();
	public XTGPage() {
		map = new HashMap<String, XTGTable>();
	}
	public String getPreFix() {
		return null;
	}

	public String getVersion() {
		return null;
	}

	public String parseDoc() 
	{
		String message = new String();
		Iterator iterator = keyList.iterator();
		while(iterator.hasNext())
		{
			Object key = iterator.next();
			XTGTable doc = (XTGTable)map.get(key);
			if(doc==null)
			{
				System.out.println(key);
				return null;
			}
			message+=doc.parseDoc();
			if(iterator.hasNext())
				message+="\n";
		}
	
		return message+"<\\c>";
	}

	public void setPreFix(String preFix) {

	}

	public void setADVData(String data) {

	}

	public void setVersion(String version) {

	}

	public void addTable(XTGTable table ) {
		logger.debug("추가:"+table.getTable_ID());
		table.setPaser(parser);
		this.map.put(table.getTable_ID(), table);
		keyList.add(table.getTable_ID());
		
	}
	public void setQuarkData(String data) {
		
	}
	public XTGTable getTable(String i) 
	{		
		return map.get(i);
	}
	public void createTables(List tableLi) {
		logger.debug(company+" 테이블 : "+tableLi.size()+"개 생성 시작");
		for(int i=0;i<tableLi.size();i++)
		{
			ShippersTable ta = (ShippersTable) tableLi.get(i);
			XTGTable table = new XTGTable(ta.getTable_id());
			table.setPaser(new XTGParserImpl());
			table.setQuarkData(ta.getQuark_format());
			table.setPageIndex(i);
			this.addTable(table);
		}
		logger.debug(company+" 테이블 프레임 생성 완료");
	}
	public void setNewADVData(List<ADVData> advLi) 
	{
		logger.debug("새로운 page 데이터 적용"+advLi.size()+"개");
		for(int i=0;i<advLi.size();i++)
		{
			String datas=advLi.get(i).getData();
			logger.debug("data:\n"+datas);
			XTGTable table=this.getTable(advLi.get(i).getTable_id());
			if(table==null)
			{
				System.out.println(advLi.get(i).getTable_id()+"가 없습니다.");
				return;
			}
			
			table.setNewADVData(datas);
		}
		
	}
	public void setCompany(String company) {
		this.company = company;
	}

}
