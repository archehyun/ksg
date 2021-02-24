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
package com.ksg.print.logic.quark;

public interface XTGPaser {

	public String[][] getFrame(String stringData);
	public String[] getRowTag(String stringData);
	public String getTablePrefix();
	public String getPagePrefix();

}
