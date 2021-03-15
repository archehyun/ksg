package com.ksg.shippertable.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.shippertable.dao.ShipperTableDAO;

/**

 * @FileName : ShipperTableService.java

 * @Date : 2021. 3. 9. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
public class ShipperTableService {

	ShipperTableDAO shipperTableDao;


	public ShipperTableService() {
		shipperTableDao = new ShipperTableDAO();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> selectPortList(Map<String, Object> commandMap) {

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
		System.out.println("insert port:"+commandMap);
	}

	public void saveShipperPort(HashMap<String, Object> commandMap) {


		System.out.println("save port");


		try
		{
			deleteShipperPortList(commandMap);	

			List<HashMap<String, Object>> master  = (List) commandMap.get("master");

			for(int i=0;i<master.size();i++)
			{
				HashMap<String, Object> port = master.get(i);

				insertShipperPort(port);

				System.out.println(port);

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		



	}
}
