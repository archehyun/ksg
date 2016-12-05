package com.ksg.xls;

import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Sheet;
import org.jdom.Element;

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.Table_Property;
import com.ksg.view.util.KSGPropertis;
import com.ksg.view.util.KSGPropertyManager;
import com.ksg.xls.model.TableLocation;

public class XLSSearchPortInfoManager {

	private BaseService baseService;
	public String bothKeyWord[];
	private boolean hasVoy=true;
	private boolean isUnderPort=false;

	private boolean isETA_ETD=false;
	private Logger logger = Logger.getLogger(this.getClass());
	private KSGPropertis propertis = KSGPropertis.getIntance();
	KSGPropertyManager propertyManager =KSGPropertyManager.getInstance();
	public Element rootElement;
	public int rows;
	String table_id;
	int tableType;
	public String vesselKeyWord[];
	private int VOY_PARAMETER=1;
	XLSStringUtil xlsUitl;

	/**
	 * @param table_id
	 */
	public XLSSearchPortInfoManager(String table_id) 
	{
		
		logger.debug("start");
		xlsUitl = new XLSStringUtil();
		baseService = DAOManager.getInstance().createBaseService();
		this.table_id=table_id  ;
		initProperty();
		try {
			List vesselKeyList=baseService.getKeywordList("VESSEL");
			List voyageKeyList=baseService.getKeywordList("VOYAGE");
			List bothKeyList=baseService.getKeywordList("BOTH");
			vesselKeyWord = new String[vesselKeyList.size()];
			bothKeyWord = new String[bothKeyList.size()];

			for(int i=0;i<vesselKeyList.size();i++)
			{
				vesselKeyWord[i]= vesselKeyList.get(i).toString();
			}
			for(int i=0;i<bothKeyList.size();i++)
			{
				bothKeyWord[i]= bothKeyList.get(i).toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("end");
	}

	/**
	 * @param num
	 * @param cells
	 * @param cell_info
	 * @param type
	 */
	private void addXMLPort(int num, int cells, HSSFCell cell_info, String type) {
		try{
			logger.debug("add port");
			if(cell_info==null)
				return;
			if(this.getPortName(cell_info,true,num).length()==0)
				return;

			Element portinfo = new Element("port1");

			portinfo.setAttribute("field",this.getPortName(cell_info,true,num));
			portinfo.setAttribute("type",type);
			portinfo.setAttribute("isUnderPort",String.valueOf(isUnderPort));

			portinfo.setAttribute("index",String.valueOf(cells));
			portinfo.setAttribute("multi",String.valueOf(false));
			rootElement.addContent(portinfo);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void addXMLPort(int num, int cells, String cell_info, String type) {
		if(cell_info==null||cell_info.length()==0)
			return;
		Element portinfo = new Element("port1");

		portinfo.setAttribute("field",cell_info);
		portinfo.setAttribute("type",type);
		portinfo.setAttribute("isUnderPort",String.valueOf(isUnderPort));
		/*if(hasVoy)
			cells++;*/
		portinfo.setAttribute("index",String.valueOf(cells));
		portinfo.setAttribute("multi",String.valueOf(false));
		rootElement.addContent(portinfo);
	}




	/**
	 * 
	 * @add_date 2010.8.19
	 * @param cell
	 * @param b
	 * @param portRange
	 * @return
	 */
	private String getPortName(HSSFCell cell, boolean b, int portRange) throws NullPointerException
	{
		//if()
		if(cell==null)
			return "";

		int row = cell.getRowIndex();
		int col = cell.getColumnIndex();
		String port_name="";


		if(isUnderPort)
		{
//			return XLSStringUtil.getStringData(cell.getSheet().getRow(row).getCell(col),b).trim();
			return xlsUitl.getPortName(cell.getSheet().getRow(row).getCell(col),b).trim();
		}else
		{
			try{
				for(int i=row;i<row+portRange-1;i++)
				{
					HSSFRow rows = cell.getSheet().getRow(i);

					if(rows==null)
						continue;
					HSSFCell cells = rows.getCell(col);
					if(cells==null)
						continue;

					String sub_port_name=xlsUitl.getPortName(cells,b);

					if(sub_port_name.contains("\n"))
					{
						StringTokenizer stringTokenizer = new StringTokenizer(sub_port_name,"\n");
						String temp="";
						while(stringTokenizer.hasMoreTokens())
						{
							String temp_port = stringTokenizer.nextToken();

							if(!temp_port.equals("-"))
								temp+=temp_port;
							if(stringTokenizer.hasMoreTokens())
								temp +=" ";
						}
						sub_port_name=temp;

					}
					if(!sub_port_name.equals("-"))
						port_name+=sub_port_name+" ";

				}
				return port_name.trim();
			}catch (NullPointerException e)
			{
				e.printStackTrace();
				logger.error("port:"+xlsUitl.getPortName(cell, b));
				return xlsUitl.getPortName(cell, b);
			}
		}
	}

	/**
	 * @param location
	 * @param cellLocation
	 * @return
	 */
	private HSSFCell getUnderCell(TableLocation location,
			TableLocation cellLocation) {
		HSSFRow underRow=(HSSFRow) location.sheet.getRow(cellLocation.row+1);

		/**
		 * 이중 항구 처리시 
		 * 항구이름이 아래 위로 구성 되어 있는 지 판다하기 위해서 키워드 cell의 아래 셀이 공백인지 확인
		 * 병합 되어 있거나 공백이면 2개의 셀이 하나의 하나의 항구명으로 구성 되어 있는 것임
		 * 
		 * 향후에.. 비어 있는 셀의 위치에 따라 항구명의 전체 파악이 가능하게 해야 함.
		 * 
		 */

		HSSFCell keywordunderCell=underRow.getCell(location.col);
		if(keywordunderCell!=null&&keywordunderCell.getCellType()!=HSSFCell.CELL_TYPE_BLANK){
			return null;
		}

		HSSFCell undercell =underRow.getCell(cellLocation.col);


		return undercell;
	}

	/**
	 * @param location
	 * @param portCount
	 * @return
	 */
	public Element getXMLPortInfo(TableLocation location,int portCount)
	{
		try{
		logger.debug("start");
		this.initProperty();
		rootElement = new Element("port_array");
		Vector<TableLocation> cellList;
		int num=searchPortRanageNumber(location.sheet, location);
		if(isUnderPort)
		{
			cellList = searchDownPortCell(location.sheet,location, portCount,0);

			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);

				HSSFRow fRow=(HSSFRow) location.sheet.getRow(cellLocation.row);
				HSSFCell cell_info =fRow.getCell(cellLocation.col);

				switch (location.getTableType()) 
				{

				case TableLocation.VESSEL:

					if(cells!=XLSTableInfo.VOYAGE_LOCATION&&cells!=XLSTableInfo.VESSEL_LOCATION) //voy
					{
						addXMLPort(num, cells-1, cell_info,"VESSEL");
					}	
					break;
				case TableLocation.VOYAGE:
					if(cells!=XLSTableInfo.VESSEL_LOCATION) //voy
					{
						addXMLPort(num, cells-1, cell_info,"VOYAGE");
					}	
					break;
				case TableLocation.BOTH:
					if(cells>XLSTableInfo.VESSEL_LOCATION)
					{
						logger.debug("cell location "+cellLocation.row+","+cellLocation.col);
						String name1=xlsUitl.getStringData(fRow.getCell(cellLocation.col), true);
						if(isETA_ETD&&name1.contains("ETA"))
						{
							HSSFRow ffRow=(HSSFRow) location.sheet.getRow(cellLocation.row-1);

							String name=xlsUitl.getStringData(ffRow.getCell(cellLocation.col), true);
							name+=" ETA";
							addXMLPort(num, cells-1, name,"BOTH");
							cells++;

						}else
						{
							addXMLPort(num, cells-1, cell_info,"BOTH");
						}
					}
				}
			}

		}else
		{
			cellList = searchNomalPortCell(location.sheet,location, portCount,0);
			boolean flag=false;
			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);
				HSSFRow fRow= (HSSFRow) location.sheet.getRow(cellLocation.row);
				HSSFCell cell_info =fRow.getCell(cellLocation.col);


				switch (location.getTableType()) {
				case TableLocation.VESSEL:
					if(cells==XLSTableInfo.VESSEL_LOCATION)
					{
						HSSFCell undercell = getUnderCell(location,cellLocation);
						if(undercell==null||undercell.getCellType()==HSSFCell.CELL_TYPE_BLANK){
							flag=true;
						}
					}

					if(cells!=XLSTableInfo.VOYAGE_LOCATION)
					{
						// 이중 항구 처리
						if(KSGPropertis.DOUBLE_LINE&&flag)
						{
							HSSFCell undercell = getUnderCell(location,cellLocation);

							if(undercell!=null&&undercell.getCellType()!=HSSFCell.CELL_TYPE_BLANK)
							{	
							}
							else if(cells!=XLSTableInfo.VOYAGE_LOCATION&&cells!=XLSTableInfo.VESSEL_LOCATION) //voy
							{
								addXMLPort(num, cells-1, cell_info,"VESSEL");
							}
						}else
						{
							if(cells!=XLSTableInfo.VOYAGE_LOCATION&&cells!=XLSTableInfo.VESSEL_LOCATION) //voy
							{
								addXMLPort(num, cells-1, cell_info,"VESSEL");
							}
						}				
					}	
					break;
				case TableLocation.VOYAGE:
					if(cells!=XLSTableInfo.VESSEL_LOCATION&&cells!=XLSTableInfo.VOYAGE_LOCATION) //voy					
					{
						addXMLPort(num, cells, cell_info, "VOYAGE");
					}	
					break;
				case TableLocation.BOTH:

					logger.debug("cell location "+cellLocation.row+","+cellLocation.col);
					if(KSGPropertis.DOUBLE_LINE)
					{
						HSSFCell undercell = getUnderCell(location,cellLocation);

						if(undercell!=null&&undercell.getCellType()!=HSSFCell.CELL_TYPE_BLANK)
						{

						}else if(cells!=XLSTableInfo.VOYAGE_LOCATION&&cells!=XLSTableInfo.VESSEL_LOCATION) //voy
						{
							addXMLPort(num, cells-1, cell_info,"BOTH");
						}
					}else
					{
						if(cells!=XLSTableInfo.VOYAGE_LOCATION&&cells!=XLSTableInfo.VESSEL_LOCATION) //voy
						{
							addXMLPort(num, cells, cell_info,"BOTH");
						}
					}
					if(cells==XLSTableInfo.VOYAGE_LOCATION) //voy
					{
						if(hasVoy)
							addXMLPort(num, cells-1, cell_info,"BOTH");
					}
				}
			}

		}
		logger.debug("end:"+rootElement.getChild("port1"));
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return rootElement;
	}

	/**
	 * 
	 */
	private void initProperty()
	{
		try {
			Table_Property property=propertyManager.getKSGTableProperty(table_id);
			if(property==null)
			{				
				this.hasVoy=false;
				this.isUnderPort=false;
			}else
			{

				this.hasVoy=property.getVoyage()==1?true:false;
				this.isUnderPort = property.getUnder_port()==0?false:true;
				this.tableType = property.getTable_type();
				this.isETA_ETD = property.getEta()==0?false:true;

			}

		} catch (SQLException e) 
		{
			hasVoy 		= Boolean.parseBoolean(propertis.getValues(KSGPropertis.PROPERTIES_VOY).toString());
		}
	}	

	/**
	 * @param sheet
	 * @param location
	 * @param portCount
	 * @param i
	 * @return
	 */
	private Vector<TableLocation> searchNomalPortCell(Sheet sheet, 
			TableLocation location, int portCount, int i) {

		this.initProperty();
		int pCount=0;
		int cellTempIndex = location.col;// 첫번째
		HSSFRow ro=null;

		ro= (HSSFRow) sheet.getRow(location.row+i);

		Vector<TableLocation> data = new Vector<TableLocation>();
		logger.debug("<port loacation"+location+",portCount:"+portCount+", search start>");


		int num = searchPortRanageNumber(sheet, location);
		//voyage 관련 수정 : 201008012		
		if(hasVoy)
		{
			logger.debug("not have voyage cell, decrease port Count "+portCount+","+(portCount-1));
			portCount-=1;
		}

		while(pCount<portCount+2)
		{
			if(cellTempIndex>ro.getLastCellNum())
				break;

			HSSFCell firstcell=ro.getCell(cellTempIndex);
			if(firstcell!=null&&firstcell.getCellStyle().getHidden()&&firstcell.getSheet().isColumnHidden(firstcell.getColumnIndex()))
			{	
				// 숨김 칼럼이면 Pass
				cellTempIndex++;
				continue;
			}
			if(firstcell!=null&&firstcell.getCellType()!=HSSFCell.CELL_TYPE_BLANK)
			{
				// 공백이 아니면 리스트에 추가

				TableLocation cc = new TableLocation();
				cc.col=cellTempIndex;
				cc.row=location.row;

				//logger.debug("port location add "+"["+cc.row+","+cc.col+"]");

				if(!xlsUitl.getPortName(firstcell,false).equals("-"))
				{
					data.add(cc);
					pCount++;
				}
			}
			// cell인덱스 증가
			cellTempIndex++;
		}
		// voyage 생략되어 있을시에
		checkVoy(data);
		return data;
	}

	private void checkVoy(Vector<TableLocation> data) {
		if(hasVoy)
		{
			TableLocation vesselKey= (TableLocation) data.get(0);// Vessel Keyword
			TableLocation voyKey  = new TableLocation();

			voyKey.setHasVoy(false);
			voyKey.col=vesselKey.col+1;
			voyKey.row=vesselKey.row;
			Vector<TableLocation> temp = new Vector<TableLocation>();
			
			temp.add(vesselKey);
			temp.add(voyKey);
			data.add(VOY_PARAMETER, voyKey);

			for(int i=1;i<data.size();i++)
			{
				TableLocation t=data.get(i);
				t.col+=1;
				temp.add(t);
			}
			
			data=temp;


			// 수정전 //20100812

			/**************************************************
			 * apl_6 작업 시 마지막 항구로 표시 안 되는 형상 발생(13,15,17)
			 * 
			 * 단순 마지막 제거에서 크기 변경이 있을 경우에 만 제거 하는 것으로 변
			 *******************************************************/

			// 수정후//20100812


		}
	}
	//
	/**
	 * @param sheet
	 * @param location
	 * @param portCount
	 * @param i
	 * @return
	 */
	private Vector<TableLocation> searchDownPortCell(Sheet sheet, 
			TableLocation location, int portCount, int i) {
		this.initProperty();
		logger.debug("start");
		HSSFRow rowUp=null;
		Vector<TableLocation> data = new Vector<TableLocation>();
		rowUp= (HSSFRow) sheet.getRow(location.row);
		HSSFRow rowDown=null;
		rowDown= (HSSFRow) sheet.getRow(location.row+1);
		logger.debug("<port loacation"+location+" search start>");


		int pCount=0;
		int cellTempIndex = location.col;// 첫번째

		if(hasVoy)
		{
			logger.debug("not have voyage cell, decrease port Count "+portCount+","+(portCount-1));
			portCount-=1;
		}

		while(pCount!=portCount+2)
		{
			if(cellTempIndex>rowUp.getLastCellNum())
				break;

			if(sheet.isColumnHidden(cellTempIndex))
			{
				cellTempIndex++;
				continue;
			}

			HSSFCell firstcell=rowUp.getCell(cellTempIndex);


			if(firstcell==null||firstcell.getCellType()==HSSFCell.CELL_TYPE_BLANK) // 위 공백
			{	

				HSSFCell secondcell=rowDown.getCell(cellTempIndex);
				if(secondcell==null||secondcell.getCellType()==HSSFCell.CELL_TYPE_BLANK)// 아래 공백
				{
					// 처리 안함
				}else // 아래 데이터 있음
				{
					// 아래 항구 사용

					TableLocation cc = new TableLocation();
					cc.col=cellTempIndex;
					cc.row = rowDown.getRowNum();
					data.add(cc);
					pCount++;

				}
			}else // 위 데이터 있음
			{

				HSSFCell secondcell=rowDown.getCell(cellTempIndex);
				if(secondcell==null||secondcell.getCellType()==HSSFCell.CELL_TYPE_BLANK)// 아래 공백
				{
					//위 항구 사용		
					TableLocation cc = new TableLocation();
					cc.col=cellTempIndex;
					cc.row=rowUp.getRowNum();
					data.add(cc);
					pCount++;

				}else // 아래 데이터 있음
				{
					if(secondcell.getCellStyle().getHidden())
					{
						cellTempIndex++;
						continue;
					}

					//아래 항구 사용 -옵션 처리-

					if(!xlsUitl.getPortName(firstcell,false).equals("-"))
					{
						TableLocation cc = new TableLocation();
						cc.col=cellTempIndex;
						cc.row=rowDown.getRowNum();
						data.add(cc);
						pCount++;
					}
				}
			}
			cellTempIndex++;
		}
		// voyage 생략되어 있을시에
		checkVoy(data);
		logger.debug("end:"+data);
		return data;
	}
	/**
	 * @param location
	 * @param portCount
	 * @return
	 */
	private void addPortFieldElement(String portName)
	{
		try{

			if(portName.length()==0)
				return;
			Element portinfo = new Element("port1");
			portinfo.setAttribute("field",portName);
			rootElement.addContent(portinfo);
			logger.debug("add port field:"+portName);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public Vector<TableLocation> searchPortCellInfo(TableLocation location,int portCount)
	{
		this.initProperty();
		logger.debug("start");
		rootElement = new Element("port_array");
		Vector<TableLocation> cellList;
		int num=searchPortRanageNumber(location.sheet, location);
		if(isUnderPort)
		{
			cellList = searchDownPortCell(location.sheet,location, portCount,0);

			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);

				HSSFRow fRow=(HSSFRow) location.sheet.getRow(cellLocation.row);
				HSSFCell cell_info =fRow.getCell(cellLocation.col);

				switch (location.getTableType()) {
				case TableLocation.VESSEL:
					if(cells!=XLSTableInfo.VOYAGE_LOCATION) //voy
					{
						logger.debug("cell location "+cellLocation.row+","+cellLocation.col+","+xlsUitl.getPortName(cell_info,true));
						addPortFieldElement(xlsUitl.getPortName(cell_info,true));
					}
					break;
				case TableLocation.VOYAGE:
					if(cells!=XLSTableInfo.VESSEL_LOCATION) //voy
					{
						logger.debug("cell location "+cellLocation.row+","+cellLocation.col+","+xlsUitl.getPortName(cell_info,true));
						addPortFieldElement(xlsUitl.getPortName(cell_info,true));
					}
					break;
				case TableLocation.BOTH:
					if(cells>XLSTableInfo.VESSEL_LOCATION)
					{
						logger.debug("cell location "+cellLocation.row+","+cellLocation.col+","+xlsUitl.getPortName(cell_info,true));

						if(isETA_ETD)
						{
							HSSFCell ETDcell=fRow.getCell(cellLocation.col+1);
							String etdDate =xlsUitl.getStringData(ETDcell,true);

							if(xlsUitl.getPortName(cell_info,true).equals("-")&&etdDate.equals("-"))							
							{
								addPortFieldElement("-");

							}else
							{
								addPortFieldElement(xlsUitl.getPortName(cell_info,true)+"-"+etdDate);	
							}
						}else
						{
							addPortFieldElement(xlsUitl.getPortName(cell_info,true));
						}
					}
				}
			}

			rows=1;

		}else // 하위 항구가 아닐때
		{
			cellList = searchNomalPortCell(location.sheet,location, portCount,0);
			
			boolean flag=false;
			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);
				HSSFRow fRow= (HSSFRow) location.sheet.getRow(cellLocation.row);
				HSSFCell cell_info =fRow.getCell(cellLocation.col);

				switch (location.getTableType()) {
				case TableLocation.VESSEL:
					if(cells==XLSTableInfo.VESSEL_LOCATION)
					{
						HSSFCell undercell = getUnderCell(location,
								cellLocation);
						if(undercell==null||undercell.getCellType()==HSSFCell.CELL_TYPE_BLANK){
							flag=true;
						}
					}

					if(cells==XLSTableInfo.VOYAGE_LOCATION)
					{
						if(cellLocation.isHasVoy())
						{
							addPortFieldElement(this.getPortName(cell_info,true,num));
						}
					}else
					{
						// 이중 항구 처리
						if(KSGPropertis.DOUBLE_LINE&&flag)
						{
							HSSFCell undercell = getUnderCell(location,cellLocation);

							if(undercell!=null&&undercell.getCellType()!=HSSFCell.CELL_TYPE_BLANK)
							{	
								// 처리 안함
							}
							else
							{							
								addPortFieldElement(this.getPortName(cell_info,true,num));
							}

						}else
						{
							addPortFieldElement(this.getPortName(cell_info,true,num));
						}						
					}	
					break;
				case TableLocation.VOYAGE:
					if(cells==XLSTableInfo.VESSEL_LOCATION||cells==XLSTableInfo.VOYAGE_LOCATION) //voy
					{

					}else
					{
						String portName = this.getPortName(cell_info,true, num);
						logger.debug("add port Name :"+portName);
						addPortFieldElement(this.getPortName(cell_info,true,num));
					}	
					break;
				case TableLocation.BOTH:
					if(cells!=XLSTableInfo.VESSEL_LOCATION)
					{
						logger.debug("cell location "+cellLocation.row+","+cellLocation.col);
						if(KSGPropertis.DOUBLE_LINE)
						{
							HSSFCell undercell = getUnderCell(location,cellLocation);

							if(undercell!=null&&undercell.getCellType()!=HSSFCell.CELL_TYPE_BLANK)
							{

							}else
							{
								try{
									addPortFieldElement(this.getPortName(cell_info,true,num));
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
						}else
						{
							try{
								if(cell_info!=null)
								addPortFieldElement(this.getPortName(cell_info,true,num));
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			rows=1;
		}

		logger.debug("end:");
		return cellList;
	}

	/**
	 * 
	 * 항구 범위 검색
	 * @date 2010.8.19
	 * @param sheet
	 * @param location
	 * @return
	 */
	private int searchPortRanageNumber(Sheet sheet, TableLocation location) {
		logger.debug("start:"+location.row);
		boolean isPortRange = true;
		int portLoaction;

		for(portLoaction=1;isPortRange;portLoaction++)
		{
			if(portLoaction>5)
			{
				portLoaction=2;
				break;
			}

			HSSFRow row=(HSSFRow) sheet.getRow(location.row+portLoaction);
			if(row==null)
				continue;
			String temp=xlsUitl.getColumString(row.getCell(location.col));

			if(temp.length()==0)
				continue;
			else
			{
				for(int z=0;z<vesselKeyWord.length;z++)
				{
					if(!temp.trim().toLowerCase().equals(vesselKeyWord[z].trim().toLowerCase()))
					{
						isPortRange=false;
						break;
					}
				}
				for(int z=0;z<bothKeyWord.length;z++)
				{
					if(!temp.trim().equals(bothKeyWord[z].trim()))
					{
						isPortRange=false;
						break;
					}
				}
			}
		}

		logger.debug("end:"+portLoaction);
		return portLoaction;
	}

	/**
	 * @param sheet
	 * @param location
	 * @param portCount
	 * @param i
	 * @return
	 */


}
