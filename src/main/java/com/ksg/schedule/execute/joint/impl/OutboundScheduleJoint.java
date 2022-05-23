package com.ksg.schedule.execute.joint.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.schedule.execute.joint.JointSchedule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OutboundScheduleJoint extends JointSchedule{

	

	public OutboundScheduleJoint(String date_isusse)	
	{
		super(date_isusse);		
	}

	@Override
	public int execute() {

		log.info("outbound joint start");
		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("inOutType", "O");
		param.put("date_issue", this.date);

		String inOutType = "O";
		List<HashMap<String, Object>> result= scheduleService.selecteScheduleListMapByCondition(param);

		log.info("schedule size:{}",result.size());

		HashMap<String, Object> areaMap = new HashMap<String, Object>();

		Iterator<HashMap<String, Object>>iter = result.iterator();
		try {

			// ������ �׷�
			while(iter.hasNext())
			{
				HashMap<String, Object> item 	= iter.next();

				String area_name				= (String) item.get("area_name");			

				String toPort = (String) item.get(inOutType.equals("O")?"port":"fromPort");

				String fromPort = (String) item.get(inOutType.equals("O")?"fromPort":"port");

				if(areaMap.containsKey(area_name))
				{  
					//�ش� ������ ������ ���
					HashMap<String, Object> toPorts =(HashMap<String, Object>) areaMap.get(area_name);

					//����� ���� ���
					if(toPorts.containsKey(toPort))					  
					{   
						//������ ���
						HashMap<String, Object> fromPorts =(HashMap<String, Object>) toPorts.get(toPort);

						// ������ ���� ���
						if(fromPorts.containsKey(fromPort))
						{
							List list =(List) fromPorts.get(fromPort); 
							list.add(item);

							// ����� �������� ����
							Collections.sort(list, new AscendingFromDate() );
						}
						// ������ ���� ���
						else
						{
							ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();

							scheduleList.add(item);

							fromPorts.put(fromPort, scheduleList);
						}
						// ������ ���


						// �ش� �������� ���� ���


					}
					// ����� ���� ���
					else
					{
						// �ű� ������ ����Ʈ ����
						ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
						scheduleList.add(item);

						HashMap<String, Object> newFromPorts = new HashMap<String, Object>();
						newFromPorts.put(fromPort, scheduleList);

						// �ű� ������ ���� ����
						toPorts.put(toPort, newFromPorts);

					}

				}
				else
				{
					ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
					scheduleList.add(item);


					HashMap<String, Object> newFromPorts = new HashMap<String, Object>();
					newFromPorts.put(fromPort, scheduleList);

					HashMap<String, Object> newToPorts = new HashMap<String, Object>();

					newToPorts.put(toPort, newFromPorts);

					areaMap.put(area_name, newToPorts);
				}
			}

			// ������ ���� �� ����Ʈȭ
			Iterator<String> areaIter= (Iterator<String>) areaMap.keySet().iterator();

			ScheduleList areaLists = new ScheduleList(date);

			while(areaIter.hasNext())
			{
				String area_name = areaIter.next();

				HashMap<String, Object> toitem= (HashMap<String, Object>) areaMap.get(area_name);

				Iterator<String> toPortite= (Iterator<String>) toitem.keySet().iterator();

				ScheduleList toPortList = new ScheduleList(area_name);

				//TODO ���� : ����� ���� �߰�
				while(toPortite.hasNext())
				{
					String toPort = toPortite.next();

					HashMap<String, Object> Schjeduleitem= (HashMap<String, Object>) toitem.get(toPort);

					Iterator<String> fromPortIter= (Iterator<String>) Schjeduleitem.keySet().iterator();

					ScheduleList fromPortList = new ScheduleList(toPort);
					//TODO ���� : ������ ���� �߰�
					while(fromPortIter.hasNext())
					{
						String fromPort = fromPortIter.next();

						List<HashMap<String, Object>> scheduleItems = getJointScheduleList((List) Schjeduleitem.get(fromPort));

						ScheduleList scheduleList = new ScheduleList(fromPort);

						//TODO ���� ����� ���� �߰�
						for(HashMap<String, Object> temp:scheduleItems)
						{	
							scheduleList.add(temp);
						}

						fromPortList.add(scheduleList);
					}	
					toPortList.add(fromPortList);
				}
				areaLists.add(toPortList);
			}

			new TagedPrintSchedule(areaLists).print();

		}catch(Exception e)
		{
			e.printStackTrace();
		}


		log.info("outbound joint end");
		return 0;
	}

	/**�����輱 ���� ����Ʈ
	 * 
	 * 
	 * @param list
	 * @return
	 */
	private List getJointScheduleList(List list)
	{
		return list;
	}

	class AscendingFromDate implements Comparator<HashMap<String,Object>> 
	{ 
		@Override 
		public int compare(HashMap<String,Object> one, HashMap<String,Object> two) {

			String fromDateOne = String.valueOf(one.get("dateF"));
			String fromDateTwo = String.valueOf(two.get("dateF"));

			return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;

		} 
	}

	abstract class PrintSchedule
	{
		protected FileWriter fw;

		protected String fileLocation;

		public abstract void printArea(String area) throws IOException;		

		public abstract void printToPort(String port)throws IOException;

		public abstract void printFromPort(String name)throws IOException;
		
		public abstract void printSchedule(HashMap<String, Object> temp) throws IOException;

		public PrintSchedule() throws IOException {
			fileLocation=KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION);
			
		}

	}
	class TagedPrintSchedule extends NormalPrintSchedule
	{
		public TagedPrintSchedule(ScheduleList scheduelList) throws IOException  {
			super(scheduelList);
		}

		public void printArea(String area) throws IOException
		{
			System.out.println("Area1:"+area);
			fw.write("Area1:"+area);
		}

		public void printToPort(String port) throws IOException
		{
			System.out.println(" \r\n"+port);
			fw.write(" \r\n"+port);
		}
		public void printFromPort(String name) throws IOException {
			System.out.println(" \r\n- "+name+" -\r\n");
			fw.write(" \r\n- "+name+" -\r\n");

		}

	}
	class NormalPrintSchedule<E> extends PrintSchedule
	{
		ScheduleList<E> scheduelList;

		public NormalPrintSchedule(ScheduleList<E> scheduelList) throws IOException  {
			this.scheduelList =scheduelList;
		}

		public void printArea(String area) throws IOException
		{
			System.out.println("Area:"+area);
			fw.write("Area:"+area);
		}

		public void printToPort(String port) throws IOException
		{
			System.out.println(" \r\n"+port);
			fw.write(" \r\n"+port);
		}

		public void print() throws IOException
		{	
			Iterator<ScheduleList> areaIter= (Iterator<ScheduleList>) scheduelList.iterator();

			try {
				fw = new FileWriter(fileLocation+"/"+"test.txt");
				
				while(areaIter.hasNext())
				{
					ScheduleList area = areaIter.next();

					printArea(area.name);

					Iterator<ScheduleList> toPortIter = area.iterator();

					// ����
					while(toPortIter.hasNext())
					{
						ScheduleList toPort = toPortIter.next();

						printToPort(toPort.name);

						Iterator<ScheduleList> fromPortIter= toPort.iterator();

						//����
						while(fromPortIter.hasNext())
						{
							ScheduleList<HashMap<String, Object>> scheduleList = fromPortIter.next();

							printFromPort(scheduleList.name);						

							// ����
							for(HashMap<String, Object> temp:scheduleList)
							{
								printSchedule(temp);

							}
						}
					}
				}
			}
			catch(Exception e)
			{	
				throw new RuntimeException();
			}
			finally
			{
				fw.close();
			}
		}
		
		private String getScheduleInfo(HashMap<String, Object> schedule)	
		{
			String vessel_name 	= (String)schedule.get("vessel");
			String company 		= (String)schedule.get("company_abbr");
			String dateF 		= (String)schedule.get("dateF");
			String dateT 		= (String)schedule.get("dateT");

			return getFormatedDate(dateF)+"\t"+vessel_name+"["+"]   ("+company+")\t"+getFormatedDate(dateT)+ "\r\n";

		}

		public void printSchedule(HashMap<String, Object> temp) throws IOException {
			System.out.println("\t\t\t"+getScheduleInfo(temp));
			fw.write("\t\t\t"+getScheduleInfo(temp));
		}

		public void printFromPort(String name) throws IOException {
			System.out.println(" \r\n- "+name+" -\r\n");
			fw.write(" \r\n- "+name+" -\r\n");

		}
	}

	class ScheduleList<E> extends ArrayList<E>
	{
		private String name;

		public ScheduleList() {

		}

		public ScheduleList(String name) {
			this.name = name;
		}
	}


}