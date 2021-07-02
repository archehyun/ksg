package com.ksg.schedule.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ksg.schedule.dao.ADVScheduleDAO;

public class ScheduleService {
	
	
	ADVScheduleDAO advScheduleDAO;
	
	public ScheduleService() {
		advScheduleDAO = new ADVScheduleDAO();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectScheduledAreaList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return advScheduleDAO.selectScheduledAreaList(commandMap);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectSceduleList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return advScheduleDAO.selectScheduleList(commandMap);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectScheduleJointList(Map<String, Object> commandMap) throws SQLException {
		
		List<Map<String, Object>> areaList = selectScheduledAreaList(commandMap);
		
		List<Map<String, Object>> returnList = new LinkedList<Map<String,Object>>();
		
		for(Map<String, Object> areaItem: areaList)
		{
			HashMap<String, Object> param = new HashMap<String, Object>();
			
			String area_name = (String) areaItem.get("area_name");		
			
			
			commandMap.put("area_name", area_name);			
			
			List<Map<String, Object>> portList = selectScheduledPortList("toPort",commandMap);
			
			param.put("area_name", area_name);
			
			param.put("port_list", portList);
			
			for(Map<String, Object> portItem: portList)
			{
				String port =(String) portItem.get("port");
				
				commandMap.put("port", port);
				
				param.put(port, advScheduleDAO.selectScheduleJointList(commandMap));	
			}
			
			returnList.add(param);
		}
		
		return returnList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectInboundScheduleJointList(Map<String, Object> commandMap) throws SQLException {
		
		List<Map<String, Object>> areaList = selectScheduledPortList("fromPort",commandMap);
		
		List<Map<String, Object>> returnList = new LinkedList<Map<String,Object>>();
		
		for(Map<String, Object> areaItem: areaList)
		{
			String port =(String) areaItem.get("fromPort");
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("fromPort", port);
			commandMap.put("fromPort", port);
			param.put(port, advScheduleDAO.selectScheduleJointList(commandMap));
			
			returnList.add(param);
		}
		
		
		
		return returnList;
	}
	
	private List<Map<String, Object>> selectScheduledPortList(String portType,Map<String, Object> commandMap) throws SQLException {
		if(portType.equals("toPort"))
		{
		return advScheduleDAO.selectScheduledToPortList(commandMap);
		}else
		{
			return advScheduleDAO.selectScheduledFromPortList(commandMap);
		}
	}

	public HashMap<String, Object> selectScheduleList(HashMap<String, Object> commandMap) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", advScheduleDAO.selectScheduleCount(commandMap));
		
		resultMap.put("master", advScheduleDAO.selectScheduleList(commandMap));
		
		return resultMap;

	}
	public int getTotalCount(HashMap<String, Object> commandMap) throws SQLException
	{
		return advScheduleDAO.selectScheduleCount(commandMap);
	}

}
