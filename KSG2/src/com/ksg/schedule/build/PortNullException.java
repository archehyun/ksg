package com.ksg.schedule.build;

public class PortNullException extends NullPointerException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fromPort;
	public String getFromPort() {
		return fromPort;
	}
	public void setFromPort(String fromPort) {
		this.fromPort = fromPort;
	}
	public String getToPort() {
		return toPort;
	}
	public void setToPort(String toPort) {
		this.toPort = toPort;
	}
	private String toPort;
	
	private String portName;
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public PortNullException(String portName)
	{
		super();
		this.portName = portName;
	}
	public PortNullException(String fromPort,String toPort)
	{
		super();
		this.fromPort = fromPort;
		this.toPort = toPort;
	}

}
