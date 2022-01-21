package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.dao.impl.PortDAOImpl;
import com.ksg.service.PortService;

/**

  * @FileName : PortServiceImpl.java

  * @Project : KSG2

  * @Date : 2021. 12. 7. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class PortServiceImpl implements PortService{
	
	PortDAOImpl portDAO;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public PortServiceImpl() {
		portDAO = new PortDAOImpl();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectList(Map<String, Object> param) throws SQLException {
		
		logger.info("param:{}", param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", portDAO.selectPortCount(param));
		
		resultMap.put("master", portDAO.selectPortList(param));
		
		return resultMap;

	}

	public int deletePortAbbr(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}", param);
		return portDAO.deletePortAbbr(param);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> selectPort(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}", param);
		
		HashMap<String,Object> resultMap=(HashMap<String, Object>) portDAO.selectPort( param);
		
		if(resultMap==null)
		{
			resultMap = (HashMap<String, Object>) portDAO.selectPortAbbr(param);
		}		
		
		return resultMap;
	}

	public int update(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}", param);
		return portDAO.updatePort(param);
		
	}

	public void insert(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}", param);
		portDAO.isnertPort(param);
		
	}

	public int delete(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}", param);
		return portDAO.deletePort(param);
	}

	@Override
	public Object selectPortAbbr(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}", param);
		return portDAO.selectPortAbbr(param);
	}

	@Override
	public List<Map<String, Object>> selectPortAbbrList(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}", param);
		return portDAO.selectPortAbbrList(param);
	}


	
	

}
