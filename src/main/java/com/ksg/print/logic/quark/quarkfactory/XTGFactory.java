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
package com.ksg.print.logic.quark.quarkfactory;

public abstract class XTGFactory 
{
	
	public static XTGFactory getFactory(String className)
	{
		XTGFactory factory=null;
		try {
			factory=(XTGFactory)Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return factory;
	}
	public abstract XTGPage createPage();
	public abstract XTGCell createCell(String data);
	public abstract XTGTable createTable(String info,int pageIndex);
	public abstract  XTGRow createRow(String data);
	
	

}
