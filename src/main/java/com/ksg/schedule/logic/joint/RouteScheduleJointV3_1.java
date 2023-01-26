package com.ksg.schedule.logic.joint;

import java.io.FileWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.StringCompare;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.ScheduleBuild;
import com.ksg.schedule.logic.ScheduleManager;

/**
 * @author ��â��
 * @���� �׷κ� ������ ����
 * @���� �� ���
 *   1. �ܱ����� 3�� �̻��� ��쿡�� ������ ���
 *   2. ������ ������ ���ڰ� �ɼ� ��¥ ���� ū ���
 *   3. ���� ����
 *      - ORDER_BY_VESSEL:	����-> ���ڸ�	->	��¥		->	������	-> �ܱ���
 *      - ORDER_BY_DATE:	����-> ��¥	->	���ڸ�	->	������	-> �ܱ���
 *   4. �ױ� �׷��
 *      - ������ : ���� ��¥ ����
 *      - ������ : 
 *       
 *   GroupArea
 *   GroupVessel
 *   GroupInOutPort    
 *       
 */
public class RouteScheduleJointV3_1 extends RouteAbstractScheduleJoint{


	/**
	 * @���� �����̸��� �������� ������ �׷�ȭ �ϴ� Ŭ���� 
	 * @author ��â��
	 *
	 */
	class GroupArea extends HashMap<String, Groupable> implements Groupable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String area_name;

		public GroupArea(ScheduleData data) throws SQLException {
			this.area_name = data.getArea_name();

			addSchedule(data);
		}

		public void addSchedule(ScheduleData data) throws SQLException 
		{

			String key =data.getVessel()+"\r\n"+data.getVoyage_num();
			if(this.containsKey(key))
			{
				// ���� �׷� �߰�
				Groupable group = this.get(key);
				// Ű : ���ڸ�
				group.addSchedule(data);

			}else
			{
				// �ű� �׷� ����
				// Ű :������				
				this.put(key, new GroupVessel(data));
			}
		}

		// ���� �̸��� �������� ����
		public int compareTo(Object arg0) {
			GroupArea one = (GroupArea) arg0;
			if(this.area_name.compareTo(one.area_name)>0)
			{
				return 1;
			}else
			{
				return -1;
			}
		}

		public String getArea_name() {
			return area_name;
		}
		private boolean isBiggerDate(String fdate, String tdate) {

			try {
				return KSGDateUtil.daysDiff(KSGDateUtil.toDate4(fdate), KSGDateUtil.toDate4(tdate))>0;
			} catch (ParseException e) {
				throw new RuntimeException(e.getMessage()+"oneDate:"+fdate+", twoDate:"+tdate);
			}
		}
		public void setArea_name(String area_name) {
			this.area_name = area_name;
		}
		/**
		 * @return
		 */
		private GroupVessel[] toSortedArray()
		{
			Set<String>keylist=keySet();

			ArrayList<Groupable> newList = new ArrayList<Groupable>();

			Iterator<String> iter=keylist.iterator();

			while(iter.hasNext())	
			{
				String key =iter.next();

				GroupVessel item  = (GroupVessel) this.get(key);
				
				if(item.getInPortListSize()==0) continue;
					
				String lastInScheduleDate=item.getLastInScheduleDate();

				// �ɼ� ��¥ ���� ũ�� �߰�
				if(isBiggerDate(op.getDate_isusse(),lastInScheduleDate ))
				{
					newList.add(get(key));
				}
			}

			GroupVessel lit[] = new GroupVessel[newList.size()];
			
			lit = newList.toArray(lit);
			
			Arrays.sort(lit);

			return lit;
		}
		public String toString()
		{
			GroupVessel group[] =this.toSortedArray();
			
			StringBuffer buffer = new StringBuffer();
			
			for(int i=0;i<group.length;i++)
			{
				buffer.append(group[i].toString());	
			}

			return buffer.toString();
		}

