package com.ksg.commands;

import java.io.File;

import com.ksg.commands.schedule.SwingWorker;
import com.ksg.commands.schedule.task.ImportVesselDataTask;


@Deprecated
public class ImportVesselDataCommand extends AbstractCommand
{
	
	File fileName;
	public ImportVesselDataCommand(File fileName) {
		this.fileName =fileName;
		System.out.println("선박정보 추가 시작");
		
	}
	private int result=IFCommand.PROCESS;
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
