package com.dtp.api.service.impl;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ksg.domain.ShippersTable;

public class ShipperTableServiceImplTest {
	
	ShipperTableServiceImpl service = new ShipperTableServiceImpl() ;

	@Test
	public void testSelectTableListByCondition() throws SQLException {
		ShippersTable param = new ShippersTable();
//		param.setGubun(ShippersTable.GUBUN_NNN);
		param.setDate_isusse("2023-02-20");
		List list =service.selectTableListAndPortListByCondition(param);
		System.out.println("size:"+list.size());
	}
	
	@Test
	public void testTableDate() throws SQLException {
		
		ShippersTable param = new ShippersTable();
		
		List idList = Arrays.asList("Maersk2","Maersk1");
		
		ShipperTableServiceImpl service = new ShipperTableServiceImpl();
		
		int result =service.updateTableDateByTableIDs(idList, "2023-06-19");
		
		System.out.println("result:"+result);
		
	}

}
