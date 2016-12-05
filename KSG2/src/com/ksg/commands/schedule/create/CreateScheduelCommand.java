package com.ksg.commands.schedule.create;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.LongTask;
import com.ksg.commands.schedule.ErrorLog;
import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.ADVService;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.ScheduleService;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ADVData;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Port;
import com.ksg.domain.Vessel;
import com.ksg.model.KSGModelManager;
import com.ksg.view.schedule.dialog.ScheduleBuildMessageDialog;
import com.ksg.view.util.KSGDateUtil;

public abstract class CreateScheduelCommand implements KSGCommand, LongTask{
	private TableService 		tableService;
	protected ADVService 		advService;
	protected ScheduleService 	scheduleService;
	protected BaseService 		baseService;
	protected Vector errorlist ;
	protected int lengthOfTask;
	protected int current = 0;
	protected boolean done = false;
	protected boolean canceled = false;
	protected String statMessage;
	protected int total;
	protected int result;
	protected ShippersTable searchOption;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	protected ScheduleBuildMessageDialog processMessageDialog;
	protected int process;
	protected long startTime,endtime;
	protected int currentMonth;
	protected List portList,portAbbrList;
	protected List NO_VESSEL;
	protected String currentYear;
	protected ADVData advData;
	protected Date selectedDate;
	public CreateScheduelCommand() throws SQLException {
		tableService 	= DAOManager.getInstance().createTableService();
		scheduleService = DAOManager.getInstance().createScheduleService();
		advService		= DAOManager.getInstance().createADVService();
		baseService 	= DAOManager.getInstance().createBaseService();
		errorlist= new Vector();
		process =0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM");

		currentYear = sdf.format(new Date());
		currentMonth = Integer.valueOf(sdf1.format(new Date()));

		portList = 		baseService.getPortInfoList();
		portAbbrList = 	baseService.getPort_AbbrList();

		Vessel op = new Vessel();
		op.setVessel_use(Vessel.NON_USE);

		NO_VESSEL = baseService.getVesselList(op);
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
	protected List getTableListByOption() throws SQLException
	{
		List li =tableService.getScheduleTableListByDate(searchOption);
		this.total =li.size();
		lengthOfTask=total;
		logger.info("스케줄 처리용 테이블 수 : "+total);
		return li;
	}
	protected void setTime(long time) {
		time = System.currentTimeMillis();
	}

	

	protected void setMessageDialog() {
		processMessageDialog = new ScheduleBuildMessageDialog();
		processMessageDialog.createAndUpdateUI();
		processMessageDialog.setTask(this);
	}
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
	protected List getTablePortList(Table_Port tablePort) throws SQLException
	{
		return tableService.getTablePortList(tablePort);
	}
	protected Table_Port getTablePort(Table_Port tablePort) throws SQLException
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
	protected String[] adjestDateYear(String dateF, String dateT, String inOutBoundType) throws NotSupportedDateTypeException{
		logger.debug("dateF:"+dateF+", dateT:"+dateT+", in/out:"+inOutBoundType);
		
		dateT = dateT.replace(" ", "");
		
		dateF = dateF.replace(" ", "");
		
		int monthAndDayF[]=getETD(dateF);
		int monthAndDayT[]=getETA(dateT);

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
	
	protected String[] adjestDateYear2(String dateF, String dateT, String inOutBoundType) throws NotSupportedDateTypeException{
		logger.debug("dateF:"+dateF+", dateT:"+dateT+", in/out:"+inOutBoundType);
		
		dateT = dateT.replace(" ", "");
		
		dateF = dateF.replace(" ", "");
		
		int monthAndDayF[]=getETA(dateF);
		int monthAndDayT[]=getETD(dateT);

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
			if(!date.equals("-"))
			{
				logger.error(date);	
			}			
			
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
	 * 문자형 항차번호 중 숫자만 반환
	 * @param voyage_num
	 * @return
	 */
	protected int getNumericVoyage(String voyage_num)
	{
		int result=0;

		String temp="";
		if(voyage_num==null)
			return 0;
		for(int i=0;i<voyage_num.length();i++)
		{
			try{
				temp+=Integer.parseInt(String.valueOf(voyage_num.charAt(i)));
			}catch(NumberFormatException e)
			{

			}
		}
		try{
			result=Integer.valueOf(temp);
		}catch(Exception e)
		{
			return 0;
		}

		return result;
	}
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
	protected void notifyMessage(String message)
	{
		KSGModelManager.getInstance().workProcessText=message;
		KSGModelManager.getInstance().execute(processMessageDialog.getName());
	}

}
