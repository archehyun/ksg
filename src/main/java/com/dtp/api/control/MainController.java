package com.dtp.api.control;

import java.util.ArrayList;

import com.dtp.api.annotation.ControlMethod;
import com.ksg.commands.schedule.BuildXMLInboundCommand;
import com.ksg.commands.schedule.BuildXMLOutboundCommand;
import com.ksg.common.model.CommandMap;
import com.ksg.schedule.ScheduleServiceManager;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.TableService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.workbench.adv.ADVManageUI;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.comp.PnArea;
import com.ksg.workbench.master.comp.PnCommonCode;
import com.ksg.workbench.master.comp.PnCompany;
import com.ksg.workbench.master.comp.PnPortNew;
import com.ksg.workbench.master.comp.PnVessel;
import com.ksg.workbench.print.PrintADVUI;
import com.ksg.workbench.schedule.ScheduleMgtUI;
import com.ksg.workbench.shippertable.ShipperTableMgtUI2;

import lombok.extern.slf4j.Slf4j;


/**
 * @author ch.park
 */
@Slf4j
public class MainController extends AbstractController{
	
	private TableService tableService;
	
	ArrayList<Menu> menuList = new ArrayList<MainController.Menu>();
	
	ScheduleServiceManager serviceManager =ScheduleServiceManager.getInstance();
	
	private ScheduleSubService scheduleService= new ScheduleServiceImpl();
	
	public MainController()
	{
		tableService=new TableServiceImpl();
	}
	
	@ControlMethod(serviceId = "showMenu")
    public CommandMap showMenu(CommandMap param) throws Exception
    {								
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        model.put("menuId", param.get("menuId"));
        
        String menuId = (String) param.get("menuId");
        
        if("광고정보 출력".equals(menuId))
        {
        	model.put("view", new PrintADVUI());
        }
        
        else if("기초정보관리".equals(menuId))
        {
        	model.put("view", new BaseInfoUI());
        }
        
        else if("항구정보".equals(menuId))
        {
        	model.put("view", new PnPortNew());
        }
        else if("공통정보".equals(menuId))
        {
        	model.put("view", new PnCommonCode());
        }
        else if("선박정보".equals(menuId))
        {
        	model.put("view", new PnVessel());
        }
        
        else if("지역정보".equals(menuId))
        {
        	model.put("view", new PnArea());
        }
        
        
        else if("선사정보".equals(menuId))
        {
        	model.put("view", new PnCompany());
        }
        else if("Schedule 확인".equals(menuId))
        {
        	model.put("view", new ScheduleMgtUI());
        }
        else if("광고정보 입력".equals(menuId))
        {
        	model.put("view", new ADVManageUI());
        }
        else if("광고정보 조회".equals(menuId))
        {
        	model.put("view", new ShipperTableMgtUI2());
        }
        
        log.info("menuId:{}",menuId);
        
		
		return model;
    }
	
	@ControlMethod(serviceId = "searchTableCount")
    public CommandMap searchTableCount(CommandMap param) throws Exception
    {
		String date = (String) param.get("searchDate");
		
		int result=tableService.getTableCount(date);
								
		CommandMap model = new CommandMap();
        
        model.put("searchDate", param.get("searchDate"));
        
        model.put("count", result);
		
		return model;
    }
	
	@ControlMethod(serviceId = "schedule.inbound")
    public CommandMap scheduleInbound(CommandMap param) throws Exception
    {
		CommandMap model = new CommandMap();
		
		BuildXMLInboundCommand scheduleCommand = new BuildXMLInboundCommand();

		scheduleCommand.execute();
		
		return model;
    }
	
	@ControlMethod(serviceId = "schedule.outbound")
    public CommandMap scheduleOutbound(CommandMap param) throws Exception
    {
		CommandMap model = new CommandMap();
		
		BuildXMLOutboundCommand scheduleCommand = new BuildXMLOutboundCommand();

		scheduleCommand.execute();
		
		return model;
    }
	
	@ControlMethod(serviceId = "schedule.build")
    public CommandMap scheduleBuild(CommandMap param) throws Exception
    {
		CommandMap model = new CommandMap();
		
		serviceManager.buildSchedule();
		
		return model;
    }
	
	@ControlMethod(serviceId = "schedule.delete")
    public CommandMap scheduleDelete(CommandMap param) throws Exception
    {
		CommandMap model = new CommandMap();
		
		int deleteCount=scheduleService.deleteSchedule();
		
		model.put("deleteCount", deleteCount);
		
		return model;
    }
	
	class Menu
	{
		public Menu(String menuGroup, String menuId, String menuName)
		{
			this.menuGroup = menuGroup;
			this.menuId = menuId;
			this.menuName = menuName;
		}
		private String menuGroup;		
		private String menuId;
		private String parentMenuId;
		private String menuName;
	}
}


