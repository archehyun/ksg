package com.ksg.schedule.logic.joint.outbound;

/**
 * ����� ǥ��
 * ��Ģ1 : ������ agent���� ���� �ҽ� ����� �� ǥ��
 * 
 * @author ��â��
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
	 * ��ǥ ���� ǥ�� ���
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
