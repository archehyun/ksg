package com.ksg.commands.xls;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.commands.IFCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.workbench.adv.comp.ADVTableNotMatchException;
import com.ksg.workbench.adv.xls.XLSManagerImpl;

public class ImportXLSFileNewCommand implements IFCommand {

	protected Logger logger = LogManager.getLogger(this.getClass());
	private Vector tableInfoList;
	private Vector sheetList;
	private XLSManagerImpl xlsmanager = XLSManagerImpl.getInstance();
	private KSGModelManager 		manager = KSGModelManager.getInstance();
	
	public ImportXLSFileNewCommand(Vector tableInfoList, Vector sheetList) {
		this.tableInfoList=tableInfoList;
		this.sheetList=sheetList;

	}

	public int execute() {
		try {
			xlsmanager.readFile(tableInfoList,this.sheetList);

			manager.memento =xlsmanager.createXLSTableInfoMemento();

			manager.memento.setPageList(tableInfoList);
			String data = xlsmanager.getData();

//			manager.ADVStringData= xlsmanager.getXLSData();
			manager.setXLSTableInfoList(xlsmanager.getXLSTableInfoList());
			manager.tableCount = xlsmanager.getSearchedTableCount();
			logger.debug("tableCount:"+manager.tableCount);
			return RESULT_SUCCESS;
		} catch (ADVTableNotMatchException e) {
			manager.vesselCount =0;
//			manager.ADVStringData=null;
			manager.tableCount=0;
			JOptionPane.showMessageDialog(null, "에러 : "+e.getMessage());
			return RESULT_FAILE;


		}catch(Exception ee)
		{
			JOptionPane.showMessageDialog(null, "예상치 못한 에러가 발행 했습니다. "+ee.getMessage());
			ee.printStackTrace();
			return RESULT_FAILE;
		}
	}

}
