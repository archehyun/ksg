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
		this.namespace = "company";
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectCompanyList(Map<String, Object> commandMap) throws SQLException {
		return selectList(namespace+".selectCompanyList", commandMap);

	}

	public int deleteCompany(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete(namespace+".deleteCompany", param);
	}	

	public int selectCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne(namespace+".selectCount", commandMap);
	}
	
	@Override
	public int update(HashMap<String, Object> param) throws SQLException{

		return (Integer) update(namespace+".updateCompany", param);

	}
	
	@Override
	public Object insert(HashMap<String, Object> param) throws SQLException{
		
		return insert(namespace+".insertCompany",param);
	}


	@Override
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {
		return selectList(namespace+".selectCompanyList", commandMap);

	}

	@Override
	public int delete(Map<String, Object> commandMap) throws SQLException {
		
		return sqlMap.delete(namespace+".deleteCompany",commandMap);
	}
	
	@Override
	public List selectListByPage(HashMap<String, Object> param) throws SQLException{
		
		return selectList(namespace+".selectCompanyListByPage", param);
	}

}
