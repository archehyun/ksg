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
	
	public List<Code> selectCodeListByCondition(Code param) throws SQLException
	{
		return selectList(this.namespace+".selectCodeListByCondition", param);
	}
	
	public List<Code> selectCodeDetailListByCondition(Code param) throws SQLException
	{
		return selectList(this.namespace+".selectCodeDetailListByCondition", param);
	}
	
	public List<KeyWordInfo> selectKeyWordInfoListByCondition(KeyWordInfo param) throws SQLException
	{
		return selectList(this.namespace+".selectKeyWordInfoListByCondition", param);
	}
	
	public Code selectCodeById(String id) throws SQLException
	{
		return (Code) selectList(namespace+".selectCodeById", id);
	}
	
	public Code selectCodeDetailByKey(Code param) throws SQLException
	{
		return (Code) selectOne(namespace+".selectCodeDetailByKey", param);
	}
	
	public int  insertCode(Code param) throws SQLException
	{
		return (int) insert(namespace+".insertCode", param);
	}
	
	public Object  insertCodeDetail(Code param) throws SQLException
	{
		return (Object) insert(namespace+".insertCodeDetail", param);
	}
	
	public int  updateCode(Code param) throws SQLException
	{
		return (int) update(this.namespace+".updateCode", param);
	}
	
	public int  updateCodeDetail(Code param) throws SQLException
	{
		return (int) update(this.namespace+".updateCodeDetail", param);
	}
	public int  deleteCode(String id) throws SQLException
	{
		return (int) update(this.namespace+".deleteCode", id);
	}
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
