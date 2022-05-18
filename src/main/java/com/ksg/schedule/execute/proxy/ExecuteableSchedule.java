package com.ksg.schedule.execute.proxy;

import com.ksg.schedule.execute.Executeable;

public class ExecuteableSchedule implements Executeable{
	
	Executeable executeable;
	
	ExecuteableSchedule(Executeable executeable)
	{
		this.executeable = executeable;
	}

	@Override
	public int execute() {
		// TODO Auto-generated method stub
		return executeable.execute();
	}
	
	

}
