package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.dao.CodeDAO;
import com.ksg.domain.PortInfo;

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
	}

	@Override
	public Object select(Map<String, Object> commandMap) throws SQLException {
		return  selectOne("code.selectCodeH", commandMap);
	}


	@Override
	public Object selectDetail(Map<String, Object> commandMap) throws SQLException {
		return  selectOne("code.selectCodeD", commandMap);
	}



	@Override
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {
		return selectList("code.selectCodeHList", commandMap);

	}

	@Override
	public List<Map<String, Object>> selectDetailList(Map<String, Object> commandMap) throws SQLException {
		return selectList("code.selectCodeDList", commandMap);

	}
	
	@Override
	public Object selectCount(Map<String, Object> commandMap)throws SQLException {
		return  (Integer) selectOne("code.selectCountH", commandMap);
	}

	@Override
	public int selectDetailCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne("code.selectCount", commandMap);
	}	

	@Override
	public Object insert(HashMap<String, Object> param) throws SQLException{
		return insert("code.insertCodeH",param);

	}
	
	@Override
	public Object insertDetail(HashMap<String, Object> param) throws SQLException {
		return insert("code.insertCodeD",param);
	}

	@Override
	public Object update(HashMap<String, Object> param) throws SQLException {
		return update("code.updateCodeH",param);
	}

	@Override
	public Object delete(HashMap<String, Object> param) throws SQLException{
		return delete("code.deleteCodeH",param);
	}

	@Override
	public Object deleteDetail(HashMap<String, Object> param) throws SQLException {
		return delete("code.deleteCodeD",param);
	}





}
