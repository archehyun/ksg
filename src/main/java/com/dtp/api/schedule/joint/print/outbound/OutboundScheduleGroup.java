package com.dtp.api.schedule.joint.print.outbound;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.dtp.api.schedule.joint.print.ScheduleGroup;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.workbench.schedule.comp.treenode.SortedSchedule;

/**
 * 

  * @FileName : OutboundScheduleGroup.java

  * @Project : KSG2

  * @Date : 2023. 4. 22. 

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� : �ƿ��ٿ ������ �����輱 ����
 */
public class OutboundScheduleGroup extends ScheduleGroup
{
	private static final String BUSAN_NEW 	= "BUSAN NEW";

	private static final String BUSAN 		= "BUSAN";
	
	private String dateT;
	
	private String jointedDateF;
	
	private String jointedDateT;
	
	private String jointed_company_abbr;
	
	/* YYYYMMdd */
	private  SimpleDateFormat formatYYYYMMDD = KSGDateUtil.dateFormat5;
	
	public ArrayList<SortedSchedule> sortedScheduleList;
	
	public OutboundScheduleGroup(ScheduleGroup parent, ArrayList<SortedSchedule> scheduleList) throws Exception
	{
		this.vessel = parent.getVessel();
		
		this.sortedScheduleList  = scheduleList;
		
		this.parent = parent;
		
		if(vessel == null) throw new Exception("vessel is null");
	}
	
	public OutboundScheduleGroup(Vessel vessel) throws Exception
	{
		sortedScheduleList = new ArrayList<SortedSchedule>();
		
		this.vessel =vessel;
		
		if(vessel == null) throw new Exception("vessel is null");
	}
	
	/**
	 * 
	 * @return jointedDateF
	 */
	public String getJointedDateF()
	{
		return jointedDateF;
	}
	
	public String getJointedDateT()
	{
		return jointedDateT;
	}
	
	public String getJointedCompanyName()
	{
		return jointed_company_abbr;
	}
	
	
	public void setParent(ScheduleGroup parent)
	{
		this.parent = parent;
	}
	

	public String getDateT()
	{
		return dateT;
	}
	/**
	 *   �λ��װ� �λ��� ������ ���� ���� ���
	 *   
	 * @return
	 */
	private boolean bothBusanAndBusanNew(List<ScheduleData> originScheduleList)
	{	
		Optional<ScheduleData> busanList= originScheduleList.stream().filter(o -> o.getFromPort().equals(BUSAN)).findFirst();
		
		Optional<ScheduleData> busanNewList= originScheduleList.stream().filter(o -> o.getFromPort().equals(BUSAN_NEW)).findFirst();
		
		return busanList.isPresent()&&busanNewList.isPresent();
	}
	
	
	private boolean isBusasnNewAndBusan(List<ScheduleData> sc)
	{
		 boolean busan = false;
		 
		 boolean busanNew = false;
		 
		 for(ScheduleData item:sc)
		 {
			 if(BUSAN.equals(item.getFromPort())) busan=true;
			 if(BUSAN_NEW .equals(item.getFromPort())) busanNew=true;
		 }
		 return busan&&busanNew;
	}
	
