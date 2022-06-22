package com.ksg.schedule.logic.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.exception.PortNullException;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.exception.VesselNullException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.ScheduleJoint;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.joint.DefaultScheduleJoint;
import com.ksg.service.ScheduleService;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

/**
 * @author ��â��
 *
 */
public class DefaultWebScheduleV2 extends DefaultScheduleJoint {

	private int format_type;

	private IFWebScheduleFormat webScheduleFormat;

	private ScheduleManager scheduleManager = ScheduleManager.getInstance();

	public static final int FORMAT_NOMAL=1;

	public static final  int FORMAT_CONSOLE=2;

	public static final  int FORMAT_INLNAD=3;

	private SimpleDateFormat dateTypeYear = new SimpleDateFormat("yyyy");

	private SimpleDateFormat dateTypeMonth = new SimpleDateFormat("MM");

	private static final String BUSAN_PORT = "BUSAN";

	private static final String BUSAN_NEW_PORT = "BUSAN NEW";

	private String currentYear;

	private Vessel vesselInfo;// ���� ����

	private int currentMonth;	

	private SAXBuilder builder;

	private List<ShippersTable> tableList; // db���� ��ȸ�� ���̺� ���� ���

	private ShippersTable parameter; // �Ķ����

	private int scheduleID;// �� ������ ���� �ε���

	private ShippersTable shipperTableData; // ���� ���̺� ����

	private ADVData advData;// xml ���� ��������

	private PortInfo toPortInfo,fromPortInfo;

	private ByteArrayInputStream stream;

	private Document document;

	private String tableID;

	private String[][] arrayDatas;

	private Element root;

	private String fromDates[];
	private String toDates[];

	private String[][] vesselArrayDatas;

	private String vesselName;

	private String voyageNum;

	private List<PortInfo> portList,portAbbrList;

	// ������ ������ ���� ��ý
	private HashMap<String, Vector<ScheduleData>> areaKeyMap;

	private Vector<TablePort> portDataArray;

	private TablePort fromPortTableInfo;

	private TablePort toTablePortInfo;

	private String errorfileName;

	private String inlnadPortStr;//������ �ױ�

	private PortInfo inlnadPortInfo;

	private String[] inlandDates;

	private int inlandPortIndexLists[];

	public DefaultWebScheduleV2(int type,ShippersTable parameter) throws SQLException {
		super();

		logger.info("���ۿ� �������� �ʱ�ȭ ����...");

		this.parameter = parameter;

		tableService = DAOManager.getInstance().createTableService();

		advService = DAOManager.getInstance().createADVService();

		builder = new SAXBuilder();		

		areaKeyMap = new HashMap<String, Vector<ScheduleData>>();

		for(int i=1;i<8;i++)
		{
			areaKeyMap.put("0"+i, new Vector<ScheduleData>());
			logger.debug("���� Ű ����:"+"0"+i);
		}		

		portList = baseService.getPortInfoList();

		logger.debug("Port ����:"+portList.size());

		portAbbrList = baseService.getPort_AbbrList();

		logger.debug("PortAbbr ����:"+portAbbrList.size());

		currentYear = dateTypeYear.format(new Date());

		currentMonth = Integer.valueOf(dateTypeMonth.format(new Date()));

		logger.debug("�Է��� : "+parameter.getDate_isusse()+", ���� ����:"+currentYear+"."+currentMonth);

		this.format_type = type;
		switch (this.format_type) {
		case FORMAT_NOMAL:
			webScheduleFormat = new NomalWebScheduleFormat(this);

			break;
		case FORMAT_CONSOLE:
			webScheduleFormat = new ConsloeWebScheduleFormat(this);
			logger.info("console format ����");

			break;
		case FORMAT_INLNAD:
			webScheduleFormat = new InlandWebScheduleFormat(this);

			break;	

		default:
			break;
		}


		this.fileName = webScheduleFormat.getFileName();

		this.errorfileName = webScheduleFormat.getErrorFileName();

		logger.info("���ۿ� �������� �ʱ�ȭ ����...");

	}

	/**
	 * @���� ���� ������ �ִ��� Ȯ��
	 * @param tableData
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	protected boolean isTableDataValidation(ShippersTable tableData) throws SQLException, ParseException
	{

		advData = (ADVData) advService.getADVData(tableData.getTable_id());
		// �Էµ� ���� ������ ������ ���
		if(advData==null||advData.getData()==null)
		{
			return true;
		}
		return false;
	}
	/**
	 * �ױ� �迭���� �ױ� ���� ����
	 * 
	 * @param array
	 * @param index
	 * @return
	 */
	private TablePort getTablePort(Vector<TablePort> array,int index)
	{
		TablePort port1 = null;

		for(int i=0;i<array.size();i++)
		{

			TablePort port=(TablePort) array.get(i);
			if(port.getPort_index()==index)
				port1= port;
		}
		return port1;

	}
	/**
	 * @param portName
	 * @return
	 */
	public PortInfo getPortInfoByPortName(String portName)
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

	/**
	 * @param portName
	 * @return
	 */
	public PortInfo getPortInfoAbbrByPortName(String portName)
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
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	private PortInfo getPortInfo(String portName)
	{
		PortInfo  portInfo=getPortInfoByPortName(portName );
		if(portInfo!=null)
		{
			return portInfo;

		}else
		{
			PortInfo  portabbrInfo=getPortInfoAbbrByPortName(portName );
			if(portabbrInfo!=null)
			{
				return portabbrInfo;

			}else
			{
				return null;
			}
		}
	}	

