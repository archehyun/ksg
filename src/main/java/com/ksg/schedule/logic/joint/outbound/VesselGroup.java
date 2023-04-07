package com.ksg.schedule.logic.joint.outbound;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.exception.VesselNullException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.StringCompare;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.print.ScheduleBuildUtil;


/**

 * @FileName : VesselGroup.java

 * @Project : KSG2

 * @Date : 2022. 3. 14. 

 * @�ۼ��� : pch

 * @�����̷� :
 *   20220314: ������� �����Ϻ��� ���� ��� ������ ����(�����輱��)

 * @���α׷� ���� :

 */
public class VesselGroup extends  ArrayList<ScheduleData> implements Comparable<Object>{
	/**
	 * 
	 */
	protected Logger logger = LogManager.getLogger(this.getClass());
	public ScheduleManager scheduleManager = ScheduleManager.getInstance();
	private static final long serialVersionUID = 1L;
	private int use;// ���� ��� ����
	private ArrayList<String> companyList;
	private ArrayList<CompanyString> companyStringList;
	private String vessel;// ���ڸ�
	private String dateF; // �����
	private String dateT; // ������
	private String company;  //����
	private String commonDateT;  // �����輱 ������
	private String commonDateF;  // �����輱 �����
	private String major_agent; // ������Ʈ
	public String getMajor_agent() {
		return major_agent;
	}

