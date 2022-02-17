package com.ksg.workbench.adv.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.PortInfo;
import com.ksg.domain.TablePort;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.dialog.PortSearchDialog;

/**
 * ========================================
 * 항구정보 추가
 * @작성일자 2021-02-24
 * @version 1.0
 * @author 박창현
 * =========================================
 *
 */
@SuppressWarnings("serial")
public class AddPortDialog extends KSGDialog {
	
	private String port_name;
	
	@SuppressWarnings("rawtypes")
	private Vector portList;
	
	private String table_id;
	
	private DefaultTableModel portModel;
	
	private JTextField txfPortName;
	
	private JLabel lblTableID; // 테이블 ID
	
	@SuppressWarnings("rawtypes")
	private JComboBox cbxPort;
	
	private JTable tblPortList;
	
	private JLabel lblInfo;
	
	/**
	 * @param table_id
	 * @param portName
	 * @param portList
	 */
	public AddPortDialog(String table_id,String portName,Vector portList) {
		this.port_name=portName;
		this.portList=portList;
		this.table_id= table_id;
		DAOManager manager = DAOManager.getInstance();
		tableService = new TableServiceImpl();
		baseService  = manager.createBaseService();

	}

	public void createAndUpdateUI() {
		this.setTitle(port_name+"하위 항구 추가(작업 중)");

		this.setModal(true);
		Box pnMain = new Box(BoxLayout.Y_AXIS);

		lblTableID = new JLabel(table_id);
		pnMain.add(addComp("테이블 아이디 : ",lblTableID));

		cbxPort = new JComboBox();
		cbxPort.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox box=(JComboBox) e.getSource();
				TablePort tablePort = new TablePort();
				tablePort.setTable_id(table_id);
				tablePort.setParent_port((String) box.getSelectedItem());
				tablePort.setPort_type("C");
				try 
				{
					List li=tableService.getTablePortList(tablePort);
					if(portModel==null)
						return;
					portModel.setRowCount(0);
					Iterator iter = li.iterator();
					while(iter.hasNext())
					{
						TablePort port=(TablePort) iter.next();
						portModel.addRow(new Object[]{table_id,port.getPort_name(),port.getPort_index()});
					}

				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "error:"+e1.getMessage());
					e1.printStackTrace();
				}

			}
		});

		for(int i=0;i<portList.size();i++)
		{
			cbxPort.addItem(portList.get(i));
		}
		cbxPort.setSelectedItem(port_name);
		pnMain.add(addComp("대표 항구명 : ",cbxPort));

		portModel = new DefaultTableModel();
		portModel.addColumn("테이블 ID");
		portModel.addColumn("항구명");
		portModel.addColumn("인덱스");
		tblPortList = new JTable();
		tblPortList.setModel(portModel);

		pnMain.add(createGap());
		pnMain.add(new JScrollPane(tblPortList));
		JPanel pn1 = new JPanel();
		pn1.setLayout( new BorderLayout());

		JButton butSearch = new JButton("검색");
		butSearch.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				PortSearchDialog dialog = new PortSearchDialog(AddPortDialog.this);
				dialog.createAndUpdateUI();
				if(dialog.portName!=null)
				{
					txfPortName.setText(dialog.portName);
				}
			}});


		txfPortName = new JTextField(27)

		{
			public Point getToolTipLocation(MouseEvent event) {
				return new Point(0, getHeight());
			}

		};

		txfPortName.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) 
			{
				try 
				{
					if(txfPortName.getText().length()<1)
					{
						lblInfo.setText("");
						return;
					}
					PortInfo info=baseService.getPortInfo(txfPortName.getText());

					if(info!=null)
					{
						lblInfo.setText("항구명이 존재합니다.");
					}else
					{
						lblInfo.setText("항구명이 존재하지 않습니다.");
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}});
		pn1.add(txfPortName);
		pn1.add(butSearch,BorderLayout.EAST);

		pnMain.add(createGap());
		pnMain.add(addComp("항구명 : ",pn1));

		JPanel pnLeft = new JPanel();
		pnLeft.setPreferredSize(new Dimension(15,0));
		JPanel pnRight = new JPanel();
		pnRight.setPreferredSize(new Dimension(15,0));
		this.getContentPane().add(pnMain);
		this.getContentPane().add(pnRight,BorderLayout.EAST);
		this.getContentPane().add(pnLeft,BorderLayout.WEST);

		JPanel pnTitle = new JPanel();
		pnTitle.setPreferredSize( new Dimension(0,30));
		pnTitle.setLayout( new FlowLayout(FlowLayout.LEFT));
		JLabel lbl = new JLabel("스케줄 처리를 위한 항구를 등록하십시요");
		pnTitle.add(lbl);
		pnTitle.setBackground(Color.white);

		this.getContentPane().add(pnTitle,BorderLayout.NORTH);
		this.getContentPane().add(buildContorl(),BorderLayout.SOUTH);

		this.setSize(500,350);
		ViewUtil.center(this, false);
		this.setVisible(true);
	}

	private Component createGap() {
		JPanel pnLine = new JPanel();
		pnLine.setPreferredSize(new Dimension(0,5));
		return pnLine ;
	}

	private Component buildContorl() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout( new FlowLayout(FlowLayout.RIGHT));
		JButton pnAdd = new JButton("추가");
		pnAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if(txfPortName.getText().length()<=0)
				{
					JOptionPane.showMessageDialog(null, "항구명을 추가하십시요");
				}else
				{
					TablePort info =  new TablePort();
					info.setTable_id(lblTableID.getText());
					info.setPort_name(txfPortName.getText());
					info.setPort_index((cbxPort.getSelectedIndex()+1));
					info.setParent_port((String) cbxPort.getSelectedItem());
					info.setPort_type(TablePort.TYPE_CHAILD);
					try {
						tableService.insertPortList(info);
						updateTable();
						txfPortName.setText("");
					} catch (SQLException e) {

						if(e.getErrorCode()==2627)
						{
							JOptionPane.showMessageDialog(null, "동일한 항구명이 존재합니다.");	
						}else
						{
							JOptionPane.showMessageDialog(null,"error:"+e.getMessage());
						}


						e.printStackTrace();
					}
				}
			}
		});
		
		JButton pnDel = new JButton("삭제");
		pnDel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				int row=tblPortList.getSelectedRow();
				if(row!=-1)
				{
					String port_name=(String) tblPortList.getValueAt(row, 1);
					String table_id=(String) tblPortList.getValueAt(row, 0);
					TablePort tablePort = new TablePort();
					tablePort.setTable_id(table_id);
					tablePort.setPort_name(port_name);

					int result=JOptionPane.showConfirmDialog(null, port_name+" 항구를 삭제하시겠습니까?");
					if(result == JOptionPane.OK_OPTION)
					{
						try {
							tableService.deleteTablePort(tablePort);
							updateTable();


						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		JButton pnCancel = new JButton("닫기");
		pnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});
		JPanel pnInfo = new JPanel();
		pnInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
		lblInfo = new JLabel();
		pnInfo.add(lblInfo);
		pnMain.add(pnInfo);

		pnMain.add(pnAdd);
		pnMain.add(pnDel);
		pnMain.add(pnCancel);

		return pnMain;
	}

	private Component addComp(String string, Component comp) {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JLabel formLabel = new JLabel(string);
		formLabel.setHorizontalAlignment(JLabel.RIGHT);
		formLabel.setPreferredSize( new Dimension(80,24));
		pnMain.add(formLabel,BorderLayout.WEST);
		pnMain.add(comp,BorderLayout.CENTER);
		return pnMain;
	}

	private void updateTable() {
		try 
		{
			TablePort tablePort = new TablePort();
			tablePort.setTable_id(lblTableID.getText());
			tablePort.setParent_port((String) cbxPort.getSelectedItem());
			tablePort.setPort_type("C");

			List li=tableService.getTablePortList(tablePort);
			if(portModel==null)
				return;
			portModel.setRowCount(0);
			Iterator<TablePort> iter = li.iterator();
			while(iter.hasNext())
			{
				TablePort port=(TablePort) iter.next();
				portModel.addRow(new Object[]{table_id,port.getPort_name(),port.getPort_index()});
			}


		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}

