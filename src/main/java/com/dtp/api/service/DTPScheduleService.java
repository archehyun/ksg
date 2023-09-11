package com.dtp.api.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;

public interface DTPScheduleService {
	
	public List<ScheduleData> selecteScheduleListByCondition(ScheduleData param) throws SQLException;

	public List<ScheduleData> selectShipperTableListAllByCondition(ShippersTable param)throws SQLException;

	List<ScheduleData> selectInlandScheduleListByCondition(ScheduleData param) throws SQLException;;

}
