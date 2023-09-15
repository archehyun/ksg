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
package com.ksg.domain;

import lombok.Data;

/**
 * @author ��â��
 *
 */
@Data
public abstract class BaseInfo {
	
	protected String updatetime;

	protected String updateUser;

	protected String searchKeyword;
	
	protected String orderBy;
	
	protected String orderby;

	public abstract String toInfoString();
	
	
}
