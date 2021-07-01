package com.ksg.schedule.joint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ksg.common.util.KSGDateUtil;

/**

  * @FileName : OutboundJoint.java

  * @Date : 2021. 5. 13. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� :

  */
public class OutboundJoint extends KSGScheduleJoint{
	
	ScheduleGroup group;
	int count;
	private List<Map<String, Object>> scheduleList;

	private int total;
	
	public OutboundJoint() {
		super();
		
		group =new ScheduleGroup("fromPort", new JointGroup("vessel"));
	}

	@Override
	public int execute() throws Exception {
	
		
		count=0;
		//����
		for(Map<String, Object> item:scheduleList)
		{	
			String area_name = (String) item.get("area_name");
			
			List<Map<String, Object>> port_list =  (List<Map<String, Object>>) item.get("port_list");
			
			System.out.println(area_name);
			//�����
			for(Map<String, Object> port:port_list)
			{
				String strPort =(String) port.get("port");
				
				System.out.println(String.format("\t<%s>", strPort));
				
				List<Map<String, Object>> schedule_list =(List) item.get(strPort);
				
				group.clear();
				
				//������
				for(Map<String, Object> schedule:schedule_list)
				{	
					group.addSchedule( schedule);
				}
				group.makeJointGroup();
				
			}			
		}
		System.out.println("size:"+total+",count:"+count);
		return 0;
	}

	@Override
	public void init() {

		try {
			long start = System.currentTimeMillis();
			
			HashMap<String, Object> commandMap = new HashMap<String, Object>();

			commandMap.put("inOutType", "O");

			commandMap.put("orderby", "area_name, port, fromPort, vessel,DateF, DateT");
			
			total = scheduleService.getTotalCount(commandMap);

			scheduleList = scheduleService.selectScheduleJointList(commandMap);
			
			long end = System.currentTimeMillis();
			
			System.out.println("total:"+total);
			
			System.out.println("����ð�: " + (end - start) + " ms");
			
			

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	class JointGroup extends AbstractGroup
	{
		public JointGroup(String keyName) {
			super(keyName);
		}
		
		@SuppressWarnings("unchecked")
		private List createJointScheduleList(List<HashMap<String, Object>> li)
		{
			if(li.size()>1)
			{
				List<HashMap<String, Object>> newList = new LinkedList<HashMap<String,Object>>();
				
				HashMap<String, Object> temp=li.get(0);
				
				boolean isCommon=false;// �����輱����
				
				List<String> companyList = new LinkedList<String>();
				
				companyList.add((String) temp.get("company_abbr"));
				
				for(int i=0;i<li.size();i++)
				{
					HashMap<String, Object> item = li.get(i);
				
					String firstDate = (String) temp.get("DateF");
					String secondDate = (String) item.get("DateF");
					
					//3�� �̳�
					if(KSGDateUtil.isThreeDayUnder(firstDate,secondDate))
					{
						//������� ���� ��¥ ����
						
						//�������� ���� ��¥ ����						
						String fristDateT = (String) temp.get("DateT");
						String secondDateT = (String) item.get("DateT");
						String company_abbr = (String) item.get("company_abbr");
						
						addCompany(companyList, company_abbr);
						
						temp.put("DateT", KSGDateUtil.biggerDate(fristDateT, secondDateT));
						
						isCommon=true;
					}
					//3�� �̻�
					else
					{
						newList.add((HashMap<String, Object>) temp.clone());
						
						temp = item;
						
						isCommon=false;
						
						companyList.clear();
					}
					
					if(i==li.size()-1&&isCommon)
					{
						HashMap<String, Object> temp2 =createJointSchedule(item, companyList);						
						
						newList.add(temp2);
					}					
				}
				
				return newList;
			}
			else
			{
				return li;	
			}
			
		}
		
		/**
		 * �����輱 ����� ����
		 * @param companyList
		 * @param company
		 */
		private void addCompany(List companyList, String company)
		{
			for(int i=0;i<companyList.size();i++)
			{
				if(companyList.get(i).equals(company))
				{
					return;
				}
			}
			companyList.add(company);
		}
		
		public HashMap<String, Object> createJointSchedule(HashMap<String, Object> item, List<String> companyList)
		{
			HashMap<String, Object> temp =(HashMap<String, Object>) item.clone();
			
			StringBuffer buf = new StringBuffer();
			
			
			//�������� ������Ʈ
			for(int index=0;index<companyList.size();index++)
			{
				buf.append(companyList.get(index)+(index<companyList.size()-1?",":""));
			}
			
			temp.put("company_abbr", buf.toString());
			return temp;
		}
		
		public void print()
		{
			Iterator<String> iter = this.keySet().iterator();
			while(iter.hasNext())
			{
				String key = iter.next();
				
				List<HashMap<String, Object>> li = createJointScheduleList((List<HashMap<String, Object>>) this.get(key));
				
				for(HashMap<String, Object> schedule:li)
				{
					String vessel = (String) schedule.get("vessel");
					
					String DateF = (String) schedule.get("DateF");
					
					String DateT = (String) schedule.get("DateT");
					
					String company = (String) schedule.get("company_abbr");
					count++;
					System.out.println(String.format("\t\t\t<%s %s (%s) %s>", convertDateFormat(DateF), vessel,company, convertDateFormat(DateF)));
				}
			}
		}
	}
	
	/**
	
	  * @FileName : OutboundJoint.java
	
	  * @Date : 2021. 5. 13. 
	
	  * @�ۼ��� : ��â��
	
	  * @�����̷� :
	
	  * @���α׷� ���� : ������ �׷��� Ŭ����(����� �������� �׷����)
	
	  */
	class ScheduleGroup extends AbstractGroup
	{
		JointGroup jointGroup;
		
		public ScheduleGroup(String key,JointGroup jointGroup) {
			this(key);
			
			this.jointGroup =jointGroup;
		}
		
		
		public void makeJointGroup()
		{
			Iterator<String> iter = this.keySet().iterator();
			while(iter.hasNext())
			{
				String key = iter.next();
				
				List<HashMap<String, Object>> li = (List) this.get(key);
				
				System.out.println(String.format("\t\t<%s>", key));
				
				jointGroup.clear();
				
				for(HashMap<String, Object> schedule:li)
				{
					jointGroup.addSchedule(schedule);
				}
				jointGroup.print();
			}
		}
		public ScheduleGroup(String key) {
			super(key);
		}		
	}

}
