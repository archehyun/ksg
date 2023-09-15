package com.dtp.api.schedule.joint.print;

import java.util.ArrayList;

public interface PrintAble {
	
	public abstract  String getHeader();
	
	public abstract  String getFooter();
	
	public abstract  String getBody(ArrayList<String> printList);

}
