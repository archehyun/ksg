package com.ksg.commands.schedule.create;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.dao.impl.ScheduleService;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Port;
import com.ksg.domain.Vessel;
import com.ksg.schedule.build.PortIndexNotMatchException;
import com.ksg.schedule.build.ScheduleManager;
import com.ksg.schedule.build.VesselNullException;

/**�η��� ������ ����
 * @author archehyun
 *
 */
public class CreateInlandScheduleCommand extends CreateScheduelCommand{

	ScheduleManager scheduleManager = ScheduleManager.getInstance();
	private String[][] arrayDatas; 	// ��¥ ���� �迭
	private String[][] vslDatas;	// ���� ���� �迭
	private int[] a_outbound_port_index; 		// 
	private int[] a_inbound_port_index; 		//
	private int[] a_outbound_toport_index; 		//
	private int[] a_inbound_toport_index; 		//
	private int[] a_inland_port_index; 		//
	private Vector portDataArray;
	private String scheduleDates[];
	private String scheduleDates2[];
	private String toPortStr;
	private String fromPortStr;
	private String inlnadPortStr;
	private String inlnad_date;
	private Vessel vesselInfo;
	private ShippersTable tableData;
	public CreateInlandScheduleCommand(ShippersTable searchOption) throws SQLException {
		super();
		this.searchOption = searchOption;
	}

	
	public int execute() {
		try {
			logger.debug("search option:"+searchOption);
			List table_list = this.getTableListByOption();
			logger.debug("table Size:"+table_list.size());
			Iterator tableIter = table_list.iterator();
			while(tableIter.hasNext())
			{
				tableData = (ShippersTable) tableIter.next();
				logger.debug("������ ó��:"+tableData);
				// �ش� ���̺� ���� �������� ��ȸ

				if(isTableDataValidation(tableData))
				{
					errorlist.add(createError("���̺� ���� ����", tableData.getTable_id(), ""));
					continue;
				}

				// ��¥ ���� �迭 ����
				arrayDatas 				= advData.getDataArray();

				// ���� ���� �迭 ����
				vslDatas 				= advData.getFullVesselArray(isTS(tableData));

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

				// ������ ����
				for(int vslIndex=0;vslIndex<vslDatas.length;vslIndex++)
				{					
					vesselInfo = scheduleManager.searchVessel(vslDatas[vslIndex][0]);

					// �ιٿ�� ������ ����
					makeSchedule(tableData, vslIndex,a_inbound_port_index,a_inbound_toport_index,a_inland_port_index,ScheduleService.INBOUND,advData);

					// �ƿ��ٿ�� ������ ����
					makeSchedule(tableData, vslIndex,a_outbound_port_index,a_outbound_toport_index,a_inland_port_index,ScheduleService.OUTBOUND,advData);
				}
			}
			JOptionPane.showMessageDialog(null, "������ ���� �Ϸ�");

		}
		catch (VesselNullException e) {
			JOptionPane.showMessageDialog(null, "������ ���� ����:���ڸ��� �����ϴ�.���̺� id:"+tableData.getTable_id()+", ���ڸ�:"+e.getVesselName() );
			e.printStackTrace();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "������ ���� ����");
			e.printStackTrace();
		}
		
