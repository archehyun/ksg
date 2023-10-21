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

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.adv.comp.SheetModel;

@SuppressWarnings("serial")
public class SheetSelectDialog extends KSGDialog implements ActionListener{
	ActionEvent e;
	private JTable _tblSheetNameList;
	private Font defaultFont;
	DirectionAaction  directionAction = new DirectionAaction();
	public int result=0;
	public SheetSelectDialog(JTable _tblSheetNameList) {
		
		this._tblSheetNameList=_tblSheetNameList;
	}
	public void createAndUpdateUI() {
		
		result = 0;
		defaultFont= KSGModelManager.getInstance().defaultFont;
		
		this.setTitle("Sheet 선택");
		this.setModal(true);

		
		this.add(buildCenter());
		this.add(buildControl(),BorderLayout.SOUTH);
		this.add(this.buildTitle(),BorderLayout.NORTH);
		

		this.setSize(400, 300);
		ViewUtil.center(this, false);
		this.setVisible(true);
		
	}
	
	private KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		pnMain.add(new JScrollPane(_tblSheetNameList));
		_tblSheetNameList.getParent().setBackground(Color.white);
		return pnMain;
	}
	
	private KSGPanel buildControl()
	{
		KSGPanel pnControl = new KSGPanel(new BorderLayout());
		JButton butOK = new JButton("확인");
		butOK.addActionListener(this);
		JButton butUp = new JButton("▲");
		butUp.setFont(defaultFont);
		butUp.addActionListener(directionAction);
		
		JButton butDown = new JButton("▼");
		butDown.addActionListener(directionAction);
		butDown.setFont(defaultFont);
		KSGPanel pnRight = new KSGPanel();
		pnRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnRight.add(butUp);
		pnRight.add(butDown);
		pnRight.add(butOK);
		pnControl.add(pnRight,BorderLayout.EAST);
		JCheckBox cbxtotal = new JCheckBox("전체선택");
		cbxtotal.setBackground(Color.white);
		cbxtotal.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				int row =_tblSheetNameList.getRowCount();
				
				for(int i=0;i<row;i++)
				{
					Boolean use=(Boolean) _tblSheetNameList.getValueAt(i, 3);
					
					_tblSheetNameList.setValueAt(e.getStateChange()==ItemEvent.SELECTED?true:false, i, 3);
				}

				_tblSheetNameList.updateUI();

			}

		});
		pnControl.add(cbxtotal,BorderLayout.WEST);

		return pnControl;
		
	}
	private Component buildTitle() {
		KSGPanel panel = new KSGPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblTitle1 = new JLabel("불러올 엑셀 Sheet를 선택하십시요\n");
		panel.add(lblTitle1);
		panel.setBackground(Color.white);
		
		return panel;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if("확인".equals(command))
		{
			SheetSelectDialog.this.setVisible(false);
			SheetSelectDialog.this.dispose();
			result = 1;
		}
		
	}
	class DirectionAaction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			int row=_tblSheetNameList.getSelectedRow();
			
			int col=_tblSheetNameList.getSelectedColumn();
			
			if(row==-1|| col==-1) return ;
			
			SheetModel model = (SheetModel) _tblSheetNameList.getModel();
			
			String command = e.getActionCommand();
			if("▲".equals(command))
			{
				model.upRow(row);
				if(row!=0)
					_tblSheetNameList.changeSelection(row-1, col, false, false);
					_tblSheetNameList.updateUI();
			}
			else if("▼".equals(command))
			{
				
				model.downRow(row);
				
				if(row<_tblSheetNameList.getRowCount()-1)
				_tblSheetNameList.changeSelection(row+1, col, false, false);
				_tblSheetNameList.updateUI();
			}
		}
	}
}
