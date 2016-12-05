package com.ksg.view.search.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Port;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.dialog.PortSearchDialog;
import com.ksg.view.search.SearchUI;
import com.ksg.view.util.ViewUtil;

/**
 * @author 박창현
 * @explanatoin 항구 정보를 등록/ 삭제 수정 하는 창을 표시
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
	private JTable tblPortList;
	private JTable currentTable;
	private int selectedportindex;
	private int selectedindex;

	private SearchUI base;
	private JCheckBox cbxD;
	private JPanel pnRightOption;
	private JPanel pnLeftOption;
	private JTextField txfIndex;
	private JTextField txfPortName;
	private JTextField txflblIndex;
	private JTextField txflblPortName;
	public int selectedcolIndex;
	private JTextField txfUpdatePortName;
	private String portName;
	private int index;
	public ManagePortDialog(String table_id,SearchUI base) 
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

		tblPortList = new JTable();
		
		tblPortList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		tblPortList.setName(Table_Port.TYPE_PARENT);
		tblPortList.addMouseListener(new MyMouseAdapter());
		currentTable =tblPortList;

		tblPortList.addKeyListener(new MyKeyAdapter());


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

				selectedindex = row;
				txfUpdatePortName.setText(String.valueOf(tblPortList.getValueAt(row, PORT_NAME_COLUM)));
				txfIndex.setText(String.valueOf(tblPortList.getValueAt(row, PORT_INDEX_COLUM)));

			}

			public void focusLost(FocusEvent arg0) 
			{


			}});
		txfIndex.addKeyListener(new KeyListener()
		{
			String portName;
			int index;
			public void keyPressed(KeyEvent arg0) 
			{
				if(arg0.getKeyCode()==KeyEvent.VK_UP)
				{
					if(selectedindex>0)
						selectedindex-=1;
					txfUpdatePortName.setText(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM)));
					portName =String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM)); 
					txfIndex.setText(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM)));
					index = Integer.parseInt(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM)));
					
					tblPortList.changeSelection(selectedindex, PORT_INDEX_COLUM, false, false);
				}
				else if(arg0.getKeyCode()==KeyEvent.VK_DOWN)
				{					
					selectedindex+=1;
					if(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM)==null)
					{
						selectedindex-=1;
						return;
					}
					txfUpdatePortName.setText(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM)));
					portName =String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM));
					txfIndex.setText(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM)));
					index = Integer.parseInt(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM)));
					tblPortList.changeSelection(selectedindex, PORT_INDEX_COLUM, false, false);
				}
				else if(arg0.getKeyCode()==KeyEvent.VK_ENTER)
				{
					try 
					{
						Table_Port port = new Table_Port();
						port.setPort_type(Table_Port.TYPE_PARENT);

						if(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM))==null)
						{
							return;
						}
						port.setPort_name(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM)));
						port.setPort_index(index);
						System.out.println(portName);
						port.setNew_port_index(Integer.parseInt(txfIndex.getText()));
						port.setTable_id(ManagePortDialog.this.table_id);
						tableService.updateTablePortIndex2(port);

						portName =port.getPort_name();
						index = port.getNew_port_index();


					} catch (SQLException e1) 
					{

						if(e1.getErrorCode()==2627)
						{
							tblPortList.clearSelection();
							JOptionPane.showMessageDialog(ManagePortDialog.this, "해당 인덱스에 동일한 항구명이 존재합니다."+e1.getErrorCode());
							try {
								updatePortTable();
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
							updatePortTable();
							int ind=0;
							for(int i=0;i<tblPortList.getRowCount();i++)
							{
								try{
									int vindex = Integer.parseInt(String.valueOf(tblPortList.getValueAt(i, 0)));
									if(vindex==index&&tblPortList.getValueAt(i, 1).equals(portName))
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

			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}});
		txfUpdatePortName = new JTextField(30);


		txfUpdatePortName.addKeyListener(new KeyListener(){



			public void keyPressed(KeyEvent arg0) 
			{
				if(arg0.getKeyCode()==KeyEvent.VK_UP)
				{
					if(selectedindex>0)
						selectedindex-=1;
					txfUpdatePortName.setText(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM)));
					portName =String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM));
					txfIndex.setText(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM)));
					index = Integer.parseInt(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM)));
					tblPortList.changeSelection(selectedindex, PORT_NAME_COLUM, false, false);
				}
				else if(arg0.getKeyCode()==KeyEvent.VK_DOWN)
				{					
					selectedindex+=1;
					if(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM)==null)
					{
						selectedindex-=1;
						return;
					}
					txfUpdatePortName.setText(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM)));
					portName =String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM));
					txfIndex.setText(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM)));
					index = Integer.parseInt(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM)));
					tblPortList.changeSelection(selectedindex, PORT_INDEX_COLUM, false, false);
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
							Table_Port port = new Table_Port();
							port.setPort_type(Table_Port.TYPE_PARENT);

							/*if(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM))==null)
							{
								return;
							}*/
							port.setPort_name(portName);
							port.setPort_index(index);
							System.out.println(portName);
							port.setNew_port_name(txf.getText());
							port.setTable_id(ManagePortDialog.this.table_id);
							tableService.updateTablePortName(port);

							portName =port.getPort_name();
							index = port.getNew_port_index();


						} catch (SQLException e1) 
						{

							if(e1.getErrorCode()==2627)
							{
								tblPortList.clearSelection();
								JOptionPane.showMessageDialog(ManagePortDialog.this, "해당 인덱스에 동일한 항구명이 존재합니다."+e1.getErrorCode());
								try {
									updatePortTable();
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
								updatePortTable();
								int ind=0;
								for(int i=0;i<tblPortList.getRowCount();i++)
								{
									try{
										int vindex = Integer.parseInt(String.valueOf(tblPortList.getValueAt(i, 0)));
										if(vindex==index&&tblPortList.getValueAt(i, 1).equals(portName))
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

			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}});
		txfUpdatePortName.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent arg0) 
			{	
				int row=tblPortList.getSelectedRow();
				if(row==-1)
					return;

				selectedindex = row;
				txfUpdatePortName.setText(String.valueOf(tblPortList.getValueAt(row, PORT_NAME_COLUM)));
				txfIndex.setText(String.valueOf(tblPortList.getValueAt(row, PORT_INDEX_COLUM)));
				portName =String.valueOf(tblPortList.getValueAt(selectedindex, PORT_NAME_COLUM));
				index = Integer.parseInt(String.valueOf(tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM)));

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
		//JPanel pnRightNorth = new JPanel();
		//pnRightNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
		/*JLabel lblPortName = new JLabel("대표 항구명 : ");
		lblPortName.setFont(defaultfont);*/


		txflblIndex = new JTextField(2);
		txflblIndex.setEditable(false);
		txflblIndex.setBorder(BorderFactory.createEmptyBorder());
		txflblPortName = new JTextField(10);
		txflblPortName.setEditable(false);
		txflblPortName.setBorder(BorderFactory.createEmptyBorder());



		/*pnRightNorth.add(lblPortName);
		pnRightNorth.add(txflblPortName);
		pnRightNorth.add(lblInfo);
		pnRightNorth.add(txflblIndex);*/


		pnRightOption = new JPanel();
		pnRightOption.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnRightOption.setVisible(false);	
		cbxD = new JCheckBox("대표 항구 삭제 시 세부 항구 삭제");
		pnRightOption.add(cbxD);


		pnRight.add(pnRightOption,BorderLayout.NORTH);

		pnPortMain.add(pnLeft);

		pnMain.add(pnPortMain);
		try {

			updatePortTable();
		} catch (SQLException e) {
			 JOptionPane.showMessageDialog(this, e.getMessage());
		}

		return pnMain;
	}

	private void updatePortTable() throws SQLException {
		List<Table_Port> portli=tableService.getParentPortList(this.table_id);
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
			Table_Port port = portli.get(i);
			model.setValueAt(port.getPort_name(), i, PORT_NAME_COLUM);
			model.setValueAt(port.getPort_index(), i, PORT_INDEX_COLUM);

		}

		tblPortList.setModel(model);
		TableColumnModel colModel=tblPortList.getColumnModel();
		TableColumn col=colModel.getColumn(PORT_INDEX_COLUM);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(50);
		col.setCellRenderer(renderer);

		tblPortList.changeSelection(selectedportindex, selectedcolIndex, false, false);
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

		JButton butCancel = new JButton("적용 및 닫기(S)");
		butCancel.setActionCommand("적용 및 닫기");
		butCancel.setMnemonic(KeyEvent.VK_S);
		butCancel.addActionListener(this);

		pnRight.add(butArrange);
		pnRight.add(butDel);
		pnRight.add(butCancel);

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


				Table_Port port = new Table_Port();

				port.setTable_id(table_id);
				port.setPort_type(Table_Port.TYPE_PARENT);


				Object value= tblPortList.getValueAt(i, PORT_NAME_COLUM);
				if(value instanceof Integer)
				{
					port.setPort_index((Integer)value);
				}else
				{
					port.setPort_index(Integer.parseInt(value.toString()));
				}
				try {
					Table_Port pp=(Table_Port) tableService.getTablePort(port);
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
		else if(command.equals("적용 및 닫기"))
		{

			try {
				int count = tableService.getPortCount(table_id);
				ShippersTable op =tableService.getTableById(table_id);
				
				ShippersTable table = new ShippersTable();
				table.setTable_id(table_id);
				table.setPort_col(count);
				table.setGubun(op.getGubun());
				tableService.updateTable(table);
				base.setPortCount(count);
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
			delAction();
		}
		else if(command.equals("검색"))
		{
			if(currentTable==null)
				return;
			int row=currentTable.getSelectedRow();
			if(row!=-1)
			{
				PortSearchDialog dialog = new PortSearchDialog(this);
				dialog.setRow(row);
				dialog.createAndUpdateUI();
				if(dialog.portName!=null)
				{
					currentTable.setValueAt(dialog.portName, row, PORT_NAME_COLUM);
				}
			}
		}
		else if(command.equals("정렬"))
		{
			try {
				updatePortTable();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(command.equals("추가"))
		{
			try 
			{
				int max=tableService.getMaxPortIndex(ManagePortDialog.this.table_id);
				String port_name = txfPortName.getText();
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
				Table_Port port = new Table_Port();
				port.setPort_type(Table_Port.TYPE_PARENT);
				port.setPort_name(port_name);
				port.setParent_port(port_name);
				port.setTable_id(ManagePortDialog.this.table_id);

				port.setPort_index(max+1);
				tableService.insertPortList(port);

				updatePortTable();

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
	private void delAction() {
		int row=currentTable.getSelectedRow();
		{
			String port_name= (String) currentTable.getValueAt(row, PORT_NAME_COLUM);
			if(port_name==null)
				return;
			Table_Port port = new Table_Port();
			port.setPort_name(port_name);
			port.setTable_id(table_id);

			if(currentTable.getName().equals(Table_Port.TYPE_PARENT))
			{
				port.setPort_type(Table_Port.TYPE_PARENT);

				Object temp_port_index = currentTable.getValueAt(row, PORT_INDEX_COLUM);
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
				port.setPort_type(Table_Port.TYPE_CHAILD);
				port.setPort_index( Integer.parseInt(txflblIndex.getText()));
			}
			try {
				tableService.deleteTablePort(port);

				updatePortTable();

				tblPortList.changeSelection(row==0?0:row-1, PORT_NAME_COLUM, false, false);
				int count=tableService.getPortCount(table_id);
				ShippersTable op =tableService.getTableById(table_id);
				ShippersTable table = new ShippersTable();
				table.setTable_id(table_id);
				table.setPort_col(count);		
				table.setGubun(op.getGubun());
				tableService.updateTable(table);
				base.setPortCount(count);

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void autoWritePort(KeyEvent e) {
		final JTable table = (JTable) e.getSource();
		int col =table.getSelectedColumn();
		int row = table.getSelectedRow();
		System.out.println("autor write");
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

			if(!isExPort())
			{
				try {
					updatePortTable();
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
			Table_Port port = new Table_Port();
			port.setPort_type(Table_Port.TYPE_PARENT);
			port.setPort_name(String.valueOf(port_name));
			port.setPort_index(selectedportindex);
			port.setNew_port_index(Integer.valueOf(val.toString()));
			port.setTable_id(this.table_id);
			System.out.println("upste:"+Integer.valueOf(val.toString())+","+selectedportindex+","+port.getPort_name());
			tableService.updateTablePortIndex2(port);
		} catch (SQLException e1) 
		{

			if(e1.getErrorCode()==2627)
			{
				tblPortList.clearSelection();
				JOptionPane.showMessageDialog(this, "해당 인덱스에 동일한 항구명이 존재합니다."+e1.getErrorCode());
				try {
					this.updatePortTable();
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
				updatePortTable();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}	
	private boolean isExPort() {
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
				Table_Port port = new Table_Port();
				port.setPort_index(i);
				port.setTable_id(table_id);
				port.setPort_type(Table_Port.TYPE_PARENT);
				List table_Port=tableService.getTablePortList(port);



				if(table_Port.size()==0)
				{
					r=i;
					break;
				}
			}
			Table_Port port = new Table_Port();
			port.setPort_type(Table_Port.TYPE_PARENT);
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
				this.updatePortTable();
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
			selectedindex = table.getSelectedRow();
			selectedcolIndex = table.getSelectedColumn();

			try{
				Object indexVal =tblPortList.getValueAt(selectedindex, PORT_INDEX_COLUM);
				if(indexVal==null)
					return;
				if(indexVal instanceof Integer)
				{
					selectedportindex=(Integer)indexVal; 
				}else
				{
					selectedportindex=Integer.valueOf(String.valueOf(indexVal));
				}

			}catch (Exception ee) {
				selectedportindex=0;
				ee.printStackTrace();
			}
			if(e.getKeyCode()==KeyEvent.VK_DELETE)
			{		
				delAction();
				return;

			}
			if(selectedcolIndex==PORT_NAME_COLUM&&e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				autoWritePort(e);
				/*try {

					//updatePortTable();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
			}



		}	
	}
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

					selectedindex=port_index;

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


}
