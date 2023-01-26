package com.ksg.commands.schedule.task;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.ksg.commands.LongTask;
import com.ksg.commands.schedule.outbound.OutboundTask;
import com.ksg.commands.schedule.route.RouteTaskDate;
import com.ksg.commands.schedule.route.RouteTaskNewVessel;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleJoint;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.joint.InboundScheduleJoint;
import com.ksg.schedule.logic.joint.OutboundScheduleJoint;
import com.ksg.schedule.logic.joint.OutboundScheduleJointV2;
import com.ksg.schedule.logic.joint.RouteScheduleJoint;

public class SortAllTask implements LongTask {

	public SortAllTask(ShippersTable op) {
		new InboundTask();
		new OutboundTask();
		new RouteTaskDate(op);
		JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "Sorting을 완료 했습니다.");
	}


	public SortAllTask(ShippersTable op,int orderBy,boolean isNew,boolean isPrintInbound, boolean isPrintOutbound, boolean isPrintRoute) throws Exception {
		try {
			if(isPrintInbound)
			{
				
				ScheduleManager.getInstance().addBulid(new InboundScheduleJoint());

			}
			if(isPrintOutbound)
			{
				//new OutboundTask();
				ScheduleManager.getInstance().addBulid(new OutboundScheduleJointV2());
			}
			if(isPrintRoute)
			{
				if(isNew)
				{
					new RouteTaskNewVessel(op,orderBy).start();
				}
				else
				{
					ScheduleManager.getInstance().addBulid(new RouteScheduleJoint(op, orderBy));
				}
			}
			ScheduleManager.getInstance().startBuild();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public int getCurrent() {
		return 0;
	}

	public int getLengthOfTask() {
		return 0;
	}

	public String getMessage() {
		return null;
	}

	public boolean isDone() {
		return false;
	}

	public void stop() {

	}

}