	/**
	 * �λ��װ� �λ� ������ ���� �ִٸ� ����� �������� �λ� ������ �ִ� �������� ��� ���� �λ� ���� ��¥�� ���� ���� ������ �����ٰ� ����
	 */
	public void joinnted() 
	{
		ArrayList<ScheduleData> originScheduleList = new ArrayList<ScheduleData>();
		
		sortedScheduleList.stream().forEach(o -> originScheduleList.add(o.getData()));
		
		String re_company_abbr = vessel.getVessel_company();
		
		// �λ��װ� �λ��� ������ ���� �ִ� ����
		
		// ����� �׷� ȭ ���� �������� �λ� �������� ���� ���� ��ü �׷�ȭ
		
		 Map<String, List<ScheduleData>> companymap =  originScheduleList.stream().collect(
				Collectors.groupingBy(ScheduleData::getCompany_abbr)); // �����
		 
		 ArrayList<String> fromDateList = new ArrayList<String>();
		 
		 ArrayList<String> toDateList = new ArrayList<String>(); 
		 
		 for(String company:companymap.keySet())
		 {
			 List<ScheduleData> sc= companymap.get(company);
			 
			 fromDateList.addAll(sc.stream()
					 	//	�λ�, �λ� ������ ���ÿ� ���� �ϴ��� �Ǵ�, ���� �ִ� ������ ��� �λ� ���� �����ٸ� ����
					 	.filter(o->isBusasnNewAndBusan(sc)?o.getFromPort().equals(BUSAN_NEW):true)
						.map(o -> o.getDateF())
						.collect(Collectors.toList()));
			 
			 toDateList.addAll(sc.stream()
					 	//	�λ�, �λ� ������ ���ÿ� ���� �ϴ��� �Ǵ�, ���� �ִ� ������ ��� �λ� ���� �����ٸ� ����
					 	.filter(o->isBusasnNewAndBusan(sc)?o.getFromPort().equals(BUSAN_NEW):true)
						.map(o -> o.getDateT())
						.collect(Collectors.toList()));
			 
		 }
		 
		 
		// ����� ���
		

		// ����� ����(���� ��¥)		
		dateF = getSortedDateOne(fromDateList, Comparator.reverseOrder());
		
		// ������ ����(���� ��¥)
		dateT = getSortedDateOne(toDateList, Comparator.naturalOrder());
		
		// ����� ����(���� ����)
		List<String> companyAbbrList=originScheduleList.stream()
											.filter(o -> !re_company_abbr.equals(o))
											.map( data-> (data.getCompany_abbr().equals(data.getAgent())?data.getCompany_abbr():data.getCompany_abbr()+"/"+data.getAgent()))
											.distinct()
											.sorted()
											.collect(Collectors.toList());
		
		// �����
		this.jointedDateF = KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD(dateF);

		// ������
		this.jointedDateT = KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD(dateT);		

		String sortedCompanyist =StringUtils.join(companyAbbrList.toArray(new String[companyAbbrList.size()]),",");
		
		
		// ����� ���2 (�����1, �����2, ...)
		
		// ����� ���2 (��ǥ�����, �����1, �����2, ...)
		
		if(sortedScheduleList.size()==1)
		{
			jointed_company_abbr  = sortedScheduleList.get(0).getData().getCompany_abbr()+"/"+sortedScheduleList.get(0).getData().getAgent(); 
		}
		
		else if(sortedScheduleList.size()>1)
		{
			jointed_company_abbr = re_company_abbr.isEmpty()?sortedCompanyist:re_company_abbr+","+sortedCompanyist;	
		}
		
		if(!re_company_abbr.isEmpty())
		{
			System.out.println(String.format("%s %s - %s",this.getVesselName(), sortedCompanyist, jointed_company_abbr));
		}
	}

	private String getSortedDateOne(List<String> fromDateList, Comparator<String> comparator) {
		
		Optional<String> lastDateF=fromDateList.stream()
				.map( o -> getForrmatedDate(o))
				.sorted(comparator) 
				.findFirst();
		return lastDateF.get();
	}
	
	public boolean isMulti()
	{
		return sortedScheduleList.size()>1;
	}
	
	/**
	 * 
	 * @return vesselName
	 */
	public String getVesselName()
	{
		return vessel.getVessel_name();
	}
	
	/**
	 * 
	 * @return vesselType
	 */
	public String getVesselType()
	{
		return vessel.getVessel_type();
	}

	public void add(SortedSchedule item)
	{
		this.sortedScheduleList.add(item);
	}

	@Override
	public int compareTo(ScheduleGroup o) {
		try {
			
			OutboundScheduleGroup two = (OutboundScheduleGroup) o;
			
			int fromDateGap = formatYYYYMMDD.parse(getDateF()).compareTo(formatYYYYMMDD.parse(o.getDateF()));
			
			int toDateGap 	= formatYYYYMMDD.parse(getDateT()).compareTo(formatYYYYMMDD.parse(two.getDateT()));
			
			int vessel = this.getVesselName().compareTo(two.getVesselName());
			
			/*
			 * ���ı��� : ����� -> ������ -> ���ڸ�
			 */
			return fromDateGap>0?1:(fromDateGap==0)?(toDateGap>0?1:(toDateGap==0?(vessel>0?1:-1):-1)):-1;
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isDateValidate() {
		
		try {
			return (KSGDateUtil.outputDateFormat.parse(getJointedDateF()).compareTo(KSGDateUtil.outputDateFormat.parse(getJointedDateT()))>0);
		} catch (ParseException e) {
			
			e.printStackTrace();
			return false;
		}
	}

}