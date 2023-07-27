package com.ksg.schedule.logic.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.dtp.api.schedule.joint.print.AbstractSchedulePrint;
import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.exception.PortNullException;
import com.ksg.common.exception.VesselNullException;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ScheduleEnum;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.PortIndexNotMatchException;
import com.ksg.schedule.logic.SchedulePrint;
import com.ksg.schedule.logic.print.ScheduleBuildUtil;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.service.ADVService;
import com.ksg.service.BaseService;
import com.ksg.service.ScheduleService;
import com.ksg.service.TableService;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

/**
 * @author archehyun
 *
 */
public class DefaultWebSchedule extends AbstractSchedulePrint {
	
	private SimpleDateFormat format = new SimpleDateFormat("M/d");
	
	private int totalTime;
	
	protected int[] a_outport,a_outtoport;
	
	protected int size_outport,size_outtoport;
	
	protected ADVService advService;
	
	protected String currentYear; // ���� ����
	
	private ShippersTable op;
	
	private SimpleDateFormat dateTypeYear = new SimpleDateFormat("yyyy");
	
	private SimpleDateFormat dateTypeMonth = new SimpleDateFormat("MM");
	
	private int currentMonth;
	
	protected HashMap<String, Vector<ScheduleData>> areaKeyMap;	
	
	protected Vector<TablePort> portDataArray;
	
	private ADVData advData;
	
	private String[][] arrayDatas;
	
	private List<ShippersTable> tableList;
	
	private String[][] vslDatas;
	
	private IFWebScheduleFormat webScheduleFormat;
	
	public static final int FORMAT_NOMAL=1;
	
	public static final  int FORMAT_CONSOLE=2;
	
	public static final  int FORMAT_INLNAD=3;
	
	private int format_type;
	
	private List<PortInfo> portList,portAbbrList;
	
	private int scheduleID;
	
	private PortInfo toPortInfo,fromPortInfo;
	
	private Vessel vesselInfo;
	
	private String fromDates[];
	
	private String fileName="";
	
	private String errorfileName="";
	
	private FileWriter errorFw;
	
	private Document document = null;
	
	private SAXBuilder builder ;

	private ByteArrayInputStream stream;

	private static final String BUSAN = "BUSAN";

	private static final String BUSAN_NEW_PORT = "BUSAN NEW";

	public static final int TYPE_INBOUND=1;

	public static final int TYPE_OUTBOUND=2;


	private String message;
	
	public String[] getFromDates() {
		return fromDates;
	}
	public void setFromDates(String[] fromDates) {
		this.fromDates = fromDates;
	}	
	private String outPortData,outToPortData;
	
	private String toDates[];
	
	private int[] a_outbound_port_index, 	a_outbound_toport_index;
	private int[] a_inbound_port_index, 	a_inbound_toport_index;
	
	private int[] a_inland_port_index;

	private String toPortStr,fromPortStr;
		
	private String inlnadPortStr;
	
	private String inlnad_date;

	private BaseService baseService;
	
	protected TableService tableService;

	private void init(int format_type) throws SQLException {

		tableService = DAOManager.getInstance().createTableService();

		advService = DAOManager.getInstance().createADVService();

		currentYear = dateTypeYear.format(new Date());

		builder = new SAXBuilder();

		currentMonth = Integer.valueOf(dateTypeMonth.format(new Date()));

		areaKeyMap = new HashMap<String, Vector<ScheduleData>>();

		this.format_type=format_type;

		for(int i=1;i<8;i++)
		{
			areaKeyMap.put("0"+i, new Vector<ScheduleData>());
		}		

		switch (this.format_type) {
		case FORMAT_NOMAL:
			webScheduleFormat = new NomalWebScheduleFormat(this);
			//fileName="WW_SYBASE";
			//errorfileName = "ww_sybase";
			break;
		case FORMAT_CONSOLE:
			webScheduleFormat = new ConsloeWebScheduleFormat(this);
			//fileName="CONSOLE";
			//errorfileName = "console";
			break;
		case FORMAT_INLNAD:
			webScheduleFormat = new InlandWebScheduleFormat(this);
			//fileName="INLAND";
			//errorfileName = "inlnad";
			break;	

		default:
			break;
		}

		this.fileName = webScheduleFormat.getFileName();

		this.errorfileName = webScheduleFormat.getErrorFileName();

		currentYear = dateTypeYear.format(new Date());

		portList = baseService.getPortInfoList();

		portAbbrList = baseService.getPort_AbbrList();

		tableList = tableService.getTableListByDate(op);

		lengthOfTask =tableList.size();

		current=0;
		
		scheduleID =0;

		logger.info("���̺� ��: "+lengthOfTask);

		processDialog = new ScheduleBuildMessageDialog(this);

		processDialog.setMessage(fileName+"("+lengthOfTask+"��)");

		processDialog.createAndUpdateUI();

		processDialog.setTask(this);


	}
	private DefaultWebSchedule(ShippersTable op) throws SQLException {
		super();
		this.op= op;
		baseService 	= DAOManager.getInstance().createBaseService();
	}


