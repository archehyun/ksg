package com.dtp.api.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dtp.api.dao.CodeDAO;
import com.dtp.api.exception.AlreadyExistException;
import com.dtp.api.service.CodeService;
import com.ksg.common.model.CommandMap;
import com.ksg.dao.impl.CodeDAOImpl;
import com.ksg.domain.Code;
import com.ksg.service.impl.AbstractServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**

  * @FileName : CodeService.java

  * @Date : 2021. 3. 11. 

  * @작성자 : 박창현

  * @변경이력 : 

  * @프로그램 설명 : 코드 정보 관리

  */
@Slf4j
public class CodeServiceImpl extends AbstractServiceImpl implements CodeService{
	
	private CodeDAO codeDAO;
	
	
	public CodeServiceImpl() {
		
		super();
		codeDAO = new CodeDAO();
	}
	
	@Override
	public List selectCodeHListByCondition(Code param) throws Exception
	{
		return codeDAO.selectCodeListByCondition(param);
	}
	
	@Override	
	public List<Code> selectCodeDetailListByCondition(Code param) throws Exception
	{
		return codeDAO.selectCodeDetailListByCondition(param);
	}
	
	public Code selectCodeDetailByKey(Code codeParam) throws SQLException {
		
		return codeDAO.selectCodeDetailByKey(codeParam);
	}

	public CommandMap selectInboundPortMap() throws Exception
	{
		CommandMap param = new CommandMap();
		
		param.put("code_type", "inPort");
		
		Code code = Code.builder().code_type("inPort").build();
		
		List<Code> list =codeDAO.selectCodeDetailListByCondition(code);
		
		CommandMap result = new CommandMap();
		
		for (Code item : list) {
		    result.put(String.valueOf(item.getCode_field()), item.getCode_name());
		}
		return result;
	}


	public Object updateCode(Code param) throws Exception{
		
		log.debug("param:{}"+param);
		
		Code target= codeDAO.selectCodeById(param.getCode_id());
		
		 if(target==null) throw new AlreadyExistException("존재하지 않는 코드입니다.");
		
		return codeDAO.updateCode(param);
		
	}
	
	public Object deleteCode(String code_field)throws Exception {
		
		Code target= codeDAO.selectCodeById(code_field);
		
		 if(target==null) throw new AlreadyExistException("존재하지 않는 코드입니다.");
		
		return codeDAO.deleteCode(code_field);
		
	}

	public Object deleteCodeDetail(Code codeParam) throws Exception {
		
		Code target= codeDAO.selectCodeDetailByKey(codeParam);
		
		 if(target==null) throw new AlreadyExistException("존재하지 않는 코드입니다.");
		 
		return codeDAO.deleteCodeDetail(codeParam);
	}

	public Object insertCode(Code codeParam) throws Exception {
		
		Code  code = codeDAO.selectCodeById(codeParam.getCode_id());
		
		if(code !=null) throw new AlreadyExistException(String.format("이미 존재하는 코드명(%s)입니다.", code.getCode_name()));
		
		return codeDAO.insertCode(codeParam);
		
	}
	
	public Object insertCodeDetail(Code codeParam) throws Exception {
		
		Code  code = selectCodeDetailByKey(codeParam);
		
		if(code !=null) throw new AlreadyExistException(String.format("이미 존재하는 코드명(%s)입니다.", code.getCode_name()));
		
		return codeDAO.insertCodeDetail(codeParam);
	}
}
