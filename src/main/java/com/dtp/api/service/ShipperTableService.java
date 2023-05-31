package com.dtp.api.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.ShippersTable;

/**
 * 

  * @FileName : ShipperTableService.java

  * @Project : KSG2

  * @Date : 2023. 5. 31. 

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� :
 */
public interface ShipperTableService {
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public List selectTableListAndPortListByCondition(ShippersTable param) throws SQLException;
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public List selectTableListByCondition(ShippersTable param) throws SQLException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public List selectShipperTablePortListByID(String id) throws SQLException;
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> selectTableAll() throws SQLException;

}
