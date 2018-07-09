package com.ksg.base.view.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

import com.ksg.base.view.dialog.InsertCompanyInfoDialog;
import com.ksg.base.view.dialog.UpdateCompanyInfoDialog;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.common.view.comp.KSGTable;
import com.ksg.common.view.comp.KSGTableCellRenderer;
import com.ksg.common.view.comp.KSGTableModel;
import com.ksg.domain.Company;


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

	private JLabel lblTable,lblTotal;

	CompanyTable tblCompanyTable;	

	private String[] fieldName = {"company_name","company_abbr","agent_name", "agent_abbr","contents"};

	private String query;

	private String orderby;

	public PnCompany() {
		super();		

		this.add(buildCenter());


	}
	public String getOrderBy(TableColumnModel columnModel)	
	{
		/*
		 * 1. 현재 칼럼 목록 순서를 이용해서 order by 순서를 생성하고 
		 * 2. 조회결과를 위한 현재 칼럼 목록 순서를 저장함
		 */
		tblCompanyTable.currentColumnNameList.clear();// 현재 칼럼 순서 초기화
		
		StringBuffer buffer = new StringBuffer();
		
		int col=tblCompanyTable.getColumnModel().getColumnCount();
		
		ArrayList<String> orderbyList = new ArrayList<String>();
		for(int i=0;i<col;i++)
		{
			String headerValue =(String) tblCompanyTable.getColumnModel().getColumn(i).getHeaderValue();
			tblCompanyTable.currentColumnNameList.add(headerValue);//현재 칼럼 순서 생성
			String row=tblCompanyTable.arrangeMap.get(headerValue);

			// 데이터 타입이 text인 것은 정렬에 포함할수 없음
			if(!row.equals("contents"))
			{	
				orderbyList.add(row);
			}
		}

		//질의를 위한 'order by' 생성
		for(int i=0;i<orderbyList.size();i++)
		{
			buffer.append(orderbyList.get(i));
			if(i<(orderbyList.size()-1))
				buffer.append(",");
		}


		return buffer.toString();

	}

	private JPanel buildCenter()
	{
		JPanel pnMain = new JPanel(new BorderLayout());

		tblCompanyTable = new CompanyTable();

		tblCompanyTable.addMouseListener(new TableSelectListner());

		tblCompanyTable.getColumnModel().addColumnModelListener(new MyTableColumnModelListener(tblCompanyTable));

		tblCompanyTable.addMouseListener(new TableSelectListner());

		JScrollPane jScrollPane = new JScrollPane(tblCompanyTable);

		jScrollPane.getViewport().setBackground(Color.white);

		pnMain.add(jScrollPane);

		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);

		pnMain.add(buildButton(),BorderLayout.SOUTH);
		return pnMain;

	}
	/**
	 * @return
	 */
	private JPanel buildSearchPanel() {
		JPanel pnSearch = new JPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lblTotal = new JLabel();
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
					searchData();
				}

			}
		});
		JLabel label = new JLabel("개 항목");
		JButton butUpSearch = new JButton("검색");
		butUpSearch.addActionListener(this);

		cbxField.setPreferredSize(new Dimension(150,23));

		pnSearch.add(lbl);
		pnSearch.add(cbxField);
		pnSearch.add(txfSearch);
		pnSearch.add(butUpSearch);
		Box pnSearchAndCount = Box.createVerticalBox();
		pnSearchAndCount.add(pnSearch);

		JPanel pnCountInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnCountInfo.add(lblTotal);
		pnCountInfo.add(label);
		pnSearchAndCount.add(pnCountInfo);

		JPanel pnCount = new JPanel();
		pnCount.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnCount.add(lblTable);


		JPanel pnInfo= new JPanel(new BorderLayout());

		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));
		Box info = new Box(BoxLayout.Y_AXIS);
		info.add(pnS);
		info.add(pnS1);


		pnInfo.add(info,BorderLayout.SOUTH);
		pnInfo.add(pnSearchAndCount,BorderLayout.EAST);
		pnInfo.add(pnCount,BorderLayout.WEST);
		return pnInfo;
	}
	private JPanel buildButton()
	{
		JPanel pnButtom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel pnButtomRight = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton butDel = new JButton("삭제");
		JButton butNew = new JButton("신규");
		pnButtomRight.setBorder(BorderFactory.createEtchedBorder());		
		butDel.addActionListener(this);
		butNew.addActionListener(this);

		pnButtomRight.add(butNew);
		pnButtomRight.add(butDel);
		pnButtom.add(pnButtomRight);
		return pnButtom;
	}



	@Override
	public void updateTable(String query) {

		logger.info("order by:"+orderby);

		try {
			tblCompanyTable.setQuery(query);
			tblCompanyTable.retrive();

			lblTotal.setText(tblCompanyTable.getRowCount()+" / "+tblCompanyTable.getToalCount());

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(PnCompany.this, e.getMessage());
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		if(command.equals("검색"))
		{
			searchData();
		}
		else if(command.equals("삭제"))
		{
			int row=tblCompanyTable.getSelectedRow();
			if(row<0)
				return;

			String data= (String) tblCompanyTable.getValueAt(row, 1);
			int result=JOptionPane.showConfirmDialog(this,data+"를 삭제 하시겠습니까?", "선사 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{	
				try {
					int count=baseService.deleteCompany(data);
					if(count>0)
					{
						searchData();
						JOptionPane.showMessageDialog(this, "삭제되었습니다.");
					}

				} catch (SQLException e1) {

					e1.printStackTrace();
					JOptionPane.showConfirmDialog(this, e1.getMessage());
				}
			}	
		}
		else if(command.equals("신규"))
		{
			KSGDialog dialog = new InsertCompanyInfoDialog();
			dialog.createAndUpdateUI();
			if(dialog.result==KSGDialog.SUCCESS)
			{
				searchData();
			}
		}


	}

	/**
	 * 
	 */
	private void searchData() {
		String field=(String) cbxField.getSelectedItem();

	/*	if(cbxField.getSelectedIndex()==0)
		{

			txfSearch.setText("");
			query = null;

			this.updateTable(query);
		}
		else*/
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

			query+=" like '"+txfSearch.getText()+"%'";

			this.updateTable(query);
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
				
				String data=(String) tblCompanyTable.getValueAt(row, 1);

				dialog = new UpdateCompanyInfoDialog(UpdateCompanyInfoDialog.UPDATE,data);
				dialog .createAndUpdateUI();
				updateTable(query);


			}
		}

	}

	@Override
	public void updateTable() {
		searchData();
	}

	class MyTableColumnModelListener implements TableColumnModelListener {
		JTable table;
		public MyTableColumnModelListener(JTable table) {
			this.table = table;
		}

		public void columnAdded(TableColumnModelEvent e) {

		}

		public void columnRemoved(TableColumnModelEvent e) {

		}

		public void columnMoved(TableColumnModelEvent e) {
			int fromIndex = e.getFromIndex();
			int toIndex = e.getToIndex();

			if(fromIndex!=toIndex)
			{
				orderby =getOrderBy(tblCompanyTable.getColumnModel());
			}
		}

		public void columnMarginChanged(ChangeEvent e) {


		}

		public void columnSelectionChanged(ListSelectionEvent e) {
		}
	}

	@Override
	public void initTable() {


		
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

			searchOP.setOrderby(orderby);

			List li =baseService.getSearchedCompanyList(searchOP);

			Iterator iter = li.iterator();

			logger.info("search : " + li.size());

			totalCount = baseService.getCompanyCount();

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


}
