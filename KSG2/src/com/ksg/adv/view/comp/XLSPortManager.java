package com.ksg.adv.view.comp;

import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Sheet;
import org.jdom.Element;

import com.ksg.adv.logic.model.TableLocation;
import com.ksg.common.util.KSGPropertis;
import com.ksg.common.util.KSGPropertyManager;
import com.ksg.domain.Table_Property;

/**
 * ���� ���Ͽ��� �ױ��� �˻�
 * @author archehyun
 *
 */
public class XLSPortManager {

	public String bothKeyWord[];
	
	private boolean hasVoy=true;
	
	private boolean isUnderPort=false;

	private boolean isETA_ETD=false;
	
	private Logger logger = Logger.getLogger(this.getClass());	
	
	private KSGPropertis propertis = KSGPropertis.getIntance();
	
	KSGPropertyManager propertyManager =KSGPropertyManager.getInstance();
	
	public Element rootElement;
	
	public int rows;
	
	private String table_id;
	
	private int tableType;
	
	public String vesselKeyWord[];
	
	private int VOY_PARAMETER=1;
	
	private XLSStringUtil xlsUitl;
	
	KeyWordManager keyWordManager = KeyWordManager.getInstance();

	/**
	 * @param table_id
	 */
	public XLSPortManager(String table_id) 
	{
		
		logger.debug("start");
		
		xlsUitl = new XLSStringUtil();
		
		this.table_id=table_id;
		
		initProperty();
		
		vesselKeyWord = keyWordManager.getVesselKeyWord();
		
		bothKeyWord = keyWordManager.getBothKeyWord();
		
	}

	/**
	 * @param num
	 * @param cells
	 * @param cell_info
	 * @param type
	 */
	private void addXMLPort(int num, int cells, HSSFCell cell_info, String type) {
		try{
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
		if(cell==null)
			return "";

		int row = cell.getRowIndex();
		int col = cell.getColumnIndex();
		String port_name="";


		if(isUnderPort)
		{
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
		HSSFRow underRow=(HSSFRow) location.getSheet().getRow(cellLocation.getRow()+1);

		/**
		 * ���� �ױ� ó���� 
		 * �ױ��̸��� �Ʒ� ���� ���� �Ǿ� �ִ� �� �Ǵ��ϱ� ���ؼ� Ű���� cell�� �Ʒ� ���� �������� Ȯ��
		 * ���� �Ǿ� �ְų� �����̸� 2���� ���� �ϳ��� �ϳ��� �ױ������� ���� �Ǿ� �ִ� ����
		 * 
		 * ���Ŀ�.. ��� �ִ� ���� ��ġ�� ���� �ױ����� ��ü �ľ��� �����ϰ� �ؾ� ��.
		 * 
		 */

		HSSFCell keywordunderCell=underRow.getCell(location.getCol());
		if(keywordunderCell!=null&&keywordunderCell.getCellType()!=HSSFCell.CELL_TYPE_BLANK){
			return null;
		}

		HSSFCell undercell =underRow.getCell(cellLocation.getCol());


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
		
		Sheet sheet = location.getSheet();
		int num=searchPortRanageNumber(sheet, location);
		if(isUnderPort)
		{
			cellList = searchDownPortCell(sheet,location, portCount,0);

			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);

				HSSFRow fRow=(HSSFRow) sheet.getRow(cellLocation.getRow());
				HSSFCell cell_info =fRow.getCell(cellLocation.getCol());

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
						logger.debug("cell location "+cellLocation.getRow()+","+cellLocation.getCol());
						String name1=xlsUitl.getStringData(fRow.getCell(cellLocation.getCol()), true);
						if(isETA_ETD&&name1.contains("ETA"))
						{
							HSSFRow ffRow=(HSSFRow) sheet.getRow(cellLocation.getRow()-1);

							String name=xlsUitl.getStringData(ffRow.getCell(cellLocation.getCol()), true);
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
			cellList = searchNomalPortCell(sheet,location, portCount,0);
			boolean flag=false;
			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);
				HSSFRow fRow= (HSSFRow) sheet.getRow(cellLocation.getRow());
				HSSFCell cell_info =fRow.getCell(cellLocation.getCol());


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
						// ���� �ױ� ó��
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

					logger.debug("cell location "+cellLocation.getRow()+","+cellLocation.getCol());
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
		
		int cellTempIndex = location.getCol();// ù��°
		
		HSSFRow ro= (HSSFRow) sheet.getRow(location.getRow()+i);

		Vector<TableLocation> data = new Vector<TableLocation>();
		logger.debug("<port loacation"+location+",portCount:"+portCount+", search start>");


		int num = searchPortRanageNumber(sheet, location);
		//voyage ���� ���� : 201008012		
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
				// ���� Į���̸� Pass
				cellTempIndex++;
				continue;
			}
			if(firstcell!=null&&firstcell.getCellType()!=HSSFCell.CELL_TYPE_BLANK)
			{
				// ������ �ƴϸ� ����Ʈ�� �߰�

				TableLocation cc = new TableLocation();
				cc.setCol(cellTempIndex);
				cc.setRow(location.getRow());
				

				//logger.debug("port location add "+"["+cc.row+","+cc.col+"]");

				if(!xlsUitl.getPortName(firstcell,false).equals("-"))
				{
					data.add(cc);
					pCount++;
				}
			}
			// cell�ε��� ����
			cellTempIndex++;
		}
		// voyage �����Ǿ� �����ÿ�
		checkVoy(data);
		return data;
	}