	private int[] extractPortIndex(String strIndex)
	{


		int indexList[];
		try{
			StringTokenizer st = new StringTokenizer(strIndex.trim(),"#");

			ArrayList<Integer> list = new ArrayList<Integer>();

			while(st.hasMoreTokens())
			{
				int indexItem =Integer.parseInt(st.nextToken().trim());
				list.add(indexItem);
			}
			indexList = new int[list.size()];
			for(int i=0;i<list.size();i++)
			{
				indexList[i] = list.get(i);
			}
			logger.debug("extract Port Index:"+strIndex.trim() + ", size:"+indexList.length);
			return indexList;
		}catch(NumberFormatException e)
		{
			indexList = new int[0];
			logger.debug("extract Port Index:"+strIndex.trim() + ", size:"+indexList.length);
			return indexList;
		}

	}

	/** 
	 * �־��� �ױ����� �ִ��� �Ǵ��ؼ� �ִ� �ױ��� �ױ� �ε�����, �ƴϸ� -1�� ��ȯ
	 * @param array
	 * @param portName
	 * @return
	 */
	private int isExitPort(Vector<TablePort> array, int indexs[], String portName)
	{
		try{
			for(int i=0;i<indexs.length;i++)
			{
				int portIndex =indexs[i];


				TablePort port=this.getTablePort(array, portIndex);
				PortInfo searchOutOldPort = this.getPortInfo(port.getPort_name());

				if(searchOutOldPort==null)
					return -1;

				if(searchOutOldPort.getPort_name().equals(portName))
					return portIndex;
			}
		}catch(Exception e)
		{
			logger.error(e.getMessage()+",portName:"+portName);
			e.printStackTrace();
			return -1;
		}

		return -1;
	}

