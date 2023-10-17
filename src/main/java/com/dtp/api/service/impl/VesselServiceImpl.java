package com.dtp.api.service.impl;

import java.util.List;

import com.dtp.api.dao.VesselDAO;
import com.dtp.api.exception.AlreadyExistException;
import com.dtp.api.exception.ResourceNotFoundException;
import com.dtp.api.service.VesselService;
import com.ksg.domain.Vessel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VesselServiceImpl implements VesselService{

	private VesselDAO dao;

	public VesselServiceImpl()
	{
		dao = new VesselDAO();
	}

	@Override
	public Vessel selectById(String id) throws Exception
	{
		return dao.selectById(id);
	}
	@Override
	public Vessel selectDeatileById(String id) throws Exception
	{
		return dao.selectById(id);
	}
	@Override
	public List<Vessel> selectAll() throws Exception
	{
		return dao.selectAll();
	}
	
	@Override
	public List<Vessel> selectDetailAll() throws Exception {

		return dao.selectDetailAll();
	}
	@Override
	public List<Vessel> selectListByCondtion(Vessel param)throws Exception
	{
		log.debug("param:{}", param);
		
		List result = dao.selectListByCondition(param);

		return result;
	}
	@Override
	public List<Vessel> selectDetailList(String vessel_name) throws Exception 
	{
		log.debug("param:{}", vessel_name);
		List result = dao.selectDetailList(vessel_name);

		return result;
	}
	
	
	@Override
	public Vessel selectVesselDetailByKey(Vessel param) throws Exception
	{
		return dao.selectVesselDetailByKey(param);
	}
	@Override
	public Vessel deleteVessel(String id) throws Exception
	{
		Vessel selectOne= dao.selectById(id);
		
		if(selectOne==null)
		{
			throw new ResourceNotFoundException("해당 선박이 없습니다.");  
		}
		dao.deleteVessel(id);

		return selectOne;
	}
	@Override
	public int deleteVesselDetail(Vessel param) throws Exception
	{
		Vessel selectOne= dao.selectVesselDetailByKey(param);
		
		if(selectOne==null)
		{
			throw new ResourceNotFoundException("해당 선박이 없습니다.");  
		}

		return (int) dao.deleteVesselDetail(param);
	}
	@Override
	public Vessel insertVessel(Vessel param) throws Exception
	{
		Vessel selectOne= dao.selectById(param.getVessel_name());
		
		if(selectOne!=null)
			throw new AlreadyExistException("("+param.getVessel_name()+")존재하는 선박명입니다.");

		dao.insertVessel(param);

		return selectOne;
	}   
	@Override
	public Vessel insertVesselDetail(Vessel param) throws Exception
	{
		Vessel selectOne= dao.selectVesselDetailByKey(param);
		
		if(selectOne!=null) throw new AlreadyExistException("("+param.getVessel_name()+")존재하는 선박명입니다.");

		dao.insertVesselDetail(param);

		return param;
	}
	@Override
	public Vessel updateVessel(Vessel param) throws Exception
	{
		Vessel selectOne= dao.selectById(param.getVessel_name());
		if(selectOne==null)
			throw new ResourceNotFoundException("("+param.getVessel_name()+")선박명이 존재하지 않습니다.");

		Object result = dao.updateVessel(param);

		return selectOne;
	}
	@Override
	public Vessel updateVesselDetail(Vessel param) throws Exception
	{
		Vessel selectOne= dao.selectById(param.getVessel_abbr());
		if(selectOne==null)
			throw new ResourceNotFoundException("("+param.getVessel_abbr()+")선박명이 존재하지 않습니다.");

		Object result = dao.updateVessel(param);

		return selectOne;
	}


}
