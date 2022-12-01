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

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� : ���� ����
 */
@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
public class Company {
	
	private String company_name;	// ����� �̸�
	private String company_abbr;	// ����� ���
	private String agent_name;		// ������Ʈ �̸�
	private String agent_abbr;		// ������Ʈ ���
	private String contents;		// ���
	private String event_date;		// ��¥

}
