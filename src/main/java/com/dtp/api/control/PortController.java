package com.dtp.api.control;

import java.util.List;
import java.util.stream.Collectors;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.service.PortService;
import com.dtp.api.service.impl.PortServiceImpl;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PortController  extends AbstractController{
	
	 
    /* 항구정보관리 서비스 */
    private PortService service;    
    
    public PortController()
    {
        super();
        service = new PortServiceImpl();

    }
    @ControlMethod(serviceId = "selectPort")
    public CommandMap selectByCondtion(CommandMap param)
    {
        log.info("param:{}",param);

        String port_name =(String) param.get("port_name");
        String area_code =(String) param.get("area_code");
        
        PortInfo port = PortInfo.builder().port_name(port_name)
                                    .area_code(area_code)
                                    .build();

        List<PortInfo> result = service.selectListByCondtion(port);
//        result.stream().forEach(o -> {
//            try {
//                o.setEvent_date( DateUtil.convertType(o.getEvent_date()));
//            } catch (Exception e) {
//                //o.setEvent_date()
//            }
//        });

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
//        var id = (int) param.get("id");
        String port_abbr = (String) param.get("port_abbr");
        PortInfo result=service.deleteDetail(port_abbr);
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);

        return returnMap;
    }

    @ControlMethod(serviceId = "updatePort")
    public CommandMap updatePort(CommandMap param) throws Exception
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


}
