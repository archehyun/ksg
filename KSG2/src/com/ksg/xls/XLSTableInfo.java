package com.ksg.xls;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Sheet;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Property;
import com.ksg.model.KSGModelManager;
import com.ksg.view.util.KSGDateUtil;
import com.ksg.view.util.KSGPropertis;
import com.ksg.view.util.KSGPropertyManager;
import com.ksg.xls.model.TableLocation;

public class XLSTableInfo extends TableLocation{

	public static final int VESSEL_LOCATION=0;
	public static final int VOYAGE_LOCATION=1;
	/**
	 * @return xls import 결과 
	 */
	private String datePattern ="\\w+.?\\(\\w+\\)";
	
	private XLSStringUtil xlsUitl;

	private KSGModelManager manager = KSGModelManager.getInstance();
	
	private Element portarray;

	private XLSSearchPortInfoManager portInfoManager;

	private KSGPropertis propertis = KSGPropertis.getIntance();
	
	private Table_Property property;
	
	private KSGPropertyManager propertyManager =KSGPropertyManager.getInstance();

	private Element rootElement;

	private Element schedule_row;
	
	private TableService _tableService;

	private ShippersTable tableInfo;

	private TableLocation xlsLocation;
	public XLSTableInfo( ShippersTable tableInfo,TableLocation location) {
		this.tableInfo=tableInfo;
		this.xlsLocation=location;

		this.setTableType(xlsLocation.getTableType());
		_tableService =  DAOManager.getInstance().createTableService();
		this.setTable_id(tableInfo.getTable_id());
		portInfoManager = new XLSSearchPortInfoManager(tableInfo.getTable_id());
		xlsUitl = new XLSStringUtil();
		initProperty();


	}
	private Element addSubPort(String port_name) {
		Element sub_port1 = new Element("sub_port");
		sub_port1.setAttribute("port_name",port_name);
		return sub_port1;
	}

	public TableLocation getLocation() 
	{
		return xlsLocation;
	}

	public Element getPortElement() {
		// TODO Auto-generated method stub
		return portarray;
	}

	public String getSheetName()
	{
		Sheet sheet = xlsLocation.sheet;
		return sheet.getWorkbook().getSheetName(sheet.getWorkbook().getSheetIndex(sheet));
	}

