package com.ksg.schedule.logic.print.outbound;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ksg.common.exception.PortNullException;
import com.ksg.common.exception.VesselNullException;
import com.ksg.common.util.SortUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.ScheduleBuild;
import com.ksg.schedule.logic.joint.outbound.FromPortGroup;
import com.ksg.schedule.logic.joint.outbound.PrintItem;
import com.ksg.schedule.logic.joint.outbound.ToPortGroup;
import com.ksg.schedule.logic.joint.outbound.VesselGroup;
import com.ksg.schedule.logic.print.ScheduleJointError;

/**
 * 
 * @설명 아웃바운드 생성 모듈
 * @author archehyun
 *
 */
/**

  * @FileName : OutboundScheduleJointV2.java

  * @Date : 2021. 5. 3. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :

  */
public class OutboundSchedulePrintV2 extends OutboundAbstractSchedulePrint{


	private ScheduleData op;

	private ScheduleData data;

	private ArrayList<PrintItem> printList;

	private List<String> outbounSchedulePortList;	

	public OutboundSchedulePrintV2() throws Exception {

		super();

		logger.info("outbound build");

		message = "Outbound 생성중...";
		
		printList = new ArrayList<PrintItem>();

		logger.info("outbound 스케줄 생성 및 초기화");

	}

	/**
	 * @author archehyun
	 *
	 */
	class ScheduleGroup extends HashMap<String, ToPortGroup>
	{
		private static final long serialVersionUID = 1L;

		public void add(ScheduleData data) throws SQLException, VesselNullException
		{
			//try {
			if(this.containsKey(data.getPort()))
			{
				ToPortGroup group = this.get(data.getPort());
				// 키 : 출발항-출발일-선박명-도착일

				group.addSchedule(data);


			}else
			{
				// 신규 그룹 생성
				// 키 : 출발항-출발일-선박명-도착일
				this.put(data.getPort(), new ToPortGroup(data));
			}
			
		}
	}



	/**출발항구 정렬
	 * @param outboundFromPortList
	 * @return
	 */
	private String[] arrangeFromPort(String[] outboundFromPortList) 
	{	
		logger.debug("from-port arrange");

		List<String> arragedFromPort = new LinkedList<String>();
		for(int i=0;i<fromPort.length;i++)
		{
			arragedFromPort.add(fromPort[i]);
		}

		List<String> fromPorts = new LinkedList<String>();
		for(int i=0;i<outboundFromPortList.length;i++)
		{
			fromPorts.add(outboundFromPortList[i]);
		}

		for(int i=0;i<fromPort.length;i++)
		{
			// 국내항 항목이 없으면
			if(!fromPorts.contains(fromPort[i]))
			{
				//정렬된 항국 목록 에서 제외
				arragedFromPort.remove(fromPort[i]);
			}
		}
		String[] newArray = new String[arragedFromPort.size()];
		for(int i=0;i<newArray.length;i++)
		{
			newArray[i] = arragedFromPort.get(i);
		}
		return newArray;
	}

