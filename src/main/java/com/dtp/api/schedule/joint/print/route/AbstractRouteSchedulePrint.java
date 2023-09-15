package com.dtp.api.schedule.joint.print.route;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.dtp.api.schedule.joint.print.AbstractSchedulePrint;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;

/**

  * @FileName : RouteAbstractScheduleJoint.java

  * @Date : 2021. 5. 3. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� : �׷κ� ������ ���� �߻� Ŭ����

  */
public abstract class AbstractRouteSchedulePrint extends AbstractSchedulePrint{
	
	
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
	
	protected  String saveLoaction;
	
	protected String errorOutPortfileName;
	
	protected String commonInPortfileName;
	
	protected FileWriter fw,errorOutfw,commonInfw;
	
	KSGPropertis ksgPropertiey= KSGPropertis.getIntance();
	
	SimpleDateFormat dateFormat =new SimpleDateFormat("yyyyMMddHHmmss");
	
	protected List<ScheduleData> scheduleList;

	public AbstractRouteSchedulePrint(ShippersTable op) throws Exception {
		this();
		
		this.op = op;
		
	}
	
	public AbstractRouteSchedulePrint() throws Exception {
		super();
		
		this.saveLoaction 			= ksgPropertiey.getProperty(KSGPropertis.SAVE_LOCATION);
		
		this.fileName  				= ksgPropertiey.getProperty("schedule.route.filename");
		
		this.errorOutPortfileName 	= ksgPropertiey.getProperty("schedule.route.erroroutport");
		
		this.commonInPortfileName 	= ksgPropertiey.getProperty("schedule.route.commoninport");

		initFile();
	}

	private void initFile() throws IOException {
		fw = new FileWriter(saveLoaction+"/"+String.format("%s.txt", fileName ));

		errorOutfw = new FileWriter(saveLoaction+"/"+errorOutPortfileName);

		commonInfw = new FileWriter(saveLoaction+"/"+commonInPortfileName);
	}

	public void close() throws IOException
	{
		fw.close();
		errorOutfw.close();
		commonInfw.close();	
	}
	public String getMessage()
	{
		return super.getMessage();
	}

}
