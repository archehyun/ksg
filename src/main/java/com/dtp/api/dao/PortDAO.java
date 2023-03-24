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

	public PortInfo selectById(String port_name) throws SQLException {
		return (PortInfo) selectOne(this.namespace+".selectPortById", port_name);
	}

	public List<PortInfo> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public List selectListByCondition(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return selectList(this.namespace+".selectPortListByCondition", param);
	}

	public Object deletePort(String port_name) throws SQLException {
		return delete(this.namespace+".deletePortInfo", port_name);
	}

	public void deletePortDetail(String id) {
		// TODO Auto-generated method stub
		
	}
	
	public Object deletePortDetail(PortInfo port) throws SQLException {
		return delete(this.namespace+".deletePortDetail", port);
		
	}

	public Object insertPort(PortInfo param) throws SQLException {
		return insert(this.namespace+".insertPortInfo", param);
		
	}

	public PortInfo selectByKey(String port_name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object insertPortDetail(PortInfo param) throws SQLException {
		
		return insert(this.namespace+".insertPortInfoDetail", param);
	}

	public Object updatePort(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return update(this.namespace+".updatePortInfo", param);
	}

	public void updatePortDetail(PortInfo param) {
		// TODO Auto-generated method stub
		
	}

	public List<PortInfo> selectPortDetailListByPortName(String port_name) throws SQLException {
		// TODO Auto-generated method stub
		return selectList(this.namespace+".selectPortDetailListByPortName", port_name);
	}

	public PortInfo selectDetailByKey(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return (PortInfo) selectOne(this.namespace+".selectPortDetailByKey", param);
	}

	

}
