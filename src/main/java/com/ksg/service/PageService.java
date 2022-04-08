package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;

public interface PageService {
	
	public HashMap<String, Object> selectListByPage(HashMap<String, Object> param) throws SQLException;

}
