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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.dao.MemberDAO;
import com.ksg.dao.impl.MemberDAOImpl;
import com.ksg.domain.Member;
import com.ksg.service.MemberService;

public class MemberServiceImpl implements MemberService 
{
	private MemberDAO memberDAO;
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	public MemberServiceImpl() {
		memberDAO = new MemberDAOImpl();
	}
	@Override
	public boolean login(String id, String pw) throws Exception {
		
		logger.info("param:{}", id);
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		
		
		Member member;
		try {
			
			param.put("member_id", id);
			
			
			member = (Member) memberDAO.select(param);
			
			if(member == null)
			{
				throw new ResourceNotFoundException(id);
			}
			
			return member.isMatchPassword(pw);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} 
		
		
	}

	@Override
	public Map<String, Object> select(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HashMap<String, Object> selectListByPage(HashMap<String, Object> param) throws SQLException {
HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", memberDAO.selectCount(param));
		
		resultMap.put("master", memberDAO.selectListByPage(param));
		
		resultMap.put("PAGE_NO", 1);
		
		return resultMap;
	}
	@Override
	public Map<String, Object> selectList(Map<String, Object> param) throws SQLException {
		
		logger.debug("param:{}", param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", memberDAO.selectCount(param));
		
		resultMap.put("master", memberDAO.selectList(param));
		
		return resultMap;

	}


}
