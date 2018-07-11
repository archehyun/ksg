package com.ksg.commands.schedule;

import java.io.IOException;

import com.ksg.commands.KSGCommand;
import com.ksg.commands.schedule.task.SortAllTask;
import com.ksg.domain.ShippersTable;

public class SortAllCommand implements KSGCommand {
	private int result=KSGCommand.PROCESS;
	
	ShippersTable op;
	
	int orderby;
	boolean isNew=false;
	boolean isPrintInbound;
	boolean isPrintOutbound;
	boolean isPrintRoute;
	
	public SortAllCommand(ShippersTable op) {
		this.op=op;
	}
	public SortAllCommand(ShippersTable op,int orderby) {
		this(op);
		this.orderby =orderby;
	}
	public SortAllCommand(ShippersTable op,int orderby,boolean isNew,boolean isPrintInbound, boolean isPrintOutbound, boolean isPrintRoute) {
		this(op);
		this.orderby =orderby;
		this.isNew = isNew;
		this.isPrintInbound= isPrintInbound;
		this.isPrintOutbound= isPrintOutbound;
		this.isPrintRoute= isPrintRoute;
	}
	public int execute() {
		SwingWorker worker = new SwingWorker() {
			
			public Object construct() {
				
				try {
					return new SortAllTask(op,orderby,isNew,isPrintInbound,isPrintOutbound,isPrintRoute);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
		};
		worker.start();

		return result;
	}	

}
