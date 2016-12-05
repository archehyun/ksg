package com.ksg.commands.schedule;

import org.apache.log4j.Logger;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.schedule.route.RouteTaskDate;
import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.ScheduleService;
import com.ksg.domain.ShippersTable;
import com.ksg.quark.XTGManager;
import com.ksg.view.schedule.dialog.ScheduleBuildMessageDialog;

public class BuildXMLRouteScheduleCommand implements KSGCommand {

	XTGManager xtgmanager = new XTGManager();
	private static final String WORLD_SOURCE_XML = "world_source.xml";
	private ScheduleService scheduleService;
	private BaseService baseService;
	DAOManager manager =DAOManager.getInstance();

	protected Logger 			logger = Logger.getLogger(getClass());
	ScheduleBuildMessageDialog di;



	ShippersTable op;
	public BuildXMLRouteScheduleCommand(ShippersTable op) {
		scheduleService = manager.createScheduleService();
		baseService = manager.createBaseService();
		

		this.op=op;
	}
	
	private int result=KSGCommand.PROCESS;
	public int execute() 
	{
		/*try {
			di = new ScheduleBuildMessageDialog ();
			di.createAndUpdateUI();
			logger.info("start Route");
			makeSchedule();
			di.setVisible(false);
			di.dispose();
			logger.debug("end Route");
			return RESULT_SUCCESS ;
		} catch (SQLException e) {
			di.setVisible(false);
			di.dispose();
			e.printStackTrace();
			return RESULT_FAILE;
		} catch (ParseException e)
		{
			di.setVisible(false);
			di.dispose();
			//JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "sort ����");
			e.printStackTrace();
			return RESULT_FAILE;
		}*/
		SwingWorker worker = new SwingWorker() {

			
			public Object construct() {

				return new RouteTaskDate(op);
			}
		};
		worker.start();
		
		return result;
	}
	



}
