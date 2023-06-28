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
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.CharUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;

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

	private ShipperTableService service;

	public ShipperTableController()
	{
		service = new ShipperTableServiceImpl();
	}

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

			//TODO NUMBER ERROR
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
			int startPage 	= (int)param.get("startPage");

			int endPage 	= (int)param.get("endPage");

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

	@ControlMethod(serviceId = "shipperTableMgtUI2.deleteAdv")
	public CommandMap deleteAdv(CommandMap param) throws Exception
	{
		String table_id = (String) param.get("table_id");

		service.delete(table_id);

		CommandMap returnMap = new CommandMap();

		return returnMap;
	}

	@ControlMethod(serviceId = "shipperTableMgtUI2.delete")
	public CommandMap delete(CommandMap param) throws Exception
	{
		String table_id = (String) param.get("table_id");

		service.delete(table_id);

		CommandMap returnMap = new CommandMap();

		return returnMap;
	}

	@ControlMethod(serviceId = "shipperTableMgtUI2.update")
	public CommandMap update(CommandMap param) throws Exception
	{
		log.info("param:{}", param);

		ShippersTable target= (ShippersTable) param.get("updateParam");

		service.update(target);

		CommandMap returnMap = new CommandMap();

		return returnMap;
	}
	@ControlMethod(serviceId = "addTableInfoDialog.insert")
	public CommandMap addTableInfoDialog_insert(CommandMap param) throws Exception
	{
		ShippersTable target = ShippersTable.builder()
				.table_id((String) param.get("table_id"))
				.title((String) param.get("title"))
				.agent((String) param.get("agent"))
				.company_abbr((String) param.get("company_abbr"))
				.company_name((String) param.get("company_name"))
				.gubun((String) param.get("gubun"))
				.d_time((Integer) param.get("d_time"))
				.c_time((Integer) param.get("c_time"))
				.page((Integer) param.get("page"))
				.console_page((String) param.get("console_page"))
				.bookPage((String) param.get("bookPage"))
				.common_shipping((String) param.get("common_shipping"))
				.in_port((String) param.get("in_port"))
				.in_to_port((String) param.get("in_to_port"))
				.out_port((String) param.get("out_port"))
				.out_to_port((String) param.get("out_to_port"))
				.inland_indexs((String) param.get("inland_indexs"))
				.date_isusse((String) param.get("date_isusse"))
				.build(); 

		service.insert(target);

		CommandMap returnMap = new CommandMap();

		return returnMap;
	}

	@ControlMethod(serviceId = "addTableInfoDialog.init")
	public CommandMap addTableInfoDialog(CommandMap param) throws Exception
	{
		String table_id = (String) param.get("table_id");

		CommandMap returnMap = new CommandMap();

		if(table_id!=null)
		{
			ShippersTable target= service.selectShipperTableById(table_id);

			returnMap.put("selectedTable", target);
		}

		return returnMap;
	}

	@ControlMethod(serviceId = "shipperTableMgtUI2.selectOne")
	public CommandMap selectOne(CommandMap param) throws Exception
	{
		String table_id = (String) param.get("table_id");

		CommandMap returnMap = new CommandMap();

		if(table_id!=null)
		{
			ShippersTable shippersTable = service.selectShipperTableById(table_id);

			if(shippersTable == null) throw new ResourceNotFoundException("table not found : "+table_id);

			ADVData advData = service.selectADVDataById(shippersTable.getTable_id());

			shippersTable.setAdvData(advData);

			List tablePortList=service.selectShipperTablePortListByID(shippersTable.getTable_id());

			shippersTable.setTablePortList(tablePortList);

			returnMap.put("selectedTable", shippersTable);
		}

		return returnMap;
	}

	@ControlMethod(serviceId = "updateShipperTableDateDialog.updateDate")
	public CommandMap updateDate(CommandMap param) throws Exception
	{
		String date_isusse = (String) param.get("date_isusse");

		List tableIDList = (List) param.get("tableIDList");

		CommandMap returnMap = new CommandMap();

		int result= service.updateTableDateByTableIDs(tableIDList,date_isusse);

		returnMap.put("result", result);

		return returnMap;
	}
	@ControlMethod(serviceId = "KSGADVTablePanel.save")
	public CommandMap saveADVData(CommandMap param) throws Exception
	{
		CommandMap returnMap = new CommandMap();

		ADVData insertParam= (ADVData) param.get("insertParam");

		service.save(insertParam);

		return returnMap;
	}

	@ControlMethod(serviceId = "showADVTable")
	public CommandMap showADVTable(CommandMap param) throws Exception
	{
		CommandMap returnMap = new CommandMap();

		String table_id = (String) param.get("table_id");

		ShippersTable shippersTable = this.service.selectShipperTableById(table_id);

		ADVData advData = service.selectADVDataById(shippersTable.getTable_id());

		if(shippersTable == null||advData == null) throw new ResourceNotFoundException("table not found : "+table_id);

		shippersTable.setAdvData(advData);

		List tablePortList=service.selectShipperTablePortListByID(shippersTable.getTable_id());

		shippersTable.setTablePortList(tablePortList);

		returnMap.put("shpperTable", shippersTable);

		return returnMap;
	}

	@ControlMethod(serviceId = "managePortDialog.init")
	public CommandMap managePortDialogInit(CommandMap param) throws Exception
	{
		log.info("param:{}", param);

		CommandMap returnMap = new CommandMap();

		String table_id =(String) param.get("table_id");

		List<TablePort>  tablePortList =service.selectShipperTablePortListByID(table_id);

		ArrayList<CommandMap> dataList = new ArrayList<CommandMap>();

		for(TablePort port:tablePortList)
		{
			CommandMap item = new CommandMap();
			item.put("port_name", port.getPort_name());
			item.put("port_index", port.getPort_index());
			item.put("parent_port", port.getParent_port());
			item.put("port_type", port.getPort_type());
			item.put("table_id", port.getTable_id());
			dataList.add(item);

		}
		
		returnMap.put("data", dataList);

		return returnMap;
	}
	
	@ControlMethod(serviceId = "managePortDialog.saveTablePort")
	public CommandMap managePortDialogSaveTablePort(CommandMap param) throws Exception
	{
		CommandMap returnMap = new CommandMap();
		
		String table_id=(String) param.get("table_id");
		
		List<HashMap<String, Object>> tablePortList = (List) param.get("tablePortList");
		
		ArrayList<TablePort> targetList = new ArrayList<TablePort>(); 
		
		for(HashMap<String, Object> item:tablePortList)
		{
			targetList.add(TablePort.builder()
										.table_id(table_id)
										.port_name((String)item.get("port_name"))
										.port_index((int) item.get("port_index"))
										.port_type((String) item.get("port_type"))
										.parent_port((String) item.get("parent_port"))
										.build()); 
			
		}
		
		service.saveTablePort(table_id, targetList);
		
		return returnMap;
	}
}


