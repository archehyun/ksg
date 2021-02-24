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

/**
 * @author 박창현
 *
 */
public class QuarkFormat {
	public int table_id; // 테이블 아이디
	public String foamt; // 포맷
	public int getTable_id() {
		return table_id;
	}
	public void setTable_id(int table_id) {
		this.table_id = table_id;
	}
	public String getFoamt() {
		return foamt;
	}
	public void setFoamt(String foamt) {
		this.foamt = foamt;
	}

}
