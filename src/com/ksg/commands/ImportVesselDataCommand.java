package com.ksg.commands;

import java.io.File;

import com.ksg.commands.schedule.SwingWorker;
import com.ksg.commands.schedule.task.ImportVesselDataTask;

public class ImportVesselDataCommand implements KSGCommand
{
	
	File fileName;
	public ImportVesselDataCommand(File fileName) {
		this.fileName =fileName;
		System.out.println("선박정보 추가 시작");
		
	}
	private int result=KSGCommand.PROCESS;
	public int execute() 
	{
		SwingWorker worker = new SwingWorker() {

			
			public Object construct() {

				return new ImportVesselDataTask(fileName);
			}
		};
		worker.start();
		return result;
	}

}
