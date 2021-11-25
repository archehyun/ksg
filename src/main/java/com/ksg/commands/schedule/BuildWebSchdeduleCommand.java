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
package com.ksg.commands.schedule;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.schedule.task.WebScheduleTask;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.schedule.ScheduleService;
import com.ksg.schedule.view.dialog.ScheduleBuildMessageDialog;
import com.ksg.service.ADVService;
import com.ksg.service.TableService;
import com.ksg.shippertable.service.impl.TableServiceImpl;
@SuppressWarnings("unchecked")
public class BuildWebSchdeduleCommand implements KSGCommand 
{
	private static final String TS = "TS";
	XTGManager manager = new XTGManager();

	private int[] a_outport;
	private int[] a_outtoport;
	private ADVService advService;

	private String[][] arrayDatas;
	private BaseService baseService;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	String outPortData;
	String outToPortData;
	private String[] portData;
	private Vector portDataArray;
	ScheduleBuildMessageDialog processMessageDialog;
	private Vector scheduleDataList;
	
	private Vector totalScheduleList;

	private ScheduleService scheduleService;
	private ShippersTable searchOption;
	private Date selectedDate;

	private int size_outport;
	private int size_outtoport;
	private TableService tableService;

	private String strYear;

	private int total;
	private int currentMonth;

	List portList;
	List portAbbrList;
	private String[][] vslDatas;
	private String strMonth;
	HashMap<String, Vector> map;
	private int scheduleID;
	public BuildWebSchdeduleCommand(ScheduleBuildMessageDialog ob) {
		tableService = new TableServiceImpl();
		scheduleService = DAOManager.getInstance().createScheduleService();
		advService = DAOManager.getInstance().createADVService();
		baseService = DAOManager.getInstance().createBaseService();
		this.processMessageDialog =ob;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM");

		strYear = sdf.format(new Date());
		strMonth = sdf.format(new Date());
		currentMonth = Integer.valueOf(sdf1.format(new Date()));
		map = new HashMap<String, Vector>();
		for(int i=1;i<8;i++)
		{
			for(int j=1;j<8;j++)
			{
				map.put("0"+i+"0"+j, new Vector());					
			}
		}
	}

	public BuildWebSchdeduleCommand(ScheduleBuildMessageDialog ob,ShippersTable op) {
		this(ob);
		searchOption =op;

	}

	private int result=KSGCommand.PROCESS;
	public int execute() {
		
			
			SwingWorker worker = new SwingWorker() {

			
				public Object construct() {

					return new WebScheduleTask(searchOption);
				}
			};
			worker.start();
			
			
		
		logger.debug("<==start build schedule ==>");
		return result;
		

	}
	


	/**
	 * @param array
	 * @param index
	 * @return
	 */
	private TablePort getPort(Vector array,int index)
	{
		TablePort port1 = new TablePort();
		for(int i=0;i<array.size();i++)
		{
			TablePort port=(TablePort) array.get(i);
			if(port.getPort_index()==index)
				port1.addSubPort(port);
		}
		return port1;

	}
	/**
	 * @param table
	 * @return
	 */
	private Vector getPortList(ShippersTable table) 
	{
		try {
			portDataArray  = new Vector();


			TablePort tablePort = new TablePort();
			tablePort.setTable_id(table.getTable_id());
			tablePort.setPort_type(TablePort.TYPE_PARENT);

			List li=tableService.getTablePortList(tablePort);

			for(int i=0;i<li.size();i++)
			{
				TablePort port = (TablePort) li.get(i);

				portDataArray.add(port);
			}
			logger.debug("portarray:"+table.getTable_id()+"\n"+portDataArray);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return portDataArray;
	}

	private boolean biggerDate(String onedate, String twodate) {

		try {
			if(KSGDateUtil.daysDiff(KSGDateUtil.toDateBySearch(onedate), KSGDateUtil.toDateBySearch(twodate))>=0)
			{
				return true;
			}else
			{
				return false;
			}
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage()+"oneDate:"+onedate+", twoDate:"+twodate);
		}
	}

