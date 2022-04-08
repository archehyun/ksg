package com.ksg.commands.schedule;

import com.ksg.commands.IFCommand;
import com.ksg.commands.schedule.task.InboundTask;

public class BuildXMLInboundCommand implements IFCommand{	
	
	private int result=IFCommand.PROCESS;
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
