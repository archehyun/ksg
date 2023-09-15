package com.dtp.api.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;

/**
 * 

  * @FileName : ShipperTableService.java

  * @Project : KSG2

  * @Date : 2023. 5. 31. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :
 */
public interface ShipperTableService {
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> selectTableAll() throws SQLException;
	
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
	
	public List selectShipperTableListAllByCondition(ShippersTable param) throws SQLException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public List<TablePort> selectShipperTablePortListByID(String id) throws SQLException;
	
	

	public void insert(ShippersTable target) throws Exception;

	public ShippersTable selectShipperTableById(String table_id) throws SQLException;

	public void delete(String table_id) throws Exception;

	public void update(ShippersTable target) throws Exception;
	
	public int updateTableDateByTableIDs(List<String> table, String updateDate) throws SQLException;

	public ADVData selectADVDataById(String table_id) throws SQLException;

	public void updateTableData(ADVData param) throws SQLException;
	
	public void save(ADVData param)throws Exception;
	
	public void saveTablePort(String table_id, List<TablePort> param)throws Exception;

	public ShippersTable selectShipperTableAllById(String table_id) throws SQLException;


}
