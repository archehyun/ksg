package com.ksg.adv.logic.xml;

import java.awt.Color;
import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.shippertable.service.TableService;
import com.ksg.shippertable.service.impl.TableServiceImpl;

public class KSGXMLManager {

	private TableService tableService;

	Logger logger = Logger.getLogger(this.getClass());

	private List<TablePort> portLi;

	public KSGXMLManager()
	{
		tableService = new TableServiceImpl();
	}
	/**
	 * @param tableInfoList
	 * @throws SQLException
	 */
	public void readFile(Vector tableInfoList) throws SQLException
	{	
		logger.debug("start");

		List li = new LinkedList();

		for(int i=0;i<tableInfoList.size();i++)
		{
			ShippersTable t=(ShippersTable) tableInfoList.get(i);
			List templi = tableService.getTableListByCompany(t.getCompany_abbr(),t.getPage());

			for(int j=0;j<templi.size();j++)
			{
				li.add(templi.get(j));
			}
		}

		if(li.size()==0)
		{
			JOptionPane.showMessageDialog(null,"테이블 정보가 없습니다. 테이블을 생성하십시요");
		}
		logger.debug("end");
	}
	/**
	 * @param data
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public DefaultTableModel createXLSTableModel(String data) throws JDOMException, IOException
	{
		logger.debug("");
		DefaultTableModel defaultTableModel = new DefaultTableModel();
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(new ByteArrayInputStream	(data.getBytes()));
		Element root = document.getRootElement();
		Element port_array = root.getChild("port_array");

		List port_list = port_array.getChildren("port1");

		defaultTableModel.addColumn("VESSEL");
		defaultTableModel.addColumn("VOYAGE");

		for(int i=0;i<port_list.size();i++)
		{
			Element sub_port = (Element) port_list.get(i);
			String port_name=sub_port.getAttributeValue("field");
			defaultTableModel.addColumn(port_name);
		}


		List vessel_list=root.getChildren("vessel");

		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);
			String vessel_name = vessel_info.getAttributeValue("name");
			String voyage  = vessel_info.getAttributeValue("voyage");
			Vector<String> rowData = new Vector<String>();
			rowData.add(vessel_name);
			rowData.add(voyage);

			List li=vessel_info.getChildren("input_date");

			for(int j=0;j<li.size();j++)
			{
				Element input_date=(Element) li.get(j);							
				rowData.add(input_date.getAttributeValue("date"));
			}

			defaultTableModel.addRow(rowData);
		}

		return defaultTableModel;
	}
	/**
	 * @param defaultTableModel
	 * @param data
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 * @throws SQLException
	 * @throws NullPointerException
	 */
	public DefaultTableModel createDBTableModel(DefaultTableModel defaultTableModel,ADVData data) throws JDOMException, IOException, SQLException,NullPointerException
	{
		logger.debug("start");
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(new ByteArrayInputStream(data.getData().getBytes()));
		Element root = document.getRootElement();
		List vessel_list=root.getChildren("vessel");

		String table_id=data.getTable_id();
		ShippersTable shippersTable=tableService.getTableById(table_id);
		if(defaultTableModel==null)
			return null;
		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);
			String vessel_name = vessel_info.getAttributeValue("name");
			String voyage  = vessel_info.getAttributeValue("voyage");
			Vector rowData = new Vector();
			rowData.add(vessel_name);
			rowData.add(voyage);
			if(shippersTable.getGubun()!=null&&shippersTable.getGubun().equals("TS"))
			{
				rowData.add( vessel_info.getAttributeValue("ts_name"));
				rowData.add( vessel_info.getAttributeValue("ts_voyage"));
			}


			List li=vessel_info.getChildren("input_date");

			for(int j=0;j<li.size();j++)
			{
				Element input_date=(Element) li.get(j);
				try{

					rowData.add(input_date.getAttributeValue("date").trim());
				}catch(Exception e)
				{
					throw new RuntimeException();
				}
			}

			defaultTableModel.addRow(rowData);
		}
		logger.debug("end");


		return defaultTableModel;
	}
	/**
	 * @param defaultTableModel
	 * @param data
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 * @throws SQLException
	 * @throws NullPointerException
	 */
	public DefaultTableModel createDBVesselNameModel(DefaultTableModel defaultTableModel,ADVData data) throws JDOMException, IOException, SQLException,NullPointerException
	{
		logger.debug("start");
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(new ByteArrayInputStream(data.getData().getBytes()));
		Element root = document.getRootElement();
		List vessel_list=root.getChildren("vessel");

		String table_id=data.getTable_id();
		ShippersTable shippersTable=tableService.getTableById(table_id);
		if(defaultTableModel==null)
			return null;
		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);
			String vessel_name = vessel_info.getAttributeValue("name");
			String f_vessel_name  = vessel_info.getAttributeValue("full-name");
			Vector rowData = new Vector();
			rowData.add( f_vessel_name);
			rowData.add(vessel_name);



			defaultTableModel.addRow(rowData);
		}
		logger.debug("end");


		return defaultTableModel;
	}

	public class VesselRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private boolean is=true;
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column)
		{
			return (column ==0);
		}
		public void setHorizontalAlignment(int i)
		{
			super.setHorizontalAlignment(i);
		}
		public void setVisible(boolean is)
		{
			super.setVisible(is);
			this.is=is;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component renderer=super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			((JLabel) renderer).setOpaque(true);
			Color foreground, background;

			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(Color.BLUE);
					setBackground(header.getBackground());
					setFont(header.getFont());

				}
			}



			if(isSelected)
			{
				foreground = Color.black;
				background=Color.YELLOW;
			}
			else
			{
				if(row %2==0)
				{
					foreground = Color.YELLOW;
					background=Color.white;	
				}
				else
				{
					background= new Color(225,235,255);		
					foreground = Color.black;
				}
			}
			renderer.setBackground(background);




			return renderer;
		}


	}

}
