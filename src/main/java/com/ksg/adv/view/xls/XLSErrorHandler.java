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
package com.ksg.adv.view.xls;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ksg.adv.service.ADVServiceImpl;
import com.ksg.domain.KSGError;
import com.ksg.service.ADVService;

public class XLSErrorHandler 
{
	private ADVService service;
	private Vector errorList;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	public XLSErrorHandler() {
		service		 	= new ADVServiceImpl();
		errorList = new Vector();
	} 

	public void createErrorList(Vector datas) {

		logger.debug("에러 정보를 처리합니다.");		
		if( datas==null||datas.size()==0)
		{
			logger.error("테이블 정보가 존재하지 않습니다.");
			return;
		}

		for(int i=0;i<datas.size();i++)
		{
			String adv[][]=service.createADVTableModel((String)datas.get(i));
			
			for(int z=1;z<adv.length;z++)
			{
				for(int c=0;c<adv[z].length;c++)
				{
					if(adv[z][c]=="")
					{
						KSGError error = new KSGError();
						error.setErrorPortCode(adv[z][c]);
						error.setErrorCode(KSGError.ERRORCODE_EMPTY);
						error.setTableIndex(i);
						errorList.add(error);
					}
					StringTokenizer to = new StringTokenizer(adv[z][c],"/");

					if(!adv[z][c].equals("-"))
					{
						if(to.countTokens()!=2&&c>1)
						{
							KSGError error = new KSGError();
							error.setErrorPortCode(adv[z][c]);
							error.setErrorCode(KSGError.ERRORCODE_DATE);
							error.setTableIndex(i);
							errorList.add(error);
						}
					}
				}
			}
		}
		for(int i=0;i<errorList.size();i++)
		{
			KSGError ero = (KSGError) errorList.get(i);
			System.out.println("errorCode:"+ero.getErrorCode()+","+ero.getErrorPort());
		}
		logger.debug("에러 정보 처리 종료");



	}

	public Vector getErrorList() {

		return errorList;
	}

}
