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
package com.ksg.dao.impl;

import java.io.IOException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.Code;
import com.ksg.domain.Company;
import com.ksg.domain.KeyWordInfo;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Vessel;

@Deprecated
@SuppressWarnings("unchecked")
public class BaseDAOManager
{
	AreaDAOImpl2 areaDAOImpl;
	
	private CompanyDAOImpl2 companyDAOImpl;
	private SqlMapClient sqlMap;
	private VesselDAOImpl2 vesselDAOImpl;
	private PortDAOImpl2 portDAOImpl;
	public BaseDAOManager() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		areaDAOImpl =new AreaDAOImpl2();
		vesselDAOImpl = new VesselDAOImpl2();
		companyDAOImpl = new CompanyDAOImpl2();
		portDAOImpl = new PortDAOImpl2();
	}	

	
	public int deleteCode(Code code_info) throws SQLException {
		return sqlMap.delete("Base.deleteCode",code_info);
		
	}

	//delete
	public int deleteArea(String data) throws SQLException {
		return areaDAOImpl.delete(data);		
	}
	public int deleteCompany(String company) throws SQLException {
		return companyDAOImpl.delete(company);
	}

	
	public void deleteKeyword(Object key_name) throws SQLException {
		sqlMap.delete("Base.deleteKeyword",key_name);
	}
	
	public int deletePort(String port) throws SQLException {
		
		return portDAOImpl.deletePort(port);
	}
	
	public int deletePortAbbr(String data) throws SQLException {
		return portDAOImpl.deletePortAbbr(data);
	}
	
	public int deleteVessel(String data) throws SQLException {
		return vesselDAOImpl.deleteVessel(data);
	}

	public int deleteVesselAbbr(Vessel op) throws SQLException {
		return sqlMap.delete("BASE_VESSEL.deleteVesselAbbr",op);
	}

	public int deleteVesselAll() throws SQLException {
		return vesselDAOImpl.deleteVesselAll();
	}
	

//	public int getAreaCount() throws SQLException {
//		return areaDAOImpl.getCount();
//	}

//	public List getAreaGroupList() throws SQLException {
//		return areaDAOImpl.getAreaGroupList();
//	}

//	public List getAreaInfoList(AreaInfo info) throws SQLException {
//		
//		return areaDAOImpl.getAreaInfoList(info);
//	}

	public List getAreaListGroupByAreaCode() throws SQLException {
		return sqlMap.queryForList("BASE_AREA.selectAreaCodeListGroupByAreaCode");
	}

