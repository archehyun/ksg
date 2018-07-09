package com.ksg.commands;

import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;

import com.ksg.adv.logic.model.SheetInfo;
import com.ksg.adv.view.comp.XLSManagerImpl;
import com.ksg.common.view.comp.FileInfo;

public class SearchSheetNameCommand implements KSGCommand {
	DefaultListModel filemodel;
	FileInfo fileInfo;
	public List<SheetInfo> sheetNameList;
	XLSManagerImpl xlsmanager= XLSManagerImpl.getInstance();
	public SearchSheetNameCommand(DefaultListModel filemodel) {
		this.filemodel=filemodel;
	}

	public SearchSheetNameCommand(FileInfo fileInfo) {
		this.fileInfo=fileInfo;
	}

	public int execute() {
		sheetNameList = new LinkedList<SheetInfo>();
		
		if(filemodel!=null)
		{
			for(int i=0;i<filemodel.size();i++)
			{
				FileInfo filename=(FileInfo) filemodel.get(i);
				List li=xlsmanager.getSheetNameList(filename.filePath);
				if(li!=null)
				{
					for(int j=0;j<li.size();j++)
					{
						SheetInfo info = new SheetInfo();
						info.file=filename.file;
						info.filePath=filename.filePath;
						info.sheetName =(String) li.get(j); 
						sheetNameList.add(info);
					}
				}						
			}
	
		}else
		{
			List li=xlsmanager.getSheetNameList(fileInfo.filePath);
			if(li!=null)
			{
				for(int j=0;j<li.size();j++)
				{
					SheetInfo info = new SheetInfo();
					info.file=fileInfo.file;
					info.filePath=fileInfo.filePath;
					info.sheetName =(String) li.get(j); 
					sheetNameList.add(info);
				}
			}			
		}
		return 0;	
	}
	

}
