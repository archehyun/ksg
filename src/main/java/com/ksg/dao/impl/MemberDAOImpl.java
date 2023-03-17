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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.dao.MemberDAO;
import com.ksg.domain.Member;

public class MemberDAOImpl extends AbstractDAO implements MemberDAO
{
	

	public MemberDAOImpl() 
	{
		super();
	}

	public Member selectMember(String member_id) throws SQLException {
		return (Member) selectOne("member.selectMember",member_id);
	}

	@Override
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {
		return selectList("member.selectList",commandMap);
	}

	@Override
	public Object select(HashMap<String, Object> param) throws SQLException {
		return selectOne("member.select",param);
	}

	@Override
	public int update(HashMap<String, Object> param) throws SQLException {
		return 0;
	}

	@Override
	public int delete(Map<String, Object> param) throws SQLException {
		return (Integer) delete("member.deleteMember",param);
	}

	@Override
	public Object insert(HashMap<String, Object> param) throws SQLException {
		return insert("member.insertMember", param);
	}

	
	
	public List selectListByPage(HashMap<String, Object> param) throws SQLException{
		return selectList("member.selectMemberListByPage", param);
	}

}
