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

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 선박 정보
 * @author 박창현
 *
 */
@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
@EqualsAndHashCode(of = {"vessel_name"})
public class Vessel extends BaseInfo{

	public static final int NON_USE=1;
	
	public static final int USE=0;
	
	
	private String vessel_name; // 선박명
	
	private String vessel_abbr; // 선박명 약어
	
	private String vessel_abbr2;
	
	private String vessel_company="";// 선사명
	
	private int vessel_use=-1; // 선박사용 유무
	
	private String vessel_mmsi="";
	
	private String input_date;
	
	private String vessel_type;
	
	private String patten;
	
	private String option;
	
	private String contents;	

	@Override
	public String toInfoString() {
		// TODO Auto-generated method stub
		return "["+getVessel_name()+","
		+getVessel_abbr()+","
		+getVessel_type()+","
		+getVessel_use()+","
		+getVessel_company()+","
		+getVessel_mmsi()+","
		+getInput_date()+"]";
	}


}
