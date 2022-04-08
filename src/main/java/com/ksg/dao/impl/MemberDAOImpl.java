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

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.dao.MemberDAO;
import com.ksg.domain.Member;

public class MemberDAOImpl implements MemberDAO
{
	private SqlMapClient sqlMap;

	public MemberDAOImpl() 
	{
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Member selectMember(String member_id) throws SQLException {
		// TODO Auto-generated method stub
		return (Member) sqlMap.queryForObject("Member.selectMember",member_id);
	}

}