		public String getID() {
			return this.getArea_name();
		}
	}
	/**
	 * @author ��â��
	 *
	 */
	class PortArray extends HashMap<String, PortScheduleInfo>
	{
		public static final int IN=1;
		
		public static final int OUT=2;

		private static final long serialVersionUID = 1L;
		
		private int type;
		
		public PortArray(int type) {
			this.type = type;
		}

		/**
		 * @���� �ױ� �߰�
		 * @param data
		 */
		public void addPort(PortScheduleInfo data)
		{

			if(containsKey(data.getPort()))
			{
				PortScheduleInfo one=get(data.port);
				try {
					if(KSGDateUtil.daysDiff(inputDateType_yyyy_MM_dd.parse(one.getDate()), inputDateType_yyyy_MM_dd.parse(data.getDate()))>=0)
					{
						put(data.getPort(), data);
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else
			{
				this.put(data.getPort(),data);
			}
		}

		private String getStringInfo(PortScheduleInfo[] portList)
		{
			StringBuffer buffer = new StringBuffer();
			
			for(int i=0;i<portList.length;i++)
			{
				buffer.append(portList[i]+(i<(portList.length-1)?" - ":""));
			}

			return buffer.toString();
		}

		/**
		 * @���� �ױ� �׷� ����
		 * @return
		 */
		public PortScheduleInfo[] toArray()
		{
			Set<String>keylist=keySet();
			PortScheduleInfo lit[] = new PortScheduleInfo[keylist.size()];
			Iterator<String> iter=keylist.iterator();

			for(int i=0;iter.hasNext();i++)	
			{
				String key =iter.next();
				lit[i]=get(key);
			}
			Arrays.sort(lit);

			return lit;
		}


		/**
		 * @���� �ױ� �׷� ����: ����� �������� �ױ��� ����
		 * @param startDate ������
		 * @return
		 */
		public PortScheduleInfo[] toArray(String startDate)
		{
			Set<String>keylist=keySet();

			ArrayList<PortScheduleInfo> newList = new ArrayList<PortScheduleInfo>();

			Iterator<String> iter=keylist.iterator();

			while(iter.hasNext())	
			{
				String key =iter.next();
				// �ιٿ�� ������ ���� ���� ũ�� �߰�
				try {
					if(KSGDateUtil.daysDiff(inputDateType_yyyy_MM_dd.parse(get(key).getDate()), inputDateType_yyyy_MM_dd.parse(startDate))<0)
					{
						newList.add(get(key));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			PortScheduleInfo lit[] = new PortScheduleInfo[newList.size()];
			lit = newList.toArray(lit);
			Arrays.sort(lit);  // ����

			return lit;
		}
		public String toStringSchedule()
		{
			PortScheduleInfo[] portList = this.toArray();

			return  getStringInfo(portList);
		}
		public String toStringSchedule(String startDate)
		{
			PortScheduleInfo[] portList = this.toArray(startDate);

			return getStringInfo(portList);
		}
	}
	/**
	 * @author ��â��
	 *
	 */
	class GroupVessel implements Groupable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<String> companyList;
		private PortArray inPortList;
		private String major_company; //��ǥ ����

		private PortArray outPortList;

		private String vessel_name; // ���ڸ�
		private String voyage_num;
		public GroupVessel(ScheduleData data) throws SQLException {

			this.vessel_name = data.getVessel();
			this.voyage_num = data.getVoyage_num();


			// ���� ���� ��ȸ �� �ʱ�ȭ
			Vessel op = new Vessel();
			op.setVessel_name(data.getVessel());

			Vessel searchedVessel = baseService.getVesselInfo(op);
			if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
			{

				if(searchedVessel.getVessel_company().contains("/"))
				{
					major_company=(String) searchedVessel.getVessel_company().subSequence(0, searchedVessel.getVessel_company().indexOf("/"));
				}else
				{
					major_company=searchedVessel.getVessel_company();
				}
			}

			// ������, �ܱ��� �ױ� ��� �� ���� �� ��� ����
			inPortList = new PortArray(PortArray.IN);
			outPortList = new PortArray(PortArray.OUT);
			companyList = new ArrayList<String>();
			this.addSchedule(data);
		}
		public void addSchedule(ScheduleData data) 
		{
			inPortList.addPort(new PortScheduleInfo(data.getFromPort(),data.getDateF()));
			outPortList.addPort(new PortScheduleInfo(data.getPort(), data.getDateT()));
			companyList.add(data.getCompany_abbr());
		}
		/**
		 * @param companyList
		 * @return
		 */
		private String arrangedCompanyList(ArrayList<String> companyList) {
			Vector<String> temp = new Vector<String>();
			for(int i=0;i<companyList.size();i++)
			{
				boolean flag=true;
				for(int j=0;j<temp.size();j++)
				{
					if(temp.get(j).equals(companyList.get(i)))
					{
						flag=false;
					}
				}
				if(flag)
					temp.add(companyList.get(i));
			}

			String[] array2=new String[temp.size()];

			for(int i=0;i<temp.size();i++)
			{
				array2[i]=temp.get(i);
			}

			companyList.toArray(array2);
			//���ĺ� ����
			Arrays.sort(array2, new StringCompare());


			String companyStringList ="";
			for(int i=0;i<array2.length;i++)
			{
				companyStringList+=array2[i]+(i<array2.length-1?",":"");

			}


			return companyStringList;

		}
		/**
		 * @param major_company
		 * @param companyList
		 * @return
		 */
		private String arrangedCompanyLists(String major_company,
				ArrayList<String> companyList) {

			ArrayList<String> new_company = new ArrayList<String>();
			for(int i=0;i<companyList.size();i++)
			{
				if(major_company.compareToIgnoreCase(companyList.get(i))!=0)
				{
					new_company.add(companyList.get(i));
				}
			}

			return major_company+","+arrangedCompanyList(new_company);
		}


		public int compareTo(Object arg0) {

			GroupVessel one = (GroupVessel) arg0;

			int result;
			switch (orderByType) {
			case ORDER_BY_DATE:// ��¥ ����
				try {
					if(KSGDateUtil.daysDiff(inputDateType_yyyy_MM_dd.parse(this.getFirstInScheduleDate()), inputDateType_yyyy_MM_dd.parse(one.getFirstInScheduleDate()))<0)
					{
						result= 1;
					}else
					{
						result= -1;
					}

				} catch (ParseException e) {
					e.printStackTrace();
					result= -1;
				}

				break;
			case ORDER_BY_VESSEL:// ���ڸ� ����
				if(this.vessel_name.compareTo(one.vessel_name)>0)
				{
					result= 1;
				}else
				{
					result= -1;
				}
				break;	

			default:
				result=-1;
				break;
			}
			return result;			

		}
		public String getFirstInScheduleDate() {
			PortScheduleInfo[] group = this.inPortList.toArray();

			return group[0].getDate();
		}

		public String getID()
		{
			return vessel_name+"\r\n"+voyage_num;
		}

		/** ������ �ױ���
		 * @return
		 */
		public int getInPortListSize()
		{
			return inPortList.size();
		}

		public String getLastInScheduleDate() {
			PortScheduleInfo[] group = this.inPortList.toArray();

			return group[group.length-1].getDate();
		}

		public int getOutPortSize()
		{
			return outPortList.size();
		}

		public String getVessel_name() {
			return vessel_name;
		}
		public String getVoyage_num() {
			return voyage_num;
		}
		private String getVoyageInfo(String vessel) {

			int result=ScheduleBuildUtil.getNumericVoyage(vessel);

			if(result!=0)
			{
				return " - "+vessel;
			}else
			{
				return "";
			}

		}

		public void setVessel_name(String vessel_name) {
			this.vessel_name = vessel_name;
		}
		public void setVoyage_num(String voyage_num) {
			this.voyage_num = voyage_num;
		}

		public String toString()
		{
			String company;

			if(major_company==null)
			{
				company =arrangedCompanyList(companyList);
			}
			else
			{
				company=arrangedCompanyLists(major_company,companyList);
			}

			return 	WORLD_F+getVessel_name()+getVoyageInfo(this.getVoyage_num())+" (" + company + ")"+"\r\n"+
			WORLD_INPORT+inPortList.toStringSchedule()+"\r\n"+
			WORLD_OUTPORT+outPortList.toStringSchedule(this.getLastInScheduleDate())+"\r\n\r\n";
		}

	}
	/**
	 * @���� ������,�ܱ��� ������ ǥ��(�ױ���, ��¥)
	 * @author ��â��
	 */
	class PortScheduleInfo implements Comparable
	{
		private String date;// ��¥
		private String port;// �ױ���
		private String printDate;
		public PortScheduleInfo(String port, String date) {
			this.port = port;
			this.date = date;
		}
		public int compareTo(Object o) {
			try {
				PortScheduleInfo d =(PortScheduleInfo) o;

				return KSGDateUtil.daysDiff(KSGDateUtil.toDate4(d.date), KSGDateUtil.toDate4(this.date));
			} catch (ParseException e) {
				return -1;
			}
		}

		public String getDate() {return date;}
		public String getPort() {return port;}
		public void setDate(String date) {this.date = date;}
		public void setPort(String port) {this.port = port;}

		public String toString()
		{
			try {
				printDate = outputDateType.format(inputDateType_yyyy_MM_dd.parse(this.getDate()));
			} catch (ParseException e) {

				e.printStackTrace();
				printDate = "error";
			}
			return this.getPort()+" "+printDate;
		}

	}

	/**
	 * @���� �ֻ��� �׷�
	 * @author ��â��
	 *
	 */
	class GroupSchedule extends HashMap<String, Groupable>
	{		
		private static final long serialVersionUID = 1L;		

		public GroupArea[] toSortedArray() {
			Set<String>keylist=keySet();

			ArrayList<Groupable> newList = new ArrayList<Groupable>();

			Iterator<String> iter=keylist.iterator();

			while(iter.hasNext())	
			{
				newList.add(get(iter.next()));
			}

			GroupArea lit[] = new GroupArea[newList.size()];
			lit = newList.toArray(lit);
			Arrays.sort(lit);

			return lit;
		}

		public void addSchedule(ScheduleData data) throws SQLException {
			if(this.containsKey(data.getArea_name()))
			{
				// ���� ���� �߰�
				Groupable group = this.get(data.getArea_name());
				// Ű : ������
				group.addSchedule(data);

			}else
			{
				// �ű� �׷� ����
				// Ű : ������

				this.put(data.getArea_name(), new GroupArea(data));
			}

		}

	}



	private String fileName;
	
	private SimpleDateFormat inputDateType_yyyy_MM_dd = new SimpleDateFormat("yyyy/MM/dd");
	
	private SimpleDateFormat outputDateType = new SimpleDateFormat("M/d");
	
	private int orderByType=1;
	
	private final int OUT_PORT_SIZE = 3;// �ּ� �ܱ��� ��	

	/*
	 * public RouteScheduleJointV3_1(ShippersTable op) throws SQLException {
	 * 
	 * super(); this.op = op;
	 * 
	 * }
	 */

	public RouteScheduleJointV3_1(ShippersTable op, int orderBy) throws SQLException {
		super(op);
		this.orderByType =orderBy;
	}
	public void initTag() {

		WORLD_F="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon���� �����100\\_TT>��<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		WORLD_B="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon���� �����100\\_TT>��<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		WORLD_VERSION1="<KSC5601-WIN>\r\n<vsn:8><fset:InDesign-Roman><ctable:=<Black:COLOR:CMYK:Process:0,0,0,1><60.100.0.0.:COLOR:CMYK:Process:0.6,1,0,0><30.60.0.0.:COLOR:CMYK:Process:0.3,0.6,0,0>>";
		WORLD_INPORT="<cc:><ct:><cs:><cf:><cc:30.60.0.0.><ct:Roman><cs:6.000000><cbs:-1.000000><cf:Helvetica LT Std>";
		WORLD_OUTPORT="<cc:><ct:><cs:><cbs:><cf:><ct:Roman><cs:6.000000><cf:Helvetica LT Std>";
		WORLD_VERSION2="<pstyle:><ct:Bold><chs:0.900000><cl:8.000000><cf:Helvetica LT Std>";
		WORLD_VERSION3="<pstyle:����><pli:182.500000><pfli:-182.000000><psa:0.566894><ptr:96.37789916992188\\,Left\\,.\\,0\\,\\;201\\,Left\\,.\\,0\\,\\;><chs:0.800003><cl:20.000000><cs:18.000000><cf:Helvetica LT Std>\r\n<cl:><cl:20.099990>\r\n<cs:><ct:Bold><cs:18.000000>";
		WORLD_E="<ct:><cs:><cf:><ct:Bold><cf:Helvetica LT Std>";

	}

	interface Groupable extends Comparable<Object>
	{
		public String getID();
		public void addSchedule(ScheduleData data) throws SQLException ;
	}


	@Override
	public int execute() {

		try{
			List<ScheduleData> li =scheduleService.getOutboundScheduleList();
			
			logger.info("outbound schedule size:"+li.size());
			
			Iterator<ScheduleData> iter = li.iterator();

			// ������ �׷� ����
			GroupSchedule scheduleGroup = new GroupSchedule(); 
			
			while(iter.hasNext())
			{
				ScheduleData data = iter.next();
				
				Vessel vesselInfo = ScheduleManager.getInstance().searchVessel(data.getVessel());
				
				// ������� �ʴ� �����̸� �����ٿ��� ����
				if(vesselInfo.getVessel_use()==Vessel.NON_USE)
					continue;
				
				
				scheduleGroup.addSchedule(data);
			}

			FileWriter fw = new FileWriter(fileName);

			// ��� ���μ���
			// toPort �׷쿡�� Ű ��(������) ��ȸ


			// �±� ���� ���

			fw.write(WORLD_VERSION1+"\r\n"+WORLD_VERSION2);
			GroupArea[] areaGroups=scheduleGroup.toSortedArray();
			// ������ ���
			Set<String>keylist=scheduleGroup.keySet();
			logger.debug("area size:"+keylist.size());

			for(int i=0;i<areaGroups.length;i++)
			{
				GroupArea group =areaGroups[i];
				fw.write((i>0?"\r\n\r\n\r\n\r\n":"")+group.getArea_name()+"\r\n\r\n");
				GroupVessel[] vesselList=group.toSortedArray();
				for(int j=0;j<vesselList.length;j++)
				{
					//�ܱ����� 3�� �̻��� ��쿡�� ������ ���
					//������ ������ ���ڰ� �ɼ� ��¥ ���� ū ���
					if(vesselList[j].getOutPortSize()>=OUT_PORT_SIZE&&
							KSGDateUtil.daysDiff(inputDateType_yyyy_MM_dd.parse(op.getDate_isusse()), inputDateType_yyyy_MM_dd.parse(vesselList[j].getLastInScheduleDate()))>0)
					{

						
						fw.write(vesselList[j].toString());
					}else
					{
						logger.error("����: "+vesselList[j].vessel_name);
						
						
					}
				}
				fw.write(WORLD_E);
			}
			// ���� �ݱ�
			fw.close();
			return ScheduleBuild.SUCCESS;
		}catch(Exception e)
		{
			e.printStackTrace();
			return ScheduleBuild.FAILURE;
		}

	}
}
