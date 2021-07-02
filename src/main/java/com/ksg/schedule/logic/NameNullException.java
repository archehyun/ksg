package com.ksg.schedule.logic;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class NameNullException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String nullName;
	
	protected int nameType;
	
	public static final int TYPE_PORT=1;
	
	public static final int VESSEL_TYPE_VESSEL=2;
	
	public static final int TYPE_COMPANY=3;
	
	public NameNullException(String nullName)
	{
		super(nullName);
	}
	
	public NameNullException(int nameType, String nullName) {
		this(nullName);
		this.nameType = nameType;
		this.nullName = nullName;
	}

}
