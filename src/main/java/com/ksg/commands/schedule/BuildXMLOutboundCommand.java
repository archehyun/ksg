package com.ksg.commands.schedule;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.schedule.outbound.OutboundTask;

public class BuildXMLOutboundCommand implements KSGCommand{


	private int result=KSGCommand.PROCESS;

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
