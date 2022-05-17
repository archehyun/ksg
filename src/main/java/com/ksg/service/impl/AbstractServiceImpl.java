package com.ksg.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractServiceImpl {
	
	protected ObjectMapper objectMapper;
	
	public AbstractServiceImpl(){
		objectMapper = new ObjectMapper();
	}

}
