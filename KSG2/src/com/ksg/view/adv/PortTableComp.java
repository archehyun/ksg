package com.ksg.view.adv;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.ksg.commands.DelPortExceptionCommand;
import com.ksg.commands.KSGCommand;
import com.ksg.commands.SearchPortCommand;
import com.ksg.commands.base.ADDPortExceptionCommand;
import com.ksg.commands.base.SearchPortExceptionCommand;
import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Table_Port;
import com.ksg.domain.Table_Property;
import com.ksg.model.KSGModelManager;
import com.ksg.view.adv.dialog.AddPortDialog;

/**
 * @author archehyun
 *
 */
public class PortTableComp extends JTable implements ActionListener
{

	/**
	 * @author Administrator
	 * @see 테이블 색 표시 부분
	 * 
	 *
	 */

	class ColoredTableCellRenderer extends DefaultTableCellRenderer {
		public void setValue(Object value) {
			if (value instanceof PortColorInfo) {
				PortColorInfo cvalue = (PortColorInfo) value;

				switch (cvalue.getType()) {
				case PortColorInfo.TYPE_NOMAL:
					setForeground(Color.black);
					setBackground(Color.white);
					break;
				case PortColorInfo.TYPE_2:
					setForeground(Color.yellow);
					setBackground(Color.lightGray);
					break;
				case PortColorInfo.TYPE_RED:
					setForeground(Color.red);
					break;
				case PortColorInfo.TYPE_BLUE:
					setForeground(Color.white);
					setBackground(Color.blue);
					break;

				default:
					break;
				}

				setText(cvalue.toString());
			} else
				super.setValue(value);
		}
	}
	class MyMouseAdepter extends MouseAdapter
	{
		public void mousePressed(MouseEvent e) {
			JTable table= (JTable) e.getSource();

			int row=table.getSelectedRow();
			if(row==-1)
				return;

			Object obj = table.getValueAt(row, 1);
			if(obj instanceof PortColorInfo)
			{
				selectedPort = ((PortColorInfo) obj).getPort_name();
			}else
			{
				selectedPort= obj.toString();
			}

			selectedPort =  table.getValueAt(row, 1).toString();
			menuPortSearch.setText(selectedPort+" 항구검색");

			KSGCommand  com = new SearchPortExceptionCommand(selectedPort);
			if(com.execute()==KSGCommand.RESULT_SUCCESS)
			{
				menuPortExceptionDel.setEnabled(true);
			}else
			{
				menuPortExceptionDel.setEnabled(false);	
			}
		}
	}
	class PortColorInfo 
	{
		public static final  int TYPE_NOMAL=1;
		public static final  int TYPE_2=2;
		public static final int TYPE_RED=3;
		public static final int TYPE_BLUE=4;
		private String area_code;
		private int index;
		private String port_name;
		private int type;
		private Object value="";
		public PortColorInfo(Object value) {
			this.value = value;
		}
		public PortColorInfo() {}
		public String getArea_code() {
			// TODO Auto-generated method stub
			return area_code;
		}
		public int getIndex() {
			return index;
		}
		public String getPort_name() {
			return port_name;
		}
		public int getType() {
			return type;
		}
		public void setArea_code(String area_code) {

			this.area_code=area_code;
		}
		public void setIndex(int index) {
			this.index= index;

		}
		public void setType(int type) {
			this.type = type;
		}
		public String toString()
		{
			return String.valueOf(value);
		}
		public void setPort_name(String port_name) {
			this.port_name = port_name;
		}
	}
	private static final long serialVersionUID = 1L;
	private BaseService _baseService;
	private TableService _tableService;
	private AddPortDialog addPortDialog;

	private KSGXLSImportPn base;
	private String colums[]={"Index","Name","Area Code"};
	protected Logger 		logger = Logger.getLogger(this.getClass());
	private JMenuItem menuPortAdd;
	private JMenuItem menuPortExceptionAdd;
	private JMenuItem menuPortExceptionDel;
	private JMenuItem menuPortSearch;
	private Element port_array;
	private DefaultTableModel portTableModel;
	private Table_Property property;

	private String selectedPort;

