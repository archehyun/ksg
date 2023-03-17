package com.dtp.api.service.impl;

import java.sql.SQLException;
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

}
