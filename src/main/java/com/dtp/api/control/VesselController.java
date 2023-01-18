package com.dtp.api.control;

import java.util.List;
import java.util.stream.Collectors;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.service.VesselService;
import com.dtp.api.service.impl.VesselServiceImpl;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.Vessel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VesselController extends AbstractController{
	
	private VesselService service;

    public VesselController() {
        super();
        service = new VesselServiceImpl();
    }

    @ControlMethod(serviceId = "selectVessel")
    public CommandMap selectByCondtion(CommandMap param) throws Exception
    {
        log.info("param:{}",param);

        String vessel_name =(String) param.get("vessel_name");
        String vessel_type =(String) param.get("vessel_type");
        Integer vessel_use =(Integer) param.get("vessel_use");
        String vessel_company =(String) param.get("vessel_company");
        Vessel vessel = Vessel.builder()
                                .vessel_name(vessel_name)
                                .vessel_use(vessel_use)
                                .vessel_type(vessel_type)
                                .vessel_company(vessel_company)
                                .build();

        List<Vessel> result = service.selectListByCondtion(vessel);

//        result.stream().forEach(o -> {
//            try {
//                o.setEvent_date( DateUtil.convertType(o.getEvent_date()));
//            } catch (Exception e) {
//                //o.setEvent_date()
//            }
//        });

        CommandMap model = new CommandMap();

        List<CommandMap> resultArray=result.stream()
                        .map(o -> objectMapper.convertValue(o, CommandMap.class))
                        .collect(Collectors.toList());
                        
        model.put("success", true);
        model.put("data", resultArray);

        return model;
        
    }
    
    @ControlMethod(serviceId = "insertVessel")
    public CommandMap insertVessel(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        String vessel_name = (String) param.get("vessel_name");
        String vessel_abbr = (String) param.get("vessel_abbr");
        String vessel_type = (String) param.get("vessel_type");
        String vessel_company = (String) param.get("vessel_company");
        String vessel_mmsi = (String) param.get("vessel_mmsi");
        String contents =(String) param.get("contents");
        Integer vessel_use = (Integer) param.get("vessel_use");
        

        Vessel vessel = Vessel.builder()
                                .vessel_name(vessel_name)
                                .vessel_abbr(vessel_abbr)
                                .vessel_type(vessel_type)
                                .vessel_company(vessel_company)
                                .vessel_mmsi(vessel_mmsi)
                                .vessel_use(vessel_use)                                
                                .contents(contents)
                                .build();

        Vessel result =service.insert(vessel);                                
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);
        return returnMap;
    }

    @ControlMethod(serviceId = "deleteVessel")
    public CommandMap deleteVessel(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        String id = (String) param.get("vessel_name");
        Vessel result=service.delete(id);
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);

        return returnMap;
    }

    @ControlMethod(serviceId = "deleteVesselDetail")
    public CommandMap deleteVesselDetail(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        String id = (String) param.get("id");
        Vessel result=service.delete(id);
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);

        return returnMap;
    }

    @ControlMethod(serviceId = "updateVessel")
    public CommandMap updateVessel(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        int id = (int) param.get("id");
        String vessel_name = (String) param.get("vessel_name");
        String vessel_abbr = (String) param.get("vessel_abbr");
        String vessel_type = (String) param.get("vessel_type");
        String vessel_mmsi = (String) param.get("vessel_mmsi");
        String vessel_company = (String) param.get("vessel_company");
        String contents =(String) param.get("contents");
        Integer vessel_use = (Integer) param.get("vessel_use");
        

        Vessel vessel = Vessel.builder()
                                
                                .vessel_name(vessel_name)
                                .vessel_abbr(vessel_abbr)
                                .vessel_type(vessel_type)
                                .vessel_mmsi(vessel_mmsi)
                                .vessel_company(vessel_company)
                                .vessel_use(vessel_use)                                
                                .contents(contents)
                                .build();
                                
        Vessel result = service.update(vessel);
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);
        return returnMap;
    }

    @ControlMethod(serviceId = "updateVesselDetail")
    public CommandMap updateVesselDetail(CommandMap param) throws Exception
    {
        log.info("param:{}",param);
        int id = (int) param.get("id");
        String vessel_name = (String) param.get("vessel_name");
        String vessel_abbr = (String) param.get("vessel_abbr");
        String vessel_type = (String) param.get("vessel_type");
        String vessel_mmsi = (String) param.get("vessel_mmsi");
        String vessel_company = (String) param.get("vessel_company");
        String contents =(String) param.get("contents");
        Integer vessel_use = (Integer) param.get("vessel_use");
        

        Vessel vessel = Vessel.builder()
                                
                                .vessel_name(vessel_name)
                                .vessel_abbr(vessel_abbr)
                                .vessel_type(vessel_type)
                                .vessel_mmsi(vessel_mmsi)
                                .vessel_company(vessel_company)
                                .vessel_use(vessel_use)                                
                                .contents(contents)
                                .build();
                                
        Vessel result = service.update(vessel);
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);
        return returnMap;
    }

}
