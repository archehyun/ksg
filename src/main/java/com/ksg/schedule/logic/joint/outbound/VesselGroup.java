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

 * @작성자 : pch

 * @변경이력 :
 *   20220314: 출발일이 도착일보다 늦은 경우 스케줄 제외(공동배선시)

 * @프로그램 설명 :

 */
public class VesselGroup extends  ArrayList<ScheduleData> implements Comparable<Object>{
	/**
	 * 
	 */
	protected Logger logger = LogManager.getLogger(this.getClass());
	public ScheduleManager scheduleManager = ScheduleManager.getInstance();
	private static final long serialVersionUID = 1L;
	private int use;// 선박 사용 유무
	private ArrayList<String> companyList;
	private ArrayList<CompanyString> companyStringList;
	private String vessel;// 선박명
	private String dateF; // 출발일
	private String dateT; // 도착일
	private String company;  //선사
	private String commonDateT;  // 공동배선 도착일
	private String commonDateF;  // 공동배선 출발일
	private String major_agent; // 에이전트
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
	 * @설명 공동배선 생성여부를 결정
	 * @param itemFirst
	 * @param itemSecond
	 * @return
	 */
	private boolean isMakeCommonShipping(ScheduleData itemFirst,ScheduleData itemSecond)
	{
		if(itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // 선사명이 같고
				ScheduleBuildUtil.getNumericVoyage(itemFirst.getVoyage_num())==ScheduleBuildUtil.getNumericVoyage(itemSecond.getVoyage_num())&& //항차명이 같고
				KSGDateUtil.isThreeDayUnder(itemFirst.getDateF(),itemSecond.getDateF())) //3일 이내
		{
			return true;
		}
		else if(!itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // 선사명이 다르고
				ScheduleBuildUtil.getNumericVoyage(itemFirst.getVoyage_num())==ScheduleBuildUtil.getNumericVoyage(itemSecond.getVoyage_num())&& //항차명이 같고
				KSGDateUtil.isThreeDayUnder(itemFirst.getDateF(),itemSecond.getDateF())||//3일 이내 이거나
				KSGDateUtil.isSame(itemFirst.getDateF(),itemSecond.getDateF())// 출발일이 같다면
				) //3일 이내
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
	 * 대표 선사 지정
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


		//공동배선 룰 : 출발일이 도착일보다 빠르면 스케줄에서 제외
		if(KSGDateUtil.dayDiff(scheduleData.getDateF(), scheduleData.getDateT())>-1)
			jointScheduleList.add(scheduleData);

	}
	/**
	 * 
	 * 공동배선이 적용된 스케줄 리스트 조회
	 * @return
	 * @throws CompanyNameNullExcption 
	 */
	public ArrayList<PrintItem> getJointedVesselList() 
	{
		HashMap<String, ArrayList<ScheduleData>> sulist = new HashMap<String, ArrayList<ScheduleData>>();

		ArrayList<PrintItem> list = new ArrayList<PrintItem>();


		//선박명 항차번호가 같은 스케줄끼리 그룹화
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
				logger.debug("일반 스케줄:"+vessel+",datef:"+dateF+",dateT:"+dateT+","+data.getVoyage_num());
				company = data.getCompany_abbr();
				PrintItem item = new PrintItem(dateF, vessel, vessel_type, new CompanyString(company, data.getAgent()).toString(), dateT);

				addJointedSchedule(list,item);
				//list.add(item);
			}
			else if(datas.size()>1)
			{
				logger.info("선박명:"+vessel+", 항차:"+datas.get(0).getVoyage_num()+ " 공동배선 적용:"+datas.size());
				// 전체 목록 출력
				logger.info(datas);
				Collections.sort(datas);


				isGroupedFormerSchedule=false;

				ScheduleData commonSchedule=null;					


				for(int i=0;i<datas.size()-1;i++)
				{
					// 비교 하기 위한 스케줄 조회
					ScheduleData itemFirst = datas.get(i);
					ScheduleData itemSecond = datas.get(i+1);


					// 공동배선 생성 여부 확인
					if(isMakeCommonShipping((isGroupedFormerSchedule?commonSchedule:itemFirst),itemSecond))					
					{	
						commonSchedule = itemSecond; // 공동배선으로 묶일 경우 임시 스케줄 클래스에 할당

						// 이전에 공동배선으로 묶였다면 공동배선 날짜와 비교
						if(isGroupedFormerSchedule)
						{
							commonDateT = KSGDateUtil.rowerDate(itemFirst.getDateT(), commonDateT);
							commonDateF =KSGDateUtil.upperDate(itemFirst.getDateF(), commonDateF);	
						}
						// 아니라면 항목간 비교
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

						isGroupedFormerSchedule=true;// 공동배선으로 묶였는지 여부 저장

					}else
					{	
						logger.info("공동배선 아님");
						//이전에 공동배선으로 묶인 결과가 있는 경우
						if(isGroupedFormerSchedule)
						{
							commonSchedule.setDateT(commonDateT);
							commonSchedule.setDateF(commonDateF);
							logger.info("!공동배선:"+commonDateT);

							String company=null;
							if(this.getVessel_company()!=null)
							{								
								company=toCompanyString(new CompanyString(getVessel_company(), getMajor_agent()),companyStringList);
							}else
							{
								company=toCompanyString(companyStringList);
							}

							logger.info("공동배선:"+this.vessel+","+company);
							PrintItem item = new PrintItem(commonSchedule.getDateF(), commonSchedule.getVessel(), vessel_type, company,  commonSchedule.getDateT());
							addJointedSchedule(list,item);
							//list.add(item);
							companyStringList.clear();
							companyList.clear();

						}else //이전에 공동배선으로 묶인 결과가 없는 경우
						{
							logger.info("단독 스케줄 추가:" +itemFirst.getVessel() +", "+itemFirst.getDateF()+", "+itemFirst.getDateT());
							PrintItem item = new PrintItem(itemFirst.getDateF(), itemFirst.getVessel(), vessel_type, new CompanyString( itemFirst.getCompany_abbr(), itemFirst.getAgent()).toString(), itemFirst.getDateT());
							addJointedSchedule(list,item);
							//list.add(item);

						}
						isGroupedFormerSchedule=false;
					}

					// 마지막 스케줄 출력
					if(i==datas.size()-2)
					{
						PrintItem item;
						if(isGroupedFormerSchedule)
						{	
							String lastDateT = datas.get(datas.size()-1).getDateT();
							String lastDateF = datas.get(datas.size()-1).getDateF();

							commonDateT = KSGDateUtil.rowerDate(lastDateT, commonDateT);
							commonDateF =KSGDateUtil.upperDate(lastDateF, commonDateF);

							logger.info("마지막 공동배선 스케줄 출력:"+commonDateF+" - "+commonDateT);

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
							logger.info("마지막 스케줄 추가:"+item.getDateF()+", "+item.getVessel_name() +", "+item.getDateT());
						}

					}
				}
				logger.info("공동배선 적용 끝\n\n");
			}
		}

		return list;
	}

	private String vessel_company;// 선박회사


	/**
	 * @return
	 */
	public String getVessel_company() {
		return vessel_company;
	}

	/**대표 선사 할당
	 * @param vessel_company
	 */
	public void setVessel_company(String vessel_company) {
		this.vessel_company = vessel_company;
	}

	public VesselGroup(ScheduleData data) throws SQLException, VesselNullException {
		this.vessel = data.getVessel();
		//대표 선사 검색

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
	 * 대표 선사 있을 경우 대표 선사를 맨 처음 표시
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
	 * @설명 선사 목록을 정리
	 * @param companyList
	 * @return
	 */
	private String arrangedCompanyList(ArrayList<String> companyList) {
		String[] companyArray=new String[companyList.size()];		

		companyList.toArray(companyArray);
		//알파벳 정렬
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
	 * 선사명 표시
	 * 규칙1 : 선사명과 agent명이 동일 할시 선사명 만 표시
	 * 규칙2 : agent가 null이면 
	 * 
	 * @author 박창현
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
		 * 대표 선사 표시 기능
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
