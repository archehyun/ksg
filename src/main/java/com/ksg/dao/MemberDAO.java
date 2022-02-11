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
package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.domain.Member;

/**
 * @author Administrator
 *
 */
public interface MemberDAO {
	public Member selectMember(String member_id) throws SQLException;	
	
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public Object select(HashMap<String, Object> param) throws SQLException;
	
	public int update(HashMap<String, Object> param) throws SQLException;
	
	public int delete(Map<String, Object> commandMap) throws SQLException;
	
	public Object insert(HashMap<String, Object> param) throws SQLException;
	
	public int selectCount(Map<String, Object> commandMap) throws SQLException;

}
