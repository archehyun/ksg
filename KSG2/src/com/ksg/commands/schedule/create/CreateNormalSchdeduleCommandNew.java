/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.commands.schedule.create;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.JDOMException;

import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.commands.schedule.SwingWorker;
import com.ksg.dao.impl.ScheduleService;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.model.KSGModelManager;
import com.ksg.quark.XTGManager;
import com.ksg.schedule.build.PortIndexNotMatchException;
import com.ksg.schedule.build.ScheduleManager;
import com.ksg.schedule.build.VesselNullException;
import com.ksg.view.ui.ErrorLogManager;
public class CreateNormalSchdeduleCommandNew extends CreateScheduelCommand
{
	
	
	public static final int TYPE_INBOUND=1;
	public static final int TYPE_OUTBOUND=2;

	/*
	 * ���� �����ٿ��� �λ��װ� �λ� ������ ���� ��Ÿ�� ��� ���� �λ��� �������� ����
	 *  
	 */
	private static final String BUSAN = "BUSAN";

	private static final String BUSAN_NEW_PORT = "BUSAN NEW";

	private static final String SCHEDULE_BUILD_ERROR_TXT = "schedule_build_error.txt";

	private ErrorLogManager errorLogManager = ErrorLogManager.getInstance();

	private int[]	a_inport,a_intoport,a_outport,a_outtoport;

	private String[][] arrayDatas;

	private String outPortData,outToPortData;

	private Vector portDataArray;

	private XTGManager xtgmanager = new XTGManager();

	private String[][] vslDatas;

	public CreateNormalSchdeduleCommandNew(ShippersTable op) throws SQLException 
	{
		super();
		searchOption =op;
	}
	
