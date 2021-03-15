package com.ksg.base.dao;

import java.sql.SQLException;
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
		// TODO Auto-generated method stub
		return selectList("code.selectCodeHList", commandMap);

	}

	public int selectCodeCount(Map<String, Object> commandMap) throws SQLException{
		// TODO Auto-generated method stub
		return  (Integer) selectOne("code.selectCount", commandMap);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectCodeDList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return selectList("code.selectCodeDList", commandMap);

	}

}
