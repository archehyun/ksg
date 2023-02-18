package com.ksg.commands.schedule;

import javax.swing.JOptionPane;

import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;

import com.ksg.commands.IFCommand;
import com.ksg.commands.schedule.task.SortAllTask;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.ShippersTable;

public class SortAllCommand implements IFCommand {
	
	private int result=IFCommand.PROCESS;
	
	ShippersTable op;
	
	int orderby;
	
	boolean isNew=false;
	
	boolean isPrintInbound;
	
	boolean isPrintOutbound;
	
	boolean isPrintRoute;
	
	CommandMap param;
	
	public SortAllCommand(CommandMap param) {
		this.param = param;
	}
	
	public SortAllCommand(ShippersTable op) {
		this.op=op;
	}
	public SortAllCommand(ShippersTable op,int orderby) {
		this(op);
		this.orderby =orderby;
	}
	public SortAllCommand(ShippersTable op,int orderby,boolean isNew,boolean isPrintInbound, boolean isPrintOutbound, boolean isPrintRoute) {
		this(op, orderby);
		
		this.isNew = isNew;
		this.isPrintInbound= isPrintInbound;
		this.isPrintOutbound= isPrintOutbound;
		this.isPrintRoute= isPrintRoute;
	}
	public int execute() {
		SwingWorker worker = new SwingWorker() {
			
			public Object construct() {
				
				try {
					return new SortAllTask(param).startBuild();
					
					
				} 
				catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					JOptionPane.showMessageDialog(null, "null error");
					
					return null;
					
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					JOptionPane.showMessageDialog(null, e.getMessage());
					
					return null;
					
				}
			}
		};
		worker.start();

		return result;
	}	

}
