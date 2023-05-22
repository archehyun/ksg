package com.dtp.api.schedule.joint.print;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.commands.ScheduleExecute;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
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
public abstract class AbstractSchedulePrint implements ScheduleExecute{

	protected FileWriter fw;
	
	protected FileWriter errorOutfw,commonInfw;
	
	protected FileWriter errorfw;

	protected FileWriter portfw;

	protected ScheduleData data;
	
	protected PrintAble print;

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

	protected ScheduleSubService scheduleService	= new ScheduleServiceImpl();

	protected TableService tableService;

	protected ADVService advService;

	protected Logger logger = LogManager.getLogger(this.getClass());

	protected Thread thread;

	protected int lengthOfTask=0;

	protected int current;

	protected String message="";

	protected ArrayList<PortInfo> allPortlist;

	protected ArrayList<PortInfo> allPortAbbrlist;

	protected ArrayList<Vessel> allVessellist;
	
	protected BaseService baseService;

	protected Map<String, PortInfo> portMap;

	protected Map<String, Vessel> vesselMap;

	protected List<ScheduleData> scheduleList;

	/**
	 * @throws SQLException
	 */
	public AbstractSchedulePrint() {
		
		fileLocation=KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION);
		
		baseService 	= DAOManager.getInstance().createBaseService();

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

	public void setDone(boolean b) {
		this.done =b;

	}
	
	public void close() throws IOException
	{
		if(fw!=null) fw.close();
		if(errorOutfw!=null)errorOutfw.close();
		if(commonInfw!=null)commonInfw.close();
		if(portfw!=null)portfw.close();
	}
	

	public abstract void init() throws Exception;
	
	public abstract void writeFile(ArrayList<String> printList) throws Exception;
	





}
