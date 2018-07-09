package com.ksg.shippertable.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.common.view.dialog.PortSearchDialog;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.shippertable.view.ShipperTableMgtUI;

/**
 * @author 박창현
 * @explanation 항구 정보를 등록/ 삭제 수정 하는 창을 표시
 * 
 */
@SuppressWarnings("serial")
public class ManagePortDialog extends KSGDialog implements ActionListener{

	private static final int PORT_NAME_COLUM = 1;
	private static final int PORT_INDEX_COLUM = 0;
	public static int UPDATE_OPTION=1;
	public static int CANCEL_OPTION=0;
	public int OPTION;
	private String table_id;
	private PortListTable tblPortList;
	private JTable currentTable;


	private ShipperTableMgtUI base;
	private JCheckBox cbxDeleteAll;
	private JPanel pnRightOption, pnLeftOption;
	
	private JTextField txfIndex, txfPortName, txflblIndex, txflblPortName, txfUpdatePortName;
	
	public int selectedcolIndex, portIndex;
	
	private String portName;
	
	public ManagePortDialog(String table_id,ShipperTableMgtUI base) 
	{	
		this.base =base;
		this.table_id=table_id;
		DAOManager manager = DAOManager.getInstance();
		tableService = manager.createTableService();
		baseService  = manager.createBaseService();
	}
	public void createAndUpdateUI() {

		setTitle(this.table_id+"테이블 항구 관리");

		setModal(true);

		getContentPane().add(buildCenter());
		getContentPane().add(createLine(),BorderLayout.WEST);
		getContentPane().add(createLine(),BorderLayout.EAST);
		getContentPane().add(buildControl(),BorderLayout.SOUTH);
		getContentPane().add(buildInfo(),BorderLayout.NORTH);

		setSize(520,550);
		this.setMinimumSize(new Dimension(520,550));
		ViewUtil.center(this, false);
		setVisible(true);
	}

