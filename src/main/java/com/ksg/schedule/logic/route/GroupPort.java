package com.ksg.schedule.logic.route;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupPort extends ArrayList<PortScheduleInfo>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int orderType;
	protected Logger logger = LogManager.getLogger(this.getClass());
	private ArrayList<PortScheduleInfo> inPortList;
	private ArrayList<PortScheduleInfo> outPortList;

	public GroupPort() {
		inPortList = new ArrayList<PortScheduleInfo>();
		outPortList = new ArrayList<PortScheduleInfo>();
	}
	
	public List getInPortList()
	{
		return inPortList;
	}
	public void setInPortArray(PortScheduleInfo array[])
	{
		inPortList.clear();
		for(int i=0;i<array.length;++i)
		{
			inPortList.add(array[i]);
		}
	}
	public void setOutPortArray(PortScheduleInfo array[])
	{
		outPortList.clear();
		for(int i=0;i<array.length;++i)
		{
			outPortList.add(array[i]);
		}
	}

	public void addPort(ScheduleData data) throws ParseException
	{
		inPortList.add(new PortScheduleInfo(data.getFromPort(), data.getDateF(),data.getCompany_abbr()));
		outPortList.add(new PortScheduleInfo(data.getPort(), data.getDateT(),data.getCompany_abbr()));
	}

	public void addPort(PortScheduleInfo data) throws ParseException
	{
		add(data);
	}
	public void addPort(PortScheduleInfo data, int type) throws ParseException
	{
		if(type==1)
		{
			inPortList.add(data);
		}
		else if(type==2)
		{
			outPortList.add(data);
		}
	}

	private PortScheduleInfo[] createArray(ArrayList<PortScheduleInfo> array) {
		PortScheduleInfo lit[] = new PortScheduleInfo[array.size()];

		for(int i=0;i<array.size();i++)	
		{			
			lit[i]=array.get(i);

		}
		Arrays.sort(lit);

		return lit;
	}
	public PortScheduleInfo[] createInPortArray()
	{
		return this.createArray(inPortList);
	}
	public PortScheduleInfo[] createOutPortArray()
	{
		return this.createArray(outPortList);
	}
	public PortScheduleInfo[] toPortArray() {

		PortScheduleInfo lit[] = new PortScheduleInfo[this.size()];

		for(int i=0;i<this.size();i++)	
		{			
			lit[i]=this.get(i);
		}
		Arrays.sort(lit);

		return lit;
	}

	private boolean checkType(String oneDate, String twoDate, int sortType) throws ParseException
	
	{
		int dayDiffer = KSGDateUtil.daysDiff(PortDateUtil.parse(oneDate), PortDateUtil.parse(twoDate));
		
		if(sortType==GroupVessel.SORT_BY_LAST) // in 늦은 날짜 기준
		{
			return dayDiffer>=0;
			
		}else if(sortType==GroupVessel.SORT_BY_FIRST) // out 빠른 날자 기준
		{
			return dayDiffer<0;
				
		}
		
		return false;
	}

	public PortScheduleInfo[] createCompressedArray(PortScheduleInfo lit[],int sortType) throws ParseException {

		// 신규 리스트 생성
		HashMap<String, PortScheduleInfo> arr = new HashMap<String, PortScheduleInfo>();
		
		StringBuffer buffer = new StringBuffer();
		for(PortScheduleInfo item:lit)
		{
			
			buffer.append(item+", ");
			
		}
		logger.info("sort전:{}",buffer.toString());
		

		for(int i=0;i<lit.length;i++)	
		{	
			if(arr.containsKey(lit[i].getPort()))
			{
				PortScheduleInfo one=arr.get(lit[i].getPort());
				
				String oneDate = one.getDate();
				
				String twoDate = lit[i].getDate();

				if(checkType(oneDate, twoDate, sortType))
				{
					arr.put(lit[i].getPort(), lit[i]);
				}

			}else
			{
				arr.put(lit[i].getPort(),lit[i]);
			}
		}
		Set<String>keylist=arr.keySet();
		
		PortScheduleInfo lit2[] = new PortScheduleInfo[keylist.size()];
		
		Iterator<String> iter=keylist.iterator();

		for(int i=0;iter.hasNext();i++)	
		{
			String key =iter.next();
			lit2[i]=arr.get(key);
		}
		Arrays.sort(lit2);
		StringBuffer buffer2 = new StringBuffer();
		for(PortScheduleInfo item:lit2)
		{
			
			buffer2.append(item+", ");
			
		}
		logger.info("sort후:{}",buffer2.toString());
		return lit2;
	}

	private GroupPort temp;
	public PortScheduleInfo[] selectArray(PortScheduleInfo lit[], int startIndex, int endIndex) throws ParseException
	{		 
		temp = new GroupPort();
		for(int i=startIndex;i<endIndex;i++)
		{
			temp.addPort(lit[i]);
		}

		return temp.toPortArray();

	}

	public PortScheduleInfo[] selectArray(PortScheduleInfo lit[],int startIndex) throws ParseException {

		return selectArray(lit, startIndex,lit.length);
	}


	public PortScheduleInfo[] createCompressedInPortArray() throws ParseException {		

		return this.createCompressedArray(this.createInPortArray(),1);
	}

	public PortScheduleInfo[] createCompressedOutPortArray() throws ParseException {


		return this.createCompressedArray(this.createOutPortArray(),2);
	}

	public int getOutPortSize() {
		return outPortList.size();
	}
	public String getFirstInScheduleDate() {
		PortScheduleInfo list[]= createInPortArray();
		return list[0].getDate();
	}	



}
