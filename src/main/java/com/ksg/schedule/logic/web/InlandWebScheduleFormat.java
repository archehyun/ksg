package com.ksg.schedule.logic.web;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;

/**
 * @author ��â��
 *
 */
public class InlandWebScheduleFormat extends WebScheduleFormat implements IFWebScheduleFormat{

	private String[][] arrayDatas;

	private String[][] vslDatas;
	
	public InlandWebScheduleFormat()
	{
		fileName="INLAND";
		errorFileName = "inlnad";
	}
	public InlandWebScheduleFormat(DefaultWebSchedule webSchedule) {
		this();
		this.webSchedule = webSchedule;

	}

	public InlandWebScheduleFormat(DefaultWebScheduleV2 defaultWebScheduleV2) {
		this();
	}
	public InlandWebScheduleFormat(DefaultWebScheduleV3 defaultWebScheduleV3) {
		this();
	}

	public String toWebScheduleString(ScheduleData data)
	{
		return	data.getInOutType()+"\t"+ 	//����(In/Out)
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
				data.getInland_port()+"\t"+	//�߰������ױ�
				data.getInland_date()+"\t"+	//�߰�������ETA
				data.getInland_date_back()+"\t"+	//�߰�������ETD
				data.getBookPage()+"\t"+    //���� ������
				data.getVessel_type()+"\t"+	//����
				data.getOperator()+"\t"+	//��ǥ ����
				data.getVessel_mmsi()+"\t"	//MMSI
				;
	}

	@Override
	public ScheduleData createWebScheduleData(ShippersTable table,
			String inOutType, int vslIndex, Vessel vesselInfo, int fromPortIndex,
			int toPortIndex, String fromPort, String toPort) {
		vslDatas = webSchedule.getVslDatas();

		String fromDates[]		= webSchedule.getFromDates();
		String toDates[]		= webSchedule.getToDates();		
		ScheduleData scheduledata = new ScheduleData();
		scheduledata.setFromPort(fromPort);				// �����
		scheduledata.setDateF(fromDates[0]);			// ����� ETA
		scheduledata.setDateFBack(fromDates[1]);	    // ����� ETD
		scheduledata.setPort(toPort);					// ������
		scheduledata.setDateT(toDates[0]);				// ������ ETA
		scheduledata.setDateTBack(toDates[1]);			// ������ ETD
		scheduledata.setVessel(vslDatas[vslIndex][0]);	// ���ڸ� �Ҵ�
		scheduledata.setVoyage_num(vslDatas[vslIndex][1]);
		scheduledata.setCompany_abbr(table.getCompany_abbr());// Console ��ü��
		scheduledata.setAgent(table.getAgent());		// ������Ʈ �Ҵ�
		scheduledata.setInland_port(table.getIn_to_port());		
		scheduledata.setVessel_type(vesselInfo.getVessel_type());				//���� 
		scheduledata.setOperator(vesselInfo.getVessel_company());// ��ǥ��ü
		return scheduledata;
	}



}