	public ShippersTable getTableInfo() {
		return tableInfo;
	}
	/**
	 * @return
	 */
	public String getTableXMLInfo()
	{
		initProperty();

		logger.debug("<create xml start>");
		int portCount=tableInfo.getPort_col()+tableInfo.getOthercell();
		int vesselCount = tableInfo.getVsl_row()+1;
		// 선박 cell 검색
		Vector<TableLocation> rowList = searchRow(xlsLocation.sheet, xlsLocation, vesselCount);
		logger.info("vesselCount:"+vesselCount+",searched row count:"+rowList.size());

		// 항구 cell 검색

		logger.info("portCount:"+portCount+",searched cell count:"+rowList.size());
		int rows=0;

		// 포트 정보 수집

		Vector<TableLocation> cellList = portInfoManager.searchPortCellInfo(xlsLocation, portCount);
		logger.debug("row:"+portInfoManager.rows);
		rows = portInfoManager.rows;
		rootElement = new Element("input");
		rootElement.setAttribute("type","xls");
		Element tableInfos = new Element("table");
		tableInfos.setAttribute("id",tableInfo.getTitle());
		rootElement.addContent(tableInfos);

		portarray = portInfoManager.getXMLPortInfo(xlsLocation, portCount);


		//  다중 항구 구분
		adjestMultiPort(portarray.getChildren());


		rootElement.addContent(portarray);

		for(;rows<rowList.size();rows++)
		{
			TableLocation loc=(TableLocation) rowList.get(rows);

			HSSFRow tempRow= (HSSFRow) loc.sheet.getRow(loc.row);
			if(tempRow==null)
				continue;
			schedule_row = new Element("vessel");
			rootElement.addContent(schedule_row);
			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);

				HSSFCell cell2 =tempRow.getCell(cellLocation.col);
				if(cell2==null)
					continue;
				if(this.getTableType()==TableLocation.BOTH)
				{
					if(cells==VESSEL_LOCATION)
					{	
						String data = xlsUitl.getStringData(cell2, false).toUpperCase();

						Vector keywordChip = new Vector();
						for(int i=0;i<data.length();i++)
						{
							String a = String.valueOf(data.charAt(i));

							if(vesselvoydivider.equals(a))
							{
								keywordChip.add(i);
							}
						}

						try{
							int index=0;

							if(keywordChip.size()!=0)
							{
								if(keywordChip.size()<vesselvoycount)
								{
									vesselvoycount=keywordChip.size();
								}
								try{

									index = (Integer)keywordChip.get(keywordChip.size()-vesselvoycount);
								}catch (Exception e) {
									index = (Integer)keywordChip.get(keywordChip.size()-vesselvoycount-1);
								}
							}

							schedule_row.setAttribute("name", data.substring(0,index).trim());
							schedule_row.setAttribute("voyage", data.substring(index+1,data.length()));
							logger.debug("vessel index:"+index+","+vesselvoycount);


						}catch(Exception e)//
						{
							logger.error(e.getMessage()+",Vessel/Voyagte 타입 검색시  오류 발생\nKeyword:"+ data+",구분자:"+vesselvoydivider);
							e.printStackTrace();
						}

					}else
					{
						addDateData(cells, cell2);
					}
				}else
				{
					if(cells==VESSEL_LOCATION)
					{
						schedule_row.setAttribute("name",xlsUitl.getVesselData(cell2).toUpperCase().trim());
					}else if(cells==VOYAGE_LOCATION)
					{
						schedule_row.setAttribute("voyage", xlsUitl.getVoyageData(cell2, true).toUpperCase());
					}
					else
					{
						addDateData(cells, cell2);
					}
				}
			}
			logger.debug("row create end:"+rows);
		}
		Document document = new Document(rootElement);
		Format format = Format.getPrettyFormat();

		format.setEncoding("EUC-KR");
		format.setIndent("\n");
		format.setIndent("\t");

		XMLOutputter outputter = new XMLOutputter(format);

		String strXml=outputter.outputString(document);

		logger.debug("<=====end>");
		return strXml;
	}
	private void adjestMultiPort(List port_array) {
		for(int i=0;i<port_array.size();i++)
		{
			Element sub = (Element) port_array.get(i);
			String port_name = sub.getAttributeValue("field");
			int type =property.getPort_type();
			try {
				switch (type) 
				{
				case KSGPropertis.PROPERTIES_DIVIDER_NOMAL:

					break;
				case KSGPropertis.PROPERTIES_DIVIDER_DOT:
					StringTokenizer stDOT = new StringTokenizer(port_name,",");

					if(stDOT.countTokens()>1)
					{
						if(port_name.contains("ETA")&&port_name.contains("ETD"))
						{
							sub.addContent(addSubPort(port_name));
						}else
						{
							if(stDOT.hasMoreTokens())
								sub.setAttribute("multi", String.valueOf(true));
							while(stDOT.hasMoreTokens())
							{
								sub.addContent(addSubPort(stDOT.nextToken().trim()));
							}	
						}
					}
					break;
				case KSGPropertis.PROPERTIES_DIVIDER_SLASH:
					StringTokenizer st = new StringTokenizer(port_name,"/");

					if(st.countTokens()>1)
					{
						if(port_name.contains("ETA")&&port_name.contains("ETD"))
						{
							sub.addContent(addSubPort(port_name));
						}else
						{
							if(st.hasMoreTokens())
								sub.setAttribute("multi", String.valueOf(true));
							while(st.hasMoreTokens())
							{					
								sub.addContent(addSubPort(st.nextToken().trim()));
							}	
						}

					}
					break;
				case KSGPropertis.PROPERTIES_DIVIDER_BRACKETS:

					if(port_name.contains(" "))
					{						
						port_name=port_name.replace(" ", "_");
					}			

					boolean isMatch = Pattern.matches(datePattern, port_name);

					if(isMatch)
					{
						String port_name1 =port_name.substring(0,port_name.indexOf("(")).trim();
						String port_name2 =port_name.substring(port_name.indexOf("(")+1,port_name.indexOf(")")).trim();

						port_name1=port_name1.replace("_", " ");
						port_name2=port_name2.replace("_", " ");
						sub.setAttribute("multi", String.valueOf(true));
						sub.addContent(addSubPort(port_name1.trim()));
						sub.addContent(addSubPort(port_name2.trim()));
					}
					break;		
				}


			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}
	private void addDateData(int cells, HSSFCell cell2) 
	{
		Element portinfo = new Element("input_date");
		portinfo.setAttribute("index",String.valueOf(cells));

		String date = xlsUitl.getStringData(cell2,true);

		// 공백 및 기타 문자 처리
		date= date.trim();
		date= date.replace(" ", "");
		date= date.replace("~", "-");



		try 
		{
			StringTokenizer tokenizer =new StringTokenizer(date,"/");
			String month2= tokenizer.nextToken();
			String day2= tokenizer.nextToken();

			//윤년 처리
			if(Integer.parseInt(month2)==2&&Integer.parseInt(day2)==29)
			{
				logger.info("윤년 처리:"+date);
				portinfo.setAttribute("date",Integer.parseInt(month2)+"/"+Integer.parseInt(day2));
				SimpleDateFormat ormat1 = new SimpleDateFormat("yyyy");
				Date d =KSGDateUtil.toDateBySearch(date); 
				String year2 = ormat1.format(d);
				portinfo.setAttribute("date2",year2+"-"+month2+"-"+day2);

				schedule_row.addContent(portinfo);
				return;
			}

			Date d =KSGDateUtil.toDateBySearch(date); 
			date=d.toString();

			SimpleDateFormat ormat = new SimpleDateFormat("M");

			String month = ormat.format(d);
			ormat.applyPattern("dd");
			String day = ormat.format(d);

			portinfo.setAttribute("date",month+"/"+day);
		} 
		catch (ParseException e) 
		{	
			// 날짜 오류시
			//logger.error(e.getMessage());
			date= e.getMessage();
			portinfo.setAttribute("date",date);

//			e.printStackTrace();

		}catch(NoSuchElementException e)
		{
			//logger.error(e.getMessage());
			//System.err.println("date pasing error (noSuchElement):"+date);
			portinfo.setAttribute("date",date);
			portinfo.setAttribute("date2",date);
		}
		catch(NumberFormatException e)
		{
			//logger.error(e.getMessage());
			//System.err.println("date pasing error (NumberFormat):"+date);
			portinfo.setAttribute("date",date);
			portinfo.setAttribute("date2",date);
		}

		portinfo.setAttribute("date2",date);



		schedule_row.addContent(portinfo);
	}

	public Element getVesselElement() 
	{
		return rootElement;
	}
	public Element getRootElement() 
	{
		initProperty();

		logger.debug("<create xml start>");
		int portCount=tableInfo.getPort_col()+tableInfo.getOthercell();
		int vesselCount = tableInfo.getVsl_row()+1;
		// 선박 cell 검색
		Vector<TableLocation> rowList = searchRow(xlsLocation.sheet, xlsLocation, vesselCount);
		logger.info("vesselCount:"+vesselCount+",searched row count:"+rowList.size());

		// 항구 cell 검색

		logger.info("portCount:"+portCount+",searched cell count:"+rowList.size());
		int rows=0;

		// 포트 정보 수집

		Vector<TableLocation> cellList = portInfoManager.searchPortCellInfo(xlsLocation, portCount);
		rows = portInfoManager.rows;
		Element rootElement = new Element("input");
		rootElement.setAttribute("type","xls");
		Element tableInfos = new Element("table");
		tableInfos.setAttribute("id",tableInfo.getTitle());
		rootElement.addContent(tableInfos);

		portarray = portInfoManager.getXMLPortInfo(xlsLocation, portCount);


		//  다중 항구 구분
		adjestMultiPort(portarray.getChildren());


		rootElement.addContent(portarray);

		for(;rows<rowList.size();rows++)
		{
			TableLocation loc=(TableLocation) rowList.get(rows);

			HSSFRow tempRow= (HSSFRow) loc.sheet.getRow(loc.row);
			schedule_row = new Element("vessel");
			rootElement.addContent(schedule_row);
			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);

				HSSFCell cell2 =tempRow.getCell(cellLocation.col);
				if(this.getTableType()==TableLocation.BOTH)
				{
					if(cells==VESSEL_LOCATION)
					{	
						String data = xlsUitl.getStringData(cell2, false).toUpperCase();

						Vector keywordChip = new Vector();
						for(int i=0;i<data.length();i++)
						{
							String a = String.valueOf(data.charAt(i));

							if(vesselvoydivider.equals(a))
							{
								keywordChip.add(i);
							}
						}

						try{
							int index=0;

							if(keywordChip.size()!=0)
							{
								if(keywordChip.size()<vesselvoycount)
								{
									vesselvoycount=keywordChip.size();
								}
								try{

									index = (Integer)keywordChip.get(keywordChip.size()-vesselvoycount);
								}catch (Exception e) {
									index = (Integer)keywordChip.get(keywordChip.size()-vesselvoycount-1);
								}
							}

							schedule_row.setAttribute("name", data.substring(0,index));
							schedule_row.setAttribute("voyage", data.substring(index+1,data.length()));
							logger.debug("vessel index:"+index+","+vesselvoycount);


						}catch(Exception e)//
						{
							logger.error(e.getMessage()+",Vessel/Voyagte 타입 검색시  오류 발생\nKeyword:"+ data+",구분자:"+vesselvoydivider);
							e.printStackTrace();
						}

					}else
					{
						addDateData(cells, cell2);
					}
				}else
				{
					if(cells==VESSEL_LOCATION)
					{
						schedule_row.setAttribute("name",xlsUitl.getVesselData(cell2).toUpperCase());
					}else if(cells==VOYAGE_LOCATION)
					{
						schedule_row.setAttribute("voyage", xlsUitl.getVoyageData(cell2, true).toUpperCase());
					}
					else
					{
						addDateData(cells, cell2);
					}
				}
			}
		}
		return rootElement;
	}

	public Object getXLSTitle() {
		return xlsUitl.getStringData(xlsLocation.getTitleCell(), false);
	}
	public Object getXLSTitle(int i) {
		return xlsUitl.getStringData(xlsLocation.getTitleCell(i), false);
	}
	private void initProperty()
	{
		try {
			logger.debug("start");
			property = propertyManager.getKSGTableProperty(table_id);
			if(property==null)
			{
				this.setHasVoy(false);
				this.setUnderPort(false);

				Table_Property p = new Table_Property();
				p.setTable_type(1);
				p.setUnder_port(0);
				p.setVesselvoycount(0);
				p.setTable_id(this.table_id);
				p.setVesselvoydivider(" ");
				p.setCompany_abbr(this.getTableInfo().getCompany_abbr());
				p.setPort_type(0);

				_tableService.insertTableProperty(p);

			}else
			{
				this.setHasVoy(property.getVoyage()==1?true:false);
				this.setUnderPort(property.getUnder_port()==1?true:false);
				this.setTableType(property.getTable_type());
				this.setVesselvoydivider(property.getVesselvoydivider());
				this.setVesselvoycount(property.getVesselvoycount());
				logger.debug("init table type:"+property.getTable_type());
			}
			tableInfo=_tableService.getTableById(table_id);



		} catch (SQLException e) 
		{
			e.printStackTrace();
			hasVoy 		= Boolean.parseBoolean(propertis.getValues(KSGPropertis.PROPERTIES_VOY).toString());
		}

		logger.debug("end");
	}

	/**
	 * @param sheet
	 * @param location
	 * @param vesselCount
	 * @return
	 */
	private Vector<TableLocation> searchRow(Sheet sheet, TableLocation location,
			int vesselCount) {
		logger.debug("start");
		Vector<TableLocation> data  = new Vector<TableLocation>();
		int rowTempIndex=location.row;
		int vCount=0;
		int emptyCount=0;
		while(vCount!=vesselCount)
		{
			if(rowTempIndex>sheet.getLastRowNum())
				break;
			/*if(emptyCount>2)
				break;*/
			HSSFRow ro= (HSSFRow) sheet.getRow(rowTempIndex);
			if(ro!=null&&!ro.getZeroHeight())
			{
				HSSFCell firstcell=ro.getCell(location.col);

				if(firstcell==null||firstcell.getCellType()==HSSFCell.CELL_TYPE_BLANK)
				{	
				}else
				{
					// 공백이 아니면 리스트에 추가
					TableLocation cc = new TableLocation();
					cc.row=rowTempIndex;
					cc.col=location.col;
					cc.sheet=sheet;
					data.add(cc);
					vCount++;
				}
			}
			// row인덱스 증가
			rowTempIndex++;

		}
		logger.debug("end:"+data.size());
		return data;
	}



	/**
	 * @param location
	 */
	public void setLocation(TableLocation location) {
		this.xlsLocation = location;
	}

	/**
	 * @param table
	 */
	public void setTableInfo(ShippersTable table)
	{
		tableInfo=table;
	}
	public void setTableType(int tableType) 
	{
		this.xlsLocation.setTableType(tableType);
		this.tableType = tableType;


	}


}