	public PortTableComp(JTextArea txaADV,KSGXLSImportPn base) {
		DAOManager manager = DAOManager.getInstance();
		_tableService= manager.createTableService();
		_baseService  = manager.createBaseService();
		this.base =base; 
		this.setComponentPopupMenu(createPortPopup());
		this.addMouseListener(new MyMouseAdepter());
	}
	public void actionPerformed(ActionEvent comm) {

		int row =this.getSelectedRow();
		if(row<0)
			return;
		PortColorInfo selectedInfo=(PortColorInfo) this.getValueAt(row, 1);
		String command = comm.getActionCommand();
		if(command.equals("항구 검색"))
		{
			SearchPortCommand  portCommand = new SearchPortCommand(selectedPort);
			portCommand.execute();

			if(portCommand.info==null)
				return;

			PortInfo ii =portCommand.info;

			PortColorInfo info = new PortColorInfo(ii.getPort_name());
			info.setPort_name(ii.getPort_name());
			info.setType(PortColorInfo.TYPE_NOMAL);

			PortTableComp.this.setValueAt(info, row, 1);

			Code op = new Code();

			op.setCode_type("port_check");
			op.setCode_field(ii.getPort_name());
			PortColorInfo info_code = new PortColorInfo(ii.getArea_code());
			info_code.setArea_code(ii.getArea_code());

			try {
				info_code.setType(_baseService.getCodeInfo(op)==null?PortColorInfo.TYPE_NOMAL:PortColorInfo.TYPE_BLUE);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			PortTableComp.this.setValueAt(info_code, row, 2);

		}
		else if(command.equals("항구 추가"))
		{
			Vector<Object>  portList = new Vector<Object>();
			for(int i=0;i<PortTableComp.this.getRowCount();i++)
			{
				Object obj = PortTableComp.this.getValueAt(i, 1);

				if(obj instanceof PortColorInfo)
				{
					PortColorInfo info1=(PortColorInfo)obj;
					portList.add(info1.getPort_name());
				}else
				{
					portList.add(obj);
				}
			}

			addPortDialog = new AddPortDialog(base.getTable_id(),selectedInfo.getPort_name(), portList);
			addPortDialog.createAndUpdateUI();
		}
		else if(command.equals("예외 항구명 추가"))
		{
			ADDPortExceptionCommand com = new ADDPortExceptionCommand(selectedInfo.getPort_name());
			if(com.execute()==KSGCommand.RESULT_SUCCESS)
			{
				KSGModelManager.getInstance().execute(base.getName());
			}
		}
		else if(command.equals("예외 항구명 제외"))
		{
			DelPortExceptionCommand com = new DelPortExceptionCommand(selectedInfo.getPort_name());
			if(com.execute()==KSGCommand.RESULT_SUCCESS)
			{
				KSGModelManager.getInstance().execute(base.getName());
			}
		}
	}
	String table_port_name;
	String table_area_code;
	public PortColorInfo createTablePortItem(int index, String port_name)throws SQLException 
	{

		port_name=port_name.replace("\n", " ");
		PortInfo info=_baseService.getPortInfoByPortName(port_name);



		if(info!=null)
		{
			table_port_name = info.getPort_name();
			table_area_code = info.getArea_code();
		}
		else
		{

			PortInfo infoabbr=_baseService.getPortInfoAbbrByPortNamePatten(port_name);
			if(infoabbr!=null)
			{
				table_port_name = infoabbr.getPort_name();
				table_area_code = infoabbr.getArea_code();
			}else
			{

				try
				{
				//수정 필요
				table_port_name = port_name;
				table_area_code =_baseService.hasCodeByField(port_name)?"예외":"-";
				}
				catch(Exception e)
			{

				System.err.println("error port:"+port_name);
				e.printStackTrace();
				}
			}
			}
			PortColorInfo item = new PortColorInfo(table_port_name);
			item.setArea_code(table_area_code);
			item.setPort_name(table_port_name);

			item.setIndex(index);
			
			
			return item;
		}

		private JPopupMenu createPortPopup() 
		{
			JPopupMenu menu = new JPopupMenu();

			menuPortSearch = new JMenuItem("항구 검색");
			menuPortSearch.setActionCommand("항구 검색");
			menuPortSearch.addActionListener(this);

			menuPortAdd = new JMenuItem("항구 추가");
			menuPortAdd.addActionListener(this);

			menuPortExceptionAdd = new JMenuItem("예외 항구명 추가");
			menuPortExceptionAdd.setActionCommand("예외 항구명 추가");
			menuPortExceptionAdd.addActionListener(this);

			menuPortExceptionDel = new JMenuItem("예외 항구명 제외");
			menuPortExceptionDel.setActionCommand("예외 항구명 제외");
			menuPortExceptionDel.addActionListener(this);


			menu.add(menuPortSearch);
			menu.add(menuPortExceptionAdd);
			menu.add(menuPortExceptionDel);

			return menu;
		}
		public Vector getPortList() {
			Vector<PortColorInfo> port = new Vector<PortColorInfo>();
			for(int i=0;i<portTableModel.getRowCount();i++)
			{
				Object selectedIndex = portTableModel.getValueAt(i, 0);
				Object selectedPort = portTableModel.getValueAt(i, 1);
				Object selectedCode = portTableModel.getValueAt(i, 2);


				PortColorInfo errorInfo = new PortColorInfo();
				if(selectedPort instanceof PortColorInfo)
				{

					errorInfo.setPort_name( ((PortColorInfo)selectedPort).getPort_name());
				}else{

					errorInfo.setPort_name( selectedPort.toString());
				}

				if(selectedIndex instanceof PortColorInfo)
				{
					errorInfo.setIndex(((PortColorInfo)selectedIndex).getIndex());
				}else
				{
					errorInfo.setIndex(Integer.valueOf(selectedIndex.toString()));
				}

				if(selectedCode instanceof PortColorInfo)
				{
					errorInfo.setArea_code(((PortColorInfo)selectedCode).getArea_code());
				}else
				{
					errorInfo.setArea_code(String.valueOf(portTableModel.getValueAt(i, 2)));
				}

				port.add(errorInfo);
			}
			return port;
		}
		public void setModel(Element port_array)
		{
			portTableModel = new DefaultTableModel(){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int nRow, int nCol) {
					return false;
				}
			};
			this.port_array = port_array;

			for(int i=0;i<colums.length;i++)
			{
				portTableModel.addColumn(colums[i]);
			}		
			// 추가 내용

			Vector<PortColorInfo> itemList = new Vector<PortColorInfo>();
			List port_list=port_array.getChildren("port1");
			try {
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

							itemList.add(createTablePortItem(index, sub_port_name));
						}

					}else
					{
						itemList.add(createTablePortItem(index, port_name));
					}
				}
				updateTableColumn(itemList);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("error");

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}



		/**
		 * 
		 * 테이블 에 색 표시
		 * @param itemList
		 */
		private void updateTableColumn(Vector<PortColorInfo> itemList) {

			try {

				//기존 항구 정보
				List portLi= _tableService.getParentPortList(base.getTable_id());
				portTableModel = new	DefaultTableModel(){
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public boolean isCellEditable(int nRow, int nCol) {
						return false;
					}
				};

				for(int i=0;i<colums.length;i++)
				{
					portTableModel.addColumn(colums[i]);				
				}

				int rowcount=portLi.size()>=itemList.size()?portLi.size():itemList.size();

				portTableModel.setRowCount(rowcount);


				/**
				 * 사이즈가 다른 경우
				 */
				for(int row=0;row<rowcount;row++)
				{
					if(row<itemList.size())
					{
						PortColorInfo item = itemList.get(row);

						PortColorInfo item_index = new PortColorInfo(item.getIndex());
						item_index.setType(PortColorInfo.TYPE_NOMAL);
						item_index.setIndex(item.getIndex());

						PortColorInfo item_port = new PortColorInfo(item.getPort_name());
						item_port.setType(PortColorInfo.TYPE_NOMAL);
						item_port.setPort_name(item.getPort_name());

						PortColorInfo item_code = new PortColorInfo(item.getArea_code());
						item_code.setArea_code(item.getArea_code());


						Code op = new Code();

						op.setCode_type("port_check");
						op.setCode_field(item.getPort_name());

						item_code.setType(_baseService.getCodeInfo(op)==null?PortColorInfo.TYPE_NOMAL:PortColorInfo.TYPE_BLUE);
						item_code.setArea_code(item.getArea_code());


						portTableModel.setValueAt(item_index, row, 0);
						portTableModel.setValueAt(item_port, row, 1);
						portTableModel.setValueAt(item_code, row, 2);
					}
					else
					{
						PortColorInfo item_index = new PortColorInfo(row+1);
						item_index.setIndex(row+1);
						item_index.setType(PortColorInfo.TYPE_RED);

						PortColorInfo item_port = new PortColorInfo("");
						item_port.setPort_name("");
						item_port.setType(PortColorInfo.TYPE_RED);


						PortColorInfo item_code = new PortColorInfo("");
						item_port.setType(PortColorInfo.TYPE_NOMAL);
						portTableModel.setValueAt(item_index, row, 0);
						portTableModel.setValueAt(item_port, row, 1);
						portTableModel.setValueAt(item_code, row, 2);
					}
				}

				/**
				 * 기존 내용과 비교
				 * 
				 * 1. 신규 항구
				 */
				for(int row=0;row<portTableModel.getRowCount();row++)
				{
					PortColorInfo newPort_index=(PortColorInfo) portTableModel.getValueAt(row, 0);
					PortColorInfo newPort=(PortColorInfo) portTableModel.getValueAt(row, 1);


					boolean isNewPort=true;
					PortColorInfo checkNewPort = createTablePortItem(newPort_index.getIndex(), newPort.getPort_name());
					//신규 항구
					for(int i=0;i<portLi.size();i++)
					{
						Table_Port orderPort = (Table_Port) portLi.get(i);

						if(checkNewPort.getPort_name().equals(orderPort.getPort_name())&&checkNewPort.getIndex()==newPort_index.getIndex())
						{
							isNewPort = false;
						}
					}

					if(isNewPort)
					{
						PortColorInfo item_index = new PortColorInfo(newPort_index.getIndex());
						item_index.setIndex(newPort_index.getIndex());
						item_index.setType(PortColorInfo.TYPE_RED);

						PortColorInfo item_port = new PortColorInfo(newPort.getPort_name());					
						item_port.setPort_name(newPort.getPort_name());
						item_port.setType(PortColorInfo.TYPE_RED);

						portTableModel.setValueAt(item_index, row, 0);
						portTableModel.setValueAt(item_port, row, 1);
					}
				}

				this.setModel(portTableModel);

				TableColumnModel colmodel = this.getColumnModel();

				for(int i=0;i<colmodel.getColumnCount();i++)
				{
					TableColumn namecol = colmodel.getColumn(i);
					if(i==0)
						namecol.setMaxWidth(25);
					if(i==2)
						namecol.setMaxWidth(35);

					DefaultTableCellRenderer renderer = new ColoredTableCellRenderer();
					namecol.setCellRenderer(renderer);

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/*private void updateTableColumn() {
		try {
			List portLi= _tableService.getParentPortList(base.getTable_id());



		 *//** 1. 항구가 있는지 검색
		 * 		1.1 있는 항구명 => 2로 이동 
		 * 		1.2 없는 항구명 => 노란색
		 * 
		 *  2. 위치가 같은 지 검색
		 *  	2.1 같은 위치 => 검정색
		 *  	2.2 다른 위치 => 파란색
		 *  
		 *  3. 저장시 정책
		 *  	3.1 해당 포트 삭제
		 *  	3.2 해당 포트 업데이트
		 *//*

		  *//** 수정
		  * 
		  * 1. 지난 주 항구수 보다 이번 주 항구수가 줄어든 경우
		  * 
		  * 
		  *//*
			for(int i=0;i<portTableModel.getRowCount();i++)
			{
				PortColorInfo port = (PortColorInfo) portTableModel.getValueAt(i, 1);

				boolean flag=false;
				for(int j=0;j<portLi.size();j++)
				{
					try{
						Table_Port p = (Table_Port) portLi.get(j);
						if(port.getPort_name().equals(p.getPort_name()))
						{
							flag=true;

							// 인덱스가 1부터 시작 됨
							if((i+1)==p.getPort_index())
							{
								port.setType(PortColorInfo.TYPE_NOMAL);
								Table_Port sp = new Table_Port();
								sp.setParent_port(port.getPort_name());
								sp.setPort_type(Table_Port.TYPE_CHAILD);
								sp.setTable_id(p.getTable_id());
								List li=_tableService.getTablePortList(sp);
								if(li.size()>0)
								{
									portTableModel.setValueAt(Table_Port.TYPE_CHAILD+":"+li.size(), i, 2);
								}

								//	logger.error("Port 위치가 동일합니다." );
							}else
							{
								port.setType(PortColorInfo.TYPE_RED);
								//logger.error("위치가 다릅니다."+port.getPort_name()+","+i+","+p.getPort_index());
							}
						}	
					}catch(Exception  e)
					{
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e.getMessage());
					}
				}
				if(!flag)
				{
					port.setType(PortColorInfo.TYPE_2);
					logger.error("항구 정보("+port.getPort_name()+")가 존재하지 않습니다.");
				}
			}


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.setModel(portTableModel);

		TableColumnModel colmodel = this.getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);
			if(i==0)
				namecol.setMaxWidth(25);
			if(i==2)
				namecol.setMaxWidth(35);

			DefaultTableCellRenderer renderer = new ColoredTableCellRenderer();
			namecol.setCellRenderer(renderer);

		}
	}*/
	}
