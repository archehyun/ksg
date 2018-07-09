package com.ksg.commands;

import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.ksg.adv.logic.model.SheetInfo;
import com.ksg.adv.view.comp.XLSManagerImpl;
import com.ksg.adv.view.comp.XLSStringUtil;
import com.ksg.common.dao.DAOManager;
import com.ksg.dao.impl.BaseService;

public class ViewXLSFileCommand implements KSGCommand {
	SheetInfo info;
	XLSManagerImpl managerImpl;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	JTable table;
	private String[] vesselKeyWord;
	private String[] bothKeyWord;
	private List vesselKeyList;
	private List voyageKeyList;
	private List bothKeyList;
	protected BaseService _baseService;
	DAOManager manager = DAOManager.getInstance();
	XLSStringUtil xlsUitl;
	public ViewXLSFileCommand(SheetInfo info) {
		this.info=info;
		xlsUitl = new XLSStringUtil();
		managerImpl = XLSManagerImpl.getInstance();
		logger.debug("view xls file start");
	}
	public ViewXLSFileCommand(SheetInfo info2, JTable table) {
		this(info2);
		this.table=table;
		try {
			_baseService=manager.createBaseService();
			vesselKeyList = _baseService.getKeywordList("VESSEL");
			voyageKeyList = _baseService.getKeywordList("VOYAGE");
			bothKeyList = _baseService.getKeywordList("BOTH");
			vesselKeyWord = new String[vesselKeyList.size()];
			bothKeyWord = new String[bothKeyList.size()];

			for(int i=0;i<vesselKeyList.size();i++)
			{
				vesselKeyWord[i]= vesselKeyList.get(i).toString();
			}
			for(int i=0;i<bothKeyList.size();i++)
			{
				bothKeyWord[i]= bothKeyList.get(i).toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public DefaultTableModel model;
	public int execute() {
		try {
			Sheet sheet= managerImpl.getXLSSheet(info.filePath,info.sheetName);
			
			Vector rowList = new Vector();
			int colMax=0;
			for(int i=0;i<sheet.getPhysicalNumberOfRows();i++)
			{
				Row row = sheet.getRow(i);
				if(row==null)
					continue;
				Vector colList = new Vector();
				
				for(int j=0;j<row.getPhysicalNumberOfCells();j++)
				{
					colList.add(this.getCell(row.getCell(j)));
				}
				if(colMax <colList.size())
					colMax = colList.size();
				rowList.add(colList);				
			}
			
			model = new DefaultTableModel();
			
			logger.debug("row size:"+rowList.size());
			model.setColumnCount(colMax+1);
			
			for(int i=0;i<rowList.size();i++)
			{
				Vector col = (Vector) rowList.get(i);
				
				Object colList[] =new Object[col.size()+1];
				colList[0]=i+1;
				for(int j=1;j<col.size()+1;j++)
				{	
					colList[j]=col.get(j-1);
				}
				model.addRow(colList);
			}
			TableColumnModel colmodel =table.getColumnModel();
			
			
			
			
			for(int i=0;i<colmodel.getColumnCount();i++)
			{

				TableColumn namecol = colmodel.getColumn(i);
				int w = sheet.getColumnWidth(i);

				DefaultTableCellRenderer renderer = new VesselRenderer();
				namecol.setPreferredWidth(w);
				
				namecol.setCellRenderer(renderer);
			}
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	private Object getCell(org.apache.poi.ss.usermodel.Cell cell) {
		return xlsUitl .getStringData((HSSFCell)cell, true);
		
	}
	class VesselRenderer extends DefaultTableCellRenderer
	{
		public void setValue(Object value)
		{
			
			if(value==null)
				return;
			setText(value.toString());
			
			for(int z=0;z<vesselKeyWord.length;z++)
			{
				if(value.toString().trim().toLowerCase().equals(vesselKeyWord[z].trim().toLowerCase()))
				{
					setForeground(Color.blue);
					setFont(new Font("aria",Font.BOLD,12));
					
					return;
				}
			}
			for(int z=0;z<bothKeyWord.length;z++)
			{
				if(value.toString().trim().equals(bothKeyWord[z].trim()))
				{
					setForeground(Color.blue);
					setFont(new Font("aria",Font.BOLD,12));
					return;
				}
			}
			setForeground(table.getForeground());
			
		}


	}
	
	

}
