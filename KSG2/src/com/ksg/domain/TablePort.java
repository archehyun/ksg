package com.ksg.domain;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author 박창현
 *
 */
public class TablePort implements Serializable {
	public static final String TYPE_CHAILD="C";
	public static final String TYPE_PARENT="P";
	String parent_port;
	int port_index;
	int new_port_index;
	String port_name;
	String new_port_name;
	String port_type;
	Vector subPort;
	String table_id;
	public TablePort() {
		subPort = new Vector();
	}
	public void addSubPort(TablePort sup) {
		subPort.add(sup);

	}
	public String getParent_port() {
		return parent_port;
	}
	public int getPort_index() {
		return port_index;
	}
	public String getPort_name() {
		return port_name;
	}
	public String getPort_type() {
		return port_type;
	}
	/**
	 * 하위 항구 존재시
	 * @return
	 */
	public String[] getPortArray() {
		if(this.subPort.size()==0)
		{
			String p[] = new String[1];
			p[0]=this.getPort_name();
			return p;
		}else
		{
			String p[] = new String[subPort.size()];
			for(int i=0;i<subPort.size();i++)
			{
				TablePort pp=(TablePort) subPort.get(i);
				if(!pp.getPort_name().equals(null))
				p[i]=pp.getPort_name();	
			}
			
			return p;
		}
	}
	public String getTable_id() {
		return table_id;
	}
	public void setParent_port(String parentPort) {
		parent_port = parentPort;
	}
	public void setPort_index(int portIndex) {
		port_index = portIndex;
	}
	public void setPort_name(String portName) {
		port_name = portName;
	}
	public void setPort_type(String portType) {
		port_type = portType;
	}
	public void setTable_id(String tableId) {
		table_id = tableId;
	}
	public String toString()
	{
		return "["+table_id+","+port_index+","+port_name+"]";
	}
	public int getNew_port_index() {
		return new_port_index;
	}
	public void setNew_port_index(int new_port_index) {
		this.new_port_index = new_port_index;
	}
	public String getNew_port_name() {
		return new_port_name;
	}
	public void setNew_port_name(String new_port_name) {
		this.new_port_name = new_port_name;
	}

}
