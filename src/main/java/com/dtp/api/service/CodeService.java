package com.dtp.api.service;

import java.util.List;

import com.ksg.domain.Code;

public interface CodeService {
	
	public List<Code> selectCodeHListByCondition(Code param) throws Exception;
	
	public List<Code> selectCodeDetailListByCondition(Code param) throws Exception;
	
	public Code selectCodeDetailByKey(Code param) throws Exception;
	
	public Object insertCode(Code codeParam) throws Exception;
	
	public Object insertCodeDetail(Code codeParam) throws Exception;
	
	public Object deleteCodeDetail(Code codeParam) throws Exception;
	
	public Object deleteCode(String code_field)throws Exception; 

}
