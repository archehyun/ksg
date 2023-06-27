package com.dtp.api.schedule.create;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dtp.api.service.DTPScheduleService;
import com.dtp.api.service.ShipperTableService;
import com.dtp.api.service.impl.DTPScheduleServiceImpl;
import com.dtp.api.service.impl.ShipperTableServiceImpl;
import com.ksg.commands.IFCommand;
import com.ksg.commands.ScheduleExecute;
import com.ksg.commands.schedule.ErrorLog;
import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.service.ADVService;
import com.ksg.service.BaseService;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.TableService;
import com.ksg.service.VesselService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 박창현
 *
 */
@Slf4j
public abstract class CreateScheduleCommand implements IFCommand, ScheduleExecute{


	public static final int TYPE_INBOUND=1;

	public static final int TYPE_OUTBOUND=2;
	private ShipperTableService shipperTableService;
	private TableService 		tableService;
	protected ADVService 		advService;
	protected ScheduleSubService 	scheduleService;
	protected BaseService 		baseService;
	private DTPScheduleService dtpScheduleService;
	private VesselService vesselService = new VesselServiceImpl();
	
	protected Vector errorlist ;
	protected int lengthOfTask;
	protected int current = 0;
	protected boolean done = false;
	protected boolean canceled = false;
	protected String statMessage;
	protected int total;
	protected int result;
	protected ShippersTable searchOption;
	protected ScheduleBuildMessageDialog processMessageDialog;
	protected int process;
	protected long startTime,endtime;
	protected int currentMonth;
	protected List portList,portAbbrList;
	protected List NO_VESSEL;
	protected String currentYear;
	protected ADVData advData;
	protected Date selectedDate;
	public CreateScheduleCommand() throws SQLException {
		
		shipperTableService = new ShipperTableServiceImpl();
		
		dtpScheduleService 	= new DTPScheduleServiceImpl();
		
		tableService 		= new TableServiceImpl();
		
		scheduleService 	= new ScheduleServiceImpl();
		
		advService			= new ADVServiceImpl();
		
		baseService 		= new BaseServiceImpl();

		errorlist= new Vector();

		process =0;
		SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");

		currentYear = sdfYear.format(new Date());
		currentMonth = Integer.valueOf(sdfMonth.format(new Date()));

		portList = 		baseService.getPortInfoList();
		portAbbrList = 	baseService.getPort_AbbrList();		
		
		HashMap<String, Object> vesselParam = new HashMap<String, Object>();
		
		vesselParam.put("vessel_use", Vessel.NON_USE);
		
	}
	public int getLengthOfTask() {
		return lengthOfTask;
	}

	/**
	 * Called from ProgressBarDemo to find out how much has been done.
	 */
	public int getCurrent() {
		return current;
	}

	public void stop() {
		canceled = true;
		statMessage = null;
	}

