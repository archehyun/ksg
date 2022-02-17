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
import java.util.Map;

import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.domain.Member;

public interface MemberService {

	public Map<String, Object> select(Map<String, Object> commandMap) throws SQLException;
	
	public boolean login(String id,String pw) throws Exception;

}
