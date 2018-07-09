package com.ksg.commands.schedule.outbound;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.JDOMException;

import com.ksg.commands.schedule.StringCompare;
import com.ksg.commands.schedule.XML_INFO;
import com.ksg.commands.schedule.task.SimpleTask;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.Code;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;

/**
 * 
 * @���� �ƿ��ٿ�� ���� ���
 * @author archehyun
 *
 */
public class OutboundTaskV2 extends SimpleTask{

	// ��¥ ���� yyyy-mm-dd;
	private String 	BOLD_TAG_F="",
			BOLD_TAG_B="",
			TAG_VERSION0="",
			TAG_VERSION2="",
			TAG_VERSION3="",
			TAG_VERSION4="",
			TAG_VERSION5="";

	private boolean isApplyTag=false;
	private String[] fromPort;
	private String fileName;
	public OutboundTaskV2() {
		fileName = KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+"outbound_new.txt";
	}

	/* (non-Javadoc)
	 * @see com.ksg.commands.schedule.task.SimpleTask#makeSchedule()
	 */
	public void makeSchedule() throws SQLException, JDOMException, IOException,
	ParseException {

		List<ScheduleData> outbounScheduleList=scheduleService.getOutboundScheduleList();
		ScheduleGroup scheduleGroup = new ScheduleGroup();
		Iterator<ScheduleData> iter = outbounScheduleList.iterator();

		// ������ �׷�ȭ
		while(iter.hasNext())
		{
			ScheduleData item = iter.next();
			scheduleGroup.add(item);
		}
		
		/*���� ����
		 * 1. ������
		 * 2. �����
		 * 3. �����
		 * 4. ������ 
		 */



		String[] toPortkeyArray = scheduleGroup.keySet().toArray(new String[scheduleGroup.keySet().size()]);

		// ������ �������� ����
		bubbleSort(toPortkeyArray);

		FileWriter fw = new FileWriter(fileName);
		for(int toPortIndex=0;toPortIndex<toPortkeyArray.length;toPortIndex++)
		{
			ToPortGroup toPortgroup = scheduleGroup.get(toPortkeyArray[toPortIndex]);
			logger.info("\r\n"+toPortgroup+"\r\n");
			fw.write("\r\n"+toPortgroup+"\r\n\r\n");
			String[] fromPortArray = toPortgroup.keySet().toArray(new String[toPortgroup.keySet().size()]);

			// ����� �������� ����
			bubbleSort(fromPortArray);			
			for(int i=0;i<fromPortArray.length;i++)
			{
				FromPortGroup fromPortGroup =toPortgroup.get(fromPortArray[i]);
				logger.info("\t"+fromPortGroup);
				fw.write("\t"+fromPortGroup+"\r\n\r\n");
				String[] vesselArray = fromPortGroup.keySet().toArray(new String[fromPortGroup.keySet().size()]);
				bubbleSort(vesselArray);

				VesselGroup[] vesselArrays =new VesselGroup[vesselArray.length];
				for(int y=0;y<vesselArray.length;y++)
				{
					vesselArrays[y] =fromPortGroup.get(vesselArray[y]);
				}
				
				// ���� �׷� ����
				// ����Ϸ� ����
				Arrays.sort(vesselArrays);

				for(int y=0;y<vesselArrays.length;y++)
				{
					logger.debug(vesselArrays[y].toCommonShippingString());
					fw.write(vesselArrays[y].toCommonShippingString());
				}
			}
		}
		// ���� �ݱ�
		fw.close();
	}

