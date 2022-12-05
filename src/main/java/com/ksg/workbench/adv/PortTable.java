package com.ksg.workbench.adv;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Element;

import com.ksg.commands.DelPortExceptionCommand;
import com.ksg.commands.IFCommand;
import com.ksg.commands.SearchPortCommand;
import com.ksg.commands.base.ADDPortExceptionCommand;
import com.ksg.commands.base.SearchPortExceptionCommand;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.TablePort;
import com.ksg.service.BaseService;
import com.ksg.service.PortService;
import com.ksg.service.TableService;
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.workbench.adv.dialog.AddPortDialog;



/**
 * 

  * @FileName : PortTable.java

  * @Project : KSG2

  * @Date : 2022. 12. 5. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 항구 정보 관리 테이블
 */
public class PortTable extends JTable implements ActionListener
{

	/**
	 * @author Administrator
	 * @see 항구 테이블 색 표시 부분
	 * 
	 *
	 */

	class ColoredTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {
			
			
			if (value instanceof PortTableInfo) {
				PortTableInfo cvalue = (PortTableInfo) value;
				switch (cvalue.getType()) {
				case PortTableInfo.TYPE_NOMAL:
					setForeground(Color.black);
					setBackground(Color.white);
					break;
				case PortTableInfo.TYPE_NEW_PORT:
					setForeground(Color.yellow);
					setBackground(Color.lightGray);
					break;
				case PortTableInfo.TYPE_RED:
					setForeground(Color.red);
					break;
				case PortTableInfo.TYPE_BLUE:
					setForeground(Color.white);
					setBackground(Color.blue);
					break;

				default:
					break;
				}

				setText(cvalue.toString());
			} else
			{	
				super.setValue(value);
			}
		}
	}
	
	/**
	 * @author 박창현
	 *
	 */
	class ColoredTableCellRenderer2 extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {			
			
			if (value instanceof ColorInfo) {
				ColorInfo cvalue = (ColorInfo) value;
				switch (cvalue.getType()) {
				case PortTableInfo.TYPE_NOMAL:
					setForeground(Color.black);
					setBackground(Color.white);
					break;
				case PortTableInfo.TYPE_NEW_PORT:
					setForeground(Color.yellow);
					setBackground(Color.lightGray);
					break;
				case PortTableInfo.TYPE_RED:
					setBackground(Color.white);
					setForeground(Color.red);
					break;
				case PortTableInfo.TYPE_BLUE:
					setForeground(Color.white);
					setBackground(Color.blue);
					break;

				default:
					break;
				}

				setText(cvalue.toString());
			} else
			{	
				super.setValue(value);
			}
		}
	}
	
	/**
	 * @author 박창현
	 *
	 */
	class MyMouseAdepter extends MouseAdapter
	{
		public void mousePressed(MouseEvent e) {
			JTable table= (JTable) e.getSource();

			int row=table.getSelectedRow();
			if(row==-1)
				return;

			Object obj = table.getValueAt(row, 1);
			if(obj instanceof PortTableInfo)
			{
				selectedPort = ((PortTableInfo) obj).getPort_name();
			}else
			{
				selectedPort= obj.toString();
			}

			selectedPort =  table.getValueAt(row, 1).toString();
			
			menuPortSearch.setText(selectedPort+" 항구검색");

			IFCommand  com = new SearchPortExceptionCommand(selectedPort);
			if(com.execute()==IFCommand.RESULT_SUCCESS)
			{
				menuPortExceptionDel.setEnabled(true);
			}else
			{
				menuPortExceptionDel.setEnabled(false);	
			}
		}
	}

	private static final long serialVersionUID = 1L;
	
	private BaseService baseService; // base service
	
	private CodeServiceImpl codeServiceImpl;
	
	private PortService portService;
	
	private TableService tableService; // table service
	
	private AddPortDialog addPortDialog;

	private KSGXLSImportPanel base;
	
	private String colums[]={"Index","Name","Area Code"};
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	private JMenuItem menuPortAdd, menuPortExceptionAdd, menuPortExceptionDel, menuPortSearch;
	
	//private DefaultTableModel portTableModel;
	
	private PortTableModel tableModel;

	private String selectedPort;

	public PortTable(JTextArea txaADV,KSGXLSImportPanel base) {
		DAOManager manager 	= DAOManager.getInstance();
		tableService		= new TableServiceImpl();
		baseService  		= manager.createBaseService();
		codeServiceImpl 	= new CodeServiceImpl();
		tableModel = new PortTableModel();
		
		portService = new PortServiceImpl();
		
		this.setRowHeight(25);
		this.base =base; 
		this.setComponentPopupMenu(createPopup());
		this.addMouseListener(new MyMouseAdepter());
	}
	

	public void actionPerformed(ActionEvent comm) {

		int row =this.getSelectedRow();
		if(row<0)
			return;
		
		selectedPort = this.getValueAt(row, 1).toString();
		
		String command = comm.getActionCommand();
		
		if(command.equals("항구 검색"))
		{
			SearchPortCommand  portCommand = new SearchPortCommand(selectedPort);
			portCommand.execute();

			if(portCommand.info==null)
				return;

			PortInfo ii =portCommand.info;		
			

			PortTableInfo info = new PortTableInfo(ii.getPort_name());
			
			info.setPort_name(ii.getPort_name());
			
			info.setType(PortTableInfo.TYPE_NOMAL);
			
			
			tableModel.setValueAt(info, row, 1);

			PortTable.this.setValueAt(info, row, 1);

//			Code op = new Code();
//
//			op.setCode_type("port_check");
//			
//			op.setCode_field(ii.getPort_name());
			
			PortTableInfo info_code = new PortTableInfo(ii.getArea_code());
			
			info_code.setArea_code(ii.getArea_code());

			try {
				
				HashMap<String, Object> param = new HashMap<String, Object>();
				
				param.put("code_type", "port_check");
				param.put("code_field", ii.getPort_name());
				
				
				info_code.setType(codeServiceImpl.selectCodeD(param)==null?PortTableInfo.TYPE_NOMAL:PortTableInfo.TYPE_BLUE);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tableModel.setValueAt(info_code, row, 2);
			
			
			PortTable.this.setModel(tableModel);
			
			PortTable.this.updateUI();

		}
		else if(command.equals("항구 추가"))
		{
			Vector<Object>  portList = new Vector<Object>();
			for(int i=0;i<PortTable.this.getRowCount();i++)
			{
				Object obj = PortTable.this.getValueAt(i, 1);

				if(obj instanceof PortTableInfo)
				{
					PortTableInfo info1=(PortTableInfo)obj;
					portList.add(info1.getPort_name());
				}else
				{
					portList.add(obj);
				}
			}

			addPortDialog = new AddPortDialog(base.getTable_id(),selectedPort , portList);
			addPortDialog.createAndUpdateUI();
		}
		else if(command.equals("예외 항구명 추가"))
		{
			ADDPortExceptionCommand com = new ADDPortExceptionCommand(selectedPort );
			if(com.execute()==IFCommand.RESULT_SUCCESS)
			{
				KSGModelManager.getInstance().execute(base.getName());
			}
		}
		else if(command.equals("예외 항구명 제외"))
		{
			DelPortExceptionCommand com = new DelPortExceptionCommand(selectedPort );
			if(com.execute()==IFCommand.RESULT_SUCCESS)
			{
				KSGModelManager.getInstance().execute(base.getName());
			}
		}
	}

	/**
	 * @see 
	 * @param index
	 * @param port_name
	 * @return PortTableInfo
	 * @throws SQLException
	 */
	private PortTableInfo createTablePortItem(int index, String port_name)throws SQLException 
	{

		String table_port_name=null;
		String table_area_code=null;

		port_name=port_name.replace("\n", " ");
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("port_name", port_name);

		HashMap<String, Object> info=(HashMap<String, Object>) portService.selectPort(param);

		if(info!=null)
		{
			table_port_name = (String) info.get("port_name");
			table_area_code = (String) info.get("area_code");
		}
		else
		{

			PortInfo infoabbr=baseService.getPortInfoAbbrByPortNamePatten(port_name);
			if(infoabbr!=null)
			{
				table_port_name = infoabbr.getPort_name();
				table_area_code = infoabbr.getArea_code();
			}

			else 
			{

				try
				{
					//수정 필요
					table_port_name = port_name;
					table_area_code =baseService.hasCodeByField(port_name)?"예외":"-";
				}
				catch(SQLException e)
				{
					System.err.println(e.getErrorCode()+", index:"+index+", error port:"+port_name);
					e.printStackTrace();
				}
			}
		}

		PortTableInfo item = new PortTableInfo(table_port_name);
		
		item.setArea_code(table_area_code);
		item.setPort_name(table_port_name);

		item.setIndex(index);

		return item;
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
	
	/**항구 목록 조회
	 * @return portList
	 */
	public Vector getPortList() {
		Vector<PortTableInfo> port = new Vector<PortTableInfo>();
		for(int i=0;i<tableModel.getRowCount();i++)
		{
			
			
			/*Object selectedIndex = tableModel.getValueAt(i, 0);
			Object selectedPort = tableModel.getValueAt(i, 1);
			Object selectedCode = tableModel.getValueAt(i, 2);


			PortTableInfo errorInfo = new PortTableInfo();

			if(selectedPort instanceof PortTableInfo)
			{

				errorInfo.setPort_name( ((PortTableInfo)selectedPort).getPort_name());
			}else{

				errorInfo.setPort_name( selectedPort.toString());
			}

			if(selectedIndex instanceof PortTableInfo)
			{
				errorInfo.setIndex(((PortTableInfo)selectedIndex).getIndex());
			}else
			{
				errorInfo.setIndex(Integer.valueOf(selectedIndex.toString()));
			}

			if(selectedCode instanceof PortTableInfo)
			{
				errorInfo.setArea_code(((PortTableInfo)selectedCode).getArea_code());
			}else
			{
				errorInfo.setArea_code(String.valueOf(tableModel.getValueAt(i, 2)));
			}*/

			port.add((PortTableInfo) tableModel.getValue(i));
		}
		return port;
	}

	/**
	 * @param port_array
	 * @return PortTableInfo list
	 * @throws SQLException
	 */
	private Vector<PortTableInfo> makeNewList(Element port_array) throws SQLException
	{
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

					itemList.add(createTablePortItem(index, sub_port_name));
				}

			}else
			{
				itemList.add(createTablePortItem(index, port_name));
			}
		}
		return itemList;
	}

	/**
	 * @param port_array
	 */
	public void setModel(Element port_array)
	{

		try {
			// search new port list
			Vector<PortTableInfo> data= makeNewList(port_array);
			
			// search base port list
			List basePortList= tableService.getParentPortList(base.getTable_id());			
			
			tableModel.setData(data);
						
			// update table info
			tableModel.validateModel(basePortList);
			
			// 테이블 정보 생성
			this.setModel(tableModel);

			updateTableColumnView();
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void updateTableColumnView() {
		TableColumnModel colmodel = this.getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);
			if(i==0)
				namecol.setMaxWidth(25);
			if(i==2)
				namecol.setMaxWidth(35);

			DefaultTableCellRenderer renderer = new ColoredTableCellRenderer2();
			namecol.setCellRenderer(renderer);

		}
	}


	/**
	 * 
	 * 테이블 모델 업데이트
	 * =================
	 * 수정일시 : 200608
	 * 
	 * @param itemList
	 */
	private DefaultTableModel createAndvalidateTableModel(Vector<PortTableInfo> itemList) {

		
		DefaultTableModel portTableModel = new	DefaultTableModel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int nRow, int nCol) {
				return false;
			}
		};
		
		
		try {


			//TODO 항구목록 테이블 모델 수정20200608

			//기존 항구 정보
			List portLi= tableService.getParentPortList(base.getTable_id());
			

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
					PortTableInfo item = itemList.get(row);

					PortTableInfo item_index = new PortTableInfo(item.getIndex());
					item_index.setType(PortTableInfo.TYPE_NOMAL);
					item_index.setIndex(item.getIndex());

					PortTableInfo item_port = new PortTableInfo(item.getPort_name());
					item_port.setType(PortTableInfo.TYPE_NOMAL);
					item_port.setPort_name(item.getPort_name());

					PortTableInfo item_code = new PortTableInfo(item.getArea_code());
					item_code.setArea_code(item.getArea_code());


					Code op = new Code();

					op.setCode_type("port_check");
					op.setCode_field(item.getPort_name());

					item_code.setType(baseService.getCodeInfo(op)==null?PortTableInfo.TYPE_NOMAL:PortTableInfo.TYPE_BLUE);
					item_code.setArea_code(item.getArea_code());


					portTableModel.setValueAt(item_index, row, 0);
					portTableModel.setValueAt(item_port, row, 1);
					portTableModel.setValueAt(item_code, row, 2);
				}
				else
				{
					PortTableInfo item_index = new PortTableInfo(row+1);
					item_index.setIndex(row+1);
					item_index.setType(PortTableInfo.TYPE_RED);

					PortTableInfo item_port = new PortTableInfo("");
					item_port.setPort_name("");
					item_port.setType(PortTableInfo.TYPE_RED);


					PortTableInfo item_code = new PortTableInfo("");
					item_port.setType(PortTableInfo.TYPE_NOMAL);
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
				PortTableInfo newPort_index=(PortTableInfo) portTableModel.getValueAt(row, 0);
				PortTableInfo newPort=(PortTableInfo) portTableModel.getValueAt(row, 1);


				boolean isNewPort=true;
				PortTableInfo checkNewPort = createTablePortItem(newPort_index.getIndex(), newPort.getPort_name());
				//신규 항구
				for(int i=0;i<portLi.size();i++)
				{
					TablePort orderPort = (TablePort) portLi.get(i);

					if(checkNewPort.getPort_name().equals(orderPort.getPort_name())&&checkNewPort.getIndex()==newPort_index.getIndex())
					{
						isNewPort = false;
					}
				}

				if(isNewPort)
				{
					PortTableInfo item_index = new PortTableInfo(newPort_index.getIndex());
					item_index.setIndex(newPort_index.getIndex());
					item_index.setType(PortTableInfo.TYPE_RED);

					PortTableInfo item_port = new PortTableInfo(newPort.getPort_name());					
					item_port.setPort_name(newPort.getPort_name());
					item_port.setType(PortTableInfo.TYPE_RED);

					portTableModel.setValueAt(item_index, row, 0);
					portTableModel.setValueAt(item_port, row, 1);
				}
			}
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return portTableModel;

	}

	/** 1. 항구가 있는지 검색
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
	  */

	/**
	 * 
	 * 항구 정보 표시
	 * @author 박창현
	 *
	 */
	public class PortTableInfo extends PortInfo{
		ColorInfo info;
		public static final  int TYPE_NOMAL=1;
		public static final  int TYPE_NEW_PORT=2;
		public static final int TYPE_RED=3;
		public static final int TYPE_BLUE=4; 
		private int index;
		private int type;
		private int checkType;
		private Object value="";
		private List codeList;
		public PortTableInfo(Object value) {
			this.value = value;
			info = new ColorInfo(this);
			this.type = TYPE_NOMAL;
			
		}

		public PortTableInfo() {}
		
		public ColorInfo getColoredIndex()
		{
			info.setValue(this.getIndex());
			return info;
		}
		
		public ColorInfo getColoredPortName()
		{
			info.setValue(this.getPort_name());
			return info;
		}
		public ColorInfo getColoredAreaCode()
		{
			info.setValue(this.getArea_code());
			return info;
		}

		public int getIndex() {
			return index;
		}

		public int getType() {
			return type;
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
		public void setCheckType(int checkType)
		{
			this.checkType = checkType;
		}
	}
	
	/**항구정보 색 표시
	 * @author 박창현
	 *
	 */
	class ColorInfo
	{
		
		PortTableInfo info;
		
		public ColorInfo(PortTableInfo info) {
			this.info = info;
		}
		private Object value;
		public Object getValue() {
			return value;
		}
		public int getType() {
			// TODO Auto-generated method stub
			return info.getType();
		}
		public void setValue(Object value)
		{
			this.value = value;
		}

		public String toString()
		{
			return String.valueOf(value);
		}
		
	}

	class ColumnData
	{
		public String  m_title;
		public int     m_width;
		public int     m_alignment;

		public ColumnData(String title, int width, int alignment) {
			m_title = title;
			m_width = width;
			m_alignment = alignment;
		}
	}

	/**
	 * @author 박창현
	 *
	 */
	class PortTableModel extends AbstractTableModel
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		final public ColumnData m_columns[] = {
				new ColumnData("Index", 60, JLabel.LEFT),
				new ColumnData("Port", 60, JLabel.RIGHT),
				new ColumnData("Code", 80, JLabel.RIGHT)
		};

		protected Vector<PortTableInfo> data;

		public void setData(Vector<PortTableInfo> data) {
			this.data = data;
		}
		
		public Vector<PortTableInfo> getData()
		{
			return data;
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return data == null ? 0 : data.size();
		}

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return m_columns.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < 0 || rowIndex >= getRowCount())
				return "";
			PortTableInfo row = data.elementAt(rowIndex);
			switch (columnIndex) {
			case 0:// 항구 인덱스
				return row.getColoredIndex();
			case 1:// 항구명
				return row.getColoredPortName();
			case 2:// 지역 코드
				return row.getColoredAreaCode();	


			}
			return "";
		}
		
		public Object getValue(int rowIndex)
		{
			if (rowIndex < 0 || rowIndex >= getRowCount())
				return "";
			return data.elementAt(rowIndex);
		}
		
		/**
		 * 항구 상태 표시
		 * 기존 : 검은색
		 * 신규 : 노란색
		 * 위치 변경 : 빨간색
		 * 
		 * @param basePortList
		 */
		public void validateModel(List<TablePort> basePortList)
		{
			for(int i=0;i<data.size();i++)
			{
				PortTableInfo info = data.get(i);								
				
				boolean isNew=true;
				
				for(int j=0;j<basePortList.size();j++)
				{
					TablePort port = basePortList.get(j);					
					
					if(info.getPort_name().equals(port.getPort_name()))
					{
						isNew=false;
						if(info.getIndex() !=port.getPort_index())							
						{
							info.setType(PortTableInfo.TYPE_RED);
						}
					}
				}
				
				
				// 신규
				if(isNew)					
				{
					info.setType(PortTableInfo.TYPE_NEW_PORT);
				}
				

				
				HashMap<String, Object> param = new HashMap<String, Object>();
				
				param.put("code_type", "port_check");
				param.put("code_field", info.getPort_name());
				
				
				PortTableInfo info_code = new PortTableInfo(info.getArea_code());
				info_code.setArea_code(info.getArea_code());

				try {
					info_code.setCheckType(codeServiceImpl.selectCodeD(param)==null?PortTableInfo.TYPE_NOMAL:PortTableInfo.TYPE_BLUE);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
