/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.dao.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.AbstractDAO;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.dao.MemberDAO;
import com.ksg.domain.Member;

public class MemberDAOImpl extends AbstractDAO implements MemberDAO
{
	private SqlMapClient sqlMap;

	public MemberDAOImpl() 
	{
		super();
	}

	public Member selectMember(String member_id) throws SQLException {
		// TODO Auto-generated method stub
		return (Member) sqlMap.queryForObject("Member.selectMember",member_id);
	}

	@Override
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object select(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object insert(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int selectCount(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

}
