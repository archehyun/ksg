package com.ksg.schedule.logic.joint.outbound;

/**
 * 선사명 표시
 * 규칙1 : 선사명과 agent명이 동일 할시 선사명 만 표시
 * 
 * @author 박창현
 *
 */
public class CompanyString implements Comparable<CompanyString>{
	private String companyName;
	private String agent;
	public CompanyString(String companyName, String agent) {
		this.companyName = companyName;
		this.agent = agent;			
	}

	public String getCompanyName() {
		// TODO Auto-generated method stub
		return companyName;
	}
	

	/* 
	 * 2020-04-07
	 * 대표 선사 표시 기능
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{	
		return companyName.equals(agent)?companyName:companyName+agent!=null?("/"+agent):"";
	}

	public int compareTo(CompanyString o) {

		return this.getCompanyName().compareTo(o.getCompanyName());
	}


}
