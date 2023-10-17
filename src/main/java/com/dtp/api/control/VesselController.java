package com.dtp.api.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.model.ExcelExport;
import com.dtp.api.service.VesselService;
import com.dtp.api.service.impl.CodeServiceImpl;
import com.dtp.api.service.impl.VesselServiceImpl;
import com.dtp.api.util.ExcelUtil;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.Code;
import com.ksg.domain.Vessel;

import lombok.extern.slf4j.Slf4j;

/**
 * 

  * @FileName : VesselController.java

  * @Project : KSG2

  * @Date : 2023. 5. 31. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :  선박 정보 관리
 */
@Slf4j
public class VesselController extends AbstractController{
	
	private VesselService service;
	
	private CodeServiceImpl codeService;

    public VesselController() {
        super();
        codeService= new CodeServiceImpl();
        service = new VesselServiceImpl();
    }

    /**
     * 
     * @param param
     * @return
     * @throws Exception
     */
    @ControlMethod(serviceId = "selectVessel")
    public CommandMap selectByCondtion(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);

        String vessel_name 		= (String) param.get("vessel_name");
        String vessel_type 		= (String) param.get("vessel_type");
        String vessel_company 	= (String) param.get("vessel_company");
        
        Vessel vessel = Vessel.builder()
                                .vessel_name(vessel_name)
                                //TODO 개선
                                .vessel_use(param.containsKey("vessel_use")? (Integer) param.get("vessel_use"):-1)
                                .vessel_type(vessel_type)
                                .vessel_company(vessel_company)
                                .build();

        List<Vessel> result = service.selectListByCondtion(vessel);

        CommandMap model = new CommandMap();

        List<CommandMap> resultArray=result.stream()
                        .map(o -> objectMapper.convertValue(o, CommandMap.class))
                        .collect(Collectors.toList());
                        
        model.put("success", true);
        
        model.put("data", resultArray);
        
        log.info("end");