	public void setMajor_agent(String major_agent) {
		this.major_agent = major_agent;
	}
	private boolean isGroupedFormerSchedule;


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
	 * @���� �����輱 �������θ� ����
	 * @param itemFirst
	 * @param itemSecond
	 * @return
	 */
	private boolean isMakeCommonShipping(ScheduleData itemFirst,ScheduleData itemSecond)
	{
		if(itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // ������� ����
				ScheduleBuildUtil.getNumericVoyage(itemFirst.getVoyage_num())==ScheduleBuildUtil.getNumericVoyage(itemSecond.getVoyage_num())&& //�������� ����
				KSGDateUtil.isThreeDayUnder(itemFirst.getDateF(),itemSecond.getDateF())) //3�� �̳�
		{
			return true;
		}
		else if(!itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // ������� �ٸ���
				ScheduleBuildUtil.getNumericVoyage(itemFirst.getVoyage_num())==ScheduleBuildUtil.getNumericVoyage(itemSecond.getVoyage_num())&& //�������� ����
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


	/**
	 * @param commpany
	 */
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
	/**		 * 
	 * ��ǥ ���� ����
	 * @param company
	 * @param agent
	 * @throws Exception 
	 */
	private void addCompany(String company,String agent) 
	{
		boolean isAdd=true;

		for(int i=0;i<companyStringList.size();i++)
		{
			if(companyStringList.get(i).getCompanyName().equals(company))
				isAdd=false;
		}

		if(isAdd)
		{
			companyStringList.add(new CompanyString(company, agent));
		}
	}
	/**
	 * @param jointScheduleList
	 * @param scheduleData
	 */
	private void addJointedSchedule(List jointScheduleList, PrintItem scheduleData)
	{


		//�����輱 �� : ������� �����Ϻ��� ������ �����ٿ��� ����
		if(KSGDateUtil.dayDiff(scheduleData.getDateF(), scheduleData.getDateT())>-1)
			jointScheduleList.add(scheduleData);

	}
	/**
	 * 
	 * �����輱�� ����� ������ ����Ʈ ��ȸ
	 * @return
	 * @throws CompanyNameNullExcption 
	 */
	public ArrayList<PrintItem> getJointedVesselList() 
	{
		HashMap<String, ArrayList<ScheduleData>> sulist = new HashMap<String, ArrayList<ScheduleData>>();

		ArrayList<PrintItem> list = new ArrayList<PrintItem>();


		//���ڸ� ������ȣ�� ���� �����ٳ��� �׷�ȭ
		for(int i=0;i<this.size();i++)
		{
			ScheduleData data = this.get(i);
			String vesselName = data.getVessel();
			int voyageNum = ScheduleBuildUtil.getNumericVoyage(data.getVoyage_num());

			if(sulist.containsKey(vesselName+"-"+voyageNum))
			{
				ArrayList<ScheduleData> sub = sulist.get(vesselName+"-"+voyageNum);
				sub.add(data);
			}
			else
			{
				ArrayList<ScheduleData> sub = new ArrayList<ScheduleData>();
				sub.add(data);					
				sulist.put(vesselName+"-"+voyageNum, sub);	
			}				
		}
		Set<String> keySet = sulist.keySet();
		Iterator<String> keyIter = keySet.iterator();
		while(keyIter.hasNext())
		{
			String key = keyIter.next();
			ArrayList<ScheduleData> datas=sulist.get(key);
			if(datas.size()==1)
			{
				ScheduleData data =datas.get(0);

				dateF = data.getDateF();
				dateT = data.getDateT();
				logger.debug("�Ϲ� ������:"+vessel+",datef:"+dateF+",dateT:"+dateT+","+data.getVoyage_num());
				company = data.getCompany_abbr();
				PrintItem item = new PrintItem(dateF, vessel, vessel_type, new CompanyString(company, data.getAgent()).toString(), dateT);

				addJointedSchedule(list,item);
				//list.add(item);
			}
			else if(datas.size()>1)
			{
				logger.info("���ڸ�:"+vessel+", ����:"+datas.get(0).getVoyage_num()+ " �����輱 ����:"+datas.size());
				// ��ü ��� ���
				logger.info(datas);
				Collections.sort(datas);


				isGroupedFormerSchedule=false;

				ScheduleData commonSchedule=null;					


				for(int i=0;i<datas.size()-1;i++)
				{
					// �� �ϱ� ���� ������ ��ȸ
					ScheduleData itemFirst = datas.get(i);
					ScheduleData itemSecond = datas.get(i+1);


					// �����輱 ���� ���� Ȯ��
					if(isMakeCommonShipping((isGroupedFormerSchedule?commonSchedule:itemFirst),itemSecond))					
					{	
						commonSchedule = itemSecond; // �����輱���� ���� ��� �ӽ� ������ Ŭ������ �Ҵ�

						// ������ �����輱���� �����ٸ� �����輱 ��¥�� ��
						if(isGroupedFormerSchedule)
						{
							commonDateT = KSGDateUtil.rowerDate(itemFirst.getDateT(), commonDateT);
							commonDateF =KSGDateUtil.upperDate(itemFirst.getDateF(), commonDateF);	
						}
						// �ƴ϶�� �׸� ��
						else
						{
							commonDateT = KSGDateUtil.rowerDate(itemFirst.getDateT(), itemSecond.getDateT());								

							commonDateF =KSGDateUtil.upperDate(itemFirst.getDateF(), itemSecond.getDateF());
						}

						commonDateT = KSGDateUtil.rowerDate(itemFirst.getDateT(), commonDateT);
						commonDateF =KSGDateUtil.upperDate(itemFirst.getDateF(), commonDateF);

						this.addCompany(itemFirst.getCompany_abbr());							
						this.addCompany(itemSecond.getCompany_abbr());



						this.addCompany(itemFirst.getCompany_abbr(), itemFirst.getAgent());
						this.addCompany(itemSecond.getCompany_abbr(), itemSecond.getAgent());

						isGroupedFormerSchedule=true;// �����輱���� �������� ���� ����

					}else
					{	
						logger.info("�����輱 �ƴ�");
						//������ �����輱���� ���� ����� �ִ� ���
						if(isGroupedFormerSchedule)
						{
							commonSchedule.setDateT(commonDateT);
							commonSchedule.setDateF(commonDateF);
							logger.info("!�����輱:"+commonDateT);

							String company=null;
							if(this.getVessel_company()!=null)
							{								
								company=toCompanyString(new CompanyString(getVessel_company(), getMajor_agent()),companyStringList);
							}else
							{
								company=toCompanyString(companyStringList);
							}

							logger.info("�����輱:"+this.vessel+","+company);
							PrintItem item = new PrintItem(commonSchedule.getDateF(), commonSchedule.getVessel(), vessel_type, company,  commonSchedule.getDateT());
							addJointedSchedule(list,item);
							//list.add(item);
							companyStringList.clear();
							companyList.clear();

						}else //������ �����輱���� ���� ����� ���� ���
						{
							logger.info("�ܵ� ������ �߰�:" +itemFirst.getVessel() +", "+itemFirst.getDateF()+", "+itemFirst.getDateT());
							PrintItem item = new PrintItem(itemFirst.getDateF(), itemFirst.getVessel(), vessel_type, new CompanyString( itemFirst.getCompany_abbr(), itemFirst.getAgent()).toString(), itemFirst.getDateT());
							addJointedSchedule(list,item);
							//list.add(item);

						}
						isGroupedFormerSchedule=false;
					}

					// ������ ������ ���
					if(i==datas.size()-2)
					{
						PrintItem item;
						if(isGroupedFormerSchedule)
						{	
							String lastDateT = datas.get(datas.size()-1).getDateT();
							String lastDateF = datas.get(datas.size()-1).getDateF();

							commonDateT = KSGDateUtil.rowerDate(lastDateT, commonDateT);
							commonDateF =KSGDateUtil.upperDate(lastDateF, commonDateF);

							logger.info("������ �����輱 ������ ���:"+commonDateF+" - "+commonDateT);

							commonSchedule.setDateT(commonDateT);
							commonSchedule.setDateF(commonDateF);

							String company=null;;
							if(this.getVessel_company()!=null)
							{								
								company=toCompanyString(new CompanyString(getVessel_company(), getMajor_agent()),companyStringList);
							}else
							{
								company=toCompanyString(companyStringList);
							}
							item = new PrintItem(commonSchedule.getDateF(), commonSchedule.getVessel(), vessel_type, company, commonSchedule.getDateT());
							addJointedSchedule(list,item);
							//list.add(item);
							companyStringList.clear();
							companyList.clear();
						}else
						{
							item = new PrintItem(itemSecond.getDateF(), itemSecond.getVessel(), vessel_type, new CompanyString( itemSecond.getCompany_abbr(), itemSecond.getAgent()).toString(), itemSecond.getDateT());
							addJointedSchedule(list,item);
							//list.add(item);
							logger.info("������ ������ �߰�:"+item.getDateF()+", "+item.getVessel_name() +", "+item.getDateT());
						}

					}
				}
				logger.info("�����輱 ���� ��\n\n");
			}
		}

		return list;
	}

	private String vessel_company;// ����ȸ��


	/**
	 * @return
	 */
	public String getVessel_company() {
		return vessel_company;
	}

	/**��ǥ ���� �Ҵ�
	 * @param vessel_company
	 */
	public void setVessel_company(String vessel_company) {
		this.vessel_company = vessel_company;
	}

	public VesselGroup(ScheduleData data) throws SQLException, VesselNullException {
		this.vessel = data.getVessel();
		//��ǥ ���� �˻�

		Vessel searchedVessel =scheduleManager.searchVessel(data.getVessel());

		this.vessel_type = searchedVessel.getVessel_type()!=null?searchedVessel.getVessel_type():"";

		if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
		{
			this.setVessel_company(searchedVessel.getVessel_company());
		}
		companyList = new ArrayList<String>();
		companyStringList = new ArrayList<CompanyString>();
		this.use = searchedVessel.getVessel_use();
		this.add(data);

	}

	public String getID() {
		return vessel;
	}

	public int compareTo(Object o) {
		VesselGroup table1 =(VesselGroup) o;

		Date one = new Date(table1.getDateF());
		Date two = new Date(this.getDateF());

		return KSGDateUtil.daysDiff( one,two);

	}
	public String getDateF()
	{
		Collections.sort(this);
		ScheduleData data = this.get(this.size()-1);
		dateF = data.getDateF();

		return dateF;
	}
	/**
	 * @param major_company
	 * @param companyStrings
	 * @return
	 */
	private String toCompanyString(CompanyString major_company,ArrayList<CompanyString> companyStrings)
	{
		ArrayList<CompanyString> new_company = new ArrayList<CompanyString>();
		for(int i=0;i<companyList.size();i++)
		{
			if(major_company.getCompanyName().compareToIgnoreCase(companyStrings.get(i).getCompanyName())!=0)
			{
				new_company.add(companyStrings.get(i));
			}
		}

		return major_company+","+toCompanyString(new_company);

	}		

	/**
	 * @param companyStrings
	 * @return
	 */
	private String toCompanyString(ArrayList<CompanyString> companyStrings)
	{
		CompanyString[] companyArray=new CompanyString[companyStrings.size()];
		for(int i=0;i<companyStrings.size();i++)
		{
			companyArray[i] = companyStrings.get(i);
		}
		Arrays.sort(companyArray);
		String companyStringList ="";

		for(int i=0;i<companyArray.length;i++)
		{
			companyStringList+=companyArray[i];
			if(i!=companyArray.length-1)
				companyStringList+=",";
		}

		return companyStringList;
	}

	/**
	 * 
	 * ��ǥ ���� ���� ��� ��ǥ ���縦 �� ó�� ǥ��
	 * @param major_company
	 * @param companyList
	 * @return
	 */
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
		logger.debug("major:"+major_company);

		return major_company+","+this.arrangedCompanyList(new_company);
	}

	/**
	 * 
	 * @���� ���� ����� ����
	 * @param companyList
	 * @return
	 */
	private String arrangedCompanyList(ArrayList<String> companyList) {
		String[] companyArray=new String[companyList.size()];		

		companyList.toArray(companyArray);
		//���ĺ� ����
		Arrays.sort(companyArray, new StringCompare());

		String companyStringList ="";

		for(int i=0;i<companyArray.length;i++)
		{
			companyStringList+=companyArray[i];
			if(i!=companyArray.length-1)
				companyStringList+=",";
		}

		return companyStringList;
	}
	/**
	 * ����� ǥ��
	 * ��Ģ1 : ������ agent���� ���� �ҽ� ����� �� ǥ��
	 * ��Ģ2 : agent�� null�̸� 
	 * 
	 * @author ��â��
	 *
	 */
	class CompanyString implements Comparable<CompanyString>{
		private String companyName;
		private String agent;
		public CompanyString(String companyName, String agent) {
			this.companyName = companyName;
			this.agent = agent;			
		}

		public String getCompanyName() {
			// TODO Auto-generated method stub
			return companyName;
		}


		/* 
		 * 2020-04-07
		 * ��ǥ ���� ǥ�� ���
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{	
			return companyName.equals(agent)?companyName:companyName+(agent!=null?("/"+agent):"");
		}

		public int compareTo(CompanyString o) {

			return this.getCompanyName().compareTo(o.getCompanyName());
		}
	}

}
