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

public class KSGError {
	public static final int ERRORCODE_VESSEL=0;
	public static final int ERRORCODE_PORT=1;
	public static final int ERRORCODE_DATE=2;
	public static int ERRORCODE_EMPTY=3;
	private String errorMessage;
	private int errorCode;
	private int tableIndex;
	private int errorCount;
	private String errorPort;
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int i) {
		this.errorCode = i;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount =errorCount;
		
	}
	public int getErrorCount() {
		return errorCount;
	}
	public void setErrorPortCode(String errorPort) {
		this.errorPort=errorPort;
		
	}
	public String getErrorPort() {
		return errorPort;
	}
	public void setErrorPort(String errorPort) {
		this.errorPort = errorPort;
	}
	public int getTableIndex() {
		return tableIndex;
	}
	public void setTableIndex(int tableIndex) {
		this.tableIndex = tableIndex;
	}

}
