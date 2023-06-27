package com.ksg.domain;

public enum ScheduleEnum {
	INBOUND("INBOUND","I"),OUTBOUND("OUTBOUND","O"), ROUTE("ROUTE", "O");

	final private String name;
	
	final private String symbol;

	private ScheduleEnum(String name, String symbol) { // enum에서 생성자 같은 역할
		this.name = name;
		this.symbol = symbol;
	}
	public String getName() { // 문자를 받아오는 함수
		return name;
	}
	public String getSymbol() { // 문자를 받아오는 함수
		return symbol;
	}
}
