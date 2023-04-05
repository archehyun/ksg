package com.test;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.workbench.schedule.comp.treenode.SortedSchedule;

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
	@Test
	public void testDate()
	{
		System.out.println( KSGDateUtil.isThreeDayUnder("2023/4/13","2023/4/17"));
		
	}
}
