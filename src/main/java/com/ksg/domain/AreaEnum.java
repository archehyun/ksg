package com.ksg.domain;

public enum AreaEnum {
	
	RUSSIA("Russia"),JAPAN("Japan"), CHINA("China");
	
	final private String name;
	
	
	private AreaEnum(String name)
	{
		this.name = name;
	}
	public String getName() { // 문자를 받아오는 함수
		return name;
	}
	public String toUpperCase() {
		// TODO Auto-generated method stub
		return name.toUpperCase();
	}
	
	

}