	/**
	 * @param data
	 */
	private void checkVoy(Vector<TableLocation> data) {
		if(hasVoy)
		{
			TableLocation vesselKey= (TableLocation) data.get(0);// Vessel Keyword
			TableLocation voyKey  = new TableLocation();

			voyKey.setHasVoy(false);
			voyKey.setCol(vesselKey.getCol()+1);
			voyKey.setRow(vesselKey.getRow());			
			Vector<TableLocation> temp = new Vector<TableLocation>();
			
			temp.add(vesselKey);
			temp.add(voyKey);
			data.add(VOY_PARAMETER, voyKey);

			for(int i=1;i<data.size();i++)
			{
				TableLocation t=data.get(i);
				t.setCol(t.getCol()+1);				
				temp.add(t);
			}
			
			data=temp;


			// ������ //20100812

			/**************************************************
			 * apl_6 �۾� �� ������ �ױ��� ǥ�� �� �Ǵ� ���� �߻�(13,15,17)
			 * 
			 * �ܼ� ������ ���ſ��� ũ�� ������ ���� ��쿡 �� ���� �ϴ� ������ ��
			 *******************************************************/

			// ������//20100812


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
		rowUp= (HSSFRow) sheet.getRow(location.getRow());
		HSSFRow rowDown=null;
		rowDown= (HSSFRow) sheet.getRow(location.getRow()+1);
		logger.debug("<port loacation"+location+" search start>");


		int pCount=0;
		int cellTempIndex = location.getCol();// ù��°

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


			if(firstcell==null||firstcell.getCellType()==HSSFCell.CELL_TYPE_BLANK) // �� ����
			{	

				HSSFCell secondcell=rowDown.getCell(cellTempIndex);
				if(secondcell==null||secondcell.getCellType()==HSSFCell.CELL_TYPE_BLANK)// �Ʒ� ����
				{
					// ó�� ����
				}else // �Ʒ� ������ ����
				{
					// �Ʒ� �ױ� ���

					TableLocation cc = new TableLocation();
					
					cc.setCol(cellTempIndex);
					cc.setRow(rowDown.getRowNum());					
					data.add(cc);
					pCount++;

				}
			}else // �� ������ ����
			{

				HSSFCell secondcell=rowDown.getCell(cellTempIndex);
				if(secondcell==null||secondcell.getCellType()==HSSFCell.CELL_TYPE_BLANK)// �Ʒ� ����
				{
					//�� �ױ� ���		
					TableLocation cc = new TableLocation();
					
					cc.setCol(cellTempIndex);
					cc.setRow(rowDown.getRowNum());	
					
					data.add(cc);
					pCount++;

				}else // �Ʒ� ������ ����
				{
					if(secondcell.getCellStyle().getHidden())
					{
						cellTempIndex++;
						continue;
					}

					//�Ʒ� �ױ� ��� -�ɼ� ó��-

					if(!xlsUitl.getPortName(firstcell,false).equals("-"))
					{
						TableLocation cc = new TableLocation();
						cc.setCol(cellTempIndex);
						cc.setRow(rowDown.getRowNum());	
						
						data.add(cc);
						pCount++;
					}
				}
			}
			cellTempIndex++;
		}
		
		// voyage �����Ǿ� �����ÿ�
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
			//logger.debug("add port field:"+portName);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * @param location
	 * @param portCount
	 * @return
	 */
	public Vector<TableLocation> searchPortCellInfo(TableLocation location,int portCount)
	{
		this.initProperty();
		logger.debug("start");
		rootElement = new Element("port_array");
		Vector<TableLocation> cellList;
		
		Sheet sheet = location.getSheet();
		
		int num=searchPortRanageNumber(sheet, location);
		
		if(isUnderPort)
		{
			cellList = searchDownPortCell(sheet,location, portCount,0);

			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);

				HSSFRow fRow=(HSSFRow)sheet.getRow(cellLocation.getRow());
				HSSFCell cell_info =fRow.getCell(cellLocation.getCol());
				logger.debug("cell location "+cellLocation.getRow()+","+cellLocation.getCol()+","+xlsUitl.getPortName(cell_info,true));
				switch (location.getTableType()) {
				
				case TableLocation.VESSEL:
					if(cells!=XLSTableInfo.VOYAGE_LOCATION) //voy
					{
						addPortFieldElement(xlsUitl.getPortName(cell_info,true));
					}
					break;
				case TableLocation.VOYAGE:
					if(cells!=XLSTableInfo.VESSEL_LOCATION) //voy
					{
						addPortFieldElement(xlsUitl.getPortName(cell_info,true));
					}
					break;
				case TableLocation.BOTH:
					if(cells>XLSTableInfo.VESSEL_LOCATION)
					{

						if(isETA_ETD)
						{
							HSSFCell ETDcell=fRow.getCell(cellLocation.getCol()+1);
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

		}else // ���� �ױ��� �ƴҶ�
		{
			cellList = searchNomalPortCell(sheet,location, portCount,0);
			
			boolean flag=false;
			for(int cells =0;cells<cellList.size();cells++)
			{
				TableLocation cellLocation = (TableLocation) cellList.get(cells);
				HSSFRow fRow= (HSSFRow) sheet.getRow(cellLocation.getRow());
				HSSFCell cell_info =fRow.getCell(cellLocation.getCol());

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
						// ���� �ױ� ó��
						if(KSGPropertis.DOUBLE_LINE&&flag)
						{
							HSSFCell undercell = getUnderCell(location,cellLocation);

							if(undercell!=null&&undercell.getCellType()!=HSSFCell.CELL_TYPE_BLANK)
							{	
								// ó�� ����
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
						logger.debug("cell location "+cellLocation.getRow()+","+cellLocation.getCol());
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
	 * ���� ���� ���̺� �� �ױ� ���� �˻�
	 * @date 2010.8.19
	 * @param sheet
	 * @param location
	 * @return
	 */
	private int searchPortRanageNumber(Sheet sheet, TableLocation location) {
		logger.debug("start:"+location.getRow());
		
		boolean isPortRange = true;
		
		int portLoaction;

		for(portLoaction=1;isPortRange;portLoaction++)
		{
			// 5�� ������ ������
			if(portLoaction>5)
			{
				portLoaction=2;
				break;
			}

			HSSFRow row=(HSSFRow) sheet.getRow(location.getRow()+portLoaction);
			if(row==null)
				continue;
			String temp=xlsUitl.getColumString(row.getCell(location.getCol()));

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


}
