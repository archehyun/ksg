package com.ksg.adv.view.comp;

import java.util.List;

import com.ksg.domain.Code;

public class PortColorInfo {
	public static final  int TYPE_NOMAL=1;
	public static final  int TYPE_2=2;
	public static final int TYPE_RED=3;
	public static final int TYPE_BLUE=4;
	private String area_code;
	private int index;
	private String port_name;
	private int type;
	private Object value="";
	List codeList;
	public PortColorInfo(Object value) {
		this.value = value;
		/*try {
			codeList=_baseService.getCodeInfoList(new Code());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	private Code searchCode()
	{
		return null;
	}
	public PortColorInfo() {}
	public String getArea_code() {
		// TODO Auto-generated method stub
		return area_code;
	}
	public int getIndex() {
		return index;
	}
	public String getPort_name() {
		return port_name;
	}
	public int getType() {
		return type;
	}
	public void setArea_code(String area_code) {

		this.area_code=area_code;
	}
	public void setIndex(int index) {
		this.index= index;

	}
	public void setType(int type) {
		this.type = type;
	}
	public String toString()
	{
		return String.valueOf(value);
	}
	public void setPort_name(String port_name) {
		this.port_name = port_name;
	}
}
