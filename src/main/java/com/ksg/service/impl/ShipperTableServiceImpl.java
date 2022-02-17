package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ksg.dao.TableDAO;
import com.ksg.dao.impl.ShipperTableDAOImpl;
import com.ksg.service.ShipperTableService;

/**

 * @FileName : ShipperTableService.java

 * @Date : 2021. 3. 9. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
public class ShipperTableServiceImpl implements ShipperTableService{



	private TableDAO tableDAO;

	private ShipperTableDAOImpl shipperTableDao;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public ShipperTableServiceImpl() {
		shipperTableDao = new ShipperTableDAOImpl();
	}

	public Map<String, Object> selectList(Map<String, Object> param)
	{
		logger.info("param:{}",param);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {

			resultMap.put("total", shipperTableDao.selectCount(null));

			resultMap.put("master", shipperTableDao.selectList(param));

		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return resultMap;
	}




	@SuppressWarnings("unchecked")
	public Map<String, Object> selectPortList(Map<String, Object> param) {
		logger.info("param:{}",param);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {

			resultMap.put("total", 0);

			resultMap.put("master", shipperTableDao.selectPortList(param));

		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return resultMap;

	}

	public void deleteShipperPortList(HashMap<String, Object> param) throws SQLException
	{
		logger.info("param:{}",param);
		shipperTableDao.deleteShipperPortList(param);
	}

	public void insertShipperPort(HashMap<String, Object> param) throws SQLException
	{
		logger.info("param:{}",param);
		shipperTableDao.insertShipperPort(param);

	}

	public void saveShipperPort(HashMap<String, Object> param) throws Exception{

		logger.info("param:{}",param);

		deleteShipperPortList(param);	

		List<HashMap<String, Object>> master  = (List) param.get("master");

		String table_id = (String) param.get("table_id");

		for(int i=0;i<master.size();i++)
		{
			HashMap<String, Object> port = master.get(i);
			port.put("table_id", table_id);

			insertShipperPort(port);			

		}		


	}

	@Override
	public void delete(Map<String, Object> param) {
		
		
	}

	@Override
	public void update(Map<String, Object> param) {
		
		
	}

	@Override
	public void insert(Map<String, Object> param) {
		try {
			shipperTableDao.update(param);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
