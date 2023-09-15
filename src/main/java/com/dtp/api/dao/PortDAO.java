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
	
	// -- select List -- //
	
	public List<PortInfo> selectListByCondition(PortInfo param) throws SQLException {
		return selectList(this.namespace+".selectPortListByCondition", param);
	}
	
	public List<PortInfo> selectPortDetailListByPortName(String port_name) throws SQLException {
		return selectList(this.namespace+".selectPortDetailListByPortName", port_name);
	}
	
	
	// -- select List -- //
	
	public PortInfo selectPortById(String port_name) throws SQLException {
		return (PortInfo) selectOne(this.namespace+".selectPortById", port_name);
	}
	
	public PortInfo selectDetailById(String id) throws SQLException {
		
		return (PortInfo) selectOne(this.namespace+".selectPortDetailById", id);
	}

	public PortInfo selectDetailByKey(PortInfo param) throws SQLException {
		return (PortInfo) selectOne(this.namespace+".selectPortDetailByKey", param);
	}

	@Deprecated
	public PortInfo selectDetail(PortInfo param) throws SQLException {
		
		return (PortInfo) selectOne(this.namespace+".selectPortDetail", param);
	}

	// -- delete -- //
	
	public Object deletePort(String port_name) throws SQLException {
		return delete(this.namespace+".deletePortInfo", port_name);
	}
	
	public Object deletePortDetail(PortInfo port) throws SQLException {
		return delete(this.namespace+".deletePortDetail", port);
		
	}
	
	// -- insert -- //

	public Object insertPort(PortInfo param) throws SQLException {
		return insert(this.namespace+".insertPortInfo", param);
		
	}

	public Object insertPortDetail(PortInfo param) throws SQLException {
		
		return insert(this.namespace+".insertPortInfoDetail", param);
	}

	// -- update -- //
	
	public Object updatePort(PortInfo param) throws SQLException {
		return update(this.namespace+".updatePortInfo", param);
	}
}
