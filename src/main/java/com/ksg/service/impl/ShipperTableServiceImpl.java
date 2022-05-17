package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ksg.common.exception.AlreadyExistException;
import com.ksg.dao.impl.ShipperTableDAOImpl;
import com.ksg.service.ShipperTableService;

import lombok.extern.slf4j.Slf4j;

/**

 * @FileName : ShipperTableService.java

 * @Date : 2021. 3. 9. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
@Slf4j
public class ShipperTableServiceImpl extends AbstractServiceImpl implements ShipperTableService{


	private ShipperTableDAOImpl shipperTableDao;

	public ShipperTableServiceImpl() {
		super();
		shipperTableDao = new ShipperTableDAOImpl();
	}

	public Map<String, Object> selectList(Map<String, Object> param)
	{
		log.info("param:{}",param);
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
		log.info("param:{}",param);
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
		log.info("param:{}",param);
		shipperTableDao.deleteShipperPortList(param);
	}

	public void insertShipperPort(HashMap<String, Object> param) throws SQLException
	{
		log.info("param:{}",param);
		shipperTableDao.insertShipperPort(param);

	}

	public void saveShipperPort(HashMap<String, Object> param) throws Exception{

		log.info("param:{}",param);

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
	public int delete(Map<String, Object> param) throws Exception {
		log.info("param:{}",param);
		return shipperTableDao.delete(param);
	}

	@Override
	public void update(Map<String, Object> param) {
		
		
	}
	
	@Override
	public int updateTableDateByTableIDs(List table,String updateDate) throws SQLException {
		log.info("update by "+updateDate+", "+table);
		Iterator<HashMap<String, Object>> iter = table.iterator();
		int count=0;
		while(iter.hasNext())
		{
			HashMap<String, Object> item = iter.next();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("table_id", item.get("table_id"));
			param.put("date_isusse", updateDate);
			
			shipperTableDao.updateDate(param);
			count++;
		}
		
		return count;
	}

	

	@Override
	public void insert(Map<String, Object> param) throws Exception{
		log.info("param:{}",param);
		try {
			
		Object result=	shipperTableDao.insert(param);
		
		log.info("reslult:{}",result);
			
		} catch (SQLException e) {
			if(e.getErrorCode()==2627)
			{
				throw new AlreadyExistException("exist");
			}
		}
		
	}

	@Override
	public HashMap<String, Object> selectListByPage(HashMap<String, Object> param) throws SQLException {
	
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", shipperTableDao.selectCount(param));
		
		resultMap.put("master",shipperTableDao.selectListByPage(param));
		
		return resultMap;
	}

}
