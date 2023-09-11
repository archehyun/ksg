package com.dtp.api.service.impl;

import java.util.List;

import com.dtp.api.dao.PortDAO;
import com.dtp.api.exception.AlreadyExistException;
import com.dtp.api.exception.ResourceNotFoundException;
import com.dtp.api.service.PortService;
import com.ksg.domain.PortInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 

  * @FileName : PortServiceImpl.java

  * @Project : KSG2

  * @Date : 2023. 5. 31. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 항구정보 관리 서비스
 */
@Slf4j
public class PortServiceImpl implements PortService{
	
	private PortDAO dao;
	
	public PortServiceImpl()
    {
        dao = new PortDAO();
    }
	
	@Override
    public PortInfo selectById(String port_name) throws Exception
    {
        log.debug("id:{}",port_name);
        
        return dao.selectPortById(port_name);
    }

    public List<PortInfo> selectListByCondtion(PortInfo param) throws Exception
    {
        List result = dao.selectListByCondition(param);
        
        return result;
    }
    
	@Override
	public List<PortInfo> selectPortDetailListByPortName(String port_name) throws Exception {
		return dao.selectPortDetailListByPortName(port_name);
	}

    public PortInfo deletePort(String id) throws Exception
    {
        log.debug("id:{}",id);
        
        PortInfo selectOne= dao.selectPortById(id);
        
        if(selectOne==null)  throw new ResourceNotFoundException("해당 항구가 없습니다.");
        
        dao.deletePort(id);

        return selectOne;
    }

    public PortInfo deleteDetail(PortInfo param) throws Exception
    {
        log.debug("detail id:{}",param);
        
        PortInfo selectOne= dao.selectDetailByKey(param);
        
        if(selectOne==null)
        {
            throw new ResourceNotFoundException("해당 항구가 없습니다.");  
        }
        dao.deletePortDetail(param);

        return selectOne;
    }
    
    @Override
	public int deletePortDetail(PortInfo port) throws Exception 
    {
    	 log.debug("detail port:{}",port);
    	 
         PortInfo selectOne= dao.selectDetailByKey(port);
         
         if(selectOne==null) throw new ResourceNotFoundException("해당 항구가 없습니다.");  

         return (int) dao.deletePortDetail(port);
	}
    
    public PortInfo insertPort(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        
        PortInfo selectOne= dao.selectPortById(param.getPort_name());
        
        if(selectOne!=null) throw new AlreadyExistException("("+param.getPort_name()+")존재하는 항구명입니다.");

        dao.insertPort(param);

        return selectOne;
    }

    public PortInfo insertPortDetail(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        
        PortInfo selectOne= dao.selectDetailByKey(param);
        
        if(selectOne!=null) throw new AlreadyExistException("("+param.getPort_name()+")존재하는 항구명입니다.");

        Object result = dao.insertPortDetail(param);

        return selectOne;
    }

    public PortInfo updatePort(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        
        PortInfo selectOne= dao.selectPortById(param.getPort_name());
        
        if(selectOne==null) throw new ResourceNotFoundException("("+param.getPort_name()+")항구명이 존재하지 않습니다.");

        dao.updatePort(param);

        return selectOne;
    }
}
