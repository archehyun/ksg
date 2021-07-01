package com.ksg.schedule.logic.build;

import java.sql.SQLException;
import java.util.Iterator;

import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleBuild;

/**
 * @author ��â��
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

				// �ιٿ�� ��� �ױ� �ε���
				int[] inboundFromPortIndex = makePortArraySub("");
				// �ιٿ�� ���� �ױ� �ε���
				int[] inboundToPortIndex = makePortArraySub("");
				// �ƿ��ٿ�� ��� �ױ� �ε���
				int[] outboundFromPortIndex = makePortArraySub("");
				// �ƿ��ٿ�� ���� �ױ� �ε���
				int[] outboundToPortIndex = makePortArraySub("");

				for(int vslIndex=0;vslIndex<vesselArray.length;vslIndex++)
				{
					// ���� ��� ���� �˻�
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
	 * @param tableData ���̺� ����
	 * @param vslIndex ���� �ε���
	 * @param fromPortIndexArray ����� �ε��� �迭
	 * @param toPortIndexArray ������ �ε��� �迭
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
