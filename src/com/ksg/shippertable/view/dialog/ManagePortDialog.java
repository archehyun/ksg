package com.ksg.shippertable.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import com.ksg.common.util.ViewUtil;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.common.view.dialog.PortSearchDialog;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.schedule.logic.PortNullException;
import com.ksg.shippertable.service.impl.TableServiceImpl;
import com.ksg.shippertable.view.ShipperTableMgtUI;

/**
 * 
 * 코리아쉬핑가제트 항구정보 관리 다이어그램
 * <pre>
 * com.ksg.shippertabe.view.dialog
 *     |_ManagePortdialog.java     
 * </pre>
 * 
 * @date: 2019. 08. 17
 * @version: 1.0
 * @author: 박창현
 * @explanation 항구 정보를 등록/ 삭제 수정 하는 창을 표시
 * 
 * 1. 항구를 추가하면 순번이 자동생성된다.
 * 2. 항구를 삭제하면 순번이 자동 업데이트 된다.
 * 3. 항구를 올리면 순번이 자동 업데이트 된다.
 * 4. 항구를 내리면 순번이 자동 업데이트 된다.
 * 5. 저장 버튼을 눌러야 실제 테이블에 반영된다. * 	
 * 
 */
@SuppressWarnings("serial")
public class ManagePortDialog extends KSGDialog implements ActionListener{

	private static final String BUT_ACTION_INSERT = "추가";

	private static final String BUT_ACTION_SAVE = "저장";

	private static final String BUT_ACTION_DELETE = "삭제";

	private static final String BUT_ACTION_SEARCH = "조회";

	private static final String ACTION_COMMAND_DOWN = "▼";

	private static final String ACTION_COMMAND_UP = "▲";

	private static final String ACTION_COMMAND_UPDATE = "수정";

	private static final int PORT_NAME_COLUM = 1;

	private static final int PORT_INDEX_COLUM = 0;

	public static int UPDATE_OPTION=1;

	public static int CANCEL_OPTION=0;

	private String table_id; // 선택된 테이블 아이디

	private PortListTable tblPortList; // 항목 목록 테이블

	private ShipperTableMgtUI base;

	private JCheckBox cbxDeleteAll;

	private JPanel pnRightOption, pnLeftOption;

	PortIndexController portIndexController;

	private JTextField 	txfIndex; 	// 항구 인덱스 표시
	
	private JTextField 	txfPortName;//항구명을 추가하기 위한 텍스트 필드
	
	private JTextField 	txflblIndex;
	
	private JTextField 	txflblPortName;
	
	private JTextField 	txfUpdatePortName;
	/**
	 * @param table_id 테이블 아이디
	 * @param baseUI
	 */
	public ManagePortDialog(String table_id,ShipperTableMgtUI baseUI) 
	{	
		super();

		this.base =baseUI;

		this.table_id=table_id;

		tableService = new TableServiceImpl();

		portIndexController = new PortIndexController();

	}
	public void createAndUpdateUI() {


		try {
			setTitle(this.table_id+"테이블 항구 관리");

			setModal(true);

			getContentPane().add(buildCenter());
			
			getContentPane().add(buildControl(),BorderLayout.SOUTH);
			
			getContentPane().add(buildInfo(),BorderLayout.NORTH);

			this.pack();
			
			this.setResizable(false);

			ViewUtil.center(this, false);
			
			tblPortList.retrive();

		} catch (SQLException e) {
			
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

		setVisible(true);
	}
	
	
	/**
	 * 화면 구성
	 * @return
	 */
	private Component buildCenterNorthControl()
	{
		
		JPanel pnMain = new JPanel(new BorderLayout());

		txfIndex = new JTextField(3);
		
		txfIndex.setEditable(false);
		
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

			public void focusLost(FocusEvent arg0){}
		});

		txfIndex.addKeyListener(new KeyAdapter()
		{	
			public void keyPressed(KeyEvent arg0) 
			{
				if(arg0.getKeyCode()==KeyEvent.VK_UP||arg0.getKeyCode()==KeyEvent.VK_DOWN)
				{
					int selectRow = tblPortList.getSelectedRow();

					if(selectRow<0) {
						selectRow=0;}
					else {
						tblPortList.changeSelection(selectRow++, 0, false, false);
						TablePort seletedPort = tblPortList.getSelectedPort();
						if(seletedPort!=null)
						{	
							txfUpdatePortName.setText(seletedPort.getPort_name());
							txfIndex.setText(String.valueOf(seletedPort.getPort_index()));

						}
					}
				}
			}
		});
		/**
		 * 항구명을 선택하면 업데이트 된다.
		 */

