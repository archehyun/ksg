package com.dtp.api.dao;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.Code;
import com.ksg.domain.KeyWordInfo;

public class CodeDAO extends AbstractDAO {
	
	public CodeDAO() {
		super();
		this.namespace = "code";
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public List<Code> selectCodeListByCondition(Code param) throws SQLException
	{
		return selectList(this.namespace+".selectCodeListByCondition", param);
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public List<Code> selectCodeDetailListByCondition(Code param) throws SQLException
	{
		return selectList(this.namespace+".selectCodeDetailListByCondition", param);
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public List<KeyWordInfo> selectKeyWordInfoListByCondition(KeyWordInfo param) throws SQLException
	{
		return selectList(this.namespace+".selectKeyWordInfoListByCondition", param);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Code selectCodeById(String id) throws SQLException
	{
		return (Code) selectList(namespace+".selectCodeById", id);
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public Code selectCodeDetailByKey(Code param) throws SQLException
	{
		return (Code) selectOne(namespace+".selectCodeDetailByKey", param);
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public int  insertCode(Code param) throws SQLException
	{
		return (int) insert(namespace+".insertCode", param);
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public Object  insertCodeDetail(Code param) throws SQLException
	{
		return (Object) insert(namespace+".insertCodeDetail", param);
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public int  updateCode(Code param) throws SQLException
	{
		return (int) update(this.namespace+".updateCode", param);
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public int  updateCodeDetail(Code param) throws SQLException
	{
		return (int) update(this.namespace+".updateCodeDetail", param);
	}
	
	/**
	 * 内靛 昏力
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public int  deleteCode(String id) throws SQLException
	{
		return (int) update(this.namespace+".deleteCode", id);
	}
	
	/**
	 * 内靛 惑技 昏力
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public int  deleteCodeDetail(Code param) throws SQLException
	{
		return (int) update(this.namespace+".deleteCodeDetail", param);
	}	
	
	public int  deleteKeyword(KeyWordInfo param) throws SQLException
	{
		return (int) update(this.namespace+".deleteKeyword", param);
	}

	public KeyWordInfo selectKeywordByKey(KeyWordInfo keyParam) throws SQLException {
		// TODO Auto-generated method stub
		return (KeyWordInfo) selectOne(namespace+".selectKeywordByKey", keyParam);
	}

	public  Object insertKeyword(KeyWordInfo keyParam) throws SQLException {
		// TODO Auto-generated method stub
		return insert(namespace+".insertKeyword", keyParam);
	}
}