	/**
	 * Called from ProgressBarDemo to find out if the task has completed.
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
	
	/**
	 * 테이블 리스트 조회
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected List selectShipperTableListAllByCondition() throws SQLException
	{
		List li =shipperTableService.selectShipperTableListAllByCondition(searchOption);

		this.total =li.size();

		lengthOfTask=total;

		log.info("스케줄 처리용 테이블 수 : "+total);

		return li;
	}
	
	/**
	 * 테이블 리스트 조회
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected List getTableListByOption() throws SQLException
	{
		List li =tableService.getScheduleTableListByDate(searchOption);

		this.total =li.size();

		lengthOfTask=total;

		log.info("스케줄 처리용 테이블 수 : "+total);

		return li;
	}
	protected void setTime(long time) {
		time = System.currentTimeMillis();
	}



	/**
	 * 
	 */
	protected void setMessageDialog() {
		processMessageDialog = new ScheduleBuildMessageDialog();
		processMessageDialog.createAndUpdateUI();
		processMessageDialog.setTask(this);
	}
	
	
	/**
	 * 테이블 데이터 테스트
	 * @param tableData
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	protected boolean isTableDataValidation(ShippersTable tableData) throws SQLException, ParseException
	{
		// 날짜 지정 향후 DB 쿼리로 대체
		if(KSGDateUtil.daysDiffABS(KSGDateUtil.toDate2(tableData.getDate_isusse()), KSGDateUtil.toDate2(selectedDate))!=0)
			return true;

		advData = (ADVData) advService.getADVData(tableData.getTable_id());

		// 입력된 광고 정보가 없으며 통과
		if(advData==null||advData.getData()==null)
		{
			return true;
		}
		return false;
	}
	/**
	 * @param tablePort
	 * @return
	 * @throws SQLException
	 */
	protected List getTablePortList(TablePort tablePort) throws SQLException
	{
		return tableService.getTablePortList(tablePort);
	}
	/**
	 * @param tablePort
	 * @return
	 * @throws SQLException
	 */
	protected TablePort getTablePort(TablePort tablePort) throws SQLException
	{
		return tableService.getTablePort(tablePort);
	}
	/** TS 인지 확인
	 * @param tableData
	 * @return
	 */
	protected boolean isTS(ShippersTable tableData)
	{
		return tableData.getTS()!=null&&tableData.getTS().equals("TS");
	}
	
	
	private String datePattern = "(\\d{1,2})/(\\d{1,2})";
	private String datePattern1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})";
	private String datePattern1_1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})\\w+";
	private String datePattern1_2 = "(\\d{1,2}).(\\d{1,2})/(\\d{1,2}).(\\d{1,2})";
	private String datePattern2 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})";
	private String datePattern2_1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})\\w+";
	private int yearF;
	private int yearT;
	
	
	
	/**
	 * @param dateF
	 * @param dateT
	 * @param inOutBoundType
	 * @return
	 * @throws NotSupportedDateTypeException
	 */
	protected String[] adjestDateYear(String dateF, String dateT, String inOutBoundType) throws NotSupportedDateTypeException{

		
		int monthAndDayF[]=getETD(dateF.replace(" ", ""));
		
		int monthAndDayT[]=getETA(dateT.replace(" ", ""));

		// 날짜 유효성 체크 2020-01-28
		if(monthAndDayF[0]==0||monthAndDayF[1]==0)
			throw new NotSupportedDateTypeException(dateF, 0);
		if(monthAndDayT[0]==0||monthAndDayT[1]==0)
			throw new NotSupportedDateTypeException(dateT, 0);


		//수정 : 2011-12-08
		/*
		전년도	당해년도	 차년도
		10,11,12	1,2,3,4,5,6,7,8,9	-
		11,12	1,2,3,4,5,6,7,8,9,10	-
		12	1,2,3,4,5,6,7,8,9,10,11	-
		-	1,2,3,4,5,6,7,8,9,10,11,12	-
		-	2,3,4,5,6,7,8,9,10,11,12	1
		-	3,4,5,6,7,8,9,10,11,12	1,2
		-	4,5,6,7,8,9,10,11,12	1,2,3
		-	5,6,7,8,9,10,11,12	1,2,3,4
		-	6,7,8,9,10,11,12	1,2,3,4,5
		-	7,8,9,10,11,12	1,2,3,4,5,6
		-	8,9,10,11,12	1,2,3,4,5,6,7
		-	9,10,11,12	1,2,3,4,5,6,7,8

		 */

		yearF = selectYear(currentMonth, monthAndDayF[0], Integer.valueOf(currentYear));
		
		yearT = selectYear(currentMonth, monthAndDayT[0], Integer.valueOf(currentYear));

		String newDate[] = new String[2];

		newDate[0] = yearF+"/"+monthAndDayF[0]+"/"+monthAndDayF[1];
		
		newDate[1] = yearT+"/"+monthAndDayT[0]+"/"+monthAndDayT[1];

		return newDate;
	}

	protected String[] adjestDateYear2(String dateF, String dateT, String inOutBoundType) throws NotSupportedDateTypeException{
		log.debug("dateF:"+dateF+", dateT:"+dateT+", in/out:"+inOutBoundType);

		dateT = dateT.replace(" ", "");

		dateF = dateF.replace(" ", "");

		int monthAndDayF[]=getETA(dateF);
		int monthAndDayT[]=getETD(dateT);


		// 날짜 유효성 체크 2020-01-28
		if(monthAndDayF[0]==0||monthAndDayF[1]==0)
			throw new NotSupportedDateTypeException(dateF, 0);
		if(monthAndDayT[0]==0||monthAndDayT[1]==0)
			throw new NotSupportedDateTypeException(dateT, 0);

		//수정 : 2011-12-08
		/*
		전년도	당해년도	 차년도
		10,11,12	1,2,3,4,5,6,7,8,9	-
		11,12	1,2,3,4,5,6,7,8,9,10	-
		12	1,2,3,4,5,6,7,8,9,10,11	-
		-	1,2,3,4,5,6,7,8,9,10,11,12	-
		-	2,3,4,5,6,7,8,9,10,11,12	1
		-	3,4,5,6,7,8,9,10,11,12	1,2
		-	4,5,6,7,8,9,10,11,12	1,2,3
		-	5,6,7,8,9,10,11,12	1,2,3,4
		-	6,7,8,9,10,11,12	1,2,3,4,5
		-	7,8,9,10,11,12	1,2,3,4,5,6
		-	8,9,10,11,12	1,2,3,4,5,6,7
		-	9,10,11,12	1,2,3,4,5,6,7,8

		 */

		yearF =selectYear(currentMonth, monthAndDayF[0], Integer.valueOf(currentYear));
		yearT =selectYear(currentMonth, monthAndDayT[0], Integer.valueOf(currentYear));


		String dd[] = new String[2];

		dd[0] = yearF+"/"+monthAndDayF[0]+"/"+monthAndDayF[1];
		dd[1] = yearT+"/"+monthAndDayT[0]+"/"+monthAndDayT[1];


		return dd;
	}


	/**
	 * @see 뒷날짜 선택
	 * @param date
	 * @return
	 * @throws NotSupportedDateTypeException
	 */
	protected int[] getETD(String date)
			throws NotSupportedDateTypeException {
		yearF = Integer.valueOf(currentYear);
		int monthAndDay[] = new int[2];


		//"(\\d{1,2})/(\\d{1,2})";
		if(date.matches(datePattern))
		{
			Pattern patt = Pattern.compile(datePattern);
			Matcher matcher = patt.matcher(date);
			matcher.lookingAt();

			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
			/*monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));*/		


		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})";
		else if(date.matches(datePattern1))
		{
			Pattern patt = Pattern.compile(datePattern1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();


			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(3));
			monthAndDay[1]=Integer.valueOf(matcher.group(4));


		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})\\w+";
		else if(date.matches(datePattern1_1))
		{
			Pattern patt = Pattern.compile(datePattern1_1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(3));
			monthAndDay[1]=Integer.valueOf(matcher.group(4));
		}
		//"(\\d{1,2}).(\\d{1,2})/(\\d{1,2}).(\\d{1,2})";
		else if(date.matches(datePattern1_2))
		{
			Pattern patt = Pattern.compile(datePattern1_2);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();


			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(3));
			monthAndDay[1]=Integer.valueOf(matcher.group(4));
		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})";
		else if(date.matches(datePattern2))
		{
			Pattern patt = Pattern.compile(datePattern2);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(3));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(3));

		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})\\w+";
		else if(date.matches(datePattern2_1))
		{
			Pattern patt = Pattern.compile(datePattern2_1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(3));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(3));
		}else
		{
			/*if(!date.equals("-"))
			{
				logger.error(date);	
			}		*/	

			throw new NotSupportedDateTypeException(date, 0);
		}
		return monthAndDay;
	}

	/**
	 * @see 앞날짜 선택
	 * @param date
	 * @return
	 * @throws NotSupportedDateTypeException
	 */
	protected int[] getETA(String date)
			throws NotSupportedDateTypeException {
		yearF = Integer.valueOf(currentYear);
		int monthAndDay[] = new int[2];


		//"(\\d{1,2})/(\\d{1,2})";
		if(date.matches(datePattern))
		{
			Pattern patt = Pattern.compile(datePattern);
			Matcher matcher = patt.matcher(date);
			matcher.lookingAt();

			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
			/*monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));*/		


		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})";
		else if(date.matches(datePattern1))
		{
			Pattern patt = Pattern.compile(datePattern1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();


			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));


		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})\\w+";
		else if(date.matches(datePattern1_1))
		{
			Pattern patt = Pattern.compile(datePattern1_1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
		}
		//"(\\d{1,2}).(\\d{1,2})/(\\d{1,2}).(\\d{1,2})";
		else if(date.matches(datePattern1_2))
		{
			Pattern patt = Pattern.compile(datePattern1_2);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();


			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})";
		else if(date.matches(datePattern2))
		{
			Pattern patt = Pattern.compile(datePattern2);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(3));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));

		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})\\w+";
		else if(date.matches(datePattern2_1))
		{
			Pattern patt = Pattern.compile(datePattern2_1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(3));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
		}else
		{			
			throw new NotSupportedDateTypeException(date, 0);
		}
		return monthAndDay;
	}

	/**
	 * @param currentMonth
	 * @param targetMonth
	 * @param currentYear
	 * @return
	 */
	protected int selectYear(int currentMonth, int targetMonth,int currentYear )
	{
		int gapMonth = currentMonth -targetMonth;

		if(currentMonth<4)
		{
			if(Math.abs(gapMonth)>=9)
			{
				return --currentYear;
			}else
			{
				return currentYear;
			}

		}else if(currentMonth==4)
		{
			return currentYear;
		}
		else
		{
			if(gapMonth<=3)
			{
				return currentYear;
			}else
			{
				return ++currentYear;
			}
		}
	}
	
	/**
	 * @param type
	 * @param tableID
	 * @param content
	 * @return
	 */
	protected ErrorLog createError(String type, String tableID,String content)
	{
		ErrorLog log = new ErrorLog();
		SimpleDateFormat formatter; 
		String pattern = "yyyy년 M월 d일 a h시 m분"; 
		formatter = new SimpleDateFormat(pattern, new Locale("ko","KOREA")); 
		log.setCurrentTime(formatter.format(new Date()));
		log.setType(type);
		log.setTableID(tableID);
		log.setDate(content);
		return log;
	}
	/**
	 * @param table
	 * @param scheduledata
	 */
	protected void checkDate(ShippersTable table, ScheduleData scheduledata) {
		String[] errorcontent={"0/0/0","TBN","SKIP","OMIT","-"};

		for(int i=0;i<errorcontent.length;i++)
		{
			if(scheduledata.getDateF()==null)
			{
				errorlist.add(createError(ErrorLog.DATE_ERROR_F_NULL, table.getTable_id()+":"+scheduledata.getVessel()+":"+scheduledata.getFromPort()+":"+scheduledata.getVoyage_num(), scheduledata.getDateF()));
				return;
			}
			if(scheduledata.getDateF().equals(errorcontent[i]))
			{
				errorlist.add(createError(ErrorLog.DATE_ERROR_F+errorcontent[i], table.getTable_id()+":"+scheduledata.getVessel()+":"+scheduledata.getFromPort()+":"+scheduledata.getVoyage_num(), scheduledata.getDateF()));
				return;
			}
			if(scheduledata.getDateT()==null)
			{
				errorlist.add(createError("날짜오류T Null", table.getTable_id()+":"+scheduledata.getVessel()+":"+scheduledata.getPort()+":"+scheduledata.getVoyage_num(), scheduledata.getDateT()));
				return;
			}
			if(scheduledata.getDateT().equals(errorcontent[i]))
			{
				errorlist.add(createError("날짜오류T "+errorcontent[i], table.getTable_id()+":"+scheduledata.getVessel()+":"+scheduledata.getPort()+":"+scheduledata.getVoyage_num(), scheduledata.getDateT()));
				return;
			}
		}
	}
	/**
	 * @param message
	 */
	protected void notifyMessage(String message)
	{
		KSGModelManager.getInstance().workProcessText=message;
		KSGModelManager.getInstance().execute(processMessageDialog.getName());
	}

}
