package com.dtp.api.control;

import com.dtp.api.annotation.ControlMethod;
import com.ksg.common.model.CommandMap;
import com.ksg.service.TableService;
import com.ksg.service.impl.TableServiceImpl;

public class MainController extends AbstractController{
	
	private TableService tableService;
	
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
		
		
		return model;
    }
	
	@ControlMethod(serviceId = "searchTableCount")
    public CommandMap searchTableCount(CommandMap param) throws Exception
    {
		String date = (String) param.get("searchDate");
		
		int result=tableService.getTableCount(date);
								
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        model.put("searchDate", param.get("searchDate"));
        
        model.put("count", result);
		
		
		return model;
    }
	


}
