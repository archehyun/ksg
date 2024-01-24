package com.ksg.schedule.logic.print;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dtp.api.schedule.joint.print.AbstractSchedulePrint;
import com.dtp.api.schedule.joint.print.outbound.OutboundScheduleRule;
import com.ksg.common.exception.PortNullException;
import com.ksg.common.exception.VesselNullException;
import com.ksg.common.util.SortUtil;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.ScheduleBuild;
import com.ksg.schedule.logic.joint.outbound.FromPortGroup;
import com.ksg.schedule.logic.joint.outbound.PrintItem;
import com.ksg.schedule.logic.joint.outbound.ToPortGroup;
import com.ksg.schedule.logic.joint.outbound.VesselGroup;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.impl.ScheduleServiceImpl;
/**
 * 
 * @���� �ƿ��ٿ�� ���� ���
 * @author archehyun
 *
 */
/**

  * @FileName : OutboundScheduleJointV2.java

  * @Date : 2021. 5. 3. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� :

  */
public class OutboundSchedulePrintV2 extends AbstractSchedulePrint{

	protected ScheduleSubService scheduleService	= new ScheduleServiceImpl();
	
	private ScheduleData op;

	private ScheduleData data;

	private ArrayList<PrintItem> printList;

	private List<String> outbounSchedulePortList;
	
	protected OutboundScheduleRule outboundSchedule;

	protected static final String PORT_NAME 	= "outbound_port.txt";

	protected static final String FILE_NAME 	= "outbound_new.txt";

	protected static final String ERROR_NAME 	= "outbound_error.txt";	

	protected String errorFileName;

	protected String portName;

	protected String[] fromPort;

	protected  Map<String, PortInfo> portMap;

	protected  Map<String, Vessel> vesselMap;
	
	protected String 	BOLD_TAG_F="",
			BOLD_TAG_B="",
			TAG_VERSION0="",
			TAG_VERSION1="",
			TAG_VERSION2="",
			TAG_VERSION3="",
			TAG_VERSION4="",
			TAG_VERSION5="";

	public OutboundSchedulePrintV2() throws Exception {

		super();

		logger.info("outbound build");

		message = "Outbound ������...";
		


		logger.info("outbound ������ ���� �� �ʱ�ȭ");

	}

	/**
	 * @author archehyun
	 *
	 */
	class ScheduleGroup extends HashMap<String, ToPortGroup>
	{
		private static final long serialVersionUID = 1L;

		public void add(ScheduleData data) throws SQLException, VesselNullException
		{
			//try {
			if(this.containsKey(data.getPort()))
			{
				ToPortGroup group = this.get(data.getPort());
				// Ű : �����-�����-���ڸ�-������

				group.addSchedule(data);


			}else
			{
				// �ű� �׷� ����
				// Ű : �����-�����-���ڸ�-������
				this.put(data.getPort(), new ToPortGroup(data));
			}
			
		}
	}



	/**����ױ� ����
	 * @param outboundFromPortList
	 * @return
	 */
	private String[] arrangeFromPort(String[] outboundFromPortList) 
	{	
		logger.debug("from-port arrange");

		List<String> arragedFromPort = new LinkedList<String>();
		for(int i=0;i<fromPort.length;i++)
		{
			arragedFromPort.add(fromPort[i]);
		}

		List<String> fromPorts = new LinkedList<String>();
		for(int i=0;i<outboundFromPortList.length;i++)
		{
			fromPorts.add(outboundFromPortList[i]);
		}

		for(int i=0;i<fromPort.length;i++)
		{
			// ������ �׸��� ������
			if(!fromPorts.contains(fromPort[i]))
			{
				//���ĵ� �ױ� ��� ���� ����
				arragedFromPort.remove(fromPort[i]);
			}
		}
		String[] newArray = new String[arragedFromPort.size()];
		for(int i=0;i<newArray.length;i++)
		{
			newArray[i] = arragedFromPort.get(i);
		}
		return newArray;
	}

