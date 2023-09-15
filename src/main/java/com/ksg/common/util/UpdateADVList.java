package com.ksg.common.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ksg.adv.logic.xml.KSGXMLManager;
import com.ksg.common.dao.DAOManager;
import com.ksg.domain.ADVData;
import com.ksg.domain.ADVDataParser;
import com.ksg.domain.ShippersTable;
import com.ksg.service.ADVService;
import com.ksg.service.TableService;
import com.ksg.service.impl.TableServiceImpl;

public class UpdateADVList {
	
	private ADVService	 		_advservice;
	private TableService tableService;
	KSGXMLManager manager = new KSGXMLManager();
	public UpdateADVList() {
		_advservice = DAOManager.getInstance().createADVService();
		tableService = new TableServiceImpl();
	}
//	public void updateStart() throws NullPointerException, JDOMException, IOException
//	{
//		
//		Vector v = new Vector();
//		System.out.println("update start");
//		
//		ShippersTable table = new ShippersTable();
//		try {
//			List li = tableService.getTableList(table);
//			for(int i=0;i<li.size();i++)
//			{
//				ShippersTable shippersTable = (ShippersTable) li.get(i);
//				ADVData data =_advservice.getADVData(shippersTable.getTable_id());
//				
//				ADVDataParser parser = new ADVDataParser(data);
//				
//				try {
//					manager.createDBTableModel(null,data);
//				} catch (JDOMException e) {
//					System.err.println("error:"+data.getTable_id());
//					System.err.println("update start");
//					
//					String new_data[][]=parser.getDataArray3();
//					
//					//if(new_data.length>0)
//					{
//						Element rootElement = new Element("input");
//						rootElement.setAttribute("type","xls");
//						Element tableInfos = new Element("table");
//						tableInfos.setAttribute("id",data.getTable_id());
//						rootElement.addContent(tableInfos);
//						for(int j=0;j<new_data.length;j++)
//						{
//							
//							Element v_row = new Element("vessel");
//							
//							v_row .setAttribute("name",new_data[j][0]);
//							v_row .setAttribute("voyage",new_data[j][1]);
//							for(int c=2;c<new_data[j].length;c++)
//							{								
//								Element in_date =  new Element("input_date");
//								in_date.setAttribute("index", String.valueOf(c));
//								in_date.setAttribute("date", new_data[j][c]);								
//								v_row.addContent(in_date);							
//								
//							}
//							rootElement.addContent(v_row);
//							
//						}
//						Document document = new Document(rootElement);
//						Format format = Format.getPrettyFormat();
//
//						format.setEncoding("EUC-KR");
//						format.setIndent("\n");
//						format.setIndent("\t");
//
//						XMLOutputter outputter = new XMLOutputter(format);
//
//
//						String strXml=outputter.outputString(document);
//						System.err.println(strXml);
//						
//						
//						data.setData(strXml);
//						
//						_advservice.updateDataADVData(data);
//						//v.add(data);
//					}
//					
//				
//					
//					
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				catch (NullPointerException e) {
//					
//					/*Element rootElement = new Element("input");
//					rootElement.setAttribute("type","xls");
//					Element tableInfos = new Element("table");
//					tableInfos.setAttribute("id",data.getTable_id());
//					rootElement.addContent(tableInfos);*/
//					e.printStackTrace();
//					continue;
//				}				
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		/*for(int i=0;i<v.size();i++)
//		{
//			ADVData d=(ADVData) v.get(i);
//			try {
//				_advservice.updateDataADVData(d);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}*/
//		
//		
//		
//	}
}