	private boolean checkFromPort(String toPort)
	{
		for(int i=0;i<fromPort.length;i++)
		{
			if(fromPort[i].equals(toPort))
				return true;
		}
		return false;
	}
	@Override
	public int execute() throws IOException, ScheduleJointError {

		long startTime = System.currentTimeMillis();


		message = "아웃바운드 스케줄 그룹화...";


		/*정렬 기준
		 * 1. 도착항
		 * 2. 출발항
		 * 3. 출발일
		 * 4. 도착일 
		 */

		try{

			//도착항구명 목록 조회
			outbounSchedulePortList = scheduleService.getOutboundPortList();

			lengthOfTask = outbounSchedulePortList.size();

			Iterator<String> toPortIter = outbounSchedulePortList.iterator();

			fw.write(buildVersionXTG());

			int tagIndex=0;// 태그 적용을 위한 인덱스

			String toPort=null;

			//도착항구명 기준으로 스케줄 생성
			while(toPortIter.hasNext())
			{
				try{
					toPort = toPortIter.next();
					logger.info("도착항: "+toPort);

					if(checkFromPort(toPort))
						continue;

					portfw.write(toPort+"\n");
					
					
					op = new ScheduleData();
					op.setPort(toPort);
					op.setInOutType("O");

					// 도착항구명 기준으로 스케줄 조회
					List<ScheduleData> outboundScheduleListByToPort =scheduleService.getScheduleList(op);

					// 도착항구명 기준으로 그룹 생성
					ToPortGroup toPortgroup = new ToPortGroup(toPort);
					Iterator<ScheduleData> scheduleList = outboundScheduleListByToPort.iterator();
					while(scheduleList.hasNext())
					{
						try{
							data =scheduleList.next();
							toPortgroup.addSchedule(data);
						}catch(VesselNullException e)
						{
							logger.error("vessel null error table-id:"+data.getTable_id()+",vessel-name:"+data.getVessel());
							errorfw.write("vessel null error table-id:"+data.getTable_id()+",vessel-name:"+data.getVessel()+"\n");
							continue;
						}
					}

					fw.write(buildToPortXTG(tagIndex, toPortgroup.getToPort(), toPortgroup.getPort_nationality()));

					tagIndex++;

					// 도착항 기준 출발항 목록 정렬
					String[] fromPortArray = arrangeFromPort(toPortgroup.keySet().toArray(new String[toPortgroup.keySet().size()]));

					/*
					 * 출발항 기준으로 출력 시작
					 */

					for(int i=0;i<fromPortArray.length;i++)
					{
						FromPortGroup fromPortGroup =toPortgroup.get(fromPortArray[i]);	

						String[] vesselArray = fromPortGroup.keySet().toArray(new String[fromPortGroup.keySet().size()]);

						//여기서 정렬하는 이유?
						SortUtil.bubbleSort(vesselArray);

						VesselGroup[] vesselArrays =new VesselGroup[vesselArray.length];

						for(int y=0;y<vesselArray.length;y++)
						{
							vesselArrays[y] =fromPortGroup.get(vesselArray[y]);
						}

						printList.clear();

						/*
						 * vesselgroup에서 공동배선 생성 및 정렬을 위한 목록 생성
						 */

						for(int y=0;y<vesselArrays.length;y++)
						{
							ArrayList<PrintItem> li = vesselArrays[y].getJointedVesselList();
							
							

//							Iterator<PrintItem> iterator = li.iterator();
//							
//							while(iterator.hasNext())
//							{
//								printList.add(iterator.next());
//							}
							li.stream().forEach(o -> printList.add(o));
							
						}
						
						// 스케줄이 있을때 출발항 표시
						if(printList.size()>0)
							
						fw.write(buildFromXTG(i, fromPortGroup.getFromPortName()));

						// 출발일로 정렬
						PrintItem pr[]= new PrintItem[printList.size()];

						pr=printList.toArray(pr);

						Arrays.sort(pr);

						for(int pr_i=0;pr_i<pr.length;pr_i++)
						{
							fw.write(pr[pr_i].toString());
						}
					}
				}catch(PortNullException e)
				{
					logger.error("port null:"+toPort);
					errorfw.write("port null:"+toPort);
				}
				current++;

			}

			logger.info("outbound 스케줄 생성 종료("+(System.currentTimeMillis()-startTime)+")");

			return ScheduleBuild.SUCCESS;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			throw new ScheduleJointError(e,data);
			
		}
		finally
		{
			// 파일 닫기
			fw.close();
			errorfw.close();
			portfw.close();
		}
	}

	/**XTG 출력
	 * @return
	 */
	private String buildVersionXTG() {
		String buffer = TAG_VERSION0+"\r\n"+
				TAG_VERSION2+"\r\n"+
				TAG_VERSION3+"\r\n"+
				TAG_VERSION4+"\r\n"+
				TAG_VERSION5;
		return buffer;
	}

	/**
	 * @param i
	 * @param portName
	 * @param portNationality
	 * @return
	 */
	private String buildToPortXTG(int i, String portName,String portNationality) {

		if(isApplyTag)
		{
			return (i!=0?" \r\n<ct:><cs:><ct:Bold><cs:8.000000>":"<ct:><cs:><ct:Bold><cs:8.000000>")

					+portName +" , "+portNationality+" ";	
		}
		else
		{
			return (i!=0?" \r\n":"")+portName +" , "+portNationality+" ";	
		}

	}
	private String buildFromXTG(int j, String fromPort) {
		if(isApplyTag)
		{
			return (j==0?" \r\n \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ":" \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ")+fromPort+" -\r\n";
		}
		else
		{
			return (j==0?" \r\n \r\n- ":" \r\n- ")+fromPort+" -\r\n";
		}
	}
}
