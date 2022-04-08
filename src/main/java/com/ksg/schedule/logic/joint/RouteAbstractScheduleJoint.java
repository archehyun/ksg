package com.ksg.schedule.logic.joint;

import java.sql.SQLException;

import com.ksg.domain.ShippersTable;

/**

  * @FileName : RouteAbstractScheduleJoint.java

  * @Date : 2021. 5. 3. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 항로별 스케줄 생성 추상 클래스

  */
public abstract class RouteAbstractScheduleJoint extends DefaultScheduleJoint{
	
	
	protected String WORLD_B="";

	protected String WORLD_E="";

	protected String WORLD_F="";

	protected String WORLD_INPORT="";

	protected String WORLD_OUTPORT="";

	protected String WORLD_VERSION1="";

	protected String WORLD_VERSION2="";

	protected String WORLD_VERSION3="";
	
	protected String INCODE_KEY="";
	
	protected ShippersTable op;
	
	public static final int ORDER_BY_DATE=1;
	
	public static final int ORDER_BY_VESSEL=2;
	
	public static final String OUTBOUND = "O";
	
	public static final String RUSSIA = "Russia";
	
	public static final String JAPAN = "Japan";
	
	public static final String CHINA = "China";

	public RouteAbstractScheduleJoint() throws SQLException {
		super();
	}

}