		return 0;
	}
	/**
	 * @param array
	 * @param index
	 * @return
	 */
	private Table_Port getPort(Vector array,int index)
	{
		Table_Port port1 = new Table_Port();
		for(int i=0;i<array.size();i++)
		{
			Table_Port port=(Table_Port) array.get(i);
			// ��Ʈ �ε����� ������
			if(port.getPort_index()==index)
				port1.addSubPort(port);
		}
		return port1;

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
		logger.debug("vslIndex:"+vslIndex);

		try{
			// ������
			portDataArray=getPortList(table);


			for(int outPortIndex=0;outPortIndex<ports.length;outPortIndex++)
			{
				logger.debug("outPortIndex:"+outPortIndex);
				//  ������ ��� �� �ε��� ��ȸ

				// �ܱ���
				for(int outToPortIndex=0;outToPortIndex<outPort.length;outToPortIndex++)
				{

					Table_Port fromPort = this.getPort(portDataArray, ports[outPortIndex]);
					Table_Port toPort = this.getPort(portDataArray, outPort[outToPortIndex]);


					String fromPortArray[]	= fromPort.getPortArray();
					String toPortArray[]	= toPort.getPortArray();


					for(int fromPortIndex =0;fromPortIndex<fromPortArray.length;fromPortIndex++)
					{
						for(int toPortIndex =0;toPortIndex<toPortArray.length;toPortIndex++)
						{
							try{


								// ������ ��ȸ
								for(int inlnadIndex=0;inlnadIndex<inlnadPortIndex.length;inlnadIndex++)
								{
									Table_Port inlnadPorts = this.getPort(portDataArray, inlnadPortIndex[inlnadIndex]);
									String inlnadPortArray[]	= inlnadPorts.getPortArray();

									for(int inlnadPortNum=0;inlnadPortNum<inlnadPortArray.length;inlnadPortNum++)
									{	
										//�����, ������ ����
										if(InOutBoundType.equals(ScheduleService.INBOUND))
										{
											if(arrayDatas[vslIndex][outPort[outPortIndex]-1].equals("-")||arrayDatas[vslIndex][ports[outToPortIndex]-1].equals("-"))
												continue;

											toPortStr =fromPortArray[fromPortIndex];// �����
											fromPortStr =toPortArray[toPortIndex];// ������											
											scheduleDates = adjestDateYear(arrayDatas[vslIndex][outPort[outPortIndex]-1],arrayDatas[vslIndex][ports[outToPortIndex]-1],InOutBoundType);
											scheduleDates = adjestDateYear2(arrayDatas[vslIndex][outPort[outPortIndex]-1],arrayDatas[vslIndex][ports[outToPortIndex]-1],InOutBoundType);
										}
										else if(InOutBoundType.equals(ScheduleService.OUTBOUND))
										{
											//											������ ó���� �������� �н�
											if(arrayDatas[vslIndex][ports[outPortIndex]-1].equals("-")||arrayDatas[vslIndex][outPort[outToPortIndex]-1].equals("-"))
												continue;
											fromPortStr =fromPortArray[fromPortIndex];	// �����
											toPortStr=toPortArray[toPortIndex];			// ������											
											scheduleDates =adjestDateYear(arrayDatas[vslIndex][ports[outPortIndex]-1],arrayDatas[vslIndex][outPort[outToPortIndex]-1],InOutBoundType);
										}

										//������ ó���� �������� �н�
										if(arrayDatas[vslIndex][inlnadPortIndex[inlnadIndex]-1].equals("-"))
											continue;
										// ������ ����
										inlnadPortStr 			= inlnadPortArray[inlnadPortNum];
										// ���� ���� ����
										inlnad_date				= arrayDatas[vslIndex][inlnadPortIndex[inlnadIndex]-1];
										int inland_MonthAndDay[]=getETD(inlnad_date);
										int inland_year = selectYear(currentMonth, inland_MonthAndDay[0], Integer.valueOf(currentYear));

										// ������ȣ(����)
										String voyage_num_str 	= vslDatas[vslIndex][1];
										// ������ȣ(����)
										int voyage_num 			= getNumericVoyage(vslDatas[vslIndex][1]);
										// ���� ���� ����
										PortInfo  portInfo=getPortInfo(toPortStr);

										ScheduleData scheduledata = new ScheduleData();
										scheduledata.setTable_id(table.getTable_id());  		// ���̺� ���̵�
										scheduledata.setGubun(table.getGubun());				// ����
										scheduledata.setVessel(vslDatas[vslIndex][0]);			// ���� ��
										scheduledata.setVessel_type(vesselInfo.getVessel_type());
										scheduledata.setCompany_abbr(table.getCompany_abbr());	// ���� ���
										scheduledata.setVoyage_num(voyage_num_str); 			// ���� ��ȣ(����)
										scheduledata.setN_voyage_num(voyage_num);				// ���� ��ȣ(����)
										scheduledata.setInOutType(InOutBoundType); 				// in, out Ÿ��			 							
										scheduledata.setCommon_shipping("");					// �����輱 ����
										scheduledata.setDate_issue(table.getDate_isusse());		// ������ ���� ��
										scheduledata.setAgent(table.getAgent());				// ������Ʈ ����									
										scheduledata.setArea_code(portInfo.getArea_code()); 	// ���� �ڵ�
										scheduledata.setArea_name(portInfo.getPort_area());		// ���� �̸�										
										scheduledata.setFromPort(fromPortStr); 					// �����
										scheduledata.setInland_port(inlnadPortStr); 			// ������ �ױ���
										scheduledata.setPort(toPortStr);						// ������
										scheduledata.setInland_date(inland_year+"/"+inland_MonthAndDay[0]+"/"+inland_MonthAndDay[1]);				// ��������
										scheduledata.setDateF(scheduleDates[0]); 				// �����
										scheduledata.setDateT(scheduleDates[1]); 				// ������
										
										//scheduledata.setDateFBack(scheduleDates2[0]);
										//scheduledata.setDateTBack(scheduleDates2[1]);
										try
										{
											logger.info("insert Schedule:"+scheduledata.toInlandScheduleString());
											scheduleService.insertInlandScheduleData(scheduledata);// DB �� ����

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
								}
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
				logger.debug("MakeSchedule end");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new PortIndexNotMatchException(table);
		}
	}


	/**
	 * @param table
	 * @return
	 */
	private Vector getPortList(ShippersTable table) 
	{
		portDataArray  = new Vector();
		try {

			Table_Port tablePort = new Table_Port();
			tablePort.setTable_id(table.getTable_id());
			tablePort.setPort_type(Table_Port.TYPE_PARENT);

			List li=getTablePortList(tablePort);

			for(int i=0;i<li.size();i++)
			{
				portDataArray.add((Table_Port) li.get(i));
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return portDataArray;
	}	
	/**
	 * @param field
	 * @param table
	 * @return
	 */
	private int[] makePortArraySub(String field) {
		if(field==null||field.equals("")||field.equals(" "))
			return null;

		field=field.trim();
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
				/*logger.error("number foramt error:"+field+",id:"+table.getTable_id());
				errorlist.add(createError("�ε��� ����", table.getTable_id(), field));*/

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
	private PortInfo getPortInfo(String portName) throws SQLException
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
				PortInfo temp = new PortInfo();
				temp.setArea_code("0");
				return temp;
			}
		}
	}
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


}
