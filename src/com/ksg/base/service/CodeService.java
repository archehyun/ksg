package com.ksg.base.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ksg.base.dao.CodeDAO;

/**

  * @FileName : CodeService.java

  * @Date : 2021. 3. 11. 

  * @작성자 : 박창현

  * @변경이력 : 

  * @프로그램 설명 : 코드 정보 관리

  */
public class CodeService {
	
	CodeDAO codeDAO;
	
	public CodeService() {
		codeDAO = new CodeDAO();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectCodeHList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", codeDAO.selectCodeCount(commandMap));
		
		resultMap.put("master", codeDAO.selectCodeHList(commandMap));
		
		return resultMap;

	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectCodeDList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", codeDAO.selectCodeCount(commandMap));
		
		resultMap.put("master", codeDAO.selectCodeDList(commandMap));
		
		return resultMap;

	}

}
