package com.ksg.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
public class Schedule {	
	private String agent; // ������Ʈ
	private String area_code; // ���� �ڵ�
	private String area_name; // ���� �̸�
	private String arrivalDate; // ��������
	private String c_time; // 
	private String common_shipping; // �����輱
	private String company_abbr; // ����� ���
	private String console_cfs;// �ܼ� CFS ����
	private String console_page; // �ܼ� ������
	private int console_print_type;
	private String d_time;
	private String data;
	private String date_issue;
	private String DateF;
	private String dateFBack;
	private String DateT;
	private String dateTBack;
	private String departDate;
	private String departure;
	private String desination;
	private int forSch;
	private String fromAreaCode;
	private String fromPort;
	private String gubun;
	private String inland_date;
	private String inland_date_back;
	private String vessel_mmsi; //���� MMSI �ڵ�
	private String inland_port;// �߰� ������
	private String InOutType;//����
	private int n_voyage_num;
	private int ntop;
	private int page; // ������
	private String port; // �ױ���
	private String table_id; // ���̺� ���̵�
	private String TS;
	private String ts_date;
	private String ts_port;
	private String ts_vessel;
	private String ts_voyage_num;
	private String vessel; // ���ڸ�
	private Vessel vesselInfo;
	private String vessel_type; // ����
	private String voyage_num; // ���� ��ȣ
	private String bookPage;// ���� ������
	private String majorCompany;// ���� ������
	private String orderby;
}
