package com.dtp.api.control;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.service.ShipperTableService;
import com.dtp.api.service.impl.ShipperTableServiceImpl;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.CharUtil;
import com.ksg.domain.ShippersTable;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ShipperTableController extends AbstractController{


	protected Logger logger = LogManager.getLogger(this.getClass());

	int groupCount=10;

	public static final char[] ALPA = {		
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S',
			'T','U','X','Y','Z'
	};
	public static final char[] CHOSEONG = { 
			//ㄱ  ㄲ  ㄴ  ㄷ  ㄸ  ㄹ  ㅁ  ㅂ  ㅃ  ㅅ
			'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 
			//ㅆ  ㅇ  ㅈ  ㅉ  ㅊ  ㅋ  ㅌ  ㅍ  ㅎ
			'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ','ㅌ', 'ㅍ', 'ㅎ' 
	};

	ShipperTableService service;

	public ShipperTableController()
	{
		service = new ShipperTableServiceImpl();
	}

	//

	@ControlMethod(serviceId = "shipperTableMgtUI2.init")
	public CommandMap select(CommandMap param) throws SQLException
	{	
		log.info("param:{}", param);
		
		groupCount = param.containsKey("groupCount")?(int) param.get("groupCount"):10;
		// 알파뱃 정렬
		List<ShippersTable> tableList=service.selectTableAll();

		List<String> companyNameList= tableList.stream()
				.map(ShippersTable::getCompany_abbr)
				.distinct()
				.collect(Collectors.toList());

		// 페이지 목록 조회
		List<Integer> pageList= tableList.stream()
										.map(ShippersTable::getPage)
										.distinct()
										.collect(Collectors.toList());



		HashMap<String,List<String>> apabetList = new HashMap<String,List<String>>();

		for(char ch:ALPA)
		{	
			apabetList.put(String.valueOf(ch), new ArrayList<String>());
		}

		companyNameList.stream().forEach(name -> {

			String key = String.valueOf( CharUtil.split(name).charAt(0));

			List<String> list =apabetList.get( key);

			if(list !=null) list.add(name);

		});


		groupCount = 10;

		Map<Integer, Map<String,List<ShippersTable>>> pageMap= tableList.stream()
				.collect(Collectors.groupingBy(table -> (int)(table.getPage()/groupCount),
						Collectors.groupingBy(o ->o.getPage()+":"+o.getCompany_abbr())));



		Object keys[]=pageMap.keySet().toArray();

		Arrays.sort(keys);

		Map<Integer,Object> newPageMap = new HashMap<Integer, Object>();

		for(Object keyItem : keys)
		{
			Map<String, List<ShippersTable>> li = pageMap.get(keyItem);

			List<ShippersTable> nameList = li.keySet().stream().map(o ->ShippersTable.builder()
					.page(Integer.parseInt( o.split(":")[0]))
					.company_abbr( o.split(":")[1])
					.build())
					.collect(Collectors.toList());


			newPageMap.put((Integer)keyItem, nameList);
		}	

		CommandMap returnMap = new CommandMap();

		returnMap.put("pageList", newPageMap);

		returnMap.put("companyList", apabetList);

		return returnMap;
	}



	@ControlMethod(serviceId = "shipperTableMgtUI2.fnSearch")
	public CommandMap fnSelect(CommandMap param) throws SQLException
	{

		logger.info("param:{}",param);

		ShippersTable shipperTable= null;

		try {
			shipperTable = ShippersTable.builder()
					.table_id((String) param.get("table_id"))
					.title((String) param.get("title"))
					.company_abbr((String) param.get("company_abbr"))
					.page(param.containsKey("page")?Integer.parseInt((String) param.get("page")):-1)
					.gubun((String) param.get("gubun"))
					.agent((String) param.get("agent"))
					.date_isusse((String) param.get("date_isusse"))
					.table_index(param.containsKey("table_index")?Integer.parseInt((String) param.get("table_index")):-1)
					.build(); 
		}catch(NumberFormatException e)
		{
			throw new RuntimeException("숫자를 입력하십시요");
		}
		List<ShippersTable> data=service.selectTableListByCondition(shipperTable);

		if(param.containsKey("startPage"))
		{
			int startPage = (int)param.get("startPage");
			int endPage = (int)param.get("endPage");
			data=data.stream().filter(o->o.getPage()>=startPage&&o.getPage()<endPage)
					.collect(Collectors.toList());
		}
		if(param.containsKey("alphabet"))
		{
			String alphabet = (String) param.get("alphabet");
			data=data.stream().filter(o->o.getCompany_abbr().startsWith(alphabet))
					.collect(Collectors.toList());

		}

		List<CommandMap> resultArry=(List<CommandMap>) data.stream()
				.map(o -> objectMapper.convertValue(o, CommandMap.class))
				.collect(Collectors.toList());


		CommandMap returnMap = new CommandMap();

		returnMap.put("data", resultArry);

		return returnMap;

	}



	@ControlMethod(serviceId = "aDVManageUI.init")
	public CommandMap aDVManageUIInit(CommandMap param) throws SQLException
	{	
		log.info("param:{}", param);
		
		groupCount = param.containsKey("groupCount")?(int) param.get("groupCount"):10;
		// 알파뱃 정렬
		List<ShippersTable> tableList=service.selectTableAll();

		List<String> companyNameList= tableList.stream()
				.map(ShippersTable::getCompany_abbr)
				.distinct()
				.collect(Collectors.toList());

		// 페이지 목록 조회
		List<Integer> pageList= tableList.stream()
				.map(ShippersTable::getPage)
				.distinct()
				.collect(Collectors.toList());



		HashMap<String,List<String>> apabetList = new HashMap<String,List<String>>();

		for(char ch:ALPA)
		{	
			apabetList.put(String.valueOf(ch), new ArrayList<String>());
		}

		companyNameList.stream().forEach(name -> {

			String key = String.valueOf( CharUtil.split(name).charAt(0));

			List<String> list =apabetList.get( key);

			if(list !=null) list.add(name);

		});


		groupCount = 10;

		Map<Integer, Map<String,List<ShippersTable>>> pageMap= tableList.stream()
				.collect(Collectors.groupingBy(table -> (int)(table.getPage()/groupCount),
						Collectors.groupingBy(o ->o.getPage()+":"+o.getCompany_abbr())));



		Object keys[]=pageMap.keySet().toArray();

		Arrays.sort(keys);

		Map<Integer,Object> newPageMap = new HashMap<Integer, Object>();

		for(Object keyItem : keys)
		{
			Map<String, List<ShippersTable>> li = pageMap.get(keyItem);

			List<ShippersTable> nameList = li.keySet().stream().map(o ->ShippersTable.builder()
					.page(Integer.parseInt( o.split(":")[0]))
					.company_abbr( o.split(":")[1])
					.build())
					.collect(Collectors.toList());


			newPageMap.put((Integer)keyItem, nameList);
		}	

		CommandMap returnMap = new CommandMap();

		returnMap.put("pageList", newPageMap);

		returnMap.put("companyList", apabetList);

		return returnMap;
	}

}
