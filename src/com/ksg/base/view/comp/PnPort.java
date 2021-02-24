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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.ksg.base.view.BaseInfoUI;
import com.ksg.base.view.dialog.InsertPortAbbrInfoDialog;
import com.ksg.base.view.dialog.InsertPortInfoDialog;
import com.ksg.base.view.dialog.UpdatePortInfoDialog;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.common.view.comp.KSGTableCellRenderer;
import com.ksg.domain.PortInfo;

/**
 * @설명 항구 정보 관리 화면
 * @author archehyun
 *
 */
public class PnPort extends PnBase implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel lblTotal,lblTable;

	private JComboBox cbxPortArea,cbxAreaCode,cbxField;

	private JTextField txfSearch;

	PortTable tblTable;


	private String orderby;

	public PnPort(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);

		this.add(buildCenter());

	}
	/**
	 * @return
	 */
	private JPanel buildSearchPanel() {
		JPanel pnSearch = new JPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lblTotal = new JLabel();
		lblTable = new JLabel("항구 정보");
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("돋움",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));
		JLabel lbl = new JLabel("필드명 : ");
		cbxField = new JComboBox();
		//cbxField.addItem("선택");
		cbxField.addItem("항구명");
		cbxField.addItem("나라");

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

		JLabel lblArea = new JLabel("지역:");
		JLabel lblAreaCode = new JLabel("지역코드:");
		cbxPortArea = new JComboBox();
		cbxAreaCode = new JComboBox();

		try {
			cbxPortArea.addItem("선택");
			cbxAreaCode.addItem("선택");
			List listArea = baseDaoService.getAreaListGroupByAreaName();
			List listAreaCode = baseDaoService.getAreaListGroupByAreaCode();
			Iterator areaIter = listArea.iterator();
			while(areaIter.hasNext())
			{
				String area = (String) areaIter.next();
				cbxPortArea.addItem(area);
			}

			Iterator areaCodeIter = listAreaCode.iterator();
			while(areaCodeIter.hasNext())
			{
				String code = (String) areaCodeIter.next();
				cbxAreaCode.addItem(code);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pnSearch.add(lblArea);
		pnSearch.add(cbxPortArea);
		pnSearch.add(lblAreaCode);
		pnSearch.add(cbxAreaCode);
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
		JButton butNewAbbr = new JButton("약어 등록");
		pnButtomRight.setBorder(BorderFactory.createEtchedBorder());		
		butDel.addActionListener(this);
		butNew.addActionListener(this);
		butNewAbbr.addActionListener(this);

		pnButtomRight.add(butNew);
		pnButtomRight.add(butNewAbbr);
		pnButtomRight.add(butDel);
		pnButtom.add(pnButtomRight);
		return pnButtom;
	}



	private JPanel buildCenter()
	{
		JPanel pnMain = new JPanel(new BorderLayout());

		tblTable = new PortTable();

		tblTable.addMouseListener(new TableSelectListner());

		tblTable.getColumnModel().addColumnModelListener(new MyTableColumnModelListener(tblTable));

		tblTable.addMouseListener(new TableSelectListner());

		JScrollPane jScrollPane = new JScrollPane(tblTable);

		pnMain.add(jScrollPane);

		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);

		pnMain.add(buildButton(),BorderLayout.SOUTH);

		return pnMain;

	}

	public void updateTable(String query) {

		logger.info("query:"+query);

		try {
			tblTable.setQuery(query);
			tblTable.retrive();

			lblTotal.setText(tblTable.getRowCount()+" / "+tblTable.getToalCount());

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(PnPort.this, e.getMessage());
		}

		lblTotal.setText(searchTotalSize+" / "+totalSize);
	}
	private void searchData()
	{
		PortInfo option = new PortInfo();
		
		if(cbxAreaCode.getSelectedIndex()>0)
			option.setArea_code((String)cbxAreaCode.getSelectedItem());
		
		if(cbxPortArea.getSelectedIndex()>0)
			option.setPort_area((String)cbxPortArea.getSelectedItem());

		String field = (String) cbxField.getSelectedItem();
		if(field.equals("항구명"))
		{
			query="port_name";
		}else if(field.equals("나라"))
		{
			query="port_nationality";
		}

		query+=" like '%"+txfSearch.getText()+"%'";
		option.setSearchKeyword(query);
		
		/*if(cbxField.getSelectedIndex()>0)
		{
			
		}
		else
		{
			txfSearch.setText("");
		}*/
		
		try {
			tblTable.setOption(option);
			tblTable.retrive();

			lblTotal.setText(tblTable.getRowCount()+" / "+tblTable.getToalCount());

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(PnPort.this, e.getMessage());
		}

		lblTotal.setText(searchTotalSize+" / "+totalSize);
	}

	


	@Override
	public void updateTable() {
		searchData();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("검색"))
		{
			searchData();
		}
		else if(command.equals("신규"))
		{
			KSGDialog dialog = new InsertPortInfoDialog(getBaseInfoUI());
			dialog.createAndUpdateUI();
			if(dialog.result==KSGDialog.SUCCESS)
			{
				searchData();
			}
		}
		else if(command.equals("약어 등록"))
		{
			int row=tblTable.getSelectedRow();
			if(row<0)
				return;
			String port_name=(String) tblTable.getValueAt(row, 0);

			KSGDialog dialog = new InsertPortAbbrInfoDialog(getBaseInfoUI(),port_name);
			dialog.createAndUpdateUI();
			if(dialog.result==KSGDialog.SUCCESS)
			{
				searchData();
			}
		}
		else if(command.equals("삭제"))
		{
			int row=tblTable.getSelectedRow();
			if(row<0)
				return;

			String data = (String) tblTable.getValueAt(row, 0);
			int result=JOptionPane.showConfirmDialog(null, data+"를 삭제 하시겠습니까?", "항구 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{						
				try {
					int count=baseDaoService.deletePort(data);
					if(count>0)
					{
						searchData();
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}
	class TableSelectListner extends MouseAdapter
	{
		KSGDialog dialog;
		public void mouseClicked(MouseEvent e) 
		{
			if(e.getClickCount()>=2)
			{
				JTable es = (JTable) e.getSource();
				int row=es.getSelectedRow();
				if(row<0)
					return;
				String Portdata=(String) tblTable.getValueAt(row, 0);
				dialog = new UpdatePortInfoDialog(Portdata);
				dialog.createAndUpdateUI();
				if(dialog.result==KSGDialog.SUCCESS)
				{
					searchData();
				}
			}
		}

	}
	public String getOrderBy(TableColumnModel columnModel) {

		logger.info("orderby start");
		/*
		 * 1. 현재 칼럼 목록 순서를 이용해서 order by 순서를 생성하고 
		 * 2. 조회결과를 위한 현재 칼럼 목록 순서를 저장함
		 */
		tblTable.currentColumnNameList.clear();// 현재 칼럼 순서 초기화
		
		StringBuffer buffer = new StringBuffer();
		
		int col=tblTable.getColumnModel().getColumnCount();

		ArrayList<String> orderbyList = new ArrayList<String>();
		
		for(int i=0;i<col;i++)
		{
			String headerValue =(String) tblTable.getColumnModel().getColumn(i).getHeaderValue();
			tblTable.currentColumnNameList.add(headerValue);//현재 칼럼 순서 생성
			String row=tblTable.arrangeMap.get(headerValue);
			orderbyList.add(row);
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
				orderby =getOrderBy(tblTable.getColumnModel());
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

	class PortTable extends BaseTable
	{
		private String[] columName = {"항구명", "나라","지역", "지역코드"};
		
		private String[] fieldName={"port_name","port_nationality","port_area","area_code"};
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public PortTable() {

			super();

			// 칼럼 정보 초기화
			for(int i=0;i<columName.length;i++)
			{
				arrangeMap.put(columName[i], fieldName[i]);
				this.currentColumnNameList.add(columName[i]);
			}
			this.setModel(modelInit());
			this.columInit();
		}
		private Object[]  getRowData(PortInfo info)
		{
			Object rows[] =new Object[currentColumnNameList.size()];

			for(int columnIndex=0;columnIndex<currentColumnNameList.size();columnIndex++)
			{

				String columnName =currentColumnNameList.get(columnIndex);
				if(columnName.equals("항구명"))
				{
					rows[columnIndex]= info.getPort_name();
				}
				else if(columnName.equals("나라"))
				{
					rows[columnIndex]= info.getPort_nationality();
				}
				else if(columnName.equals("지역"))
				{
					rows[columnIndex]= info.getPort_area();
				}
				else if(columnName.equals("지역코드"))
				{
					rows[columnIndex]= info.getArea_code();
				}
			}

			return rows;
		}
		PortInfo searchOP;

		@Override
		public void retrive() throws SQLException {
			
			modelInit();

			List li =baseService.getSearchedPortList(searchOP);

			Iterator iter = li.iterator();

			logger.info("search : " + li.size());

			totalCount = baseService.getPortCount();

			while(iter.hasNext())
			{
				PortInfo portInfo = (PortInfo) iter.next();
				model.addRow(this.getRowData(portInfo));
			}

			RowSorter<TableModel> sorter
			= new TableRowSorter<TableModel>(model);

			setRowSorter(sorter);

			setModel(model);
			
			this.columInit();

			updateUI();

		}
		protected void columInit()
		{
			TableColumnModel colmodel = getColumnModel();

			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new KSGTableCellRenderer();
				if(i==3)
				{
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
				}
				else
				{
					renderer.setHorizontalAlignment(SwingConstants.LEFT);
				}
				namecol.setCellRenderer(renderer);	
			}
			colmodel.getColumn(0).setPreferredWidth(300);
			colmodel.getColumn(1).setPreferredWidth(300);
			colmodel.getColumn(2).setPreferredWidth(300);
			colmodel.getColumn(3).setPreferredWidth(100);
			//setModel(model);
		}
		public void setOption(PortInfo param)
		{
			this.searchOP = param;
		}

	}


}
