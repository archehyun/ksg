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
package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public interface MemberService extends PageService{

	
	public Map<String, Object> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public boolean login(String id,String pw) throws Exception;
	public Object insertMember(HashMap<String, Object> param)throws Exception;
	public int deleteMember(HashMap<String, Object> item)throws  SQLException;

}
