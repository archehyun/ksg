package com.ksg.commands.schedule.route;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.JDOMException;

import com.ksg.commands.schedule.StringCompare;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.model.KSGModelManager;
import com.ksg.view.schedule.dialog.ScheduleBuildMessageDialog;
import com.ksg.view.util.KSGDateUtil;
import com.ksg.view.util.KSGPropertis;

/**
 * @author 박창현
 * @적용 룰 목록
 *   1. 외국항이 3개 이상인 경우에만 스케줄 출력
 *   2. 마지막 국내항 일자가 옵션 날짜 보다 큰 경우
 *   3. 정렬 기준
 *      - ORDER_BY_VESSEL:	지역-> 선박명	->	날짜		->	국내항	-> 외국항
 *      - ORDER_BY_DATE:	지역-> 날짜	->	선박명	->	국내항	-> 외국항
 *   4. 항구 그룹시
 *      - 국내항 : 늦은 날짜 기준
 *      - 국제항 : 
 *       
 *   GroupArea
 *   GroupVessel
 *   GroupInOutPort    
 *       
 */
public class RouteTaskNewVessel extends DefaultRouteTask{

	
	/**
	 * @설명 지역이름을 기준으로 선박을 그룹화 하는 클래스 
	 * @author 박창현
	 *
	 */
	class GroupArea extends HashMap<String, GroupVessel> implements Comparable
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
				
