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

	public Map<String, Object> selectList(Map<String, Object> commandMap)
	{
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {

			resultMap.put("total", 0);

			resultMap.put("master", shipperTableDao.selectList(commandMap));

		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return resultMap;
	}




	@SuppressWarnings("unchecked")
	public Map<String, Object> selectPortList(Map<String, Object> commandMap) {
		logger.info("param:"+ commandMap);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {

			resultMap.put("total", 0);

			resultMap.put("master", shipperTableDao.selectPortList(commandMap));

		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return resultMap;

	}

	public void deleteShipperPortList(HashMap<String, Object> commandMap) throws SQLException
	{
		System.out.println("delete portList");
		shipperTableDao.deleteShipperPortList(commandMap);
	}

	public void insertShipperPort(HashMap<String, Object> commandMap) throws SQLException
	{
		shipperTableDao.insertShipperPort(commandMap);

	}

	public void saveShipperPort(HashMap<String, Object> commandMap) throws Exception{

		logger.debug("param:"+commandMap);



		deleteShipperPortList(commandMap);	

		List<HashMap<String, Object>> master  = (List) commandMap.get("master");

		String table_id = (String) commandMap.get("table_id");

		for(int i=0;i<master.size();i++)
		{
			HashMap<String, Object> port = master.get(i);
			port.put("table_id", table_id);

			insertShipperPort(port);

			System.out.println(port);

		}

		logger.debug("end:");


	}
}
