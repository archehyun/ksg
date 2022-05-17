package com.ksg.commands.schedule;

import com.ksg.commands.IFCommand;
import com.ksg.commands.schedule.outbound.OutboundTask;

public class BuildXMLOutboundCommand implements IFCommand{


	private int result=IFCommand.PROCESS;

	public int execute() {
		SwingWorker worker = new SwingWorker() {

			public Object construct() {
				
				return new OutboundTask();
			}
		};
		worker.start();

		return result;
	}

	

}
