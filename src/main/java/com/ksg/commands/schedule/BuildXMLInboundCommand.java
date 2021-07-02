package com.ksg.commands.schedule;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.schedule.task.InboundTask;

public class BuildXMLInboundCommand implements KSGCommand{	
	
	private int result=KSGCommand.PROCESS;
	public int execute() {
		
		SwingWorker worker = new SwingWorker() {

		
			public Object construct() {

				return new InboundTask();
			}
		};
		worker.start();
		
		return result;
	}
	



}
