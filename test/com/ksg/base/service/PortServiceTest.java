package com.ksg.base.service;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.HashMap;

import org.junit.Test;

import com.ksg.service.impl.PortServiceImpl;

public class PortServiceTest {
	
	PortServiceImpl service;

	@Test
	public void testSelectPort() {
		
		service = new PortServiceImpl();
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		param.put("port_name", "PUSAN");
		
		param.put("port_abbr", "PUSAN");
		try {
			HashMap<String, Object> result = service.selectPort(param);
			
			System.out.println(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
