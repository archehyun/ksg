package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.dao.impl.CodeDAOImpl;

/**

  * @FileName : CodeService.java

  * @Date : 2021. 3. 11. 

  * @작성자 : 박창현

  * @변경이력 : 

  * @프로그램 설명 : 코드 정보 관리

  */
public class CodeServiceImpl {
	
	CodeDAOImpl codeDAO;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public CodeServiceImpl() {
		codeDAO = new CodeDAOImpl();
	}
	
	public Map<String, Object> selectCodeHList(Map<String, Object> param) throws SQLException {
		
		logger.debug("param:{}"+param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", codeDAO.selectCount(param));
		
		resultMap.put("master", codeDAO.selectList(param));
		
		return resultMap;

	}
	
	public Map<String, Object> selectCodeDList(Map<String, Object> param) throws SQLException {
		logger.debug("param:{}"+param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", codeDAO.selectDetailCount(param));
		
		resultMap.put("master", codeDAO.selectDetailList(param));
		
		return resultMap;

	}
	public Object selectCodeH(Map<String, Object> param) throws SQLException {
		logger.debug("param:{}"+param);
		return codeDAO.select(param);
	}
	public Object selectCodeD(Map<String, Object> param) throws SQLException {
		logger.debug("param:{}"+param);
		return codeDAO.selectDetail(param);
	}

	public Object insertCodeH(HashMap<String, Object> param) throws SQLException{
		logger.debug("param:{}"+param);
		return codeDAO.insert(param);
		
	}

	public Object updateCodeH(HashMap<String, Object> param) throws SQLException{
		logger.debug("param:{}"+param);
		return codeDAO.update(param);
		
	}

	public Object deleteCodeH(HashMap<String, Object> param)throws SQLException {
		logger.debug("param:{}"+param);
		return codeDAO.delete(param);
		
	}

	public Object insertCodeD(HashMap<String, Object> param) throws SQLException {
		logger.debug("param:{}"+param);
		return codeDAO.insertDetail(param);
		
	}

	public Object deleteCodeD(HashMap<String, Object> param) throws SQLException {
		logger.debug("param:{}"+param);
		return codeDAO.deleteDetail(param);
		
	}

}
