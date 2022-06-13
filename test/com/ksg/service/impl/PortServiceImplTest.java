package com.ksg.service.impl;

import org.junit.Before;
import org.junit.Test;

import com.ksg.common.model.CommandMap;
import com.ksg.service.PortService;

public class PortServiceImplTest {

	private PortService portService;

	private int count=0;

	@Before
	public void setUp()
	{
		portService = new PortServiceImpl();
	}

	@Test
	public void testUpdate() {
		CommandMap param = new CommandMap();
		param.put("port_name", "testPort1");
		param.put("base_port_name", "testPort");
		param.put("port_abbr", "testPort123");
		param.put("port_area", "testArea");
		param.put("port_nationality", "testNation1");

		try {
			Object obj=	portService.update(param);
			System.out.println("result:"+obj);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testUpdateDetail() {
		CommandMap param = new CommandMap();
		param.put("port_name", "testPort1");
		param.put("base_port_name", "testPort");
		param.put("port_abbr", "testPort123");
		

		try {
			Object obj=	portService.updateDetail(param);
			System.out.println("result:"+obj);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testDelete() {
		CommandMap param = new CommandMap();
		param.put("port_name", "testPort1");
		param.put("base_port_name", "testPort1");
		param.put("port_abbr", "testPort123");
		param.put("port_area", "testArea");
		param.put("port_nationality", "testNation1");

		try {
			Object obj=	portService.delete(param);
			System.out.println("result:"+obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	//@Test
	public void testDeleteDetail() {
		CommandMap param = new CommandMap();
		param.put("port_name", "testPort1");
		param.put("base_port_name", "testPort1");
		param.put("port_abbr", "testPort123");
		param.put("port_area", "testArea");
		param.put("port_nationality", "testNation1");

		try {
			Object obj=	portService.deleteDetail(param);
			System.out.println("result:"+obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

		@Test
	public void testInsert() {

		CommandMap param = new CommandMap();
		param.put("port_name", "testPort");
		param.put("port_abbr", "testPort");
		param.put("port_area", "testArea");
		param.put("port_nationality", "testNation");

		try {
			Object obj=	portService.insert(param);
			System.out.println("result:"+obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		count++;
		System.out.println("count:"+count);
	}


	//	@Test
	public void testInsertDetail() {

		CommandMap param = new CommandMap();
		param.put("port_name", "testPort");
		param.put("port_abbr", "testPortAbbr");

		try {
			Object obj=	portService.insertDetail(param);
			System.out.println("result:"+obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		count++;
		System.out.println("count:"+count);
	}



}
