package com.ksg.schedule.logic.joint;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ksg.common.util.SortUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.PortNullException;
import com.ksg.schedule.logic.ScheduleBuild;
import com.ksg.schedule.logic.VesselNullException;
import com.ksg.schedule.logic.joint.outbound.FromPortGroup;
import com.ksg.schedule.logic.joint.outbound.PrintItem;
import com.ksg.schedule.logic.joint.outbound.ToPortGroup;
import com.ksg.schedule.logic.joint.outbound.VesselGroup;

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
public class OutboundScheduleJointV2 extends DefaultScheduleJoint{

	private static final String PORT_NAME = "outbound_port.txt";

	private static final String FILE_NAME = "outbound_new.txt";

	private static final String ERROR_NAME = "outbound_error.txt";	

	private ScheduleData op;

	private ScheduleData data;

	private ArrayList<PrintItem> printList;

	private String 	BOLD_TAG_F="",
			BOLD_TAG_B="",
			TAG_VERSION0="",
			TAG_VERSION1="",
			TAG_VERSION2="",
			TAG_VERSION3="",
			TAG_VERSION4="",
			TAG_VERSION5="";

	private boolean isApplyTag=true;// �±� ���� ����

	private String errorFileName;

	private String portName;

	private List<String> outbounSchedulePortList;

	private FileWriter fw;

	private FileWriter errorfw;

	private FileWriter portfw;

	

	public OutboundScheduleJointV2() throws SQLException, IOException {

		super();

		logger.info("outbound build");

		fileName = fileLocation+"/"+FILE_NAME;

		errorFileName = fileLocation+"/"+ERROR_NAME;

		portName = fileLocation+"/"+PORT_NAME;

		fw = new FileWriter(fileName);

		errorfw = new FileWriter(errorFileName);

		portfw = new FileWriter(portName);

		printList = new ArrayList<PrintItem>();

		message = "Outbound ������...";

		logger.info("outbound ������ ���� �� �ʱ�ȭ:"+lengthOfTask);

	}

	public void initTag() {
		logger.info("�±����� �ʱ�ȭ");


		if(isApplyTag)
		{
			BOLD_TAG_B="<ct:>";

			BOLD_TAG_F="<ct:Bold Condensed>";

			TAG_VERSION0="<KSC5601-WIN>";
			
			TAG_VERSION1="";

			TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<����:COLOR:CMYK:Process:0,0,0,1>>";

			TAG_VERSION3="<dps:����=<Nextstyle:����><cc:����><cs:8.000000><clig:0><cbs:-0.000000><phll:0><pli:53.858291><pfli:-53.858292><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:����><prat:100.000000><prbc:����><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";		

			TAG_VERSION4="<dps:Body Text=<BasedOn:����><Nextstyle:Body Text><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";

			TAG_VERSION5="<pstyle:Body Text><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cs:8.000000><cl:5.479995><cf:Helvetica LT Std><ct:Roman>\r\n";			
		}
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

			//��â�ױ��� �������� ������ ����
			while(toPortIter.hasNext())
			{
				try{
					toPort = toPortIter.next();
					logger.info("");
					logger.info("������: "+toPort);

					if(checkFromPort(toPort))
						continue;

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

						fw.write(buildFromXTG(i, fromPortGroup.getFromPortName()));

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
							ArrayList<PrintItem> li = vesselArrays[y].getVesselList();

							Iterator<PrintItem> iterator = li.iterator();
							while(iterator.hasNext())
							{
								printList.add(iterator.next());
							}
						}

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
}