package com.ksg.commands.schedule;

import java.sql.SQLException;
import java.util.Date;

import com.ksg.commands.IFCommand;
import com.ksg.commands.schedule.create.CreateNormalSchdeduleCommand;
import com.ksg.common.dao.DAOManager;
import com.ksg.domain.ShippersTable;
import com.ksg.service.ADVService;
import com.ksg.service.BaseService;
import com.ksg.service.ScheduleService;
import com.ksg.service.TableService;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

public class BuildScheduleAndCreateXMLCommand implements IFCommand{
	
	
	private TableService tableService;
	private ScheduleService scheduleService;
	private BaseService baseService;
	private ADVService advService;
	ScheduleBuildMessageDialog processMessageDialog;
	private Date selectedDate;
	private ShippersTable searchOption;
	public BuildScheduleAndCreateXMLCommand()
	{
		scheduleService = DAOManager.getInstance().createScheduleService();
	}
	public BuildScheduleAndCreateXMLCommand(ScheduleBuildMessageDialog ob) {
		tableService = DAOManager.getInstance().createTableService();
		scheduleService = DAOManager.getInstance().createScheduleService();
		advService = DAOManager.getInstance().createADVService();
		baseService = DAOManager.getInstance().createBaseService();
		this.processMessageDialog =ob; 
	}
	public BuildScheduleAndCreateXMLCommand(ScheduleBuildMessageDialog ob,ShippersTable op) {
		this(ob);
		searchOption =op;
	}

	public int execute() 
	{
		try {
			scheduleService.deleteSchedule();
			new CreateNormalSchdeduleCommand(searchOption).execute();
			new BuildXMLInboundCommand().execute();
			//new BuildXMLRouteScheduleCommand().execute();
			return RESULT_SUCCESS;
		} catch (SQLException e) {
			e.printStackTrace();
			return RESULT_FAILE;
		}
		
		
	}

}
