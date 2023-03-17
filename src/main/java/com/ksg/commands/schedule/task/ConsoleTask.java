package com.ksg.commands.schedule.task;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.jdom.JDOMException;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ScheduleType;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;

import com.ksg.domain.Vessel;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

/**
 * 2014-11-20 ������Ʈ
 * @author ��â��
 *
 */
public class ConsoleTask extends SimpleTask{
	/**
	 * @���� ������ �׷� Ŭ����
	 * Ű: ������ �׷�(�����-�����-���ڸ�-������)
	 * @author archehyun
	 *
	 */
	class FromPortGroup extends HashMap<String,ScheduleGroup>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public FromPortGroup(ScheduleData data) {

			addSchedule(data);

		}
		public void addSchedule(ScheduleData data)
		{	
			// �������� ������ ��� �߰�
			if(this.containsKey(data.getFromPort()+"\r\n"+data.getDateF()+"\r\n"+data.getVessel()+"\r\n"+data.getDateT()))
			{
				ScheduleGroup group = this.get(data.getFromPort()+"\r\n"+data.getDateF()+"\r\n"+data.getVessel()+"\r\n"+data.getDateT());
				// Ű : �����-�����-���ڸ�-������
				group.addSchedule(data);

			}else// �ű� �׷� ����
			{	

				// Ű : �����-�����-���ڸ�-������				
				ScheduleGroup group = new ScheduleGroup(data);				
				this.put(group.getID(), group);
			}
		}
	}
	/**
	 * @���� ����� ���� ������Ʈ ����Ʈ �׷�ȭ Ŭ����
	 * @author archehyun
	 *
	 */
	class ScheduleGroup extends ArrayList<SortScheduleData> 
	{

		private static final long serialVersionUID = 1L;

		private String dateF,dateT,fromPort,vessel;	// �����, ������, �����,  ���ڸ�

		/**�ʱ�ȭ ������, ������ �����͸� ���ڷ� �����, �����, ������, ���ڸ��� �ʱ�ȭ�� �� ������Ʈ�� �߰���
		 * @param data
		 */
		public ScheduleGroup(ScheduleData data) {
			this.setFromPort(data.getFromPort());
			this.setDateF(data.getDateF());
			this.setVessel(data.getVessel());
			this.setDateT(data.getDateT());
			this.addSchedule(data);
			
			
		}
		public void addSchedule(ScheduleData data)
		{
			this.add(new SortScheduleData(data));
		}
		/**@���� Ŭ��¡ �ð� ������ ��/�� ������ ǥ�� 
		 * @param time Ŭ��¡ Ÿ��
		 * @return
		 */
		private String getClosingTime(String time) throws ArrayIndexOutOfBoundsException,NumberFormatException
		{	
			logger.debug("closing time parsing:"+time);


			// TBN Ȯ��		
			String newTime = time.trim();
			String tbnList[]={"TBN", "T.B.N", "T.B.N.", "T B N", "-TBN-","Tbn","tbn"};
			for(int i=0;i<tbnList.length;i++)
			{
				if(newTime.equals(tbnList[i]))
					return "TBN";
			}

			// '/'�� ���� ��� �׳� ���
			if(!time.contains("/"))
			{
				return time;
			}

			String[] timeGroup=time.split("/");
			String month =timeGroup[0];
			String dayString=timeGroup[1];
			char dayGroup[] = dayString.toCharArray();
			try{
				int  first= Integer.parseInt(String.valueOf(dayGroup[0]));
				int second= Integer.parseInt(String.valueOf(dayGroup[1]));
			}
			catch(NumberFormatException ee)
			{
				return Integer.parseInt(month)+"/"+ String.valueOf(dayGroup[0]);
			}
			catch(ArrayIndexOutOfBoundsException eee)
			{
				return Integer.parseInt(month)+"/"+ String.valueOf(dayGroup[0]);
			}

			return 	Integer.parseInt(month)+"/"+
			((String.valueOf(dayGroup[0]).equals("0"))?"":String.valueOf(dayGroup[0]))
			+dayGroup[1];

		}

		public String getDateF() {
			return dateF;
		}
		public String getDateT() {
			return dateT;
		}
		public String getFromPort() {
			return fromPort;
		}
		// �׷�ȭ Ű ���� ����: �����-�����-����-������
		public String getID()
		{
			return getFromPort()+"\r\n"+getDateF()+"\r\n"+getVessel()+"\r\n"+getDateT();
		}
		public String getVessel() {
			return vessel;
		}
		
		public void setDateF(String dateF) {
			this.dateF = dateF;
		}
		
		public void setDateT(String dateT) {
			this.dateT = dateT;
		}

		public void setFromPort(String fromPort) {
			this.fromPort = fromPort;
		}
		public void setVessel(String vessel) {
			this.vessel = vessel;
		}
		/**��� �� �����, �����, ���ڸ�, ������
		 * @return
		 * @throws SQLException 
		 */
		public String toScheduleInfo() throws SQLException
		{
			return toStringSchedueInfoByTag(portMap.get(getFromPort()), dateFormat.format(new Date(getDateF())), getVessel(), dateFormat.format(new Date(getDateT())));			
		}
		/**
		 * @���� ������Ʈ ����Ʈ ���� ����
		 * @param comapny_abbr 			���� ���
		 * @param d_time 				Document Ŭ��¡
		 * @param c_time 				Cargo Ŭ��¡ 
		 * @param console_info 			�ܼ�����(Page, CFS)
		 * @param console_print_type 	�ܼ� ��� Ÿ�� 0:Page, 1:CFS
		 * @������� ���� ��� 	��ť��Ʈ Ŭ��¡	ī�� Ŭ��¡ 	[Page] �ܼ�����
		 * @return
		 */
		private String toStringAgentInfoByTag(String comapny_abbr, String d_time, String c_time,String console_info, int console_print_type) throws ArrayIndexOutOfBoundsException,NumberFormatException		
		{
			return TAG_BODY_AGENT+"\t"+comapny_abbr+"\t"+getClosingTime(d_time)+"\t"+getClosingTime(c_time)+"\t"+(console_print_type==ScheduleType.CONSOLE_PAGE?"[Page] ":"")+console_info+TAG_BODY_AGENT_CLOSE;
		}

		/**
		 * @���� ������Ʈ ����Ʈ�� ��Ʈ�� ���·� ���� �ϴ� �޼ҵ�
		 * @return
		 */
		public String toStringAgentList()
		{
			StringBuffer buffer = new StringBuffer();
			Collections.sort(this);
			for(int i=0;i<this.size();i++)
			{
				ScheduleData data = this.get(i).getData();
				try{
					buffer.append(this.toStringAgentInfoByTag(	data.getCompany_abbr(),
							data.getD_time(),
							data.getC_time(),
							op.getConsole_print_type()==ScheduleType.CONSOLE_PAGE?data.getConsole_page():data.getConsole_cfs(),
									op.getConsole_print_type())+"\r\n");
				}catch(ArrayIndexOutOfBoundsException e)
				{
					logger.error("closing time index error:id:"+data.getTable_id()+","+data.getD_time()+","+data.getC_time());
				}	
				catch(NumberFormatException e)
				{
					logger.error("closing time number format error:id:"+data.getTable_id()+","+data.getD_time()+","+data.getC_time());
				}	
			}	
			return buffer.toString();
		}
		/**
		 * @���� �±׸� �����Ͽ� ���
		 * @param fromPort 		�����
		 * @param dateF 		�������
		 * @param vessel_abbr 	���ڸ� ���
		 * @param dateT 		������
		 * @������� [�����] �����	[VSL] ���ڸ��� [����]	[ETA] ������
		 * @return
		 * @throws SQLException 
		 */
		private String toStringSchedueInfoByTag(String fromPort, String dateF, String vessel_abbr, String dateT) throws SQLException
		{
			Vessel op = new Vessel();
			op.setVessel_name(vessel_abbr);
			Vessel  vesselInfo = baseService.getVesselInfo(op);

			String vesseltype=vesselInfo!=null?vesselInfo.getVessel_type():"error";
			// ������ ��� ���� ����
			String vessel = vesseltype.equals("")?"":"["+vesseltype+"]";

			return TAG_BODY_FROM_PORT+"["+fromPort+"] "+dateF+"\t"+vessel_abbr+" "+vessel+"\t[ETA] "+dateT;
		}		
	}
	class SortScheduleData implements Comparable<SortScheduleData>
	{
		ScheduleData data;
		public ScheduleData getData() {
			return data;
		}
		public void setData(ScheduleData data) {
			this.data = data;
		}
		public SortScheduleData(ScheduleData data) {
			this.data=data;
		}
		public String toString()
		{
			return data.getAgent();
		}
		public int compareTo(SortScheduleData o) {
			return this.getData().getAgent().toUpperCase().compareTo(o.getData().getAgent().toUpperCase());
		}
	}
	/**
	 * ������ �׷�
	 * Ű : toPort
	 * @author ��â��
	 * @���� ������ �׷� Ŭ����, �������� key�� �ϴ� �ؽ��� ���� ��ü value ���� ����� �׷� ��ü�� ������
	 *
	 */
	class ToPortGroup extends HashMap<String, FromPortGroup>
	{

		private static final long serialVersionUID = 1L;

		public void add(ScheduleData data)
		{
			if(this.containsKey(data.getPort()))
			{
				FromPortGroup group = this.get(data.getPort());
				// Ű : �����-�����-���ڸ�-������
				group.addSchedule(data);

			}else
			{
				// �ű� �׷� ����
				// Ű : �����-�����-���ڸ�-������
				
				this.put(data.getPort(), new FromPortGroup(data));
			}
		}
	}


	private Calendar dateIssue;
	
	private SimpleDateFormat dateIssueformat 	= new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat inputDateformat 	= new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat dateFormat 		= new SimpleDateFormat("M/d");
	private ScheduleBuildMessageDialog di;
	private String fileName;
	private DAOManager manager =DAOManager.getInstance();
	private ScheduleData op; // �ܼ� ������ ��ȸ �ɼ� ����
	private String TAG_BODY_AGENT;
	private String TAG_BODY_AGENT_CLOSE;
	private String TAG_BODY_FROM_PORT;
	private String TAG_BODY_TO_PORT;
	private String TAG_DOCUMENT_INFO_1;
	private String TAG_DOCUMENT_INFO_2;
	private String TAG_HEAD_TO_PORT;
	private final String TAG_VERSION="<KSC5601-WIN>";

	public ConsoleTask(ScheduleData op) {
		super();
		this.op=op;
		try {

			// ���� ���� �̸� ����
			fileName = KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+"console.txt";

			dateIssue = Calendar.getInstance();
			dateIssue.setTime(dateIssueformat.parse(op.getDate_issue()));

			initTag();
			makeSchedule();
			JOptionPane.showMessageDialog(null, "���� �Ϸ�");

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "���� ����");

		}
		finally
		{
			done=true;
			di.setVisible(false);
			di.dispose();
		}
	}


	protected void initTag() {
		logger.debug("������ �±� �ʱ�ȭ");
		TAG_BODY_AGENT ="<ct:><cs:><chs:><cl:><cf:><ptr:><ptr:8\\,Left\\,.\\,0\\,\\;63\\,Left\\,.\\,0\\,\\;86\\,Left\\,.\\,0\\,\\;107\\,Left\\,.\\,0\\,\\;><cc:C\\=0 M\\=0 Y\\=0 K\\=60><ct:30><cs:6.000000><chs:0.900000><cl:8.000000><cf:Yoon���� �����100\\_TT>";
		TAG_BODY_AGENT_CLOSE="<cc:>";
		TAG_BODY_FROM_PORT ="<ct:><cs:><chs:><cl:><cf:><ptr:><ptr:44\\,Left\\,.\\,0\\,\\;107\\,Left\\,.\\,0\\,\\;><ct:Regular><cs:6.000000><chs:0.900000><cl:8.000000><cf:Rix������ B>";		
		TAG_BODY_TO_PORT ="<ct:><cs:><chs:><cl:><cf:><ptr:><ptr:8\\,Left\\,.\\,0\\,\\;63\\,Left\\,.\\,0\\,\\;86\\,Left\\,.\\,0\\,\\;107\\,Left\\,.\\,0\\,\\;><ct:Bold><cs:8.000000><cl:8.000000><cf:Helvetica LT Std>";
		TAG_DOCUMENT_INFO_1="<vsn:8><fset:InDesign-Roman><ctable:=<Black:COLOR:CMYK:Process:0,0,0,1><C\\=0 M\\=0 Y\\=0 K\\=60:COLOR:CMYK:Process:0,0,0,0.6>>";
		TAG_DOCUMENT_INFO_2="<dps:NormalParagraphStyle=<Nextstyle:NormalParagraphStyle>>";		
		TAG_HEAD_TO_PORT ="<pstyle:NormalParagraphStyle><ptr:8\\,Left\\,.\\,0\\,\\;63\\,Left\\,.\\,0\\,\\;86\\,Left\\,.\\,0\\,\\;107\\,Left\\,.\\,0\\,\\;><ct:Bold><cs:8.000000><cl:8.000000><cf:Helvetica LT Std>";
	}
	/**
	 * @���� ��¥ ��  �־��� ���ڰ� ���� ���� ���� ���� ���� �� 
	 * (ex1: dateIssue:3.1, dateF:3.2=>false)
	 * (ex2: dateIssue:3.1, dateF:3.1=>false)
	 * (ex3: dateIssue:3.2, dateF:3.1=>true)
	 * @param dateF ���� ����
	 * @return
	 * @throws ParseException
	 */
	private boolean isAfterThenDateIssue(String dateF) throws ParseException
	{		
		Calendar dateFCal = Calendar.getInstance();
		dateFCal.setTime(inputDateformat.parse(dateF));

		return dateIssue.after(dateFCal);

	}
	/*	public static void main(String[] args) throws ParseException 
	{
		SimpleDateFormat inputDateformat 	= new SimpleDateFormat("yyyy/MM/dd");
		Calendar dateFCal = Calendar.getInstance();
		dateFCal.setTime(inputDateformat.parse("2015/03/02"));

		Calendar dateIssue;
		dateIssue = Calendar.getInstance();
		SimpleDateFormat dateIssueformat 	= new SimpleDateFormat("yyyy-MM-dd");
		dateIssue.setTime(dateIssueformat.parse("2015-03-02"));

		System.out.println(dateIssue.after(dateFCal));
	}*/



	/* (non-Javadoc)
	 * @see com.ksg.commands.schedule.task.ScheduleTask#makeSchedule()
	 * @ ������ ���� �޼ҵ�
	 */
	public void makeSchedule() throws SQLException, JDOMException, IOException {

		// �ƿ� �ٿ�� �ױ� ��� ��ȸ
		logger.debug("�ܼ� ������ ���� ����");

		// �ݼ� ������ ��ȸ
		
		
		//List<ScheduleData> scheduleli = scheduleService.getConsoleScheduleList(op);
		
		op.setGubun(ScheduleType.GUBUN_CONSOLE);
		
		op.setOrderby("port, fromPort, DateF,DateT");
		
		List<ScheduleData> scheduleli = scheduleService.getScheduleList(op);
		
		logger.debug("�ܼ� ������ ��ȸ:"+scheduleli.size()+"��");

		lengthOfTask = scheduleli.size();
		di = new ScheduleBuildMessageDialog (this);
		di.setMessage("Console");
		di.createAndUpdateUI();
		logger.debug("date_issue:"+op.getDate_issue());


		for(int i=0;i<scheduleli.size();i++)
		{

			ScheduleData data = scheduleli.get(i);
			try {
				logger.debug(data.getDateF()+","+dateIssueformat.format(inputDateformat.parse(data.getDateF()))+","+isAfterThenDateIssue(data.getDateF()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}		

		ToPortGroup toPortGroupList = new ToPortGroup();
		Iterator<ScheduleData> iter = scheduleli.iterator();

		/* ������ ��ȸ �� ����
		 * ��ȸ�� ��������  ToPort �׷쿡 �߰� 
		 */
		while(iter.hasNext())
		{
			ScheduleData item = iter.next();
			try {
				// ���� ���� ���� ������ �� ������ ó��
				if(!isAfterThenDateIssue(item.getDateF()))
				{
					toPortGroupList.add(item);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}	

		FileWriter fw = new FileWriter(fileName);


		// ��� ���μ���
		// toPort �׷쿡�� Ű ��(������) ��ȸ


		// �±� ���� ���
		fw.write(TAG_VERSION+"\r\n");
		fw.write(TAG_DOCUMENT_INFO_1+"\r\n");
		fw.write(TAG_DOCUMENT_INFO_2+"\r\n");

		String[] toPortkeyArray = toPortGroupList.keySet().toArray(new String[toPortGroupList.keySet().size()]);

		// ������ Ű�� ����
		bubbleSort(toPortkeyArray);
		for(int toPortIndex=0;toPortIndex<toPortkeyArray.length;toPortIndex++)
		{
			logger.debug("toPort:"+toPortkeyArray[toPortIndex]);
			PortInfo info=baseService.getPortInfo(toPortkeyArray[toPortIndex]);
			// ���� üũ
			if(info==null)// �������� �ʴ� �ױ� �� ��� �н�
			{
				logger.error(toPortkeyArray[toPortIndex]+":"+info);
				continue;
			}
			fw.write("\r\n"+(toPortIndex==0?TAG_HEAD_TO_PORT:TAG_BODY_TO_PORT)+toPortkeyArray[toPortIndex]+" , "+info.getPort_nationality() + "\r\n\r\n");

			// ������ �������� ����� �׷� ��ȸ
			FromPortGroup group = toPortGroupList.get(toPortkeyArray[toPortIndex]);

			// ��¥���� �̿��Ͽ� ���� ����
			String[] keyArray = group.keySet().toArray(new String[group.keySet().size()]);
			bubbleSortByDate(keyArray);			
			current++;
			for(int i=0;i<keyArray.length;i++)
			{
				//�� ������Ʈ ���
				logger.debug("fromPort Key:"+keyArray[i]);
				ScheduleGroup subGroup =group.get(keyArray[i]);
				logger.debug(subGroup.toScheduleInfo());
				fw.write(subGroup.toScheduleInfo()+"\r\n");
				logger.debug(subGroup.toStringAgentList());
				fw.write(subGroup.toStringAgentList());
			}
		}		

		// ���� �ݱ�
		fw.close();
	}
	private void bubbleSortByDate(String[] intArray) {
		int out, in;
		for (out = intArray.length - 1; out > 0; out--) {
			for (in = 0; in < out; in++) {
				try{
					String[] oneDateList = intArray[in].split("\r\n");
					String[] twoDateList = intArray[in+1].split("\r\n");

					String onePort = oneDateList[0];
					String twoPort = twoDateList[0];
					String oneDateF =oneDateList[1];
					String twoDateF =twoDateList[1];

					String oneDateT =oneDateList[3];
					String twoDateT =twoDateList[3];


					// ����� ��
					if (onePort.compareTo(twoPort)>0) {
						swap(intArray, in, in + 1);
					}
					else if (onePort.compareTo(twoPort)==0)
					{				
						// ����� ��
						if(KSGDateUtil.biggerDate(oneDateF, twoDateF).equals(oneDateF)&&oneDateF.compareTo(twoDateF)!=0)
						{

							swap(intArray, in, in + 1);
						}
						else if(KSGDateUtil.biggerDate(oneDateF, twoDateF).equals(twoDateF))
						{
							if(oneDateF.compareTo(twoDateF)==0)
							{
								try{
									//������ ��
									if(KSGDateUtil.biggerDate(oneDateT, twoDateT).equals(oneDateT))
									{

										swap(intArray, in, in + 1);
									}
								}
								catch(Exception e)
								{

									e.printStackTrace();
									System.err.println("error one:"+intArray[in]+",two:"+ intArray[in+1]);
								}
							}
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.err.println("error one:"+intArray[in]+",two:"+ intArray[in+1]);
				}
			}
		}
	}
}

