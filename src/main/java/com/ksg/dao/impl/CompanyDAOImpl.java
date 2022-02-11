package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.dao.CompanyDAO;

/**

 * @FileName : CompanyDAO.java

 * @Date : 2021. 2. 26. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
public class CompanyDAOImpl extends AbstractDAO implements CompanyDAO{

	public CompanyDAOImpl() {
		super();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectCompanyList(Map<String, Object> commandMap) throws SQLException {
		return selectList("company.selectCompanyList", commandMap);

	}

	public int deleteCompany(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete("company.deleteCompany", param);
	}	

	public int selectCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne("company.selectCount", commandMap);
	}

	public int update(HashMap<String, Object> param) throws SQLException{

		return (Integer) update("company.updateCompany", param);

	}

	public Object insert(HashMap<String, Object> param) throws SQLException{
		// TODO Auto-generated method stub
		return insert("company.insertCompany",param);
	}


	@Override
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {
		return selectList("company.selectCompanyList", commandMap);

	}

	@Override
	public int delete(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.delete("company.deleteCompany",commandMap);
	}

}
