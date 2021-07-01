package com.ksg.schedule.logic.joint;

import java.sql.SQLException;

import com.ksg.domain.ShippersTable;

/**

  * @FileName : RouteAbstractScheduleJoint.java

  * @Date : 2021. 5. 3. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� : �׷κ� ������ ���� �߻� Ŭ����

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

	public RouteAbstractScheduleJoint() throws SQLException {
		super();
	}

}
