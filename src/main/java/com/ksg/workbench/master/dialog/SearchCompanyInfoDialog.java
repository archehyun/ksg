package com.ksg.workbench.master.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.dtp.api.control.CompanyController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;

import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.button.KSGGradientButton;
import com.ksg.workbench.common.comp.panel.KSGPanel;

import mycomp.comp.MyTable;

/**@설명 선사 검색 화면
 * 
 * @author 박창현
 *	
 */
public class SearchCompanyInfoDialog extends BaseInfoDialog {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField txfCompanyName;
	
	private MyTable tableH;
	
	public SearchCompanyInfoDialog(Dialog dialog,JTextField textField) {
		
		super(dialog);
		
		this.setController(new CompanyController());
		
		txfCompanyName =textField;
		
	}

	@Override
	public void createAndUpdateUI() {
		
		this.setTitle("선사명 조회");
		
		KSGPanel pnSearch = new KSGPanel(new BorderLayout());
		
		pnSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		JLabel lblSearch = new JLabel("선사명: ");
		
		final JTextField txfSearch = new JTextField(10);
		
		txfSearch.addKeyListener(new KeyAdapter(){
	
			public void keyReleased(KeyEvent e) {
				
				String strParam = txfSearch.getText();
				
				CommandMap param = new CommandMap();
				
				param.put("company_name", strParam);
				
				callApi("selectCompany", param);

			}
	
			});
		
		pnSearch.add(lblSearch,BorderLayout.WEST);
		
		pnSearch.add(txfSearch);
		
		KSGPanel pnControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton butOk = new KSGGradientButton("확인");
		
		butOk.addActionListener(new ActionListener(){
	
			public void actionPerformed(ActionEvent e) {
				
				int result=tableH.getSelectedRow();
				if(result==-1)
				{
					JOptionPane.showMessageDialog(SearchCompanyInfoDialog.this, "선택된 선사명이 없습니다.");
				}else
				{
					String company_abbr=(String) tableH.getValueAt(result, 1);
					String agent_abbr=(String) tableH.getValueAt(result, 3);
					txfCompanyName.setText(company_abbr.compareToIgnoreCase(agent_abbr)==0?company_abbr:company_abbr+"/"+agent_abbr);
					txfCompanyName.setText(company_abbr);
					setVisible(false);
					dispose();
				}
				
			}});
		
		JButton butCancel = new KSGGradientButton("취소");
		
		
		
		butCancel.addActionListener(new ActionListener(){
	
			public void actionPerformed(ActionEvent e) {
				txfCompanyName.setText("");
				setVisible(false);
				dispose();
			}});
		
		pnControl.add(butOk);
		
		pnControl.add(butCancel);
		
		getContentPane().add(pnSearch,BorderLayout.NORTH);
		
		getContentPane().add(buildCenter(),BorderLayout.CENTER);
		
		getContentPane().add(pnControl,BorderLayout.SOUTH);
		
		pack();
		
		ViewUtil.center(this, true);
		
		setVisible(true);
	}
	
	public JComponent buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		tableH = new KSGAbstractTable();
		
		tableH.addColumn(new KSGTableColumn("company_name", "선사명"));
		
		tableH.addColumn(new KSGTableColumn("company_abbr", "선사명약어"));
		
		tableH.addColumn(new KSGTableColumn("agent_name", "에이전트"));
		
		tableH.addColumn(new KSGTableColumn("agent_abbr", "에이전트약어"));
		
		tableH.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		tableH.initComp();
		
		pnMain.add(new JScrollPane(tableH));
		
		tableH.getParent().setBackground(Color.white);
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		return pnMain;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		int result=tableH.getSelectedRow();
		if(result==-1)
		{
			JOptionPane.showMessageDialog(SearchCompanyInfoDialog.this, "선택된 선사명이 없습니다.");
		}else
		{
			String company_abbr=(String) tableH.getValueAt(result, 1);
			String agent_abbr=(String) tableH.getValueAt(result, 3);
			txfCompanyName.setText(company_abbr.compareToIgnoreCase(agent_abbr)==0?company_abbr:company_abbr+"/"+agent_abbr);
			txfCompanyName.setText(company_abbr);
			setVisible(false);
			dispose();
		}
	}
	
	@Override
	public void updateView() {
		CommandMap resultMap= this.getModel();

		boolean success = (boolean) resultMap.get("success");

		if(success)
		{
			String serviceId=(String) resultMap.get("serviceId");

			if("selectCompany".equals(serviceId))
			{	
				List data = (List )resultMap.get("data");

				tableH.setResultData(data);
			}

		}
		else{  
			String error = (String) resultMap.get("error");

			JOptionPane.showMessageDialog(this, error);
		}
	}


}