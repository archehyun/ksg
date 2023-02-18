package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.exception.UnhandledException;
import com.ksg.common.model.CommandMap;
import com.ksg.dao.impl.VesselDAOImpl;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.VesselServiceV2;

import lombok.extern.slf4j.Slf4j;

/**

 * @FileName : VesselServiceImpl.java

 * @Project : KSG2

 * @Date : 2021. 11. 25. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 선박, 선박 약어 관리

 */
@Slf4j
public class VesselServiceImpl extends AbstractServiceImpl implements VesselServiceV2{


	private VesselDAOImpl vesselDAO;

	public VesselServiceImpl() {
		super();
		vesselDAO = new VesselDAOImpl();
	}
	
	public List<Vessel> selectVesselListByCondition()
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	public CommandMap selectList(Map<String, Object> commandMap) throws SQLException {

		log.info("param:"+commandMap);

		CommandMap resultMap = new CommandMap();

		resultMap.put("total", vesselDAO.selectCount(commandMap));

		resultMap.put("master",vesselDAO.selectList(commandMap));

		return resultMap;

	}

	

	public Object updateDetail(HashMap<String, Object> param) throws SQLException {
		return vesselDAO.updateDetail(param);

	}

	/**
	 *
	 */
	@Override
	public Object delete(HashMap<String, Object> pram) throws SQLException {

		int result=vesselDAO.delete(pram);
		vesselDAO.deleteDetail(pram);
		return result;

	}

	/**
	 *선박명 등록
	 *선박 약어 등록
	 */
	public void insert(HashMap<String, Object> param) throws RuntimeException {

		log.debug("param:"+param);

		Vessel vessel = new Vessel();
		vessel.setVessel_name(String.valueOf(param.get("vessel_name")));
		vessel.setVessel_company(String.valueOf(param.get("vessel_company")));
		vessel.setVessel_mmsi(String.valueOf(param.get("vessel_mmsi")));
		vessel.
		setVessel_use( (Integer) param.get("vessel_use"));
		vessel.setVessel_type(String.valueOf(param.get("vessel_type")));
		vessel.setVessel_abbr(String.valueOf(param.get("vessel_name")));
		this.insert(vessel);

	}
	@Override
	public Object deleteDetail(HashMap<String, Object> param) throws RuntimeException  {
		log.debug("param:{}", param);
		Object obj =null;
		try {
			
			obj = vesselDAO.deleteDetail(param);
		}catch(SQLException e1)
		{
			throw new UnhandledException(e1.getMessage());
		}
		return  obj;

	}

	@Override
	public HashMap<String, Object> selectDetailList(Map<String, Object> param) throws SQLException {
		log.debug("param:"+param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("master",vesselDAO.selectDetailList((HashMap<String, Object>) param));
		
		return resultMap;
	}

	@Override
	public Object update(HashMap<String, Object> param) throws SQLException {
		log.info("param:{}", param);
		
		Object result = vesselDAO.update(param);;

		log.debug("result:{}:",param);
		return result;
	}



	@Override
	public HashMap<String, Object> selectListByPage(HashMap<String, Object> param) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("total", vesselDAO.selectCount(param));

		resultMap.put("master", param.get("vessel_abbr")==null? vesselDAO.selectListByPage(param):vesselDAO.selectListByPage2(param));

		resultMap.put("PAGE_NO", 1);

		return resultMap;
	}

	@Override
	public HashMap<String, Object> selectDetailListByLike(Map<String, Object> param) throws SQLException {
		log.debug("param:"+param);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("master",vesselDAO.selectDetailListByLike((HashMap<String, Object>) param));
		return resultMap;
	}

	@Override

	public Map<String, String> selectAll() throws SQLException {
		List<Vessel> li = vesselDAO.selectAll(); 
		
		Map<String, String> map = li.stream().collect(Collectors.toMap(Vessel::getVessel_name, Vessel::getVessel_name));
		
		return map;
	}
	public Vessel selectDetail(String vessel_abbr) throws SQLException {

		return vesselDAO.selectDetail(vessel_abbr);
	}
	public Vessel selectDetailInfo(String vessel_abbr) throws SQLException {

		return vesselDAO.selectDetailInfo(vessel_abbr);
	}

	@Override
	public void insert(Vessel param) throws RuntimeException {
		log.info("param:{}", param);
		try {
			vesselDAO.insert( param);
			
			vesselDAO.insertDetail( param);

		} catch (SQLException e1) {
			if(e1.getErrorCode()==2627)
			{
				throw new AlreadyExistException("exist");

			}else
			{

				e1.printStackTrace();
			}
		}		
	}

