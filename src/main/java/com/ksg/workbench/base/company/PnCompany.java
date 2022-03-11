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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.domain.Company;
import com.ksg.service.impl.CompanyServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTable;
import com.ksg.view.comp.table.KSGTableCellRenderer;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.model.KSGTableModel;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.comp.PnBase;
import com.ksg.workbench.base.company.dialog.UpdateCompanyInfoDialog;
import com.ksg.workbench.common.comp.button.PageAction;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.schedule.comp.KSGPageTablePanel;


/**

 * @FileName : PnCompany.java

 * @Date : 2021. 2. 26. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 선사 정보 관리

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
	
	protected BaseDAOManager baseDaoService;

	private KSGPageTablePanel tableH;

	private String[] fieldName = {"company_name","company_abbr","agent_name", "agent_abbr","contents"};

	private String query;

	private String orderby;
	
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

		

		KSGPanel pnCount = new KSGPanel();
		
		pnCount.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		pnCount.add(lblTable);

		KSGPanel pnInfo= new KSGPanel(new BorderLayout());

		KSGPanel pnS = new KSGPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		KSGPanel pnS1 = new KSGPanel();
		pnS1.setPreferredSize(new Dimension(0,15));
		Box info = new Box(BoxLayout.Y_AXIS);
		info.add(pnS);
		info.add(pnS1);


		pnInfo.add(info,BorderLayout.SOUTH);
		pnInfo.add(pnSearchAndCount,BorderLayout.EAST);
		pnInfo.add(pnCount,BorderLayout.WEST);
		return pnInfo;
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

	/**
	 * 
	 */
//	private void searchData() {
//		String field=(String) cbxField.getSelectedItem();
//
//		/*	if(cbxField.getSelectedIndex()==0)
//		{
//
//			txfSearch.setText("");
//			query = null;
//
//			this.updateTable(query);
//		}
//		else*/
//		{
//			if(field.equals("선사명"))
//			{
//				query="company_name";
//			}else if(field.equals("선사명 약어"))
//			{
//				query="company_abbr";
//			}
//			else if(field.equals("에이전트"))
//			{
//				query="agent_name";
//			}
//			else if(field.equals("에이전트 약어"))
//			{
//				query="agent_abbr";
//			}
//
//			query+=" like '"+txfSearch.getText()+"%'";
//
//			this.updateTable(query);
//		}
//	}

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

//	public void updateTable() {
//		searchData();
//	}

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

	class CompanyTable extends KSGTable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		int totalCount;

		private String[] columName = {"선사명","선사명 약어","에이전트", "에이전트 약어","비고"};

		private HashMap<String, String> arrangeMap;

		private ArrayList<String> currentColumnNameList;

		String query;

		String orderby;

		public CompanyTable() {
			super();

			//order by를 위한 칼럼 목록 생성		
			arrangeMap = new HashMap<String, String>();

			// 칼럼 순서 정보를 저장하기 위한 클래스 생성
			currentColumnNameList = new ArrayList<String>();

			// 칼럼 정보 초기화
			for(int i=0;i<columName.length;i++)
			{
				arrangeMap.put(columName[i], fieldName[i]);
				currentColumnNameList.add(columName[i]);
			}
			this.setModel(modelInit());
			this.columInit();


		}

		public void setQuery(String query) {
			this.query =query;
		}

		private Object[]  getRowData(Company info)
		{

			Object rows[] =new Object[currentColumnNameList.size()];

			for(int columnIndex=0;columnIndex<currentColumnNameList.size();columnIndex++)
			{
				String columnName =currentColumnNameList.get(columnIndex);
				if(columnName.equals("선사명"))
				{
					rows[columnIndex]= info.getCompany_name();
				}
				else if(columnName.equals("선사명 약어"))
				{
					rows[columnIndex]= info.getCompany_abbr();
				}
				else if(columnName.equals("에이전트"))
				{
					rows[columnIndex]= info.getAgent_name();
				}
				else if(columnName.equals("에이전트 약어"))
				{
					rows[columnIndex]= info.getAgent_abbr();
				}
				else if(columnName.equals("비고"))
				{
					rows[columnIndex]= info.getContents();
				}
			}
			return rows;
		}

		private DefaultTableModel modelInit()
		{
			model = new KSGTableModel();

			for(int i=0;i<currentColumnNameList.size();i++)
			{
				model.addColumn(currentColumnNameList.get(i));
			}
			return model;
		}
		private void columInit()
		{
			TableColumnModel colmodel = getColumnModel();

			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new KSGTableCellRenderer();
				//if(i==1)
				{
					renderer.setHorizontalAlignment(SwingConstants.LEFT);
				}
				namecol.setCellRenderer(renderer);
				namecol.setPreferredWidth(300);
			}
		}

		@Override
		public void retrive() throws SQLException {

			modelInit();

			Company searchOP = new Company();

			searchOP.setSearchKeyword(query);

			searchOP.setOrderBy(orderby);

			List li =baseDaoService.getSearchedCompanyList(searchOP);

			Iterator iter = li.iterator();

			logger.info("search : " + li.size());

			totalCount = companyService.getCompanyCount();

			while(iter.hasNext())
			{
				Company companyInfo = (Company) iter.next();
				model.addRow(this.getRowData(companyInfo));
			}

			RowSorter<TableModel> sorter
			= new TableRowSorter<TableModel>(model);

			setRowSorter(sorter);

			setModel(model);

			columInit();

			updateUI();

		}
		public int getToalCount()
		{
			return totalCount;
		}
		public int getRowCount()
		{
			return model.getRowCount();
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
