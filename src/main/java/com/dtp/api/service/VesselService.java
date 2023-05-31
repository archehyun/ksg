package com.dtp.api.service;

import java.util.List;

import com.ksg.domain.Vessel;

/**
 * 

  * @FileName : VesselService.java

  * @Project : KSG2

  * @Date : 2023. 5. 31. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :
 */
public interface VesselService {

	public List<Vessel> selectAll() throws Exception;

	public Vessel selectById(String id) throws Exception;

	public List<Vessel> selectListByCondtion(Vessel param) throws Exception;

	public List<Vessel> selectListByKeys(List names) throws Exception;
	
	public List<Vessel> selectDetailList(String param) throws Exception;

	public Vessel selectDeatileById(String id)throws Exception;

	public Vessel updateVessel(Vessel param) throws Exception;

	public Vessel updateVesselDetail(Vessel param) throws Exception;

	public Vessel insertVessel(Vessel vessel) throws Exception;
	
	public Vessel insertVesselDetail(Vessel param) throws Exception;

	public Vessel deleteVessel(String id) throws Exception;

	public Vessel deleteVesselDetail(String id) throws Exception;

}
