package com.ksg.schedule.logic.joint;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ksg.adv.service.ADVService;
import com.ksg.commands.schedule.XML_INFO;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.ScheduleService;
import com.ksg.schedule.logic.ScheduleJoint;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.view.dialog.ScheduleBuildMessageDialog;
import com.ksg.shippertable.service.TableService;

/**
 * @author archehyun
 *
 */
public abstract class DefaultScheduleJoint implements ScheduleJoint{
	
	public ScheduleManager scheduleManager = ScheduleManager.getInstance();

	public static final String BUSAN = "BUSAN";

	public static final String BUSAN_NEW_PORT = "BUSAN NEW";

	protected ScheduleData option; // 콘솔 스케줄 조회 옵션 저장

	protected SimpleDateFormat dateIssueformat 	= new SimpleDateFormat("yyyy-MM-dd");

	protected SimpleDateFormat inputDateFormat 	= KSGDateUtil.createInputDateFormat();

	protected SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();

	protected String fileName;

	protected boolean done = false;

	protected ScheduleBuildMessageDialog processDialog;

	protected BaseService baseService;

	protected ScheduleService scheduleService;

	protected TableService tableService;

	protected ADVService advService;

	protected Logger 			logger = Logger.getLogger(getClass());

	protected Thread thread;

	protected int lengthOfTask;

	protected int current;

	protected String message="";

	protected ArrayList<PortInfo> allPortlist;

	protected ArrayList<PortInfo> allPortAbbrlist;

	protected ArrayList<Vessel> allVessellist;

	protected String[] fromPort;

	public DefaultScheduleJoint() throws SQLException {

		baseService 	= DAOManager.getInstance().createBaseService();

		scheduleService	= DAOManager.getInstance().createScheduleService();

		Code param = new Code();

		param.setCode_type(XML_INFO.XML_TAG_FROM_PORT);

		List<Code> li = baseService.getCodeInfoList(param);

		fromPort = new String[li.size()];

		for(int i=0;i<li.size();i++)
		{
			Code info = li.get(i);
			fromPort[i] =info.getCode_name();
		}


	}

	@Override
	public int getLengthOfTask() {
		// TODO Auto-generated method stub
		return lengthOfTask;
	}
	@Override
	public int getCurrent() {
		// TODO Auto-generated method stub
		return current;
	}
	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return done;
	}
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}
	
	public abstract void initTag();



}
