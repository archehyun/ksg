package com.ksg.service.impl;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.VesselServiceV2;

public class VesselServiceImplTest {

	VesselServiceV2 vesselService;

	@Before
	public void setUp()
	{
		vesselService = new VesselServiceImpl();
	}

	@Test
	public void testSelectList() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectDetailListHashMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateDetail() {
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
	public void testDeleteDetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectDetailListMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateHashMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertDetail() {
//		Vessel item= new Vessel();
//
//		item.setVessel_name("test1");
//		item.setVessel_mmsi("test1");
//		try {
//		vesselService.insertDetail(item);
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}

	@Test
	public void testSelectListByPage() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectDetailListByLike() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectDetail() {
		fail("Not yet implemented");
	}

//	@Test
	public void testInsertVessel() 
	{
		Vessel item= new Vessel();

		item.setVessel_name("test1");
		item.setVessel_mmsi("test1");
		item.setVessel_company("test");
		item.setVessel_use(1);
		item.setVessel_type("C");
		try {
			vesselService.insert(item);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdateVessel() {
		fail("Not yet implemented");
	}

}
