package com.ksg.schedule.joint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ksg.base.service.CodeService;
import com.ksg.common.util.KSGDateUtil;

/**

  * @FileName : InboundJoint.java

  * @Date : 2021. 5. 18. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :
  * Inbound 스케줄 생성
  * 

  */
public class InboundJoint extends KSGScheduleJoint{

	private List<Map<String, Object>> scheduleList;
	
	private List<Map<String, Object>> inPortCodeList;
	
	private int total,count;
	
	private ScheduleGroup group;
	
	CodeService codeService;
	
	String tag="-";
	
	public InboundJoint() {
		super();
		group = new ScheduleGroup("vessel", new InboundJointGroup("vessel"));
		codeService = new CodeService();
	}
	
	/**
	 * @param name 국내항항구명
	 * @return 국내항 항구명 약어
	 */
	public String toInPortAbbr(String name)
	{
		for(Map<String, Object> item : inPortCodeList)
		{
			if(item.get("code_field").equals(name))
			{
				return (String) item.get("code_name");
			}
		}
		return "";
	}

	@Override
	public int execute() throws Exception {
		
		count=0;
		
		for(Map<String, Object> item:scheduleList)
		{	
			String strPort = (String) item.get("fromPort");
			
			System.out.println(strPort);
			
			List<Map<String, Object>> schedule_list =  (List<Map<String, Object>>) item.get(strPort);

			group.clear();
			
			for(Map<String, Object> schedule:schedule_list)
			{
				group.addSchedule(schedule);
			}
			group.makeJointGroup();

		}
		System.out.println("size:"+total+",count:"+count);
		return 0;
	}

	@Override
	public void init() {
		try {
			long start = System.currentTimeMillis();
			System.out.println("init start");
			
			// 축약어 코드 조회
			
			HashMap<String, Object> commandMapCode = new HashMap<String, Object>();
			
			commandMapCode.put("code_type", "inPort");
			
			inPortCodeList=(List<Map<String, Object>>) codeService.selectCodeDList(commandMapCode).get("master");
			
			System.out.println(inPortCodeList);
			
			
			HashMap<String, Object> commandMap = new HashMap<String, Object>();

			commandMap.put("inOutType", "I");

			commandMap.put("orderby", "fromPort, vessel, DateF, port");
			
			total = scheduleService.getTotalCount(commandMap);

			scheduleList = scheduleService.selectInboundScheduleJointList(commandMap);
			
			long end = System.currentTimeMillis();
			
			System.out.println("total:"+total);
			
			System.out.println("수행시간: " + (end - start) + " ms");

		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	class InPortGroup extends HashMap<String, Object>
	{	
		public void addInPort(HashMap<String, Object> item)
		{
			String port = (String) item.get("port");
			String dateT = (String) item.get("DateT");
			
			List li = null;
			
			if(this.containsKey(port))
			{
				li = (List) this.get(port);
				li.add(dateT);
				
			}else
			{
				li = new LinkedList<HashMap<String, Object>>();
				li.add(dateT);
				this.put(port, li);
			}
		}
		public InPortGroup(String strKey) {
		}
	}

	/**
	
	  * @FileName : InboundJoint.java
	
	  * @Date : 2021. 6. 2. 
	
	  * @작성자 : 박창현
	
	  * @변경이력 :
	
	  * @프로그램 설명 :
	
	  */
	class InboundJointGroup extends AbstractGroup
	{	
		List fromDateList = new LinkedList();
		List inPortList = new LinkedList();
		public InboundJointGroup(String key) {
			super(key);
		}

		/**
		 * 
		 * 국내항
		 * @param li
		 * @return
		 */
		private List createJointScheduleList(List<HashMap<String, Object>> li)
		{
			if(li.size()>1)
			{	
				// 국내 항 그룹
				InPortGroup inPortGroup = new InPortGroup("port");
				
				List<HashMap<String, Object>> newList = new LinkedList<HashMap<String,Object>>();
				HashMap<String, Object> temp=li.get(0);
				boolean isCommon=false;// 공동배선여부
				List<String> companyList = new LinkedList<String>();
				
				for(int i=0;i<li.size();i++)
				{
					HashMap<String, Object> item = li.get(i);
				
					String firstDate = (String) temp.get("DateF");
					String secondDate = (String) item.get("DateF");
					
					//출발일 3일 이내
					if(KSGDateUtil.isDayUnder(3, firstDate,secondDate))
					{
						//출발일은 빠른 날짜 적용
						
						//도착일은 늦은 날짜 적용						
						String fristDateT = (String) temp.get("DateT");
						String secondDateT = (String) item.get("DateT");
						String company_abbr = (String) item.get("company_abbr");
						
						// 선사명 추가
						addCompany(companyList, company_abbr);
						
						// 국내항 추가
						// addInPort();
						
						inPortGroup.addInPort(item);
						
						temp.put("DateT", KSGDateUtil.biggerDate(fristDateT, secondDateT));
						
						isCommon=true;
					}
					//3일 이상
					else
					{
						newList.add((HashMap<String, Object>) temp.clone());
						
						temp = item;
						
						isCommon=false;
						
						companyList.clear();
						inPortGroup.clear();
						//.clear()
					}
					
					if(i==li.size()-1&&isCommon)
					{
						HashMap<String, Object> temp2 =createJointSchedule(item, companyList);						
						
						newList.add(temp2);
					}					
				}
				
				return newList;
				
			}else
			{
				return li;	
			}
		}
		
		/**
		 * 공동배선 선사명 생성
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
			
			
			//선사정보 업데이트
			for(int index=0;index<companyList.size();index++)
			{
				buf.append(companyList.get(index)+(index<companyList.size()-1?",":""));
			}
			
			temp.put("company_abbr", buf.toString());
			return temp;
		}

		public void print() {
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

					String port = (String) toInPortAbbr((String) schedule.get("port"));

					String company = (String) schedule.get("company_abbr");
					count++;
					System.out.println(String.format("\t\t<%s"+tag+"%s (%s) %s %s>", convertDateFormat(DateF), vessel,company, port, convertDateFormat(DateT)));
				}
			}
		}
	}

	class ScheduleGroup extends AbstractGroup
	{
		InboundJointGroup jointGroup;

		public ScheduleGroup(String key,InboundJointGroup jointGroup) {
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

	public static void main(String[] args) {
		InboundJoint joint = new InboundJoint();
		joint.init();
		try {
			joint.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
