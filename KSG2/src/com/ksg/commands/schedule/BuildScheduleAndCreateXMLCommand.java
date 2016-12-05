package com.ksg.commands.schedule;

import java.sql.SQLException;
import java.util.Date;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.schedule.create.CreateNormalSchdeduleCommand;
import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.ADVService;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.ScheduleService;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ShippersTable;
import com.ksg.view.schedule.dialog.ScheduleBuildMessageDialog;

public class BuildScheduleAndCreateXMLCommand implements KSGCommand{
	
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RESULT_FAILE;
		}
		
		
	}

}
