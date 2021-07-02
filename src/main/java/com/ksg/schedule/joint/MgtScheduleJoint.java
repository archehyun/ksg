package com.ksg.schedule.joint;

public class MgtScheduleJoint {
	
	public static void main(String[] args) {
		KSGScheduleJoint joint = new InboundJoint();
		joint.init();
		try {
			joint.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
