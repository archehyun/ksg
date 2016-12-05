package com.ksg.view.search.dialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.dao.DAOManager;
import com.ksg.domain.Table_Port;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.util.ViewUtil;

public class ManageTablePortDialog extends KSGDialog implements ActionListener 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable tblPortList;
	private String table_id;
	public ManageTablePortDialog(String table_id) {
		this.table_id = table_id;
		DAOManager manager = DAOManager.getInstance();
		tableService = manager.createTableService();
		baseService  = manager.createBaseService();
	}


	public void createAndUpdateUI() 
	{
		try {
			setTitle(this.table_id+"테이블 항구 관리");
			setModal(true);
			JPanel pnMain = new JPanel(new BorderLayout());
			tblPortList = new JTable();
			pnMain.add(new JScrollPane(tblPortList));
			
			this.getContentPane().add(pnMain);
			getContentPane().add(KSGDialog.createMargin(),BorderLayout.WEST);
			getContentPane().add(KSGDialog.createMargin(),BorderLayout.EAST);
			getContentPane().add(buildControl(),BorderLayout.SOUTH);

			updatePortTable();
			

			setSize(600,450);
			ViewUtil.center(this, false);
			setVisible(true);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "항구정보를 불러올수 없습니다.\n\n"+e.getMessage());
			e.printStackTrace();
		}

	}

	private Component buildControl() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JPanel pnRight = new JPanel();
		pnRight.setLayout( new FlowLayout(FlowLayout.RIGHT));
		
		JPanel pnLeft = new JPanel();		
		pnLeft.setLayout( new FlowLayout(FlowLayout.LEFT));
		
		JButton butDel = new JButton("적용");

		butDel.addActionListener(this);

		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(this);
		pnRight.add(butDel);
		pnRight.add(butCancel);

		pnMain.add(pnLeft,BorderLayout.WEST);
		pnMain.add(pnRight,BorderLayout.EAST);

		return pnMain;
	}


	private void updatePortTable() throws SQLException 
	{
		List<Table_Port> portli=tableService.getParentPortList(this.table_id);
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("순서");
		model.addColumn("항구명");
		model.addColumn("구분");

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
			model.setValueAt(port.getPort_index(), i, 0);
			model.setValueAt(port.getPort_name(), i, 1);
			model.setValueAt("일반", i, 2);
			
		}

		tblPortList.setModel(model);
		TableColumnModel colModel=tblPortList.getColumnModel();
		TableColumn col=colModel.getColumn(0);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(50);
		
		col.setCellRenderer(renderer);
		TableColumn col2=colModel.getColumn(2);
		DefaultTableCellRenderer renderer2 = new DefaultTableCellRenderer();
		renderer2.setHorizontalAlignment(SwingConstants.CENTER);
		col2.setMaxWidth(50);		
		col2.setCellRenderer(renderer2);		

	}


	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("적용"))
		{
			
		}else if(command.equals("취소"))
		{
			this.OPTION = ManagePortDialog.CANCEL_OPTION;
			this.setVisible(false);
			dispose();
		}
		
	}


}
