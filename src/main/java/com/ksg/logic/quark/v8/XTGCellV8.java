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
package com.ksg.logic.quark.v8;

import com.ksg.quark.logic.quarkfactory.XTGCell;

public class XTGCellV8 extends XTGCell{
	
	public XTGCellV8(String data)
	{
		super(data);
	}

	public XTGCellV8(String data, String prefix, String endfix) {
		super(data, prefix, endfix);
	}

	@Override
	public String makeXTG() {
		if(!endFix.equals("<\\c>"))
		{
			return preFix+data+endFix;
			
		}else
		{
			return preFix+data;
		}
	}

}
