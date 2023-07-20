package com.dtp.api.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter 
public class ExcelExport {
	
	private String sheetName;
	
	private String column[];
	
	private List dataList;

}
