package com.ksg.base.view.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.util.ViewUtil;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Company;

/**@���� ���� �˻� ȭ��
 * @author ��â��
 *
 */
public class SearchCompanyInfoDialog extends KSGDialog {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTextField txfCompanyName;
	
	public SearchCompanyInfoDialog(JDialog dialog,JTextField textField) {
		super(dialog);
		
		txfCompanyName =textField;
		
		baseService = new BaseServiceImpl(); 
	}

	@Override
	public void createAndUpdateUI() {
		this.setTitle("����� ��ȸ");
		
		JPanel pnMain = new JPanel(new BorderLayout());
		final JTable table = new JTable();
		JPanel pnSearch = new JPanel(new BorderLayout());
		JLabel lblSearch = new JLabel("�����: ");
		final JTextField txfSearch = new JTextField(10);
		txfSearch.addKeyListener(new KeyAdapter(){
	
		
	
			public void keyReleased(KeyEvent e) {
				try {
					List li = baseService.getSearchedCompanyList("company_name like '%"+txfSearch.getText()+"%'");
					DefaultTableModel model = new DefaultTableModel();
					model.addColumn("�����");
					model.addColumn("�������");
					model.addColumn("������Ʈ");
					model.addColumn("������Ʈ���");
					
					Iterator iter = li.iterator();
					while(iter.hasNext())
					{
						Company companyInfo = (Company) iter.next();
						model.addRow(new Object[]{companyInfo.getCompany_name(),companyInfo.getCompany_abbr(),companyInfo.getAgent_name(),companyInfo.getAgent_abbr()});	
	
					}
					table.setModel(model);
					table.updateUI();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	
			});
		pnSearch.add(lblSearch,BorderLayout.WEST);
		pnSearch.add(txfSearch);
		
		JPanel pnControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton butOk = new JButton("Ȯ��");
		
		butOk.addActionListener(new ActionListener(){
	
			public void actionPerformed(ActionEvent e) {
				
				int result=table.getSelectedRow();
				if(result==-1)
				{
					JOptionPane.showMessageDialog(SearchCompanyInfoDialog.this, "���õ� ������� �����ϴ�.");
				}else
				{
					String company_abbr=(String) table.getValueAt(result, 1);
					String agent_abbr=(String) table.getValueAt(result, 3);
					txfCompanyName.setText(company_abbr.compareToIgnoreCase(agent_abbr)==0?company_abbr:company_abbr+"/"+agent_abbr);
					txfCompanyName.setText(company_abbr);
					setVisible(false);
					dispose();
				}
				
			}});
		JButton butCancel = new JButton("���");
		
		
		
		pnMain.add(new JScrollPane(table));
		butCancel.addActionListener(new ActionListener(){
	
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}});
		
		pnControl.add(butOk);
		pnControl.add(butCancel);
		getContentPane().add(pnSearch,BorderLayout.NORTH);
		getContentPane().add(pnMain,BorderLayout.CENTER);
		getContentPane().add(pnControl,BorderLayout.SOUTH);
		pack();
		ViewUtil.center(this, true);
		setVisible(true);
	}

}