        return model;
    }
    
    /**
     * 
     * @param param
     * @return
     * @throws Exception
     */
    @ControlMethod(serviceId = "selectVesselDetailList")
    public CommandMap selectVesselDetailList(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
    	
    	String vesselName = (String) param.get("vessel_name");
    	
    	List<Vessel> result = service.selectDetailList(vesselName);
    	
    	CommandMap model = new CommandMap();

        List<CommandMap> resultArray=result.stream()
                         .map(o -> objectMapper.convertValue(o, CommandMap.class))
                         .collect(Collectors.toList());
        
        model.put("data", resultArray);
        
        log.info("end");

        return model;
    }
    @ControlMethod(serviceId = "insertVessel")
    public CommandMap insertVessel(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        String vessel_name 		= (String) param.get("vessel_name");
        
        String vessel_abbr 		= (String) param.get("vessel_abbr");
        
        String vessel_type 		= (String) param.get("vessel_type");
        
        String vessel_company 	= (String) param.get("vessel_company");
        
        String vessel_mmsi 		= (String) param.get("vessel_mmsi");
        
        String contents 		= (String) param.get("contents");
        
        Integer vessel_use 		= (Integer) param.get("vessel_use");
        

        Vessel vessel = Vessel.builder()
                                .vessel_name(vessel_name)
                                .vessel_abbr(vessel_abbr)
                                .vessel_type(vessel_type)
                                .vessel_company(vessel_company)
                                .vessel_mmsi(vessel_mmsi)
                                .vessel_use(vessel_use)                                
                                .contents(contents)
                                .build();

        Vessel result =service.insertVessel(vessel);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }
    
    @ControlMethod(serviceId = "insertVesselDetail")
    public CommandMap insertVesselDetail(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        String vessel_name 		= (String) param.get("vessel_name");
        String vessel_abbr 		= (String) param.get("vessel_abbr");

        Vessel vessel = Vessel.builder()
                                .vessel_name(vessel_name)
                                .vessel_abbr(vessel_abbr)
                                .build();
        

        Vessel result =service.insertVesselDetail(vessel);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }

    @ControlMethod(serviceId = "deleteVessel")
    public CommandMap deleteVessel(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        String id = (String) param.get("vessel_name");
        
        Vessel result=service.deleteVessel(id);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");

        return returnMap;
    }

    @ControlMethod(serviceId = "deleteVesselDetail")
    public CommandMap deleteVesselDetail(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        String vessel_name = (String) param.get("vessel_name");
        String vessel_abbr = (String) param.get("vessel_abbr");
        
        Vessel vesselparam = Vessel.builder().vessel_name(vessel_name).vessel_abbr(vessel_abbr).build();
        
        int result=service.deleteVesselDetail(vesselparam);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");

        return returnMap;
    }

    @ControlMethod(serviceId = "updateVessel")
    public CommandMap updateVessel(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        String vessel_name = (String) param.get("vessel_name");
        String vessel_abbr = (String) param.get("vessel_abbr");
        String vessel_type = (String) param.get("vessel_type");
        String vessel_mmsi = (String) param.get("vessel_mmsi");
        String vessel_company = (String) param.get("vessel_company");
        Integer vessel_use = (Integer) param.get("vessel_use");

        Vessel vessel = Vessel.builder()
                                .vessel_name(vessel_name)
                                .vessel_abbr(vessel_abbr)
                                .vessel_type(vessel_type)
                                .vessel_mmsi(vessel_mmsi)
                                .vessel_company(vessel_company)
                                .vessel_use(vessel_use)                                
                                .build();
                                
        Vessel result = service.updateVessel(vessel);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }

    @ControlMethod(serviceId = "updateVesselDetail")
    public CommandMap updateVesselDetail(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
        
        int id = (int) param.get("id");
        
        String vessel_name 		= (String) param.get("vessel_name");
        String vessel_abbr 		= (String) param.get("vessel_abbr");
        String vessel_type 		= (String) param.get("vessel_type");
        String vessel_mmsi 		= (String) param.get("vessel_mmsi");
        String vessel_company 	= (String) param.get("vessel_company");
        String contents 		= (String) param.get("contents");
        Integer vessel_use 		= (Integer) param.get("vessel_use");

        Vessel vessel 			= Vessel.builder()
                                
                                .vessel_name(vessel_name)
                                .vessel_abbr(vessel_abbr)
                                .vessel_type(vessel_type)
                                .vessel_mmsi(vessel_mmsi)
                                .vessel_company(vessel_company)
                                .vessel_use(vessel_use)                                
                                .contents(contents)
                                .build();
                                
        Vessel result = service.updateVessel(vessel);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }
    
    @ControlMethod(serviceId = "pnVessel.init")
    public CommandMap init(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
    	
		Code codeParam = Code.builder().code_type((String) param.get("code_type")).build();

		List<Code> result= codeService.selectCodeDetailListByCondition(codeParam);

        List<CommandMap> resultArry=result.stream()
                        .map(o -> objectMapper.convertValue(o, CommandMap.class))
                        .collect(Collectors.toList());

        CommandMap returnMap = new CommandMap();
	  
        returnMap.put("data", resultArry);
        
        log.info("end");
      
        return returnMap;
    }
    @ControlMethod(serviceId = "pnVessel.export")
    public CommandMap export(CommandMap param) throws Exception
    {
    	log.info("start:{}",param);
    	
    	List<Vessel> vesselList=service.selectAll();
    	
    	List<Vessel> vesselDetailList=service.selectDetailAll();
    	
    	String column_vessel[]= {"vessel_name", "vessel_type", "vessel_mmsi", "vessel_company", "vessel_use", "input_date"};
    	
    	String column_vessel_abbr[]= {"vessel_name", "vessel_abbr", "input_date"};
    	
        List<CommandMap> vesselArray=(List<CommandMap>) vesselList.stream()
                .map(o -> objectMapper.convertValue(o, CommandMap.class))
                .collect(Collectors.toList());
        
        List<CommandMap> vesselAbbrArray=(List<CommandMap>) vesselDetailList.stream()
                .map(o -> objectMapper.convertValue(o, CommandMap.class))
                .collect(Collectors.toList());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        
        String fileNameAndTime = String.format("%s_%s.xls", "vessel", dateFormat.format(new Date())) ;
        
        ArrayList<ExcelExport> exportList = new ArrayList<ExcelExport>();
        
        exportList.add(new ExcelExport("TB_VESSEL_H", column_vessel, vesselArray));
        
        exportList.add(new ExcelExport("TB_VESSEL_ABBR", column_vessel_abbr, vesselAbbrArray));
    	
    	ExcelUtil.exportData(fileNameAndTime,exportList);
		
        CommandMap returnMap = new CommandMap();
        
        log.info("end");
      
        return returnMap;
    }
}
