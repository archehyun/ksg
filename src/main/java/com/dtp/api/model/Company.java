package com.dtp.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 

  * @FileName : Company.java

  * @Project : KSG2

  * @Date : 2022. 12. 1. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 선사 정보
 */
@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
public class Company {
	
	private String company_name;	// 선사명 이름
	private String company_abbr;	// 선사명 약어
	private String agent_name;		// 에이전트 이름
	private String agent_abbr;		// 에이전트 약어
	private String contents;		// 비고
	private String event_date;		// 날짜

}