	private Component buildCenter() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());

		JPanel pnPortMain = new JPanel();
		GridLayout gridLayout = new GridLayout(1,0);
		gridLayout.setHgap(5);
		pnPortMain.setLayout(gridLayout);

		tblPortList = new PortListTable(this.table_id);
		
		tblPortList.addMouseListener(new MyMouseAdapter());

		tblPortList.addKeyListener(new MyKeyAdapter());

		currentTable =tblPortList;


		JPanel pnLeft = new JPanel();
		pnLeft.setLayout(new BorderLayout());
		JPanel pnLeftNorth = new JPanel();
		pnLeftNorth.setLayout(new FlowLayout(FlowLayout.LEFT));

		txfIndex = new JTextField(3);
		txfIndex.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent arg0) 
			{	
				int row=tblPortList.getSelectedRow();
				if(row==-1)
					return;

				tblPortList.selectedindex = row;
				txfUpdatePortName.setText(String.valueOf(tblPortList.getValueAt(row, PORT_NAME_COLUM)));
				txfIndex.setText(String.valueOf(tblPortList.getValueAt(row, PORT_INDEX_COLUM)));

			}

			public void focusLost(FocusEvent arg0) 
			{


			}});

		txfIndex.addKeyListener(new KeyAdapter()
		{

			public void keyPressed(KeyEvent arg0) 
			{

				if(arg0.getKeyCode()==KeyEvent.VK_UP||arg0.getKeyCode()==KeyEvent.VK_DOWN)
				{
					switch (arg0.getKeyCode()) {
					case KeyEvent.VK_UP:

						if(tblPortList.selectedindex>0)
							tblPortList.selectedindex-=1;
						break;
					case KeyEvent.VK_DOWN:
						if(tblPortList.getValueAt(tblPortList.selectedindex, PORT_NAME_COLUM)==null)
						{
							tblPortList.selectedindex-=1;
							return;
						}
						break;	

					default:
						break;
					}

					showSelectedTable();
				}

				else if(arg0.getKeyCode()==KeyEvent.VK_ENTER)
				{
					try 
					{
						TablePort port = new TablePort();
						port.setPort_type(TablePort.TYPE_PARENT);

						if(String.valueOf(tblPortList.getValueAt(tblPortList.selectedindex, PORT_NAME_COLUM))==null)
						{
							return;
						}
						port.setPort_name(String.valueOf(tblPortList.getValueAt(tblPortList.selectedindex, PORT_NAME_COLUM)));
						port.setPort_index(portIndex);
						port.setNew_port_index(Integer.parseInt(txfIndex.getText()));
						port.setTable_id(ManagePortDialog.this.table_id);
						tableService.updateTablePortIndex2(port);

						portName =port.getPort_name();
						portIndex = port.getNew_port_index();


					} catch (SQLException e1) 
					{

						if(e1.getErrorCode()==2627)
						{
							tblPortList.clearSelection();
							String message1 = "해당 인덱스에 동일한 항구명이 존재합니다.";
							JOptionPane.showMessageDialog(ManagePortDialog.this, message1+e1.getErrorCode());
							try {
								tblPortList.retrive();
							} catch (SQLException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
						}else
						{
							e1.printStackTrace();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();

					}
					finally
					{
						try {
							tblPortList.retrive();
							int ind=0;
							for(int i=0;i<tblPortList.getRowCount();i++)
							{
								try{
									int vindex = Integer.parseInt(String.valueOf(tblPortList.getValueAt(i, 0)));
									if(vindex==portIndex&&tblPortList.getValueAt(i, 1).equals(portName))
									{
										ind=i;
									}
								}catch(Exception e)
								{
									continue;
								}
							}
							tblPortList.changeSelection(ind, 0, false, false);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

			}

		});

		txfUpdatePortName = new JTextField(30);

		txfUpdatePortName.addKeyListener(new KeyAdapter(){

			public void keyPressed(KeyEvent arg0) 
			{
				if(arg0.getKeyCode()==KeyEvent.VK_UP||arg0.getKeyCode()==KeyEvent.VK_DOWN)
				{
					switch (arg0.getKeyCode()) {
					case KeyEvent.VK_UP:

						if(tblPortList.selectedindex>0)
							tblPortList.selectedindex-=1;
						break;
					case KeyEvent.VK_DOWN:
						if(tblPortList.getValueAt(tblPortList.selectedindex, PORT_NAME_COLUM)==null)
						{
							tblPortList.selectedindex-=1;
							return;
						}
						break;	

					default:
						break;
					}

					showSelectedTable();

					tblPortList.changeSelection(tblPortList.selectedindex, PORT_INDEX_COLUM, false, false);
				}
				else if(arg0.getKeyCode()==KeyEvent.VK_ENTER)
				{
					JTextField txf = (JTextField) arg0.getSource();
					String val = txf.getText();
					if(val.length()>0)
					{
						List<String> li=null;
						try {
							li=	baseService.getPortListByPatten(String.valueOf(val));
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(ManagePortDialog.this, e1.getMessage());
						}
						logger.debug("searched "+li.size());
						if(li.size()==1)
						{
							txf.setText(li.get(0));
						}
						else if(li.size()>1)
						{
							logger.debug("create portdialog");

							SearchPortDialog searchPortDialog = new SearchPortDialog(ManagePortDialog.this,li);
							searchPortDialog.createAndUpdateUI();


							if(searchPortDialog.result!=null)
							{
								txf.setText(searchPortDialog.result);		

							}else
							{
								txf.setText("");
								return;
							}
						}else 
						{
							JOptionPane.showMessageDialog(null, "해당 항구정보이 없습니다.");
							txf.setText("");
							return;
						}
						try 
						{
							TablePort port = new TablePort();
							port.setPort_type(TablePort.TYPE_PARENT);
							port.setPort_name(portName);
							port.setPort_index(portIndex);
							port.setNew_port_name(txf.getText());
							port.setTable_id(ManagePortDialog.this.table_id);
							tableService.updateTablePortName(port);

							portName =port.getPort_name();
							portIndex = port.getNew_port_index();


						} catch (SQLException e1) 
						{

							if(e1.getErrorCode()==2627)
							{
								tblPortList.clearSelection();
								JOptionPane.showMessageDialog(ManagePortDialog.this, "해당 인덱스에 동일한 항구명이 존재합니다."+e1.getErrorCode());
								try {
									tblPortList.retrive();
								} catch (SQLException e2) {
									// TODO Auto-generated catch block
									e2.printStackTrace();
								}
							}else
							{
								e1.printStackTrace();
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();

						}
						finally
						{
							try {
								tblPortList.retrive();
								int ind=0;
								for(int i=0;i<tblPortList.getRowCount();i++)
								{
									try{
										int vindex = Integer.parseInt(String.valueOf(tblPortList.getValueAt(i, 0)));
										if(vindex==portIndex&&tblPortList.getValueAt(i, 1).equals(portName))
										{
											ind=i;
										}
									}catch(Exception e)
									{
										continue;
									}


								}
								tblPortList.changeSelection(ind, 0, false, false);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}

				}


			}

		});

		txfUpdatePortName.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent arg0) 
			{	
				int row=tblPortList.getSelectedRow();
				if(row==-1)
					return;

				showSelectedTable();

			}

			public void focusLost(FocusEvent arg0) 
			{


			}});

		pnLeftNorth.add(txfIndex);

		pnLeftNorth.add(txfUpdatePortName);

		pnLeftOption = new JPanel();
		pnLeftOption .setLayout(new FlowLayout(FlowLayout.LEFT));


		txfPortName = new JTextField(28);

		txfPortName.addKeyListener(new KeyAdapter(){


			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					JTextField txf = (JTextField) e.getSource();
					String val = txf.getText();
					if(val.length()>0)
					{
						List<String> li=null;
						try {
							li=	baseService.getPortListByPatten(String.valueOf(val));
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(ManagePortDialog.this, e1.getMessage());
						}
						logger.debug("searched "+li.size());
						if(li.size()==1)
						{
							txf.setText(li.get(0));
						}
						else if(li.size()>1)
						{
							logger.debug("create portdialog");

							SearchPortDialog searchPortDialog = new SearchPortDialog(ManagePortDialog.this,li);
							searchPortDialog.createAndUpdateUI();


							if(searchPortDialog.result!=null)
							{
								txf.setText(searchPortDialog.result);		

							}else
							{
								txf.setText("");
							}
						}else 
						{
							JOptionPane.showMessageDialog(null, "해당 항구정보이 없습니다.");
							txf.setText("");
						}
					}
				}
			}

		});

		JLabel lbl2 = new JLabel("항구명: ");

		JButton butAdd = new JButton("추가(A)");
		butAdd.setMnemonic(KeyEvent.VK_A);
		butAdd.setActionCommand("추가");
		butAdd.addActionListener(this);

		pnLeftOption.add(lbl2);
		pnLeftOption.add(txfPortName);
		pnLeftOption.add(butAdd);


		pnLeft.add(new JScrollPane(tblPortList));

		

		pnLeft.add(pnLeftNorth,BorderLayout.NORTH);


		pnLeft.add(pnLeftOption,BorderLayout.SOUTH);

		JPanel pnRight = new JPanel();

		pnRight.setLayout(new BorderLayout());

		txflblIndex = new JTextField(2);
		txflblIndex.setEditable(false);
		txflblIndex.setBorder(BorderFactory.createEmptyBorder());
		txflblPortName = new JTextField(10);
		txflblPortName.setEditable(false);
		txflblPortName.setBorder(BorderFactory.createEmptyBorder());

		pnRightOption = new JPanel();
		pnRightOption.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnRightOption.setVisible(false);	
		cbxDeleteAll = new JCheckBox("대표 항구 삭제 시 세부 항구 삭제");
		pnRightOption.add(cbxDeleteAll);


		pnRight.add(pnRightOption,BorderLayout.NORTH);

		pnPortMain.add(pnLeft);

		pnMain.add(pnPortMain);
		try {

			tblPortList.retrive();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

		return pnMain;
	}

	private void showSelectedTable()
	{
		portName =String.valueOf(tblPortList.getValueAt(tblPortList.selectedindex, PORT_NAME_COLUM));

		portIndex = Integer.parseInt(String.valueOf(tblPortList.getValueAt(tblPortList.selectedindex, PORT_INDEX_COLUM)));	

		txfUpdatePortName.setText(portName);

		txfIndex.setText(String.valueOf(portIndex));

		tblPortList.changeSelection(tblPortList.selectedindex, PORT_INDEX_COLUM, false, false);
	}

	private Component buildInfo() {
		
		JPanel pnMain = new JPanel();
		
		pnMain.setLayout(new GridLayout(0,1));

		pnMain.add(new JLabel(""));
		
		pnMain.add(new JLabel("항구정보를 등록합니다"));
		
		pnMain.add(new JLabel(""));
		
		//pnMain.add(new JLabel("\t- 테이블에 입력 시에는항구명 셀에서  F2키를 눌러 입력 셀이 활성화 된 후에 입력 하십시요."));
		pnMain.setBackground(Color.white);
		return pnMain;
	}

	private Component buildControl() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JPanel pnRight = new JPanel();
		pnRight.setLayout( new FlowLayout(FlowLayout.RIGHT));


		JPanel pnLeft = new JPanel();

		JButton butArrange = new JButton("정렬");
		butArrange.addActionListener(this);


		pnLeft.setLayout( new FlowLayout(FlowLayout.LEFT));
		JButton butDel = new JButton("삭제");

		butDel.addActionListener(this);
		JButton butSave = new JButton("저장");
		butSave.setActionCommand("저장");
		//butSave.setMnemonic(KeyEvent.VK_S);


		JButton butCancel = new JButton("닫기");
		butCancel.setActionCommand("닫기");
		butCancel.setMnemonic(KeyEvent.VK_S);
		butCancel.addActionListener(this);

		pnRight.add(butArrange);
		pnRight.add(butDel);
		pnRight.add(butCancel);
		pnRight.add(butSave);

		pnMain.add(pnLeft,BorderLayout.WEST);
		pnMain.add(pnRight,BorderLayout.EAST);

		return pnMain;
	}
	public Component createLine()
	{
		JPanel pn = new JPanel();
		pn.setPreferredSize(new Dimension(15,0));
		return pn;
	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			for(int i=0;i<tblPortList.getRowCount();i++)
			{
				int index1=-1;
				Object value1 = tblPortList.getValueAt(i, PORT_INDEX_COLUM);
				if(value1 instanceof String)
				{
					index1=Integer.parseInt(tblPortList.getValueAt(i, PORT_INDEX_COLUM).toString());
				}else if(value1 instanceof Integer)
				{
					index1=(Integer)tblPortList.getValueAt(i, PORT_INDEX_COLUM);
				}
				for(int j=0;j<tblPortList.getRowCount();j++)
				{
					int index2=-1;
					Object value = tblPortList.getValueAt(j, PORT_INDEX_COLUM);
					if(value instanceof String)
					{
						try{
							index2=Integer.parseInt(tblPortList.getValueAt(j, PORT_INDEX_COLUM).toString());
						}catch(Exception ee)
						{
							JOptionPane.showMessageDialog(null, "정확한 인덱스를 입력 하십시요");
							return;
						}
					}else if(value instanceof Integer)
					{
						index2=(Integer)tblPortList.getValueAt(j, PORT_INDEX_COLUM);
					}
					if(i!=j)
					{
						if(index1!=-1&&index2!=-1&&index1==index2)
						{
							JOptionPane.showMessageDialog(null, " 동일한 인덱스("+index1+")가 존재합니다.");
							return;
						}
					}
				}
			}


			for(int i=0;i<tblPortList.getRowCount();i++)
			{
				Object v= tblPortList.getValueAt(i, PORT_INDEX_COLUM);
				if(v==null)
					break;
				String port_name=(String) tblPortList.getValueAt(i, PORT_NAME_COLUM);


				TablePort port = new TablePort();

				port.setTable_id(table_id);
				port.setPort_type(TablePort.TYPE_PARENT);


				Object value= tblPortList.getValueAt(i, PORT_NAME_COLUM);
				if(value instanceof Integer)
				{
					port.setPort_index((Integer)value);
				}else
				{
					port.setPort_index(Integer.parseInt(value.toString()));
				}
				try {
					TablePort pp=(TablePort) tableService.getTablePort(port);
					port.setParent_port(port_name);
					port.setPort_name(port_name);
					if(pp==null)
					{
						try
						{
							tableService.insertPortList(port);
						}catch(SQLException ee23)
						{
							// 중복							
							if(ee23.getErrorCode()==2627)
							{
								tableService.updateTablePort(port);
							}
						}
					}
					else
					{
						tableService.updateTablePort(port);
					}

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}			



			this.OPTION = ManagePortDialog.UPDATE_OPTION;
			this.setVisible(false);
			dispose();
		}
		else if(command.equals("저장"))
		{

			try {
				updateTableInfo();
			} catch (SQLException e1) {

				JOptionPane.showMessageDialog(this, e1.getMessage());
				e1.printStackTrace();
			}
		}
		else if(command.equals("닫기"))
		{
			this.OPTION = ManagePortDialog.CANCEL_OPTION;
			this.setVisible(false);
			dispose();
			base.searchADVTable();

		}

		else if(command.equals("적용 및 닫기"))
		{

			try {
				updateTableInfo();
				
			} catch (SQLException e1) {

				JOptionPane.showMessageDialog(this, e1.getMessage());
				e1.printStackTrace();
			}


			this.OPTION = ManagePortDialog.CANCEL_OPTION;
			this.setVisible(false);
			dispose();
			base.searchADVTable();

		}else if(command.equals("삭제"))
		{
			try {
				tblPortList.delete(cbxDeleteAll.isSelected());
				
				updateTableInfo();
				
				
				
				
				tblPortList.retrive();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		else if(command.equals("검색"))
		{
			if(currentTable==null)
				return;
			int row=currentTable.getSelectedRow();

			if(row==-1)
				return;


			PortSearchDialog dialog = new PortSearchDialog(this);
			dialog.setRow(row);
			dialog.createAndUpdateUI();
			if(dialog.portName!=null)
			{
				currentTable.setValueAt(dialog.portName, row, PORT_NAME_COLUM);
			}

		}
		else if(command.equals("정렬"))
		{
			try {
				tblPortList.retrive();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(command.equals("추가"))
		{
			try 
			{
				String port_name = txfPortName.getText();

				tblPortList.insertPort(port_name);
				
				
				int count=tableService.getPortCount(table_id);				
				ShippersTable op =tableService.getTableById(table_id);				
				ShippersTable table = new ShippersTable();
				table.setGubun(op.getGubun());
				table.setTable_id(table_id);
				table.setPort_col(count);
				
				tableService.updateTable(table);
				
				base.setPortCount(count);
				
				tblPortList.retrive();
				
				txfPortName.setText("");

			}
			catch (SQLException e1) {
				JOptionPane.showMessageDialog(ManagePortDialog.this, e1.getMessage());
				e1.printStackTrace();
			}
			catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(ManagePortDialog.this, "숫자를 입력하십시요");
				txfIndex.setText("");
				return;
			}
		}
	}
	private void updateTableInfo() throws SQLException {
		int count=tableService.getPortCount(table_id);
		ShippersTable op =tableService.getTableById(table_id);
		ShippersTable table = new ShippersTable();
		table.setTable_id(table_id);
		table.setPort_col(count);		
		table.setGubun(op.getGubun());
		tableService.updateTable(table);
		base.setPortCount(count);
	}



	/**
	 * @param e
	 */
	private void autoWritePort(KeyEvent e) {
		final JTable table = (JTable) e.getSource();
		int col =table.getSelectedColumn();
		int row = table.getSelectedRow();
		logger.debug("auto write");

		switch (col) {
		case PORT_INDEX_COLUM:
			System.out.println("index colum");

			updatePointIndex( row);

			break;

		case PORT_NAME_COLUM:
			System.out.println("new port");
			addNewPort( col, row);

			break;

		default:
			break;
		}
	}
	private void addNewPort(int col, int row) {
		Object val = tblPortList.getValueAt(row, PORT_NAME_COLUM);
		if(val==null)
		{
			System.out.println("null");
			return;
		}

		List<String> li=null;
		try {
			li=	baseService.getPortListByPatten(String.valueOf(val));
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage());
		}
		if(li.size()==1)
		{
			addPort(tblPortList, row, li.get(0));
		}
		else if(li.size()>1)
		{
			SearchPortDialog searchPortDialog = new SearchPortDialog(ManagePortDialog.this,li);
			searchPortDialog.createAndUpdateUI();
			if(searchPortDialog.result!=null)
			{					
				addPort(tblPortList, row, searchPortDialog.result);
			}else
			{
				System.out.println("null2");
				tblPortList.setValueAt(null, row, col);
			}

		}else 
		{

			if(!isExitPort())
			{
				try {
					tblPortList.retrive();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(null, val+" 해당 항구정보이 없습니다.");

			}
		}
	}
	private void updatePointIndex(int row) {


		Object val = tblPortList.getValueAt(row, PORT_INDEX_COLUM);
		Object port_name = tblPortList.getValueAt(row,PORT_NAME_COLUM);


		if(val==null||port_name==null)
		{
			tblPortList.setValueAt(null, row, PORT_INDEX_COLUM);
			tblPortList.setValueAt(null, row, PORT_NAME_COLUM);
			System.out.println(row+" null;"+val+","+port_name);
			return;
		}

		try 
		{
			TablePort port = new TablePort();
			port.setPort_type(TablePort.TYPE_PARENT);
			port.setPort_name(String.valueOf(port_name));
			port.setPort_index(tblPortList.selectedPortIndex);
			port.setNew_port_index(Integer.valueOf(val.toString()));
			port.setTable_id(this.table_id);
			System.out.println("upste:"+Integer.valueOf(val.toString())+","+tblPortList.selectedPortIndex+","+port.getPort_name());
			tableService.updateTablePortIndex2(port);
		} catch (SQLException e1) 
		{

			if(e1.getErrorCode()==2627)
			{
				tblPortList.clearSelection();
				JOptionPane.showMessageDialog(this, "해당 인덱스에 동일한 항구명이 존재합니다."+e1.getErrorCode());
				try {
					tblPortList.retrive();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}else
			{
				e1.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				tblPortList.retrive();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}	
	private boolean isExitPort() {
		Code code_info = new Code();
		code_info.setCode_type("port_exception");


		try {
			return baseService.getCodeInfoList(code_info).size()<0?true:false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	private void addPort(final JTable table, int row, String port_name) {
		try 
		{
			int max=tableService.getMaxPortIndex(this.table_id);
			int r=0;
			for(int i=1;i<max;i++)
			{
				TablePort port = new TablePort();
				port.setPort_index(i);
				port.setTable_id(table_id);
				port.setPort_type(TablePort.TYPE_PARENT);
				List table_Port=tableService.getTablePortList(port);

				if(table_Port.size()==0)
				{
					r=i;
					break;
				}
			}
			TablePort port = new TablePort();
			port.setPort_type(TablePort.TYPE_PARENT);
			port.setPort_name(port_name);
			port.setParent_port(port_name);
			port.setTable_id(this.table_id);
			if(r!=0)
			{
				port.setPort_index(r);
			}
			else
			{
				port.setPort_index(max+1);
			}
			try{
				Object obj=table.getValueAt(row, PORT_INDEX_COLUM);
				if(obj==null)
				{
					tableService.insertPortList(port);
				}else
				{
					port.setPort_index(Integer.valueOf(obj.toString()));
					tableService.updateTablePort(port);
				}
				tblPortList.retrive();
			}catch(SQLException se)
			{
				if(se.getErrorCode()!=2627)
				{
					JOptionPane.showMessageDialog(null, se.getMessage());
					se.printStackTrace();
				}
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	class MyKeyAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e) {
			final JTable table = (JTable) e.getSource();
			if(table.getSelectedRow()==-1)
				return;
		}
		public void keyReleased(KeyEvent e) 
		{
			final JTable table = (JTable) e.getSource();


			if(table.getSelectedRow()==-1)
			{
				return;
			}
			tblPortList.selectedindex = table.getSelectedRow();
			selectedcolIndex = table.getSelectedColumn();

			try{
				Object indexVal =tblPortList.getValueAt(tblPortList.selectedindex, PORT_INDEX_COLUM);
				if(indexVal==null)
					return;
				if(indexVal instanceof Integer)
				{
					tblPortList.selectedPortIndex=(Integer)indexVal; 
				}else
				{
					tblPortList.selectedPortIndex=Integer.valueOf(String.valueOf(indexVal));
				}

			}catch (Exception ee) {
				tblPortList.selectedPortIndex=0;
				ee.printStackTrace();
			}
			if(e.getKeyCode()==KeyEvent.VK_DELETE)
			{		
				try {
					tblPortList.delete(cbxDeleteAll.isSelected());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;

			}
			if(selectedcolIndex==PORT_NAME_COLUM&&e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				autoWritePort(e);

			}
		}	
	}
	/**
	 * @author archehyun
	 *
	 */
	class PortDropHandler extends TransferHandler
	{
		public boolean canImport(TransferSupport support) { 

			if (!support.isDrop()) { 

				return true; 

			} 

			return support.isDataFlavorSupported(DataFlavor.stringFlavor); 

		} 

		public boolean importData(TransferSupport support) { 

			if (!canImport(support)) { 

				return false; 

			} 

			Transferable transferable = support.getTransferable(); 

			String line; 

			try { 

				line = (String) transferable.getTransferData(DataFlavor.stringFlavor); 

			} catch (Exception e) { 

				return false; 

			} 

			JList.DropLocation dl = (JList.DropLocation) support.getDropLocation(); 

			int index = dl.getIndex(); 

			String[] data = line.split(","); 

			for (String item: data) { 

				//			if (!item.isEmpty()) 

				//					model2.add(index++, item.trim()); 

			} 

			return true; 

		}
	}
	/**
	 * @author archehyun
	 *
	 */
	class MyMouseAdapter extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e) {

			JTable t=(JTable) e.getSource();
			currentTable=t;
			if(currentTable.equals(tblPortList))
			{
				int row=t.getSelectedRow();
				if (row!=-1)
				{
					Object val =t.getValueAt(row, PORT_INDEX_COLUM);
					if(val==null)
						return;
					int port_index;
					if(val instanceof Integer)
					{
						port_index=(Integer)val;
					}else
					{
						port_index=Integer.parseInt(String.valueOf(val.toString()));
					}

					tblPortList.selectedindex=port_index;

					if(t.getValueAt(row, PORT_INDEX_COLUM)!=null)
					{
						txflblIndex.setText(String.valueOf(t.getValueAt(row, PORT_INDEX_COLUM)));	
						txfIndex.setText(String.valueOf(t.getValueAt(row, PORT_INDEX_COLUM)));
					}else
					{
						txflblIndex.setText("");
					}

					if(t.getValueAt(row, PORT_NAME_COLUM)!=null)
					{
						txflblPortName.setText(String.valueOf(t.getValueAt(row, PORT_NAME_COLUM)));	
						txfUpdatePortName.setText(String.valueOf(t.getValueAt(row, PORT_NAME_COLUM)));
					}else
					{
						txflblPortName.setText("");
					}
				}
			}
		}
	}



	class PortListRenderer extends JLabel implements ListCellRenderer {
		private final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

		public PortListRenderer() {
			setOpaque(true);
			setIconTextGap(12);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			TablePort entry = (TablePort) value;
			setText(entry.getPort_index()+":"+entry.getPort_name());

			if (isSelected) {
				setBackground(HIGHLIGHT_COLOR);
				setForeground(Color.white);
			} else {
				setBackground(Color.white);
				setForeground(Color.black);
			}
			return this;
		}
	}
	
	static class TablePortTransable  implements Transferable, Shape,Serializable
	{
		
		  double maxX = Double.NEGATIVE_INFINITY; // The bounding box

		  double maxY = Double.NEGATIVE_INFINITY;

		  double minX = Double.POSITIVE_INFINITY;

		  double minY = Double.POSITIVE_INFINITY;
		  
		
		private TablePort tablePort;

		public TablePort getTablePort() {
			return tablePort;
		}

		public void setTablePort(TablePort tablePort) {
			this.tablePort = tablePort;
		}

		// This is the custom DataFlavor for Scribble objects
		public static final DataFlavor scribbleDataFlavor = new DataFlavor(
				TablePortTransable.class, "Scribble");

		// This is a list of the flavors we know how to work with
		public  DataFlavor[] supportedFlavors = { scribbleDataFlavor,
				DataFlavor.stringFlavor };

		/** Return the data formats or "flavors" we know how to transfer */
		public DataFlavor[] getTransferDataFlavors() {
			return (DataFlavor[]) supportedFlavors.clone();
		}

		/** Check whether we support a given flavor */
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return (flavor.equals(scribbleDataFlavor) || flavor
					.equals(DataFlavor.stringFlavor));
		}

		/**
		 * Return the scribble data in the requested format, or throw an exception
		 * if we don't support the requested format
		 */
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (flavor.equals(scribbleDataFlavor)) {
				return this;
			} else if (flavor.equals(DataFlavor.stringFlavor)) {
				return toString();
			} else
				throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public boolean contains(Point2D p) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean contains(Rectangle2D r) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean contains(double x, double y) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean contains(double x, double y, double w, double h) {
			// TODO Auto-generated method stub
			return false;
		}

		  /** Return the bounding box of the Shape */
		  public Rectangle getBounds() {
		    return new Rectangle((int) (minX - 0.5f), (int) (minY - 0.5f),
		        (int) (maxX - minX + 0.5f), (int) (maxY - minY + 0.5f));
		  }

		  /** Return the bounding box of the Shape */
		  public Rectangle2D getBounds2D() {
		    return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
		  }


		@Override
		public PathIterator getPathIterator(AffineTransform at) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PathIterator getPathIterator(AffineTransform at, double flatness) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean intersects(Rectangle2D r) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean intersects(double x, double y, double w, double h) {
			// TODO Auto-generated method stub
			return false;
		}

		  public void translate(double x, double y) {

		    minX += x;
		    maxX += x;
		    minY += y;
		    maxY += y;
		  }
		  public void moveto(double x, double y) {

			    lineto(x, y);
			  }

			  /**
			   * Add the point (x,y) to the end of the current polyline
			   */
			  public void lineto(double x, double y) {


			    // See if the point enlarges our bounding box
			    if (x > maxX)
			      maxX = x;
			    if (x < minX)
			      minX = x;
			    if (y > maxY)
			      maxY = y;
			    if (y < minY)
			      minY = y;
			  }

	}
	/**
	 * @author archehyun
	 *
	 *//*
	class PortListTable extends JTable implements DropTargetListener,  DragGestureListener, MouseListener, DragSourceListener
	{
		TablePortTransable portTransable;
		
		private int selectedportindex;
		
		private int selectedindex;	
		
		private String table_id;
		
		DragSource dragSource; // A central DnD object
		
		DropTarget dtg;
		
		List<TablePort> portli;

		public PortListTable(String table_id) {

			portTransable = new TablePortTransable();

			this.table_id = table_id;
			
			addMouseListener(new MyMouseAdapter());

			addKeyListener(new MyKeyAdapter());			
			
			this.addMouseListener(this);

			dragSource = DragSource.getDefaultDragSource();
			
			dragSource.createDefaultDragGestureRecognizer(this, // What component
					DnDConstants.ACTION_COPY_OR_MOVE, // What drag types?
					this);// the listener

			this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			dtg = new DropTarget(this, this);

			this.setDropTarget(dtg);

			setName(TablePort.TYPE_PARENT);

			

		}
		*//**
		 * @throws SQLException
		 *//*
		private void retrive() throws SQLException {

			portli=tableService.getParentPortList(this.table_id);

			DefaultTableModel model = new DefaultTableModel();

			model.addColumn("순서");

			model.addColumn("항구명");

			if(portli.size()<10)
			{
				model.setRowCount(portli.size()+10);
			}else
			{
				model.setRowCount(portli.size()+5);
			}

			for(int i=0;i<portli.size();i++)
			{
				TablePort port = portli.get(i);
				model.setValueAt(port.getPort_name(), i, PORT_NAME_COLUM);
				model.setValueAt(port.getPort_index(), i, PORT_INDEX_COLUM);
			}

			setModel(model);

			TableColumnModel colModel=tblPortList.getColumnModel();

			TableColumn col=colModel.getColumn(PORT_INDEX_COLUM);

			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

			renderer.setHorizontalAlignment(SwingConstants.CENTER);

			col.setMaxWidth(50);

			col.setCellRenderer(renderer);

			changeSelection(selectedportindex, selectedcolIndex, false, false);
		}
		
		private void chagePortIndex(TablePort onePortInfo, TablePort twoPortInfo) throws SQLException
		{
			int oneIndex =onePortInfo.getPort_index();
			int twoIndex =twoPortInfo.getPort_index();
			
			onePortInfo.setPort_index(twoIndex);
			twoPortInfo.setPort_index(oneIndex);
			tableService.updateTablePort(onePortInfo);
			tableService.updateTablePort(twoPortInfo);

			tblPortList.retrive();
		}
		

		private void insertPort(String port_name) throws SQLException
		{
			int max=tableService.getMaxPortIndex(this.table_id);
			if(port_name.length()<=0||port_name==null||port_name.equals(""))
			{
				JOptionPane.showMessageDialog(null, "항구명을 입력하세요");
				return;
			}
			PortInfo info =baseService.getPortInfoByPortName(port_name);
			if(info==null)
			{
				Code code_info = new Code();
				code_info.setCode_name(port_name);
				baseService = new BaseServiceImpl();
				Code templi=	baseService.getCodeInfo(code_info);
				if(templi==null)
				{
					JOptionPane.showMessageDialog(ManagePortDialog.this, "("+port_name+") 존재하지 않는 항구입니다.");
					return;	
				}
			}
			TablePort port = new TablePort();
			port.setPort_type(TablePort.TYPE_PARENT);
			port.setPort_name(port_name);
			port.setParent_port(port_name);
			port.setTable_id(ManagePortDialog.this.table_id);

			port.setPort_index(max+1);

			tableService.insertPortList(port);

			tblPortList.retrive();

			txfPortName.setText("");
			
			int count=tableService.getPortCount(table_id);
			
			ShippersTable op =tableService.getTableById(table_id);
			
			ShippersTable table = new ShippersTable();
			table.setGubun(op.getGubun());
			table.setTable_id(table_id);
			table.setPort_col(count);
			
			tableService.updateTable(table);
			
			base.setPortCount(count);
		}

		*//**
		 * 삭제
		 *//*
		private void delete() {

			int row=getSelectedRow();
			if(row<-1)
				return;


			String port_name= (String) getValueAt(row, PORT_NAME_COLUM);
			if(port_name==null)
				return;
			TablePort port = new TablePort();
			port.setPort_name(port_name);
			port.setTable_id(table_id);

			if(getName().equals(TablePort.TYPE_PARENT))
			{
				port.setPort_type(TablePort.TYPE_PARENT);

				Object temp_port_index = getValueAt(row, PORT_INDEX_COLUM);
				if(temp_port_index instanceof Integer)
				{
					port.setPort_index((Integer) temp_port_index);
				}else if(temp_port_index instanceof String)
				{
					port.setPort_index(Integer.valueOf( (String)temp_port_index));
				} 

				if(cbxD.isSelected())
				{						
					port.setPort_name(null);
				}

			}
			else
			{
				port.setPort_type(TablePort.TYPE_CHAILD);
				port.setPort_index( Integer.parseInt(txflblIndex.getText()));
			}
			try {
				tableService.deleteTablePort(port);

				retrive();

				changeSelection(row==0?0:row-1, PORT_NAME_COLUM, false, false);

				int count=tableService.getPortCount(table_id);
				ShippersTable op =tableService.getTableById(table_id);
				ShippersTable table = new ShippersTable();
				table.setTable_id(table_id);
				table.setPort_col(count);		
				table.setGubun(op.getGubun());
				tableService.updateTable(table);
				base.setPortCount(count);


				logger.debug("delete port:"+table_id+","+port_name);

			} catch (SQLException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(ManagePortDialog.this, "error:"+e1.getMessage());
			}
		}
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {

			System.out.println("endter");
		}
		@Override
		public void dragExit(DropTargetEvent dte) {
			System.out.println("exit");
		}
		
		TablePort chagePort;
		public void dragOver(DropTargetDragEvent dtde) {

			int row=this.rowAtPoint(dtde.getLocation());
			
			if(portli.size()>row)
			{
				TablePort  portItem=portli.get(row);
				chagePort = portItem;
			}
			else
			{
				chagePort = null;
				return;
				
			}

			this.changeSelection(row, 0, false, false);


		}
		@Override
		public void drop(DropTargetDropEvent dtde) {
		
			
		    if (dtde.isDataFlavorSupported(TablePortTransable.scribbleDataFlavor)
		            || dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
		    	dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		        } else {
		        	dtde.rejectDrop();
		        	
		          return;
		        }
		    
		    Transferable t = dtde.getTransferable(); // Holds the dropped data
		    // First, try to get the data directly as a scribble object
		    TablePortTransable  droppedScribble;
		    try {
		    droppedScribble = (TablePortTransable) t
		          .getTransferData(TablePortTransable.scribbleDataFlavor);

		    
		    if(chagePort==null)
		    	return;

		    
		    chagePortIndex(droppedScribble.getTablePort(), chagePort);
		    } catch (Exception ex) { // unsupported flavor, IO exception, etc
		    	ex.printStackTrace();
		      // If that doesn't work, try to get it as a String and parse it
		      try {
		        String s = (String) t.getTransferData(DataFlavor.stringFlavor);
		      //  droppedScribble = Scribble.parse(s);
		      } catch (Exception ex2) {
		    	  System.out.println("error drop");
		        // If we still couldn't get the data, tell the system we failed
		    	  dtde.dropComplete(false);
		        return;
		      }
		    }  

		}
		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {


		}
		@Override
		public void dragGestureRecognized(DragGestureEvent e) {

			MouseEvent inputEvent = (MouseEvent) e.getTriggerEvent();
			int x = inputEvent.getX();
			int y = inputEvent.getY();

			Cursor cursor;
			switch (e.getDragAction()) {
			case DnDConstants.ACTION_COPY:
				cursor = DragSource.DefaultCopyDrop;
				break;
			case DnDConstants.ACTION_MOVE:
				cursor = DragSource.DefaultMoveDrop;
				break;
			default:
				return; // We only support move and copys
			}
			if (dragSource.isDragImageSupported()) {
				Image dragImage = this.createImage(100,
						25);
				Graphics2D g = (Graphics2D) dragImage.getGraphics();
				Rectangle scribbleBox = portTransable.getBounds();
				g.setColor(new Color(0, 0, 0, 0)); // transparent background
				g.fillRect(0, 0, 200, 25);
				g.setColor(Color.black);				
				g.drawString(portTransable.getTablePort().getPort_name(), 0, 15);
				g.translate(-scribbleBox.x, -scribbleBox.y);
				
				//g.draw(portTransable);

				Point hotspot = new Point(-scribbleBox.x, -scribbleBox.y);

				// Now start dragging, using the image.
				e.startDrag(cursor, dragImage, hotspot, portTransable, this);

			}
			else
			{
				e.startDrag(cursor, portTransable,this);
			}
			 return;


		}
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}
		@Override
		public void mousePressed(MouseEvent e) {

			int row = this.getSelectedRow();
			if(row<0||portli.size()<row-1)
				return;

			TablePort portInfo = portli.get(row);
			
			this.portTransable.setTablePort(portInfo);
			
			portTransable.moveto(e.getX(), e.getY());

		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}
		@Override
		public void dragDropEnd(DragSourceDropEvent e) {
		    if (!e.getDropSuccess())
		        return;
		    int action = e.getDropAction();
		    if (action == DnDConstants.ACTION_MOVE) {
		      scribbles.remove(beingDragged);
		      beingDragged = null;
		      repaint();
		    	//테이블 정보 업데이트
		    	System.out.println("테이블 정보 업데이트");
		    	
		    	
		    }
		}
		@Override
		public void dragEnter(DragSourceDragEvent arg0) {
			// TODO Auto-generated method stub

		}
		@Override
		public void dragExit(DragSourceEvent arg0) {
			// TODO Auto-generated method stub

		}
		@Override
		public void dragOver(DragSourceDragEvent arg0) {
			// TODO Auto-generated method stub

		}
		@Override
		public void dropActionChanged(DragSourceDragEvent arg0) {
			// TODO Auto-generated method stub

		}
	}*/


}
