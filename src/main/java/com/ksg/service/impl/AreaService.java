package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.model.CommandMap;

public interface AreaService {

	List<CommandMap> selectAreaInfoList() throws SQLException;

}