	private boolean checkFromPort(String toPort)
	{
		for(int i=0;i<fromPort.length;i++)
		{
			if(fromPort[i].equals(toPort))
				return true;
		}
		return false;
	}
	@Override
	public int execute() throws IOException, ScheduleJointError {

		long startTime = System.currentTimeMillis();


		message = "�ƿ��ٿ�� ������ �׷�ȭ...";


		/*���� ����
		 * 1. ������
		 * 2. �����
		 * 3. �����
		 * 4. ������ 
		 */

		try{

			//�����ױ��� ��� ��ȸ
			outbounSchedulePortList = scheduleService.getOutboundPortList();

			lengthOfTask = outbounSchedulePortList.size();

			Iterator<String> toPortIter = outbounSchedulePortList.iterator();

			fw.write(buildVersionXTG());

			int tagIndex=0;// �±� ������ ���� �ε���

			String toPort=null;

			//�����ױ��� �������� ������ ����
			while(toPortIter.hasNext())
			{
				try{
					toPort = toPortIter.next();
					logger.info("������: "+toPort);

					if(checkFromPort(toPort)) continue;

					portfw.write(toPort+"\n");
					
					
					op = new ScheduleData();
					op.setPort(toPort);
					op.setInOutType("O");

					// �����ױ��� �������� ������ ��ȸ
					List<ScheduleData> outboundScheduleListByToPort =scheduleService.getScheduleList(op);

					// �����ױ��� �������� �׷� ����
					ToPortGroup toPortgroup = new ToPortGroup(toPort);
					
					Iterator<ScheduleData> scheduleList = outboundScheduleListByToPort.iterator();
					
					while(scheduleList.hasNext())
					{
						try{
							data =scheduleList.next();
							toPortgroup.addSchedule(data);
						}catch(VesselNullException e)
						{
							logger.error("vessel null error table-id:"+data.getTable_id()+",vessel-name:"+data.getVessel());
							errorfw.write("vessel null error table-id:"+data.getTable_id()+",vessel-name:"+data.getVessel()+"\n");
							continue;
						}
					}

					fw.write(buildToPortXTG(tagIndex, toPortgroup.getToPort(), toPortgroup.getPort_nationality()));

					tagIndex++;

					// ������ ���� ����� ��� ����
					String[] fromPortArray = arrangeFromPort(toPortgroup.keySet().toArray(new String[toPortgroup.keySet().size()]));

					/*
					 * ����� �������� ��� ����
					 */

					for(int i=0;i<fromPortArray.length;i++)
					{
						FromPortGroup fromPortGroup =toPortgroup.get(fromPortArray[i]);	

						String[] vesselArray = fromPortGroup.keySet().toArray(new String[fromPortGroup.keySet().size()]);

						//���⼭ �����ϴ� ����?
						SortUtil.bubbleSort(vesselArray);

						VesselGroup[] vesselArrays =new VesselGroup[vesselArray.length];

						for(int y=0;y<vesselArray.length;y++)
						{
							vesselArrays[y] =fromPortGroup.get(vesselArray[y]);
						}

						printList.clear();

						/*
						 * vesselgroup���� �����輱 ���� �� ������ ���� ��� ����
						 */

						for(int y=0;y<vesselArrays.length;y++)
						{
							ArrayList<PrintItem> li = vesselArrays[y].getJointedVesselList();
							
							li.stream().forEach(o -> printList.add(o));
							
						}
						
						// �������� ������ ����� ǥ��
						if(printList.size()>0)
							
						fw.write(buildFromXTG(i, fromPortGroup.getFromPortName()));

						// ����Ϸ� ����
						PrintItem pr[]= new PrintItem[printList.size()];

						pr=printList.toArray(pr);

						Arrays.sort(pr);

						for(int pr_i=0;pr_i<pr.length;pr_i++)
						{
							fw.write(pr[pr_i].toString());
						}
					}
				}catch(PortNullException e)
				{
					logger.error("port null:"+toPort);
					errorfw.write("port null:"+toPort);
				}
				current++;

			}

			logger.info("outbound ������ ���� ����("+(System.currentTimeMillis()-startTime)+")");

			return ScheduleBuild.SUCCESS;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			throw new ScheduleJointError(e,data);
			
		}
		finally
		{
			// ���� �ݱ�
			fw.close();
			errorfw.close();
			portfw.close();
		}
	}

	/**XTG ���
	 * @return
	 */
	private String buildVersionXTG() {
		String buffer = TAG_VERSION0+"\r\n"+
				TAG_VERSION2+"\r\n"+
				TAG_VERSION3+"\r\n"+
				TAG_VERSION4+"\r\n"+
				TAG_VERSION5;
		return buffer;
	}

	/**
	 * @param i
	 * @param portName
	 * @param portNationality
	 * @return
	 */
	private String buildToPortXTG(int i, String portName,String portNationality) {

		if(isApplyTag)
		{
			return (i!=0?" \r\n<ct:><cs:><ct:Bold><cs:8.000000>":"<ct:><cs:><ct:Bold><cs:8.000000>")

					+portName +" , "+portNationality+" ";	
		}
		else
		{
			return (i!=0?" \r\n":"")+portName +" , "+portNationality+" ";	
		}

	}
	private String buildFromXTG(int j, String fromPort) {
		if(isApplyTag)
		{
			return (j==0?" \r\n \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ":" \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ")+fromPort+" -\r\n";
		}
		else
		{
			return (j==0?" \r\n \r\n- ":" \r\n- ")+fromPort+" -\r\n";
		}
	}

	@Override
	public void writeFile(ArrayList<String> printList) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() throws Exception {
		fileName = fileLocation+"/"+FILE_NAME;

		errorFileName = fileLocation+"/"+ERROR_NAME;

		portName = fileLocation+"/"+PORT_NAME;

		fw = new FileWriter(fileName);

		errorfw = new FileWriter(errorFileName);

		portfw = new FileWriter(portName);
		
		printList = new ArrayList<PrintItem>();
		
	}

}