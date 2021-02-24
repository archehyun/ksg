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

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.ksg.adv.service.ADVService;
import com.ksg.adv.service.ADVServiceImpl;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.print.logic.quark.XTGManager;
import com.ksg.print.logic.quark.XTGPage;
import com.ksg.shippertable.service.TableService;
import com.ksg.shippertable.service.impl.TableServiceImpl;

public class CreateXTGCommandInD implements KSGCommand{

	protected Logger 			logger = Logger.getLogger(getClass());
	protected ADVService	 		_advService;
	protected TableService 		_tableService;
	int pages;
	String selectedCompany;
	XTGManager xtgmanager = new XTGManager();
	XTGPage page;
	private String selectDate;
	private int selectedPage;
	public String result="";
	private boolean isCreateFile=false;
	String data;
	String agent;

	SimpleDateFormat dateFormat = new SimpleDateFormat("mm/d");

	private CreateXTGCommandInD( String selectedCompany) {

		_advService = new ADVServiceImpl();
		_tableService = new TableServiceImpl();
		this.selectedCompany=selectedCompany;


	}
	public CreateXTGCommandInD(String selectCompany, int selectedPage,String date, boolean createFile,String data) {
		this(selectCompany);
		this.selectedPage=selectedPage;
		this.selectDate=date;
		this.isCreateFile=createFile;
		this.data=data;
	}
	public CreateXTGCommandInD(String selectCompany, String agent,String date, boolean createFile,String data) {
		this(selectCompany);
		this.agent=agent;
		this.selectDate=date;
		this.isCreateFile=createFile;
		this.data=data;
	}
	public int execute() {
		logger.debug("start");
		if(selectedCompany==null)
		{
			result="선택된 선사가 없습니다.";
			return RESULT_FAILE;
		}
		String version="";
		List<ADVData> advLi = new LinkedList<ADVData>();
		try {
			List lis =null;
			ShippersTable shippersTable = new ShippersTable();
			shippersTable.setPage(selectedPage);
			shippersTable.setAgent(agent);
			shippersTable.setDate_isusse(selectDate);
			lis=_tableService.getTableList(shippersTable);
			
			if(agent==null)
			{
				logger.debug("agent null");
				
				
				lis = _tableService.getTableListByPage(shippersTable);
			}
			else
			{
				lis = _tableService.getTableListByAgent(shippersTable);
			}
//			logger.debug(lis);

			for(int i=0;i<lis.size();i++)
			{
				ShippersTable table= (ShippersTable) lis.get(i);
				ADVData d = _advService.getADVData(table.getTable_id());
				if(d!=null&&table.getTable_index()!=0)
				{
					d.setTs(table.getGubun());
					advLi.add(d);
				}
			}

			if(advLi.size()==0)
			{
				result="광고정보가 없습니다.";
				return RESULT_FAILE;
			}
			for(int i=0;i<advLi.size();i++)
			{
				ADVData  data = advLi.get(i);
				ShippersTable table=_tableService.getTableById(data.getTable_id());
				if(table.getTable_index()==0)
					continue;
				//result+=data.getData()+"\n";
				if(i==0)//첫번째 태그 분석 - version
				{
					if(table.getQuark_format().length()<1)
					{
						JOptionPane.showMessageDialog(null, table.getPage()+"-"+table.getTable_index()+"번 테이블의 쿽 정보가 없습니다.");
						return RESULT_FAILE;
					}

					version=table.getQuark_format()+"\n<$><B>";
					System.out.println("vesrion:"+version);
				}
				else
				{
					int firstIndex=table.getQuark_format().indexOf("@");
					int lastIndex = table.getQuark_format().lastIndexOf(":");
					//					int boldLastIndex = table.getQuark_format().lastIndexOf(">");
					// 추후 수정 예정
					//result+="<"+table.getQuark_format().substring(fistIndex,lastIndex+1);//table
					try{
					version=table.getQuark_format().substring(firstIndex,table.getQuark_format().length());//bold
					}catch(Exception e)
					{
						logger.error("출력 오류"+e.getMessage());
						e.printStackTrace();
					}
				}

				//				logger.debug("result:"+result);
				String dataArray[][]=null;
				try {
					dataArray = data.getDataArray();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				String vesselList[][] = data.getAbbrVesselArray(false);
				
				for(int j=0;j<dataArray.length;j++)
				{
					// bold
					if(j==0)
					{	
						if(i==0)
						{
							result+=version+"\t"+vesselList[j][0]+"\t"+vesselList[j][1]+"<$>\t";
							if(data.getTs()!=null&&data.getTs().equals("TS"))
							{
								String tsvesselList[][]=data.getFullVesselArray(true);
								result+="<B>"+tsvesselList[j][0]+"\t"+tsvesselList[j][1]+"<$>\t";
							}
						}else
						{
							result+=version+"\n<$>\t<B>"+vesselList[j][0]+"\t"+vesselList[j][1]+"<$>\t";
							if(data.getTs()!=null&&data.getTs().equals("TS"))
							{
								String tsvesselList[][]=data.getFullVesselArray(true);
								result+="<B>"+tsvesselList[j][0]+"\t"+tsvesselList[j][1]+"<$>\t";
							}
						}

					}else
					{
						result+="<$>\t<B>"+vesselList[j][0]+"\t"+vesselList[j][1]+"<$>\t";
						if(data.getTs()!=null&&data.getTs().equals("TS"))
						{
							String tsvesselList[][]=data.getFullVesselArray(true);
							result+="<B>"+tsvesselList[j][0]+"\t"+tsvesselList[j][1]+"<$>\t";
						}
					}
					// 선박 + 항차 
					//result+=bold+vesselList[j][0]+"\t"+vesselList[j][1]+"<$>\t";

					/*for(int z=0;z<dataArray[j].length;z++)
					{
						result +=dataArray[j][z];
						if(z<dataArray[j].length-1)
							result+="\t";

					}*/
					for(int z=0;z<dataArray[j].length;z++)
					{
						StringTokenizer st = new StringTokenizer(dataArray[j][z],"-");
						if(st.countTokens()==2)
						{
							String date = st.nextToken();
							try {
								
								StringTokenizer st2 = new StringTokenizer(date,"/");
								
								int month  = Integer.parseInt(st2.nextToken());
								int day  = Integer.parseInt(st2.nextToken());
								
								StringTokenizer st3 = new StringTokenizer(st.nextToken(),"/");
								
								if(st3.countTokens()==2)
								{
									int month3  = Integer.parseInt(st3.nextToken());
									int day3  = Integer.parseInt(st3.nextToken());
									result +=month+"/"+day+"-"+month3+"/"+day3;
									
								}else
								{
									int day3  = Integer.parseInt(st3.nextToken());
									result +=month+"/"+day+"-"+(day<=day3?month:month+1)+"/"+day3;
								}
								/*int month3  = Integer.parseInt(st3.nextToken());
								int day3  = Integer.parseInt(st3.nextToken());
								
								result +=month+"/"+day+"-"+month3+"/"+day3;*/

							} catch (Exception e1) {
								result +=dataArray[j][z];
								logger.error(date);
								e1.printStackTrace();
							}
							
							/*try {
								
								
								StringTokenizer st2 = new StringTokenizer(st.nextToken(),"/");
								
								int month  = Integer.parseInt(st2.nextToken());
								int day  = Integer.parseInt(st2.nextToken());
								result +=month+"/"+day;

							} catch (Exception e1) {
								result +=dataArray[j][z];
								e1.printStackTrace();
							}*/



						}else if(st.countTokens()==1)
						{
							try {
								StringTokenizer st2 = new StringTokenizer(st.nextToken(),"/");
								
								int month  = Integer.parseInt(st2.nextToken());
								int day  = Integer.parseInt(st2.nextToken());
								
								result +=month+"/"+day;

							} catch (Exception e1) {
								result +=dataArray[j][z];
								e1.printStackTrace();
							}
							
						}else
						{
							result +=dataArray[j][z];
						}
						
						/*try
						{
							
							
							Date d =KSGDateUtil.toDateBySearch(dataArray[j][z]);
//							result +=dateFormat.parse(dataArray[j][z]).toString();
						
							result+=(d.getMonth()+"/"+d.getDay());
							
						} catch (ParseException e)
						{
							result +=dataArray[j][z];

							
							e.printStackTrace();
						}*/
						if(z<dataArray[j].length-1)
							result+="\t";
					}

					result= result.trim();
					if(j!=dataArray.length-1)
						result+="\n";

				}

				if(i<advLi.size()-1)
				{
					result+="<\\c>";	
				}


			}
		} catch (SQLException e) {

			e.printStackTrace();
			return RESULT_FAILE;
		}
		if(isCreateFile)
		{
			try{
				xtgmanager.createXTGFile(data,selectedPage+".txt");
				JOptionPane.showMessageDialog(null,selectedPage+".txt 파일을 생성했습니다.");
				return RESULT_SUCCESS;
			}catch(Exception e)
			{

				JOptionPane.showMessageDialog(null, selectedPage+".txt 파일을 생성에 실패 했습니다."+e.getMessage());
				return RESULT_FAILE;
			}
		}else
		{
			return RESULT_FAILE;
		}

	}
	public void execute2() {
		//		String advMessage = new String();
		// 테이블 정보 조회

		if(selectedCompany==null)
		{
			result="선택된 선사가 없습니다.";
			return;
		}


		try {
			//			String ver;
			String bold="";
			List<ADVData> advLi = _advService.getADVDataListByPage(selectedCompany, selectedPage, selectDate);
			if(advLi.size()==0)
			{
				result="광고정보가 없습니다.";
				return;
			}
			logger.debug("searched table size:"+advLi.size());
			// 광고 정보 조회
			for(int i=0;i<advLi.size();i++)
			{
				ADVData  data = advLi.get(i);
				ShippersTable table=_tableService.getTableById(data.getTable_id());
				//result+=data.getData()+"\n";
				if(i==0)//첫번째 태그 분석 - version
				{
					if(table.getQuark_format().length()<1)
					{
						JOptionPane.showMessageDialog(null, table.getPage()+"-"+table.getTable_index()+"번 테이블의 쿽 정보가 없습니다.");
						return;
					}
					int lastIndex=table.getQuark_format().lastIndexOf(":");
					int lastIndex2=table.getQuark_format().lastIndexOf(">");
					result+=table.getQuark_format().substring(0,lastIndex+1);//version

					bold=table.getQuark_format().substring(lastIndex+1,lastIndex2+1);//bold

				}else
				{
					int fistIndex=table.getQuark_format().lastIndexOf("*t");
					int lastIndex = table.getQuark_format().lastIndexOf(":");
					int boldLastIndex = table.getQuark_format().lastIndexOf(">");
					// 추후 수정 예정
					result+="<"+table.getQuark_format().substring(fistIndex,lastIndex+1);//table
					bold=table.getQuark_format().substring(lastIndex+1,boldLastIndex+1);//bold
				}

				logger.debug("result:"+result);
				String dataArray[][]=null;
				try {
					dataArray = data.getDataArray();
				} catch (OutOfMemoryError e2) {
					e2.printStackTrace();
				} catch (JDOMException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				String vesselList[][] = data.getFullVesselArray(false);
				for(int j=0;j<dataArray.length;j++)
				{
					// bold
					if(j==0)
					{
						result+=bold+"\t"+vesselList[j][0]+"\t"+vesselList[j][1]+"<$>\t";

					}else
					{
						result+="<$>\t<B>"+vesselList[j][0]+"\t"+vesselList[j][1]+"<$>\t";
					}
					// 선박 + 항차 
					//result+=bold+vesselList[j][0]+"\t"+vesselList[j][1]+"<$>\t";

					for(int z=0;z<dataArray[j].length;z++)
					{

						try
						{
							System.out.println(dateFormat.parse(dataArray[j][z]).toString());
							result +=dateFormat.parse(dataArray[j][z]).toString();
						} catch (ParseException e) 
						{
							result +=dataArray[j][z];

							StringTokenizer st = new StringTokenizer(dataArray[j][z]);

							if(st.countTokens()==2)
							{

								String temp;
								try {
									temp = dateFormat.parse(st.nextToken()).toString();
									temp+="-"+dateFormat.parse(st.nextToken()).toString();


								} catch (ParseException e1) {
									result +=dataArray[j][z];
									e1.printStackTrace();
								}


							}
							e.printStackTrace();
						}
						result +=dataArray[j][z];
						if(z<dataArray[j].length-1)
							result+="t";	

					}
					if(j!=dataArray.length-1)
						result+="\n";

				}
				if(i!=advLi.size()-1)
				{
					result+="<\\c>";	
				}
			}

			if(isCreateFile)
			{
				try{
					xtgmanager.createXTGFile(data,selectedPage+".txt");
					JOptionPane.showMessageDialog(null,selectedCompany+"_"+selectedPage+"_"+selectDate+" 파일을 생성했습니다.");
				}catch(Exception e)
				{
					JOptionPane.showMessageDialog(null, selectedPage+".txt 파일을 생성에 실패 했습니다."+e.getMessage());
				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

}
