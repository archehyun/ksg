package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.model.CommandMap;
import com.ksg.dao.impl.AreaDAOImpl;
import com.ksg.domain.AreaInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AreaServiceImpl extends AbstractServiceImpl implements AreaService{
	
	AreaDAOImpl areaDAO;
	
	
	
	public AreaServiceImpl() {
		super();
		areaDAO = new AreaDAOImpl();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectAreaList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return areaDAO.selectAreaList(commandMap);

	}

	public int deleteArea(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) areaDAO.deleteArea(param);
	}
	
	public int updateArea(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) areaDAO.updateArea(param);
	}

	public void insertArea(HashMap<String, Object> param) throws SQLException{
		areaDAO.insertArea(param);
		
	}
	
	public List getAreaListGroupByAreaName() throws SQLException {
		return areaDAO.getAreaListGroupBy("area");
	}


	public List getAreaListGroupByAreaCode() throws SQLException{
		// TODO Auto-generated method stub
		return areaDAO.getAreaListGroupBy("code");
	}


	@Override
	public List<CommandMap> selectAreaInfoList() throws SQLException {
		
		List<AreaInfo> re =areaDAO.selectAreaList2(new CommandMap()); 
		ArrayList<CommandMap> map = new ArrayList<CommandMap>();
		for(AreaInfo item:re)
		{	
			map.add((CommandMap) objectMapper.convertValue(item, CommandMap.class));
		}
		
		return map;
	}


	
	

}
