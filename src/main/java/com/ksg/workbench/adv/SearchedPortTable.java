package com.ksg.workbench.adv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Element;

import com.ksg.commands.DelPortExceptionCommand;
import com.ksg.commands.SearchPortCommand;
import com.ksg.commands.base.ADDPortExceptionCommand;
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.service.impl.ShipperTableServiceImpl;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.adv.PortTable.PortTableInfo;

/**

  * @FileName : SearchedPortTable.java

  * @Date : 2021. 3. 18. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 광고정보(엑셀)에서 가져올시 항구 정보 관리

  */
@SuppressWarnings("serial")
public class SearchedPortTable extends JPanel implements ComponentListener, ActionListener{

	public String ACTION_PORT_INSERT="항구 추가";
	public String ACTION_PORT_SELECT="항구 검색";
	public String ACTION_EXCEPTION_PORT_INSERT="예외 항구 추가";
	public String ACTION_EXCEPTION_PORT_DELETE="예외 항구 제외";
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public static final  int TYPE_NOMAL=1;
	public static final  int TYPE_NEW_PORT=2;
	public static final int TYPE_NOT_MATCH_INDEX=3;
	public static final int TYPE_BLUE=4; 

	PortServiceImpl portService;

	ShipperTableServiceImpl shipperTableService;

	CodeServiceImpl codeService;

	KSGAbstractTable tableH;

	private HashMap<String, Object> result;

	private List<HashMap<String, Object>> dbList;

	private JMenuItem menuPortSearch;

	private JMenuItem menuPortAdd;

	private JMenuItem menuPortExceptionAdd;

	private JMenuItem menuPortExceptionDel;
	private List<HashMap<String, Object>> master;

	
	public SearchedPortTable() {
		this.setLayout(new BorderLayout());

		tableH = new KSGAbstractTable();

		KSGTableColumn column = new KSGTableColumn("port_index", "순서");

		column.minSize=35;		
		column.maxSize=35;		
		column.size=35;
		
		tableH.addColumn(column);

		tableH.addColumn(new KSGTableColumn("port_name", "항구명"));

		KSGTableColumn column2 = new KSGTableColumn("area_code", "코드");
		column2.minSize=35;		
		column2.maxSize=35;		
		column2.size=35;
		tableH.addColumn(column2);

		tableH.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		tableH.setCellRenderer(new PortTableRenderer());
		
		tableH.getTableHeader().setVisible(false);

		tableH.initComp();

		portService = new PortServiceImpl();

		shipperTableService = new ShipperTableServiceImpl();

		codeService = new CodeServiceImpl();

		tableH.setComponentPopupMenu(createPopup());

		this.add(tableH);

	}

	private JPopupMenu createPopup() 
	{
		JPopupMenu menu = new JPopupMenu();

		menuPortSearch = new JMenuItem("항구 검색");
		menuPortSearch.setActionCommand("항구 검색");
		menuPortSearch.addActionListener(this);

		menuPortAdd = new JMenuItem("항구 추가");
		menuPortAdd.addActionListener(this);

		menuPortExceptionAdd = new JMenuItem("예외 항구명 추가");
		menuPortExceptionAdd.setActionCommand(ACTION_EXCEPTION_PORT_INSERT);
		menuPortExceptionAdd.addActionListener(this);

		menuPortExceptionDel = new JMenuItem("예외 항구명 제외");
		menuPortExceptionDel.setActionCommand(ACTION_EXCEPTION_PORT_DELETE);
		menuPortExceptionDel.addActionListener(this);

		menu.add(menuPortSearch);
		menu.add(menuPortExceptionAdd);
		menu.add(menuPortExceptionDel);

		return menu;
	}


