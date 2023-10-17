package com.dtp.api.schedule.joint.print.inbound;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.dtp.api.schedule.joint.print.ScheduleGroup;
import com.dtp.api.schedule.joint.print.route.PortAndDay;
import com.dtp.api.schedule.joint.tree.comparator.ScheduleDateComparator;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.workbench.schedule.comp.treenode.InboundCodeMap;

/**
 * 
 * @FileName : InboundScheduleGroup.java
 * @Project : KSG2
 * @Date : 2023. 5. 16
 * @작성자 : pch
 * @변경이력 :
 * @프로그램 설명 :
 */
public class InboundScheduleGroup extends ScheduleGroup{
	
	private SimpleDateFormat inputDateFormat 	= KSGDateUtil.inputDateFormat;
	
	private String FROM_PORT_TAG1 = "<ct:><cf:><cs:><cs:8.000000><cf:Helvetica LT Std><ct:Bold>";

	private String FROM_DATE_TAG1 = "<ct:><cf:><cs:><cs:6.500000><cf:Helvetica LT Std><ct:Roman>";

	private String VESSEL_TAG ="\t<ct:><cf:><cf:Helvetica Neue LT Std>";

	private String BOLD_TAG_B="<ct:>";

	private String BOLD_TAG_F="<ct:77 Bold Condensed>";
	
	private InboundCodeMap inboundCodeMap = InboundCodeMap.getInstance();
	
	// "M/d"
	protected SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();

	private boolean isTaged;

	private List<PortAndDay> portAndDayList;

	private String sortedCompanyist;
	
	public InboundScheduleGroup(ScheduleGroup parent, ArrayList<ScheduleData> scheduleList) throws Exception
	{
		this(parent.getVessel(), scheduleList, false);
		
		this.parent = parent;
	}

	public InboundScheduleGroup(Vessel vessel, List<ScheduleData>scheduleList, boolean isTaged)
	{
		super(vessel, scheduleList);
		
		Collections.sort(this.scheduleList, new ScheduleDateComparator(ScheduleDateComparator.FROM_DATE));
		
		//외국항 출발일은 늦은 일자 적용
		this.dateF = this.scheduleList.get(scheduleList.size()-1).getDateF();
		
		portAndDayList 		= getJointedInboundPortList();
		
		this.dateT = portAndDayList.get(0).getDateF();
		
		sortedCompanyist 	= toJointedCompanyString();
		
		this.isTaged =isTaged;
	}
	
	/**
	 * 날짜 검증 dateT>dateF(?)
	 * @return
	 */
	public boolean isValidateDate() 
	{
		try
		{
			return inputDateFormat.parse(dateT).compareTo(inputDateFormat.parse(dateF))>0;
		}catch(Exception e)
		{
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List<PortAndDay> getJointedInboundPortList() {
		
		// 국내항 그룹화
		Map<String, List<ScheduleData>> portList =  scheduleList.stream().collect(
				Collectors.groupingBy(ScheduleData::getPort));// 선박
		
		ArrayList<PortAndDay> list = new ArrayList<PortAndDay>();
		
		for(Object key:portList.keySet())
		{	
			List<ScheduleData> li =portList.get(key);
			
			Collections.sort(li, new ScheduleDateComparator(ScheduleDateComparator.TO_DATE));
			
			list.add(new PortAndDay(String.valueOf(key), li.get(0).getDateT()));
		}
			
		list.stream().sorted(dateComparator);
		
		return list;
	}	

	public String toJointedCompanyString() {
		
		String re_company_abbr = vessel.getVessel_company();
		
		List<String> companyAbbrList=this.scheduleList.stream()
				.filter(o -> !re_company_abbr.equals(o))
				.map( data-> (data.getCompany_abbr().equals(data.getAgent())?data.getCompany_abbr():data.getCompany_abbr()+"/"+data.getAgent()))
				.distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.collect(Collectors.toList());
		
		String sortedCompanyist =StringUtils.join(companyAbbrList.toArray(new String[companyAbbrList.size()]),",");
		
		return sortedCompanyist;
	}
	
	public String toPrintString(boolean taged) {
		
		String dateF 			= KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD2(getDateF());
		
		StringBuffer jointedInboundPort = new StringBuffer();
		
		portAndDayList.stream().sorted(dateComparator)
		
		.forEach(o -> jointedInboundPort.append(String.format("\t%s%s", getTagedPortCode( o.getPort(), taged),  KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD2(o.getDateF()))));		
		
		String vesselName = getVessel().getVessel_name();

		return toFormattedString(dateF, vesselName, sortedCompanyist, jointedInboundPort.toString(), taged);
	}
	
	public String toPrintString() {
		return toPrintString(true);
	}
	
	public String toFormattedString(String dateF,String vesselName, String company, String ports, boolean taged)
	{
		String company_tag = "<cf:Helvetica LT Std><ct:Roman>";

		if(taged)
		{
			return String.format("%s%s%s %s%s (%s) %s", FROM_DATE_TAG1,dateF, VESSEL_TAG+BOLD_TAG_F, vesselName+BOLD_TAG_B+"<cf:>",company_tag, company, ports);
		}
		else
		{
			return String.format("%s %s (%s) %s", dateF, vesselName , company, ports);
		}
	}
	
	public String toString()
	{
		return toPrintString(false);
	}
	
	private String getTagedPortCode(String fport, boolean isTaged) {
		
		String port =inboundCodeMap.get(fport);
		
		String tagF = "<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F;
		
		String tagB = BOLD_TAG_B+"<cf:><cf:Helvetica LT Std><ct:Roman>";
		
		if(isTaged)
		{
			return String.format("[%s%s%s]", tagF,port!=null?port:fport, tagB);
							
		}else
		{
			return String.format("[%s]", port!=null?port:fport);
		}
	}
	
	@Override
	public int compareTo(ScheduleGroup o) {
		try {
			
			int fromDateGap = inputDateFormat.parse(getDateF()).compareTo(inputDateFormat.parse(o.getDateF()));
			
			int toDateGap = inputDateFormat.parse(getDateT()).compareTo(inputDateFormat.parse(o.getDateT()));
						
			if(fromDateGap==0) return toDateGap>0?1:-1;
			
			return fromDateGap>0?1:-1;
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
