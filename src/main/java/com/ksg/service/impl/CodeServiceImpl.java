package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.dao.CodeDAO;
import com.ksg.domain.PortInfo;

/**

  * @FileName : CodeService.java

  * @Date : 2021. 3. 11. 

  * @작성자 : 박창현

  * @변경이력 : 

  * @프로그램 설명 : 코드 정보 관리

  */
public class CodeServiceImpl {
	
	CodeDAO codeDAO;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public CodeServiceImpl() {
		codeDAO = new CodeDAO();
	}
	
	public Map<String, Object> selectCodeHList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", codeDAO.selectCodeHCount(commandMap));
		
		resultMap.put("master", codeDAO.selectCodeHList(commandMap));
		
		return resultMap;

	}
	
	public Map<String, Object> selectCodeDList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", codeDAO.selectCodeCount(commandMap));
		
		resultMap.put("master", codeDAO.selectCodeDList(commandMap));
		
		return resultMap;

	}
	public Object selectCodeD(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return codeDAO.selectCodeD(commandMap);
	}

	public Object insertCodeH(HashMap<String, Object> param) throws SQLException{
		return codeDAO.insertCodeH(param);
		
	}

	public Object updateCodeH(HashMap<String, Object> param) throws SQLException{
		return codeDAO.updateCodeH(param);
		
	}

	public Object deleteCodeH(HashMap<String, Object> param)throws SQLException {
		return codeDAO.deleteCodeH(param);
		
	}

	public Object insertCodeD(HashMap<String, Object> param) throws SQLException {
		return codeDAO.insertCodeD(param);
		
	}

	public Object deleteCodeD(HashMap<String, Object> param) throws SQLException {
		return codeDAO.deleteCodeD(param);
		
	}
	
	public PortInfo getPortInfoAbbrByPortName(String port) throws SQLException {
		return codeDAO.getPortInfoAbbrByPortName(port);
	}

}
