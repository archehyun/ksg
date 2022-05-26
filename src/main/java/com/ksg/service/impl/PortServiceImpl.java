package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ksg.common.exception.AlreadyExistException;
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

 * @프로그램 설명 :

 */
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

		HashMap<String,Object> resultMap=(HashMap<String, Object>) portDAO.select( param);

		if(resultMap==null)
		{
			resultMap = (HashMap<String, Object>) portDAO.selectDetail(param);
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
	
	@Override
	public Object insert(PortInfo param) throws Exception {
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


	public int delete(HashMap<String, Object> param) throws SQLException {
		log.debug("param:{}", param);
		return portDAO.delete(param);
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
	public PortInfo select(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> selectAll() throws SQLException {
		
		List<PortInfo> li = portDAO.selectAll(); 
		
		Map<String, String> map = li.stream().collect(Collectors.toMap(PortInfo::getPort_name, PortInfo::getPort_name));
		
		return map;
	}






}
