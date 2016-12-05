package com.ksg.commands.schedule;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.schedule.task.ConsoleTask;
import com.ksg.domain.ScheduleData;

public class SortConsoleCommnad implements KSGCommand {

	//private int result=KSGCommand.PROCESS;
	
	ScheduleData op;
	public int result=-1; 
	
	
	public SortConsoleCommnad(ScheduleData op) {
		this.op=op;
	}
	public int execute() {
		try{
		SwingWorker worker = new SwingWorker() {
			
			public Object construct() {
				
				return new ConsoleTask(op);
			}
		};
		
		worker.start();
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}	


}
