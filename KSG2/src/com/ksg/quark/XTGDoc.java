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
package com.ksg.quark;

import org.apache.log4j.Logger;

public abstract class XTGDoc {
	protected Logger logger = Logger.getLogger(getClass());
	protected XTGPaser parser;
	public abstract String getPreFix() ;

	public abstract void setPreFix(String preFix); 

	public void setPaser(XTGPaser parser)
	{
		this.parser = parser;
	}
	public abstract String parseDoc();
	public abstract void setADVData(String data);
	public abstract void setQuarkData(String data);
		
}
