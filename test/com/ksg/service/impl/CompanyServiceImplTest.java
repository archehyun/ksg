package com.ksg.service.impl;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.ksg.domain.Company;
import com.ksg.service.CompanyService;

public class CompanyServiceImplTest {
	
	
	CompanyService companyService;
	@Before
	public void setUp()
	{
		companyService = new CompanyServiceImpl();
	}

	@Test
	public void testSelect() {
		try {
			Company result = companyService.select("a1");
			assertNotNull(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