		txfUpdatePortName = new JTextField(15);

		txfUpdatePortName.addKeyListener(new KeyAdapter(){


			public void keyReleased(KeyEvent arg0) {
				super.keyReleased(arg0);
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

					tblPortList.changeSelection(tblPortList.selectedindex, PORT_INDEX_COLUM, false, false);
				}
				else if(arg0.getKeyCode()==KeyEvent.VK_ENTER)
				{
					JTextField txf = (JTextField) arg0.getSource();
					String searchedPortName = txf.getText();
					if(searchedPortName.length()>0)
					{
						List<String> li=null;
						try {
							li=	baseService.getPortListByPatten(String.valueOf(searchedPortName));
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(ManagePortDialog.this, e1.getMessage());
						}
						if(li.size()==1)
						{
							txf.setText(li.get(0));
						}
						else if(li.size()>1)
						{

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

					}
				}
			}

			public void keyPressed(KeyEvent arg0) 
			{
				JTextField txf = (JTextField) arg0.getSource();
			}

		});

		txfUpdatePortName.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent arg0) 
			{	
				int row=tblPortList.getSelectedRow()-1;
				if(row==-1)
					return;
			}

			public void focusLost(FocusEvent arg0){}
		});

		

		/*
		 * 20181228 기능 추가
		 * 버튼으로 항구의 위아래 순서를 변경하는 기능
		 */
		JPanel pnLeftNorthControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton butUpdate = new JButton(ACTION_COMMAND_UPDATE);
		
		butUpdate.addActionListener(portIndexController);
		
		JButton butUp=new JButton(ACTION_COMMAND_UP);
		
		butUp.addActionListener(portIndexController);
		
		JButton butDown=new JButton(ACTION_COMMAND_DOWN);
		
		butDown.addActionListener(portIndexController);
		
		pnLeftNorthControl.add(new JLabel("순서 변경"));
		
		pnLeftNorthControl.add(butUp);
		
		pnLeftNorthControl.add(butDown);		
		
		JPanel pnMainCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		FlowLayout out=(FlowLayout) pnMainCenter.getLayout();
		
		out.setHgap(5);
		
		pnMainCenter.add(txfIndex);
		
		pnMainCenter.add(txfUpdatePortName);
		pnMainCenter.add(butUpdate);
		
		pnMain.add(pnMainCenter);
		pnMain.add(pnLeftNorthControl,BorderLayout.EAST);

		return pnMain;
	}

	/**	
	 * @return
	 * @throws SQLException 
	 */
	private Component buildCenter() {
		JPanel pnMain = new JPanel(new BorderLayout());
		pnMain.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10));

		JPanel pnPortMain = new JPanel();
		GridLayout gridLayout = new GridLayout(1,0);
		gridLayout.setHgap(5);
		pnPortMain.setLayout(gridLayout);

		tblPortList = new PortListTable(this.table_id);

		tblPortList.addMouseListener(new MyMouseAdapter());

		tblPortList.addKeyListener(new MyKeyAdapter());

		JPanel pnLeft = new JPanel();
		pnLeft.setLayout(new BorderLayout());

		JLabel lbl2 = new JLabel("항구명: ");

		JButton butAdd = new JButton("추가(A)");
		butAdd.setMnemonic(KeyEvent.VK_A);
		butAdd.setActionCommand(BUT_ACTION_INSERT);
		butAdd.addActionListener(this);
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
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(ManagePortDialog.this, e1.getMessage());
						}
					}
				}
			}	
		});

		pnLeftOption = new JPanel(new BorderLayout());
		pnLeftOption.add(lbl2,BorderLayout.WEST);
		pnLeftOption.add(txfPortName);
		pnLeftOption.add(butAdd,BorderLayout.EAST);
		pnLeftOption.setBorder(new EmptyBorder(4, 0, 4, 0));

		pnLeft.add(new JScrollPane(tblPortList));		

		pnLeft.add(buildCenterNorthControl(),BorderLayout.NORTH);


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

		return pnMain;
	}


	/**
	 * 화면 구성
	 * 
	 * @return
	 */
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

	/**
	 * @return
	 */
	private Component buildControl() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JPanel pnRight = new JPanel();
		pnRight.setLayout( new FlowLayout(FlowLayout.RIGHT));
		JPanel pnLeft = new JPanel();

		JButton butArrange = new JButton(BUT_ACTION_SEARCH);
		butArrange.addActionListener(this);


		pnLeft.setLayout( new FlowLayout(FlowLayout.LEFT));
		JButton butDel = new JButton(BUT_ACTION_DELETE);	

		butDel.addActionListener(this);
		JButton butSave = new JButton("저장 및 닫기");
		butSave.setActionCommand(BUT_ACTION_SAVE);
		butSave.setMnemonic(KeyEvent.VK_S);
		butSave.addActionListener(this);

		pnRight.add(butArrange);
		pnRight.add(butDel);
		pnRight.add(butSave);

		pnMain.add(pnLeft,BorderLayout.WEST);
		pnMain.add(pnRight,BorderLayout.EAST);

		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		try {
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
								JOptionPane.showMessageDialog(ManagePortDialog.this, " 동일한 인덱스("+index1+")가 존재합니다.");
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
				}			
				this.OPTION = ManagePortDialog.UPDATE_OPTION;
				this.setVisible(false);
				dispose();
			}

			else if(command.equals(BUT_ACTION_SAVE))
			{	
				//TODO 저장시 항구 정보 유무 확인
				
				for(int i=0;i<tblPortList.getRowCount();i++)
				{
					String portName=(String) tblPortList.getValueAt(i, PORT_NAME_COLUM);
					
					/* 
					 * 
					 * 내용 : Port name validation 
					 * date :2020.08.04
					*/	
					
					try {
						PortInfo port = baseService.getPortInfo(portName);
						if(port==null)
						{
							JOptionPane.showMessageDialog(ManagePortDialog.this, "Null Port : "+portName);
							return;
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
				}
				
				
				tblPortList.save();
				
				updateTableInfo();
				
				this.OPTION = ManagePortDialog.CANCEL_OPTION;
				
				this.setVisible(false);
				
				dispose();
				
				base.searchADVTable();

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
				tblPortList.save();
				updateTableInfo();
				this.OPTION = ManagePortDialog.CANCEL_OPTION;
				this.setVisible(false);
				dispose();
				base.searchADVTable();

			}else if(command.equals(BUT_ACTION_DELETE))
			{
				int selectedrow = tblPortList.getSelectedRow();

				TablePort selectedPort = tblPortList.getSelectedPort();

				if(selectedPort==null)
					return;

				tblPortList.deletePort(selectedPort);				
				tblPortList.changeSelection(selectedrow-1, 0, false, false);


			}
			else if(command.equals("검색"))
			{
				if(tblPortList==null)
					return;

				int row=tblPortList.getSelectedRow();

				if(row==-1)
					return;


				PortSearchDialog dialog = new PortSearchDialog(this);
				dialog.setRow(row);
				dialog.createAndUpdateUI();
				if(dialog.portName!=null)
				{
					tblPortList.setValueAt(dialog.portName, row, PORT_NAME_COLUM);
				}
			}
			else if(command.equals(BUT_ACTION_SEARCH))
			{
				tblPortList.retrive();
			}
			else if(command.equals(BUT_ACTION_INSERT))
			{
				try 
				{
					String port_name = txfPortName.getText();

					tblPortList.insertPortName(port_name);
					
					txfPortName.setText("");
				}

				catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(ManagePortDialog.this, "숫자를 입력하십시요");
					txfIndex.setText("");
					return;
				}catch(PortNullException e2)
				{
					JOptionPane.showMessageDialog(ManagePortDialog.this, e2.getPortName()+ ":등록되지 않은 항구입니다.");
				}
			}


		} catch (SQLException e1) {

			JOptionPane.showMessageDialog(this, e1.getMessage());
			e1.printStackTrace();
		}

	}


	/**
	 * <pre>
	 * 1. 개요 : 항구 정보 변경 컨트롤러
	 * 2. 처리 내용:
	 *    - 항구 선택(row=n)
	 *    - up 버튼 선택
	 *    - 항구 위로 이동(row=n-1)
	 *    - 인덱스 변경
	 * </pre>
	 * @date: 2019. 08. 17
	 * @author 박창현
	 * @history:
	 * ---------------------------------------
	 * 변경일                    작성자         변경내용
	 * ---------------------------------------
	 * 2018. 08. 17  박창현         최조 작성
	 * @param 
	 * 
	 * 
	 */

	class PortIndexController implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedRow =tblPortList.getSelectedRow();

			if(selectedRow==-1)
				return;

			String command = e.getActionCommand();
			
			if(command.equals(ACTION_COMMAND_UP))
			{	
				if(selectedRow==0)
					return;

				tblPortList.movePort(selectedRow,selectedRow-1);
				
				tblPortList.changeSelection(selectedRow-1, 0, false, false);

			}
			else if(command.equals(ACTION_COMMAND_DOWN))
			{
				if(selectedRow==tblPortList.getPortListSize()-1)
				{	
					return; 
				}
				tblPortList.movePort(selectedRow,selectedRow+1);

				tblPortList.changeSelection(selectedRow+1, 0, false, false);
				
			}else if(command.equals(ACTION_COMMAND_UPDATE))
			{
				TablePort selectedPort = tblPortList.getSelectedPort();				
				String portName=txfUpdatePortName.getText();
				
				/* 
				 * 
				 * 내용 : Port name validation 
				 * date :2020.08.04
				 * 
				 * 
				 * 
				 * 
				*/
				try {
					PortInfo port = baseService.getPortInfo(portName);
					if(port==null)
					{
						
						/*
						 * 내용 : 예외 항수 추가 프로세스
						 * date : 2021.01.22
						 */
						if(port==null)
						{
							Code code_info = new Code();
							code_info.setCode_name(portName);
							baseService = new BaseServiceImpl();
							Code templi=	baseService.getCodeInfo(code_info);
							if(templi==null)
							{
								JOptionPane.showMessageDialog(ManagePortDialog.this, "Null Port : "+portName);
								return;
							}
						}
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
				
				
				selectedPort.setPort_name(portName);
				tblPortList.updateUI();
			}
		}
	}


	/**
	 * @throws SQLException
	 */
	private void updateTableInfo() throws SQLException {


		int count=tblPortList.getPortListSize();
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

			updatePointIndex( row);

			break;

		case PORT_NAME_COLUM:

			addNewPort( col, row);

			break;

		default:
			break;
		}
	}
	/**
	 * @param col
	 * @param row
	 */
	private void addNewPort(int col, int row) {
		Object val = tblPortList.getValueAt(row, PORT_NAME_COLUM);
		if(val==null)
		{
			logger.debug("val is null");
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
	/**
	 * @param row
	 */
	private void updatePointIndex(int row) {


		Object val = tblPortList.getValueAt(row, PORT_INDEX_COLUM);
		Object port_name = tblPortList.getValueAt(row,PORT_NAME_COLUM);


		if(val==null||port_name==null)
		{
			tblPortList.setValueAt(null, row, PORT_INDEX_COLUM);
			tblPortList.setValueAt(null, row, PORT_NAME_COLUM);
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
	/**
	 * @return
	 */
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
	/**
	 * @param table
	 * @param row
	 * @param port_name
	 */
	private void addPort(final JTable table, int row, String port_name) {
		logger.info("add port:"+port_name);
		TablePort newPort = new TablePort();
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

			newPort.setPort_type(TablePort.TYPE_PARENT);
			newPort.setPort_name(port_name);
			newPort.setParent_port(port_name);
			newPort.setTable_id(this.table_id);
			if(r!=0)
			{
				newPort.setPort_index(r);
			}
			else
			{
				newPort.setPort_index(max+1);
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			Object obj=table.getValueAt(row, PORT_INDEX_COLUM);
			if(obj==null)
			{
				tableService.insertPortList(newPort);
			}else
			{
				newPort.setPort_index(Integer.valueOf(obj.toString()));
				tableService.updateTablePort(newPort);
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
	}

	/**
	 * @author 박창현
	 *
	 */
	class MyKeyAdapter extends KeyAdapter
	{

		StringBuffer buffer;
		public void keyPressed(KeyEvent e) {
			final JTable table = (JTable) e.getSource();
			if(table.getSelectedRow()==-1)
				return;

			if(e.getKeyCode()==KeyEvent.VK_F2)
			{
				buffer =new StringBuffer();
				tblPortList.setEdit(true);
			}
		}
		public void keyReleased(KeyEvent e) 
		{
			final JTable table = (JTable) e.getSource();


			if(table.getSelectedRow()==-1)
			{
				return;
			}

			if(e.getKeyCode()==KeyEvent.VK_UP||e.getKeyCode()==KeyEvent.VK_DOWN)
			{
				TablePort seletedPort = tblPortList.getSelectedPort();
				txfIndex.setText(Integer.toString(seletedPort.getPort_index()));
				txfUpdatePortName.setText(seletedPort.getPort_name());
			}
			
			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				int col = tblPortList.getSelectedColumn();
				/*int row = tblPortList.getSelectedRow();
				TablePort selectedPort = tblPortList.getSelectedPort();				

				int portIndex = selectedPort.getPort_index();*/

				switch (col) {
				case PORT_INDEX_COLUM:
					
				
					break;
				case PORT_NAME_COLUM:

					break;

				default:
					break;
				}
			}
			tblPortList.setEdit(false);
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

			//if(tblPortList.equals(tblPortList))
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

	/**
	 * @author 박창현
	 *
	 */
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
}
