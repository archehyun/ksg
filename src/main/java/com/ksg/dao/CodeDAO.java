package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

/**

  * @FileName : CodeDAO.java

  * @Date : 2021. 3. 5. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� : �ڵ� ���� ��ȸ DAO

  */
public class CodeDAO extends AbstractDAO{
	public CodeDAO() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectCodeHList(Map<String, Object> commandMap) throws SQLException {
		return selectList("code.selectCodeHList", commandMap);

	}

	public int selectCodeCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne("code.selectCount", commandMap);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectCodeDList(Map<String, Object> commandMap) throws SQLException {
		return selectList("code.selectCodeDList", commandMap);

	}

	public Object selectCodeD(Map<String, Object> commandMap) throws SQLException {
		return  selectOne("code.selectCodeD", commandMap);
	}

	public Object insertCodeH(HashMap<String, Object> param) throws SQLException{
		return insert("code.insertCodeH",param);
		
	}
	public Object insertCodeD(HashMap<String, Object> param) throws SQLException {
		return insert("code.insertCodeD",param);
	}

	public Object updateCodeH(HashMap<String, Object> param) throws SQLException {
		return update("code.updateCodeH",param);
	}

	public Object selectCodeHCount(Map<String, Object> commandMap)throws SQLException {
		return  (Integer) selectOne("code.selectCountH", commandMap);
	}

	public Object deleteCodeH(HashMap<String, Object> param) throws SQLException{
		return delete("code.deleteCodeH",param);
	}

	public Object deleteCodeD(HashMap<String, Object> param) throws SQLException {
		return delete("code.deleteCodeD",param);
	}



}