	@SuppressWarnings("finally")
	@Override
	public int execute() {

		try {
			logger.info("���ۿ� ������ ���� ����...");

			tableList = tableService.getTableListByDate(parameter);

			lengthOfTask =tableList.size();

			current=0;

			scheduleID =0;

			logger.info("���̺� ��: "+lengthOfTask);

			processDialog = new ScheduleBuildMessageDialog(this);

			processDialog.setMessage(fileName+"("+lengthOfTask+"��)");

			processDialog.createAndUpdateUI();

			processDialog.setTask(this);			

			Iterator<ShippersTable> iter = tableList.iterator();

			this.message ="�� ���ۿ� ������ ���� ��...";

			while(iter.hasNext())
			{					
				try {
					shipperTableData = iter.next();

					logger.debug("tableID:"+shipperTableData.getTable_id()+", page:"+shipperTableData.getPage()+" ó��");
					// �� ���̺� ���� ������ �̻��� ���� ��� ó�� ���� ����
					if(isTableDataValidation(shipperTableData))
					{
						continue;
					}

					// ���̺� �ش��ϴ� ���� ����
					this.arrayDatas = this.getXMLDataArray(advData.getData());

					// TS ����
					boolean isTS =shipperTableData.getTS()!=null&&shipperTableData.getTS().equals("TS");

					// ���̺� �ش��ϴ� ���� ����					
					vesselArrayDatas = this.getXMLFullVesselArray(isTS, advData.getData());

					// �� �ױ��� �ش��ϴ� �ε��� ����
					//HashMap<Integer, Integer> portIndexMap=makePortArrayWebIndexMap(shipperTableData);

					portDataArray=getPortList(shipperTableData);

					tableID = shipperTableData.getTable_id();

					int outboundFromPortIndexList[]=extractPortIndex(shipperTableData.getOut_port());

					int outboundToPortIndexList[]=extractPortIndex(shipperTableData.getOut_to_port());


					int inboundFromPortIndexList[]=extractPortIndex(shipperTableData.getIn_to_port());

					int inboundToPortIndexList[]=extractPortIndex(shipperTableData.getIn_port());
					
					if(format_type==FORMAT_INLNAD)
					{
						inlandPortIndexLists = extractPortIndex(shipperTableData.getInland_indexs());
					}

					// outbound �λ������ �ִ��� �Ǵ�						
					int outbundBusanNewPortIndex=isExitPort(portDataArray, outboundFromPortIndexList, BUSAN_NEW_PORT);

					// outbound �λ����� �ִ��� �Ǵ�
					int outboundBusanPortIndex=isExitPort(portDataArray, outboundFromPortIndexList, BUSAN_PORT);

					// inbound �λ������ �ִ��� �Ǵ�						
					int inbundBusanNewPortIndex=isExitPort(portDataArray, inboundToPortIndexList, BUSAN_NEW_PORT);

					// inbound �λ����� �ִ��� �Ǵ�
					int inboundBusanPortIndex=isExitPort(portDataArray, inboundToPortIndexList, BUSAN_PORT);


					logger.debug("BUSAN PORT:"+outboundBusanPortIndex+", BUSAN NEW PORT:"+outbundBusanNewPortIndex);

					for(int vslIndex=0;vslIndex<vesselArrayDatas.length;vslIndex++)
					{	
						try {
							vesselName = vesselArrayDatas[vslIndex][0];


							vesselInfo=scheduleManager.searchVessel(vesselName);
							
							// ������� �ʴ� �����̸� �����ٿ��� ����
							if(vesselInfo.getVessel_use()==Vessel.NON_USE)
								continue;

							voyageNum = vesselArrayDatas[vslIndex][1];

							logger.debug("vessel index:"+vslIndex+",���ڸ�:"+vesselName+", voyage:"+voyageNum+" �� ������ ����" );
							
							
							if(format_type==FORMAT_INLNAD)
							{
								logger.debug("<outbound>");
								makeInlandWebSchedule(ScheduleService.OUTBOUND,outboundFromPortIndexList, outboundToPortIndexList,inlandPortIndexLists,outboundBusanPortIndex, outbundBusanNewPortIndex,
										vslIndex);

								logger.debug("<inbound>");
								makeInlandWebSchedule(ScheduleService.INBOUND,inboundFromPortIndexList, inboundToPortIndexList,inlandPortIndexLists,inboundBusanPortIndex, inbundBusanNewPortIndex,
										vslIndex);
							}
							else
							{
								logger.debug("<outbound>");
								makeWebSchedule(ScheduleService.OUTBOUND,outboundFromPortIndexList, outboundToPortIndexList,outboundBusanPortIndex, outbundBusanNewPortIndex,
										vslIndex);

								logger.debug("<inbound>");
								makeWebSchedule(ScheduleService.INBOUND,inboundFromPortIndexList, inboundToPortIndexList,inboundBusanPortIndex, inbundBusanNewPortIndex,
										vslIndex);
							}

						} catch (ResourceNotFoundException e1) {
							logger.error("vessel name is null:"+vesselName);
							continue;
						}

					}
					logger.debug("tableID:"+shipperTableData.getTable_id()+",page:"+shipperTableData.getPage()+" ó�� ����\n");
					// ���� �߻��� ���� ó�� �� ���� ���̺� ����
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch(Exception e)
				{
					e.printStackTrace();
					logger.info("tableID:"+shipperTableData.getTable_id()+", error:"+e.getMessage());
				}

				current++;
			}
			try {
				printWebSchedule();
				JOptionPane.showMessageDialog(processDialog, "�� ������ ���� �Ϸ�");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (SQLException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
			return ScheduleJoint.FAILURE; 

		}
		finally{
			processDialog.setVisible(false);
			done=true;
			if(this.processDialog!=null){
				this.processDialog.setVisible(false);
				this.processDialog.dispose();
			}
		}

		logger.info("���ۿ� ������ ���� ����...");
		return ScheduleJoint.SUCCESS;
	}

	private void makeInlandWebSchedule(String inOutType,int[] fromPortIndexList, int[] toPortIndexList,int[] inlandIndexList,
			int busanPortIndex,int busanNewPortIndex, int vslIndex) throws SQLException
	{
		// ����� ��ȸ
		for(int fromPortIndex=0;fromPortIndex<fromPortIndexList.length;fromPortIndex++)
		{						
			int fromPortIndexA=fromPortIndexList[fromPortIndex];
			// ����� ����
			try{
				// �ƿ��ٿ��, �λ���, �λ� ���� �Ѵ� �ִ� ��� ����
				if(inOutType.equals(ScheduleService.OUTBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
				{	
					// �λ��� �ε����� �λ� �������� ����
					/* skip�ϱ� ���� ����
					 * 1. �λ���, �λ������ ���ÿ� �����ϴ� ���
					 * 2. �λ� ���� �������� �ִ� ���
					 */
					if(fromPortIndexA==busanPortIndex)
					{
						try{

							KSGDateUtil.getDates(arrayDatas[vslIndex][busanNewPortIndex-1],currentMonth,currentYear);
							logger.info("�λ���� �����ٷ� ���� �λ��� ������ skip:"+shipperTableData.getTable_id()+","+busanNewPortIndex+","+arrayDatas[vslIndex][busanNewPortIndex-1]);
							continue;	
						}catch(Exception e)
						{
							// �λ���, �λ���� �Ѵ� ���� ������ �λ������ ��¥ ������ ���� ��� �λ��� ���� ǥ��
						}
					}
				}
				
				fromPortTableInfo = this.getTablePort(portDataArray, fromPortIndexA);
				
				// �ƿ��ٿ��, �λ���, �λ� ���� �Ѵ� �ִ� ��� ����
				if(inOutType.equals(ScheduleService.OUTBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
				{
					
					//�λ���� �ױ����� �λ������� ���� 
					if(fromPortIndexA==busanNewPortIndex)
					{	
						fromPortTableInfo = this.getTablePort(portDataArray, busanPortIndex);
					}
				}
				
				//�ױ����� �λ� ������ ��� �λ������� ����				
				if(fromPortTableInfo.getPort_name().equals(BUSAN_NEW_PORT))
				{
					fromPortTableInfo.setPort_name(BUSAN_PORT);
				}
				

				if(fromPortTableInfo== null)
				{
					logger.error(shipperTableData.getTable_id()+" ��ϵ��� ���� �ױ��� ����, ���̺� ���� �� �ױ� �ε��� Ȯ��, index: "+fromPortIndexA);										
					continue;

				}

				fromPortInfo = scheduleManager.searchPort(fromPortTableInfo.getPort_name());

				logger.debug("\tFrom Port Index:"+fromPortIndexA+" from port name:"+fromPortTableInfo.getPort_name()+", from dates:"+arrayDatas[vslIndex][fromPortIndexA-1]);
			}
			catch(PortNullException e)
			{
				logger.error("tableID: "+shipperTableData.getTable_id()+", from port name null:"+fromPortTableInfo.getPort_name());
				
				continue;
			}
			
			// ����� ����
			try{

				/* ����� �Է��� ���� 1���ͽ���
				 * ���α׷������� 0���� ����
				 */
				fromDates = KSGDateUtil.getDates(arrayDatas[vslIndex][fromPortIndexA-1],currentMonth,currentYear);


			}
			catch(VesselDateNotMatchException e)
			{
				//��¥ ������ �ٸ��ٸ� �н�
				//logger.error("outbound ����� ��¥ ���� ����:"+arrayDatas[vslIndex][outboundFromPort[outboundFromPortIndex]-1]);
				continue;
			}


			//������ ��ȸ
			for(int toPortIndex=0;toPortIndex<toPortIndexList.length;toPortIndex++)
			{								
				int toPortIndexA=toPortIndexList[toPortIndex];
				// ������ ����
				try{

					// �ιٿ��, �λ���, �λ� ���� �Ѵ� �ִ� ���
					if(inOutType.equals(ScheduleService.INBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
					{	
						// �λ��� �ε����� �λ� �������� ����
						/* skip�ϱ� ���� ����
						 * 1. �λ���, �λ������ ���ÿ� �����ϴ� ���
						 * 2. �λ� ���� �������� �ִ� ���
						 */
						if(toPortIndexA==busanPortIndex)
						{						
							try{
								KSGDateUtil.getDates(arrayDatas[vslIndex][busanNewPortIndex-1],currentMonth,currentYear);
								logger.info("�λ���� �����ٷ� ���� �λ��� ������ skip:"+shipperTableData.getTable_id()+","+busanNewPortIndex);
								continue;	
							}catch(Exception e)
							{
								// �λ���, �λ���� �Ѵ� ���� ������ �λ������ ��¥ ������ ���� ��� ���� ����

							}
						}
					}

					toTablePortInfo = this.getTablePort(portDataArray, toPortIndexA);
					
					// �ιٿ��, �λ���, �λ� ���� �Ѵ� �ִ� ���
					if(inOutType.equals(ScheduleService.INBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
					{
						//�λ� ���� �ױ����� �λ������� ����
						if(toPortIndexA==busanNewPortIndex)
						{
							toTablePortInfo = this.getTablePort(portDataArray, busanPortIndex);
						}
					}

					if(toTablePortInfo== null)
					{
						logger.error(shipperTableData.getTable_id()+" ��ϵ��� ���� �ױ��� ����, ���̺� ���� Ȯ��");										
						continue;
					}
					
					//�ױ����� �λ� ������ ��� �λ������� ����
					if(toTablePortInfo.getPort_name().equals(BUSAN_NEW_PORT))
					{
						toTablePortInfo.setPort_name(BUSAN_PORT);
					}

					toPortInfo = scheduleManager.searchPort(toTablePortInfo.getPort_name());
					
					
				}catch(PortNullException e)
				{
					logger.error("to port name is null:"+toTablePortInfo.getPort_name());
					continue;
				}

				// ������ ����
				try {

					logger.debug("\t\tTo Port Index:"+(toPortIndexA)+", port name:"+toTablePortInfo.getPort_name()+", to dates:"+arrayDatas[vslIndex][toPortIndexA-1]);

					toDates = KSGDateUtil.getDates(arrayDatas[vslIndex][toPortIndexA-1],currentMonth,currentYear);

				} catch (VesselDateNotMatchException e) {
					//logger.error("outbound ������ ��¥ ���� ����:"+arrayDatas[vslIndex][outboundToPort[outboundToPortIndex]-1]);
					//e.printStackTrace();
					continue;
				}				


				// ������ ��ȸ
				for(int inlnadIndex=0;inlnadIndex<inlandIndexList.length;inlnadIndex++)
				{
					int inlandIndexA=inlandIndexList[inlnadIndex];
					//������ �ױ� ����
					TablePort inlandTablePortInfo = this.getTablePort(portDataArray, inlandIndexA);


					if(inlandTablePortInfo== null)
					{
						logger.error(shipperTableData.getTable_id()+" ��ϵ��� ���� ������ �ױ��� ����, ���̺� ���� Ȯ��");										
						continue;
					}

					try{
						inlnadPortInfo = scheduleManager.searchPort(inlandTablePortInfo.getPort_name());
					}catch(PortNullException e)
					{
						logger.error("to port name is null:"+inlandTablePortInfo.getPort_name());
						continue;
					}
					
					// ������ ����
					try {

						logger.debug("\t\t\tInland Port Index:"+(inlandIndexA)+", port name:"+inlnadPortInfo.getPort_name()+", to dates:"+arrayDatas[vslIndex][inlandIndexA-1]);

						inlandDates = KSGDateUtil.getDates(arrayDatas[vslIndex][inlandIndexA-1],currentMonth,currentYear);
					} catch (VesselDateNotMatchException e) {
						//logger.error("outbound ������ ��¥ ���� ����:"+arrayDatas[vslIndex][outboundToPort[outboundToPortIndex]-1]);
						//e.printStackTrace();
						continue;
					}
					insertInlandWebSchedule(inOutType, fromPortInfo, fromDates, vesselInfo, toPortInfo, toDates, voyageNum, inlnadPortInfo.getPort_name(), inlandDates, shipperTableData);
					
				}
			}

		}
	}

	private void makeWebSchedule(String inOutType,int[] fromPortIndexList, int[] toPortIndexList,
			int busanPortIndex,int busanNewPortIndex, int vslIndex) throws SQLException {

		// ����� ��ȸ
		for(int fromPortIndex=0;fromPortIndex<fromPortIndexList.length;fromPortIndex++)
		{						
			int fromPortIndexA=fromPortIndexList[fromPortIndex];

			// ����� ����
			try{
				// �ƿ��ٿ��, �λ���, �λ� ���� �Ѵ� �ִ� ��� ����
				if(inOutType.equals(ScheduleService.OUTBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
				{	
					// �λ��� �ε����� �λ� �������� ����
					/* skip�ϱ� ���� ����
					 * 1. �λ���, �λ������ ���ÿ� �����ϴ� ���
					 * 2. �λ� ���� �������� �ִ� ���
					 */
					if(fromPortIndexA==busanPortIndex)
					{
						try{

							KSGDateUtil.getDates(arrayDatas[vslIndex][busanNewPortIndex-1],currentMonth,currentYear);
							logger.info("�λ���� �����ٷ� ���� �λ��� ������ skip:"+shipperTableData.getTable_id()+","+busanNewPortIndex+","+arrayDatas[vslIndex][busanNewPortIndex-1]);
							continue;	
						}catch(Exception e)
						{
							// �λ���, �λ���� �Ѵ� ���� ������ �λ������ ��¥ ������ ���� ��� �λ��� ���� ǥ��
						}
					}
				}

				fromPortTableInfo = this.getTablePort(portDataArray, fromPortIndexA);
				
				// �ƿ��ٿ��, �λ���, �λ� ���� �Ѵ� �ִ� ���
				if(inOutType.equals(ScheduleService.OUTBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
				{
					// �λ� ���� �ױ� ���� �λ������� ����
					if(fromPortIndexA==busanNewPortIndex)
					{
						fromPortTableInfo = this.getTablePort(portDataArray, busanPortIndex);
					}
				}

				if(fromPortTableInfo== null)
				{
					logger.error(shipperTableData.getTable_id()+" ��ϵ��� ���� �ױ��� ����, ���̺� ���� �� �ױ� �ε��� Ȯ��, index: "+fromPortIndexA);										
					continue;
				}

				fromPortInfo = scheduleManager.searchPort(fromPortTableInfo.getPort_name());

				logger.debug("\tFrom Port Index:"+fromPortIndexA+" from port name:"+fromPortTableInfo.getPort_name()+", from dates:"+arrayDatas[vslIndex][fromPortIndexA-1]);
			}
			catch(PortNullException e)
			{
				logger.error("tableID: "+shipperTableData.getTable_id()+", from port name null:"+fromPortTableInfo.getPort_name());
				System.err.println("from port name null:"+fromPortTableInfo.getPort_name());
				continue;
			}

			// ����� ����
			try{

				/* ����� �Է��� ���� 1���ͽ���
				 * ���α׷������� 0���� ����
				 */
				fromDates = KSGDateUtil.getDates(arrayDatas[vslIndex][fromPortIndexA-1],currentMonth,currentYear);


			}
			catch(VesselDateNotMatchException e)
			{
				//��¥ ������ �ٸ��ٸ� �н�
				//logger.error("outbound ����� ��¥ ���� ����:"+arrayDatas[vslIndex][outboundFromPort[outboundFromPortIndex]-1]);
				continue;
			}



			//������ ��ȸ
			for(int toPortIndex=0;toPortIndex<toPortIndexList.length;toPortIndex++)
			{	

				// ������ �ε���
				int toPortIndexA=toPortIndexList[toPortIndex];

				// ������ ����
				try{

					// �ιٿ��, �λ���, �λ� ���� �Ѵ� �ִ� ��� ����
					if(inOutType.equals(ScheduleService.INBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
					{	
						// �λ��� �ε����� �λ� �������� ����
						/* skip�ϱ� ���� ����
						 * 1. �λ���, �λ������ ���ÿ� �����ϴ� ���
						 * 2. �λ� ���� �������� �ִ� ���
						 */
						if(toPortIndexA==busanPortIndex)
						{						
							try{
								KSGDateUtil.getDates(arrayDatas[vslIndex][busanNewPortIndex-1],currentMonth,currentYear);
								logger.info("�λ���� �����ٷ� ���� �λ��� ������ skip:"+shipperTableData.getTable_id()+","+busanNewPortIndex);
								continue;	
							}catch(Exception e)
							{
								// �λ���, �λ���� �Ѵ� ���� ������ �λ������ ��¥ ������ ���� ��� ���� ����

							}
						}
					}

					toTablePortInfo = this.getTablePort(portDataArray, toPortIndexA);
					
					// �ιٿ��, �λ���, �λ� ���� �Ѵ� �ִ� ��� ����
					if(inOutType.equals(ScheduleService.INBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
					{
						// �λ� ���� �ױ� ���� �λ������� ����
						if(toPortIndexA==busanNewPortIndex)
						{
						toTablePortInfo = this.getTablePort(portDataArray, busanPortIndex);
						}
					}


					if(toTablePortInfo== null)
					{
						logger.error(shipperTableData.getTable_id()+" ��ϵ��� ���� �ױ��� ����, ���̺� ���� Ȯ��");										
						continue;
					}

					toPortInfo = scheduleManager.searchPort(toTablePortInfo.getPort_name());
					
				}catch(PortNullException e)
				{
					logger.error("to port name is null:"+toTablePortInfo.getPort_name());
					continue;
				}

				// ������ ����
				try {

					logger.debug("\t\tTo Port Index:"+(toPortIndexA)+", port name:"+toTablePortInfo.getPort_name()+", to dates:"+arrayDatas[vslIndex][toPortIndexA-1]);

					toDates = KSGDateUtil.getDates(arrayDatas[vslIndex][toPortIndexA-1],currentMonth,currentYear);
				} catch (VesselDateNotMatchException e) {
					//logger.error("outbound ������ ��¥ ���� ����:"+arrayDatas[vslIndex][outboundToPort[outboundToPortIndex]-1]);
					//e.printStackTrace();
					continue;
				}

				switch (format_type) {
				case FORMAT_NOMAL:
					insertNomalWebSchedule(inOutType, fromPortInfo, fromDates, vesselInfo, toPortInfo, toDates, voyageNum, shipperTableData);
					break;
				case FORMAT_CONSOLE:

					String closingTimes[] = new String[2];
					closingTimes[0] = shipperTableData.getD_time()==0?"":arrayDatas[vslIndex][shipperTableData.getD_time()-1];
					closingTimes[1] = shipperTableData.getC_time()==0?"":arrayDatas[vslIndex][shipperTableData.getC_time()-1];
					insertConsoleWebSchedule(inOutType, fromPortInfo, fromDates, vesselInfo, toPortInfo, toDates, voyageNum, closingTimes, shipperTableData);
					break;

				default:
					break;
				}
			}
		}
	}
	
	/**�ܼ� Ÿ�� �� ������ ���� ����
	 * @param inOutType �ιٿ��/�ƿ��ٿ�� Ÿ��
	 * @param fromPort ����� ����
	 * @param fromDates ����� ���� (ETA, ETD)
	 * @param vesselInfo ���� ����
	 * @param toPort ������ ����
	 * @param toDates ������ ����(ETA, ETD)
	 * @param voyageNum ���� ��ȣ
	 * @param closingTimes Closing Time(DCT,CCT)
	 * @param shipperTableData ���̺� ����
	 */
	private void insertInlandWebSchedule(String inOutType, PortInfo fromPort, String fromDates[], Vessel vesselInfo, PortInfo toPort, String toDates[],String voyageNum,String inlandPort, String[] inlandDates, ShippersTable shipperTableData)
	{
		ScheduleData webScheduleData =  new ScheduleData();

		webScheduleData.setInOutType(inOutType); 								// in, out Ÿ��
		webScheduleData.setFromPort(fromPort.getPort_name());					//�����
		webScheduleData.setDateF(fromDates[0]);									//����� ETA
		webScheduleData.setDateFBack(fromDates[1]);								//����� ETD
		webScheduleData.setVessel(vesselInfo.getVessel_name());					//���ڸ�
		webScheduleData.setPort(toPort.getPort_name());							//������
		webScheduleData.setDateT(toDates[0]);									//������ ETA
		webScheduleData.setDateTBack(toDates[1]);								//������ ETD
		webScheduleData.setVoyage_num(voyageNum);								//������ȣ
		webScheduleData.setCompany_abbr(shipperTableData.getCompany_abbr());	//Line
		webScheduleData.setAgent(shipperTableData.getAgent());					//������Ʈ
		webScheduleData.setBookPage(shipperTableData.getBookPage());			//å ������
		webScheduleData.setVessel_type(vesselInfo.getVessel_type());			//����Ÿ��
		webScheduleData.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi()); //MMSI		
		webScheduleData.setFromAreaCode(fromPort.getArea_code());				//����� �����ڵ�
		webScheduleData.setArea_code(toPort.getArea_code());					//������ �����ڵ�
		webScheduleData.setOperator(getOperator(vesselInfo.getVessel_company()));// ���� ��ǥ��ü
		webScheduleData.setInland_port(inlandPort);
		webScheduleData.setInland_date(inlandDates[0]);							//������ ETA
		webScheduleData.setInland_date_back(inlandDates[1]);					//������ ETD
		

		addWebData(webScheduleData);
	}
	

	/**
	 * @param inOutType �ιٿ��/�ƿ��ٿ�� Ÿ��
	 * @param fromPort ����� ����
	 * @param fromDates ����� ���� (ETA, ETD)
	 * @param vesselInfo ���� ����
	 * @param toPort ������ ����
	 * @param toDates ������ ����(ETA, ETD)
	 * @param voyageNum ���� ��ȣ
	 * @param shipperTableData ���̺� ����
	 */
	private void insertNomalWebSchedule(String inOutType, PortInfo fromPort, String fromDates[], Vessel vesselInfo, PortInfo toPort, String toDates[],String voyageNum, ShippersTable shipperTableData)
	{
		ScheduleData webScheduleData =  new ScheduleData();

		webScheduleData.setInOutType(inOutType); 								// in, out Ÿ��
		webScheduleData.setFromPort(fromPort.getPort_name());					//�����
		webScheduleData.setDateF(fromDates[0]);									//����� ETA
		webScheduleData.setDateFBack(fromDates[1]);								//����� ETD
		webScheduleData.setVessel(vesselInfo.getVessel_name());					//���ڸ�
		webScheduleData.setPort(toPort.getPort_name());							//������
		webScheduleData.setDateT(toDates[0]);									//������ ETA
		webScheduleData.setDateTBack(toDates[1]);								//������ ETD
		webScheduleData.setVoyage_num(voyageNum);								//������ȣ
		webScheduleData.setCompany_abbr(shipperTableData.getCompany_abbr());	//Line
		webScheduleData.setAgent(shipperTableData.getAgent());					//������Ʈ
		webScheduleData.setBookPage(shipperTableData.getBookPage());			//å ������
		webScheduleData.setVessel_type(vesselInfo.getVessel_type());			//����Ÿ��
		webScheduleData.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi()); //MMSI
		webScheduleData.setOperator(vesselInfo.getVessel_company().contains("/")?vesselInfo.getVessel_company().substring(0,vesselInfo.getVessel_company().indexOf("/")):vesselInfo.getVessel_company());
		webScheduleData.setFromAreaCode(fromPort.getArea_code());
		webScheduleData.setArea_code(toPort.getArea_code());		

		addWebData(webScheduleData);
	}

	/**�ܼ� Ÿ�� �� ������ ���� ����
	 * @param inOutType �ιٿ��/�ƿ��ٿ�� Ÿ��
	 * @param fromPort ����� ����
	 * @param fromDates ����� ���� (ETA, ETD)
	 * @param vesselInfo ���� ����
	 * @param toPort ������ ����
	 * @param toDates ������ ����(ETA, ETD)
	 * @param voyageNum ���� ��ȣ
	 * @param closingTimes Closing Time(DCT,CCT)
	 * @param shipperTableData ���̺� ����
	 */
	private void insertConsoleWebSchedule(String inOutType, PortInfo fromPort, String fromDates[], Vessel vesselInfo, PortInfo toPort, String toDates[],String voyageNum,String closingTimes[], ShippersTable shipperTableData)
	{
		ScheduleData webScheduleData =  new ScheduleData();

		webScheduleData.setInOutType(inOutType); 								// in, out Ÿ��
		webScheduleData.setFromPort(fromPort.getPort_name());					//�����
		webScheduleData.setDateF(fromDates[0]);									//����� ETA
		webScheduleData.setDateFBack(fromDates[1]);								//����� ETD
		webScheduleData.setVessel(vesselInfo.getVessel_name());					//���ڸ�
		webScheduleData.setPort(toPort.getPort_name());							//������
		webScheduleData.setDateT(toDates[0]);									//������ ETA
		webScheduleData.setDateTBack(toDates[1]);								//������ ETD
		webScheduleData.setVoyage_num(voyageNum);								//������ȣ
		webScheduleData.setCompany_abbr(shipperTableData.getCompany_abbr());	//Line
		webScheduleData.setAgent(shipperTableData.getAgent());					//������Ʈ
		webScheduleData.setBookPage(shipperTableData.getBookPage());			//å ������
		webScheduleData.setVessel_type(vesselInfo.getVessel_type());			//����Ÿ��
		webScheduleData.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi()); //MMSI		
		webScheduleData.setFromAreaCode(fromPort.getArea_code());				//����� �����ڵ�
		webScheduleData.setArea_code(toPort.getArea_code());					//������ �����ڵ�

		webScheduleData.setOperator(getOperator(vesselInfo.getVessel_company()));// ���� ��ǥ��ü
		webScheduleData.setD_time(closingTimes[0]); //DCT
		webScheduleData.setC_time(closingTimes[1]); //CCT		
		webScheduleData.setConsole_cfs(getNewCFS(shipperTableData.getConsole_cfs())); //CFS
		webScheduleData.setConsole_page(shipperTableData.getConsole_page());  // �ܼ� ������

		addWebData(webScheduleData);
	}
	private String getOperator(String vessel_company)
	{
		if(vessel_company.contains("/"))
		{
			return vessel_company.substring(0,vessel_company.indexOf("/"));
		}
		else
		{
			return vessel_company;
		}	

	}

	/**
	 * @since 2015-10-12
	 * @���� �Էµ� CFS ���� �� ���๮�ڸ� $�� ġȯ�Ͽ� ��ȯ
	 * @param cfs �ܼ� CFS ����
	 * @return
	 */
	private String getNewCFS(String cfs)
	{		
		return cfs.replaceAll("\n", "\\\\&");
	}




	/**
	 * @���� ��ü ������ ���� �������� ���
	 * @throws IOException
	 */
	private void printWebSchedule() throws IOException
	{
		logger.info("���� ��� ����...");

		Set<String> keyList = areaKeyMap.keySet();

		Iterator<String> iter = keyList.iterator();

		while(iter.hasNext())
		{
			String keyArea = iter.next();
			writeWebSchedule(keyArea, areaKeyMap.get(keyArea));
		}

		areaKeyMap.clear();

		logger.info("���� ��� ����");
	}
	/**
	 * @���� ���� ������ ���� ���
	 * @param areaName
	 * @param vesselList
	 * @throws IOException
	 */
	private void writeWebSchedule(String areaName,Vector<ScheduleData> vesselList) throws IOException
	{
		logger.info(areaName+"����("+vesselList.size()+"��)���� ��� ����...");

		makeFile(areaName);

		FileWriter fw = new FileWriter(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+fileName+areaName);
		logger.info("file name:"+fileName);


		for(int i=0;i<vesselList.size();i++)
		{
			ScheduleData data = vesselList.get(i);
			//logger.debug(webScheduleFormat.toWebScheduleString(data));

			fw.write(data.getArea_code()+"\t"+scheduleID+"\t"+webScheduleFormat.toWebScheduleString(data)+"\n");

			scheduleID++;
		}

		fw.flush();

		fw.close();

		logger.info(areaName+"���� ���� ��� ����");
	}


	/**
	 * @param data
	 */
	private void addWebData(ScheduleData data)
	{
		String key =data.getFromAreaCode();
		logger.debug("area code:"+key);
		if(areaKeyMap.containsKey(key))
		{	
			Vector<ScheduleData> area=areaKeyMap.get(key);
			area.add(data);
		}
	}

	/**
	 * @param fileName
	 */
	private void makeFile(String fileName)
	{
		File fo = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION));
		if(!fo.exists())
		{
			fo.mkdir();
		}

		File file = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+fileName);
		if(file.exists())
		{
			if(file.delete())
			{
				logger.info("���� ���� ����");
			}
		}

	}
	/**
	 * @param ports
	 * @return
	 */
	private String getPortIndexInfo(String ports)
	{
		if(ports==null)
		{
			return "";
		}
		else
		{
			return ports.trim();
		}
	}
	/**
	 * @param portName
	 * @return
	 */
	public PortInfo getPortAreaCode(String portName){
		PortInfo  portInfo=getPortInfoByPortName(portName );
		if(portInfo!=null)
		{
			return portInfo;
		}else
		{
			PortInfo  portabbrInfo=getPortInfoAbbrByPortName(portName );
			if(portabbrInfo!=null)
			{
				return portabbrInfo;
			}else
			{
				return null;
			}
		}
	}

	/**
	 *  ���̺� ���̵� �������� ��Ʈ ���� ����
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public Vector<TablePort> getPortList(ShippersTable table) throws SQLException 
	{
		Vector<TablePort> portDataArray  = new Vector<TablePort>();
		TablePort tablePort = new TablePort();
		tablePort.setTable_id(table.getTable_id());
		tablePort.setPort_type(TablePort.TYPE_PARENT);

		List<TablePort> li=tableService.getTablePortList(tablePort);

		for(int i=0;i<li.size();i++)
		{
			TablePort port = li.get(i);

			portDataArray.add(port);
		}

		return portDataArray;
	}


	/**
	 * @���� ���̺� �������� �� �ױ���Ͽ� ���� �ε��� ���� ����
	 * @param outPortIndex �ƿ� �ٿ�� �����
	 * @param outToPortIndex �ƿ��ٿ�� ������
	 * @param inPortIndex �ιٿ�� �����
	 * @param inToPortIndex �ιٿ�� ������
	 * @return
	 * @throws NumberFormatException
	 */
	public HashMap<Integer, Integer> makePortArrayWebIndexMap(String outPortIndex, String outToPortIndex,String inPortIndex, String inToPortIndex) throws NumberFormatException{
		String delimiters = "#";
		HashMap<Integer, Integer> indexlist = new HashMap<Integer, Integer>();
		StringTokenizer st = new StringTokenizer(outPortIndex,delimiters);

		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(outToPortIndex,delimiters);
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(inPortIndex,delimiters);
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(inToPortIndex,delimiters);
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}
		return indexlist;
	}


	/**
	 * @param tableData
	 * @return
	 * @throws NumberFormatException
	 */
	private HashMap<Integer, Integer> makePortArrayWebIndexMap(ShippersTable tableData) throws NumberFormatException{


		String outPortIndex 	= getPortIndexInfo(tableData.getOut_port());
		String outToPortIndex	= getPortIndexInfo(tableData.getOut_to_port());
		String inPortIndex 		= getPortIndexInfo(tableData.getIn_port());
		String inToPortIndex 	= getPortIndexInfo(tableData.getIn_to_port());

		HashMap<Integer, Integer> indexlist = this.makePortArrayWebIndexMap(outPortIndex, outToPortIndex, inPortIndex, inToPortIndex);


		//TS �ױ� �ε���  �߰�
		if(tableData.getGubun().equals("TS"))
		{
			int ts_index=tableData.getTsIndex();

			if(!indexlist.containsKey(ts_index))
			{
				indexlist.put(ts_index, ts_index);
			}
		}

		return indexlist;
	}

	private String[][] getXMLDataArray(String data) throws JDOMException, IOException, OutOfMemoryError
	{
		// error Java heap space

		stream =new ByteArrayInputStream(data.getBytes());

		document= builder.build(stream);

		Element root = document.getRootElement();

		List vessel_list=root.getChildren("vessel");

		String[][] returndata = new String[vessel_list.size()][];

		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);			

			List li=vessel_info.getChildren("input_date");

			returndata[i] = new String[li.size()];

			for(int j=0;j<li.size();j++)
			{
				Element input_date=(Element) li.get(j);

				returndata[i][j]=input_date.getAttributeValue("date").trim();
			}
		}
		return returndata;
	}

	/**
	 * @param ts
	 * @param data
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public String[][] getXMLFullVesselArray(boolean ts,String data) throws JDOMException, IOException
	{	
		stream =new ByteArrayInputStream(data.getBytes());
		document= builder.build(stream);
		root = document.getRootElement();
		List vessel_list=root.getChildren("vessel");
		String[][] returndata = new String[vessel_list.size()][];
		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);
			if(ts){
				String vessel_name = vessel_info.getAttributeValue("ts_name");
				String voyage  = vessel_info.getAttributeValue("ts_voyage");
				returndata[i] = new String[2];
				returndata[i][0] =vessel_name;
				returndata[i][1] =voyage;
			}else
			{
				String vessel_name = vessel_info.getAttributeValue("full-name");

				String voyage  = vessel_info.getAttributeValue("voyage");
				returndata[i] = new String[2];
				returndata[i][0] =vessel_name;
				returndata[i][1] =voyage;
			}
		}
		return returndata;
	}

	@Override
	public void initTag() {
		// TODO Auto-generated method stub
		
	}

}
