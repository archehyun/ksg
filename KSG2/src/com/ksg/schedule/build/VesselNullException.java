package com.ksg.schedule.build;

import com.ksg.domain.ShippersTable;

public class VesselNullException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ShippersTable table;
	
	
	public ShippersTable getTable() {
		return table;
	}

	public void setTable(ShippersTable table) {
		this.table = table;
	}

	private String vesselName;

	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}
	public VesselNullException() {
		super();
	}

	public VesselNullException(String vesselName2) {
		super(vesselName2);
	}
	public VesselNullException(ShippersTable table, String vesselName) {
		super(vesselName);
		this.setTable(table);
	}

}
