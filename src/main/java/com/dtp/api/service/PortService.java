package com.dtp.api.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.PortInfo;

public interface PortService {

	List<PortInfo> selectListByCondtion(PortInfo port) throws SQLException;

	PortInfo insert(PortInfo port) throws Exception;

	PortInfo insertDetail(PortInfo port)throws Exception;

	PortInfo update(PortInfo port)throws Exception;

	PortInfo delete(String port_name)throws Exception;

	PortInfo deleteDetail(String port_abbr)throws Exception;

	List<PortInfo> selectPortDetailListByPortName(String port_name) throws SQLException;

}
