package com.ksg.schedule.logic.web;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;

/**
 * @author ��â��
 *
 */
public class NomalWebScheduleFormat extends WebScheduleFormat implements IFWebScheduleFormat{
	
	private DefaultWebSchedule webSchedule;
	private DefaultWebScheduleV2 webSchedulev2;
	private String[][] arrayDatas;
	private String[][] vslDatas;
	
	public NomalWebScheduleFormat(DefaultWebSchedule webSchedule) {
		this.webSchedule = webSchedule;
		fileName="WW_SYBASE";
		errorFileName = "ww_sybase";
	}

	public NomalWebScheduleFormat(DefaultWebScheduleV2 defaultWebScheduleV2) {
		webSchedulev2 = defaultWebScheduleV2;
		fileName="WW_SYBASE";
		errorFileName = "ww_sybase";
	}

	public String toWebScheduleString(ScheduleData data)
	{
		return	data.getInOutType()+"\t"+ 	//����(Inbound/Outbound)
				data.getFromPort()+"\t"+ 	//�����
				data.getDateF()+"\t"+	 	//���ETA
				data.getDateFBack()+"\t"+	//���ETD
				data.getPort()+"\t"+		//�����ױ�
				data.getDateT()+"\t"+		//����ETA
				data.getDateTBack()+"\t"+	//����ETD
				data.getVessel()+"\t"+		//���ڸ�
				data.getVoyage_num()+"\t"+	//������ȣ
				data.getCompany_abbr()+"\t"+//Line
				data.getAgent()+"\t"+		//Agent
				data.getBookPage()+"\t"+	//����������				
				data.getVessel_type()+"\t"+	//����
				data.getOperator()+"\t"+//��ǥ ����
				data.getVessel_mmsi()+"\t"	//���� MMSI �ڵ� 
				;
	}

	public ScheduleData createWebScheduleData(ShippersTable table,
			String inOutType, int vslIndex, Vessel vesselInfo, int fromPortIndex,
			int toPortIndex, String fromPort, String toPort) {

		arrayDatas = webSchedule.getArrayDatas();
		vslDatas = webSchedule.getVslDatas();
		String outToPortData 	= arrayDatas[vslIndex][toPortIndex-1];
		String outPortData		= arrayDatas[vslIndex][fromPortIndex-1];
		String fromDates[]		= webSchedule.getFromDates();
		String toDates[]		= webSchedule.getToDates();		
		
		ScheduleData scheduledata = new ScheduleData();
		scheduledata.setInOutType(inOutType);
		scheduledata.setFromPort(fromPort);				// �����
		scheduledata.setDateF(fromDates[0]);			// ����� ETA
		scheduledata.setDateFBack(fromDates[1]);	    // ����� ETD
		scheduledata.setPort(toPort);					// ������
		scheduledata.setDateT(toDates[0]);				// ������ ETA
		scheduledata.setDateTBack(toDates[1]);			// ������ ETD
		scheduledata.setVessel(vesselInfo.getVessel_name());	// ���ڸ� �Ҵ�
		scheduledata.setVoyage_num(vslDatas[vslIndex][1]); // ���� ��ȣ
		scheduledata.setCompany_abbr(table.getCompany_abbr());// Line
		scheduledata.setAgent(table.getAgent());		// ������Ʈ 
		scheduledata.setBookPage(table.getBookPage());
		scheduledata.setVessel_type(vesselInfo.getVessel_type());				//����
		scheduledata.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi());
		
		String vessel_compmay="";
		if(vessel_compmay.contains("/"))
		{
			vessel_compmay=vesselInfo.getVessel_company().substring(0,vesselInfo.getVessel_company().indexOf("/"));
		}
		else
		{
			vessel_compmay= vesselInfo.getVessel_company();
		}
		scheduledata.setOperator(vessel_compmay);// ��ǥ��ü
		return scheduledata;
	}
}
