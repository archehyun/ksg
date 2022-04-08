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
package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ksg.domain.AreaInfo;
import com.ksg.domain.BaseInfo;
import com.ksg.domain.Code;
import com.ksg.domain.Company;
import com.ksg.domain.KeyWordInfo;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Vessel;


@Deprecated
public interface BaseDAO {
	/**
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public List getBaseInfo(String type) throws SQLException;
	/**
	 * @param map
	 * @param type
	 * @throws SQLException
	 */
	public void insertBaseInfo(HashMap<Object, Object> map,String type) throws SQLException;
	/**
	 * @param baseInfo
	 * @throws SQLException
	 */
	public void delBaseInfo(BaseInfo baseInfo) throws SQLException;
	/**
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public List getFieldInfo(String type) throws SQLException;
	/**
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public List getCodeInfo(String type) throws SQLException;
	/**
	 * @param port_name
	 * @return
	 * @throws SQLException
	 */
	public boolean isExitPort(String port_name)throws SQLException;
	
	/**
	 * @param map
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public int updateBaseInfo(HashMap<Object, Object> map, String type)throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getCodeTypeList()throws SQLException;
	public List<Code> getCodeInfoByType(String code_type)throws SQLException;
	public List getPortListByPatten(String patten)throws SQLException;
	public PortInfo getPortInfoByPortNamePatten(String port)throws SQLException;
	public PortInfo getPortInfoAbbrByPortName(String port)throws SQLException;
	public Company getCompanyInfo(String company_abbr)throws SQLException;
	public AreaInfo getAreaInfo(AreaInfo area)throws SQLException;
//	public Vessel selectVessel(String vessel)throws SQLException;
	
	public List getPortInfoList()throws SQLException;
	public List getAreaInfoList()throws SQLException;	
	public List getCompanyList()throws SQLException;
	public List getPageList()throws SQLException;
	public List getPor_AbbrList()throws SQLException;
	public List getVesselList(Vessel info)throws SQLException;
	public List getAreaGroupList()throws SQLException;
	public List getAreaSubList(AreaInfo info)throws SQLException;
	public List selectADV()throws SQLException;
	
	public void insertPortInfo(PortInfo info)throws SQLException;
	public void insertCompany(Company info)throws SQLException;
	
	public void insertAreaInfo(AreaInfo insert)throws SQLException;
	public void updateAreaInfo(AreaInfo update)throws SQLException;
	public void updatePort_Abbr(PortInfo info)throws SQLException;
	public Object insertVessel(Vessel vessel)throws SQLException;
	
	
	public int deleteArea(String data)throws SQLException;
	public int deletePort(String data)throws SQLException;
	public int deletePort_Abbr(String data)throws SQLException;
	public int deleteCompany(String data)throws SQLException;
	public int deleteVessel(String data)throws SQLException;
	
	public void updateCompany(Company info)throws SQLException;
	public void updateVessel(Vessel info)throws SQLException;
	public void updatePort(PortInfo info)throws SQLException;
	public void updatePortInfo(PortInfo t)throws SQLException;
	
	
	

	public List getFieldNameList(String table_name) throws SQLException;
	
	
	public List getArrangedPortInfoList(String orderBy)throws SQLException;
	public List getArrangedAreaInfoList(String orderBy)throws SQLException;
	public List getArrangedPort_AbbrList(Object orderBy)throws SQLException;
	public List getArrangedCompanyList(Object orderBy)throws SQLException;
	public List getArrangedTableList(Object orderBy)throws SQLException;
	public List getSearchedAreaList(String searchKeyword)throws SQLException;
	
	public List getSearchedPortList(String searchKeyword) throws SQLException ;
	public List getSearchedPort_AbbrList(String searchKeyword) throws SQLException ;
	public List getSearchedCompanyList(String searchKeyword) throws SQLException;
	public List getSearchedVesselList(String searchKeyword) throws SQLException ;
	public List getKeywordList(String searchKeywordType)throws SQLException ;
	public void deleteKeyword(Object selectedValue)throws SQLException ;
	public void insertKeyword(KeyWordInfo insert)throws SQLException;
	public List getVesselListByPatten(String value)throws SQLException;
	public List getArrangedAreaInfoList(String orderBy, String type)throws SQLException;
	
	public int getVesselCount()throws SQLException;
	public int getCompanyCount()throws SQLException;
	public int getAreaCount()throws SQLException;
	public int getPortCount()throws SQLException;
	public int getPort_AbbrCount()throws SQLException;
	
	//<=====Code====>
	public List<Code> getCodeInfoList(Code code_info) throws SQLException;
	public List<Code> getSubCodeInfo(Code code_info) throws SQLException;
	public Object hasCodeByField(String info) throws SQLException;
	public void insertCode(Code code_info) throws SQLException;
	public int deleteCode(Code code_info)throws SQLException;
	public Code getCodeInfo(Code code_info)throws SQLException;
	public void updateCode(Code code)throws SQLException;
	public PortInfo getPortInfoByPortName(String portName)throws SQLException;
	public List getAreaInfoList(AreaInfo info)throws SQLException;
	
	public Object insertVessel_Abbr(Vessel vesselAbbr)throws SQLException;
	public int getVesselAbbrCount()throws SQLException;
	public List getArrangedVesselList(Object orderBy)throws SQLException;
	public List getArrangedVesselAbbrList(Object orderBy)throws SQLException;
	public List getSearchedVesselAbbrList(String orderBy)throws SQLException;
	public List getVesselAbbrList(String vesselName)throws SQLException;
	public List getVesselAbbrList(Vessel info)throws SQLException;
	public int deleteVesselAbbr(Vessel op)throws SQLException;
	public Vessel selectVessel(Vessel vessel)throws SQLException;
	public Vessel getVesselAbbrInfo(String vesselName)throws SQLException;
	public List getVesselAbblListByPatten(String op)throws SQLException;
	public List getVesselListGroupByName(Vessel info)throws SQLException;
	public void updateVesselAbbr(Vessel vessel)throws SQLException;
	public List getVesselListByPattenGroupByName(String string)throws SQLException;
	public Vessel getVesselInfoItem(Vessel vessel)throws SQLException;
	public void deleteVessel()throws SQLException;
	public void insertPort_Abbr(PortInfo info)throws SQLException;
	public List getSearchedVesselList(Vessel op)throws SQLException;
	public List getAreaListGroupByAreaCode()throws SQLException;
	public List getAreaListGroupByAreaName()throws SQLException;
	public List getSearchedPortList(PortInfo option)throws SQLException;
	public List getSearchedCompanyList(Company company)throws SQLException;
	
	

}

