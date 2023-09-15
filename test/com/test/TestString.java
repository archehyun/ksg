package com.test;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Test;

import com.dtp.api.schedule.joint.print.outbound.OutboundScheduleGroup;
import com.dtp.api.schedule.joint.print.outbound.OutboundScheduleRule;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.workbench.schedule.comp.treenode.SortedSchedule;
import com.test.StaticItemGroup.StaticItem;

public class TestString {

	//	@Test
	public void testSelectList() {
		String testfile = "t.t";

		String[] a = testfile.split(".");

		System.out.println(a.length);

	}

	//	@Test
	public void testSpltSelectList() {
		ArrayList<Integer> dataList = new ArrayList<Integer>(); 
		ArrayList<Integer> indexList = new ArrayList<Integer>();

		dataList.add(1);
		dataList.add(2);		
		dataList.add(3);
		dataList.add(4);
		dataList.add(5);
		dataList.add(6);
		dataList.add(7);

		//		indexList.add(2);
		//		indexList.add(5);
		int dateGap =3;
		for(int i=0;i<dataList.size()-1;i++)			
		{
			int first = dataList.get(i);

			int second = dataList.get(i+1);

			if((second -first)>=dateGap)
			{
				indexList.add(i+1);
			}
		}

		List<List<Integer>> partitions = new ArrayList<>();

		for(int index =0,startIndex=0;index<indexList.size();index++)
		{
			int splitIndex 	= indexList.get(index);

			partitions.add(dataList.subList(startIndex,splitIndex));

			startIndex = splitIndex;

			if(index==indexList.size()-1) partitions.add(dataList.subList(startIndex,dataList.size()));
		}



		System.out.println(partitions);
	}
	//	@Test
	public void testDate()
	{
		System.out.println( KSGDateUtil.isThreeDayUnder("2023/4/13","2023/4/17"));

	}

	@Test
	public void testDate2()
	{
		String[] a = {"A","C","B","D","F"};

		String[] b = {"B,D"};

		List<String> arragedFromPortList =Arrays.asList(a);


		List<String> fromPortList =Arrays.asList(b);


		List<String> resultList1 = arragedFromPortList.stream()
				.filter(old -> fromPortList.stream().noneMatch(Predicate.isEqual(b)))
				.collect(Collectors.toList());


		System.out.println(resultList1);

	}
	@Test
	public void testDate3()
	{
		
		String[] productIds = {"1", "2"};
		
		StaticItemGroup test = new StaticItemGroup();

		test.add("1,","02", 11, null, 0,"EA");
		test.add("2,","02", 12, "02", 5,"EA");
		test.add("2,","01", 12, null, 0,"EA");

		test.print();
	}
	
	@Test
	public void testDate44()
	{
		

		System.out.println(String.format("%d%02d", 2023,4));
	}
	@Test
	public void testDate4()
	{
		System.out.println("20230206".substring(4,6));
		
	}
	
	@Test
	public void testDate5()
	{
		
		Calendar cal = Calendar.getInstance();
		int acturlMonth =2;
		int month = acturlMonth-1; 

		cal.set(2022,month,1);

		System.out.println(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
	}
	@Test
	public void dateTest()
	{
		String date = "202302";
		
		int year = Integer.parseInt(date.substring(0,4));
		int month = Integer.parseInt(date.substring(4,6));
		
		System.out.println("year:"+year+",month:"+month);
		
	}
	// 부산항 부산 신항 공동배선 
	@Test
	public void test5() throws Exception
	{
		
		OutboundScheduleGroup group = new OutboundScheduleGroup(Vessel.builder().vessel_type("C").vessel_company("").build());
		
		
		SortedSchedule scheduleBusanNew1 = new SortedSchedule(ScheduleData.builder()
																.company_abbr("Dongjin")
																.agent("Dongjin")
																.DateF("4/26")
																.DateT("4/25")																
																.fromPort("BUSAN").build());
		
		SortedSchedule scheduleBusanNew = new SortedSchedule(ScheduleData.builder()
																.company_abbr("Dongjin")
																.agent("Dongjin")
																.DateF("4/23")
																.DateT("4/26")																
																.fromPort("BUSAN NEW").build());
		
		SortedSchedule scheduleBusan1 = new SortedSchedule(ScheduleData.builder()
																.company_abbr("Dong Young")
																.agent("Dong Young")
																.DateF("4/24")
																.DateT("4/25")
																.fromPort("BUSAN").build());
		
		SortedSchedule scheduleBusan2 = new SortedSchedule(ScheduleData.builder()
																.company_abbr("Heung-A")
																.agent("Heung-A")
																.DateF("4/24")
																.DateT("4/25")
																.fromPort("BUSAN").build());		
		
		group.add(scheduleBusanNew1);
		
		group.add(scheduleBusanNew);
		
		group.add(scheduleBusan1);
		
		group.add(scheduleBusan2);		
		
		group.joinnted();
		

		String vessel_type = group.getVesselType();
		
		String formatedVesselType =  (vessel_type.equals("")||vessel_type.equals(" "))?"   ":String.format("   [%s]   ", vessel_type);   
		
		String str= String.format("%-8s%-15s%s(%s)   %s", group.getJointedDateF(), group.getVesselName(),formatedVesselType, group.getJointedCompanyName(), group.getJointedDateT());
		
		System.out.println(str);
		
		//System.out.println(group.toJointedOutboundScheduleString());
		
	}
	
}

