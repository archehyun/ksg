package com.ksg.schedule.joint;

@Deprecated
public class InboundJoint2 extends KSGScheduleJoint{

	@Override
	public int execute() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	class ScheduleBilder
	{
		String fromPort;
		
		public Schedule build()
		{
			return null;
		}
	}
	class Schedule
	{
		String vesselName;
		
		public Schedule(String vesselName) {
			this.vesselName = vesselName;
		}
	}

}
