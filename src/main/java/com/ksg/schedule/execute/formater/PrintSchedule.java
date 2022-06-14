package com.ksg.schedule.execute.formater;

import java.io.FileWriter;
import java.io.IOException;

import com.ksg.common.util.KSGPropertis;
import com.ksg.schedule.execute.joint.impl.JointedSchehduleData;

public abstract class PrintSchedule
{
	protected FileWriter fw;

	protected String fileLocation;

	public abstract void printArea(String area) throws IOException;		

	public abstract void printToPort(String port)throws IOException;

	public abstract void printFromPort(String name)throws IOException;

	public abstract void printSchedule(JointedSchehduleData temp) throws IOException;

	public PrintSchedule() throws IOException {
		fileLocation=KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION);

	}

}
