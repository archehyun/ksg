package com.ksg.domain;

public enum AreaEnum {
	
	RUSSIA("Russia"),JAPAN("Japan"), CHINA("China");
	
	final private String name;
	
	
	private AreaEnum(String name)
	{
		this.name = name;
	}
	public String getName() { // ���ڸ� �޾ƿ��� �Լ�
		return name;
	}
	public String toUpperCase() {
		// TODO Auto-generated method stub
		return name.toUpperCase();
	}
	
	

}
