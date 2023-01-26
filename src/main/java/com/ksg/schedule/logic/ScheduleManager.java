package com.ksg.schedule.logic;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.joint.ConsoleScheduleJoint;
import com.ksg.schedule.logic.joint.DefaultScheduleJoint;
import com.ksg.schedule.logic.joint.InboundScheduleJoint;
import com.ksg.schedule.logic.joint.OutboundScheduleJointV2;
import com.ksg.schedule.logic.joint.RouteScheduleJoint;
import com.ksg.service.BaseService;
import com.ksg.service.CompanyService;
import com.ksg.service.VesselService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ������ ���� �Ŵ���
 * 
 * 
 * @author archehyun
 *
 */
@Slf4j
public class ScheduleManager {
	
	protected Logger logger = LogManager.getLogger(this.getClass());

	private static ScheduleManager instance;

	private ArrayList<ScheduleJoint> scheduleBuilds;

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
		scheduleBuilds = new ArrayList<ScheduleJoint>();
		
		baseService = new BaseServiceImpl();
		
		vesselDAO = new VesselDAOImpl();
		companyDAO = new CompanyDAOImpl();
		
	}
	public void init()
	{
		try {
			logger.info("������ ���� ������ �ʱ�ȭ");
			allPortlist 	= (ArrayList<PortInfo>) baseService.getPortInfoList();
			allPortAbbrlist = (ArrayList<PortInfo>) baseService.getPort_AbbrList();
//			allVessellist 	= (ArrayList<Vessel>) baseService.getVesselList(new Vessel());
			allVessellist 	 = (ArrayList<Vessel>) vesselDAO.selectTotalList();
			allCompanyList 	= (ArrayList<Company>) companyDAO.selectList(new Company());
			logger.info("������ ���� ������ �ʱ�ȭ ����");
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
	public ScheduleJoint getRouteSchedule(ShippersTable op,int orderType) throws Exception
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
	 * 
	 * ���� ���� ��ȸ
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
	 * ���� ���� ��ȸ
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
				log.info("������ ���� ����");

				String fileLocation =KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION);

				File file = new File(fileLocation);

				if(!file.exists())
				{
					file.mkdirs();
					log.info("���� ����:"+fileLocation);
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
				catch (Exception e) {
					// TODO Auto-generated catch block
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
	public void addBulid(ScheduleJoint build)
	{
		scheduleBuilds.add(build);
	}


}
