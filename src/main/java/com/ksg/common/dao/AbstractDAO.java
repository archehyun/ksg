package com.ksg.common.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

/**DAO 추상 클래스
 * @date 2021-02024
 * @author 박창현
 *
 */
public abstract class AbstractDAO {

	protected SqlMapClient sqlMap;
	
	protected String namespace;


	public AbstractDAO() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public List selectList(String queryId, Object param) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList(queryId, param);

	}

	@SuppressWarnings("rawtypes")
	public Object selectOne(String queryId, Object param) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForObject(queryId, param);
	}


	public Object insert(String queryId, Object params) throws SQLException {

		return sqlMap.insert(queryId, params);
	}



	public Object update(String queryId, Object params) throws SQLException {
		return sqlMap.update(queryId, params);
	}

	public Object delete(String queryId, Object params) throws SQLException {
		return sqlMap.delete(queryId, params);
	}

}
