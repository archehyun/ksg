package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PortDAOImplTest {
	
	
	PortDAOImpl portDao;
	@Before
	public void setUp()
	{
		portDao = new PortDAOImpl();
	}

	@Test
	public void testSelectListMapOfStringObject() throws SQLException {
		
		
//		List li=portDao.selectList(null);
//		System.out.println(li);
	}
	
}
