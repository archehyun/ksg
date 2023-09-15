package com.dtp.api.dao;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.ScheduleData;

public class ScheduleDAO extends AbstractDAO{
	
	public ScheduleDAO()
	{
		this.namespace ="schedule";
	}
	
	public List<ScheduleData> selectScheduleLisByCondition(ScheduleData schedule) throws SQLException {
		return selectList(namespace+"."+ "selectScheduleListByCondition2", schedule);
	}
	
	public List<ScheduleData> selectInlandScheduleListByCondition(ScheduleData schedule) throws SQLException {
		return selectList(namespace+"."+ "selectInlandScheduleListByCondition", schedule);
	}
	
	public int deleteScheduleById(String table_id) throws SQLException
	{
		return (int) delete("schedule.deleteScheduleById", table_id);
	}

}
