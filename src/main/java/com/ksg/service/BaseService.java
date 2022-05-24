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
package com.ksg.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.AreaInfo;
import com.ksg.domain.Code;
import com.ksg.domain.Company;
import com.ksg.domain.KeyWordInfo;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Vessel;

@Deprecated
public interface BaseService {
	
	public void delete(Object key);
	
	public int deleteArea(String data)throws SQLException;

	public int deleteCode(Code code_info)throws SQLException;	

	public int deleteCompany(String data)throws SQLException;

	public void deleteKeyword(Object selectedValue) throws SQLException;
	
	public int deletePort(String data)throws SQLException;

	public int deletePort_Abbr(String data)throws SQLException;
	
	public int deleteVessel(String data)throws SQLException;

	public List getADVInfoList()throws SQLException;

	public int getAreaCount()throws SQLException;
	
	public List getAreaGroupList()throws SQLException;

	public AreaInfo getAreaInfo(String area_name) throws SQLException;

	public List getAreaInfoList()throws SQLException;

	public List getAreaSubList()throws SQLException;

	
	public List getAreaSubList(String area_code) throws SQLException;

	public List getArrangedAreaList(String orderBy)throws SQLException;


	public List getArrangedAreaList(String orderBy,String type)throws SQLException;

	
	public List getArrangedCompanyList(Object orderBy)throws SQLException;

	
	
	public List getArrangedPort_AbbrList(Object orderBy)throws SQLException;

	public List getArrangedPortInfoList(String orderBy) throws SQLException;

	public List getArrangedTableList(Object orderBy)throws SQLException;


	public List getArrangedVesselList(Object orderBy)throws SQLException;
	
	public List getBaseInfo(String type) throws SQLException;
	
	public List<Code> getCodeInfoList(Code code_info) throws SQLException;	
	
	public Code getCodeInfo(Code code_info) throws SQLException;
	
	public List getCodeInfo(String type) throws SQLException;
	
	public List<Code> getCodeInfoByField(String code_type) throws SQLException;
	
	public List getCodeType()throws SQLException;
	
	public int getCompanyCount()throws SQLException;
	
	//company
	public Company getCompanyInfo(String company_abbr)throws SQLException;
	
	public List getCompanyList()throws SQLException;
	
	public List getFieldInfo(String type) throws SQLException;

	public List getFieldNameList(String table_name) throws SQLException;

	public List getKeywordList(String searchKeywordType) throws SQLException;	
	
	public List getPageList()throws SQLException;
	
	public List<PortInfo> getPort_AbbrList()throws SQLException;
	
	public int getPort_AbbrCount()throws SQLException;
	
	public int getPortCount()throws SQLException;
	
	public PortInfo getPortInfo(String string)throws SQLException;
	
	public PortInfo getPortInfoAbbrByPortNamePatten(String string)throws SQLException;
	
	//port
	public PortInfo getPortInfoByPortName(String port)throws SQLException;
	
	public List<PortInfo> getPortInfoList() throws SQLException;
	
	public List<String> getPortListByPatten(String patten)throws SQLException;
	
	//area
	public List getSearchedAreaList(String orderBy) throws SQLException;
	
	public List getSearchedCompanyList(String searchKeyword) throws SQLException;	
	
	public List getSearchedPortList(String searchKeyword) throws SQLException ;
	
	public List getSearchedVesselList(String searchKeyword) throws SQLException ;
	
	public List<Code> getSubCodeInfo(Code code_info) throws SQLException;
	
	public int getVesselCount()throws SQLException;
	
	public Vessel getVesselInfo(Vessel vessel)throws SQLException;
	public Vessel getVesselInfoItem(Vessel vessel)throws SQLException;
	
	//vessel
	public List getVesselInfoByPatten(String valueOf)throws SQLException;
	
	public List<Vessel> getVesselList(Vessel info)throws SQLException;
	
	public boolean hasCodeByField(String info)throws SQLException;
	
	public boolean hasCodeByField(Code info)throws SQLException;
	
	public void insertAreaInfo(AreaInfo insert)throws SQLException;
	
	
	public void insertCode(Code code_info)throws SQLException;
	
	//public void insertCompany(Company info)throws SQLException;

	public void insertKeyword(KeyWordInfo insert)throws SQLException;

	public void insertPort_Abbr(PortInfo info)throws SQLException;
	
	//public void insertPortInfo(PortInfo info)throws SQLException;
	
	
	
	public boolean isExitPort(String string)throws SQLException;
	
	public void updateAreaInfo(AreaInfo update)throws SQLException;	
	
	public void updateCompany(Company info)throws SQLException;
	public void updatePortInfo(PortInfo t)throws SQLException;
	public void updatePortAbbr(PortInfo t)throws SQLException;
	public void updateCode(Code code)throws SQLException;
	public PortInfo getPortInfoAbbrByPortName(String portName)throws SQLException;
	//public void updateVessel(Vessel vessel)throws SQLException;
	public List getAreaInfoList(AreaInfo info)throws SQLException;
	public void insertVessel_Abbr(Vessel vesselAbbr) throws SQLException ;
	public int getVesselAbbrCount()throws SQLException;
	public List getArrangedVesselAbbrList(String orderBy)throws SQLException;
	public List getSearchedVesselAbbrList(String orderBy)throws SQLException;
	//public Vessel getVesselAbbrInfo(String vesselName)throws SQLException;
	public List getVessel_AbbrList(String vesselName)throws SQLException;
	public List getVesselAbbrList(Vessel info)throws SQLException;
	public int deleteVesselAbbr(Vessel op)throws SQLException;
	public List getVesselAbbrInfoByPatten(String op)throws SQLException;
	public List getVesselListGroupByName(Vessel info) throws SQLException;
	public void updateVesselAbbr(Vessel vessel)throws SQLException;
	public List getVesselInfoByPattenGroupByName(String string)throws SQLException;
	public void deleteVessel()throws SQLException;
	public List getSearchedVesselList(Vessel op) throws SQLException;
	public List getAreaListGroupByAreaCode() throws SQLException;
	public List getAreaListGroupByAreaName()throws SQLException;
	public List getSearchedPortList(PortInfo option)throws SQLException;
	public List getSearchedCompanyList(Company company)throws SQLException;
	
	
}

