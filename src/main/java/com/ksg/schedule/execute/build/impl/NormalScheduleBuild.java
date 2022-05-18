package com.ksg.schedule.execute.build.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.JDOMException;

import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.TablePort;
import com.ksg.schedule.execute.build.BuildSchedule;
import com.ksg.schedule.execute.util.ScheduleDateUtil;
import com.ksg.service.PortService;
import com.ksg.service.VesselService;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NormalScheduleBuild extends BuildSchedule{

	private int fromDateIndex;
	
	private int toDateIndex;
	
	private String[][] vslDatas;
	
	private HashMap<String, String> portMap;
	
	private HashMap<String, String> vesselMap;
	
	private PortService portService;
	
	private VesselService vesselService;

	private String table_id;
	
	private String date_isusse;
	
	private static final String BUSAN = "BUSAN";

	private static final String BUSAN_NEW_PORT = "BUSAN NEW";
	
	public static final int TYPE_INBOUND=1;

	public static final int TYPE_OUTBOUND=2;

	public NormalScheduleBuild(String date_isusse)
	{
		super();
		this.date_isusse = date_isusse;
		portService = new PortServiceImpl();
		vesselService = new VesselServiceImpl();
		
	}

	@Override
	public int execute() {
				
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("date_isusse", date_isusse);
		param.put("gubun", "Normal");
		log.info("start:{}", param);
		HashMap<String, Object> map = (HashMap<String, Object>) shipperTableService.selectList(param);

		List<HashMap<String, Object>> master = (List) map.get("master");

		try {
			portMap=(HashMap<String, String>) portService.selectAll();
			//vesselMap=(HashMap<String, String>) vesselService.selectAll();
			log.debug("schedule size:{}",master.size());
			log.debug("port map size:{}",portMap.size());
			
			for(HashMap<String, Object>item:master)
			{	
				makeSchedule(item);				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		log.info("end");

		return 0;
	}



	private void makeSchedule(HashMap<String, Object> item) throws SQLException, OutOfMemoryError, JDOMException, IOException
	{
		table_id = (String) item.get("table_id");

		log.info("tableid:{}", table_id);

		ADVData data = advService.getADVData(table_id);

		if(data == null) return;

		String[][] dateDatas = data.getDataArray();	

		if(dateDatas.length == 0) return;

		vslDatas = data.getFullVesselArray(false);

		List portList=shipperTableService.getPortList(table_id);
		
		String company_name = (String) item.get("company_abbr");

		int arrListSize = portList.size();

		TablePort portArray[] 	= (TablePort[])portList.toArray(new TablePort[arrListSize]);				

		int in_from_index[] 	= makePortArraySub(String.valueOf( item.get("in_port")));
		
		int in_to_index[]		= makePortArraySub(String.valueOf( item.get("in_to_port")));
		
		int out_from_index[]	= makePortArraySub(String.valueOf( item.get("out_port")));
		
		int out_to_index[]		= makePortArraySub(String.valueOf( item.get("out_to_port")));

		for(int vesselIndex=0;vesselIndex<vslDatas.length;vesselIndex++)
		{	
			// 선박명 validation
//			if(!vesselMap.containsKey(vslDatas[vesselIndex][0]))
//			{
//				log.error("vessel not found:{},{} ",vslDatas[vesselIndex][0]);
//					continue;
////					throw new RuntimeException("port not found:"+fromPort.getPort_name());
//			}
			
			
			makeScheduleSub("O",vslDatas[vesselIndex], dateDatas[vesselIndex],portArray, out_from_index, out_to_index);
			makeScheduleSub("I",vslDatas[vesselIndex], dateDatas[vesselIndex],portArray, in_from_index, in_to_index);	
		}
	}
	
	private void makeScheduleSub(String inOutType,String[] vessel, String[] arrayDatas,TablePort[] portArray, int fromIndexs[], int toIndexs[])
	{
		if(fromIndexs ==null|| toIndexs == null)
			return;
		String vesselName = vessel[0];
		String voyage = vessel[1];
		
		
		boolean isOutFromBusanAndNewBusan	= isBusanAndNewBusan(fromIndexs, toIndexs,TYPE_INBOUND, portArray);
		
		boolean isOutToBusanAndNewBusan		= isBusanAndNewBusan(fromIndexs, toIndexs,TYPE_OUTBOUND, portArray);

		for(int fromIndex =0;fromIndex<fromIndexs.length;fromIndex++)
		{
			for(int toIndex =0;toIndex<toIndexs.length;toIndex++)
			{
				try {

					//출발일 도착일 날짜 인덱스
					fromDateIndex 		= fromIndexs[fromIndex]-1;
					
					//도착일 날짜 인덱스
					toDateIndex 		= toIndexs[toIndex]-1;
					
					// 스케줄 날짜형식 지정
					String dates[] 		= ScheduleDateUtil.getScheduleDate(arrayDatas[fromDateIndex], arrayDatas[toDateIndex], inOutType);
					
					// 출발항
					TablePort fromPort 	= portArray[fromDateIndex];
					
					// 도착항
					TablePort toPort 	= portArray[toDateIndex];
					
					// 항구명 validation
					if(!portMap.containsKey(fromPort.getPort_name())||!portMap.containsKey(toPort.getPort_name()))
					{
						log.error("port not found:{},{} ",fromPort.getPort_name(), toPort.getPort_name());
							continue;
//							throw new RuntimeException("port not found:"+fromPort.getPort_name());
					}					

					log.debug("{}, {},{}, index[{},{}], {},{},{},{}",inOutType,vesselName,voyage,fromIndexs[fromIndex],toIndexs[toIndex], dates[0], dates[1], fromPort.getPort_name(), toPort.getPort_name());
					
				}catch(ArrayIndexOutOfBoundsException e)
				{	
					e.printStackTrace();
					log.error("{},arrayDatas {},{},{},{}",table_id,arrayDatas.length,fromIndex, toDateIndex, toIndexs[toIndex]);
					//throw new RuntimeException();

				} catch (NotSupportedDateTypeException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}

	}

	/**
	 * @param field
	 * @param table
	 * @return
	 */
	private int[] makePortArraySub(String field ) {
		if(field==null||field.equals("")||field.equals(" "))
			return null;


		field=field.trim();

		// #을 기준으로 항구 인덱스를 구분
		StringTokenizer st = new StringTokenizer(field,"#");

		Vector<Integer> indexList = new Vector<Integer>();
		while(st.hasMoreTokens())
		{
			try
			{
				indexList.add(Integer.parseInt(st.nextToken().trim()));
			}
			catch (NumberFormatException e) 
			{
				//				logger.error("number foramt error:"+field+",id:"+table.getTable_id());
				//				errorlist.add(createError("인덱스 오류", table.getTable_id(), field));

				continue;
			}
		}

		int array[] =new int[indexList.size()];
		
		for(int i=0;i<indexList.size();i++)
		{
			array[i]=indexList.get(i);
		}		

		return array;

	}
	
	

	/**
	 * @날짜 2015-10-01
	 * @설명 스케줄에 부산항과 부산신항이 동시에 존재하는지 판단, 부산항 : BUSAN, 부산신항 : BUSAN NEW
	 * @param fromPortIndexs
	 * @param toPortIndexs
	 * @return
	 * @throws SQLException
	 * 
	 */
	private boolean isBusanAndNewBusan(int[] fromPortIndexs, int[] toPortIndexs, int type, TablePort[] portArray)
	{
		
		boolean isExitOutFromOldPort=false;
		boolean isExitOutFromNewPort=false;
		boolean isExitOutToOldPort=false;
		boolean isExitOutToNewPort=false;
		// 항구확인
		for(int fromPortIndex=0;fromPortIndex<fromPortIndexs.length;fromPortIndex++)
		{	
			for(int toPortIndex=0;toPortIndex<toPortIndexs.length;toPortIndex++)
			{
				

				System.out.println(portArray.length+","+ fromPortIndexs[fromPortIndex]+","+toPortIndexs[toPortIndex] );
				TablePort searchOldFromPort = portArray[fromPortIndexs[fromPortIndex]-1];
				TablePort searchNewFromPort = portArray[fromPortIndexs[fromPortIndex]-1];
				
				TablePort searchOldToPort = portArray[toPortIndexs[toPortIndex]-1];
				TablePort searchNewToPort = portArray[toPortIndexs[toPortIndex]-1];


				if(searchOldFromPort==null||searchNewFromPort==null)
					continue;

				
				
				if(searchOldFromPort.getPort_name().equals(BUSAN))
				{
					isExitOutFromOldPort = true;
				}
				if(searchOldFromPort.getPort_name().equals(BUSAN_NEW_PORT))
				{
					isExitOutFromNewPort = true;
					//outBusanNewPortIndex=outPortIndex;
				}

				if(searchOldToPort==null||searchNewToPort==null)
					continue;

				if(searchOldToPort.getPort_name().equals(BUSAN))
				{
					isExitOutToOldPort = true;

				}
				if(searchNewToPort.getPort_name().equals(BUSAN_NEW_PORT))
				{
					isExitOutToNewPort = true;
//					outToBusanNewPortIndex=outPortIndex;
				}
			}
		}
		
		
		boolean result=false;
		switch (type) {
		case TYPE_INBOUND:
			
			result= isExitOutFromOldPort&&isExitOutFromNewPort;
			break;

		case TYPE_OUTBOUND:
			
			result= isExitOutToOldPort&&isExitOutToNewPort;
			break;
		}
		return result;
		
	}
	
	private TablePort getPort(List arry,int index)
	{
		TablePort port1 = new TablePort();

		if(arry.size()==1)
		{
			port1 = (TablePort) arry.get(0);
		}
		else if(arry.size()>1)
		{
			for(int i=0;i<arry.size();i++)
			{
				TablePort port=(TablePort) arry.get(i);
				if(port.getPort_index()==index)
				{
					port1.setPort_name(port.getPort_name());
					port1.addSubPort(port);
				}
			}
		}

		return port1;

	}
	


}