	public DefaultWebSchedule(int format_type, ShippersTable op) throws SQLException {
		this(op);
		
		logger.info("type:"+format_type);
		this.format_type = format_type;		
		init(this.format_type);

	}

	/**
	 * @param outPortIndex
	 * @param outToPortIndex
	 * @param inPortIndex
	 * @param inToPortIndex
	 * @return
	 * @throws NumberFormatException
	 */
	public HashMap<Integer, Integer> makePortArrayWebIndexMap(String outPortIndex, String outToPortIndex,String inPortIndex, String inToPortIndex) throws NumberFormatException{

		HashMap<Integer, Integer> indexlist = new HashMap<Integer, Integer>();
		StringTokenizer st = new StringTokenizer(outPortIndex,"#");

		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(outToPortIndex,"#");
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(inPortIndex,"#");
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(inToPortIndex,"#");
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
	 * @param tableData
	 * @return
	 * @throws NumberFormatException
	 */
	public HashMap<Integer, Integer> makePortArrayWebIndexMap(ShippersTable tableData) throws NumberFormatException{
		
		
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




	@Override
	public int execute() {
		logger.info(fileName+" �������� ���� ����");
		try {

			errorFw = new FileWriter(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/web_"+errorfileName+"_error.txt");
			
			if(fileName.equals("INLAND"))
			{
				inlandWebScheduleBuild();
			}
			else
			{
				normalWebScheduleBuild();
			}
			
			printWebSchedule();
			
			JOptionPane.showMessageDialog(processDialog, "�� ������ ���� �Ϸ�");
			return SchedulePrint.SUCCESS;

		} catch (Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			return SchedulePrint.FAILURE;
		}
		finally{
			processDialog.setVisible(false);
			done=true;
			if(this.processDialog!=null){
				this.processDialog.setVisible(false);
				this.processDialog.dispose();
			}
		}
	}

	/**
	 * @param portIndex
	 * @return
	 */
	private int[] makePortArraySub(String portIndex) {
		if(portIndex==null||portIndex.equals("")||portIndex.equals(" "))
			return null;

		portIndex=portIndex.trim();
		StringTokenizer st = new StringTokenizer(portIndex,"#");

		Vector<Integer> indexList = new Vector<Integer>();
		while(st.hasMoreTokens())
		{
			try
			{
				indexList.add(Integer.parseInt(st.nextToken().trim()));
			}
			catch (NumberFormatException e) 
			{
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



	/** TS ���� Ȯ��
	 * @param tableData
	 * @return
	 */
	protected boolean isTS(ShippersTable tableData)
	{
		return tableData.getTS()!=null&&tableData.getTS().equals("TS");
	}	
	/**
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
	 * 
	 * inlnad �� ������ ����
	 * @throws SQLException
	 * @throws ParseException
	 * @throws OutOfMemoryError
	 * @throws JDOMException
	 * @throws IOException
	 */
	private void inlandWebScheduleBuild() throws SQLException, ParseException, OutOfMemoryError, JDOMException, IOException {

		Iterator tableIter = tableList.iterator();

		while(tableIter.hasNext())
		{
			tableData = (ShippersTable) tableIter.next();
			
			logger.info("������ ó��:"+tableData);
			
			// �ش� ���̺� ���� �������� ��ȸ

			if(isTableDataValidation(tableData))
			{
				//errorlist.add(createError("���̺� ���� ����", tableData.getTable_id(), ""));
				continue;
			}
			message =current+"��° ���̺� ����, id:"+tableData.getTable_id();

			processDialog.setMessage(message);
			// ��¥ ���� �迭 ����
			arrayDatas 				= this.getXMLDataArray(advData.getData());
			// ���� ���� �迭 ����
			vslDatas 				= this.getXMLFullVesselArray(isTS(tableData),advData.getData());
			// �ƿ��ٿ�� ������ �ε��� �迭 ����
			a_outbound_port_index	= makePortArraySub(tableData.getOut_port());
			// �ιٿ�� ������ �ε��� �迭 ����
			a_inbound_port_index 	= makePortArraySub(tableData.getIn_port());
			// �ƿ��ٿ�� �ܱ��� �ε��� �迭 ����
			a_outbound_toport_index = makePortArraySub(tableData.getOut_to_port());
			// �ιٿ�� �ܱ��� �ε��� �迭 ����
			a_inbound_toport_index 	= makePortArraySub(tableData.getIn_to_port());
			// ������ �ε��� �迭 ����
			a_inland_port_index= makePortArraySub(tableData.getInland_indexs());

			// ���� �� ������ ����
			for(int vslIndex=0;vslIndex<vslDatas.length;vslIndex++)
			{
				try {
					vesselInfo = getVesselInfo(vslDatas[vslIndex][0]);
					// �ιٿ�� ������ ����
					makeSchedule(tableData, vslIndex,a_inbound_port_index,a_inbound_toport_index,a_inland_port_index,ScheduleEnum.INBOUND.getSymbol(),advData);

					// �ƿ��ٿ�� ������ ����
					makeSchedule(tableData, vslIndex,a_outbound_port_index,a_outbound_toport_index,a_inland_port_index,ScheduleEnum.OUTBOUND.getSymbol(),advData);


					processDialog.setMessage(message);

				} catch (VesselNullException e) {
					e.printStackTrace();
				}
				catch (PortNullException e) {
					
					logger.error("error:"+e.getFromPort()+","+e.getToPort());
				}
			}

			current++;
		}


	}
	

	/**
	 * @param table ���̺� ����
	 * @param vslIndex ���� �ε���
	 * @param ports
	 * @param outPort
	 * @param InOutBoundType
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private void makeSchedule(ShippersTable table, int vslIndex, int[]ports, int[]outPort, int[] inlnadPortIndex,String InOutBoundType,ADVData adv) throws ArrayIndexOutOfBoundsException,NullPointerException, ParseException{
		if(ports==null||outPort==null)
		{			
			return;
		}

		try{
			// ������
			portDataArray=getPortList(table);
			
			
			// �ƿ��ٿ�� ������
			int[] outboundFromPort=makePortArraySub(tableData.getOut_port(),tableData);
			
			// �ƿ��ٿ�� �ܱ���
			int[] outboundToPort=makePortArraySub(tableData.getOut_to_port(),tableData);
			
			// �ιٿ�� ������
			int[] inboundFromPort =makePortArraySub(tableData.getIn_port(),tableData);
			
			// �ιٿ�� �ܱ���
			int[] inboundToPort =makePortArraySub(tableData.getIn_to_port(),tableData);	
			
			boolean isOutBusanAndNewBusan=isBusanAndNewBusan(outboundFromPort, outboundToPort,TYPE_INBOUND);

			boolean isOutToBusanAndNewBusan=isBusanAndNewBusan(outboundFromPort, outboundToPort,TYPE_OUTBOUND);

			for(int outPortIndex=0;outPortIndex<ports.length;outPortIndex++)
			{
				//  ������ ��� �� �ε��� ��ȸ

				// �ܱ���
				for(int outToPortIndex=0;outToPortIndex<outPort.length;outToPortIndex++)
				{

					TablePort fromPort = this.getTablePort(portDataArray, ports[outPortIndex]);
					TablePort toPort = this.getTablePort(portDataArray, outPort[outToPortIndex]);


					String fromPortArray[]	= fromPort.getSubPortNameArray();
					String toPortArray[]	= toPort.getSubPortNameArray();


					for(int fromPortIndex =0;fromPortIndex<fromPortArray.length;fromPortIndex++)
					{
						for(int toPortIndex =0;toPortIndex<toPortArray.length;toPortIndex++)
						{
							try{
								setPortName(fromPortArray[fromPortIndex], toPortArray[toPortIndex]);

								// ������ ��ȸ
								for(int inlnadIndex=0;inlnadIndex<inlnadPortIndex.length;inlnadIndex++)
								{
									TablePort inlnadPorts = this.getTablePort(portDataArray, inlnadPortIndex[inlnadIndex]);
									String inlnadPortArray[]	= inlnadPorts.getSubPortNameArray();

									for(int inlnadPortNum=0;inlnadPortNum<inlnadPortArray.length;inlnadPortNum++)
									{	
										//�����, ������ ����
										if(InOutBoundType.equals(ScheduleEnum.INBOUND.getSymbol()))
										{
											if(arrayDatas[vslIndex][outPort[outPortIndex]-1].equals("-")||arrayDatas[vslIndex][ports[outToPortIndex]-1].equals("-"))
												continue;

											toPortStr =fromPortArray[fromPortIndex];// �����
											fromPortStr =toPortArray[toPortIndex];// ������

											fromDates = KSGDateUtil.getDates(arrayDatas[vslIndex][outPort[outPortIndex]-1],currentMonth,currentYear);
											toDates   = KSGDateUtil.getDates(arrayDatas[vslIndex][ports[outToPortIndex]-1],currentMonth,currentYear);

										}
										else if(InOutBoundType.equals(ScheduleEnum.OUTBOUND.getSymbol()))
										{
											// ������ ó���� �������� �н�
											if(arrayDatas[vslIndex][ports[outPortIndex]-1].equals("-")||arrayDatas[vslIndex][outPort[outToPortIndex]-1].equals("-"))
												continue;
											fromPortStr =fromPortArray[fromPortIndex];	// �����
											toPortStr=toPortArray[toPortIndex];			// ������											
											fromDates = KSGDateUtil.getDates(arrayDatas[vslIndex][ports[outPortIndex]-1],currentMonth,currentYear);
											toDates   = KSGDateUtil.getDates(arrayDatas[vslIndex][outPort[outToPortIndex]-1],currentMonth,currentYear);											
										}

										//������ ó���� �������� �н�
										if(arrayDatas[vslIndex][inlnadPortIndex[inlnadIndex]-1].equals("-"))
											continue;
										
										
										

										// ������, �������� ����
										inlnadPortStr 			= inlnadPortArray[inlnadPortNum]; 										
										inlnad_date				= arrayDatas[vslIndex][inlnadPortIndex[inlnadIndex]-1];										

										// ������ ETD, ETA ����
										int inland_MonthAndDayETD[]=KSGDateUtil.getETD(inlnad_date);
										
										int inland_MonthAndDayETA[]=KSGDateUtil.getETA(inlnad_date);
										
										int inland_year = KSGDateUtil.selectYear(currentMonth, inland_MonthAndDayETD[0], Integer.valueOf(currentYear));

										// ������ȣ ����										
										String voyage_num_str 	= vslDatas[vslIndex][1]; // ����										
										int voyage_num 			= ScheduleBuildUtil.getNumericVoyage(vslDatas[vslIndex][1]); // ����
										
										
										// outbound ������:
										if(InOutBoundType.equals(ScheduleEnum.OUTBOUND.getSymbol())&&isOutToBusanAndNewBusan)
										{	
											/* �����ϱ� ���� ����
											 * 1. �λ���, �λ������ ���ÿ� �����ϴ� ���
											 * 2. �λ� ���� �������� �ִ� ���
											 * ����: �λ���, �λ� ������ ���ÿ� �����ϴ� ��� �λ� ���� �������� ���� ��쿡�� �λ� ������ ���õ� 
											 */

											if(fromPort.equals(BUSAN)&&isOutToNewBusanPortScheduleAdd)
											{
												logger.info(InOutBoundType+" busan port skip:");
												
												logger.info("new port index:"+outBusanNewPortIndex);
												
												continue;
											}
										}
										// inbound ������
										if(InOutBoundType.equals(ScheduleEnum.INBOUND.getSymbol())&&isOutBusanAndNewBusan)
										{									
											if(toPort.equals(BUSAN)&&isOutNewBusanPortScheduleAdd)
											{
												logger.info(InOutBoundType+" busan port skip:"+vesselInfo.getVessel_abbr());
												logger.info("new port index:"+outToBusanNewPortIndex);
												continue;
											}
										}							

										// ���� ���� ����
										PortInfo  portInfo=getPortInfo(toPortStr);
										
										ScheduleData scheduledata = new ScheduleData();
										scheduledata.setInOutType(InOutBoundType); 				// in, out Ÿ��
										scheduledata.setFromPort(fromPortStr); 					// �����
										scheduledata.setDateF(fromDates[0]);					// ��� ETA
										scheduledata.setDateFBack(fromDates[1]);				// ��� ETD
										scheduledata.setPort(toPortStr);						// ������
										scheduledata.setDateT(toDates[0]);						// ���� ETA
										scheduledata.setDateTBack(toDates[1]);					// ���� ETD
										scheduledata.setVessel(vslDatas[vslIndex][0]);			// ���� ��
										scheduledata.setVoyage_num(voyage_num_str); 			// ���� ��ȣ(����)
										scheduledata.setN_voyage_num(voyage_num);				// ���� ��ȣ(����)

										scheduledata.setCompany_abbr(table.getCompany_abbr());	// Line(���� ���)										
										scheduledata.setAgent(table.getAgent());				// �ܼ־�ü��(������Ʈ ���)
										scheduledata.setInland_port(inlnadPortStr); 			// ������ �ױ���										
										scheduledata.setInland_date(inland_year+"/"+inland_MonthAndDayETA[0]+"/"+inland_MonthAndDayETA[1]);				// ��������
										scheduledata.setInland_date_back(inland_year+"/"+inland_MonthAndDayETD[0]+"/"+inland_MonthAndDayETD[1]);				// ��������										
										scheduledata.setBookPage(table.getBookPage());
										scheduledata.setVessel_type(vesselInfo.getVessel_type());// ����										
										scheduledata.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi());// ���� MMSI �ڵ�
										String vessel_compmay="";
										
										if(vessel_compmay.contains("/"))
										{
											vessel_compmay=vesselInfo.getVessel_company().substring(0,vesselInfo.getVessel_company().indexOf("/"));
										}
										else
										{
											vessel_compmay= vesselInfo.getVessel_company();
										}
										
										scheduledata.setOperator(vessel_compmay);// ��ǥ��ü

										scheduledata.setFromAreaCode(fromPortInfo.getArea_code());
										
										scheduledata.setArea_code(toPortInfo.getArea_code());
										
										addWebData(scheduledata);

									}
								}
							}

							catch(VesselDateNotMatchException e)
							{

								logger.error(table.getTable_id()+","+e.getDate());
								e.printStackTrace();
								continue;
							}
							// SKIP ��¥ ����
							catch(NotSupportedDateTypeException e)
							{
								e.printStackTrace();
								continue;
							}
							catch(Exception e)
							{
								e.printStackTrace();
								throw new NotSupportedDateTypeException("", 0);
							}
						}
					}

					/******************
					 *���� �߻� �� �� ����				*
					 **/				

				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new PortIndexNotMatchException(table);
		}
	}


	public String[][] getXMLDataArray(String data) throws JDOMException, IOException, OutOfMemoryError
	{
		// error Java heap space

		stream =new ByteArrayInputStream(data.getBytes());
		
		document= builder.build(stream);
		
		Element root = document.getRootElement();
		
		List vessel_list=root.getChildren("vessel");
		
		returndata = new String[vessel_list.size()][];
		
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
	
	String returndata[][];
	
	public String[][] getXMLFullVesselArray(boolean ts,String data) throws JDOMException, IOException
	{	
		stream =new ByteArrayInputStream(data.getBytes());
		document= builder.build(stream);
		root = document.getRootElement();
		List vessel_list=root.getChildren("vessel");
		returndata = new String[vessel_list.size()][];
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

	

	private boolean done;
	
	ShippersTable tableData;
	
	private Element root;

	/**
	 * @throws SQLException
	 * @throws JDOMException
	 * @throws IOException
	 * @throws OutOfMemoryError
	 * @throws ParseException
	 */
	private void normalWebScheduleBuild()
			throws SQLException, JDOMException, IOException, OutOfMemoryError,
			ParseException {

		Iterator<ShippersTable> iter = tableList.iterator();
		
		while(iter.hasNext())
		{
			tableData = iter.next();

			long startTime=System.currentTimeMillis();

			message =current+"��° ���̺� ����, id:"+tableData.getTable_id();

			processDialog.setMessage(message);

			logger.debug(message);


			/*
			 * ������ ��¥ Ȯ��
			 */
			if(isTableDataValidation(tableData))
			{
				continue;
			}

			arrayDatas = this.getXMLDataArray(advData.getData());

			vslDatas = this.getXMLFullVesselArray(tableData.getTS()!=null&&tableData.getTS().equals("TS"),advData.getData());

			HashMap<Integer, Integer> map=makePortArrayWebIndexMap(tableData);

			Set<Integer> keySet =map.keySet();
			
			a_outport = new int[keySet.size()];

			Iterator<Integer> keyIter = keySet.iterator();

			for(int i=0;keyIter.hasNext();i++)
			{
				a_outport[i]=keyIter.next();
			}

			Arrays.sort(a_outport);

			a_outtoport = a_outport;//?

			if(a_outport!=null)
			{
				size_outport=a_outport.length;
				size_outtoport=a_outtoport.length;
			}		

			for(int vslIndex=0;vslIndex<vslDatas.length;vslIndex++)
			{
				try 
				{
					makeWebSchedule(tableData, vslIndex,a_outport,ScheduleEnum.OUTBOUND.getSymbol(),advData);
				}
				catch (PortIndexNotMatchException e) 
				{
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e.table.getCompany_abbr()+"������ "+e.table.getPage()+" ������,"+e.table.getTable_index()+
							"�� ���̺��� ������ ���� ������ ������ ������ϴ�.\n\n�ױ�����, �ױ� �ε��� ����,��¥ ���ĸ� Ȯ�� �Ͻʽÿ�.\n\n������ ������ ���� �մϴ�.");
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e) {
					errorFw.write("�迭 �ε��� ���� :"+e.getMessage()+","+tableData.getTable_id()+", ������:"+tableData.getPage()+","+tableData.getTable_index());
					logger.error("�迭 �ε��� ���� :"+e.getMessage()+","+tableData.getTable_id()+", ������:"+tableData.getPage()+","+tableData.getTable_index()+"\n");						

				}catch (PortNullException portError)
				{
					errorFw.write(tableData.getTable_id()+"\t"+portError.getMessage()+",\t"+portError.getFromPort()+",\t"+portError.getToPort()+"\n");
					
					
					logger.error("port null error table id=>"+tableData.getTable_id()+": "+portError.getMessage()+","+portError.getFromPort()+","+portError.getToPort()+"\n");
					portError.printStackTrace();
				} catch (NullPointerException e) {
					 
					e.printStackTrace();
				} catch (VesselNullException e) 
				{
					logger.error("vessel null:"+tableData.getTable_id());
					//e.printStackTrace();
				}
				
			}
			long endtime = System.currentTimeMillis();
			
			long processTime =endtime-startTime;
			
			totalTime +=processTime;
			
			current++;
			
			message ="���̺� �۾� �Ϸ�:"+processTime+", avg:"+(totalTime/lengthOfTask);
			
			processDialog.setMessage(message);
			
			logger.debug(message);
			
			arrayDatas = null;
			
			vslDatas = null;
		}
		
		errorFw.flush();
		
		errorFw.close();
		
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


		for(int i=0;i<vesselList.size();i++)
		{
			ScheduleData data = vesselList.get(i);
			
			fw.write(data.getArea_code()+"\t"+scheduleID+"\t"+webScheduleFormat.toWebScheduleString(data)+"\n");
			
			scheduleID++;
		}
		
		fw.flush();
		
		fw.close();
		
		logger.info(areaName+"���� ���� ��� ����");
	}

	private TablePort getTablePort(Vector<TablePort> array,int index)
	{
		TablePort port1 = new TablePort();
		for(int i=0;i<array.size();i++)
		{
			TablePort port=(TablePort) array.get(i);
			if(port.getPort_index()==index)
				port1.addSubPort(port);
		}
		return port1;

	}
	/**
	 * @param data
	 */
	private void addWebData(ScheduleData data)
	{
		String key =data.getFromAreaCode();
		if(areaKeyMap.containsKey(key))
		{	
			Vector<ScheduleData> area=areaKeyMap.get(key);
			area.add(data);
		}
	}
	private void setPortName(String fromPort, String toPort) throws SQLException, PortNullException
	{
		if(fromPort== null||toPort==null)
			throw new PortNullException(fromPort, toPort);

		fromPortInfo = getPortAreaCode(fromPort);
		
		toPortInfo = getPortAreaCode(toPort);
		

		if(fromPortInfo==null||toPortInfo==null)
			throw new PortNullException(fromPort, toPort);
	}

	/**
	 * @param field
	 * @param table DB�� ����ǳ� �ִ� ���̺� ����
	 * @return
	 */
	private int[] makePortArraySub(String field,ShippersTable table) {
		if(field==null||field.equals("")||field.equals(" "))
		{
			//logger.error("table id:"+table.getTable_id()+" field:"+field);
			return null;
		}

		
		
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
				
				//errorlist.add(createError("�ε��� ����", table.getTable_id(), field));

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
	private void makeWebSchedule(ShippersTable table, int vslIndex, int portList[],String InOutBoundType,ADVData adv)
			throws ArrayIndexOutOfBoundsException,NullPointerException, ParseException, SQLException, PortNullException, VesselNullException
	{

		/*
		 * 
		 */


		table_id = table.getTable_id();
		
		company_abbr = table.getCompany_abbr();
		
		agent = table.getAgent();
		
		String gubun= table.getGubun();
		
		String date_isusse = table.getDate_isusse();
		
		String common_shipping = table.getCommon_shipping();
		
		dateF = null;
		
		dateT = null;
		
		area_code = null;
		
		area_name = null;
		
		vesselName = vslDatas[vslIndex][0];

		voyageNum = vslDatas[vslIndex][1];
		
		fromPort = null;
		
		toPort = null;
		
		dates = null;

		// ������
		portDataArray=getPortList(table);

		logger.debug("make web schedule=>table id:"+table.getTable_id()+", vessel:"+vesselName);
		
		if(vesselName==null)
			return;
			
		vesselInfo = ScheduleManager.getInstance().searchVessel(vesselName);
		
		// ������� �ʴ� �����̸� �����ٿ��� ����
		if(vesselInfo.getVessel_use()==Vessel.NON_USE)
			return;
		
		// �ƿ��ٿ�� ������
		int[] outboundFromPort=makePortArraySub(tableData.getOut_port(),tableData);
		
		// �ƿ��ٿ�� �ܱ���
		int[] outboundToPort=makePortArraySub(tableData.getOut_to_port(),tableData);
		
		// �ιٿ�� ������
		int[] inboundFromPort =makePortArraySub(tableData.getIn_port(),tableData);
		
		// �ιٿ�� �ܱ���
		int[] inboundToPort =makePortArraySub(tableData.getIn_to_port(),tableData);	


		boolean isOutBusanAndNewBusan=isBusanAndNewBusan(outboundFromPort, outboundToPort,TYPE_INBOUND);

		boolean isOutToBusanAndNewBusan=isBusanAndNewBusan(outboundFromPort, outboundToPort,TYPE_OUTBOUND);
		
		if(isOutBusanAndNewBusan&&isOutToBusanAndNewBusan)		
		logger.info("�λ��� �� ���:"+isOutBusanAndNewBusan+","+isOutToBusanAndNewBusan);
		


		portDataArray=getPortList(table);

		for(int i=0;i<portList.length;i++)
		{
			int outPortIndex= portList[i];
			
			for(int j=i+1;j<portList.length;j++)
			{
				int outToPortIndex = portList[j];

				TablePort _outport = this.getTablePort(portDataArray, outPortIndex);

				TablePort _outtoport = this.getTablePort(portDataArray, outToPortIndex);

				String subFromPortArray[]=_outport.getSubPortNameArray();

				String subToPortArray[]=_outtoport.getSubPortNameArray();

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



				for(int z =0;z<subFromPortArray.length;z++)
				{
					for(int c =0;c<subToPortArray.length;c++)
					{
						try{

							// outbound ������:
							if(InOutBoundType.equals(ScheduleEnum.OUTBOUND.getSymbol())&&isOutToBusanAndNewBusan)
							{	
								/* �����ϱ� ���� ����
								 * 1. �λ���, �λ������ ���ÿ� �����ϴ� ���
								 * 2. �λ� ���� �������� �ִ� ���
								 * ����: �λ���, �λ� ������ ���ÿ� �����ϴ� ��� �λ� ���� �������� ���� ��쿡�� �λ� ������ ���õ� 
								 */

								if(fromPort.equals(BUSAN)&&isOutToNewBusanPortScheduleAdd)
								{
									logger.info(InOutBoundType+" busan port skip:");
									
									logger.info("new port index:"+outBusanNewPortIndex);
									
									continue;
								}
							}
							// inbound ������
							if(InOutBoundType.equals(ScheduleEnum.INBOUND.getSymbol())&&isOutBusanAndNewBusan)
							{									
								if(toPort.equals(BUSAN)&&isOutNewBusanPortScheduleAdd)
								{
									logger.info(InOutBoundType+" busan port skip:"+vesselInfo.getVessel_abbr());
									logger.info("new port index:"+outToBusanNewPortIndex);
									continue;
								}
							}

							setPortName(subFromPortArray[z], subToPortArray[c]);

							fromDates = KSGDateUtil.getDates(arrayDatas[vslIndex][outPortIndex-1],currentMonth,currentYear);

							toDates   = KSGDateUtil.getDates(arrayDatas[vslIndex][outToPortIndex-1],currentMonth,currentYear);

							ScheduleData webData=webScheduleFormat.createWebScheduleData(table, "O", vslIndex, vesselInfo, outPortIndex, outToPortIndex, subFromPortArray[z], subToPortArray[c]);

							webData.setFromAreaCode(fromPortInfo.getArea_code());

							webData.setArea_code(toPortInfo.getArea_code());

							addWebData(webData);

						}catch(VesselDateNotMatchException e)
						{
							//logger.error(e.getMessage()+","+e.getDate());
						}

					}
				}
			}	
		}

	}
	public String[] getToDates() {
		return toDates;
	}
	public void setToDates(String[] toDates) {
		this.toDates = toDates;
	}
	/**�����̸����� ���� ���� ��ȸ
	 * @param vessel_name
	 * @return
	 * @throws SQLException
	 * @throws VesselNameNullException
	 */
	private Vessel getVesselInfo(String vessel_name) throws SQLException,VesselNullException {
		if(vessel_name==null)
			throw new VesselNullException(vessel_name);
		Vessel op = new Vessel();
		op.setVessel_name(vessel_name);
		Vessel search = baseService.getVesselInfo(op);

		if(search==null)
			throw new VesselNullException(vessel_name);
		return search;
	}



	boolean isOutToNewBusanPortScheduleAdd=false;
	boolean isOutNewBusanPortScheduleAdd=false;
	int outBusanNewPortIndex;
	int outToBusanNewPortIndex;
	boolean isExitOutOldPort=false;
	boolean isExitOutNewPort=false;
	boolean isExitOutToOldPort=false;
	boolean isExitOutToNewPort=false;

	private String table_id;

	private String company_abbr;

	private String agent;

	private String dateF;

	private String dateT;

	private String area_code;

	private String area_name;

	private String vesselName;

	private String voyageNum;

	private String fromPort;

	private String toPort;

	private String dates[];


	/**
	 * @��¥ 2016-2-26
	 * @���� �����ٿ� �λ��װ� �λ������ ���ÿ� �����ϴ��� �Ǵ�, �λ��� : BUSAN, �λ���� : BUSAN NEW
	 * @param fromPorts
	 * @param toPort
	 * @return
	 * @throws SQLException
	 * 
	 */
	private boolean isBusanAndNewBusan(int[] fromPorts, int[] toPort, int type)
			throws PortNullException {

		isExitOutOldPort=false;
		isExitOutNewPort=false;
		isExitOutToOldPort=false;
		isExitOutToNewPort=false;
		
		if(fromPorts==null)
			return false;
		// �ױ�Ȯ��
		for(int fromPortCount=0;fromPortCount<fromPorts.length;fromPortCount++)
		{
			int outPortIndex =fromPorts[fromPortCount];
			for(int toPortCount=0;toPortCount<toPort.length;toPortCount++)
			{
				int outToPortIndex = toPort[toPortCount];
				TablePort _outport = this.getTablePort(portDataArray, outPortIndex);
				TablePort _outtoport = this.getTablePort(portDataArray, outToPortIndex);


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
	public String[][] getVslDatas() {
		return vslDatas;
	}
	public String[][] getArrayDatas() {
		return arrayDatas;
	}
	@Override
	public void init() {
		
	}
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void writeFile(ArrayList<String> printList) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