	/**
	 * @param table
	 * @param vslIndex
	 * @param InOutBoundType
	 * @param outPortIndex
	 * @param outToPortIndex
	 * @param outPort 국내항 인덱스
	 * @param outToPort 외국항 인덱스
	 * @param adv 광고정보
	 * @throws SQLException
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private void insertWebSchedule(ShippersTable table, int vslIndex,
			String InOutBoundType, int outPortIndex, int outToPortIndex, String outPort, String outToPort,ADVData adv) throws SQLException,ArrayIndexOutOfBoundsException {

		outToPortData = arrayDatas[vslIndex][outToPortIndex-1];
		outPortData = arrayDatas[vslIndex][outPortIndex-1];

		if(!outToPortData.equals("-")&&!outPortData.equals("-")&&
				!outToPortData.equals("_")&&!outPortData.equals("_")&&
				!outToPortData.trim().equals("")&&!outPortData.trim().equals(""))
		{
			ScheduleData scheduledata = new ScheduleData();
			scheduledata.setTable_id(table.getTable_id());
			scheduledata.setGubun(table.getGubun());
			
//			 선박명이 - 이면 스케줄 처리 안함
			if(vslDatas[vslIndex][0]==null)
				return;
			// 선박명이 - 이면 스케줄 처리 안함
			if(vslDatas[vslIndex][0].equals("-"))
				return;


			scheduledata.setVessel(vslDatas[vslIndex][0]);
			if(table.getGubun().equals(TS))
			{
				String vsl[][] = adv.getFullVesselArray(true);
				scheduledata.setTs_vessel(vsl[vslIndex][0]);
				scheduledata.setTs_voyage_num(vsl[vslIndex][1]);
				TablePort tablePort = new TablePort();
				//tablePort.setPort_index(table.getDirection());
				tablePort.setTable_id(table.getTable_id());
				TablePort info;
				info = (TablePort) tableService.getTablePort(tablePort);
				scheduledata.setTs(info.getPort_name());

				String date[][]=null;
				try {
					date = adv.getDataArray();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//scheduledata.setTs_date(date[vslIndex][table.getDirection()-1]);
			}
			scheduledata.setAgent(table.getAgent());

			String dateFs[] = this.getDateList(arrayDatas[vslIndex][outPortIndex-1]);
			String dateTs[] = this.getDateList(arrayDatas[vslIndex][outToPortIndex-1]);

			if(dateFs[0].equals("0/0/0")||dateFs[1].equals("0/0/0"))
			{
				logger.debug("f날짜 형식 오류=>table_id:"+table.getTable_id()+",page:"+table.getPage()+",index:"+table.getTable_index());
				return;
			}
			if(dateTs[0].equals("0/0/0")||dateTs[1].equals("0/0/0"))
			{
				logger.debug("t날짜 형식 오류=>table_id:"+table.getTable_id()+",page:"+table.getPage()+",index:"+table.getTable_index());
				return;
			}
			scheduledata.setDateF(dateFs[0]);
			scheduledata.setDateFBack(dateFs[1]);
			
			scheduledata.setDateT(dateTs[0]);
			scheduledata.setDateTBack(dateTs[1]);
			scheduledata.setFromPort(outPort);
			scheduledata.setPort(outToPort);


			// 항상 빠른 날짜의 항구가 앞으로 나오도록 조정
			/*	if(biggerDate(dateFs[0], dateTs[0]))
			{
				scheduledata.setDateF(dateFs[0]);
				scheduledata.setDateFBack(dateFs[1]);
				scheduledata.setDateT(dateTs[0]);
				scheduledata.setDateTBack(dateTs[1]);
				scheduledata.setFromPort(outPort);
				scheduledata.setPort(outToPort);
			}else
			{
				logger.error("swap:"+dateFs[0]+","+dateTs[0]) ;
				scheduledata.setDateF(dateTs[0]);
				scheduledata.setDateFBack(dateTs[1]);
				scheduledata.setDateT(dateFs[0]);
				scheduledata.setDateTBack(dateFs[1]);
				scheduledata.setFromPort(outToPort);
				scheduledata.setPort(outPort);
				logger.error("swap result=> f:"+scheduledata.getDateF()+",t:"+scheduledata.getDateT()) ;
			}*/



			scheduledata.setCompany_abbr(table.getCompany_abbr());
			scheduledata.setVoyage_num(vslDatas[vslIndex][1]);
			//scheduledata.setE_I(InOutBoundType);
			scheduledata.setInOutType(InOutBoundType);
			scheduledata.setGubun(table.getGubun());

			scheduledata.setCommon_shipping("");
			scheduledata.setDate_issue(table.getDate_isusse());
			scheduledata.setTable_id(table.getTable_id());
			if(scheduledata.getPort()==null)
			{
				logger.error("port is null=>"+scheduledata.getTable_id());
				return;
			}



			PortInfo info=getAreaCode(scheduledata.getPort());
			if(info==null)
			{
				logger.error("tableID:"+scheduledata.getTable_id()+",company:"+scheduledata.getCompany_abbr()+",등록되지 않은 Port:"+scheduledata.getPort());
				return;
			}else
			{
				scheduledata.setArea_code(info.getArea_code());	
			}

