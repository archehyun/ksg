package com.ksg.schedule.build;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.console.ConsoleScheduleBuild;
import com.ksg.schedule.inbound.InboundScheduleBuild;
import com.ksg.schedule.outbound.OutboundScheduleBuildV2;
import com.ksg.schedule.route.RouteScheduleBuild;
import com.ksg.view.schedule.dialog.ScheduleBuildMessageDialog;

/**
 * @author archehyun
 *
 */
public class ScheduleManager {
	
	private static ScheduleManager instance;
	
	private ArrayList<ScheduleBuild> scheduleBuilds;
	
	private ScheduleBuildMessageDialog di;
	
	protected Logger 			logger = Logger.getLogger(getClass());
	
	protected BaseService baseService;
	
	protected ArrayList<PortInfo> allPortlist;
	
	protected ArrayList<PortInfo> allPortAbbrlist;
	
	protected ArrayList<Vessel> allVessellist;
	
	private ScheduleManager() {
		scheduleBuilds = new ArrayList<ScheduleBuild>();
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
	 * @설명 아웃바운드 스케줄 생성
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleBuild getOutboundSchedule() throws SQLException
	{
		return new OutboundScheduleBuildV2();
	}
	/**
	 * @설명 인바운드 스케줄 생성
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleBuild getInboundSchedule() throws SQLException
	{
		return new InboundScheduleBuild();
	}
	/**
	 * @설명 항로별 스케줄 생성
	 * @param op
	 * @param orderType
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleBuild getRouteSchedule(ShippersTable op,int orderType) throws SQLException
	{
		return new RouteScheduleBuild(op,orderType);
	}
	/**
	 * @설명 콘솔 스케줄 생성
	 * @param op
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleBuild getConsoleSchedudle(ScheduleData op) throws SQLException
	{
		return new ConsoleScheduleBuild(op);
	}
	/**
	 * @설명 인랜드 스케줄 생성
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleBuild getInlandSchedule() throws SQLException
	{
		return new InboundScheduleBuild();
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
				logger.info("스케줄 생성 시작");
				di = new ScheduleBuildMessageDialog();
				di.createAndUpdateUI();
				Iterator<ScheduleBuild>iter = scheduleBuilds.iterator();
				while(iter.hasNext())
				{
					ScheduleBuild build = iter.next();
					di.setTask(build);
					build.execute();
				}
				scheduleBuilds.clear();
				di.end();

			}
		};
		thread.start();
	}
	public void addBulid(ScheduleBuild build)
	{
		scheduleBuilds.add(build);
	}


}
