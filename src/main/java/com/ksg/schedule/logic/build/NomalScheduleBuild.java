package com.ksg.schedule.logic.build;

import java.sql.SQLException;
import java.util.Iterator;

import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleBuild;

/**
 * @author 박창현
 * @deprecated
 */
public class NomalScheduleBuild extends DefaultScheduleBuild{


	public NomalScheduleBuild(ShippersTable searchOption) throws SQLException {
		super(searchOption);
	}

	@Override
	public int execute() throws Exception{

		
		try {
			
			Iterator iter = scheduleList.iterator();
			while(iter.hasNext())
			{
				ShippersTable tableData = (ShippersTable) iter.next();

				if(isValidation(tableData)) {continue;}

				// 인바운드 출발 항구 인덱스
				int[] inboundFromPortIndex = makePortArraySub("");
				// 인바운드 도착 항구 인덱스
				int[] inboundToPortIndex = makePortArraySub("");
				// 아웃바운드 출발 항구 인덱스
				int[] outboundFromPortIndex = makePortArraySub("");
				// 아웃바운드 도착 항구 인덱스
				int[] outboundToPortIndex = makePortArraySub("");

				for(int vslIndex=0;vslIndex<vesselArray.length;vslIndex++)
				{
					// 선박 사용 유무 검사
					makeInboundSchedule(tableData, vslIndex, inboundFromPortIndex, inboundToPortIndex);
					//makeOutboundSchedule(tableData, vslIndex, outboundFromPortIndex, outboundToPortIndex);

				}
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			return ScheduleBuild.FAILURE; 
		}
		return ScheduleBuild.SUCCESS;
	}

	/**
	 * @param tableData 테이블 정보
	 * @param vslIndex 선박 인덱스
	 * @param fromPortIndexArray 출발항 인덱스 배열
	 * @param toPortIndexArray 도착항 인덱스 배열
	 */
	private void makeInboundSchedule(ShippersTable tableData,int vslIndex, int[] fromPortIndexArray, int[] toPortIndexArray)
	{
		for(int fromPortCount=0;fromPortCount<fromPortIndexArray.length;fromPortCount++)
		{
			int fromPortIndex = fromPortIndexArray[fromPortCount];
			for(int toPortCount=0;toPortCount<toPortIndexArray.length;toPortCount++)
			{
				int toPortIndex = toPortIndexArray[toPortCount];


				String fromPortData = arrayDatas[vslIndex][fromPortIndex-1];

				String toPortData = arrayDatas[vslIndex][toPortIndex-1];


				String vesselName=vslDatas[vslIndex][0];			
				String voyageNum=vslDatas[vslIndex][1];


			}
		}
	}

/*	private void makeOutboundSchedule(ShippersTable tableData,int vslIndex, int[] fromPortIndexArray, int[] toPortIndexArray)
	{

	}
*/




}
