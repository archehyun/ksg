package com.dtp.api.dao;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.Code;

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
	
	public Code selectCodeById(String id) throws SQLException
	{
		return (Code) selectList(namespace+".selectCodeById", id);
	}
	
	public Code selectCodeDetailByKey(Code param) throws SQLException
	{
		return (Code) selectList(namespace+".selectCodeDetailByKey", param);
	}
	
	public int  insertCode(Code param) throws SQLException
	{
		return (int) insert(namespace+".insertCode", param);
	}
	
	public int  insertCodeDetail(Code param) throws SQLException
	{
		return (int) insert(namespace+".insertCodeDetail", param);
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
}
