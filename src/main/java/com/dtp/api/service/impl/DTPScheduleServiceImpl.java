package com.dtp.api.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.dtp.api.dao.ScheduleDAO;
import com.dtp.api.service.DTPScheduleService;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;

public class DTPScheduleServiceImpl implements DTPScheduleService{
	
	private ScheduleDAO dao;
	
	public DTPScheduleServiceImpl()
	{
		dao = new ScheduleDAO();
	}

	@Override
	public List<ScheduleData> selecteScheduleListByCondition(ScheduleData param) throws SQLException {
		return dao.selectScheduleLisByCondition(param);
	}

	@Override
	public List<ScheduleData> selectShipperTableListAllByCondition(ShippersTable param) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
