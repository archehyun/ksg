package com.dtp.api.service;

import java.util.List;

import com.ksg.domain.PortInfo;

public interface PortService {

	List<PortInfo> selectListByCondtion(PortInfo port) throws Exception;

	PortInfo insertPort(PortInfo port) throws Exception;

	PortInfo insertPortDetail(PortInfo port)throws Exception;

	PortInfo updatePort(PortInfo port)throws Exception;
	
	PortInfo deletePort(String port_name)throws Exception;
	
	PortInfo deletePortDetail(PortInfo port)throws Exception;

	PortInfo deleteDetail(String port_abbr)throws Exception;
	
	List<PortInfo> selectPortDetailListByPortName(String port_name) throws Exception;

	PortInfo selectById(String port_name) throws Exception;

}
