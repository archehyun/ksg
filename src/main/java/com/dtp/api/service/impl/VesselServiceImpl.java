package com.dtp.api.service.impl;

import java.util.List;

import com.dtp.api.dao.VesselDAO;
import com.dtp.api.exception.AlreadyExistException;
import com.dtp.api.exception.ResourceNotFoundException;
import com.dtp.api.service.VesselService;
import com.ksg.domain.Vessel;

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
	public List<Vessel> selectListByCondtion(Vessel param)throws Exception
	{
		List result = dao.selectListByCondition(param);

		return result;
	}
	@Override
	public List<Vessel> selectDetailList(String vessel_name) throws Exception 
	{
		List result = dao.selectDetailList(vessel_name);

		return result;
	}
	@Override
	public List<Vessel> selectListByKeys(List names) throws Exception
	{
		List result = dao.selectByVesselNames(names);
		return result;
	}
	@Override
	public Vessel delete(String id) throws Exception
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
	public Vessel deleteDetail(String id) throws Exception
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
	public Vessel insert(Vessel param) throws Exception
	{
		Vessel selectOne= dao.selectById(param.getVessel_name());
		if(selectOne!=null)
			throw new AlreadyExistException("("+param.getVessel_name()+")존재하는 선박명입니다.");

		dao.insertVessel(param);

		return selectOne;
	}   
	@Override
	public Vessel insertDetail(Vessel param) throws Exception
	{
		Vessel selectOne= dao.selectById(param.getVessel_name());
		if(selectOne!=null)
			throw new AlreadyExistException("("+param.getVessel_name()+")존재하는 선박명입니다.");

		dao.insertVessel(param);

		return selectOne;
	}
	@Override
	public Vessel update(Vessel param) throws Exception
	{
		Vessel selectOne= dao.selectById(param.getVessel_name());
		if(selectOne==null)
			throw new ResourceNotFoundException("("+param.getVessel_name()+")선박명이 존재하지 않습니다.");

		Object result = dao.updateVessel(param);

		return selectOne;
	}
	@Override
	public Vessel updateDetail(Vessel param) throws Exception
	{
		Vessel selectOne= dao.selectById(param.getVessel_abbr());
		if(selectOne==null)
			throw new ResourceNotFoundException("("+param.getVessel_abbr()+")선박명이 존재하지 않습니다.");

		Object result = dao.updateVessel(param);

		return selectOne;
	}



}
