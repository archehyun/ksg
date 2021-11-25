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
package com.ksg.service.impl;

import java.sql.SQLException;

import com.ksg.dao.MemberDAO;
import com.ksg.dao.impl.MemberDAOImpl;
import com.ksg.domain.Member;
import com.ksg.service.MemberService;

public class MemberServiceImpl implements MemberService 
{
	private MemberDAO memberDAO;
	public MemberServiceImpl() {
		memberDAO = new MemberDAOImpl();
	}

	public Member selectMember(String id) throws SQLException {
		// TODO Auto-generated method stub
		return memberDAO.selectMember(id);
	}

}
