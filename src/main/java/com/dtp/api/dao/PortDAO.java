package com.dtp.api.dao;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.PortInfo;

public class PortDAO extends AbstractDAO {
	
	public PortDAO()
	{
		super();
		this.namespace ="port";
	}

	public PortInfo selectById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PortInfo> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public List selectListByCondition(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return selectList(this.namespace+".selectPortListByCondition", param);
	}

	public int deletePort(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void deletePortDetail(String id) {
		// TODO Auto-generated method stub
		
	}

	public void insertPort(PortInfo param) {
		// TODO Auto-generated method stub
		
	}

	public PortInfo selectByKey(String port_name) {
		// TODO Auto-generated method stub
		return null;
	}

	public int insertPortDetail(PortInfo param) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int updatePort(PortInfo param) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updatePortDetail(PortInfo param) {
		// TODO Auto-generated method stub
		
	}

	public List<PortInfo> selectPortDetailListByPortName(String port_name) throws SQLException {
		// TODO Auto-generated method stub
		return selectList(this.namespace+".selectPortDetailListByPortName", port_name);
	}

}
