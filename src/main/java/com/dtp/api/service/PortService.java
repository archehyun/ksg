package com.dtp.api.service;

import java.util.List;

import com.ksg.domain.PortInfo;

public interface PortService {

	public List<PortInfo> selectListByCondtion(PortInfo port) throws Exception;

	public PortInfo insertPort(PortInfo port) throws Exception;

	public PortInfo insertPortDetail(PortInfo port)throws Exception;

	public PortInfo updatePort(PortInfo port)throws Exception;
	
	public PortInfo deletePort(String port_name)throws Exception;
	
	public int deletePortDetail(PortInfo port)throws Exception;
	
	public List<PortInfo> selectPortDetailListByPortName(String port_name) throws Exception;

	public PortInfo selectById(String port_name) throws Exception;

}
