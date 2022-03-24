package com.ksg.workbench.base.company;

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

import com.ksg.service.impl.CompanyServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.comp.PnBase;
import com.ksg.workbench.base.company.dialog.UpdateCompanyInfoDialog;
import com.ksg.workbench.common.comp.KSGPageTablePanel;
import com.ksg.workbench.common.comp.button.PageAction;
import com.ksg.workbench.common.comp.dialog.KSGDialog;


/**

 * @FileName : PnCompany.java

 * @Date : 2021. 2. 26. 
 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 선사 정보 관리 화면

 */
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

	private KSGPageTablePanel tableH;

	//private String[] fieldName = {"company_name","company_abbr","agent_name", "agent_abbr","contents"};

	private String query;

	//private String orderby;
	
	CompanyServiceImpl companyService = new CompanyServiceImpl();

	private List<HashMap<String, Object>> master;

	public PnCompany(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);		
		this.addComponentListener(this);
		this.add(buildCenter());
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));


	}


	private JComponent buildCenter()
	{
		logger.debug("화면 초기화");
		KSGPanel pnMain = new KSGPanel(new BorderLayout());	

		KSGTableColumn columns[] = new KSGTableColumn[5];

		columns[0] = new KSGTableColumn();
		columns[0].columnField = "company_name";
		columns[0].columnName = "선사명";
		columns[0].size = 300;
		columns[0].ALIGNMENT = SwingConstants.LEFT;

		columns[1] = new KSGTableColumn();
		columns[1].columnField = "company_abbr";
		columns[1].columnName = "선사약어";
		columns[1].size = 300;
		columns[1].ALIGNMENT = SwingConstants.LEFT;

		columns[2] = new KSGTableColumn();
		columns[2].columnField = "agent_name";
		columns[2].columnName = "에이전트명";
		columns[2].size = 300;
		columns[2].ALIGNMENT = SwingConstants.LEFT;

		columns[3] = new KSGTableColumn();
		columns[3].columnField = "agent_abbr";
		columns[3].columnName = "에이전트 약어";
		columns[3].size = 300;
		columns[3].ALIGNMENT = SwingConstants.LEFT;
		
		columns[4] = new KSGTableColumn();
		columns[4].columnField = "contents";
		columns[4].columnName = "비고";
		columns[4].size = 300;
		
		
		tableH = new KSGPageTablePanel("선사목록");

		tableH.addMouseListener(new TableSelectListner());
		
		tableH.setShowControl(true);
		tableH.addContorlListener(this);

		pnMain.add(tableH);

		tableH.setColumnName(columns);
		tableH.initComp();
		
		tableH.addActionListener(new PageAction(tableH, companyService));
		

		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);

		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));
		
		logger.debug("화면 초기화 종료");
		return pnMain;

	}
	/**
	 * @return
	 */
	private KSGPanel buildSearchPanel() {
		KSGPanel pnSearch = new KSGPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		lblTable = new JLabel("선사 정보");
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("돋움",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));
		JLabel lbl = new JLabel("필드명 : ");
		cbxField = new JComboBox();		
		cbxField.addItem("선사명");
		cbxField.addItem("선사명 약어");
		cbxField.addItem("에이전트");
		cbxField.addItem("에이전트 약어");
		txfSearch = new JTextField(15);

		txfSearch.addKeyListener(new KeyAdapter() {


			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					fnSearch();
				}
			}
		});
		
		
		JButton butUpSearch = new JButton("검색");
		
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
		pnMain.add(buildTitleIcon("사용자 정보"),BorderLayout.WEST);
		return pnMain;
	}	

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals("검색"))
		{
			fnSearch();
		}
		else if(command.equals(KSGPageTablePanel.DELETE))
		{
			int row=tableH.getSelectedRow();
			if(row<0)
				return;

			HashMap<String, Object> data= (HashMap<String, Object>) tableH.getValueAt(row);
			
			int result=JOptionPane.showConfirmDialog(this,data.get("company_name")+"를 삭제 하시겠습니까?", "선사 정보 삭제", JOptionPane.YES_NO_OPTION);
			
			if(result==JOptionPane.OK_OPTION)
			{	
				try {
					int count=companyService.delete(data);
					
					if(count>0)
					{
						fnSearch();
						JOptionPane.showMessageDialog(this, "삭제되었습니다.");
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

		logger.debug("start");
		
		HashMap<String, Object> param = new HashMap<String, Object>();

		String field = (String) cbxField.getSelectedItem();
		
		if(!"".equals(txfSearch.getText()))
		{
			if(field.equals("선사명"))
			{
				query="company_name";
			}else if(field.equals("선사명 약어"))
			{
				query="company_abbr";
			}
			else if(field.equals("에이전트"))
			{
				query="agent_name";
			}
			else if(field.equals("에이전트 약어"))
			{
				query="agent_abbr";
			}
			param.put(query, txfSearch.getText());
		}
		
		
		try {
			
			logger.info("param:"+param);
			
			int page_size = tableH.getPageSize();
			
			param.put("PAGE_SIZE", page_size);
			
			param.put("PAGE_NO", 1);
			
			HashMap<String, Object> result = (HashMap<String, Object>) companyService.selectListByPage(param);
			
			result.put("PAGE_NO", 1);

			tableH.setResultData(result);

			master = (List) result.get("master");

			if(master.size()==0)
			{
				/*lblArea.setText("");
				lblAreaCode.setText("");
				lblPationality.setText("");
				lblPortName.setText("");
				tableD.clearReslult();*/
			}
			else
			{
				tableH.changeSelection(0,0,false,false);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public void componentShown(ComponentEvent e) {
		if(isShowData)fnSearch();
		
	}

}
