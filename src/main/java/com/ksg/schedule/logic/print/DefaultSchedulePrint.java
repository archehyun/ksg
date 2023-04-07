package com.ksg.schedule.logic.print;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.commands.schedule.XML_INFO;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.SchedulePrint;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.service.ADVService;
import com.ksg.service.BaseService;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.TableService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

/**
 * @author archehyun
 *
 */
public abstract class DefaultSchedulePrint implements SchedulePrint{
	
	protected ScheduleData data;
	
	public ScheduleManager scheduleManager = ScheduleManager.getInstance();

	public static final String BUSAN = "BUSAN";

	public static final String BUSAN_NEW_PORT = "BUSAN NEW";

	protected ScheduleData option; // 콘솔 스케줄 조회 옵션 저장

	protected SimpleDateFormat dateIssueformat 	= new SimpleDateFormat("yyyy-MM-dd");

	protected SimpleDateFormat inputDateFormat 	= KSGDateUtil.inputDateFormat;

	protected SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();

	protected String fileName, portFileName;
	
	protected String fileLocation;

	protected boolean done = false;

	protected ScheduleBuildMessageDialog processDialog;

	protected BaseService baseService;

	protected ScheduleSubService scheduleService;

	protected TableService tableService;

	protected ADVService advService;

	protected Logger logger = LogManager.getLogger(this.getClass());

	protected Thread thread;

	protected int lengthOfTask;

	protected int current;

	protected String message="";

	protected ArrayList<PortInfo> allPortlist;

	protected ArrayList<PortInfo> allPortAbbrlist;

	protected ArrayList<Vessel> allVessellist;

	protected String[] fromPort;

	/**
	 * @throws SQLException
	 */
	public DefaultSchedulePrint() throws SQLException {

		baseService 	= DAOManager.getInstance().createBaseService();

		scheduleService	= new ScheduleServiceImpl();

		Code param = new Code();

		param.setCode_type(XML_INFO.XML_TAG_FROM_PORT);

		List<Code> li = baseService.getCodeInfoList(param);

		fromPort = new String[li.size()];

		for(int i=0;i<li.size();i++)
		{
			Code info = li.get(i);
			fromPort[i] =info.getCode_name();
		}
		
		fileLocation=KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION);


	}

	@Override
	public int getLengthOfTask() {
		return lengthOfTask;
	}
	@Override
	public int getCurrent() {
		return current;
	}
	@Override
	public boolean isDone() {
		return done;
	}
	@Override
	public void stop() {

	}

	@Override
	public String getMessage() {
		return message;
	}
	
	public abstract void initTag();

	public void setDone(boolean b) {
		this.done =b;
		
	}



}
