package com.dtp.api.control;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.service.PortService;
import com.dtp.api.service.impl.PortServiceImpl;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;
import com.ksg.service.AreaService;
import com.ksg.service.impl.AreaServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PortController  extends AbstractController{
	
	 
	
	private AreaService areaService;
	
    /* 항구정보관리 서비스 */
    private PortService service;    
    
    public PortController()
    {
        super();
        service = new PortServiceImpl();
        
        areaService = new AreaServiceImpl();

    }
    @ControlMethod(serviceId = "selectPort")
    public CommandMap selectByCondtion(CommandMap param) throws SQLException
    {
        log.info("param:{}",param);

        String port_name =(String) param.get("port_name");
        String area_code =(String) param.get("area_code");
        String port_area =(String) param.get("port_area");
        
        PortInfo port = PortInfo.builder().port_name(port_name)
                                    .area_code(area_code)
                                    .port_area(port_area)
                                    .build();

        List<PortInfo> result = service.selectListByCondtion(port);


        CommandMap model = new CommandMap();

        List<CommandMap> resultArry=result.stream()
                        .map(o -> objectMapper.convertValue(o, CommandMap.class))
                        .collect(Collectors.toList());

        model.put("success", true);
        model.put("data", resultArry);

        return model;
        
    }
    
    @ControlMethod(serviceId = "selectPortDetailList")
    public CommandMap selectPortDeatilListByPortName(CommandMap param) throws SQLException
    {
        log.info("param:{}",param);

        String port_name =(String) param.get("port_name");
        
        if(port_name== null) throw new NullPointerException("port_name is null");

        List<PortInfo> result = service.selectPortDetailListByPortName(port_name);

        CommandMap model = new CommandMap();

        List<CommandMap> resultArry=result.stream()
                        .map(o -> objectMapper.convertValue(o, CommandMap.class))
                        .collect(Collectors.toList());

        model.put("success", true);
        
        model.put("data", resultArry);

        return model;
        
    }
    @ControlMethod(serviceId = "insertPort")
    public CommandMap insertPort(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        String port_name = (String) param.get("port_name");
        String port_area = (String) param.get("port_area");
        String port_nationality = (String) param.get("port_nationality");
        String area_code = (String) param.get("area_code");
        String contents = (String) param.get("contents");

        PortInfo port = PortInfo.builder()
                                .port_name(port_name)
                                .port_area(port_area)
                                .port_nationality(port_nationality)
                                .area_code(area_code)
                                
                                .build();

        PortInfo result = service.insert(port);                                
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);
        return returnMap;
    }

    @ControlMethod(serviceId = "insertPortDetail")
    public CommandMap insertPortDetail(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        String port_name = (String) param.get("port_name");
        String port_abbr = (String) param.get("port_abbr");

        PortInfo port = PortInfo.builder()
                                .port_name(port_name)
                                .port_abbr(port_abbr)
                                .build();

        PortInfo result = service.insertDetail(port);   
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("success", true);
        
        returnMap.put("data", result);
        
        return returnMap;
    }

    @ControlMethod(serviceId = "deletePort")
    public CommandMap deletePort(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        
        String port_name = (String) param.get("port_name");
        
        PortInfo result=service.delete(port_name);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("success", true);
        
        returnMap.put("data", result);

        return returnMap;
    }

    @ControlMethod(serviceId = "deletePortDetail")
    public CommandMap deletePortDetail(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        String port_name = (String) param.get("port_name");
        String port_abbr = (String) param.get("port_abbr");
        PortInfo portParam = PortInfo.builder()
        							.port_name(port_name)
        							.port_abbr(port_abbr)
        							.build();
        
       service.deleteDetail(portParam);
        
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        //returnMap.put("data", result);

        return returnMap;
    }

    @ControlMethod(serviceId = "updatePort")
    public CommandMap updatePort(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        
//        int id = (int) param.get("id");
        
        String port_name = (String) param.get("port_name");
        
        String port_area = (String) param.get("port_area");
        
        String port_nationality = (String) param.get("port_nationality");

        String area_code = (String) param.get("area_code");
        
        String contents = (String) param.get("contents");

        PortInfo port = PortInfo.builder()
//                                .id(id)
                                .port_name(port_name)
                                .port_area(port_area)
                                .port_nationality(port_nationality)
                                .area_code(area_code)
//                                .contents(contents)
                                .build();

        PortInfo result = service.update(port);
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);
        return returnMap;
    }

    @ControlMethod(serviceId = "updatePortDetail")
    public CommandMap updatePortDetail(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        int id = (int) param.get("id");
        String port_name = (String) param.get("port_name");
        String port_area = (String) param.get("port_area");
        String port_nationality = (String) param.get("port_nationality");

        String area_code = (String) param.get("area_code");
        String contents = (String) param.get("contents");

        PortInfo port = PortInfo.builder()
//                                .id(id)
                                .port_name(port_name)
                                .port_area(port_area)
                                .port_nationality(port_nationality)
                                .area_code(area_code)
//                                .contents(contents)
                                .build();

        PortInfo result = service.update(port);
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);
        return returnMap;
    }
    
    @ControlMethod(serviceId = "portDialog.init")
    public CommandMap dialogInit(CommandMap param) throws Exception
    {
    	
    	CommandMap returnMap = new CommandMap();
    	
    	List<CommandMap> areaMap = areaService.selectAreaInfoList();
		
		returnMap.put("areaMap", areaMap);
		
    	return returnMap;
    }
    


}
