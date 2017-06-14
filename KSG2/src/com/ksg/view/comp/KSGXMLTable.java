package com.ksg.view.comp;

import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ksg.model.KSGModelManager;
import com.ksg.view.KSGViewParameter;
import com.ksg.xls.XLSTableInfo;

public class KSGXMLTable extends KSGTable
{
	private XLSTableInfo tableInfo;
	
	
	private Element rootE;

	public void setRootElement(Element rootElement) {
		this.rootE = rootElement;
		if(rootElement==null)
			throw new RuntimeException();
	}


	public KSGXMLTable() {
		super();
		this.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		this.setCellSelectionEnabled(false);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	public void update(KSGModelManager manager) 
	{
		logger.debug("table update start");
		updateModel();

		logger.debug("end");
	}

	private void updateModel() {
		try {


			Element port_array = rootE.getChild("port_array");

			List port_list = port_array.getChildren("port1");
			DefaultTableModel defaultTableModel = new DefaultTableModel(){
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			defaultTableModel.addColumn("VESSEL");
			defaultTableModel.addColumn("VOYAGE");

			for(int i=0;i<port_list.size();i++)
			{
				Element sub_port = (Element) port_list.get(i);
				String port_name=sub_port.getAttributeValue("field");
				defaultTableModel.addColumn(port_name);
			}


			List vessel_list=rootE.getChildren("vessel");

			for(int i=0;i<vessel_list.size();i++)
			{
				Element vessel_info = (Element) vessel_list.get(i);
				String vessel_name = vessel_info.getAttributeValue("name");
				String voyage  = vessel_info.getAttributeValue("voyage");
				Vector rowData = new Vector();
				rowData.add(vessel_name);
				rowData.add(voyage);

				List li=vessel_info.getChildren("input_date");

				for(int j=0;j<li.size();j++)
				{
					Element input_date=(Element) li.get(j);							
					rowData.add(input_date);
				}
				defaultTableModel.addRow(rowData);
			}

			KSGXMLTable.this.setModel(defaultTableModel);


			TableColumnModel colmodel =this.getColumnModel();

			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);
				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				if(i!=0)
				{
					namecol.setHeaderRenderer(renderer);
					namecol.setCellRenderer(new KSGTableCellRenderer());
					namecol.setMinWidth(100);
				}else
				{
					namecol.setHeaderRenderer(renderer);
					namecol.setMinWidth(175);
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e.getMessage());
		}
	}
	public void updateVesselName(int row, String vesselName)
	{
		List li=rootE.getChildren("vessel");
		
		if(li.size()<row)
			throw new RuntimeException();
		
		Element vessel=(Element) li.get(row);
		vessel.setAttribute("name",vesselName.toUpperCase());
		logger.info("선박명 수정:"+vesselName);
		updateModel();
		
	}
	public void updateVesseFulllName(int row, String vesselName)
	{
		List li=rootE.getChildren("vessel");
		
		if(li.size()<row)
			throw new RuntimeException();
		
		Element vessel=(Element) li.get(row);
		vessel.setAttribute("full-name",vesselName);
		
	}

	

	public String getXMLModel()
	{
		
		
		Element tempRoot = (Element) rootE.clone();
	
		Document document = new Document(tempRoot);
		Format format = Format.getPrettyFormat();

		format.setEncoding("EUC-KR");
		format.setIndent("\n");
		format.setIndent("\t");

		XMLOutputter outputter = new XMLOutputter(format);


		String strXml=outputter.outputString(document);

		
		
		return strXml;
	}






}
