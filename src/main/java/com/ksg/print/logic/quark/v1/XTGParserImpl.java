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

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




public class XTGParserImpl implements XTGPaser{
	private String tablePrefix;
	private String pagePrefix;
	protected Logger logger = LogManager.getLogger(this.getClass());

	public XTGParserImpl() {
		logger.debug("파서 생성");
	}

	public String[] getTables(String stringData){
		Pattern p = Pattern.compile("(<[*]p).+?(>)");
		String token[]= p.split(stringData);
		return token;
	}
	public String[] parsePageFrame(String stringData)
	{
		logger.debug("버전 정보 분리 reg:"+XTGTagParameter.pageFramedivider);
		
		if(stringData==null)
		{
			JOptionPane.showMessageDialog(null, "data is null :\n"+stringData);
			return null;
		}
		
		Pattern p = Pattern.compile(XTGTagParameter.pageFramedivider);
		String token[]= p.split(stringData);
		return token;
	}

	// 쿽 스타일 정보를 바탕으로 프래임 생성
	public String[][] getFrame(String stringData) {
		
		String pageFrame[]=this.parsePageFrame(stringData);
		if(pageFrame.length<2)
		{
			JOptionPane.showMessageDialog(null, "quark format Error:\n"+stringData);
			return null;
		}
		if(stringData==null)
		{
			JOptionPane.showMessageDialog(null, "data is null :\n"+stringData);
			return null;
		}
		String info =pageFrame[0];//
		String data =pageFrame[1];//
		this.setPagePrefix(info);
		logger.debug("테이블 구분 시작 reg:"+XTGTagParameter.tableDivider);
		
		Pattern p = Pattern.compile(XTGTagParameter.tableDivider);
		String token[]= p.split(data);
		String parseData[][] = new String[token.length-1][];

		this.tablePrefix=token[0];
		for(int i=1;i<token.length;i++)
		{
			StringTokenizer tt = new StringTokenizer(token[i],XTGTagParameter.rowDivider);
			int count=0;
			
			parseData[i-1]=new String[tt.countTokens()];	
			try{
				while(tt.hasMoreTokens())
				{
					String to =tt.nextToken();
					parseData[i-1][count]=to;
					count++;
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return parseData;
	}
	private void setPagePrefix(String info) {
		this.pagePrefix=info;

	}
	
	public String[] getRowTag(String stringData) {
		//String message = new String();

		//String patten1 ="<[*].*?>.*";
		//String patten2 ="@[$]:<.*?>";
		String patten3 ="(<[*]t).+?(>)";
		Matcher m = Pattern.compile(patten3).matcher
		(
				stringData);

		int count=0;
		int index=m.groupCount();
		Vector da =new Vector();
		while (m.find()) {

			String temp=m.group();
			da.add(temp);
			count++;
		}
		String data[]=new String[da.size()];
		for(int i=0;i<da.size();i++)
		{
			data[i]=(String) da.get(i);
		}
		return data;
	}
	
	public String getTablePrefix() {
		// TODO Auto-generated method stub
		return tablePrefix;
	}
	
	public String getPagePrefix() {
		// TODO Auto-generated method stub
		return pagePrefix;
	}
}
