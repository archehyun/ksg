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
package com.ksg.dao.base;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.dao.SqlMapManager;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.BaseInfo;
import com.ksg.domain.Code;
import com.ksg.domain.Company;
import com.ksg.domain.KeyWordInfo;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Vessel;
@SuppressWarnings("unchecked")
public class BaseDAOImpl implements BaseDAO
{
	private SqlMapClient sqlMap;

	public BaseDAOImpl() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void delBaseInfo(BaseInfo baseInfo) throws SQLException {
		
	}

	
	public int deleteArea(String data) throws SQLException {
		return sqlMap.delete("BASE_AREA.deleteArea",data);
		
	}

	
	public int deleteCode(Code code_info) throws SQLException {
		return sqlMap.delete("Base.deleteCode",code_info);
		
	}
	public int deleteCompany(String data) throws SQLException {
		return sqlMap.delete("BASE_COMPANY.deleteCompany",data);
		
	}

	
	public void deleteKeyword(Object key_name) throws SQLException {
		sqlMap.delete("Base.deleteKeyword",key_name);
	}


	
	public int deletePort(String data) throws SQLException {
		return sqlMap.delete("BASE_PORT.deletePort",data);
		
	}


	
	public int deletePort_Abbr(String data) throws SQLException {
		return sqlMap.delete("BASE_PORT.deletePort_Abbr",data);
		
	}


	


	
	public int getAreaCount() throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) sqlMap.queryForObject("BASE_AREA.selectAreaCount");
	}
	



	public List getAreaGroupList() throws SQLException {
		return sqlMap.queryForList("BASE_AREA.selectAreaGroupList");
	}



	public AreaInfo getAreaInfo(AreaInfo  area) throws SQLException {
		return (AreaInfo) sqlMap.queryForObject("BASE_AREA.selectAreaInfo",area);
	}


	public List getAreaInfoList() throws SQLException {
		return sqlMap.queryForList("BASE_AREA.selectAreaInfoList");
	}



	public List getAreaSubList(AreaInfo info) throws SQLException {
		return sqlMap.queryForList("BASE_AREA.selectAreaSubList",info);
	}



	public List getArrangedAreaInfoList(String orderBy) throws SQLException {
		AreaInfo areaInfo = new AreaInfo();
		areaInfo.setOrderBy(orderBy);
		return sqlMap.queryForList("BASE_AREA.selectArrangedAreaList",areaInfo);
	}



	public List getArrangedAreaInfoList(String orderBy, String type)
			throws SQLException {
		AreaInfo areaInfo = new AreaInfo();
		areaInfo.setOrderBy(orderBy);
		areaInfo.setArea_type(type);
		return sqlMap.queryForList("BASE_AREA.selectArrangedAreaList",areaInfo);
	}



	public List getArrangedCompanyList(Object orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("BASE_COMPANY.selectArrangedCompanyList",orderBy);
	}



	public List getArrangedPort_AbbrList(Object orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("BASE_PORT.selectArrangedPort_AbbrList",orderBy);
	}



	public List getArrangedPortInfoList(String orderBy) throws SQLException {

		return sqlMap.queryForList("BASE_PORT.selectArrangedPortList",orderBy);
	}

	public List getArrangedTableList(Object orderBy) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Base.selectArrangedTableList",orderBy);
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
		// TODO Auto-generated method stub
		return (Integer) sqlMap.queryForObject("BASE_COMPANY.selectCompanyCount");
	}

	public Company getCompanyInfo(String company_abbr) throws SQLException {
		return (Company) sqlMap.queryForObject("BASE_COMPANY.selectCompanyInfo",company_abbr);
	}

	public List getCompanyList() throws SQLException {
		return sqlMap.queryForList("BASE_COMPANY.selectCompanyList");
	}
	public List getCompanyList(Company company) throws SQLException {
		return sqlMap.queryForList("BASE_COMPANY.selectCompanyListOrderby",company);
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

	public List getPor_AbbrList() throws SQLException {
		return sqlMap.queryForList("Base.selectPort_AbbrList");
	}

	public int getPort_AbbrCount() throws SQLException {
		return (Integer) sqlMap.queryForObject("BASE_PORT.selectPort_AbbrCount");
	}

	public int getPortCount() throws SQLException {
		return (Integer) sqlMap.queryForObject("BASE_PORT.selectPortCount");
	}

	public PortInfo getPortInfoAbbrByPortName(String port) throws SQLException {
		return (PortInfo) sqlMap.queryForObject("Base.selectPortInfoAbbrByPortName",port);
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

	public List getSearchedCompanyList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_COMPANY.selectSearchedCompanyList",searchKeyword);
	}

	public List getSearchedPort_AbbrList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_PORT.selectSearchedPort_AbbrList",searchKeyword);
	}


	public List getSearchedPortList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_PORT.selectSearchedPortList",searchKeyword);
	}

	public List<Code> getSubCodeInfo(Code code_info) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Base.selectSubCodeInfo",code_info);
	}
	
	
	
	//vessel===========================================
	public int getVesselCount() throws SQLException {
		return (Integer) sqlMap.queryForObject("BASE_VESSEL.selectVesselCount");
	}
	public List getVesselList(Vessel info) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectVesselList", info);
	}

	public List getVesselListByPatten(String patten) throws SQLException {
		return sqlMap.queryForList("Base.selectVesselListByPatten",patten);
	}
	public int deleteVessel(String data) throws SQLException {
		return sqlMap.delete("BASE_VESSEL.deleteVessel",data);
	}
	public List getSearchedVesselList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectSearchedVesselList",searchKeyword);
	}
	public Object insertVessel(Vessel vessel) throws SQLException {
		return sqlMap.insert("BASE_VESSEL.insertVessel",vessel);
	}
	public void updateVessel(Vessel info) throws SQLException {
		sqlMap.update("BASE_VESSEL.updateVessel",info);
	}
	public Object insertVessel_Abbr(Vessel vesselAbbr) throws SQLException {
		return sqlMap.insert("BASE_VESSEL.insertVesselAbbr",vesselAbbr);
	}
	public List getArrangedVesselList(Object orderBy) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectArrangedVesselAbbrList",orderBy);
	}
	public Vessel selectVessel(Vessel vessel) throws SQLException {
		return (Vessel) sqlMap.queryForObject("BASE_VESSEL.selectVesselInfo",vessel);
	}	
	public List getVesselListGroupByName(Vessel info) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectVesselListGroupByName", info);
	}
	public List getVesselListByPattenGroupByName(String string)throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectVesselListByPattenGroupBynName",string);
	}
	public Vessel getVesselInfoItem(Vessel vessel) throws SQLException {
		return (Vessel) sqlMap.queryForObject("BASE_VESSEL.selectVesselInfoItem",vessel);
	}
	public void deleteVessel() throws SQLException {
		sqlMap.delete("BASE_VESSEL.deleteVesselAll");
	}
	//vessel===========================================end

	//vessel_abbr========================================
	public int getVesselAbbrCount() throws SQLException {
		return (Integer) sqlMap.queryForObject("BASE_VESSEL.selectVesselAbbrCount");
	}

	public List getArrangedVesselAbbrList(Object orderBy) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectArrangedVesselList",orderBy);
	}



	public List getSearchedVesselAbbrList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectSearchedVesselAbbrList",searchKeyword);
	}

	public List getVesselAbbrList(String vesselName) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectVesselAbbrInfo",vesselName);
	}

	public List getVesselAbbrList(Vessel info) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectVesselAbbrList", info);
	}

	public int deleteVesselAbbr(Vessel op) throws SQLException {
		return sqlMap.delete("BASE_VESSEL.deleteVesselAbbr",op);
	}

	public Vessel getVesselAbbrInfo(String vesselName) throws SQLException {
		return (Vessel) sqlMap.queryForObject("BASE_VESSEL.selectVesselAbbrInfo",vesselName);
	}
	public List getVesselAbblListByPatten(String op) throws SQLException {
		return sqlMap.queryForList("BASE_VESSEL.selectVesselAbbrListByPatten",op);
	}



	public void updateVesselAbbr(Vessel vessel) throws SQLException {
		sqlMap.update("BASE_VESSEL.updateVesselAbbr",vessel);
	}


	

	public Code hasCodeByField(String code_field) throws SQLException {
		
		return (Code) sqlMap.queryForObject("Base.selectCodeByField",code_field)
		;
	}

	public void insertAreaInfo(AreaInfo info) throws SQLException {
		
		sqlMap.insert("BASE_AREA.insertArea",info);
	}

	public void insertBaseInfo(HashMap<Object, Object> map,String type) throws SQLException {
		sqlMap.insert("Base.insert"+type,map);
		
	}
	
	public void insertCode(Code code_info) throws SQLException {
		sqlMap.insert("Base.insertCode",code_info);
		
	}

	public void insertCompany(Company info) throws SQLException {
		sqlMap.insert("BASE_COMPANY.insertCompany",info);
		
	}

	public void insertKeyword(KeyWordInfo insert) throws SQLException {
		sqlMap.insert("Base.insertKeyword",insert);
	}

	public void insertPortInfo(PortInfo info) throws SQLException {
		sqlMap.insert("BASE_PORT.insertPort",info);
	}



	public boolean isExitPort(String port_name) throws SQLException {
		String name =(String) sqlMap.queryForObject("BASE_PORT.selectPort_name",port_name);
		if(name!=null)
			return true;
		else
			return false;
			
		
	}

	public List selectADV() throws SQLException {
		return sqlMap.queryForList("Base.selectADV");
	}


	public void updateAreaInfo(AreaInfo update) throws SQLException {
		sqlMap.update("BASE_AREA.updateArea",update);
		
	}
	
	public int updateBaseInfo(HashMap<Object, Object> map, String type)
			throws SQLException {
		return sqlMap.update("Base.update"+type,map);
		
	}



	public void updateCompany(Company info) throws SQLException {
		sqlMap.update("BASE_COMPANY.updateCompany",info);
		
	}

	public void updatePort(PortInfo info) throws SQLException {
		sqlMap.update("BASE_PORT.updatePort",info);
		
	}

	public void updatePort_Abbr(PortInfo info) throws SQLException {
		sqlMap.update("BASE_PORT.updatePort_Abbr",info);
		
	}

	public void updatePortInfo(PortInfo t) throws SQLException {
		sqlMap.update("BASE_PORT.updatePortInfo",t);
		
	}


	public void updateCode(Code code) throws SQLException {
		sqlMap.update("Base.updateCode",code);
		
	}

	public PortInfo getPortInfoByPortName(String portName) throws SQLException {
		return (PortInfo) sqlMap.queryForObject("BASE_PORT.selectPortInfoByPortName",portName);
	}

	public List getAreaInfoList(AreaInfo info) throws SQLException {
		
		return sqlMap.queryForList("BASE_AREA.selectAreaInfoListOrderBy",info);
	}



	public void insertPort_Abbr(PortInfo info) throws SQLException {
		sqlMap.insert("Base.insertPort_Abbr",info);
	}

	@Override
	public List getSearchedVesselList(Vessel op) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("BASE_VESSEL.selectSearchedVesselList",op);
	}

	@Override
	public List getAreaListGroupByAreaCode() throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("BASE_AREA.selectAreaCodeListGroupByAreaCode");
	}

	@Override
	public List getAreaListGroupByAreaName() throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("BASE_AREA.selectAreaListGroupByAreaName");
	}

	@Override
	public List getSearchedPortList(PortInfo option) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("BASE_PORT.selectSearchedPortList",option);
	}

	@Override
	public List getSearchedCompanyList(Company company) throws SQLException {
		return sqlMap.queryForList("BASE_COMPANY.selectSearchedCompanyListOrderby",company);
	}

	
	
	
	


}
