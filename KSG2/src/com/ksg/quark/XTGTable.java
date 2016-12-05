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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.ksg.domain.ADVData;

public class XTGTable extends XTGDoc{

	private int pageIndex;
	private String stringData;
	private String quarkData;
	private ADVData newData;
	Vector rows= new Vector();
	private String preFix;
	private String table_id;
	private boolean isFirst;
	public XTGTable() {
		
	}
	public XTGTable(String table_id) {
		this.table_id=table_id;
		logger.debug(table_id+": XTG 생성");
	}
	@Override
	public String getPreFix() {
		// TODO Auto-generated method stub
		return null;
	}
	private void addXTGRow(XTGRow row)
	{
		this.rows.add(row);
	}



	@Override
	public void setPreFix(String preFix) {
		this.preFix = preFix;

	}


	@Override
	public void setPaser(XTGPaser parser) {
		this.parser = parser;

	}

	@Override
	public String parseDoc() 
	{
		String message = new String();
		if(pageIndex==0)
			message=preFix;
		Iterator iter =rows.iterator();
		while(iter.hasNext())
		{
			XTGRow row = (XTGRow) iter.next();
			message+=row.parseDoc();
			if(iter.hasNext())
				message+="\n";
		}
		return message;
	}

	@Override
	public void setADVData(String data) {
		this.stringData=data;


	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	@Override
	public void setQuarkData(String data) {

		this.quarkData=data;
		//logger.debug("quark 데이터 할당 :\n"+quarkData);
		if(quarkData==null)
		{
			JOptionPane.showMessageDialog(null, "쿽스타일 정보가 없습니다.");
			return;
		}
		
		
		String frame[][]=parser.getFrame(quarkData);//데이터 를 반환함
		
		String rowformat[]=parser.getRowTag(quarkData);// 테이블의 row의 사전정보를 반환
		
		
		if(parser.getPagePrefix()!=null)
		{
			this.setPreFix(parser.getPagePrefix()+"@$:"+parser.getTablePrefix());
		}	
		else
		{
			this.setPreFix(parser.getTablePrefix());
		}

		if(frame!=null)
		{
			logger.debug("table_id:"+table_id+ " table row "+frame.length+"개 생성 시작");
			for(int i=0;i<frame.length;i++)
			{
				XTGRow row = new XTGRow(i);
				row.createXTGCell(frame[i]);
				row.setPreFix(rowformat[i]);
				this.addXTGRow(row);
			}
		}
	}

	public void setNewADVData(String data) {

		this.stringData=data;
		logger.debug("새로운 테이블 데이터 할당:\n"+stringData);
		StringTokenizer st = new StringTokenizer(data,"\n");
		String[][] newRow = new String[st.countTokens()-1][];
		logger.debug("삭제:"+st.nextToken());// 제목 표시줄 삭제
		logger.debug("row size:"+newRow.length);
		for(int i=0;i<newRow.length;i++)
		{
			StringTokenizer st2= new StringTokenizer(st.nextToken(),"\t");
			newRow[i]=new String[st2.countTokens()];
			for(int j=0;j<newRow[i].length;j++)
			{
				newRow[i][j]=st2.nextToken();
			}
		}
		for(int i=0;i<newRow.length;i++)
		{
			XTGRow row=this.getXTGRow(i);
			if(row!=null)
				row.setADVData(newRow[i]);
		}
	}
	private XTGRow getXTGRow(int i) {
		if(i>=rows.size())
		{
			//TODO 인덱스가 차이날때 처리할 룰 작성
			/* 처리안 1:바로 위의 포맷을 적용
			 * 처리안 2:적용하지 않음
			 * 
			 */
			logger.error("인댁스가 차이 납니다.\nrows size:"+rows.size()+" index:"+i);
			return null;
		}else
		{
			return (XTGRow) this.rows.get(i);
		}



	}
	public void setTable_ID(String table_id) {
		this.table_id=table_id;

	}
	public String getTable_ID()
	{
		return table_id;
	}

}
