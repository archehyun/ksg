package com.ksg.dao.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AreaMapper {
	public List<Map<String, Object>> selectAreaList(Map<String, Object> commandMap) throws SQLException;
}
