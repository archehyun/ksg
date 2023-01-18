package com.dtp.api.service;

import java.util.List;

import com.ksg.domain.Vessel;

public interface VesselService {

	public Vessel insert(Vessel vessel) throws Exception;

	Vessel selectById(String id) throws Exception;

	List<Vessel> selectListByCondtion(Vessel param) throws Exception;

	List<Vessel> selectAll() throws Exception;

	Vessel selectDeatileById(String id)throws Exception;

	Vessel update(Vessel param) throws Exception;

	Vessel updateDetail(Vessel param) throws Exception;

	Vessel insertDetail(Vessel param) throws Exception;

	List<Vessel> selectListByKeys(List names) throws Exception;

	Vessel delete(String id) throws Exception;

	List<Vessel> selectDetailList(String param) throws Exception;

	Vessel deleteDetail(String id) throws Exception;

}
