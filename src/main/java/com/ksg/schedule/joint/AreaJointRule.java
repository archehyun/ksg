package com.ksg.schedule.joint;

public enum AreaJointRule {
	
	AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC(8),
	CHINA(4),
	RUSSIA(4),
	JAPAN(4),
	ASIA(6),
	
	PERSIAN_GULF(8);
	
	
	private int value;
	
	AreaJointRule( int value)
	{	
		this.value = value;
	}
	public int getValue()
	{
		return value;
	}
	

}
