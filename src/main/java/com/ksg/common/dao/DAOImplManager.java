package com.ksg.common.dao;

import com.ksg.adv.dao.AdvDAOImpl;
import com.ksg.dao.impl.BaseDAOManager;

public class DAOImplManager {
	
	private DAOImplManager() {
		
	}
	private static DAOImplManager instance;
	public static DAOImplManager getInstance()
	{
		if(instance==null)
		instance = new DAOImplManager();
		return instance;
	}
	public AdvDAOImpl createADVDAOImpl()
	{
		return new AdvDAOImpl();
	}
	public BaseDAOManager createBaseDAOImpl()
	{
		return new BaseDAOManager();
	}

}
