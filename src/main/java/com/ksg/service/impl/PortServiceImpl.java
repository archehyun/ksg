package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.exception.AlreadyExistException;
import com.ksg.dao.PortDAO;
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
	
	PortDAO portDAO;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public PortServiceImpl() {
		portDAO = new PortDAOImpl();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectList(Map<String, Object> param) throws SQLException {
		
		logger.debug("param:{}", param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", portDAO.selectCount(param));
		
		resultMap.put("master", portDAO.selectList(param));
		
		return resultMap;

	}

	public int deletePortAbbr(HashMap<String, Object> param) throws SQLException {
		logger.debug("param:{}", param);
		return portDAO.deleteDetail(param);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> selectPort(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}", param);
		
		HashMap<String,Object> resultMap=(HashMap<String, Object>) portDAO.select( param);
		
		if(resultMap==null)
		{
			resultMap = (HashMap<String, Object>) portDAO.selectDetail(param);
		}		
		
		return resultMap;
	}

	public int update(HashMap<String, Object> param) throws SQLException {
		logger.debug("param:{}", param);
		return portDAO.update(param);
		
	}

	public Object insert(HashMap<String, Object> param) throws Exception {
		
		
		try {
			
			logger.debug("param:{}", param);
			return portDAO.isnert(param);
		}
			catch (SQLException e1) {
			
			if(e1.getErrorCode()==2627)
			{
				throw new AlreadyExistException();
				
			}else
			{
				throw new SQLException(e1.getMessage());
			}
		}
		
		
		
		
	}

	public int delete(HashMap<String, Object> param) throws SQLException {
		logger.debug("param:{}", param);
		return portDAO.delete(param);
	}

	@Override
	public Object selectPortAbbr(HashMap<String, Object> param) throws SQLException {
		logger.debug("param:{}", param);
		return portDAO.selectDetail(param);
	}

	@Override
	public List<Map<String, Object>> selectPortAbbrList(HashMap<String, Object> param) throws SQLException {
		logger.debug("param:{}", param);
		return portDAO.selectDetailList(param);
	}

	@Override
	public HashMap<String, Object> selectListByPage(HashMap<String, Object> param) throws SQLException {
	
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", portDAO.selectCount(param));
		
		resultMap.put("master", portDAO.selectListByPage(param));
		
		return resultMap;
	}


	
	

}