			PortInfo frominfo=getAreaCode(scheduledata.getFromPort());
			if(frominfo==null)
			{
				logger.error("tableID:"+scheduledata.getTable_id()+",company:"+scheduledata.getCompany_abbr()+",등록되지 않은From Port:"+scheduledata.getPort());
				return;
			}else
			{
				scheduledata.setFromAreaCode(frominfo.getArea_code());	
			}


			/*	PortInfo  FromPortInfo=getPortInfoByPortName(scheduledata.getFromPort() );
			if(FromPortInfo!=null)
			{
				scheduledata.setFromAreaCode(FromPortInfo.getArea_code());
			}else
			{
				PortInfo  portabbrInfo=getPortInfoAbbrByPortName(scheduledata.getFromPort() );
				if(portabbrInfo!=null)
				{
					scheduledata.setFromAreaCode(portabbrInfo.getArea_code());
				}else
				{
					logger.error("tableID:"+scheduledata.getTable_id()+",등록되지 않은 FromPort:"+scheduledata.getPort());
					return;
				}
			}*/


			String key=scheduledata.getFromAreaCode()+scheduledata.getArea_code();
			totalScheduleList.add(scheduledata);
			/*if(map.containsKey(key))
			{
				Vector d=map.get(key);
				System.out.println(key+" add "+scheduledata.toWebSchedule());
				d.add(scheduledata);
			}else
			{
				//logger.error("no have key:"+key+","+scheduledata.getTable_id()+","+scheduledata.getArea_name());
			}*/

		}



	}

	private PortInfo getAreaCode(String portName) throws SQLException {
		PortInfo  portInfo=getPortInfoByPortName(portName );
		if(portInfo!=null)
		{
			//			scheduledata.setArea_code(portInfo.getArea_code());
			return portInfo;
		}else
		{
			PortInfo  portabbrInfo=getPortInfoAbbrByPortName(portName );
			if(portabbrInfo!=null)
			{
				//				scheduledata.setArea_code(portabbrInfo.getArea_code());
				return portabbrInfo;
			}else
			{
				//logger.error("tableID:"+scheduledata.getTable_id()+",company:"+scheduledata.getCompany_abbr()+",등록되지 않은 Port:"+scheduledata.getPort());
				return null;
			}
		}
	}	
	public String[] getDateList(String dateF)
	{
		String datePattern = "(\\d{1,2})/(\\d{1,2})";
		String datePattern1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})";
		String datePattern2 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})";
		int monthF = 0;
		int dayF = 0;
		int yearF = 0;

		int monthT = 0;
		int dayT = 0;
		int yearT = 0;

		if(dateF.matches(datePattern))
		{
			Pattern patt = Pattern.compile(datePattern);
			Matcher matcher = patt.matcher(dateF);
			matcher.lookingAt();

			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));		
			yearF = Integer.valueOf(strYear);


			monthT=monthF;
			dayT=dayF;	
			yearT= yearF;



		}else if(dateF.matches(datePattern1))
		{
			Pattern patt = Pattern.compile(datePattern1);
			Matcher matcher = patt.matcher(dateF);			
			matcher.lookingAt();
		

			yearF = Integer.valueOf(strYear);
			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));
			yearT = Integer.valueOf(strYear);
			monthT=Integer.valueOf(matcher.group(3));
			dayT=Integer.valueOf(matcher.group(4));
		}
		else if(dateF.matches(datePattern2))
		{
			Pattern patt = Pattern.compile(datePattern2);
			Matcher matcher = patt.matcher(dateF);			
			matcher.lookingAt();

			yearF = Integer.valueOf(strYear);
			yearT = Integer.valueOf(strYear);

			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));
			monthT=Integer.valueOf(matcher.group(1));
			dayT=Integer.valueOf(matcher.group(3));

		}

		int tempMonth = currentMonth+6;
		if(monthF>(tempMonth))
		{			
			yearF--;
		}
		if(monthT>(tempMonth))
		{
			yearT--;
		}

		String dd[] = new String[2]; 


		{
			dd[0] = yearF+"/"+monthF+"/"+dayF;
			dd[1] = yearT+"/"+monthT+"/"+dayT;
		}
		return dd;

	}



	private PortInfo getPortInfoByPortName(String portName) throws SQLException
	{
		Iterator<PortInfo> iter = portList.iterator();
		while(iter.hasNext())
		{
			PortInfo info = iter.next();
			if(info.getPort_name().equals(portName))
				return info;
		}
		return null;
	}
	private PortInfo getPortInfoAbbrByPortName(String portName) throws SQLException
	{
		Iterator<PortInfo> iter = portAbbrList.iterator();
		while(iter.hasNext())
		{
			PortInfo info = iter.next();
			if(info.getPort_abbr().equals(portName))
				return info;
		}
		return null;
		//return baseService.getPortInfoAbbrByPortName(portName);
	}

	/**
	 * @param field
	 * @param table
	 * @return
	 */
	public int[] makePortArrayWebIndex(String outPort, String outToPort,String inPort, String inToPort) throws NumberFormatException{

		HashMap<Integer, Integer> indexlist = new HashMap<Integer, Integer>();
		StringTokenizer st = new StringTokenizer(outPort,"#");

		Vector<Integer> outPortList = new Vector<Integer>();
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(Integer.parseInt(st.nextToken().trim())))
			{
				indexlist.put(indexItem,indexItem);
			}

			//			outPortList.add(Integer.parseInt(st.nextToken().trim()));
		}

		Vector<Integer> outToPortList = new Vector<Integer>();
		st = new StringTokenizer(outToPort,"#");
		while(st.hasMoreTokens())
		{
			//			outPortList.add(Integer.parseInt(st.nextToken().trim()));

			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(Integer.parseInt(st.nextToken().trim())))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		Vector<Integer> inPortList = new Vector<Integer>();
		st = new StringTokenizer(inPort,"#");
		while(st.hasMoreTokens())
		{
			//			inPortList.add(Integer.parseInt(st.nextToken().trim()));
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(Integer.parseInt(st.nextToken().trim())))
			{
				indexlist.put(indexItem,indexItem);
			}

		}

		Vector<Integer> inToPortdexList = new Vector<Integer>();
		st = new StringTokenizer(inToPort,"#");
		while(st.hasMoreTokens())
		{
			//			inToPortdexList.add(Integer.parseInt(st.nextToken().trim()));
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(Integer.parseInt(st.nextToken().trim())))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		int array[] =new int[outPortList.size()+outToPortList.size()];
		int i=0;
		for(;i<outPortList.size();i++)
		{
			array[i]=outPortList.get(i);
		}
		for(int j=i;j<outToPortList.size();j++,i++)
		{
			array[i]=outPortList.get(j);
		}

		return array;
	}
	public HashMap makePortArrayWebIndexMap(String outPort, String outToPort,String inPort, String inToPort) throws NumberFormatException{

		HashMap<Integer, Integer> indexlist = new HashMap<Integer, Integer>();
		StringTokenizer st = new StringTokenizer(outPort,"#");

		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(outToPort,"#");
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(inPort,"#");
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(inToPort,"#");
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}
		return indexlist;
	}
	private void makeWebSchedule(ShippersTable table, int vslIndex, int portList[],String InOutBoundType,ADVData adv)
	throws ArrayIndexOutOfBoundsException,NullPointerException, ParseException
	{
		portDataArray=getPortList(table);
		for(int i=0;i<portList.length;i++)
		{
			int FromPortIndex= portList[i];
			for(int j=i+1;j<portList.length;j++)
			{
				int ToPortIndex = portList[j];
				TablePort _outport = this.getPort(portDataArray, FromPortIndex);
				TablePort _outtoport = this.getPort(portDataArray, ToPortIndex);
				String outportarray[]=_outport.getPortArray();
				String outtoportarray[]=_outtoport.getPortArray();
				for(int z =0;z<outportarray.length;z++)
				{
					for(int c =0;c<outtoportarray.length;c++)
					{
						try {
							insertWebSchedule(table, vslIndex,
									InOutBoundType, FromPortIndex, ToPortIndex, outportarray[z], outtoportarray[c],adv);
						} catch (SQLException e) {
							JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e.getMessage());
							e.printStackTrace();
							throw new  RuntimeException();

						}catch (ArrayIndexOutOfBoundsException e) {
							//JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "배열 인덱스 오류 :"+e.getMessage()+","+table.getTable_id()+", 페이지:"+table.getPage()+","+table.getTable_index());
							logger.error("배열 인덱스 오류 :"+e.getMessage()+","+table.getTable_id()+", 페이지:"+table.getPage()+","+table.getTable_index());
							StringBuffer errorlogger = new StringBuffer();
							for(int error=0;error<portList.length;error++)
								errorlogger.append(portList[error]+",");
							logger.error(errorlogger.toString());
							//e.printStackTrace();
							//throw new  RuntimeException();
						}
					}
				}
			}	
		}

	}

	private void notifyMessage(String message)
	{

		KSGModelManager.getInstance().workProcessText=message;
		if(processMessageDialog!=null)
			KSGModelManager.getInstance().execute(processMessageDialog.getName());
	}

	public static void main(String[] args) throws NoSuchElementException, ParseException {
		ShippersTable op = new ShippersTable();
		op.setDate_isusse("2011.2.14");
		op.setPage(228);
		BuildWebSchdeduleCommand command = new BuildWebSchdeduleCommand(null,op);
		//		System.out.println(command.biggerDate("2011/3/15", "2011/3/12"));
		command.execute();

	}
}
