package com.ksg.commands.schedule.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.jdom.JDOMException;

import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.schedule.logic.PortIndexNotMatchException;
import com.ksg.service.ADVService;
import com.ksg.service.ScheduleService;
import com.ksg.service.TableService;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

public class WebScheduleTask extends SimpleTask{
	public static final int RESULT_SUCCESS=0;
	public static final int RESULT_FAILE=1;


	private static final String TS = "TS";
	private XTGManager manager = new XTGManager();
	private Vector totalScheduleList;
	private int[] a_outport,a_outtoport;
	
	private ADVService advService;
	private XTGManager xtgmanager = new XTGManager();
	private String[][] arrayDatas;
	
	private String outPortData,outToPortData;
	
	private String[] portData;
	private Vector portDataArray,scheduleDataList;
	
	ScheduleBuildMessageDialog processMessageDialog;
	

	private ShippersTable searchOption;
	private Date selectedDate;

	private int size_outport,size_outtoport;
	
	private TableService tableService;

	private String strYear;

	private int total;
	private int currentMonth;

	private List portList;
	private List portAbbrList;
	private String[][] vslDatas;
	private String strMonth;
	private HashMap<String, Vector> map;
	private int scacheduleID;
	private Vector errorlist;
	public WebScheduleTask() {
		errorlist= new Vector();
		tableService = new TableServiceImpl();
		scheduleService = DAOManager.getInstance().createScheduleService();
		advService = DAOManager.getInstance().createADVService();
		baseService = DAOManager.getInstance().createBaseService();

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
	public WebScheduleTask(ShippersTable op) {
		this();
		searchOption =op;
		try{
			this.makeWebSchedule();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}catch(OutOfMemoryError e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());			
		}
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
	private List table_list = null;
	private ADVData advData = null;
	private int makeWebSchedule() throws SQLException, OutOfMemoryError
	{
		logger.debug("<==start build schedule ==>");

		portList = baseService.getPortInfoList();
		portAbbrList = baseService.getPort_AbbrList();


		totalScheduleList = new Vector();
		scheduleDataList = new Vector();
		try {
			/************ 전체 테이블 정보 조회  **********
			 * 1. 구분이 NULL 또는 NNN이 아닌 경우를 조회
			 * ********************************************			 
			 */
			logger.debug("search option:"+searchOption);

			table_list = tableService.getScheduleTableListByDate(searchOption);
			total = table_list.size();
			logger.debug("스케줄 처리용 테이블 수 : "+total);
			Iterator iter = table_list.iterator();

			lengthOfTask = table_list.size();

			processMessageDialog = new ScheduleBuildMessageDialog(this);
			processMessageDialog.createAndUpdateUI();
			int process=0;
			while(iter.hasNext())
			{
				ShippersTable tableData = (ShippersTable) iter.next();
				// 스케줄 처리할 내용 테이블만 작업 함
				try {
					// 날짜 지정 향후 DB 쿼리로 대체
					if(KSGDateUtil.daysDiffABS(KSGDateUtil.toDate2(tableData.getDate_isusse()), KSGDateUtil.toDate2(selectedDate))!=0)
						continue;
				} catch (ParseException e1) 
				{
					e1.printStackTrace();
				}
				// 해당 테이블에 대한 광고정보 조회
				advData=(ADVData) advService.getADVData(tableData.getTable_id());

				// 입력된 광고 정보가 없으며 통과
				if(advData==null||advData.getData()==null)
					continue;

				if(processMessageDialog!=null)
					processMessageDialog.setMessage("");
					arrayDatas = advData.getDataArray();
				
				if(tableData.getTS()!=null&&tableData.getTS().equals(TS))
				{
					vslDatas = advData.getFullVesselArray(true);

					logger.debug("<=======>"+advData.getTable_id());
					for(int i=0;i<vslDatas.length;i++)
					{

						for(int j=0;j<vslDatas[i].length;j++)
						{
							logger.debug(vslDatas[i][j]+",");
						}
					}

				}else
				{
						vslDatas = advData.getFullVesselArray(false);
					
					logger.debug("<=======>"+advData.getTable_id());
					for(int i=0;i<vslDatas.length;i++)
					{

						for(int j=0;j<vslDatas[i].length;j++)
						{
							logger.debug(vslDatas[i][j]+",,");
						}

					}
				}

				// 항구 정보<=== 변경 해야 함 DB에서 조회 해야 함
				portData =advData.getPortArray();

				/**
				 * 각 항구 인덱스를 이용해서 
				 * 스케줄에서 불러오 배열을 생성함
				 */
				try{

					String outPort = tableData.getOut_port();
					String outToPort = tableData.getOut_to_port();
					String inPort = tableData.getIn_port();
					String inToPort = tableData.getIn_to_port();
					if(outPort==null)
					{
						outPort="";
					}
					if(outToPort==null)
					{
						outToPort="";
					}
					if(inPort==null)
					{
						inPort="";
					}
					if(inToPort==null)
					{
						inToPort="";
					}

					outPort = outPort.trim();
					outToPort = outToPort.trim();
					inPort = inPort.trim();
					inToPort = inToPort.trim();

					HashMap<Integer, Integer> map=makePortArrayWebIndexMap(outPort, outToPort,inPort,inToPort);

					//TS 항구 인덱스  추가
					if(tableData.getGubun().equals(TS))
					{

						int ts_index=tableData.getTsIndex();

						logger.error("TS 추가 "+(ts_index));
						if(!map.containsKey(ts_index))
						{
							map.put(ts_index, ts_index);
						}
					}

					Set keySet =map.keySet();

					a_outport = new int[keySet.size()];
					Iterator<Integer> iter2 = keySet.iterator();
					for(int i=0;iter2.hasNext();i++)
					{
						a_outport[i]=iter2.next();
					}
					Arrays.sort(a_outport);


					StringBuffer tset = new StringBuffer();

					for(int i=0;i<a_outport.length;i++)
					{
						tset.append(a_outport[i]+",");

					}


				}catch(NumberFormatException e)
				{
					logger.equals("숫자 형식 오류 =>tableId:"+tableData.getTable_id()+",page:"+tableData.getPage()+",index:"+tableData.getTable_index()+",국내항:"+tableData.getOut_port()+",외국항:"+tableData.getOut_to_port());

					ErrorLog log = new ErrorLog();
					log.setTableID(tableData.getTable_id()+":"+"page:"+tableData.getPage()+",index:"+tableData.getTable_index()+",국내항:"+tableData.getOut_port()+",외국항:"+tableData.getOut_to_port());
					log.setType("숫자 형식 오류");
					errorlist.add(log);
				}

				if(a_outport!=null)
					size_outport=a_outport.length;


				a_outtoport = a_outport;
				if(a_outtoport!=null)				
					size_outtoport=a_outtoport.length;

				for(int vslIndex=0;vslIndex<vslDatas.length;vslIndex++)
				{
					try 
					{
						makeWebSchedule(tableData, vslIndex,a_outport,ScheduleService.OUTBOUND,advData);
					}

					catch (PortIndexNotMatchException e) 
					{
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e.table.getCompany_abbr()+"선사의 "+e.table.getPage()+" 페이지,"+e.table.getTable_index()+
						"번 테이블의 스케줄 정보 생성시 문제가 생겼습니다.\n\n항구정보, 항구 인덱스 정보,날짜 형식를 확인 하십시요.\n\n스케줄 생성을 종료 합니다.");
						e.printStackTrace();
						return RESULT_FAILE;
					}
					catch(NotSupportedDateTypeException e)
					{
						return RESULT_FAILE;
					}
				}
				process++;	
				current++;
			}
			printSchedule();
			printError();
			logger.info("web schedule create end");
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "웹용 스케줄 생성 완료");


			//ScheduleServiceManager.testPrint(process+"건 처리");
			if(processMessageDialog!=null){
				processMessageDialog.setVisible(false);
				processMessageDialog.dispose();
			}
			logger.debug("<==build schedule end==>");
			return RESULT_SUCCESS;

		} catch (SQLException e) {
			processMessageDialog.setVisible(false);
			processMessageDialog.dispose();
			e.printStackTrace();
			return RESULT_FAILE;
		}catch (Exception e) {
			e.printStackTrace();
			processMessageDialog.setVisible(false);
			processMessageDialog.dispose();
			JOptionPane.showMessageDialog(null, "error:"+e.getMessage());
			return RESULT_FAILE;
		}// 전체

	}
	private void printSchedule() throws IOException {
		logger.debug("scheduleDataList :"+scheduleDataList.size());

		scheduleID = 1;

		Object[] sortDate= totalScheduleList.toArray();
		Arrays.sort(sortDate);

		for(int i=0;i<sortDate.length;i++)
		{
			ScheduleData data =(ScheduleData) sortDate[i];
			String key=data.getFromAreaCode()+data.getArea_code();
			if(map.containsKey(key))
			{
				data.setScheduleID(scheduleID);
				Vector d=map.get(key);
				d.add(data);
			}else
			{
				//logger.error("no have key:"+key+","+scheduledata.getTable_id()+","+scheduledata.getArea_name());
			}

		}

		Set<String> v=map.keySet();
		Iterator iter2=v.iterator();
		while(iter2.hasNext())
		{
			String key = (String) iter2.next();
			logger.info(key);
			File fo = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION));
			if(!fo.exists())
			{
				fo.mkdir();
			}

			File file = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+"WW_SYBASE"+key.replace("0", ""));
			if(file.exists())
			{
				if(file.delete())
				{
					logger.info("이전 파일 삭제");
				}
			}
			FileWriter writer = new FileWriter(file, true);

			Vector<ScheduleData> vesselList = map.get(key);
			logger.debug("key :"+key+",size:"+vesselList.size());
			for(int i=0;i<vesselList.size();i++)
			{
				ScheduleData data = vesselList.get(i);
				String dateF =data.getDateF();
				String dateT =data.getDateT();
				String dateFback = data.getDateFBack();
				String dateTback = data.getDateTBack();
				String port = data.getPort();
				String FromPort = data.getFromPort();

				if(!biggerDate(dateF, dateT))

				{
					data.setDateF(dateT);
					data.setDateFBack(dateTback);
					data.setDateT(dateF);
					data.setDateTBack(dateFback);
					data.setFromPort(port);
					data.setPort(FromPort);
				}
				logger.debug(data.getTable_id()+"\t"+data.toWebSchedule()+"\n");
				writer.write(scheduleID+"\t"+data.toWebSchedule()+"\n");


				scheduleID++;
			}
			writer.flush();
			writer.close();
			scheduleDataList.clear();
			totalScheduleList.clear();

		}
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
	private int scheduleID;
	public int getLengthOfTask() {
		return lengthOfTask;
	}

	/**
	 */
	public int getCurrent() {
		return current;
	}

	public void stop() {
		canceled = true;
		statMessage = null;
	}

	/**
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * Returns the most recent status message, or null
	 * if there is no current status message.
	 */
	public String getMessage() {
		return statMessage;
	}
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
	private PortInfo getAreaCode(String portName) throws SQLException {
		PortInfo  portInfo=getPortInfoByPortName(portName );
		if(portInfo!=null)
		{
			return portInfo;
		}else
		{
			PortInfo  portabbrInfo=getPortInfoAbbrByPortName(portName );
			if(portabbrInfo!=null)
			{
				return portabbrInfo;
			}else
			{
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


		int monthT = 0;
		int dayT = 0;
		int yearT = 0;
		int yearF = 0;
		yearF = Integer.valueOf(strYear);
		yearT = Integer.valueOf(strYear);
		if(dateF.matches(datePattern))
		{
			Pattern patt = Pattern.compile(datePattern);
			Matcher matcher = patt.matcher(dateF);
			matcher.lookingAt();

			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));		
			monthT=monthF;
			dayT=dayF;	




		}else if(dateF.matches(datePattern1))
		{
			Pattern patt = Pattern.compile(datePattern1);
			Matcher matcher = patt.matcher(dateF);			
			matcher.lookingAt();
			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));

			monthT=Integer.valueOf(matcher.group(3));
			dayT=Integer.valueOf(matcher.group(4));
		}
		else if(dateF.matches(datePattern2))
		{
			Pattern patt = Pattern.compile(datePattern2);
			Matcher matcher = patt.matcher(dateF);			
			matcher.lookingAt();



			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));
			monthT=Integer.valueOf(matcher.group(1));
			dayT=Integer.valueOf(matcher.group(3));

		}



		if(Math.abs(currentMonth-monthF)>=6)
		{
			if(currentMonth<monthF)
				yearF--;
			if(currentMonth>monthF)
				yearF++;
		}
		if(Math.abs(currentMonth-monthT)>=6)
		{
			if(currentMonth<monthT)
				yearT--;
			if(currentMonth>monthT)
				yearT++;
		}

		String dd[] = new String[2]; 


		{
			dd[0] = yearF+"/"+monthF+"/"+dayF;
			dd[1] = yearT+"/"+monthT+"/"+dayT;
		}
		return dd;

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
				
				// 확인 필요
				//tablePort.setPort_index(table.getDirection());
				tablePort.setTable_id(table.getTable_id());
				TablePort info;
				info = (TablePort) tableService.getTablePort(tablePort);
				scheduledata.setTs(info.getPort_name());

				String date[][]=null;
				try {
					date = adv.getDataArray();
				} catch (OutOfMemoryError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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

			scheduledata.setCompany_abbr(table.getCompany_abbr());
			scheduledata.setVoyage_num(vslDatas[vslIndex][1]);
			logger.debug("voyage num:"+vslDatas[vslIndex][1]);
			scheduledata.setInOutType(InOutBoundType);
			scheduledata.setGubun(table.getGubun());

			scheduledata.setCommon_shipping("");
			scheduledata.setDate_issue(table.getDate_isusse());
			scheduledata.setTable_id(table.getTable_id());



			try{
				if(scheduledata.getPort()==null)
				{
					logger.error("port is null=>"+scheduledata.getTable_id());
					return;
				}

				PortInfo info=getAreaCode(scheduledata.getPort().trim());
				PortInfo frominfo=getAreaCode(scheduledata.getFromPort().trim());
				scheduledata.setFromAreaCode(frominfo.getArea_code());
				scheduledata.setArea_code(info.getArea_code());

			}catch(NullPointerException e)
			{
				//e.printStackTrace();
				ErrorLog errorLog = new ErrorLog();
				errorLog.setTableID("table_id:"+scheduledata.getTable_id()+"=>port:"+scheduledata.getPort()+",fromport:"+scheduledata.getFromPort());
				errorLog.setType("해당 항구 항목이 Null 값 을 가지거나 등록되지 않았음");
				errorlist.add(errorLog);
				logger.error("port is null=>"+scheduledata.getTable_id());

				return;

			}


			String key=scheduledata.getFromAreaCode()+scheduledata.getArea_code();
			totalScheduleList.add(scheduledata);


		}



	}
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

	private void printError() {
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<errorlist.size();i++)
		{
			buffer.append(errorlist.get(i).toString()+"\n");
		}
		xtgmanager.createXTGFile(buffer.toString(),"web_error.txt");
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

						}catch (ArrayIndexOutOfBoundsException e) 
						{

							logger.error("배열 인덱스 오류 :"+e.getMessage()+","+table.getTable_id()+", 페이지:"+table.getPage()+","+table.getTable_index());
							StringBuffer errorlogger = new StringBuffer();
							for(int error=0;error<portList.length;error++)
								errorlogger.append(portList[error]+",");
							logger.error(errorlogger.toString());

						}
					}
				}
			}	
		}

	}
	class ErrorLog
	{
		String type;
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		String id;
		String date;
		public void setTableID(String tableId) {
			this.id = tableId;

		}

		public void setDate(String date) {
			this.date = date;
		}

		public String toString()
		{
			return "["+type+":"+id+",date:"+date+"]";
		}
	}
	public void makeSchedule() throws SQLException, JDOMException, IOException,
			ParseException {
		
	}
	protected void initTag() {
		// TODO Auto-generated method stub
		
	}


}
