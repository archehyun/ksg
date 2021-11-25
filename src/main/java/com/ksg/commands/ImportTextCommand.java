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
package com.ksg.commands;

import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ADVData;
import com.ksg.service.TableService;
import com.ksg.service.TableServiceImpl;

public class ImportTextCommand implements KSGCommand{
	String d;
	private boolean emptyCheck;
	String isUnderPort="";
	boolean hasVoy=true;
	public String bothKeyWord[];
	private KSGPropertis propertis = KSGPropertis.getIntance();
	public String vesselKeyWord[];
	protected Logger 		logger = Logger.getLogger(this.getClass());
	private Boolean isDoubleKey;
	private String upDown;
	private TableService tableService;
	
	public ImportTextCommand(String string) {
		d = string;
		tableService = new TableServiceImpl();
		propertis.reLoad();
		this.emptyCheck=Boolean.parseBoolean((String)propertis.getValues("emptyCheck"));
		String xlskey=(String) propertis.getValues("xlskey.vessel");
		String bothKey = (String) propertis.getValues("xlskey.both");
		isUnderPort = (String)propertis.getValues(KSGPropertis.PROPERTIES_UNDERPORT).toString();
		hasVoy = Boolean.parseBoolean(propertis.getValues(KSGPropertis.PROPERTIES_VOY).toString());
		StringTokenizer st = new StringTokenizer(xlskey,",");
		vesselKeyWord = new String[st.countTokens()];
		for(int i=0;i<vesselKeyWord.length;i++)
		{
			vesselKeyWord[i]=st.nextToken();
		}
		StringTokenizer st2 = new StringTokenizer(bothKey,",");
		bothKeyWord = new String[st2.countTokens()];
		for(int i=0;i<bothKeyWord.length;i++)
		{
			bothKeyWord[i]=st2.nextToken();
		}
		String doubleKey=(String) propertis.getValues(KSGPropertis.PROPERTIES_DOUBLEKEY);
		StringTokenizer st3 = new StringTokenizer(doubleKey,"|");
		isDoubleKey = Boolean.parseBoolean(st3.nextToken());
		upDown = st3.nextToken();
		
	}

	public int execute() {
		logger.debug("start extrct text data:\n"+d);
		
		StringTokenizer st1 = new StringTokenizer(d,"\n");
		String data[][] = new String[st1.countTokens()][];
		for(int i=0;st1.hasMoreTokens();i++)
		{
			String sub = st1.nextToken();
			StringTokenizer st2 = new StringTokenizer(sub," ");
			data[i]=new String[st2.countTokens()];
			for(int j=0;st2.hasMoreTokens();j++)
			{
				String sub1=st2.nextToken();
				data[i][j]=sub1;
			}
		}
		
		int count=0;
		boolean vesselflag=false;
		for(int i=0;i<data.length;i++)
		{
			
			// 키워드(Vessel )이 발견 되는지 확인 함
			for(int z=0;z<vesselKeyWord.length;z++)
			{
				if(data[i][0].trim().equals(vesselKeyWord[z]))
					count++;
					vesselflag=true;
			}
		}
		String d = new String();
		for(int i=0;i<data.length;i++)
		{
			for(int j=0;j<data[i].length;j++)
			{
				d+=data[i][j]+ADVData.COL_DIVIDER;
			}
			if(data.length>i)
				d+=ADVData.ROW_DIVIDER;
		}
		JOptionPane.showMessageDialog(null, "table:"+count+"\n"+d);
		KSGModelManager.getInstance().tableCount=count;
		Vector advDataList = new Vector();

		advDataList.add(d);
//		KSGModelManager.getInstance().ADVStringData=advDataList;
		return RESULT_SUCCESS;
	}

}
