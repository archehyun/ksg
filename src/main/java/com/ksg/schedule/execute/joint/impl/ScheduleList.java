package com.ksg.schedule.execute.joint.impl;

import java.util.ArrayList;


public class ScheduleList<E> extends ArrayList<E>
{
	
	private String name;
	public String getName() {
		return name;
	}

	public ScheduleList() {

	}

	public ScheduleList(String name) {
		this.name = name;
	}
}