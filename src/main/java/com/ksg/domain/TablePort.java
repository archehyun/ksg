package com.ksg.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 박창현
 *
 */
@SuppressWarnings("serial")
@AllArgsConstructor
@Builder
@Getter @Setter
public class TablePort extends BaseInfo implements Serializable {
	
	public static final String TYPE_CHAILD="C";
	
	public static final String TYPE_PARENT="P";
	
	private String parent_port;
	
	private int port_index;
	
	private int new_port_index;
	
	private String port_name;
	
	private String new_port_name;
	
	private String port_type;
	
	private List<TablePort> subPortList;
	
	private String table_id;
	
	private String port_area;
	
	private String area_code;
	
	public TablePort() {
		subPortList = new ArrayList<TablePort>();
	}
	public void addSubPort(TablePort sup) {
		subPortList.add(sup);
	}
	
	/**
	 * 하위 항구 존재시
	 * @return
	 */
	public String[] getSubPortNameArray() {
		
		if(subPortList.isEmpty()) return new String[]{this.getPort_name()};
		
		List subPortNameList=subPortList.stream()
										.map(TablePort::getPort_name)
										.collect(Collectors.toList());
		
		return (String[]) subPortNameList.toArray(new String[subPortNameList.size()]);
	}
	
	public TablePort[] getSubPortArray() {
		
		if(subPortList.isEmpty()) return new TablePort[]{this};
		
		return (TablePort[]) subPortList.toArray(new TablePort[subPortList.size()]);
	}
	
	public String toString()
	{
		return "["+port_name+", "+port_index+", "+port_area+", "+area_code+", "+port_type+"]";
		
	}
	
	@Override
	public String toInfoString() {
		return null;
	}
	
	@Override
    public boolean equals(Object o) {
        if (o instanceof TablePort) {
            return port_name.equals(((TablePort) o).port_name);
        }
        return false;
    }

}