	public int execute() {

		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				current = 0;
				done = false;
				canceled = false;
				statMessage = null;
				return new ActualTask();
			}
		};
		worker.start();
		return result;
	}


	private int makeBildingSchedule() {
		logger.debug("<==start build schedule ==>");

		try {
			/************ ��ü ���̺� ���� ��ȸ  **********
			 * 1. ������ NULL �Ǵ� NNN�� �ƴ� ��츦 ��ȸ
			 * ********************************************			 
			 */
			logger.info("search option:"+searchOption);
			this.setTime(startTime);
			
			List table_list = this.getTableListByOption();
			
			setMessageDialog();
			
			Iterator iter = table_list.iterator();
			
			while(iter.hasNext())
			{
				ShippersTable tableData = (ShippersTable) iter.next();

				// ���̺� �����Ͱ� ��ȿ ���� ����
				if(isTableDataValidation(tableData))
				{
					logger.error("���̺� ���� ����:"+tableData.getTable_id());
					errorlist.add(createError("���̺� ���� ����", tableData.getTable_id(), ""));
					continue;
				}

				processMessageDialog.setMessage("");

				notifyMessage("Make:"+tableData.getCompany_abbr()+","+tableData.getTable_id());

				// ��¥ ���� �迭
				arrayDatas = advData.getDataArray();

				// ���� ���� �迭
				vslDatas = advData.getFullVesselArray(isTS(tableData));				

				//�ױ� ���� �迭
				/**
				 * �� �ױ� �ε����� �̿��ؼ� 
				 * �����ٿ��� �ҷ��� �迭�� ������
				 */

				// �ƿ��ٿ�� ������
				a_outport=makePortArraySub(tableData.getOut_port(),tableData);
				// �ιٿ�� ������
				a_inport =makePortArraySub(tableData.getIn_port(),tableData);
				// �ƿ��ٿ�� �ܱ���
				a_outtoport=makePortArraySub(tableData.getOut_to_port(),tableData);
				// �ιٿ�� �ܱ���
				a_intoport =makePortArraySub(tableData.getIn_to_port(),tableData);				

				for(int vslIndex=0;vslIndex<vslDatas.length;vslIndex++)
				{
					try 
					{
						// �ιٿ�� ������ ����
						makeSchedule(tableData, vslIndex,a_inport,a_intoport,ScheduleService.INBOUND,advData);

						// �ƿ��ٿ�� ������ ����
						makeSchedule(tableData, vslIndex,a_outport,a_outtoport,ScheduleService.OUTBOUND,advData);
					}

					catch (PortIndexNotMatchException e) 
					{		
						
						String errorLog =e.table.getCompany_abbr()+"������ "+e.table.getPage()+" ������,"+e.table.getTable_index()+
						"�� ���̺��� ������ ���� ������ ������ ������ϴ�.\n\n�ױ�����, �ױ� �ε��� ����,��¥ ���ĸ� Ȯ�� �Ͻʽÿ�.\n\n������ ������ ���� �մϴ�.";
						errorLogManager.setLogger(errorLog);
						
						logger.error(errorLog);
						JOptionPane.showMessageDialog(null, errorLog);

						e.printStackTrace();
						errorlist.add(this.createError("�ε��� ����", tableData.getTable_id(), ""));
						close();						
						return RESULT_FAILE;
					}
					catch(NotSupportedDateTypeException e)
					{
						close();
						return RESULT_FAILE;
					}catch(VesselNullException e)
					{
						close();
						ShippersTable errorTable = e.getTable();
						logger.error(errorTable);
						JOptionPane.showMessageDialog(null, "table id:"+errorTable.getTable_id()+",vessel null:"+e.getVesselName());
						return RESULT_FAILE;
					}
				}
				process++;		
				current++;
			}

			done = true;
			
			current = lengthOfTask;	

			logger.info("<==build schedule end==>");
			
			this.setTime(endtime);
			
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "������ �����Ϸ� ("+(endtime-startTime)+"ms)");
			
			return RESULT_SUCCESS;

		} catch (SQLException e) {

			e.printStackTrace();
			done=false;
			return RESULT_FAILE;
		}catch (Exception e) {
			e.printStackTrace();

			done=false;
			JOptionPane.showMessageDialog(null, "error:"+e.getMessage());
			return RESULT_FAILE;
		}
		finally{
			printError();
			processMessageDialog.setVisible(false);
			processMessageDialog.dispose();
		}

		// ��ü
	}


	private void close() {
		done=false;
		processMessageDialog.setVisible(false);
		processMessageDialog.dispose();
	}






	private void printError() {
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<errorlist.size();i++)
		{
			buffer.append(errorlist.get(i).toString()+"\n");
		}
		xtgmanager.createXTGFile(buffer.toString(),SCHEDULE_BUILD_ERROR_TXT);
	}


	private TablePort getPort(Vector arry,int index)
	{
		TablePort port1 = new TablePort();

		if(arry.size()==1)
		{
			port1 = (TablePort) arry.get(0);
		}
		else if(arry.size()>1)
		{
			for(int i=0;i<arry.size();i++)
			{
				TablePort port=(TablePort) arry.get(i);
				if(port.getPort_index()==index)
				{
					port1.setPort_name(port.getPort_name());
					port1.addSubPort(port);
				}
			}
		}

		return port1;

	}
	/**
	 * @param table
	 * @return
	 */
	private Vector getPortList(ShippersTable table) 
	{
		portDataArray  = new Vector();
		try {

			TablePort tablePort = new TablePort();
			tablePort.setTable_id(table.getTable_id());
			tablePort.setPort_type(TablePort.TYPE_PARENT);

			List li=getTablePortList(tablePort);

			for(int i=0;i<li.size();i++)
			{
				TablePort port = (TablePort) li.get(i);

				portDataArray.add(port);
			}
			logger.debug("portarray:"+table.getTable_id()+","+portDataArray);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return portDataArray;
	}


	private void insertSchedule(ShippersTable table,String table_id,
			String voyageNum,String vesselName,
			String dateF, String dateT,
			String area_code, String area_name, 
			String gubun,String company_abbr,String agent,
			String fromPort, String toPort,String inOutType,
			String common_shipping,String date_isusse, int vslIndex)
	{

		// ��¥ ������ ���� ������ ó�� ���� ����
		if(!isScheduleDataValidation())
			return;

		ScheduleData scheduledata = new ScheduleData();
		scheduledata.setTable_id(table_id); // ���̺� ���̵�
		scheduledata.setVessel(vesselName); // ���ڸ�
		scheduledata.setAgent(agent);		// ������Ʈ
		scheduledata.setPort(toPort);		// ������
		scheduledata.setFromPort(fromPort);		// ������
		scheduledata.setDateF(dateF);		// �����
		scheduledata.setDateT(dateT);		// ������
		scheduledata.setArea_code(area_code);// ���� �ڵ�
		scheduledata.setArea_name(area_name);// ���� ��
		scheduledata.setCompany_abbr(company_abbr);// �����
		scheduledata.setVoyage_num(voyageNum);//	������ȣ
		scheduledata.setN_voyage_num(getNumericVoyage(voyageNum));// ���� ��ȣ
		scheduledata.setInOutType(inOutType);	//in, out Ÿ��
		scheduledata.setGubun(gubun);		//����
		if(common_shipping==null)
			common_shipping="";
		scheduledata.setCommon_shipping(common_shipping);//
		scheduledata.setDate_issue(date_isusse);// �Է���

		if(searchOption.getGubun().equals(ShippersTable.GUBUN_CONSOLE))
		{
			scheduledata.setC_time(table.getC_time()==0?"":arrayDatas[vslIndex][table.getC_time()-1]);
			scheduledata.setD_time(table.getD_time()==0?"":arrayDatas[vslIndex][table.getD_time()-1]);
			scheduledata.setConsole_cfs(table.getConsole_cfs());
			scheduledata.setConsole_page(table.getConsole_page());
		}

		try
		{
			scheduleService.insertScheduleData(scheduledata);// DB �� ����
		}
		catch(SQLException e)
		{

			switch (e.getErrorCode()) {
			case 1062:// ��Ű ����
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				//					update++;
				break;
			case 2627:// ��Ű ����
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//					update++;
				break;
			default:
				logger.error("error:"+scheduledata+", id:"+scheduledata.getTable_id()+", fromPort:"+scheduledata.getFromPort());
			e.printStackTrace();
			break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			errorlist.add(createError(e.getMessage(), scheduledata.getTable_id(), ""));

		}


	}
	/**
	 * @deprecated
	 * @param table
	 * @param vslIndex
	 * @param InOutBoundType
	 * @param outPortIndex
	 * @param outToPortIndex
	 * @param outPort
	 * @param outToPort
	 * @param adv
	 * @throws SQLException
	 */
	private void insertSchedule(ShippersTable table, int vslIndex,
			String InOutBoundType, int outPortIndex, int outToPortIndex, String outPort, String outToPort,ADVData adv) throws SQLException,ArrayIndexOutOfBoundsException {

		outToPortData = arrayDatas[vslIndex][outToPortIndex-1];

		outPortData = arrayDatas[vslIndex][outPortIndex-1];

		ScheduleData scheduledata = new ScheduleData();

		scheduledata.setTable_id(table.getTable_id());

		if(isScheduleDataValidation())
		{
			// ���ڸ��� '-' �̸� ������ ó�� ����

			Vessel op = new Vessel();
			op.setVessel_name(vslDatas[vslIndex][0]);

			for(int i=0;i<NO_VESSEL.size();i++)
			{
				Vessel opItem = (Vessel) NO_VESSEL.get(i);
				if(op.getVessel_name()==null)
				{
					errorlist.add(createError("Vessel Null", scheduledata.getTable_id(), scheduledata.getVessel()));
					return;	
				}

				if(op.getVessel_name().equals(opItem.getVessel_name()))
				{
					logger.error("no vessel:"+op.getVessel_name());
					return ; 
				}
			}

			scheduledata.setVessel(vslDatas[vslIndex][0]);
			if(scheduledata.getVessel()==null)
			{
				errorlist.add(createError("Vessel Null", scheduledata.getTable_id(), scheduledata.getVessel()));
				return;
			}

			if(table.getGubun().equals("TS"))
			{
				String vsl[][] = adv.getFullVesselArray(true);
				scheduledata.setTs_vessel(vsl[vslIndex][0]);
				TablePort tablePort = new TablePort();
				// TS Ȯ��
				tablePort.setTable_id(table.getTable_id());
				TablePort info = getTablePort(tablePort);
				scheduledata.setTs(info.getPort_name());


				String date[][]=null;
				try {
					date = adv.getDataArray();
				} catch (OutOfMemoryError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//scheduledata.setTs_date(date[vslIndex][table.getDirection()-1]);


			}
			scheduledata.setAgent(table.getAgent());

			// �ιٿ��
			if(InOutBoundType.equals(ScheduleService.INBOUND))
			{
				//inbound ������ ó���� �ܱ��������� 7/29-7/30�϶� ������¥�� 7/30�� ó���Ǿ�� ��.
				try 
				{
					scheduledata.setPort(outPort);
					scheduledata.setFromPort(outToPort);
					String dates[]=adjestDateYear(arrayDatas[vslIndex][outToPortIndex-1],arrayDatas[vslIndex][outPortIndex-1],InOutBoundType);

					scheduledata.setDateF(dates[0]);
					scheduledata.setDateT(dates[1]);
					checkDate(table, scheduledata);

				} catch (NotSupportedDateTypeException e) 
				{
					logger.error("no match date:"+e.date+", id:"+scheduledata.getTable_id());
					errorlist.add(createError("No match date", scheduledata.getTable_id()+":"+scheduledata.getVessel(), e.date));
					return;
				}
			}
			else
			{
				try 
				{
					scheduledata.setPort(outToPort);
					scheduledata.setFromPort(outPort);

					String dates[]=adjestDateYear(arrayDatas[vslIndex][outPortIndex-1],arrayDatas[vslIndex][outToPortIndex-1],InOutBoundType);

					scheduledata.setDateF(dates[0]);
					scheduledata.setDateT(dates[1]);
					checkDate(table, scheduledata);
				} catch (NotSupportedDateTypeException e)
				{
					logger.error("no match date:"+e.date+", id:"+scheduledata.getTable_id());
					errorlist.add(createError("��¥ ����", scheduledata.getTable_id(), e.date));
					return;
				}
			}


			if(scheduledata==null||scheduledata.getPort()==null)
			{
				logger.error("schedule null:"+scheduledata);
				errorlist.add(createError("schedule null", scheduledata.getTable_id(), ""));
				return;
			}

			PortInfo  portInfo=getPortInfoByPortName(scheduledata.getPort() );
			if(portInfo!=null)
			{
				scheduledata.setArea_code(portInfo.getArea_code());
				scheduledata.setArea_name(portInfo.getPort_area());
			}else
			{
				PortInfo  portabbrInfo=getPortInfoAbbrByPortName(scheduledata.getPort() );
				if(portabbrInfo!=null)
				{
					scheduledata.setArea_code(portabbrInfo.getArea_code());
					scheduledata.setArea_name(portabbrInfo.getPort_area());
				}else
				{
					scheduledata.setArea_code("0");
				}
			}

		} 
		scheduledata.setCompany_abbr(table.getCompany_abbr());
		scheduledata.setVoyage_num(vslDatas[vslIndex][1]);
		scheduledata.setN_voyage_num(getNumericVoyage(vslDatas[vslIndex][1]));
		scheduledata.setInOutType(InOutBoundType);
		scheduledata.setGubun(table.getGubun());
		scheduledata.setCommon_shipping("");
		scheduledata.setDate_issue(table.getDate_isusse());
		scheduledata.setTable_id(table.getTable_id());

		if(searchOption.getGubun().equals(ShippersTable.GUBUN_CONSOLE))
		{
			scheduledata.setC_time(table.getC_time()==0?"":arrayDatas[vslIndex][table.getC_time()-1]);
			scheduledata.setD_time(table.getD_time()==0?"":arrayDatas[vslIndex][table.getD_time()-1]);
			scheduledata.setConsole_cfs(table.getConsole_cfs());
			scheduledata.setConsole_page(table.getConsole_page());
		}

		try
		{

			if(scheduledata.getFromPort()!=null)
			{
				scheduleService.insertScheduleData(scheduledata);// DB �� ����
			}

		}
		catch(SQLException e)
		{

			switch (e.getErrorCode()) {
			case 1062:// ��Ű ����
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				//					update++;
				break;
			case 2627:// ��Ű ����
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//					update++;
				break;
			default:
				logger.error("error:"+scheduledata+", id:"+scheduledata.getTable_id()+", fromPort:"+scheduledata.getFromPort());
			e.printStackTrace();
			break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			errorlist.add(createError(e.getMessage(), scheduledata.getTable_id(), ""));
		}
	}


	/**������ ��¥ Ÿ�� Ȯ��
	 * @return
	 */
	private boolean isScheduleDataValidation() {
		return !outToPortData.equals("-")&&!outPortData.equals("-")&&
		!outToPortData.equals("_")&&!outPortData.equals("_")&&
		!outToPortData.trim().equals("")&&!outPortData.trim().equals("");
	}

	/**@���� �ױ��̸��� �������� �޸� �� �ִ� �ױ� �� ���� �˻�
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	private PortInfo getPortInfoByPortName(String portName) throws SQLException
	{
		Iterator<PortInfo> iter = portList.iterator();
		while(iter.hasNext())
		{
			PortInfo info = iter.next();
			if(info.getPort_name().equals(portName))
				return info;
		}
		return null;
	}
	/**@���� �ױ��̸��� �������� �޸� �� �ִ� �ױ� ��� ���� �˻� 
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	private PortInfo getPortInfoAbbrByPortName(String portName) throws SQLException
	{
		Iterator<PortInfo> iter = portAbbrList.iterator();
		while(iter.hasNext())
		{
			PortInfo info = iter.next();
			if(info.getPort_abbr().equals(portName))
				return info;
		}
		return null;
	}


	/**
	 * @param field
	 * @param table
	 * @return
	 */
	private int[] makePortArraySub(String field,ShippersTable table) {
		if(field==null||field.equals("")||field.equals(" "))
			return null;

		logger.debug("table id:"+table.getTable_id()+" field:"+field);
		field=field.trim();

		// #�� �������� �ױ� �ε����� ����
		StringTokenizer st = new StringTokenizer(field,"#");

		Vector<Integer> indexList = new Vector<Integer>();
		while(st.hasMoreTokens())
		{
			try
			{
				indexList.add(Integer.parseInt(st.nextToken().trim()));
			}
			catch (NumberFormatException e) 
			{
				logger.error("number foramt error:"+field+",id:"+table.getTable_id());
				errorlist.add(createError("�ε��� ����", table.getTable_id(), field));

				continue;
			}
		}

		int array[] =new int[indexList.size()];
		for(int i=0;i<indexList.size();i++)
		{
			array[i]=indexList.get(i);
		}		

		return array;

	}

	int oldPortIndex;
	int newPortIndex;
	boolean isNewPort=false;

	private boolean isExitNewPort(String[] array)
	{
		boolean isOldPort=false;
		boolean isNewPort=false;
		for(int i=0;i<array.length;i++)
		{
			if(array[i].equals(BUSAN_NEW_PORT))
			{
				isOldPort= true;
			}
			if(array[i].equals(BUSAN))
			{
				isNewPort= true;
			}
		}

		return isOldPort&&isNewPort;
	}


	private PortInfo getPortInfo(String toPort) throws SQLException
	{
		PortInfo  portInfo=getPortInfoByPortName(toPort);
		if(portInfo== null)
		{
			return getPortInfoAbbrByPortName(toPort);
		}
		else
		{
			return portInfo;
		}		

	}
	boolean isOutToNewBusanPortScheduleAdd=false;
	boolean isOutNewBusanPortScheduleAdd=false;
	int outBusanNewPortIndex;
	int outToBusanNewPortIndex;
	boolean isExitOutOldPort=false;
	boolean isExitOutNewPort=false;
	boolean isExitOutToOldPort=false;
	boolean isExitOutToNewPort=false;
	SimpleDateFormat format = new SimpleDateFormat("M/d");
	/**
	 * @param table
	 * @param vslIndex
	 * @param ports
	 * @param outPort
	 * @param InOutBoundType
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private void makeSchedule(ShippersTable table, int vslIndex, int[]ports, int[]outPort,String InOutBoundType,ADVData adv)
			throws ArrayIndexOutOfBoundsException,NullPointerException, ParseException,VesselNullException{
		if(ports==null||outPort==null)
		{			
			return;
		}

		try{
			String table_id= table.getTable_id();
			String company_abbr=table.getCompany_abbr();
			String agent = table.getAgent();
			String gubun= table.getGubun();
			String date_isusse = table.getDate_isusse();
			String common_shipping = table.getCommon_shipping();
			String dateF=null;
			String dateT=null;
			String area_code=null;
			String area_name=null;
			String vesselName=vslDatas[vslIndex][0];
			
			String voyageNum=vslDatas[vslIndex][1];
			String fromPort=null;
			String toPort=null;
			String dates[]= null;

			// ������
			portDataArray=getPortList(table);
			
			logger.info("table info:"+table.getTable_id());
			
			Vessel vesselInfo = ScheduleManager.getInstance().searchVessel(vesselName);		
			

			logger.debug("MakeSchedule "+InOutBoundType+ " start:"+table.getTable_id()+","+table.getPage());
			
			logger.info("vessel name:"+vesselName+","+ports.length+","+outPort.length);


			boolean isOutBusanAndNewBusan=isBusanAndNewBusan(ports, outPort,TYPE_INBOUND);
			
			boolean isOutToBusanAndNewBusan=isBusanAndNewBusan(ports, outPort,TYPE_OUTBOUND);

			for(int fromPortCount=0;fromPortCount<ports.length;fromPortCount++)
			{
				int outPortIndex =ports[fromPortCount];

				// �ܱ���
				for(int toPortCount=0;toPortCount<outPort.length;toPortCount++)
				{
					int outToPortIndex = outPort[toPortCount];

					TablePort _outport = this.getPort(portDataArray, outPortIndex);
					TablePort _outtoport = this.getPort(portDataArray, outToPortIndex);

					String subFromPortArray[]=_outport.getPortArray();					
					String subToPortArray[]=_outtoport.getPortArray();

					outToPortData = arrayDatas[vslIndex][outToPortIndex-1];

					outPortData = arrayDatas[vslIndex][outPortIndex-1];
					
					try{
						//�λ� ���� ������ Ȯ�� �ƿ� �ٿ��
						format.parse(arrayDatas[vslIndex][outToBusanNewPortIndex-1]);
						
						isOutToNewBusanPortScheduleAdd=true;
					}
					catch(Exception e)
					{
						isOutToNewBusanPortScheduleAdd=false;
					}
					
					try{
						//�λ� ���� ������ Ȯ�� �� �ٿ��
						format.parse(arrayDatas[vslIndex][outBusanNewPortIndex-1]);
						isOutNewBusanPortScheduleAdd=true;
					}
					catch(Exception e)
					{
						isOutNewBusanPortScheduleAdd=false;
					}

					/*
					 * �λ��׸� ���� ���
					 * �λ���׸� ���� ���
					 * �λ��װ� ������ ���� ���� ���
					 * 
					 */

					try 
					{
						// �����, ������ ��¥ ����
						if(InOutBoundType.equals(ScheduleService.INBOUND))
						{
							dates=adjestDateYear(arrayDatas[vslIndex][outToPortIndex-1],arrayDatas[vslIndex][outPortIndex-1],InOutBoundType);
						}
						else
						{
							dates=adjestDateYear(arrayDatas[vslIndex][outPortIndex-1],arrayDatas[vslIndex][outToPortIndex-1],InOutBoundType);
						}
						dateF=dates[0];
						dateT=dates[1];

					} catch (NotSupportedDateTypeException e) 
					{
						if(!e.date.equals("-"))
						{
							logger.error("no match date:"+e.date+", id:"+table_id);
							errorlist.add(createError("��¥ ����", table_id, e.date));	
						}
						continue;
					}

					for(int z =0;z<subFromPortArray.length;z++)
					{
						for(int c =0;c<subToPortArray.length;c++)
						{
							try{

								// �����, ������ ����
								if(InOutBoundType.equals(ScheduleService.INBOUND))
								{										
									fromPort=subToPortArray[c];
									toPort=subFromPortArray[z];
								}
								else
								{
									fromPort=subFromPortArray[z];
									toPort=subToPortArray[c];									
								}
								PortInfo  fromPortInfo=getPortInfo(fromPort);
								if(fromPortInfo== null)
								{
									logger.error("null port error:"+fromPort+",id:"+table_id);
									continue;
								}

								PortInfo  toPortInfo=getPortInfo(toPort);

								if(toPortInfo== null)
								{
									logger.error("null port error:"+toPort+",id:"+table_id);
									continue;
								}

								area_code=toPortInfo.getArea_code();
								area_name=toPortInfo.getPort_area();
								
								
								/* �λ��װ� ������ ���� ���� ��� �λ��� �������� �߰� ���� ����
								 * 
								 */

								// outbound ������:
								if(InOutBoundType.equals(ScheduleService.OUTBOUND)&&isOutToBusanAndNewBusan)
								{	
									/* �����ϱ� ���� ����
									 * 1. �λ���, �λ������ ���ÿ� �����ϴ� ���
									 * 2. �λ� ���� �������� �ִ� ���
									 * ����: �λ���, �λ� ������ ���ÿ� �����ϴ� ��� �λ� ���� �������� ���� ��쿡�� �λ� ������ ���õ� 
									 */
									
									if(fromPort.equals(BUSAN)&&isOutToNewBusanPortScheduleAdd)
									{
										logger.info(InOutBoundType+" busan port skip:"+vesselName);
										logger.info("new port index:"+outBusanNewPortIndex);
										continue;
									}
								}
								// inbound ������
								if(InOutBoundType.equals(ScheduleService.INBOUND)&&isOutBusanAndNewBusan)
								{									
									if(toPort.equals(BUSAN)&&isOutNewBusanPortScheduleAdd)
									{
										logger.info(InOutBoundType+" busan port skip:"+vesselName);
										logger.info("new port index:"+outToBusanNewPortIndex);
										continue;
									}
								}


								insertSchedule(table,table_id, 
										voyageNum, vesselName,
										dateF, dateT, area_code,
										area_name, gubun, company_abbr,agent,fromPort,toPort,
										InOutBoundType,common_shipping,
										date_isusse,vslIndex);
								
							}catch(Exception e)
							{
								e.printStackTrace();
								logger.error("error:"+e.getMessage()+",table id:"+table.getTable_id());
								throw new NotSupportedDateTypeException("", 0);
							}
						}
					}

					/******************
					 *���� �߻� �� �� ����				*
					 **/				

				}

			}
			logger.debug("MakeSchedule end");
		}
		catch(VesselNullException e)
		{
			throw new VesselNullException(table,e.getVesselName());
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			throw new PortIndexNotMatchException(table);
		}
	}


	/**
	 * @��¥ 2015-10-01
	 * @���� �����ٿ� �λ��װ� �λ������ ���ÿ� �����ϴ��� �Ǵ�, �λ��� : BUSAN, �λ���� : BUSAN NEW
	 * @param ports
	 * @param outPort
	 * @return
	 * @throws SQLException
	 * 
	 */
	private boolean isBusanAndNewBusan(int[] ports, int[] outPort, int type)
	throws SQLException {
		
		isExitOutOldPort=false;
		isExitOutNewPort=false;
		isExitOutToOldPort=false;
		isExitOutToNewPort=false;
		// �ױ�Ȯ��
		for(int fromPortCount=0;fromPortCount<ports.length;fromPortCount++)
		{
			int outPortIndex =ports[fromPortCount];
			for(int toPortCount=0;toPortCount<outPort.length;toPortCount++)
			{
				int outToPortIndex = outPort[toPortCount];
				TablePort _outport = this.getPort(portDataArray, outPortIndex);
				TablePort _outtoport = this.getPort(portDataArray, outToPortIndex);


				PortInfo searchOutOldPort = this.getPortInfo(_outport.getPort_name());
				PortInfo searchOutNewPort = this.getPortInfo(_outport.getPort_name());

				PortInfo searchOutToOldPort = this.getPortInfo(_outtoport.getPort_name());
				PortInfo searchOutToNewPort = this.getPortInfo(_outtoport.getPort_name());				


				if(searchOutOldPort==null||searchOutNewPort==null)
					continue;

				if(searchOutOldPort.getPort_name().equals(BUSAN))
				{
					isExitOutOldPort = true;
				}
				if(searchOutNewPort.getPort_name().equals(BUSAN_NEW_PORT))
				{
					isExitOutNewPort = true;
					outBusanNewPortIndex=outPortIndex;
				}

				if(searchOutToOldPort==null||searchOutToNewPort==null)
					continue;

				if(searchOutOldPort.getPort_name().equals(BUSAN))
				{
					isExitOutToOldPort = true;

				}
				if(searchOutNewPort.getPort_name().equals(BUSAN_NEW_PORT))
				{
					isExitOutToNewPort = true;
					outToBusanNewPortIndex=outPortIndex;
				}
			}
		}
		
		
		boolean result=false;
		switch (type) {
		case TYPE_INBOUND:
			
			result= isExitOutOldPort&&isExitOutNewPort;
			break;

		case TYPE_OUTBOUND:
			
			result= isExitOutToOldPort&&isExitOutToNewPort;
			break;
		}
		return result;
		
	}

	class ActualTask {
		ActualTask() {

			result=makeBildingSchedule();
		}
	}










}



