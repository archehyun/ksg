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
package com.ksg.adv.view.comp;

public class ADVTableNotMatchException extends Exception {
	
	public ADVTableNotMatchException(int current, int newADV) {
		super( "현재 DB에 저장된 광고테이블 수는 "+current+
				"이고 검색된 광고테이블 수는 : "+newADV+"입니다.");
	}

}
