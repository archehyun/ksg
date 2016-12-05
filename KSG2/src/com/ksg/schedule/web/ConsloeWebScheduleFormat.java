package com.ksg.schedule.web;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;

/**
 * �ܼ� ���� ������ ���� �����ٷ� ��ȯ
 * @author archehyun
 *
 */
public class ConsloeWebScheduleFormat extends WebScheduleFormat  implements IFWebScheduleFormat{

	/**
	 * 
	 */

	private String[][] arrayDatas;

	private String[][] vslDatas;
	DefaultWebSchedule webSchedule;
	public ConsloeWebScheduleFormat(DefaultWebSchedule webSchedule) {
		this.webSchedule = webSchedule;
		fileName="CONSOLE";
		errorFileName = "console";
	}
	

	public ConsloeWebScheduleFormat(DefaultWebScheduleV2 defaultWebScheduleV2) {
		fileName="CONSOLE";
		errorFileName = "console";
	}


	public String toWebScheduleString(ScheduleData data)
	{
		return	data.getFromPort()+"\t"+ 	//�����
				data.getDateF()+"\t"+	 	//���ETA
				data.getDateFBack()+"\t"+	//���ETD
				data.getPort()+"\t"+		//�����ױ�
				data.getDateT()+"\t"+		//����ETA
				data.getDateTBack()+"\t"+	//����ETD
				data.getVessel()+"\t"+		//���ڸ�
				data.getVoyage_num()+"\t"+	//������ȣ
				data.getAgent()+"\t"+		//�ܼ� ��ü��(������Ʈ ���)
				data.getD_time()+"\t"+		//DCT
				data.getC_time()+"\t"+		//CCT
				data.getConsole_cfs()+"\t"+	//CFS
				data.getBookPage()+"\t"+	//���� ������
				data.getVessel_type()+"\t"+	//����
				data.getOperator()+"\t"	+	//��ǥ ����
				data.getCompany_abbr()+"\t"	+	//Line(����� ���)
				data.getVessel_mmsi()+"\t"	//���� MMSI �ڵ�
				;
	}
	public ScheduleData createWebScheduleData(ShippersTable table,String inOutType,int vslIndex,
			Vessel vesselInfo,int fromPortIndex,int toPortIndex, String fromPort, String toPort)
	{
		arrayDatas = webSchedule.getArrayDatas();
		vslDatas = webSchedule.getVslDatas();
		String outToPortData 	= arrayDatas[vslIndex][toPortIndex-1];
		String outPortData		= arrayDatas[vslIndex][fromPortIndex-1];
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
		scheduledata.setVoyage_num(vslDatas[vslIndex][1]);// ���� ��ȣ
		scheduledata.setCompany_abbr(table.getCompany_abbr());// Console ��ü��
		scheduledata.setAgent(table.getAgent());		// ������Ʈ ���
		scheduledata.setD_time(table.getD_time()==0?"":arrayDatas[vslIndex][table.getD_time()-1]); //DCT
		scheduledata.setC_time(table.getC_time()==0?"":arrayDatas[vslIndex][table.getC_time()-1]); //CCT		
		scheduledata.setConsole_cfs(getNewCFS(table.getConsole_cfs())); //CFS
		scheduledata.setConsole_page(table.getConsole_page());  // �ܼ� ������
		scheduledata.setBookPage(table.getBookPage());	// ���� ������
		scheduledata.setVessel_type(vesselInfo.getVessel_type());				//����
		scheduledata.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi());	
		scheduledata.setOperator(getOperator(vesselInfo.getVessel_company()));// ���� ��ǥ��ü
		
		return scheduledata;
	}
	private String getOperator(String vessel_company)
	{
		if(vessel_company.contains("/"))
		{
			return vessel_company.substring(0,vessel_company.indexOf("/"));
		}
		else
		{
			return vessel_company;
		}	
		
	}
	/**
	 * @since 2015-10-12
	 * @���� �Էµ� CFS ���� �� ���๮�ڸ� &�� ġȯ�Ͽ� ��ȯ
	 * @param cfs �ܼ� CFS ����
	 * @return
	 */
	private String getNewCFS(String cfs)
	{		
		return cfs.replaceAll("\n", "\\\\&");
	}



}
