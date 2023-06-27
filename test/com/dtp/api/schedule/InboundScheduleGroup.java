package com.dtp.api.schedule;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.ksg.domain.ShippersTable;

public class InboundScheduleGroup {
	
	@Test
	public void testSelectTableListByCondition() throws SQLException {
	
//		List<String> testList = Arrays.asList("AB","AA", "C",  "D" );
		List<String> testList = Arrays.asList("Heung-A","Sinokor", "HMM",  "KMTC" );
		testList = testList.stream().sorted( String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
		
		testList.forEach(o -> System.out.println(o));
	}
	

}
