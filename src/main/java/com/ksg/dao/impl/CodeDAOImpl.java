package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.dao.CodeDAO;

/**

 * @FileName : CodeDAO.java

 * @Date : 2021. 3. 5. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 코드 정보 조회 DAO

 */
public class CodeDAOImpl extends AbstractDAO implements CodeDAO{
	public CodeDAOImpl() {
		super();
		
		this.namespace ="code";
	}

	@Override
	public Object select(Map<String, Object> commandMap) throws SQLException {
		return  selectOne(namespace+".selectCodeH", commandMap);
	}


	@Override
	public Object selectDetail(Map<String, Object> commandMap) throws SQLException {
		return  selectOne(namespace+".selectCodeD", commandMap);
	}



	@Override
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {
		return selectList(namespace+".selectCodeHList", commandMap);

	}

	@Override
	public List<Map<String, Object>> selectDetailList(Map<String, Object> commandMap) throws SQLException {
		return selectList(namespace+".selectCodeDList", commandMap);

	}
	
	@Override
	public Object selectCount(Map<String, Object> commandMap)throws SQLException {
		return  (Integer) selectOne(namespace+".selectCountH", commandMap);
	}

	@Override
	public int selectDetailCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne(namespace+".selectCount", commandMap);
	}	

	@Override
	public Object insert(HashMap<String, Object> param) throws SQLException{
		return insert(namespace+".insertCodeH",param);

	}
	
	@Override
	public Object insertDetail(HashMap<String, Object> param) throws SQLException {
		return insert(namespace+".insertCodeD",param);
	}

	@Override
	public Object update(HashMap<String, Object> param) throws SQLException {
		return update("code.updateCodeH",param);
	}

	@Override
	public Object delete(HashMap<String, Object> param) throws SQLException{
		return delete(namespace+".deleteCodeH",param);
	}

	@Override
	public Object deleteDetail(HashMap<String, Object> param) throws SQLException {
		return delete(namespace+".deleteCodeD",param);
	}





}
