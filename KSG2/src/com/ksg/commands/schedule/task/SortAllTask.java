package com.ksg.commands.schedule.task;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.ksg.commands.LongTask;
import com.ksg.commands.schedule.outbound.OutboundTask;
import com.ksg.commands.schedule.route.RouteTaskDate;
import com.ksg.commands.schedule.route.RouteTaskNewVessel;
import com.ksg.domain.ShippersTable;
import com.ksg.model.KSGModelManager;
import com.ksg.schedule.build.ScheduleBuild;
import com.ksg.schedule.build.ScheduleManager;
import com.ksg.schedule.inbound.InboundScheduleBuild;
import com.ksg.schedule.outbound.OutboundScheduleBuild;
import com.ksg.schedule.outbound.OutboundScheduleBuildV2;
import com.ksg.schedule.route.RouteScheduleBuild;

public class SortAllTask implements LongTask {

	public SortAllTask(ShippersTable op) {
		new InboundTask();
		new OutboundTask();
		new RouteTaskDate(op);
		JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "Sorting을 완료 했습니다.");
	}


	public SortAllTask(ShippersTable op,int orderBy,boolean isNew,boolean isPrintInbound, boolean isPrintOutbound, boolean isPrintRoute) {
		try {
			if(isPrintInbound)
			{
				//	new InboundTask();
				/*ScheduleBuild build=ScheduleManager.getInstance().getInboundSchedule();
			build.execute();*/


				ScheduleManager.getInstance().addBulid(new InboundScheduleBuild());

			}
			if(isPrintOutbound)
			{
				//new OutboundTask();
				ScheduleManager.getInstance().addBulid(new OutboundScheduleBuildV2());
			}
			if(isPrintRoute)
			{
				if(isNew)
				{
					new RouteTaskNewVessel(op,orderBy).start();
				}
				else
				{
					ScheduleManager.getInstance().addBulid(new RouteScheduleBuild(op, orderBy));
					/*ScheduleBuild build=ScheduleManager.getInstance().getRouteSchedule(op,orderBy);
				build.execute();*/
				}
			}
			ScheduleManager.getInstance().startBuild();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "Sorting을 완료 했습니다.");


	}



	public int getCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLengthOfTask() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	public void stop() {
		// TODO Auto-generated method stub

	}

}
