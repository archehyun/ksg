package com.ksg.workbench.adv.comp;

import java.util.List;

public class PortColorInfo {
	
		public static final  int TYPE_NOMAL=1;
		public static final  int TYPE_NEW_PORT=2;
		public static final int TYPE_RED=3;
		public static final int TYPE_BLUE=4;
		private int index;
		private int type;
		private Object value="";
		private List codeList;
		private String area_code;
		private String port_name;
		public PortColorInfo(Object value) {
			this.value = value;
		}

		public PortColorInfo() {}

		public int getIndex() {
			return index;
		}

		public int getType() {
			return type;
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

		public String getArea_code() {
			// TODO Auto-generated method stub
			return area_code;
		}

		public String getPort_name() {
			// TODO Auto-generated method stub
			return port_name;
		}
}
