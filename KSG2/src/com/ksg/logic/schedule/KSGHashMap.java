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
package com.ksg.logic.schedule;

import java.util.HashMap;
import java.util.Vector;

import com.ksg.domain.ScheduleData;

public class KSGHashMap extends HashMap<Object, Object> {
	Vector schedulelist;
	String port;
	public KSGHashMap(String port)
	{		
		this.put(port, port);
		this.port = port;
		schedulelist = new Vector();
	}
	public void putSchedule(ScheduleData obj)
	{
		if(this.get(obj.getDesination())==null)
		return;
		
		schedulelist.add(obj);
			
	}
	public String toString()
	{
		return port+","+this.schedulelist.size();
	}
	public String getPort() {
		// TODO Auto-generated method stub
		return port;
	}
	public Vector getSchedulelist() {
		return schedulelist;
	}	
}