	protected void initTag() {
		logger.info("�±����� �ʱ�ȭ");


		if(isApplyTag)
		{
			BOLD_TAG_B="<ct:>";

			BOLD_TAG_F="<ct:Bold Condensed>";

			TAG_VERSION0="<KSC5601-WIN>";

			TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<����:COLOR:CMYK:Process:0,0,0,1>>";

			TAG_VERSION3="<dps:����=<Nextstyle:����><cc:����><cs:8.000000><clig:0><cbs:-0.000000><phll:0><pli:53.858291><pfli:-53.858292><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:����><prat:100.000000><prbc:����><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";		

			TAG_VERSION4="<dps:Body Text=<BasedOn:����><Nextstyle:Body Text><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";


			TAG_VERSION5="<pstyle:Body Text><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cs:8.000000><cl:5.479995><cf:Helvetica LT Std><ct:Roman>\r\n";			
		}

		try {
			Code param = new Code();
			param.setCode_type(XML_INFO.XML_TAG_FROM_PORT);
			List<Code> li = baseService.getCodeInfoList(param);

			fromPort = new String[li.size()];
			for(int i=0;i<li.size();i++)
			{
				Code info = li.get(i);
				fromPort[i] =info.getCode_name();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	class ScheduleGroup extends HashMap<String, ToPortGroup>
	{
		private static final long serialVersionUID = 1L;

		public void add(ScheduleData data) throws SQLException
		{
			if(this.containsKey(data.getPort()))
			{
				ToPortGroup group = this.get(data.getPort());
				// Ű : �����-�����-���ڸ�-������
				group.addSchedule(data);

			}else
			{
				// �ű� �׷� ����
				// Ű : �����-�����-���ڸ�-������
				//logger.debug("toPort create:"+data.getPort());
				this.put(data.getPort(), new ToPortGroup(data));
			}
		}
	}

	class ToPortGroup extends HashMap<String,FromPortGroup>
	{
		private String toPort;

		public ToPortGroup(ScheduleData data) throws SQLException {
			this.toPort =data.getPort();
			this.addSchedule(data);
		}
		public void addSchedule(ScheduleData data) throws SQLException {
			// �������� ������ ��� �߰�
			if(this.containsKey(data.getFromPort()))
			{
				FromPortGroup group = this.get(data.getFromPort());
				// Ű : �����-�����-���ڸ�-������
				group.addSchedule(data);

			}else// �ű� �׷� ����
			{
				// Ű : �����-�����-���ڸ�-������				
				FromPortGroup group = new FromPortGroup(data);				
				this.put(group.getID(), group);
			}

		}
		public String toString()
		{
			return toPort;
		}

	}
	/**
	 * @���� ���ڸ��� ���� �����ٵ��� �׷�, ���ǿ� ���� �����輱 �ǽ�
	 * @author archehyun
	 *
	 */
	class VesselGroup extends  ArrayList<ScheduleData> implements Comparable<Object>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int use;// ���� ��� ����
		ArrayList<String> companyList;
		private String vessel;// ���ڸ�
		private String dateF; // �����
		private String dateT; // ������
		private String commpany;  //����
		private String commonDateT;  // �����輱 �����
		private String agent; // ������Ʈ
		boolean isGroupedFormerSchedule;
		private SimpleDateFormat outputFormat = new SimpleDateFormat("M/d");
		private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
		
		public int isUse() {
			return use;
		}

		public void setUse(int use) {
			this.use = use;
		}
		private String vessel_type;
		public String getVessel_type() {
			return vessel_type;
		}

		public void setVessel_type(String vessel_type) {
			this.vessel_type = vessel_type;
		}
		
		/**
		 * @rule1 ������ ������Ʈ ���� ���� ��쿡�� ����� ���
		 * @rule2 �����輱���� ������ ��� ��ǥ ����
		 * @param dateF
		 * @param vessel
		 * @param company
		 * @param agent
		 * @param dateT
		 * @return
		 */
		private String toShippingString(String dateF, String vessel, String company, String agent, String dateT)
		{
			// 
			try {

				String formatedDateF=outputFormat.format(inputFormat.parse(dateF));
				String formatedDateT=outputFormat.format(inputFormat.parse(dateT));
				String formatedCompany = company.equals(agent)?company:company+"/"+agent;
				return  "\t"+formatedDateF+
						"\t"+vessel+
						"\t"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+
						"("+formatedCompany+")"+
						"\t"+formatedDateT+"\r\n";
			} catch (ParseException e) {

				e.printStackTrace();
				return "date error";
			}

		}
		private String toShippingString(ScheduleData data,ArrayList<String> companyList)
		{
			String company=null;
			if(this.getVessel_company()!=null)
			{

				company=arrangedCompanyList(this.getVessel_company(),companyList);
			}else
			{
				company=arrangedCompanyList(companyList);
			}

			return this.toShippingString(data.getDateF(), data.getVessel(), company, data.getAgent(), data.getDateT());
		}
		private String toShippingString(ScheduleData data)
		{
			return this.toShippingString(data.getDateF(), data.getVessel(), data.getCompany_abbr(), data.getAgent(), data.getDateT());
		}

		/**@���� ���ڷε� ������ȣ���� ���ڸ� �����ϴ� �޼ҵ�
		 * @param voyage_num
		 * @return
		 */
		private int getNumericVoyage(String voyage_num)
		{

			int result=0;

			String temp="";
			if(voyage_num==null)
				return 0;
			for(int i=0;i<voyage_num.length();i++)
			{
				try{
					temp+=Integer.parseInt(String.valueOf(voyage_num.charAt(i)));
				}catch(NumberFormatException e)
				{

				}
			}
			try{
				result=Integer.valueOf(temp);
			}catch(Exception e)
			{
				return 0;
			}

			return result;
		}

		/**
		 * @���� �����輱 �������θ� ����
		 * @param itemFirst
		 * @param itemSecond
		 * @return
		 */
		private boolean isMakeCommonShipping(ScheduleData itemFirst,ScheduleData itemSecond)
		{
			if(itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // ������� ����
					this.getNumericVoyage(itemFirst.getVoyage_num())==this.getNumericVoyage(itemSecond.getVoyage_num())&& //�������� ����
					KSGDateUtil.isThreeDayUnder(itemFirst.getDateF(),itemSecond.getDateF())) //3�� �̳�
			{
				return true;
			}
			else if(!itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // ������� �ٸ���
					this.getNumericVoyage(itemFirst.getVoyage_num())==this.getNumericVoyage(itemSecond.getVoyage_num())&& //�������� ����
					KSGDateUtil.isThreeDayUnder(itemFirst.getDateF(),itemSecond.getDateF())||//3�� �̳� �̰ų�
					KSGDateUtil.isSame(itemFirst.getDateF(),itemSecond.getDateF())// ������� ���ٸ�
					) //3�� �̳�
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		

		private void addCompany(String commpany)
		{
			boolean isAdd=true;

			for(int i=0;i<companyList.size();i++)
			{
				if(companyList.get(i).equals(commpany))
					isAdd=false;
			}

			if(isAdd)
			{
				companyList.add(commpany);
			}
		}
		@SuppressWarnings("unchecked")
		private String toCommonShippingString()
		{
			if(this.size()==1)
			{
				ScheduleData data =this.get(0);
				dateF = data.getDateF();
				dateT = data.getDateT();
				commpany = data.getCompany_abbr();
				agent = data.getAgent();
				return toShippingString(dateF,vessel,commpany,agent,dateT);
			}
			else
			{				
				Collections.sort(this);
				String list="";
				boolean isCommonThree=false;

				isGroupedFormerSchedule=false;
				ScheduleData commonSchedule=null;

				ArrayList<String> lists = new ArrayList<String>();
				for(int i=0;i<this.size();i++)
				{
					ScheduleData itemFirst = this.get(i);
					logger.info("-"+this.toShippingString(itemFirst));
				}

				for(int i=0;i<this.size()-1;i++)
				{

					// �� �ϱ� ���� ������ ��ȸ
					ScheduleData itemFirst = this.get(i);
					ScheduleData itemSecond = this.get(i+1);

					// �����輱 ���� ���� Ȯ��
					if(isMakeCommonShipping((isGroupedFormerSchedule?commonSchedule:itemFirst),itemSecond))					
					{	
						commonSchedule = itemSecond; // �����輱���� ���� ��� �ӽ� ������ Ŭ������ �Ҵ�
						isGroupedFormerSchedule=true;// �����輱���� �������� ���� ����
						isCommonThree = true;// �����輱 �ĺ���
						commonDateT = KSGDateUtil.rowerDate(itemFirst.getDateT(), commonSchedule.getDateT());
						this.addCompany(itemSecond.getCompany_abbr());

					}else
					{	
						//������ �����輱���� ���� ����� �ִ� ���
						if(isGroupedFormerSchedule)
						{
							commonSchedule.setDateT(commonDateT);

							lists.add((isCommonThree?"*":"")+this.toShippingString(commonSchedule,companyList));
							companyList.clear();

						}else //������ �����輱���� ���� ����� ���� ���
						{
							lists.add(this.toShippingString(itemFirst));
						}
						isGroupedFormerSchedule=false;
					}

					// ������ ������ ���
					if(i==this.size()-2)
					{
						if(isGroupedFormerSchedule)
						{
							commonSchedule.setDateT(commonDateT);
							lists.add((isCommonThree?"*":"")+this.toShippingString(commonSchedule,companyList));
							companyList.clear();
						}else
						{
							lists.add(this.toShippingString(itemSecond));
						}
					}
				}
				for(int i=0;i<lists.size();i++)
				{
					if(lists.get(i).startsWith("*"))
						logger.info(lists.get(i));
					list+=lists.get(i);
				}

				return list;
			}
		}
		private String vessel_company;// ����ȸ��


		public String getVessel_company() {
			return vessel_company;
		}

		public void setVessel_company(String vessel_company) {
			this.vessel_company = vessel_company;
		}

		public VesselGroup(ScheduleData data) throws SQLException {
			this.vessel = data.getVessel();
			//��ǥ ���� �˻�
			Vessel op = new Vessel();
			op.setVessel_name(data.getVessel());

			Vessel searchedVessel = baseService.getVesselInfo(op);
			this.vessel_type = searchedVessel.getVessel_type()!=null?searchedVessel.getVessel_type():"";
			if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
			{
				this.setVessel_company(searchedVessel.getVessel_company());
			}
			companyList = new ArrayList<String>();
			this.use = searchedVessel.getVessel_use();
			this.add(data);
		}

		public String getID() {
			return vessel;
		}
		public String toString()
		{	
			return toCommonShippingString();
		}

		public int compareTo(Object o) {
			VesselGroup table1 =(VesselGroup) o;

			try 
			{
				return KSGDateUtil.daysDiff( KSGDateUtil.toDate4(table1.getDateF()),KSGDateUtil.toDate4(this.getDateF()));
			} catch (ParseException e) 
			{

				return -1;
			}
		}
		public String getDateF()
		{
			Collections.sort(this);
			ScheduleData data = this.get(this.size()-1);
			dateF = data.getDateF();

			return dateF;
		}
		private String arrangedCompanyList(String major_company,
				ArrayList<String> companyList) {

			ArrayList<String> new_company = new ArrayList<String>();
			for(int i=0;i<companyList.size();i++)
			{
				if(major_company.compareToIgnoreCase(companyList.get(i))!=0)
				{
					new_company.add(companyList.get(i));
				}
			}

			return major_company+","+this.arrangedCompanyList(new_company);
		}

		private String arrangedCompanyList(ArrayList<String> companyList) {
			String[] array2=new String[companyList.size()];		

			companyList.toArray(array2);
			//���ĺ� ����
			Arrays.sort(array2, new StringCompare());

			String companyStringList ="";
			for(int i=0;i<array2.length;i++)
			{
				companyStringList+=array2[i];
				if(i!=array2.length-1)
					companyStringList+=",";
			}

			return companyStringList;
		}

	}
	class FromPortGroup extends HashMap<String,VesselGroup>
	{
		// �������� ������ ��� �߰�
		private String fromPort;

		public FromPortGroup(ScheduleData data) throws SQLException {
			this.fromPort =data.getFromPort();
			this.addSchedule(data);
		}
		public String getID() {
			// TODO Auto-generated method stub
			return fromPort;
		}
		public void addSchedule(ScheduleData data) throws SQLException {
			// �������� ������ ��� �߰�
			if(this.containsKey(data.getVessel()))
			{
				VesselGroup group = this.get(data.getVessel());
				// Ű : ���ڸ�
				group.add(data);

			}else// �ű� �׷� ����
			{
				// Ű : �����-�����-���ڸ�-������				
				VesselGroup group = new VesselGroup(data);				
				this.put(group.getID(), group);
			}

		}
		public String toString()
		{
			return "- "+fromPort+" -";
		}

	}



	public static void main(String[] args) throws SQLException, JDOMException, IOException, ParseException {
		new OutboundTaskV2().makeSchedule();

	}



}
