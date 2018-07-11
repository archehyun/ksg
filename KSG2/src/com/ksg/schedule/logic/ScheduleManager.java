package com.ksg.schedule.logic;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ksg.common.util.KSGPropertis;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.joint.ConsoleScheduleJoint;
import com.ksg.schedule.logic.joint.DefaultScheduleJoint;
import com.ksg.schedule.logic.joint.InboundScheduleJoint;
import com.ksg.schedule.logic.joint.OutboundScheduleJointV2;
import com.ksg.schedule.logic.joint.RouteScheduleJoint;
import com.ksg.schedule.view.dialog.ScheduleBuildMessageDialog;

/**
 * @author archehyun
 *
 */
public class ScheduleManager {

	private static ScheduleManager instance;

	private ArrayList<ScheduleJoint> scheduleBuilds;

	private ScheduleBuildMessageDialog di;

	protected Logger 			logger = Logger.getLogger(getClass());

	protected BaseService baseService;

	protected ArrayList<PortInfo> allPortlist;

	protected ArrayList<PortInfo> allPortAbbrlist;

	protected ArrayList<Vessel> allVessellist;

	private ScheduleManager() {
		scheduleBuilds = new ArrayList<ScheduleJoint>();
		baseService = new BaseServiceImpl();
	}
	public void init()
	{
		try {
			allPortlist = (ArrayList<PortInfo>) baseService.getPortInfoList();
			allPortAbbrlist = (ArrayList<PortInfo>) baseService.getPort_AbbrList();
			allVessellist = (ArrayList<Vessel>) baseService.getVesselList(new Vessel());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public static ScheduleManager getInstance()
	{
		if(instance == null)
			instance = new ScheduleManager();
		return instance;
	}
	/**
	 * @���� �ƿ��ٿ�� ������ ����
	 * @return
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public ScheduleJoint getOutboundSchedule() throws SQLException, IOException
	{
		return new OutboundScheduleJointV2();
	}
	/**
	 * @���� �ιٿ�� ������ ����
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleJoint getInboundSchedule() throws SQLException
	{
		return new InboundScheduleJoint();
	}
	/**
	 * @���� �׷κ� ������ ����
	 * @param op
	 * @param orderType
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleJoint getRouteSchedule(ShippersTable op,int orderType) throws SQLException
	{
		return new RouteScheduleJoint(op,orderType);
	}
	/**
	 * @���� �ܼ� ������ ����
	 * @param op
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleJoint getConsoleSchedudle(ScheduleData op) throws SQLException
	{
		return new ConsoleScheduleJoint(op);
	}
	/**
	 * @���� �η��� ������ ����
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleJoint getInlandSchedule() throws SQLException
	{
		return new InboundScheduleJoint();
	}

	public PortInfo searchPort(String portName) throws SQLException,PortNullException
	{
		Iterator<PortInfo> iterator = allPortlist.iterator();
		while(iterator.hasNext())
		{
			PortInfo info = iterator.next();
			if(info.getPort_name().equals(portName))
				return info;

		}
		throw new PortNullException(portName);
	}
	public PortInfo searchPortAbbr(String portAbbrName) throws SQLException, PortNullException
	{
		Iterator<PortInfo> iterator = allPortAbbrlist.iterator();
		while(iterator.hasNext())
		{
			PortInfo info = iterator.next();
			if(info.getPort_abbr().equals(portAbbrName))
				return info;

		}

		throw new PortNullException(portAbbrName);
	}
	/**
	 * @param vesselName
	 * @return
	 * @throws SQLException
	 * @throws VesselNullException
	 */
	public Vessel searchVessel(String vesselName) throws SQLException,VesselNullException {		

		Iterator<Vessel> iterator = allVessellist.iterator();
		while(iterator.hasNext())
		{
			Vessel info=iterator.next();
			if(info.getVessel_name().toUpperCase().equals(vesselName.toUpperCase()))
			{
				if(info.getVessel_name().toUpperCase().equals(info.getVessel_abbr().toUpperCase()))
				{
					return info;
				}
			}
		}

		throw new VesselNullException("vesselName null:"+vesselName);
	}


	public void startBuild()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				logger.info("������ ���� ����");

				String fileLocation =KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION);

				File file = new File(fileLocation);

				if(!file.exists())
				{
					file.mkdirs();
					logger.info("���� ����:"+fileLocation);
				}


				di = new ScheduleBuildMessageDialog();
				
				di.createAndUpdateUI();
				
				Iterator<ScheduleJoint>iter = scheduleBuilds.iterator();
				init();
				try {
					while(iter.hasNext())
					{
						DefaultScheduleJoint build = (DefaultScheduleJoint) iter.next();
						di.setTask(build);
						build.initTag();
						build.execute();
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();	
				}
				scheduleBuilds.clear();
				di.end();

			}
		};
		thread.start();
	}
	public void addBulid(ScheduleJoint build)
	{
		scheduleBuilds.add(build);
	}


}
