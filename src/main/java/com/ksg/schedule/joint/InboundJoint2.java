package com.ksg.schedule.joint;

@Deprecated
public class InboundJoint2 extends KSGScheduleJoint{

	//TODO ���� ó��
	@Override
	public int execute() throws Exception {
		return 0;
	}

	@Override
	public void init() {
		
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
