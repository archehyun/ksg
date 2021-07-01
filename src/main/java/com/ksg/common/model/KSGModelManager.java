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
package com.ksg.common.model;

import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.ksg.adv.view.dialog.ProcessDialog;
import com.ksg.adv.view.xls.XLSTableInfoMemento;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.KSGHashMap;
@SuppressWarnings("unchecked")
public class KSGModelManager 
{
	
	public static boolean isProcess=true;
	public static final int ADV_ROW_H = 40;
	protected Logger logger = Logger.getLogger(getClass());
	public Vector<KSGObserver> observers;
	public JFrame frame;
	
	public String data;
	public int selectTableIndex;
	public int vesselCount;
	public String selectedDate;
	public String selectedCompany;
	public int tableCount;
	public List<ShippersTable> searchedData;
	public String fileLoction="./data";
	public String orderBy;
	public String selectedTable_id;
	public Vector<KSGHashMap> ksgHashMapList;
	public int selectedPage;
	public String INCODE_KEY;
	private Vector tempXLSTableInfoList;
	private Vector XLSTableInfoList;
	public String scheduleCreateMessage;
	public String[] colums1 ={"페이지","인덱스","입력일자","테이블 ID","항구 수","선박 수","선사명","제목","구분",
			"TS 항구","InPorts","InToPorts","OutPorts","OutToPorts","Agent"};
	private KSGModelManager()
	{
		observers = new Vector<KSGObserver>();
	}
	public Font defaultFont = new Font("돋음",0,12);
	public ADVData selectedADVData;
	
	public ShippersTable selectedTable;
	public String selectedInput="File";
	public XLSTableInfoMemento memento;
	public boolean isWorkMoniter=false;
	public String workProcessText="";
	public boolean seperatedVesselvoy=false;
	public String version;
	public ProcessDialog processBar;
	private static KSGModelManager manager;
	public String menu_command="";
	
	public static KSGModelManager getInstance()
	{
		if(manager==null)
			manager = new KSGModelManager();

		return manager;
	}

	public void addObservers(KSGObserver o)
	{
		if(((Component)o).getName()!=null&&o!=null)
		{
			observers.add(o);
		}else
		{
			System.exit(1);
		}
	}
	public void removeObserver(KSGObserver o)
	{
		System.out.println(observers.remove(o));
	}
	public void removeComp(String type)
	{
		Vector<KSGObserver> temp = new Vector<KSGObserver>();
		for(KSGObserver table:observers)
		{
			String tableName=((Component)table).getName();
			if(tableName.equals(type))
			{
				temp.add(table);
				
			}
		}
		
		for(KSGObserver table:temp)
		{
			String tableName=((Component)table).getName();
			if(tableName.equals(type))
			{
				observers.remove(table);
				
			}
		}
		temp.removeAllElements();
		temp = null;
		
	}
	public void execute()
	{
		notifyComp();
	}
	public void execute(String name)
	{
		notifyComp(name);
	}

	
	private void notifyComp(String string) 
	{
		try{
		for(KSGObserver table:observers)
		{
			String tableName=((Component)table).getName();
			if(tableName.equals(string))
			{
				table.update(this);
			}
		}
		}catch(Exception e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
	private void notifyComp() 
	{
		for(KSGObserver table:observers)
		{
			table.update(this);
		}

	}

	public Vector getTempXLSTableInfoList() {
		
		return tempXLSTableInfoList;
	}
	
	public Vector getXLSTableInfoList() {
		
		return XLSTableInfoList;
	}

	public void setXLSTableInfoList(Vector tableInfoList) {
		XLSTableInfoList = tableInfoList;
		if(XLSTableInfoList==null)
			return;	
		
		tempXLSTableInfoList = new Vector();
		for(int i=0;i<XLSTableInfoList.size();i++)
		{
			tempXLSTableInfoList.add(XLSTableInfoList.get(i));
		}
	}
	public void initTempXLSTableInfoList()
	{
		tempXLSTableInfoList = new Vector();
		
	}



}
