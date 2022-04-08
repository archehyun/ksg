package com.ksg.common.exception;

import com.ksg.domain.ShippersTable;

/**
 * 
 * 선박명 NULL 예외
 * @author 박창현
 *
 */
public class VesselNullException extends NameNullException{
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

	public VesselNullException(String vesselName2) {
		super(vesselName2);
	}
	public VesselNullException(ShippersTable table, String vesselName) {
		super(vesselName);
		this.setTable(table);
	}

}
