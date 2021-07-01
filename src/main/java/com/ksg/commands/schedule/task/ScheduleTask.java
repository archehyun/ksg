package com.ksg.commands.schedule.task;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.jdom.JDOMException;

import com.ksg.commands.LongTask;

public interface ScheduleTask extends LongTask{
	public void makeSchedule ()throws SQLException, JDOMException, IOException, ParseException;
}
