package com.ksg.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.AreaInfo;

public interface AreaService {

	List<CommandMap> selectAreaInfoList() throws SQLException;
	
	List<AreaInfo> selectAll() throws SQLException;

}
