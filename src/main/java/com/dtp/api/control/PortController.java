package com.dtp.api.control;

import java.util.List;
import java.util.stream.Collectors;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.service.CodeService;
import com.dtp.api.service.PortService;
import com.dtp.api.service.impl.CodeServiceImpl;
import com.dtp.api.service.impl.PortServiceImpl;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.service.AreaService;
import com.ksg.service.impl.AreaServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PortController  extends AbstractController{
	
	private AreaService areaService;
	
    /* 항구정보관리 서비스 */
    private PortService service;
    
    private CodeService codeService;
    
    public PortController()
    {
        super();
        
        service 	= new PortServiceImpl();
        
        areaService = new AreaServiceImpl();
        
        codeService = new CodeServiceImpl();

    }
    @ControlMethod(serviceId = "selectPort")
    public CommandMap selectByCondtion(CommandMap param) throws Exception
    {
        log.info("start:{}",param);

        String port_name = (String) param.get("port_name");
        
        String area_code = (String) param.get("area_code");
        
        String port_area = (String) param.get("port_area");
        
        PortInfo port = PortInfo.builder()
        						.port_name(port_name)
                                .area_code(area_code)
                                .port_area(port_area)
                                .build();

        List<PortInfo> result = service.selectListByCondtion(port);

        CommandMap model = new CommandMap();

        List<CommandMap> resultArry=result.stream()
                        .map(o -> objectMapper.convertValue(o, CommandMap.class))
                        .collect(Collectors.toList());

        model.put("data", resultArry);
        
        log.info("end");

        return model;
        
    }
    
    @ControlMethod(serviceId = "selectPortDetailList")
    public CommandMap selectPortDeatilListByPortName(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);

        String port_name =(String) param.get("port_name");
        
        if(port_name== null) throw new NullPointerException("port_name is null");

        List<PortInfo> result = service.selectPortDetailListByPortName(port_name);

        CommandMap model = new CommandMap();

        List<CommandMap> resultArry=result.stream()
					                        .map(o -> objectMapper.convertValue(o, CommandMap.class))
					                        .collect(Collectors.toList());

        model.put("data", resultArry);
        
        log.info("end");

        return model;
        
    }
    @ControlMethod(serviceId = "insertPort")
    public CommandMap insertPort(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        String port_name 		= (String) param.get("port_name");
        
        String port_area 		= (String) param.get("port_area");
        
        String port_nationality = (String) param.get("port_nationality");
        
        String area_code 		= (String) param.get("area_code");
        
        String contents 		= (String) param.get("contents");

        PortInfo port = PortInfo.builder()
                                .port_name(port_name)
                                .port_area(port_area)
                                .port_nationality(port_nationality)
                                .area_code(area_code)
                                
                                .build();

        PortInfo result = service.insertPort(port);  
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }

    @ControlMethod(serviceId = "insertPortDetail")
    public CommandMap insertPortDetail(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        String port_name = (String) param.get("port_name");
        
        String port_abbr = (String) param.get("port_abbr");

        PortInfo port = PortInfo.builder()
                                .port_name(port_name)
                                .port_abbr(port_abbr)
                                .build();

        PortInfo result = service.insertPortDetail(port);   
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }

    @ControlMethod(serviceId = "deletePort")
    public CommandMap deletePort(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        String port_name = (String) param.get("port_name");
        
        PortInfo result=service.deletePort(port_name);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");

        return returnMap;
    }

    @ControlMethod(serviceId = "deletePortDetail")
    public CommandMap deletePortDetail(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
    	
        String port_name = (String) param.get("port_name");
        
        String port_abbr = (String) param.get("port_abbr");
        
        PortInfo portParam = PortInfo.builder()
        							.port_name(port_name)
        							.port_abbr(port_abbr)
        							.build();
        
       int result =service.deletePortDetail(portParam);
        
        CommandMap returnMap = new CommandMap();
        
        log.info("end");

        return returnMap;
    }

    @ControlMethod(serviceId = "updatePort")
    public CommandMap updatePort(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
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

        PortInfo result = service.updatePort(port);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }

    @ControlMethod(serviceId = "updatePortDetail")
    public CommandMap updatePortDetail(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        int id 					= (int) param.get("id");
        
        String port_name 		= (String) param.get("port_name");
        
        String port_area 		= (String) param.get("port_area");
        
        String port_nationality = (String) param.get("port_nationality");

        String area_code 		= (String) param.get("area_code");
        
        String contents 		= (String) param.get("contents");

        PortInfo port = PortInfo.builder()
//                                .id(id)
                                .port_name(port_name)
                                .port_area(port_area)
                                .port_nationality(port_nationality)
                                .area_code(area_code)
//                                .contents(contents)
                                .build();

        PortInfo result = service.updatePort(port);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }
    
    @ControlMethod(serviceId = "portDialog.init")
    public CommandMap dialogInit(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
    	
    	CommandMap returnMap = new CommandMap();
    	
    	List<CommandMap> areaMap = areaService.selectAreaInfoList();
		
		returnMap.put("areaMap", areaMap);
		
		log.info("end");
		
    	return returnMap;
    }
    
    @ControlMethod(serviceId = "pnPort.init")
    public CommandMap pnPortInit(CommandMap param) throws Exception
    {
    	CommandMap returnMap = new CommandMap();
    	
    	List<AreaInfo> areaList = areaService.selectAll();
    	
		List<String> areaCode=areaList.stream().map(AreaInfo::getArea_code)
				.distinct()
				.collect(Collectors.toList());
		
		List<String> areaName=areaList.stream().map(AreaInfo::getArea_name)
				.distinct()
				.collect(Collectors.toList());
    	
		returnMap.put("areaCode", areaCode);
		
		returnMap.put("areaName", areaName);
		
		log.info("end");
		
    	return returnMap;
    }
    
    @ControlMethod(serviceId = "searchPortDialog.init")
    public CommandMap searchPortDialogInit(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
    	
    	CommandMap returnMap = new CommandMap();
    	
    	Code codeParam = Code.builder().code_type("port_exception").build();
		
		List li=codeService.selectCodeDetailListByCondition(codeParam);
		
		log.info("code list:{}", li.size());
		
        List portExceptionList=(List) li.stream()
						                .map(o -> objectMapper.convertValue(o, CommandMap.class))
						                .collect(Collectors.toList());
        
		
		returnMap.put("portExceptionList", portExceptionList);
		
		log.info("end");
		
    	return returnMap;
    }
    
    @ControlMethod(serviceId = "searchPortDialog.searchPortException")
    public CommandMap ssearchPortException(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
    	
    	CommandMap returnMap = new CommandMap();
    	
    	String code_type = (String) param.get("code_type");
    	
    	String code_name = (String) param.get("code_name");
    	
		Code codeParam = Code.builder().code_type(code_type).code_name(code_name).build();
		
		List<Code> li=codeService.selectCodeDetailListByCondition(codeParam);
		
		log.info("code list:{}", li.size());
		
        List portExceptionList=(List) li.stream()
						                .map(o -> objectMapper.convertValue(o, CommandMap.class))
						                .collect(Collectors.toList());
        
		
		returnMap.put("portExceptionList", portExceptionList);
		
		log.info("end");
		
    	return returnMap;
    }
    
    @ControlMethod(serviceId = "searchPortDialog.searchPort")
    public CommandMap ssearchPort(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
    	
    	CommandMap returnMap = new CommandMap();
    	
    	String port_name = (String) param.get("port_name");
    	
    	PortInfo  portParam = PortInfo.builder().port_name(port_name).build();
    	
		
		List<PortInfo> li=service.selectListByCondtion(portParam);
		
		log.info("code list:{}", li.size());
		
        List portExceptionList=(List) li.stream()
						                .map(o -> objectMapper.convertValue(o, CommandMap.class))
						                .collect(Collectors.toList());
        
		
		returnMap.put("portList", portExceptionList);
		
		log.info("end");
		
    	return returnMap;
    }
    
   
}
