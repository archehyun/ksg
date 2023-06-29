package com.ksg.workbench.common.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.master.dialog.BaseInfoDialog;


/**

  * @FileName : SearchVesselDialog.java

  * @Project : KSG2

  * @Date : 2022. 11. 22. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 선박명 검색 화면

  */
@SuppressWarnings("serial")
public class SearchVesselDialog extends BaseInfoDialog{
	
	public String result=null;
	
	public String resultAbbr=null;

	private List<HashMap<String, Object>> vesselli;
	
	private JTable vesselTable;// 검색된 선박 목룍 표시
	
	private JTable vesselDetail;// 검색된 선박 목룍 표시
	
	private JButton butOk;

	private String searchText;

	private JTextField txf;
	
	public SearchVesselDialog(String searchText, List vesselList) 
	{
		this.searchText = searchText;
		
		this.vesselli	= vesselList;
		
		this.addComponentListener(this);
	}
	
	private KSGPanel buildCenter()
	{	
		vesselTable = new JTable();
		
		vesselTable.setRowHeight(25);
		
		// 단일 선택
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
		model.addColumn("선박 명");
		
		model.addColumn("선박 명 약어");
		
		for(HashMap<String, Object> item:vesselli)
		{
			model.addRow(new Object[]{item.get("vessel_name"),item.get("vessel_abbr")});
		}

		vesselTable.setModel(model);
		
		vesselTable.changeSelection(0, 0, false, false);
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,7,5,7));
		
		pnMain.add(new JScrollPane(vesselTable));
		
		return pnMain;
		
	}

	public void createAndUpdateUI() {
		
		this.setModal(true);
		
		setTitle("선박 선택");		
		
		getContentPane().add(buildCenter());
		
		getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		getContentPane().add(buildNorth(),BorderLayout.NORTH);

		setSize(new Dimension(400,400));
		
		ViewUtil.center(this, false);
		
		this.setVisible(true);
	}

	private Component buildNorth() {
		
		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		txf = new JTextField(20);
		
		pnMain.add(txf);
		
		return pnMain;
	}

	private void setResult(final JTable jTable) {
		
		int row = jTable.getSelectedRow();
		
		int col = jTable.getSelectedColumn();
		
		if(row==-1)
		{
			JOptionPane.showMessageDialog(SearchVesselDialog.this, "선택된 선박이 없습니다.");
			
			return;
		}
		Object obj = jTable.getValueAt(row, 0);
		
		Object obj2 = jTable.getValueAt(row, 1);
		
		result = String.valueOf(obj);
		
		resultAbbr = String.valueOf(obj2).toUpperCase();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if("확인".equals(command))
		{
			setResult(vesselTable);
			
			close();
		}
		else if("취소".equals(command))
		{
			setVisible(false);
			dispose();
		}
	}
	@Override
	public void componentShown(ComponentEvent e) {
		
		txf.setText(searchText);
	}

	@Override
	public void updateView() {
		
	}

}
