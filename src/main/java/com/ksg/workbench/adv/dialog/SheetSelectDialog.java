package com.ksg.workbench.adv.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.workbench.adv.comp.SheetModel;

@SuppressWarnings("serial")
public class SheetSelectDialog extends KSGDialog {
	ActionEvent e;
	private JTable _tblSheetNameList;
	private Font defaultFont;
	
	public int result=0;
	public SheetSelectDialog(JTable _tblSheetNameList) {
		
		this._tblSheetNameList=_tblSheetNameList;
	}
	public void createAndUpdateUI() {
		
		result = 0;
		defaultFont= KSGModelManager.getInstance().defaultFont;
		
		this.setTitle("Sheet 선택");
		this.setModal(true);

		JPanel pnControl = new JPanel();
		pnControl.setLayout(new BorderLayout());

		JButton butOK = new JButton("확인");
		butOK.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				SheetSelectDialog.this.setVisible(false);
				SheetSelectDialog.this.dispose();
				result = 1;

			}});
		JButton butUp = new JButton("▲");
		butUp.setFont(defaultFont);
		butUp.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int row=_tblSheetNameList.getSelectedRow();
				int col=_tblSheetNameList.getSelectedColumn();
				if(row==-1)
					return ;
				SheetModel model = (SheetModel) _tblSheetNameList.getModel();
				model.upRow(row);
				if(row!=0)
					_tblSheetNameList.changeSelection(row-1, col, false, false);
					_tblSheetNameList.updateUI();
				
				
			}});
		
		JButton butDown = new JButton("▼");
		butDown.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				int row=_tblSheetNameList.getSelectedRow();
				int col=_tblSheetNameList.getSelectedColumn();
				if(row==-1)
					return ;
				SheetModel model = (SheetModel) _tblSheetNameList.getModel();
				model.downRow(row);
				
				if(row<_tblSheetNameList.getRowCount()-1)
				_tblSheetNameList.changeSelection(row+1, col, false, false);
				_tblSheetNameList.updateUI();
			}});
		butDown.setFont(defaultFont);
		JPanel pnRight = new JPanel();
		pnRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnRight.add(butUp);
		pnRight.add(butDown);
		pnRight.add(butOK);
		pnControl.add(pnRight,BorderLayout.EAST);
		JCheckBox cbxtotal = new JCheckBox("전체선택");
		cbxtotal.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				System.out.println(e.getStateChange());
				switch (e.getStateChange()) {
				case ItemEvent.DESELECTED:
					int row =_tblSheetNameList.getRowCount();
					for(int i=0;i<row;i++)
					{
						Boolean use=(Boolean) _tblSheetNameList.getValueAt(i, 3);
						
						_tblSheetNameList.setValueAt(false, i, 3);
					}
					break;
				case ItemEvent.SELECTED:
					int row2 =_tblSheetNameList.getRowCount();
					for(int i=0;i<row2;i++)
					{
						Boolean use=(Boolean) _tblSheetNameList.getValueAt(i, 3);
						
						_tblSheetNameList.setValueAt(true, i, 3);
					}
					break;
				default:
					break;
				}
				_tblSheetNameList.updateUI();

			}

		});
		pnControl.add(cbxtotal,BorderLayout.WEST);
		this.add(new JScrollPane(_tblSheetNameList));
		this.add(pnControl,BorderLayout.SOUTH);
		this.add(this.buildTitle(),BorderLayout.NORTH);
		this.add(KSGDialog.createMargin(),BorderLayout.WEST);
		this.add(KSGDialog.createMargin(),BorderLayout.EAST);
		this.setSize(400, 300);
		ViewUtil.center(this, false);
		this.setVisible(true);
		
	}
	private Component buildTitle() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblTitle1 = new JLabel("불러올 엑셀 Sheet를 선택하십시요\n");
		panel.add(lblTitle1);
		panel.setBackground(Color.white);
		
		return panel;
	}

}
