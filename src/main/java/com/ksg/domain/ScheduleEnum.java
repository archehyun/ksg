package com.ksg.domain;

public enum ScheduleEnum {
	INBOUND("INBOUND","I"),OUTBOUND("OUTBOUND","O"), ROUTE("ROUTE", "O");

	final private String name;
	
	final private String symbol;

	private ScheduleEnum(String name, String symbol) { // enum���� ������ ���� ����
		this.name = name;
		this.symbol = symbol;
	}
	public String getName() { // ���ڸ� �޾ƿ��� �Լ�
		return name;
	}
	public String getSymbol() { // ���ڸ� �޾ƿ��� �Լ�
		return symbol;
	}
}
