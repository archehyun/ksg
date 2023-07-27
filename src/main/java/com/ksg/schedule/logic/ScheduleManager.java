package com.ksg.schedule.logic;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.dtp.api.schedule.joint.print.AbstractSchedulePrint;
import com.ksg.commands.ScheduleExecute;
import com.ksg.common.exception.PortNullException;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.exception.VesselNullException;
import com.ksg.common.util.KSGPropertis;
import com.ksg.dao.CompanyDAO;
import com.ksg.dao.VesselDAO;
import com.ksg.dao.impl.CompanyDAOImpl;
import com.ksg.dao.impl.VesselDAOImpl;
import com.ksg.domain.Company;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.ConsoleScheduleJoint;
import com.ksg.schedule.logic.print.InboundScheduleJoint;
import com.ksg.schedule.logic.print.OutboundSchedulePrintV2;
import com.ksg.service.BaseService;
import com.ksg.service.VesselService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 스케줄 생성 매니저
 * 
 * 
 * @author archehyun
 *
 */
@Slf4j
public class ScheduleManager {

	private static ScheduleManager instance;

	private ArrayList<ScheduleExecute> scheduleBuilds;

	private ScheduleBuildMessageDialog di;

	protected BaseService baseService;
	
	protected VesselService vesselService;

	protected ArrayList<PortInfo> allPortlist;

	protected ArrayList<PortInfo> allPortAbbrlist;

	protected ArrayList<Vessel> allVessellist;

	private ArrayList<Company> allCompanyList;
	
	private VesselDAO vesselDAO;
	
	private CompanyDAO companyDAO;

	private ScheduleManager() {
		scheduleBuilds = new ArrayList<ScheduleExecute>();
		
		baseService = new BaseServiceImpl();
		
		vesselDAO = new VesselDAOImpl();
		
		companyDAO = new CompanyDAOImpl();
		
	}
	public void initMasterData()
	{
		try {
			log.info("스케줄 생성 마스터 초기화");
			
			allPortlist 	= (ArrayList<PortInfo>) baseService.getPortInfoList();
			
			allPortAbbrlist = (ArrayList<PortInfo>) baseService.getPort_AbbrList();
			
			allVessellist 	 = (ArrayList<Vessel>) vesselDAO.selectTotalList();
			
			allCompanyList 	= (ArrayList<Company>) companyDAO.selectList(new Company());
			
			log.info("스케줄 생성 마스터 초기화 종료");
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
	 * @throws Exception 
	 */
	public ScheduleExecute getOutboundSchedule() throws Exception
	{
		return new OutboundSchedulePrintV2();
	}
	/**
	 * @설명 인바운드 스케줄 생성
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleExecute getInboundSchedule() throws SQLException
	{
		return new InboundScheduleJoint();
	}
	/**
	 * @설명 항로별 스케줄 생성
	 * @param op
	 * @param orderType
	 * @return
	 * @throws SQLException 
	 */
//	public ScheduleJoint getRouteSchedule(ShippersTable op,int orderType) throws Exception
//	{
//		return new RouteScheduleJoint(op,orderType);
//	}
	/**
	 * @설명 콘솔 스케줄 생성
	 * @param op
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleExecute getConsoleSchedudle(ScheduleData op) throws SQLException
	{
		return new ConsoleScheduleJoint(op);
	}
	/**
	 * @설명 인랜드 스케줄 생성
	 * @return
	 * @throws SQLException 
	 */
	public ScheduleExecute getInlandSchedule() throws SQLException
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
	 * 
	 * 선박 정보 조회
	 * 
	 * 
	 * @param vesselName
	 * @return
	 * @throws SQLException
	 * @throws VesselNullException
	 */
	public Vessel searchVessel(String vesselName) throws ResourceNotFoundException {		

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

		throw new ResourceNotFoundException("vessel name error :"+vesselName);
	}

	/**
	 * 
	 * 선사 정보 조회
	 * @param companyName
	 * @return
	 * @throws SQLException
	 * @throws VesselNullException
	 */
	public Company searchCompany(String companyName) throws ResourceNotFoundException {		

		Iterator<Company> iterator = allCompanyList.iterator();
		while(iterator.hasNext())
		{
			Company info=iterator.next();
			if(info.getCompany_name().toUpperCase().equals(companyName.toUpperCase()))
			{
				if(info.getCompany_name().toUpperCase().equals(info.getCompany_abbr().toUpperCase()))
				{
					return info;
				}
			}
		}

		throw new ResourceNotFoundException("companyName null:"+companyName);
	}


	/**
	 * 
	 */
	public void startBuild()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				log.info("스케줄 생성 시작");

				String fileLocation =KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION);

				File file = new File(fileLocation);

				if(!file.exists()) file.mkdirs();
				
				di = new ScheduleBuildMessageDialog();
				
				di.createAndUpdateUI();
				
				Iterator<ScheduleExecute>iter = scheduleBuilds.iterator();
				
				initMasterData();
				
				try {
					while(iter.hasNext())
					{
						long startTime = System.currentTimeMillis();
						
						AbstractSchedulePrint build = (AbstractSchedulePrint) iter.next();
						
						di.setTask(build);
						
						build.init();
						
						build.execute();
						
						build.setDone(true);
						
						long endTime = System.currentTimeMillis();
						
						log.info("스케줄 생성 종료({}s)",(endTime-startTime));
					}
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, e);
					e.printStackTrace();
				}finally {
					scheduleBuilds.clear();
					di.end();
				}
			}
		};
		thread.start();
	}
	/**
	 * @param build
	 */
	public void addBulid(ScheduleExecute build)
	{
		scheduleBuilds.add(build);
	}


}
