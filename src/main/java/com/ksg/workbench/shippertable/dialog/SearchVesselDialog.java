package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**
 * 
 * ���ڸ� �˻� ȭ��
 * @author archehyun
 *
 */
public class SearchVesselDialog extends KSGDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String result=null;
	public String resultAbbr=null;

	private List vesselli;
	
	private JTable vesselTable;// �˻��� ���� �� ǥ��
	
	private JButton butOk;
	
	public SearchVesselDialog(List vesselli) 
	{
		this.vesselli=vesselli;
	}

	public void createAndUpdateUI() {
		this.setModal(true);
		setTitle("���� ����");
		JPanel pnMain= new JPanel();
		this.getContentPane().add(pnMain); 
		vesselTable = new JTable();
		// ���� ����
		vesselTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		vesselTable.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				
				JTable table=(JTable) e.getSource();
				
				if(e.getClickCount()>1)
				{
					setResult(table);
					close();
				}

			}
		});
		vesselTable.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				JTable table=(JTable) e.getSource();
				if(e.getKeyCode()==KeyEvent.VK_TAB)
				{					
					butOk.requestFocus();
				}
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{					
					setResult(table);
					close();
					
				}
			}

			public void keyReleased(KeyEvent e) {}

			public void keyTyped(KeyEvent e) {}
		});

		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		model.addColumn("���� ��");
		model.addColumn("���� �� ���");

		for(int i=0;i<vesselli.size();i++)
		{
			Vessel v=(Vessel) vesselli.get(i);
			model.addRow(new Object[]{v.getVessel_name(),v.getVessel_abbr()});
		}
		vesselTable.setModel(model);
		vesselTable.changeSelection(0, 0, false, false);

		getContentPane().add(new JScrollPane(vesselTable));
		JPanel pnContorl = new JPanel();
		butOk = new JButton("Ȯ��");
		butOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				setResult(vesselTable);
				
				close();
			}
		});
		JButton butCancel = new JButton("���");
		butCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});
		pnContorl.add(butOk);
		pnContorl.add(butCancel);

		getContentPane().add(pnContorl,BorderLayout.SOUTH);

		setSize(new Dimension(300,300));
		ViewUtil.center(this, false);
		this.setVisible(true);
	}
	

	private void setResult(final JTable jTable) {
		int row=jTable.getSelectedRow();
		int col =jTable.getSelectedColumn();
		if(row==-1)
		{
			JOptionPane.showMessageDialog(SearchVesselDialog.this, "���õ� ������ �����ϴ�.");
			return;
		}
		Object obj = jTable.getValueAt(row, 0);
		result = String.valueOf(obj);
		Object obj2 = jTable.getValueAt(row, 1);
		resultAbbr = String.valueOf(obj2).toUpperCase();
	}

}
