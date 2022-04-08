/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.ksg.dao.BaseDAO;
import com.ksg.dao.impl.BaseDAOImpl2;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.Code;
import com.ksg.domain.Company;
import com.ksg.domain.KeyWordInfo;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Vessel;
import com.ksg.service.BaseService;


@Deprecated
@SuppressWarnings("unchecked")
public class BaseServiceImpl implements BaseService
{
	BaseDAO baseDAO; 
	public BaseServiceImpl() {
		baseDAO = new BaseDAOImpl2();
	}
	
	public List getBaseInfo(String type) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getBaseInfo(type);
	}
	
	public List getFieldInfo(String type) throws SQLException
	{
		return baseDAO.getFieldInfo(type);
	}
	
	public List getCodeInfo(String type) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getCodeInfo(type);
	}
	
	public boolean isExitPort(String port_name) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.isExitPort(port_name);
	}
		

	public List<Code> getSubCodeInfo(Code code_info) throws SQLException
	{
		return baseDAO.getSubCodeInfo(code_info);
	}
	public List getCodeType() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getCodeTypeList();
	}

	public List<Code> getCodeInfoByField(String code_type) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getCodeInfoByType(code_type);
	}

	public List getPortListByPatten(String patten) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getPortListByPatten(patten);
	}

	public void delete(Object key) {
		// TODO Auto-generated method stub
		
	}

	public PortInfo getPortInfoByPortName(String port) throws SQLException {
		return baseDAO.getPortInfoByPortNamePatten(port);
		
	}

	public Company getCompanyInfo(String company_abbr) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getCompanyInfo(company_abbr);
	}

	public List getPortInfoList() throws SQLException{
		return baseDAO.getPortInfoList();
	}

	public List getAreaInfoList() throws SQLException{
		return baseDAO.getAreaInfoList();
	}
	public List getAreaInfoList(AreaInfo info) throws SQLException{
		return baseDAO.getAreaInfoList(info);
	}

	public PortInfo getPortInfoAbbrByPortNamePatten(String port)
			throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getPortInfoAbbrByPortName(port);
	}

	public PortInfo getPortInfo(String string) throws SQLException {
		PortInfo  portInfo=this.getPortInfoByPortName(string );
		if(portInfo==null)
		{
			PortInfo info =this.getPortInfoAbbrByPortNamePatten(string);
			if(info==null)
			{
				return null;
			}else
			{
				return info;
			}
		}else
		{
			return portInfo;
		}
	}

	

	public List getCompanyList() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getCompanyList();
	}

	public List getPageList() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getPageList();
	}

	public List getPort_AbbrList() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getPor_AbbrList();
	}

	public List getVesselList(Vessel info) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getVesselList(info);
	}

	public void insertPortInfo(PortInfo info) throws SQLException {
		baseDAO.insertPortInfo(info);
		
	}

	public void insertCompany(Company info) throws SQLException {
		baseDAO.insertCompany(info);
		
	}

	public AreaInfo getAreaInfo(String area) throws SQLException {
		AreaInfo info = new AreaInfo();
		info.setArea_name(area);
		return baseDAO.getAreaInfo(info);
	}

	public List getAreaGroupList() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getAreaGroupList();
	}

	

	public List getAreaSubList() throws SQLException {
		AreaInfo info = new AreaInfo();
		return baseDAO.getAreaSubList(info);
	}

	public List getAreaSubList(String area_code)  throws SQLException {
		AreaInfo info = new AreaInfo();
		info.setArea_code(area_code);
		return baseDAO.getAreaSubList(info);
	}

	public void insertAreaInfo(AreaInfo insert) throws SQLException {
		baseDAO.insertAreaInfo(insert);
		
	}

	public void updateAreaInfo(AreaInfo update) throws SQLException {
		baseDAO.updateAreaInfo(update);
		
	}

	public void insertPort_Abbr(PortInfo info) throws SQLException {
		baseDAO.insertPort_Abbr(info);
		
	}

	public Object insertVessel(Vessel vessel) throws SQLException {
		return baseDAO.insertVessel(vessel);
	}

	public int deleteArea(String data) throws SQLException {
		return baseDAO.deleteArea(data);
		
	}

	public int deleteCompany(String data) throws SQLException {
		return baseDAO.deleteCompany(data);
		
	}

	public int deletePort(String data) throws SQLException {
		return baseDAO.deletePort(data);
		
	}

	public int deletePort_Abbr(String data) throws SQLException {
		return baseDAO.deletePort_Abbr(data);
		
	}

	public int deleteVessel(String data) throws SQLException {
		return baseDAO.deleteVessel(data);
		
	}

	public void updateCompany(Company info) throws SQLException {
		baseDAO.updateCompany(info);
	}

	public List getADVInfoList() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.selectADV();
	}


	public void updatePortInfo(PortInfo t) throws SQLException {
		baseDAO.updatePortInfo(t);
		
	}

	public List getFieldNameList(String table_name) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getFieldNameList(table_name);
	}

	
	public List getArrangedPortInfoList(String orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getArrangedPortInfoList(orderBy);
	}

	public List getArrangedAreaList(String orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getArrangedAreaInfoList(orderBy);
	}

	public List getArrangedCompanyList(Object orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getArrangedCompanyList(orderBy);
	}

	public List getArrangedPort_AbbrList(Object orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getArrangedPort_AbbrList(orderBy);
	}

	public List getArrangedTableList(Object orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getArrangedTableList(orderBy);
	}

	public List getArrangedVesselList(Object orderBy) throws SQLException {
		return baseDAO.getArrangedVesselList(orderBy);
	}
	public List getSearchedAreaList(String searchKeyword) throws SQLException{
		// TODO Auto-generated method stub
		return baseDAO.getSearchedAreaList(searchKeyword);
	}

	public List getSearchedCompanyList(String searchKeyword)
			throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getSearchedCompanyList(searchKeyword);
	}

	public List getSearchedPortList(String searchKeyword) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getSearchedPortList(searchKeyword);
	}

	public List getSearchedPort_AbbrList(String searchKeyword)
			throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getSearchedPort_AbbrList(searchKeyword);
	}
	public List getSearchedVesselList(Vessel op) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getSearchedVesselList(op);
	}
	public List getSearchedVesselList(String searchKeyword) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getSearchedVesselList(searchKeyword);
	}

	public List getKeywordList(String searchKeywordType) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getKeywordList(searchKeywordType);
	}

	public void deleteKeyword(Object selectedValue) throws SQLException {
		baseDAO.deleteKeyword(selectedValue);
		
	}

	public void insertKeyword(KeyWordInfo insert) throws SQLException {
		baseDAO.insertKeyword(insert);
		
	}

	public List getVesselInfoByPatten(String value) throws SQLException {
		return baseDAO.getVesselListByPatten(value);
	}

	public List getArrangedAreaList(String orderBy, String type)
			throws SQLException {
	
		return baseDAO.getArrangedAreaInfoList(orderBy,type);
	}

	public int getAreaCount() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getAreaCount();
	}

	public int getCompanyCount() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getCompanyCount();
	}

	public int getPortCount() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getPortCount();
	}

	public int getPort_AbbrCount() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getPort_AbbrCount();
	}

	public int getVesselCount() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getVesselCount();
	}

	public List<Code> getCodeInfoList(Code code_info) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getCodeInfoList(code_info);
	}

	public boolean hasCodeByField(String info) throws SQLException {
		
		
		info=info.replace("\n", " ");
		Object obj=baseDAO.hasCodeByField(info);
		if(obj!=null)
		{
			return true;
		}else
		{
			return false;
		}
	}

	public void insertCode(Code code_info) throws SQLException {
		baseDAO.insertCode(code_info);
		
	}

	public int deleteCode(Code code_info) throws SQLException {
		return baseDAO.deleteCode(code_info);
		
	}

	public Code getCodeInfo(Code code_info) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getCodeInfo(code_info);
	}

	public void updateCode(Code code) throws SQLException {
		baseDAO.updateCode(code);
	}

	public PortInfo getPortInfoAbbrByPortName(String portName)
			throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getPortInfoByPortName(portName);
	}

	public void updateVessel(Vessel vessel) throws SQLException {
		baseDAO.updateVessel(vessel);
		
	}

	public void insertVessel_Abbr(Vessel vesselAbbr) throws SQLException {
		baseDAO.insertVessel_Abbr(vesselAbbr);
	}

	

	

	public int getVesselAbbrCount() throws SQLException {
		return baseDAO.getVesselAbbrCount();
	}

	public List getArrangedVesselAbbrList(String orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getArrangedVesselAbbrList(orderBy);
	}

	public List getSearchedVesselAbbrList(String orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getSearchedVesselAbbrList(orderBy);
	}

	public Vessel getVesselAbbrInfo(String vesselName) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getVesselAbbrInfo(vesselName);
	}

	public List getVessel_AbbrList(String vesselName) throws SQLException {
		// TODO Auto-generated method stub
		return  baseDAO.getVesselAbbrList(vesselName);
	}

	public List getVesselAbbrList(Vessel info) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getVesselAbbrList(info);
	}

	public int deleteVesselAbbr(Vessel op) throws SQLException {
		return baseDAO.deleteVesselAbbr(op);
		
	}

	
	public Vessel getVesselInfo(Vessel vessel) throws SQLException {
		return baseDAO.selectVessel(vessel);
	}
	
	public List getVesselAbbrInfoByPatten(String op) throws SQLException {
		return baseDAO.getVesselAbblListByPatten(op);
	}

	public List getVesselListGroupByName(Vessel info) throws SQLException {
		return baseDAO.getVesselListGroupByName(info);
	}

	public void updateVesselAbbr(Vessel vessel) throws SQLException {
		baseDAO.updateVesselAbbr(vessel);
		
	}

	public List getVesselInfoByPattenGroupByName(String string)
			throws SQLException {
		return baseDAO.getVesselListByPattenGroupByName(string);
	}

	public Vessel getVesselInfoItem(Vessel vessel) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getVesselInfoItem(vessel);
	}

	public void deleteVessel() throws SQLException {
		baseDAO.deleteVessel();
		
	}

	public void updatePortAbbr(PortInfo info) throws SQLException {
		baseDAO.updatePort_Abbr(info);
	}

	public boolean hasCodeByField(Code info) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List getAreaListGroupByAreaCode() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getAreaListGroupByAreaCode();
	}

	@Override
	public List getAreaListGroupByAreaName() throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getAreaListGroupByAreaName();
	}

	@Override
	public List getSearchedPortList(PortInfo option) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getSearchedPortList(option);
	}

	@Override
	public List getSearchedCompanyList(Company company) throws SQLException {
		// TODO Auto-generated method stub
		return baseDAO.getSearchedCompanyList(company);
	}

	@Override
	public List getSearchedPort_AbbrList(PortInfo searchKeyword) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	






}