				// 기존 그룹 추가
				GroupVessel group = this.get(key);
				// 키 : 선박명
				group.addSchedule(data);

			}else
			{
				// 신규 그룹 생성
				// 키 :선막명				
				
				this.put(key, new GroupVessel(data));
			}
		}

		// 지역 이름을 기준으로 정렬
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
		private boolean isBigger(String fdate, String tdate) {

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
		private GroupVessel[] toSortedVesselArray()
		{
			Set<String>keylist=keySet();

			ArrayList<GroupVessel> newList = new ArrayList<GroupVessel>();

			Iterator<String> iter=keylist.iterator();

			while(iter.hasNext())	
			{
				String key =iter.next();

				GroupVessel item  = this.get(key);
				if(item.getInPortListSize()==0)
					continue;
				String lastInScheduleDate=item.getLastInScheduleDate();


				// 옵션 날짜 보다 크면 추가
				if(isBigger(op.getDate_isusse(),lastInScheduleDate ))
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
			GroupVessel group[] =this.toSortedVesselArray();
			StringBuffer buffer = new StringBuffer();
			for(int i=0;i<group.length;i++)
			{
				buffer.append(group[i].toString());	
			}

			return buffer.toString();
		}
	}
	/**
	 * @author 박창현
	 *
	 */
	class GroupInOutPort extends HashMap<String, PortScheduleInfo>
	{
		public static final int IN=1;
		public static final int OUT=2;

		private static final long serialVersionUID = 1L;
		private int type;
		public GroupInOutPort(int type) {
			this.type = type;
		}

		/**
		 * @설명 항구 추가
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
		 * @설명 항구 그룹 정렬
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
		 * @설명 항구 그룹 정렬: 출발일 기준이후 항구만 정렬
		 * @param startDate 시작일
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
				// 인바운드 마지막 날자 보다 크면 추가
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
			Arrays.sort(lit);  // 정렬

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
	 * @author 박창현
	 *
	 */
	class GroupVessel implements Comparable
	{
		private ArrayList<String> companyList;
		private GroupInOutPort inPortList;
		private String major_company; //대표 선사

		private GroupInOutPort outPortList;

		private String vessel_name; // 선박명
		private String voyage_num;
		public GroupVessel(ScheduleData data) throws SQLException {

			this.vessel_name = data.getVessel();
			this.voyage_num = data.getVoyage_num();

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
			inPortList = new GroupInOutPort(GroupInOutPort.IN);
			outPortList = new GroupInOutPort(GroupInOutPort.OUT);
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
			//알파벳 정렬
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
			case ORDER_BY_DATE:// 날짜 정렬
				try {
					if(KSGDateUtil.daysDiff(inputDateType_yyyy_MM_dd.parse(this.getFirstInScheduleDate()), inputDateType_yyyy_MM_dd.parse(one.getFirstInScheduleDate()))<0)
					{
						result= 1;
					}else
					{
						result= -1;
					}
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result= -1;
				}

				break;
			case ORDER_BY_VESSEL:// 선박명 정렬
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

			int result=getNumericVoyage(vessel);
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
	 * @author 박창현
	 */
	class PortScheduleInfo implements Comparable
	{
		private String date;
		private String port;
		public PortScheduleInfo(String port, String date) {
			this.port = port;
			this.date = date;
		}
		public int compareTo(Object o) {

			PortScheduleInfo d =(PortScheduleInfo) o;
			try {
				return KSGDateUtil.daysDiff(KSGDateUtil.toDate4(d.date), KSGDateUtil.toDate4(this.date));
			} catch (ParseException e) {
				return -1;
			}
		}
		public String getDate() {
			return date;
		}
		public String getPort() {
			return port;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public void setPort(String port) {
			this.port = port;
		}
		public String toString()
		{
			String printDate="";
			try {
				printDate = outputDateType.format(inputDateType_yyyy_MM_dd.parse(this.getDate()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this.getPort()+" "+printDate;
		}

	}
	/**
	 * @설명 최상위 그룹
	 * @author 박창현
	 *
	 */
	class GroupSchedule extends HashMap<String, GroupArea>
	{		
		private static final long serialVersionUID = 1L;

		public void addScheule(ScheduleData data) throws SQLException
		{
			if(this.containsKey(data.getArea_name()))
			{
				// 기존 지역 추가
				GroupArea group = this.get(data.getArea_name());
				// 키 : 지역명
				group.addSchedule(data);

			}else
			{
				// 신규 그룹 생성
				// 키 : 지역명

				this.put(data.getArea_name(), new GroupArea(data));
			}
		}

		public GroupArea[] toSortedArray() {
			Set<String>keylist=keySet();

			ArrayList<GroupArea> newList = new ArrayList<GroupArea>();

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

	}

	public static void main(String[] args) {
		ShippersTable op = new ShippersTable();
		op.setDate_isusse("2014/11/17");
		new RouteTaskNewVessel(op,RouteTaskNewVessel.ORDER_BY_VESSEL).start();
	}
	private String fileName;


	private int orderByType=1;
	private final int OUT_PORT_SIZE = 3;// 최소 외국항 수	

	public RouteTaskNewVessel(ShippersTable op) {


		this.op = op;
		
	}

	public RouteTaskNewVessel(ShippersTable op, int orderBy) {
		this(op);
		this.orderByType =orderBy;
	}

	public void makeSchedule() throws SQLException, JDOMException, IOException,
	ParseException {

		di = new ScheduleBuildMessageDialog(this);
		di.setMessage("Route ");
		di.createAndUpdateUI();


		List<ScheduleData> li =scheduleService.getOutboundScheduleList();
		logger.info("schedule size:"+li.size());
		Iterator<ScheduleData> iter = li.iterator();

		// 스케줄 그룹 생성
		GroupSchedule scheduleGroup = new GroupSchedule(); 
		while(iter.hasNext())
		{
			ScheduleData data = iter.next();
			scheduleGroup.addScheule(data);
		}
		FileWriter fw = new FileWriter(fileName);


		// 출력 프로세스
		// toPort 그룹에서 키 셋(도착항) 조회


		// 태그 정보 출력

		fw.write(WORLD_VERSION1+"\r\n"+WORLD_VERSION2);
		GroupArea[] areaGroups=scheduleGroup.toSortedArray();
		// 스케줄 출력
		//Set<String>keylist=scheduleGroup.keySet();
		logger.info("area size:"+areaGroups.length);

		for(int i=0;i<areaGroups.length;i++)
		{
			GroupArea group =areaGroups[i];
			logger.info("area:"+group.getArea_name());
			fw.write((i>0?"\r\n\r\n\r\n\r\n":"")+group.getArea_name()+"\r\n\r\n");
			GroupVessel[] vesselList=group.toSortedVesselArray();
			for(int j=0;j<vesselList.length;j++)
			{
				//외국항이 3개 이상인 경우에만 스케줄 출력
				//마지막 국내항 일자가 옵션 날짜 보다 큰 경우
				if(vesselList[j].getOutPortSize()>=OUT_PORT_SIZE&&
					KSGDateUtil.daysDiff(inputDateType_yyyy_MM_dd.parse(op.getDate_isusse()), inputDateType_yyyy_MM_dd.parse(vesselList[j].getLastInScheduleDate()))>0)
					
				fw.write(vesselList[j].toString());
			}
			fw.write(WORLD_E);
		}
		// 파일 닫기
		fw.close();
	}
	public void start()
	{
		logger.info("op date:"+op.getDate_isusse());
		try {
			fileName = KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+"world_print_new_"+(orderByType==RouteTaskNewVessel.ORDER_BY_DATE?"date":"vessel")+".txt";
			initTag();
			makeSchedule();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e.getMessage());
		} finally
		{
			done=true;
			di.setVisible(false);
			di.dispose();
		}
	}
}
