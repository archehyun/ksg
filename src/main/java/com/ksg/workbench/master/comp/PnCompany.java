package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import com.dtp.api.control.CompanyController;
import com.ksg.common.model.CommandMap;
import com.ksg.service.impl.CompanyServiceImpl;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPageTablePanel;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.UpdateCompanyInfoDialog;

import lombok.extern.slf4j.Slf4j;


/**

 * @FileName : PnCompany.java

 * @Date : 2021. 2. 26. 
 * @�ۼ��� : ��â��

 * @�����̷� :

 * @���α׷� ���� : ���� ���� ���� ȭ��

 */
@Slf4j
public class PnCompany extends PnBase implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	/**
	 * 
	 */
	private JComboBox cbxField;

	private JTextField txfSearch;

	private JLabel lblTable;	

	private KSGTablePanel tableH;

	private String query;

	CompanyServiceImpl companyService = new CompanyServiceImpl();

	private List<HashMap<String, Object>> master;

	public PnCompany(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);		
		this.addComponentListener(this);
		this.setController(new CompanyController());
		this.add(buildCenter());
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}


	private JComponent buildCenter()
	{
		log.debug("ȭ�� �ʱ�ȭ");
		KSGPanel pnMain = new KSGPanel(new BorderLayout());	

		KSGTableColumn columns[] = new KSGTableColumn[5];

		columns[0] = new KSGTableColumn();
		columns[0].columnField = "company_name";
		columns[0].columnName = "�����";
		columns[0].size = 300;
		columns[0].ALIGNMENT = SwingConstants.LEFT;

		columns[1] = new KSGTableColumn();
		columns[1].columnField = "company_abbr";
		columns[1].columnName = "������";
		columns[1].size = 300;
		columns[1].ALIGNMENT = SwingConstants.LEFT;

		columns[2] = new KSGTableColumn();
		columns[2].columnField = "agent_name";
		columns[2].columnName = "������Ʈ��";
		columns[2].size = 300;
		columns[2].ALIGNMENT = SwingConstants.LEFT;

		columns[3] = new KSGTableColumn();
		columns[3].columnField = "agent_abbr";
		columns[3].columnName = "������Ʈ ���";
		columns[3].size = 300;
		columns[3].ALIGNMENT = SwingConstants.LEFT;

		columns[4] = new KSGTableColumn();
		columns[4].columnField = "contents";
		columns[4].columnName = "���";
		columns[4].size = 300;


		tableH = new KSGTablePanel("������");

		tableH.addMouseListener(new TableSelectListner());

		tableH.setShowControl(true);
		
		tableH.addContorlListener(this);

		pnMain.add(tableH);

		tableH.setColumnName(columns);
		
		tableH.initComp();

		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);

		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));

		log.debug("ȭ�� �ʱ�ȭ ����");
		
		return pnMain;

	}
	/**
	 * @return
	 */
	private KSGPanel buildSearchPanel() {
		KSGPanel pnSearch = new KSGPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));

		lblTable = new JLabel("���� ����");
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("����",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));
		JLabel lbl = new JLabel("�ʵ�� : ");
		cbxField = new JComboBox();		
		cbxField.addItem("�����");
		cbxField.addItem("����� ���");
		cbxField.addItem("������Ʈ");
		cbxField.addItem("������Ʈ ���");
		txfSearch = new JTextField(15);

		txfSearch.addKeyListener(new KeyAdapter() {


			@Override
			public void keyPressed(KeyEvent e) {

				if(e.getKeyChar()==KeyEvent.VK_ENTER)
				{
					fnSearch();
				}
			}
		});


		JButton butUpSearch = new JButton("�˻�");

		butUpSearch.addActionListener(this);

		cbxField.setPreferredSize(new Dimension(150,23));

		pnSearch.add(lbl);
		pnSearch.add(cbxField);
		pnSearch.add(txfSearch);
		pnSearch.add(butUpSearch);
		Box pnSearchAndCount = Box.createVerticalBox();
		pnSearchAndCount.add(pnSearch);



		KSGPanel pnMain= new KSGPanel(new BorderLayout());
		pnMain.add(buildLine(),BorderLayout.SOUTH);
		pnMain.add(pnSearchAndCount,BorderLayout.EAST);
		pnMain.add(buildTitleIcon("����� ����"),BorderLayout.WEST);
		return pnMain;
	}	

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals("�˻�"))
		{
			fnSearch();
		}
		else if(command.equals(KSGPageTablePanel.DELETE))
		{
			int row=tableH.getSelectedRow();
			if(row<0)
				return;

			CommandMap data= (CommandMap) tableH.getValueAt(row);

			int result=JOptionPane.showConfirmDialog(this,data.get("company_name")+"�� ���� �Ͻðڽ��ϱ�?", "���� ���� ����", JOptionPane.YES_NO_OPTION);

			if(result==JOptionPane.OK_OPTION)
			{	
				try {
					int count=companyService.delete(data);

					if(count>0)
					{
						fnSearch();
						JOptionPane.showMessageDialog(this, "�����Ǿ����ϴ�.");
					}

				} catch (SQLException e1) {

					e1.printStackTrace();
					JOptionPane.showConfirmDialog(this, e1.getMessage());
				}
			}	
		}
		else if(command.equals(KSGPageTablePanel.INSERT))
		{
			KSGDialog dialog = new UpdateCompanyInfoDialog(UpdateCompanyInfoDialog.INSERT);

			dialog.createAndUpdateUI();

			if(dialog.result==KSGDialog.SUCCESS)
			{
				fnSearch();
			}
		}
	}



	class TableSelectListner extends MouseAdapter
	{
		KSGDialog dialog;

		public void mouseClicked(MouseEvent e) 
		{

			if(e.getClickCount()>1)
			{
				JTable es = (JTable) e.getSource();
				int row=es.getSelectedRow();

				if(row<0)
					return;


				HashMap<String, Object> port=(HashMap<String, Object>) tableH.getValueAt(row);

				dialog = new UpdateCompanyInfoDialog(UpdateCompanyInfoDialog.UPDATE,port);
				
				

				dialog .createAndUpdateUI();
				
				
				
				int result = dialog.result;

				if(result==UpdateCompanyInfoDialog.SUCCESS)
				{
					fnSearch();
				}



			}
		}

	}


	class MyTableColumnModelListener implements TableColumnModelListener {
		JTable table;
		public MyTableColumnModelListener(JTable table) {
			this.table = table;
		}

		public void columnAdded(TableColumnModelEvent e) {}

		public void columnRemoved(TableColumnModelEvent e) {}

		public void columnMoved(TableColumnModelEvent e) {
			int fromIndex = e.getFromIndex();
			int toIndex = e.getToIndex();

			if(fromIndex!=toIndex)
			{
				//orderby =getOrderBy(tblCompanyTable.getColumnModel());
			}
		}

		public void columnMarginChanged(ChangeEvent e) {


		}

		public void columnSelectionChanged(ListSelectionEvent e) {
		}
	}	

	@Override
	public void fnSearch() {

		log.debug("start");

		CommandMap param = new CommandMap();

		String field = (String) cbxField.getSelectedItem();

		if(!"".equals(txfSearch.getText()))
		{
			if(field.equals("�����"))
			{
				query="company_name";
			}else if(field.equals("����� ���"))
			{
				query="company_abbr";
			}
			else if(field.equals("������Ʈ"))
			{
				query="agent_name";
			}
			else if(field.equals("������Ʈ ���"))
			{
				query="agent_abbr";
			}
			param.put(query, txfSearch.getText());
		}

		callApi("selectCompany", param);

	}


	@Override
	public void componentShown(ComponentEvent e) {
		if(isShowData)fnSearch();

	}

	@Override
	public void updateView() {
		CommandMap result= this.getModel();

		boolean success = (boolean) result.get("success");

		if(success)
		{

			List data = (List )result.get("data");

			tableH.setResultData(data);

			if(data.size()==0)tableH.changeSelection(0,0,false,false);

		}
		else{  
			String error = (String) result.get("error");
			JOptionPane.showMessageDialog(this, error);
		}


	}

}
