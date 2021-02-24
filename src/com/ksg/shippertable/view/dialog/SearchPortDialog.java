package com.ksg.shippertable.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.view.comp.KSGDialog;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Code;

/**
 * 
 * 항구 조회 다이어그램
 * @author 박창현
 *
 */
public class SearchPortDialog extends KSGDialog{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String result;
	private List<String> portli;
	private JTable tblNomal,tblExternal,tblCurrent;
	private String val;
	private List<Code> exportli;
	ManagePortDialog main;
	public SearchPortDialog(ManagePortDialog main, List<String> portli) {
		super(main);
		this.main=main;
		this.portli= portli;
		try {
			Code code_info = new Code();
			code_info.setCode_type("port_exception");
			baseService = new BaseServiceImpl();
			exportli=	baseService.getCodeInfoList(code_info);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createAndUpdateUI()
	{
		this.setTitle("항구 선택");
		this.setModal(true);


		tblNomal = new JTable();
		tblCurrent = tblNomal;
		tblNomal.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()>1)
				{
					int ro=tblCurrent.getSelectedRow();
					int col =tblCurrent.getSelectedColumn();
					if(ro==-1)
					{
						return;
					}
					result= String.valueOf(tblCurrent.getValueAt(ro, col));
					close();
				}
			}
		});
		tblNomal.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {}

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					int ro=tblCurrent.getSelectedRow();
					int col =tblCurrent.getSelectedColumn();
					if(ro==-1)
					{
						return;
					}
					result= String.valueOf(tblCurrent.getValueAt(ro, col));
					close();
				}
			}
		});

		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		model.addColumn("항구 명");

		for(int i=0;i<portli.size();i++)
		{
			model.addRow(new Object[]{portli.get(i)});
		}
		tblNomal.setModel(model);


		tblExternal =  new JTable();
		tblExternal.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()>1)
				{
					int ro=tblCurrent.getSelectedRow();
					int col =tblCurrent.getSelectedColumn();
					if(ro==-1)
					{
						return;
					}
					result= String.valueOf(tblCurrent.getValueAt(ro, col));
					close();
				}
			}
		});
		tblExternal.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {}

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					int ro=tblCurrent.getSelectedRow();
					int col =tblCurrent.getSelectedColumn();
					if(ro==-1)
					{
						return;
					}
					result= String.valueOf(tblCurrent.getValueAt(ro, col));
					close();
				}
			}
		});

		DefaultTableModel model2 = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		model2.addColumn("예외 항구 명");

		for(int i=0;i<exportli.size();i++)
		{
			model2.addRow(new Object[]{exportli.get(i)});
		}
		tblExternal.setModel(model2);


		JPanel pnContorl = new JPanel();
		JButton butOk = new JButton("확인");
		butOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int ro=tblCurrent.getSelectedRow();
				int col =tblCurrent.getSelectedColumn();
				if(ro==-1)
				{
					JOptionPane.showMessageDialog(null, "선택된 항구명이 없습니다.");
					return;
				}
				result= String.valueOf(tblCurrent.getValueAt(ro, col));
				close();
			}
		});
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				close();
			}
		});
		pnContorl.add(butOk);
		pnContorl.add(butCancel);

		JTabbedPane pane = new JTabbedPane();
		pane.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) 
			{
				JTabbedPane tp = (JTabbedPane) e.getSource();
				int index=tp.getSelectedIndex();
				switch (index) {
				case 0:
					tblCurrent = tblNomal;
					break;
				case 1:
					tblCurrent = tblExternal;
					break;

				default:
					break;
				}

			}});
		pane.addTab("일반", new JScrollPane(tblNomal));
		pane.addTab("예외", new JScrollPane(tblExternal));

		this.getContentPane().add(pnContorl,BorderLayout.SOUTH);
		this.getContentPane().add(pane);

		int x=main.getX()+main.getWidth();
		int y=main.getY();
		int h=main.getHeight();
		this.setSize(new Dimension(300,h));
		this.setLocation(x, y);
		this.setVisible(true);
	}
	public void close()
	{
		this.setVisible(false);
		this.dispose();
	}

}
