package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.model.CommandMap;
import com.ksg.dao.impl.AreaDAOImpl;
import com.ksg.domain.AreaInfo;
import com.ksg.service.AreaService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AreaServiceImpl extends AbstractServiceImpl implements AreaService{
	
	private AreaDAOImpl areaDAO;
	
	public AreaServiceImpl() {
		super();
		areaDAO = new AreaDAOImpl();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectAreaList(Map<String, Object> commandMap) throws SQLException {
		
		log.debug("param:{}", commandMap);
		return areaDAO.selectAreaList(commandMap);
	}
	
	public List<AreaInfo> selectAll() throws SQLException
	{	
		return areaDAO.selectAll();
	}
	
	public List getAreaListGroupByAreaName() throws SQLException {
		return areaDAO.getAreaListGroupBy("area");
	}

	public List getAreaListGroupByAreaCode() throws SQLException{
		return areaDAO.getAreaListGroupBy("code");
	}

	@Override
	public List<CommandMap> selectAreaInfoList() throws SQLException {
		log.debug("");
		
		List<AreaInfo> re =areaDAO.selectAreaList2(new CommandMap()); 
		ArrayList<CommandMap> map = new ArrayList<CommandMap>();
		for(AreaInfo item:re)
		{	
			map.add((CommandMap) objectMapper.convertValue(item, CommandMap.class));
		}
		
		return map;
	}

	@Override
	public int deleteArea(AreaInfo info) throws SQLException {
		// TODO Auto-generated method stub
		return areaDAO.deleteArea(info);
	}

	@Override
	public int updateArea(AreaInfo info) throws SQLException {
		// TODO Auto-generated method stub
		return areaDAO.updateArea(info);
	}

	@Override
	public Object insertArea(AreaInfo info) throws SQLException {
		return areaDAO.insertArea(info);
	}
	public int deleteArea(HashMap<String, Object> param) throws SQLException {
		return (Integer) areaDAO.deleteArea(param);
	}
	
	public int updateArea(HashMap<String, Object> param) throws SQLException {
		return (Integer) areaDAO.updateArea(param);
	}

	public void insertArea(HashMap<String, Object> param) throws SQLException{
		areaDAO.insertArea(param);
		
	}
}
