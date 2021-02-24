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

public class CreateXTGCommand implements KSGCommand{

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
	private int printFlag;

	private CreateXTGCommand( String selectedCompany) {

		_advService = new ADVServiceImpl();
		_tableService = new TableServiceImpl();
		this.selectedCompany=selectedCompany;


	}
	public CreateXTGCommand(String selectCompany, int selectedPage,String date, boolean createFile,String data, int printFlag) {
		this(selectCompany);
		this.selectedPage=selectedPage;
		this.selectDate=date;
		this.isCreateFile=createFile;
		this.data=data;
		this.printFlag=printFlag;
		System.out.println("page로 넘어옴   ---"+printFlag);
	}
	public CreateXTGCommand(String selectCompany, String agent,String date, boolean createFile,String data,int printFlag) {
		this(selectCompany);
		this.agent=agent;
		this.selectDate=date;
		this.isCreateFile=createFile;
		this.data=data;
		this.printFlag=printFlag;
		System.out.println("agent로 넘어옴");
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
						JOptionPane.showMessageDialog(null, table.getPage()+"-"+table.getTable_index()+"번 테이블의 태그 정보가 없습니다.");
						return RESULT_FAILE;
					}

					if(printFlag == 1)
					{
						version=table.getQuark_format()+"\n<$><B>";
						System.out.println("vesrion:"+version);
					}
					else
					{	

						/*
						 * 
						 * 2013.6.4 유현경 수정 - 첫번째 스케줄 테이블의 태그만 개행처리
						 * 스케줄 자료(선박,항차,날짜)는 파일 생성 후 다시 개행 처리함
						 * XTGManager.java 에서 처리함 
						 * 
						 */
						
						String str1=table.getQuark_format();
						String str2="";
						String newline="\r\n";
						String line_lf="\n";
						
						for(int j=0;j<str1.length();j++) {
							if(str1.substring(j,j+1).compareTo(line_lf)==0){
								str2=str2+"\r"+str1.substring(j,j+1);
							}
							else{
								str2=str2+str1.substring(j,j+1);
							}
						}
						
						
						version=str2;   	//+"\n<ct:><cs:>";
//						version=table.getQuark_format();   	//+"\n<ct:><cs:>";
						/*
						 * 
						 * 2013.6.4 유현경 수정 - 첫번째 스케줄 테이블의 태그만 개행처리 [끝]
						 *
						 */

						System.out.println("vesrion:"+version);
					}
				}
				else
				{
					if(printFlag == 1)
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
							//e.printStackTrace();
						}
					}
					else
					{
						try{
						// version=table.getQuark_format();//.substring(firstIndex,table.getQuark_format().length());//bold
						version="\r\n"+table.getQuark_format();//.substring(firstIndex,table.getQuark_format().length());//bold
						}catch(Exception e)
						{
							logger.error("출력 오류"+e.getMessage());
							//e.printStackTrace();
						}
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
					if(printFlag==1)  // Quark인 경우
					{
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
					}else  // InDesign 인 경우
					{
						if(j==0)
						{	
							if(i==0)
							{
								result+=version+"\t"+vesselList[j][0]+"\t"+vesselList[j][1]+"<ct:>\t";
								if(data.getTs()!=null&&data.getTs().equals("TS"))
								{
									String tsvesselList[][]=data.getFullVesselArray(true);
									result+="<ct:Bold>"+tsvesselList[j][0]+"\t"+tsvesselList[j][1]+"<ct:>\t";
								}
							}else
							{
								result+=version+""+"<ct:Bold>"+"\t"+vesselList[j][0]+"\t"+vesselList[j][1]+"<ct:>\t";
								//result+=version+""+"<ct:Bold>"+vesselList[j][0]+"\t"+vesselList[j][1]+"<ct:>\t";
								if(data.getTs()!=null&&data.getTs().equals("TS"))
								{
									String tsvesselList[][]=data.getFullVesselArray(true);
									result+="<ct:Bold>"+tsvesselList[j][0]+"\t"+tsvesselList[j][1]+"<ct:>\t";
								}
							}

						}else
						{
							result+="\r\n"+"<ct:Bold>"+"\t"+vesselList[j][0]+"\t"+vesselList[j][1]+"<ct:>\t";
//							result+="\r\n"+"<ct:Bold>"+"\t"+vesselList[j][0]+"\t"+vesselList[j][1]+"<ct:>\t";
							//result+="\r\n"+"<ct:Bold>"+vesselList[j][0]+"\t"+vesselList[j][1]+"<ct:>\t";
							if(data.getTs()!=null&&data.getTs().equals("TS"))
							{
								String tsvesselList[][]=data.getFullVesselArray(true);
								result+="<ct:Bold>"+tsvesselList[j][0]+"\t"+tsvesselList[j][1]+"<ct:>\t";
							}
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
							String firstDate = st.nextToken();
							String secondDate=st.nextToken();
							try {
									
								StringTokenizer stFirstDate = new StringTokenizer(firstDate,"/");
								
								int firstMonth  = Integer.parseInt(stFirstDate.nextToken());
								int firstDay  = Integer.parseInt(stFirstDate.nextToken());
								
								StringTokenizer stSecondDate = new StringTokenizer(secondDate,"/");
								
								
								int secondMonth;
								int secondDay;
								if(stSecondDate.countTokens()==2)
								{
									secondMonth  = Integer.parseInt(stSecondDate.nextToken());
									secondDay  = Integer.parseInt(stSecondDate.nextToken());
									
								}else
								{
									secondDay  = Integer.parseInt(stSecondDate.nextToken());
									secondMonth =firstDay<=secondDay?firstMonth:firstMonth+1;
								}
								result +=firstMonth+"/"+firstDay+"-"+secondMonth+"/"+secondDay;
								

							} catch (Exception e1) {
								result +=dataArray[j][z];
								logger.error(firstDate);
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
							/*try {
								StringTokenizer st2 = new StringTokenizer(st.nextToken(),"/");
								
								int month  = Integer.parseInt(st2.nextToken());
								int day  = Integer.parseInt(st2.nextToken());
								
								result +=month+"/"+day;

							} catch (Exception e1) {
								result +=dataArray[j][z];
								e1.printStackTrace();
							}*/
							
							/* 2014.4.18 박창현씨 수정
							 * 이하 수정된 부분은 날짜 앞에 0이 오는 경우 0을 제외하고 출력하도록 한 것임
							 */
							String monthStr=null,dayStr=null;
							try {

								
								StringTokenizer stOneDate = new StringTokenizer(st.nextToken(),"/");
								
								if(stOneDate.countTokens()==1) // 날짜 형식이 아닌 경우
								{
									result +=stOneDate.nextToken();
								}
								else if(stOneDate.countTokens()==2) // 날짜 형식 인 경우
								{
									try{
										monthStr =stOneDate.nextToken();
										int month  = Integer.parseInt(monthStr);
										
										result +=String.valueOf(month); // 월 정보 추가
									}catch(NumberFormatException e1) // 숫자 형식이 아닐때
									{
										result +=monthStr;
									}
									
									try{
										// 오류 발생!
										dayStr =stOneDate.nextToken();
										int day  = Integer.parseInt(dayStr);
										result +="/"+String.valueOf(day); // 일 정보 추가, 
									}catch(NumberFormatException e1)//숫자 형식이 아닐때
									{
	/*
										if(dayStr.charAt(0)=='0')// 한자리
										{										
											result +="/"+String.valueOf(dayStr.charAt(1)) // 1자리 일
														+" "+dayStr.substring(2,dayStr.length()).trim();// 기타 문자(AM, PM..)
										}else // 두자리
										{
											result +="/"+String.valueOf(dayStr.charAt(0))+String.valueOf(dayStr.charAt(1))//2자리 일
														+" "+dayStr.substring(2,dayStr.length()).trim();// 기타 문자(AM, PM..)
										}
	*/									
										if(dayStr.charAt(0)=='0')// 한자리
										{										
											result +="/"+dayStr.substring(1);
										}else // 두자리
										{
											result +="/"+dayStr;
										}
									}
									
								}
								
								


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
					if(printFlag==1)
					{
						result+="<\\c>";
					}
					else
					{	
						result+="";
					}
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
				
				
				if(printFlag==1)
				{
					if(i==0)//첫번째 태그 분석 - version
					{
						if(table.getQuark_format().length()<1)
						{
							JOptionPane.showMessageDialog(null, table.getPage()+"-"+table.getTable_index()+"번 테이블의 태그 정보가 없습니다.");
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
				}
				else			
				{
					if(i==0)//첫번째 태그 분석 - version
				
					{
						if(table.getQuark_format().length()<1)
						{
							JOptionPane.showMessageDialog(null, table.getPage()+"-"+table.getTable_index()+"번 테이블의 태그 정보가 없습니다.");
							return;
						}
						/*
						int lastIndex=table.getQuark_format().lastIndexOf(":");
						int lastIndex2=table.getQuark_format().lastIndexOf(">");
						result+=table.getQuark_format().substring(0,lastIndex+1);//version

						bold=table.getQuark_format().substring(lastIndex+1,lastIndex2+1);//bold
*/
						result+="여기가 어디냐?????";
						bold="여기는 또 어디냐????? - Bold tag인데";
					}else
					{
						/*
						int fistIndex=table.getQuark_format().lastIndexOf("*t");
						int lastIndex = table.getQuark_format().lastIndexOf(":");
						int boldLastIndex = table.getQuark_format().lastIndexOf(">");
						// 추후 수정 예정
						result+="<"+table.getQuark_format().substring(fistIndex,lastIndex+1);//table
						bold=table.getQuark_format().substring(lastIndex+1,boldLastIndex+1);//bold
						*/
						result+="else인 경우 여기가 어디냐?????";
						bold="else인 경우 여기는 또 어디냐????? - Bold tag인데";

					}
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
						result+="\r\n";

				}
				if(i!=advLi.size()-1)
				{
					if(printFlag==1)
					{
						result+="<\\c>";			
					}
					else
					{
						result+="";
					}
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
