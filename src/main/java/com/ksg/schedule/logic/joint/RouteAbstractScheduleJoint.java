package com.ksg.schedule.logic.joint;

import java.sql.SQLException;

import com.ksg.common.util.KSGPropertis;
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
	
//	private String fileName="world_print_new_date2.txt";
//	private String commonInPortfileName="world_print_common_inport.txt";;
//	private String errorOutPortfileName="world_print_error_outport.txt";
	protected  String saveLoaction;
	
	protected String fileName;

	protected String errorOutPortfileName;
	
	protected String commonInPortfileName;
	
	KSGPropertis ksgPropertiey= KSGPropertis.getIntance();

	public RouteAbstractScheduleJoint(ShippersTable op) throws SQLException {
		super();
		
		
		this.saveLoaction = ksgPropertiey.getProperty(KSGPropertis.SAVE_LOCATION);
		
		this.fileName  = ksgPropertiey.getProperty("schedule.route.filename");
		
		this.errorOutPortfileName = ksgPropertiey.getProperty("schedule.route.erroroutport");
		
		this.commonInPortfileName = ksgPropertiey.getProperty("schedule.route.commoninport");
		
		this.op = op;
		
		
	}

}
