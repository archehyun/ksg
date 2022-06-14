package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.model.CommandMap;
import com.ksg.dao.impl.CodeDAOImpl;

import lombok.extern.slf4j.Slf4j;

/**

  * @FileName : CodeService.java

  * @Date : 2021. 3. 11. 

  * @작성자 : 박창현

  * @변경이력 : 

  * @프로그램 설명 : 코드 정보 관리

  */
@Slf4j
public class CodeServiceImpl extends AbstractServiceImpl{
	
	private CodeDAOImpl codeDAO;
	
	
	public CodeServiceImpl() {
		
		super();
		codeDAO = new CodeDAOImpl();
	}
	
	public Map<String, Object> selectCodeHList(Map<String, Object> param) throws SQLException {
		
		log.debug("param:{}"+param);
		
		CommandMap resultMap = new CommandMap();
		
		resultMap.put("total", codeDAO.selectCount(param));
		
		resultMap.put("master", codeDAO.selectList(param));
		
		return resultMap;

	}
	
	public CommandMap selectCodeDList(Map<String, Object> param) throws SQLException {
		log.debug("param:{}"+param);
		
		CommandMap resultMap = new CommandMap();
		
		resultMap.put("total", codeDAO.selectDetailCount(param));
		
		resultMap.put("master", codeDAO.selectDetailList(param));
		
		return resultMap;

	}
	
	public CommandMap selectInboundPortMap() throws SQLException
	{
		CommandMap param = new CommandMap();
		param.put("code_type", "inPort");
		
		List<Map<String, Object>> list =codeDAO.selectDetailList(param);
		
		CommandMap result = new CommandMap();
		for (Map<String, Object> item : list) {
		    result.put(String.valueOf(item.get("code_field")), item.get("code_name"));
		}
		return result;
	}
	public Object selectCodeH(Map<String, Object> param) throws SQLException {
		log.debug("param:{}"+param);
		return codeDAO.select(param);
	}
	public Object selectCodeD(Map<String, Object> param) throws SQLException {
		log.debug("param:{}"+param);
		return codeDAO.selectDetail(param);
	}

	public Object insertCodeH(HashMap<String, Object> param) throws SQLException{
		log.debug("param:{}"+param);
		return codeDAO.insert(param);
		
	}

	public Object updateCodeH(HashMap<String, Object> param) throws SQLException{
		log.debug("param:{}"+param);
		return codeDAO.update(param);
		
	}

	public Object deleteCodeH(HashMap<String, Object> param)throws SQLException {
		log.debug("param:{}"+param);
		return codeDAO.delete(param);
		
	}

	public Object insertCodeD(HashMap<String, Object> param) throws SQLException {
		log.debug("param:{}"+param);
		return codeDAO.insertDetail(param);
		
	}

	public Object deleteCodeD(HashMap<String, Object> param) throws SQLException {
		log.debug("param:{}"+param);
		return codeDAO.deleteDetail(param);
		
	}

}
