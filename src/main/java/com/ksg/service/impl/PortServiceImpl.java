package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.model.CommandMap;
import com.ksg.dao.PortDAO;
import com.ksg.dao.impl.PortDAOImpl;
import com.ksg.domain.PortInfo;
import com.ksg.service.PortService;

import lombok.extern.slf4j.Slf4j;

/**

 * @FileName : PortServiceImpl.java

 * @Project : KSG2

 * @Date : 2021. 12. 7. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 항구 정보 관리

 */
@Deprecated
@Slf4j
public class PortServiceImpl extends AbstractServiceImpl implements PortService{

	private PortDAO portDAO;	

	public PortServiceImpl() {
		super();
		portDAO = new PortDAOImpl();
	}

	@SuppressWarnings("unchecked")
	public CommandMap selectList(Map<String, Object> param) throws SQLException {

		log.debug("param:{}", param);

		CommandMap resultMap = new CommandMap();

		resultMap.put("total", portDAO.selectCount(param));

		resultMap.put("master", portDAO.selectList(param));

		return resultMap;

	}

	public CommandMap selectListByCondition(Map<String, Object> param) throws SQLException
	{
		log.debug("param:{}", param);

		CommandMap resultMap = new CommandMap();

		resultMap.put("total", portDAO.selectCount(param));

		resultMap.put("master", portDAO.selectListByLike(param));

		return resultMap;
	}

	public int deletePortAbbr(HashMap<String, Object> param) throws SQLException {
		log.debug("param:{}", param);
		return portDAO.deleteDetail(param);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> selectPort(HashMap<String, Object> param) throws SQLException {
		log.info("param:{}", param);

		HashMap<String,Object> resultMap = null;

		PortInfo result= select( param);

		if(result==null)
		{
			resultMap = (HashMap<String, Object>) portDAO.selectDetail(param);
		}
		else
		{
			resultMap=(HashMap<String, Object>) objectMapper.convertValue(result, Map.class);
		}

		return resultMap;
	}

	public int update(HashMap<String, Object> param) throws SQLException {
		log.debug("param:{}", param);
		return portDAO.update(param);
	}

	public Object insert(HashMap<String, Object> param) throws Exception {

		try {

			log.debug("param:{}", param);
			return portDAO.isnert(param);
		}
		catch (SQLException e1) {

			if(e1.getErrorCode()==2627)
			{
				throw new AlreadyExistException("exist");

			}else
			{
				throw new SQLException(e1.getMessage());
			}
		}
	}

	@Deprecated
	@Override
	public Object insert(PortInfo param) throws Exception {
		try {

			log.debug("param:{}", param);
			return portDAO.insert(param);
		}
		catch (SQLException e1) {

			if(e1.getErrorCode()==2627)
			{
				throw new AlreadyExistException("exist");

			}else
			{
				throw new SQLException(e1.getMessage());
			}
		}
	}

	@Override
	public Object selectPortAbbr(HashMap<String, Object> param) throws SQLException {
		log.debug("param:{}", param);
		return portDAO.selectDetail(param);
	}

	@Override
	public List<Map<String, Object>> selectPortAbbrList(HashMap<String, Object> param) throws SQLException {
		log.debug("param:{}", param);
		return portDAO.selectDetailList(param);
	}

	@Override
	public CommandMap selectListByPage(HashMap<String, Object> param) throws SQLException {

		log.debug("param:{}", param);

		CommandMap resultMap = new CommandMap();

		resultMap.put("total", portDAO.selectCount(param));

		resultMap.put("master", portDAO.selectListByPage(param));

		return resultMap;
	}

	@Override
	public PortInfo select(Map<String, Object> param) throws RuntimeException {

		PortInfo item = PortInfo.builder().port_name(String.valueOf(param.get("port_name")))
				.port_abbr(String.valueOf(param.get("port_abbr")))
				.build();

		try {
			return portDAO.select(item);	
		}catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Map<String, String> selectAll() throws SQLException {

		List<PortInfo> li = portDAO.selectAll(); 

		Map<String, String> map = li.stream().collect(Collectors.toMap(PortInfo::getPort_name, PortInfo::getPort_name));

		return map;
	}

	public int delete(CommandMap param) throws RuntimeException {
		log.debug("delete param:{}", param);
		PortInfo item = PortInfo.builder().port_name(String.valueOf(param.get("port_name")))				
			.build();
		
		try {
			PortInfo hasItem=portDAO.select(item);

			if(hasItem==null)
				throw new ResourceNotFoundException("port_name:"+item.getPort_name());	
			
			log.debug("delete param2:{}", param);
			return portDAO.delete(param);
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public int deleteDetail(CommandMap param) throws RuntimeException {

		log.debug("param:{}", param);
		PortInfo item = PortInfo.builder().port_name(String.valueOf(param.get("port_name")))
										  .port_abbr(String.valueOf(param.get("port_abbr")))
										  .build();
		
		try {
			PortInfo hasItem=portDAO.selectDetail(item);

			if(hasItem!=null)
				throw new ResourceNotFoundException("port_name:"+item.getPort_name());	
			
			
			return portDAO.deleteDetail(param);
			
		}catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}



	@Override
	public int update(CommandMap param) throws SQLException {

		log.debug("param:{}", param);
		PortInfo item = PortInfo.builder().port_name(String.valueOf(param.get("port_name")))
										.port_area(String.valueOf(param.get("port_area")))
										.port_nationality(String.valueOf(param.get("port_nationality")))
										.base_port_name(String.valueOf(param.get("base_port_name")))
										.area_code(String.valueOf(param.get("area_code")))
									.build();
		return portDAO.update(item);
	}

	@Override
	public int updateDetail(CommandMap param) throws SQLException {


		PortInfo item = PortInfo.builder().port_name(String.valueOf(param.get("port_name")))
				.port_abbr(String.valueOf(param.get("port_abbr")))
				.build();
		
		return portDAO.updateDetail(item);
	}

	@Override
	public Object insert(CommandMap param) throws RuntimeException {
		log.debug("param:{}", param);
		PortInfo item = PortInfo.builder().port_name(String.valueOf(param.get("port_name")))
				.port_abbr(String.valueOf(param.get("port_name")))
				.port_area(String.valueOf(param.get("port_area")))
				.area_code(String.valueOf(param.get("area_code")))
				.port_nationality(String.valueOf(param.get("port_nationality")))
				.build();

		try {
			PortInfo hasItem=portDAO.select(item);

			if(hasItem!=null)
				return new AlreadyExistException("port_name:"+item.getPort_name());			

			portDAO.insert(item);

			PortInfo hasItemDetail=portDAO.selectDetail(item);

			if(hasItemDetail!=null)
				return new AlreadyExistException("port_abbr:"+item.getPort_abbr());	

			portDAO.insertDetail(item);

			return null;


		}catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}		

	}
	@Override
	public Object insertDetail(CommandMap param) throws RuntimeException {

		log.debug("param:{}", param);
		try {

			PortInfo item = PortInfo.builder().port_name(String.valueOf(param.get("port_name")))
					.port_abbr(String.valueOf(param.get("port_abbr")))

					.build();

			PortInfo hasItemDetail=portDAO.selectDetail(item);

			if(hasItemDetail!=null)
				return new AlreadyExistException("port_abbr:"+item.getPort_abbr());	

			return portDAO.insertDetail(item);
		}catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}		

	}

	@Override
	public List<PortInfo> selectPortListByNameList(List<String> nameList) throws SQLException {
		return portDAO.selectPortListByNameList(nameList);
	}
}
