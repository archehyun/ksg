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
package com.ksg.commands.xls;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.ksg.adv.view.comp.ADVTableNotMatchException;
import com.ksg.adv.view.comp.XLSManagerImpl;
import com.ksg.adv.view.comp.XLSTableInfoMemento;
import com.ksg.commands.KSGCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ShippersTable;

public class ImportXLSFileCommand implements KSGCommand {

	protected Logger 		logger = Logger.getLogger(this.getClass());
	private String selectXLSFilePath;
	private String company;
	private XLSManagerImpl xlsmanager = XLSManagerImpl.getInstance();
	private KSGModelManager 		manager = KSGModelManager.getInstance();
	ShippersTable table;
	String sheetName;
	private Vector sheetNameList;
	private Vector tableInfoList;
	private XLSTableInfoMemento memento;
	String searchType;
	String selectedInput;
	private int keyType=0;
	public static final int VESSEL=0;
	public static final int VOYAGE=1;
	
	
	private ImportXLSFileCommand(String path, String company, int page)
	{
		this.selectXLSFilePath = path;
		this.table =new ShippersTable();
		table.setCompany_abbr(company);
		table.setPage(page);
		
		
	}

	public ImportXLSFileCommand(Object sheetName, String path, String company, int page) {
		
		this(path, company, page);
		
		this.sheetName =(String) sheetName;

	}
	public ImportXLSFileCommand(Vector sheetNameList, String path, String company, int page) {
		
		this(path, company, page);
		
		this.sheetNameList= sheetNameList;
	}
	public ImportXLSFileCommand(Vector tableInfoList,Vector sheetNameList, String path, String company, int page) {
		
		this(path,company, page);

		
		this.sheetNameList= sheetNameList;
		
		this.tableInfoList= tableInfoList;
	}
	public ImportXLSFileCommand(Object sheetName, String path, String company, int page,int other) {
		this(sheetName,path,company,page);
		xlsmanager.other=other;
	}

	public ImportXLSFileCommand(Vector<ShippersTable> tableInfoList,
			Vector sheetNameList, String selectXLSFilePath, String company,
			int page, String searchType, String selectedInput) {
		
		
		this(sheetNameList,selectXLSFilePath, company, page);
		
		this.tableInfoList= tableInfoList;
		this.searchType=searchType;
		this.selectedInput=selectedInput;
	}
	public ImportXLSFileCommand(Vector<ShippersTable> tableInfoList2,
			Vector sheetList, String selectXLSFilePath2, String company2,
			int parseInt, String searchType2, String selectedInput2, int keyType) {
		this(tableInfoList2,sheetList,selectXLSFilePath2,company2,parseInt,searchType2,selectedInput2);
		this.keyType= keyType;

	}
	public int execute() {
		
		try {
			logger.info("start");

			if(sheetName==null)
			{
				xlsmanager.readFile(tableInfoList,this.sheetNameList,selectXLSFilePath,table, keyType);
			}else
			{
				xlsmanager.readFile(this.sheetName,selectXLSFilePath,table);
			}

			manager.memento =xlsmanager.createXLSTableInfoMemento();
			
			manager.memento.setSearchType(this.searchType);
			
			manager.memento.setSelectedInput(this.selectedInput);
			
			manager.memento.setPageList(tableInfoList);
			
			manager.setXLSTableInfoList(xlsmanager.getXLSTableInfoList());
			
			manager.tableCount = xlsmanager.getSearchedTableCount();
			
			logger.info("tableCount:"+manager.tableCount);

			return RESULT_SUCCESS;
		} catch (ADVTableNotMatchException e) {
			manager.vesselCount =0;
			manager.tableCount=0;
			JOptionPane.showMessageDialog(null, "에러 : "+e.getMessage());
			e.printStackTrace();
			return RESULT_FAILE;

		}catch(Exception ee)
		{
			JOptionPane.showMessageDialog(null, "예상치 못한 에러가 발행 했습니다. "+ee.getMessage());
			ee.printStackTrace();
			return RESULT_FAILE;
		}


	}

}
