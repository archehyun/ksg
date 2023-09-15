package com.ksg.domain;

import java.util.Arrays;

public enum AreaEnum {
	
	RUSSIA("RUSSIA", 4),
	JAPAN("JAPAN", 4), 
	CHINA("CHINA",4), 
	ASIA("ASIA",6),
	AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC("AUSTRALIA, NEW ZEALAND & SOUTH PACIFIC", 8),
	PERSIAN_GULF("PERSIAN GULF", 8),
	ELSE("ELSE",10);
	
	final private String name;
	
	final private int gap;
	
	
	private AreaEnum(String name, int gap)
	{
		this.name = name;
		this.gap = gap;
	}
	
	
	public String getName() { // 문자를 받아오는 함수
		return name;
	}
	
	public int getGap()
	{
		return gap;
	}
	
	public static AreaEnum findGapByAreaName(String areaName)
	{
		return Arrays.stream(AreaEnum.values())
				.filter(area ->area.toUpperCase().equals(areaName))
				.findAny()
				.orElse(ELSE);
	}
	public String toUpperCase() {
		return name.toUpperCase();
	}
	
	

}
