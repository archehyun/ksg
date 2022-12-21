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
	private String agent; // 에이전트
	private String area_code; // 지역 코드
	private String area_name; // 지역 이름
	private String arrivalDate; // 도착일자
	private String c_time; // 
	private String common_shipping; // 공동배선
	private String company_abbr; // 선사명 약어
	private String console_cfs;// 콘솔 CFS 정보
	private String console_page; // 콘솔 페이지
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
	private String vessel_mmsi; //선박 MMSI 코드
	private String inland_port;// 중간 기항지
	private String InOutType;//구분
	private int n_voyage_num;
	private int ntop;
	private int page; // 페이지
	private String port; // 항구명
	private String table_id; // 테이블 아이디
	private String TS;
	private String ts_date;
	private String ts_port;
	private String ts_vessel;
	private String ts_voyage_num;
	private String vessel; // 선박명
	private Vessel vesselInfo;
	private String vessel_type; // 선종
	private String voyage_num; // 항차 번호
	private String bookPage;// 지면 페이지
	private String majorCompany;// 지면 페이지
	private String orderby;
}
