package com.dtp.api.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dtp.api.dao.PortDAO;
import com.dtp.api.exception.AlreadyExistException;
import com.dtp.api.exception.ResourceNotFoundException;
import com.dtp.api.service.PortService;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PortServiceImpl implements PortService{
	
	private PortDAO dao;
	
	public PortServiceImpl()
    {
        dao = new PortDAO();
    
    }
    public PortInfo selectById(String id)
    {
        log.debug("id:{}",id);
        return dao.selectById(id);
    }

    public List<PortInfo> selectAll()
    {
        return dao.selectAll();
    }

    public List<PortInfo> selectListByCondtion(PortInfo param) throws SQLException
    {
        List result = dao.selectListByCondition(param);

        
        return result;
    }
    
	@Override
	public List<PortInfo> selectPortDetailListByPortName(String port_name) throws SQLException {
		// TODO Auto-generated method stub
		return dao.selectPortDetailListByPortName(port_name);
	}


    public CommandMap selectMapAll()
    {
        List<PortInfo> result = selectAll();
        log.info("result:{}",result);

		ArrayList<CommandMap> data = new ArrayList<CommandMap>();

		CommandMap resultMap = new CommandMap();
		resultMap.put("success", true);
		resultMap.put("data", data);
        return resultMap;
    }


    public PortInfo delete(String id) throws Exception
    {
        log.debug("id:{}",id);
        PortInfo selectOne= dao.selectById(id);
        if(selectOne==null)
        {
            throw new ResourceNotFoundException("해당 선사가 없습니다.");  
        }
        dao.deletePort(id);

        return selectOne;
    }

    public PortInfo deleteDetail(String id) throws Exception
    {
        log.debug("detail id:{}",id);
        PortInfo selectOne= dao.selectById(id);
        if(selectOne==null)
        {
            throw new ResourceNotFoundException("해당 선사가 없습니다.");  
        }
        dao.deletePortDetail(id);

        return selectOne;
    }

    public PortInfo insert(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        PortInfo selectOne= dao.selectByKey(param.getPort_name());
        if(selectOne!=null)
            throw new AlreadyExistException("("+param.getPort_name()+")존재하는 항구명입니다.");

        dao.insertPort(param);

        return selectOne;
    }

    public PortInfo insertDetail(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        PortInfo selectOne= dao.selectByKey(param.getPort_name());
        if(selectOne!=null)
            throw new AlreadyExistException("("+param.getPort_name()+")존재하는 항구명입니다.");

        int result = dao.insertPortDetail(param);

        return selectOne;
    }

    public PortInfo update(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        PortInfo selectOne= dao.selectById(param.getPort_abbr());
        if(selectOne==null)
            throw new ResourceNotFoundException("("+param.getPort_name()+")항구명이 존재하지 않습니다.");

        int result = dao.updatePort(param);

        return selectOne;
    }
    public PortInfo updateDetail(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        PortInfo selectOne= dao.selectById(param.getPort_abbr());
        if(selectOne==null)
            throw new ResourceNotFoundException("("+param.getPort_name()+")항구명이 존재하지 않습니다.");

        dao.updatePortDetail(param);

        return selectOne;
    }


}
