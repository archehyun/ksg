package com.ksg.schedule.logic.route;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.ksg.schedule.logic.joint.ScheduleBuildUtil;

public class GroupVesselTest {

	//@Test
	public void testArrangedCompanyList() {
		
		
		GroupVessel vessel = new GroupVessel();
		ArrayList<String> list = new ArrayList<String>();
		list.add("test2");
		list.add("test1");
		list.add("test1");
		list.add("test2");
		System.out.println(vessel.getArrangedCompanyList(list));
	}
	//@Test
	public void testGetMajorCompanyAndCompanyListString()
	{
		GroupVessel vessel = new GroupVessel();
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("test2");
		list.add("test1");
		list.add("test1");
		list.add("test2");		
		list.add("test3");
		System.out.println(vessel.getMajorCompanyAndCompanyListString("test3",list));
	}
//	@Test
	public void testGetCompanyString()
	{
		GroupVessel vessel = new GroupVessel();
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("test2");
		list.add("test1");
		list.add("test1");
		list.add("test2");		
		list.add("test3");
		//vessel.setMajorCompany("test3");
		System.out.println(vessel.getCompanyString());
	}
	//@Test
	public void testgetNumericVoyage()
	{
		GroupVessel vessel = new GroupVessel();
		System.out.println(ScheduleBuildUtil.getNumericVoyage("v1-23v"));
	}
	@Test
	public void testStringnull()
	{
		String major=null;
		System.out.println(major.isEmpty());
		
	}

}

