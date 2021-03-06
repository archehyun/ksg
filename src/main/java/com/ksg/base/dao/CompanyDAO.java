package com.ksg.base.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

/**

  * @FileName : CompanyDAO.java

  * @Date : 2021. 2. 26. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :

  */
public class CompanyDAO extends AbstractDAO{
	
	public CompanyDAO() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectCompanyList(Map<String, Object> commandMap) throws SQLException {
		return selectList("company.selectCompanyList", commandMap);

	}

	public int deleteCompany(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete("company.deleteCompany", param);
	}	

	public int selectCompanyCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne("company.selectCount", commandMap);
	}

	public int updateCompany(HashMap<String, Object> param) throws SQLException{
		
		return (Integer) update("company.updateCompany", param);
		
	}

	public Object insertCompany(HashMap<String, Object> param) throws SQLException{
		// TODO Auto-generated method stub
		return insert("company.insertCompany",param);
	}

}
