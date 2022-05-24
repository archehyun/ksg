package com.ksg.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ksg.domain.PortInfo;
import com.ksg.service.impl.PortServiceImpl;

public class PortServiceTest {

	
	PortService portService;
	@Before
	public void setUp()
	{
		portService = new PortServiceImpl();
	}
	@Test
	public void testSelectList() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectListByLike() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectPortAbbrList() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectPortAbbr() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectPort() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertHashMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertPortInfo() {
		
		
		PortInfo  test = new PortInfo();
		test.setPort_name("test123");
		test.setArea_code("teest");
		test.setPort_nationality("test123");
		
		try {
			
			Object ob=portService.insert(test);
			
			System.out.println("ob:"+ob);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