	@Override
	public Object update(Vessel param) throws SQLException {
		log.info("param:{}", param);
		Object result = vesselDAO.update(param);;

		log.debug("result:{}:",param);
		return result;
	}

	//TODO 다른 메소드와 통합 필요
//	@Override
//	public void insertDetail(Vessel param) throws RuntimeException {
//		log.info("param:{}", param);
//		try {
//
//			Vessel parent=vesselDAO.selectVessel(param);
//
//			if(parent == null)
//				throw new ResourceNotFoundException("vessel_name:"+param.getVessel_name());
//			
//			// 선막명 약어가 기존에 다른 선박명과 같을 시에 에러 발생
//			
//			Vessel newParam = new Vessel();
//			newParam.setVessel_name(param.getVessel_abbr());
//			
//			Vessel parentB=vesselDAO.selectVessel(param);
//			
//			if(parentB != null)
//				throw new AlreadyExistException("vessel_name:"+param.getVessel_abbr());
//			
//
//			vesselDAO.insertDetail(param);
//
//		} catch (SQLException e1) {
//			if(e1.getErrorCode()==2627)
//			{
//
//				throw new AlreadyExistException("exist");
//
//
//			}else
//			{
//
//				e1.printStackTrace();
//			}
//		}
//	}

	@Override
	public void insertDetail(HashMap<String, Object> param) throws RuntimeException {

		log.info("param:{}", param);
		try {

			Vessel vessel = new Vessel();
			vessel.setVessel_name(String.valueOf(param.get("vessel_name")));
			vessel.setVessel_abbr(String.valueOf(param.get("vessel_abbr")));

			Vessel parent=vesselDAO.selectVessel(vessel);

			if(parent == null)
				throw new ResourceNotFoundException("vessel_name:"+vessel.getVessel_name());
			
			
			// 선막명 약어가 기존에 다른 선박명과 같을 시에 에러 발생
			
			Vessel newParam = new Vessel();
			newParam.setVessel_name(vessel.getVessel_abbr());
						
			Vessel parentB=vesselDAO.selectVessel(newParam);
						
			if(parentB != null)
					throw new AlreadyExistException("vessel_name:"+newParam.getVessel_name());

			vesselDAO.insertDetail(vessel);

		} catch (SQLException e1) {
			if(e1.getErrorCode()==2627)
			{
				throw new AlreadyExistException("exist");

			}else
			{

				e1.printStackTrace();
			}
		}
	}

	@Override
	public Vessel select(String vessel_name) throws SQLException {

		Vessel vessel = new Vessel();
		vessel.setVessel_name(vessel_name);

		return vesselDAO.selectVessel(vessel);
	}

	@Override
	public HashMap<String, Object> selectList(Vessel commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Object> selectDetailList(Vessel commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *선박명 약어 기준으로 전체 선박 목록 반환
	 */
	@Override
	public HashMap<String, Object> selectTotalList() throws SQLException {
		
		List<Vessel> re = vesselDAO.selectTotalList();
		
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		
		for(Vessel item:re)
		{
			returnMap.put(item.getVessel_abbr(), item);
		}
		log.debug("vessel list size:{}", returnMap.size());
		return returnMap;
	}

	@Override
	public List<HashMap<String, Object>> selectListByLike(HashMap<String, Object> param) throws SQLException {
		log.info("param:{}", param);
		Vessel param2 = Vessel.builder().vessel_name((String)param.get("vessel_name")).build();
		
		
		List<Vessel> li = vesselDAO.selectVesselListByLike(param2);
		
		ArrayList<HashMap<String, Object>> map = new ArrayList<HashMap<String, Object>>();
		
		for(Vessel item:li)
		{	
			map.add((HashMap<String, Object>) objectMapper.convertValue(item, Map.class));
		}
		
		
		return map;
	}
	
	@Override
	public List<Vessel> selectListByCondition(HashMap<String, Object> param) throws SQLException {
		return vesselDAO.selectVesselListByCondition(param);
	}

	@Override
	public List<Vessel> selectVesselListByNameList(List<String> nameList) throws SQLException {
		// TODO Auto-generated method stub
		return vesselDAO.selectVesselListByNameList(nameList);
	}

	@Override
	public List<Vessel> selectAllList() throws SQLException {
		// TODO Auto-generated method stub
		return vesselDAO.selectAll();
	}
}
