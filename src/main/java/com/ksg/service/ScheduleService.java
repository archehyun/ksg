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
import java.util.List;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.ScheduleData;

public interface ScheduleService extends PageService{
	
	public List<ScheduleData> selecteScheduleListByCondition(ScheduleData param) throws SQLException;
	
	public List<ScheduleData> selecteAll(CommandMap param) throws SQLException;
	
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	
	public ScheduleData insertScheduleData(ScheduleData data)				throws SQLException;
	
	public Object insertScheduleBulkData(List<ScheduleData> scheduleList)				throws SQLException;
	
	public int deleteSchedule()												throws SQLException;	
}


