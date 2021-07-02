package com.ksg.commands.schedule;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.schedule.task.InlnandTask;
import com.ksg.domain.ScheduleData;

public class SortInlandCommnad implements KSGCommand {

private int result=KSGCommand.PROCESS;
	
	ScheduleData op;
	
	public SortInlandCommnad(ScheduleData op) {
		this.op=op;
	}
	public int execute() {
		SwingWorker worker = new SwingWorker() {
			
			public Object construct() {
				
				return new InlnandTask(op);
			}
		};
		worker.start();

		return result;
	}	


}
