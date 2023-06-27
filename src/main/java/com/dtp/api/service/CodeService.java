package com.dtp.api.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.Code;

public interface CodeService {
	
	public List selectCodeHListByCondition(Code param) throws Exception;
	
	public List selectCodeDetailListByCondition(Code param) throws Exception;
	
	public Code selectCodeDetailByKey(Code param) throws Exception;
	
	public Object insertCode(Code codeParam) throws Exception;
	
	public Object insertCodeDetail(Code codeParam) throws Exception;
	
	public Object deleteCodeDetail(Code codeParam) throws Exception;
	
	public Object deleteCode(String code_field)throws Exception; 

}