	/**
	 * @see 
	 * @param port_index
	 * @param port_name
	 * @return PortTableInfo
	 * @throws SQLException
	 */
	private HashMap<String, Object> createTablePortItem(HashMap<String, Object> param)throws SQLException 
	{

		String port_name = (String) param.get("port_name");
		
		param.put("port_abbr", port_name);

		HashMap<String, Object> result=portService.selectPort(param);

		if(result==null)
		{	

			try
			{
				// 항구정보가 없을 경우
				
				// 코드에 예외 항구에 있으면 예외 표시 없으면 - 표시 
				
				HashMap<String, Object> codeParam = new HashMap<String, Object>();

				codeParam.put("code_field", port_name);

				param.put("area_code", codeService.selectCodeD(codeParam)!=null?"예외":"-");
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			param.put("port_name", result.get("port_name"));
			param.put("area_code", result.get("area_code"));
		}



		return param;
	}


	/**
	 * @see 
	 * @param port_index
	 * @param port_name
	 * @return PortTableInfo
	 * @throws SQLException
	 */
	private HashMap<String, Object> createTablePortItem(int port_index, String port_name)throws SQLException 
	{

		port_name=port_name.replace("\n", " ");

		

		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("port_name", port_name);
		param.put("port_abbr", port_name);

		HashMap<String, Object> result=portService.selectPort(param);
		
		HashMap<String, Object> newItem = new HashMap<String, Object>();
		newItem.put("port_index", port_index);
		newItem.put("port_type", "P");

		if(result==null)
		{

			try
			{
				HashMap<String, Object> codeParam = new HashMap<String, Object>();

				codeParam.put("code_field", port_name);
				newItem.put("port_name", port_name);
				newItem.put("parent_port", port_name);
				newItem.put("area_code", codeService.selectCodeD(codeParam)!=null?"예외":"-");
			}
			catch(SQLException e)
			{
				System.err.println(e.getErrorCode()+", index:"+port_index+", error port:"+port_name);
				e.printStackTrace();
			}
		}
		else
		{
			newItem.put("port_name", result.get("port_name"));
			newItem.put("parent_port", result.get("port_name"));			
			newItem.put("area_code", result.get("area_code"));

		}

		return newItem;
	}



	public List<HashMap<String, Object>> getPortList() {
		
		return master;
	}
	public void setModel(Element portElement, String tableID) {
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();

			param.put("port_type", "P");

			param.put("table_id", tableID);

			result =(HashMap<String, Object>) shipperTableService.selectPortList(param);

			dbList = (List<HashMap<String, Object>>) result.get("master");		

			master = makeNewList(portElement);

			validateModel(dbList, master);
			
			for(int i=0;i<master.size();i++)
			{
				HashMap<String, Object> map=(HashMap<String, Object>) master.get(i);

				createTablePortItem(map);

			}

			tableH.setResultData(master);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 항구 상태 표시
	 * 기존 : 검은색
	 * 신규 : 노란색
	 * 위치 변경 : 빨간색
	 * 
	 * @param basePortList
	 */
	public void validateModel(List<HashMap<String, Object>> baseList, List<HashMap<String, Object>> newList)
	{
		for(int i=0;i<newList.size();i++)
		{
			HashMap<String, Object> newInfo =newList.get(i);

			boolean isNew=true;
			
			String new_port = (String) newInfo.get("port_name");
			
			
			for(int j=0;j<baseList.size();j++)
			{
				HashMap<String, Object> baseInfo= baseList.get(j);
				
				String port_name = (String) baseInfo.get("port_name");
				
				if(new_port.equals(port_name))
				{
					isNew=false;
					
					int new_index = (Integer) newInfo.get("port_index");
					int port_index = (Integer) baseInfo.get("port_index");

					if(new_index !=port_index)							
					{
						newInfo.put("type", TYPE_NOT_MATCH_INDEX);
						logger.info(port_name+"=>not match index:"+newInfo.get("port_index")+","+baseInfo.get("port_index"));
						
					}
					else
					{
						newInfo.put("type", TYPE_NOMAL);
					}
					break;
				}
			}


			// 신규
			if(isNew)					
			{
				newInfo.put("type", TYPE_NEW_PORT);
			}		
		}
	}




	/**엑셀 정보에서 항구정보를 추출하여 새로운 모델 생성
	 * @param port_array
	 * @return PortTableInfo list
	 * @throws SQLException
	 */
	private List<HashMap<String, Object>> makeNewList(Element port_array) throws SQLException
	{

		List<HashMap<String, Object>> resultMap = new LinkedList<HashMap<String, Object>>();

		Vector<PortTableInfo> itemList = new Vector<PortTableInfo>();

		List port_list=port_array.getChildren("port1");

		for(int i=0;i< port_list.size();i++)
		{
			Element port_info = (Element) port_list.get(i);

			boolean multi =Boolean.valueOf(port_info.getAttributeValue("multi"));
			int index = Integer.parseInt(port_info.getAttributeValue("index"));
			String port_name =port_info.getAttributeValue("field");
			if(multi)
			{
				List sub_port_list = port_info.getChildren("sub_port");
				
				for(int j=0;j< sub_port_list.size();j++)
				{
					Element sub_port = (Element) sub_port_list.get(j);

					String sub_port_name = sub_port.getAttributeValue("port_name");

					resultMap.add(createTablePortItem(index, sub_port_name, port_name));
				}

			}else
			{
				resultMap.add(createTablePortItem(index, port_name));
			}
		}
		return resultMap;
	}

	private HashMap<String, Object> createTablePortItem(int index, String port_name, String parent_port) throws SQLException {
		HashMap<String, Object> item = createTablePortItem(index, port_name);
		item.put("parent_port", parent_port);
				
		return item;
	}
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void componentShown(ComponentEvent e) {


	}
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals(ACTION_PORT_SELECT))
		{
			int row=tableH.getSelectedRow();
			
			if(row<0)
				return;

			String port_name=(String) tableH.getValueAt(row, 1);
			
			SearchPortCommand  portCommand = new SearchPortCommand(port_name);
			
			portCommand.execute();
			
			if(portCommand.info!=null)
			{	
				HashMap<String, Object> item= master.get(row);
				item.put("port_name",portCommand.info.getPort_name());	
				item.put("area_code",portCommand.info.getArea_code());
			}
			tableH.setResultData(master);
			
			
			
			
			
		}
		else if(command.equals(ACTION_EXCEPTION_PORT_INSERT))
		{
			int row=tableH.getSelectedRow();
			if(row<0)
				return;

			String port_name=(String) tableH.getValueAt(row, 1);

			ADDPortExceptionCommand com = new ADDPortExceptionCommand(port_name );
			com.execute();
		}
		else if(command.equals(ACTION_EXCEPTION_PORT_DELETE))
		{
			int row=tableH.getSelectedRow();
			if(row<0)
				return;

			String port_name=(String) tableH.getValueAt(row, 1);

			DelPortExceptionCommand com = new DelPortExceptionCommand(port_name );
			com.execute();
		}
	}
	class PortTableRenderer extends DefaultTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			((JLabel) renderer).setOpaque(true);
			Color foreground, background;
			if (isSelected) {
				foreground = Color.WHITE;
				background = new Color(51, 153, 255);
			} else {
				KSGAbstractTable tableH =(KSGAbstractTable) table;
				HashMap<String, Object> item=(HashMap<String, Object>)tableH.getValueAt(row);
				int type=(Integer)item.get("type");
				
				switch (type) {
				case TYPE_NOMAL:
					setForeground(Color.black);
					setBackground(Color.white);
					break;
					
				case TYPE_NEW_PORT:
					setForeground(Color.yellow);
					setBackground(Color.lightGray);
					break;	
				case TYPE_NOT_MATCH_INDEX:
					setBackground(Color.white);
					setForeground(Color.red);
					break;		

				default:
					setForeground(Color.black);
					setBackground(Color.white);
					break;
				}
				
			}

			return renderer;
		}

	}

}

