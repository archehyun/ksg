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

/**
 * 선박 정보
 * @author 박창현
 *
 */
public class Vessel extends BaseInfo{

	public static final int NON_USE=1;
	public static final int USE=0;
	private String vessel_name;
	private String vessel_abbr;
	private String vessel_abbr2;
	private String vessel_company="";
	private int vessel_use=-1;
	private String vessel_mmsi="";
	private Date input_date;


	public Date getInput_date() {
		return input_date;
	}

	public void setInput_date(Date input_date) {
		this.input_date = input_date;
	}

	public String getVessel_mmsi() {
		return vessel_mmsi;
	}

	public void setVessel_mmsi(String vessel_mmsi) {
		this.vessel_mmsi = vessel_mmsi;
	}

	public int getVessel_use() {
		return vessel_use;
	}

	public void setVessel_use(int vesselUse) {
		vessel_use = vesselUse;
	}

	public String getVessel_abbr2() {
		return vessel_abbr2;
	}

	public void setVessel_abbr2(String vesselAbbr2) {
		vessel_abbr2 = vesselAbbr2;
	}

	private String vessel_type;
	private String patten;
	private String option;
	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}
	public String getPatten() {
		return patten;
	}

	public void setPatten(String patten) {
		this.patten = patten;
	}
	public String getVessel_name() {
		return vessel_name;
	}

	public void setVessel_name(String vessel_name) {
		this.vessel_name = vessel_name;
	}
	public String toString()
	{
		return vessel_name;
	}


	public String getVessel_type() {
		return vessel_type;
	}

	public void setVessel_type(String vessel_type) {
		this.vessel_type = vessel_type;
	}

	public String getVessel_abbr() {
		return vessel_abbr;
	}

	public void setVessel_abbr(String vessel_abbr) {
		this.vessel_abbr = vessel_abbr;
	}

	public String getVessel_company() {
		return vessel_company;
	}

	public void setVessel_company(String vessel_company) {
		this.vessel_company = vessel_company;
	}

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
