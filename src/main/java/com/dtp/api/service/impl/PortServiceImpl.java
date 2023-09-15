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

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� : �ױ����� ���� ����
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
        
        if(selectOne==null)  throw new ResourceNotFoundException("�ش� �ױ��� �����ϴ�.");
        
        dao.deletePort(id);

        return selectOne;
    }

    public PortInfo deleteDetail(PortInfo param) throws Exception
    {
        log.debug("detail id:{}",param);
        
        PortInfo selectOne= dao.selectDetailByKey(param);
        
        if(selectOne==null)
        {
            throw new ResourceNotFoundException("�ش� �ױ��� �����ϴ�.");  
        }
        dao.deletePortDetail(param);

        return selectOne;
    }
    
    @Override
	public int deletePortDetail(PortInfo port) throws Exception 
    {
    	 log.debug("detail port:{}",port);
    	 
         PortInfo selectOne= dao.selectDetailByKey(port);
         
         if(selectOne==null) throw new ResourceNotFoundException("�ش� �ױ��� �����ϴ�.");  

         return (int) dao.deletePortDetail(port);
	}
    
    public PortInfo insertPort(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        
        PortInfo selectOne= dao.selectPortById(param.getPort_name());
        
        if(selectOne!=null) throw new AlreadyExistException("("+param.getPort_name()+")�����ϴ� �ױ����Դϴ�.");

        dao.insertPort(param);

        return selectOne;
    }

    public PortInfo insertPortDetail(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        
        PortInfo selectOne= dao.selectDetailByKey(param);
        
        if(selectOne!=null) throw new AlreadyExistException("("+param.getPort_name()+")�����ϴ� �ױ����Դϴ�.");

        Object result = dao.insertPortDetail(param);

        return selectOne;
    }

    public PortInfo updatePort(PortInfo param) throws Exception
    {
        log.debug("param:{}",param);
        
        PortInfo selectOne= dao.selectPortById(param.getPort_name());
        
        if(selectOne==null) throw new ResourceNotFoundException("("+param.getPort_name()+")�ױ����� �������� �ʽ��ϴ�.");

        dao.updatePort(param);

        return selectOne;
    }
}