//	public List getAreaListGroupByAreaName() throws SQLException {
//		return areaDAOImpl.getAreaListGroupByAreaName();
//	}

	public List getArrangedAreaInfoList(String orderBy) throws SQLException {

		return areaDAOImpl.getArrangedAreaInfoList(orderBy);
	}
	public List getArrangedAreaInfoList(String orderBy, String type)
			throws SQLException {
		
		return areaDAOImpl.getArrangedAreaInfoList(orderBy,type);
	}

	public List getArrangedCompanyList(Object orderBy) throws SQLException {
		return companyDAOImpl.getArrangedCompanyList(orderBy);
	}

	public List getArrangedPort_AbbrList(Object orderBy) throws SQLException {
		return sqlMap.queryForList("BASE_PORT.selectArrangedPort_AbbrList",orderBy);
	}	

	public List getArrangedPortInfoList(String orderBy) throws SQLException {

		return sqlMap.queryForList("BASE_PORT.selectArrangedPortList",orderBy);
	}

	public List getArrangedTableList(Object orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Base.selectArrangedTableList",orderBy);
	}

	public List getArrangedVesselAbbrList(Object orderBy) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectArrangedVesselList",orderBy);
	}

	public List getArrangedVesselList(Object orderBy) throws SQLException {
		return vesselDAOImpl.getArrangedVesselList(orderBy);
	}

	public List getBaseInfo(String type) throws SQLException 
	{
		return sqlMap.queryForList("Base.select"+type+"List");
	}

	public Code getCodeInfo(Code code_info) throws SQLException {
		return (Code) sqlMap.queryForObject("Base.selectCodeField",code_info);
	}

	public List getCodeInfo(String type) throws SQLException {
		return sqlMap.queryForList("Base.selectCodeList",type);
	}

	public List<Code> getCodeInfoByType(String code_type) throws SQLException {
		return sqlMap.queryForList("Base.selectCodeListByField",code_type);
	}
	public List<Code> getCodeInfoList(Code code_info) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Base.selectCodeInfo",code_info);
	}

	// Code
	public List getCodeTypeList() throws SQLException {
		return sqlMap.queryForList("Base.selectCodeType");
	}

	public int getCompanyCount() throws SQLException {
		
		return companyDAOImpl.getCount();
	}

	public Company getCompanyInfo(String company_abbr) throws SQLException {
		return (Company) sqlMap.queryForObject("BASE_COMPANY.selectCompanyInfo",company_abbr);
	}

	public List getCompanyList() throws SQLException {
		return companyDAOImpl.getCompanyList();
	}

	public List getCompanyList(Company company) throws SQLException {
		return companyDAOImpl.getCompanyList(company);
	}

	public List getFieldInfo(String type) throws SQLException {
		return sqlMap.queryForList("Base.selectFieldList",type);
	}

	public List getFieldNameList(String table_name) throws SQLException
	{
		return sqlMap.queryForList("Base.getFieldNameList",table_name);
	}

	public List getKeywordList(String searchKeywordType) throws SQLException {
		return sqlMap.queryForList("Base.selectKeywordList",searchKeywordType);
	}
	public List getPageList() throws SQLException {
		return sqlMap.queryForList("Base.selectPageList");
	}

	public List getPortAbbrList() throws SQLException {
		return sqlMap.queryForList("Base.selectPort_AbbrList");
	}

	public int getPortAbbrCount() throws SQLException {
		return (Integer) sqlMap.queryForObject("BASE_PORT.selectPort_AbbrCount");
	}

	public int getPortCount() throws SQLException {
		return (Integer) sqlMap.queryForObject("BASE_PORT.selectPortCount");
	}

	public PortInfo getPortInfoAbbrByPortName(String port) throws SQLException {
		return (PortInfo) sqlMap.queryForObject("Base.selectPortInfoAbbrByPortName",port);
	}

	public PortInfo getPortInfoByPortName(String portName) throws SQLException {
		return (PortInfo) sqlMap.queryForObject("BASE_PORT.selectPortInfoByPortName",portName);
	}


	public PortInfo getPortInfoByPortNamePatten(String port_code) throws SQLException {
		return (PortInfo) sqlMap.queryForObject("BASE_PORT.selectPortInfoByPortName",port_code);
	}

	public List getPortInfoList() throws SQLException {
		return sqlMap.queryForList("BASE_PORT.selectPortInfoList");
	}	
	
	public List getPortListByPatten(String patten) throws SQLException {
		return sqlMap.queryForList("BASE_PORT.selectPortListByPatten",patten+"%");
	}
	public List getSearchedAreaList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_AREA.selectSearchedAreaList",searchKeyword);
	}

	public List getSearchedCompanyList(Company company) throws SQLException {
		return sqlMap.queryForList("BASE_COMPANY.selectSearchedCompanyListOrderby",company);
	}
	public List getSearchedCompanyList(String searchKeyword) throws SQLException {
		return companyDAOImpl.getSearchedCompanyList(searchKeyword);
	}
	public List getSearchedPortAbbrList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_PORT.selectSearchedPort_AbbrList",searchKeyword);
	}
	public List getSearchedPortList(PortInfo option) throws SQLException {
		return sqlMap.queryForList("BASE_PORT.selectSearchedPortList",option);
	}
	public List getSearchedPortList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_PORT.selectSearchedPortList",searchKeyword);
	}
	public List getSearchedVesselAbbrList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectSearchedVesselAbbrList",searchKeyword);
	}
	public List getSearchedVesselList(String searchKeyword) throws SQLException {
		return vesselDAOImpl.getSearchedVesselList(searchKeyword);
	}
	public List getSearchedVesselList(Vessel op) throws SQLException {
		return vesselDAOImpl.getSearchedVesselList(op);
	}	
	public List<Code> getSubCodeInfo(Code code_info) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Base.selectSubCodeInfo",code_info);
	}
	public List getVesselAbblListByPatten(String op) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectVesselAbbrListByPatten",op);
	}
	//vessel_abbr========================================
	public int getVesselAbbrCount() throws SQLException {
		return vesselDAOImpl.getVesselAbbrCount();
	}
	public Vessel getVesselAbbrInfo(String vesselName) throws SQLException {
		return (Vessel) sqlMap.queryForObject("BASE_VESSEL.selectVesselAbbrInfo",vesselName);
	}

	public List getVesselAbbrList(String vesselName) throws SQLException {
		return vesselDAOImpl.getVesselAbbrList(vesselName);
	}

	public List getVesselAbbrList(Vessel info) throws SQLException {
		return getVesselAbbrList(info);
	}

	//vessel===========================================
	public int getVesselCount() throws SQLException {
		return vesselDAOImpl.getCount();
	}

	public Vessel getVesselInfo(Vessel vessel) throws SQLException {
		return vesselDAOImpl.getVesselInfo(vessel);
	}
	
	public Vessel select(Vessel vessel) throws SQLException {
		return vesselDAOImpl.select(vessel);
	}

	public List getVesselListByPatten(String patten) throws SQLException {
		return vesselDAOImpl.getVesselListByPatten(patten);
	}

	public List getVesselListByPattenGroupByName(String string)throws SQLException {
		return vesselDAOImpl.getVesselListByPattenGroupByName(string);
	}

	public List getVesselListGroupByName(Vessel info) throws SQLException {
		return vesselDAOImpl.getVesselListGroupByName(info);
	}
	public Code hasCodeByField(String code_field) throws SQLException {
		
		return (Code) sqlMap.queryForObject("Base.selectCodeByField",code_field)
		;
	}

	public Object insertArea(AreaInfo info) throws SQLException {
		
		return areaDAOImpl.insert(info);
		
	}

	public Object insertCompany(Company info) throws SQLException {
		return companyDAOImpl.insert(info);
		
	}

	public void insertPort(PortInfo info) throws SQLException {
		sqlMap.insert("BASE_PORT.insertPort",info);
	}

	public Object insertVessel(Vessel vessel) throws SQLException {
		return vesselDAOImpl.insert(vessel);
	}
	public Object insertNewVessel(Vessel vessel) throws SQLException {
		return vesselDAOImpl.insertNew(vessel);
	}
	
	/**
	 * @deprecated
	 * @param map
	 * @param type
	 * @throws SQLException
	 */
	public void insertBaseInfo(HashMap<Object, Object> map,String type) throws SQLException {
		sqlMap.insert("Base.insert"+type,map);
		
	}

	public void insertCode(Code code_info) throws SQLException {
		sqlMap.insert("Base.insertCode",code_info);
	}

	public void insertKeyword(KeyWordInfo insert) throws SQLException {
		sqlMap.insert("Base.insertKeyword",insert);
	}

	public void insertPortAbbr(PortInfo info) throws SQLException {
		sqlMap.insert("Base.insertPort_Abbr",info);
	}
	public Object insertVesselAbbr(Vessel vesselAbbr) throws SQLException {
		return vesselDAOImpl.insertVesselAbbr(vesselAbbr);
	}

	public boolean isExitPort(String port_name) throws SQLException {
		String name =(String) sqlMap.queryForObject("BASE_PORT.selectPort_name",port_name);
		if(name!=null)
			return true;
		else
			return false;
	}


	public AreaInfo select(AreaInfo  area) throws SQLException {
		return areaDAOImpl.select(area);
	}
	
	public List selectADV() throws SQLException {
		return sqlMap.queryForList("Base.selectADV");
	}

	public List selectAreaList() throws SQLException {
		return areaDAOImpl.selectAreaList();
	}

	public List selectAreaSubList(AreaInfo info) throws SQLException {
		return areaDAOImpl.getAreaSubList(info);
	}

	public List selectList(Vessel info) throws SQLException {
		return vesselDAOImpl.selectList(info);
	}

	public Vessel selectVessel(Vessel vessel) throws SQLException {
		return vesselDAOImpl.select(vessel);
	}


	/**
	 * @설명 지역 정보 업데이트
	 * @param update
	 * @throws SQLException
	 */
	public int update(AreaInfo update) throws SQLException {
		return areaDAOImpl.update(update);
		
	}

	public void update(Code code) throws SQLException {
		sqlMap.update("Base.updateCode",code);
		
	}

	/**
	 * @param companyInfo
	 * @throws SQLException
	 */
	public int update(Company companyInfo) throws SQLException {
		return companyDAOImpl.update(companyInfo);
		
	}

	public void update(PortInfo portInfo) throws SQLException {
		sqlMap.update("BASE_PORT.updatePort",portInfo);
		
	}

	public int update(Vessel info) throws SQLException {
		return vesselDAOImpl.update(info);
	}

	public int updateBaseInfo(HashMap<Object, Object> map, String type)
			throws SQLException {
		return sqlMap.update("Base.update"+type,map);
		
	}

	public void updatePort_Abbr(PortInfo info) throws SQLException {
		sqlMap.update("BASE_PORT.updatePort_Abbr",info);
		
	}

	public void updatePortInfo(PortInfo t) throws SQLException {
		sqlMap.update("BASE_PORT.updatePortInfo",t);
		
	}

	public int updateVesselAbbr(Vessel vessel) throws SQLException {
		return vesselDAOImpl.updateVesselAbbr(vessel);		
	}



	public int deletePortAll() throws SQLException {
		return portDAOImpl.deletePortAll();
		
	}

}
