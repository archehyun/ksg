package com.ksg.workbench.base.port;

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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.ksg.domain.PortInfo;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableCellRenderer;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.view.comp.table.model.KSGTableModel;
import com.ksg.workbench.KSGViewParameter;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.comp.PnBase;
import com.ksg.workbench.base.port.dialog.InsertPortAbbrInfoDialog;
import com.ksg.workbench.base.port.dialog.UpdatePortAbbrInfoDialog;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**
 * 항구 약어 정보 관리 화면
 * @author 박창현
 *
 */
@Deprecated
public class PnPortAbbr extends PnBase implements ActionListener{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblTotal;
	private JLabel lblTable;
	private JComboBox cbxField;
	private JTextField txfSearch;
	private String[] columName = {"항구명","항구명 약어"};
	
	private KSGAbstractTable tableH;
	
	private PortServiceImpl portService = new PortServiceImpl();
	
	public PnPortAbbr(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);

		this.add(buildCenter());
		//this.initTable();

	}

	/**
	 * @return
	 */
	private JPanel buildSearchPanel() {
		JPanel pnSearch = new JPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lblTotal = new JLabel();
		lblTable = new JLabel("항구 약어 정보");
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("돋움",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));
		JLabel lbl = new JLabel("필드명 : ");
		cbxField = new JComboBox();		
		cbxField.addItem("항구명");
		cbxField.addItem("항구명 약어");

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
	private JPanel buildCenter()
	{
		JPanel pnMain = new JPanel(new BorderLayout());
		
		
		tableH = new KSGAbstractTable();
		
//		tableH.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		KSGTableColumn dcolumns1 = new KSGTableColumn();
		dcolumns1.columnField = "port_name";
		dcolumns1.columnName = "항구명";
		dcolumns1.size=200;
		
		
		KSGTableColumn dcolumns2 = new KSGTableColumn();
		dcolumns2.columnField = "port_abbr";
		dcolumns2.columnName = "항구명 약어";
		dcolumns2.size=200;
		
		tableH.addColumn(dcolumns1);
		tableH.addColumn(dcolumns2);
		tableH.initComp();	
		
		JScrollPane jScrollPane = new JScrollPane(tableH);
		
		
		jScrollPane.getViewport().setBackground(Color.white);
		pnMain.add(jScrollPane);
		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);
		pnMain.add(buildButton(),BorderLayout.SOUTH);
		return pnMain;

	}

	public void updateTable(String query) {


	}

	private void searchData()
	{	
		try {
			
			
			String field = (String) cbxField.getSelectedItem();
			if(field.equals("항구명"))
			{
				query="port_name";
			}else if(field.equals("항구명 약어"))
			{
				query="port_abbr";
			}

			query+=" like '"+txfSearch.getText()+"%'";
			this.updateTable(query);
			/*if(cbxField.getSelectedIndex()>0)
			{
				
			}
			else
			{
				txfSearch.setText("");
				query = null;
			}*/

			List li =baseDaoService.getSearchedPortAbbrList(query);

			searchTotalSize=li.size();

			totalSize = baseDaoService.getPortAbbrCount();

			createTable(li);
		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(PnPortAbbr.this, e.getMessage());
		}
	}
	private void createTable(List li) {

		model.clear();

		Iterator iter = li.iterator();
		while(iter.hasNext())
		{
			PortInfo portInfo = (PortInfo) iter.next();
			model.addRow(new Object[]{	portInfo.getPort_name(),
					portInfo.getPort_abbr()
			});
		}	


		lblTotal.setText(searchTotalSize+"/"+totalSize);
	}

	public void updateTable() {
	//	searchData();
		
		fnSearch();
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

			if(tblTable.getSelectedRow()==-1)
			{
				JOptionPane.showMessageDialog(PnPortAbbr.this, "항구명을 선택하십시요");
			}else
			{
				String portName=(String) tblTable.getValueAt(tblTable.getSelectedRow(), 0);
				KSGDialog dialog = new InsertPortAbbrInfoDialog(getBaseInfoUI(),portName);
				dialog.createAndUpdateUI();
				if(dialog.result==KSGDialog.SUCCESS)
				{
					searchData();
				}
			}

		}
		else if(command.equals("삭제"))
		{
			int row=tblTable.getSelectedRow();
			if(row<0)
				return;
			String data = (String) tblTable.getValueAt(row, 1);
			int result=JOptionPane.showConfirmDialog(this, data+"를 삭제 하시겠습니까?", "항구 약어 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{	
				try {
					HashMap<String, Object> param = new HashMap<String, Object>();
					
					param.put("port_abbr", data);
					
					int count=portService.deletePortAbbr(param);
					if(count>0)
					{
						int hrow = tableH.getSelectedRow();
						
						String port_name =(String) tableH.getValueAt(hrow,0);
						
						HashMap<String, Object> param2 = new HashMap<String, Object>();
						param2.put("port_name", port_name);
						tableH.setResultData(portService.selectPortAbbrList(param2));
						
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
				
				
//				try {
//					int count=baseDaoService.deletePortAbbr(data);
//					if(count>0)
//					{
//						searchData();
//					}
//				} catch (SQLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
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
				{
					String portName=(String) tblTable.getValueAt(row, 0);
					String portAbbr=(String) tblTable.getValueAt(row, 1);
					dialog = new UpdatePortAbbrInfoDialog(portName,portAbbr);
					dialog.createAndUpdateUI();
					if(dialog.result==KSGDialog.SUCCESS)
					{
						searchData();
					}
				}

			}
		}

	}

	@Override
	public String getOrderBy(TableColumnModel columnModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initTable() {
		model = new KSGTableModel();

		for(int i=0;i<columName.length;i++)
		{
			model.addColumn(columName[i]);
		}
		RowSorter<TableModel> sorter
		= new TableRowSorter<TableModel>(model);

		tblTable.setRowSorter(sorter);
		tblTable.setModel(model);


		tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnModel colmodel = tblTable.getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new KSGTableCellRenderer();
			//if(i==1)
			{
				renderer.setHorizontalAlignment(SwingConstants.LEFT);
			}
			namecol.setCellRenderer(renderer);	
		}

		colmodel.getColumn(0).setPreferredWidth(300);
		colmodel.getColumn(1).setPreferredWidth(300);


	}

	@Override
	public void fnSearch() {
		HashMap<String, Object> commandMap = new HashMap<String, Object>();
		
		//commandMap.put("port_name", param.get("port_name"));
		
		try {
			List li=portService.selectPortAbbrList(commandMap);
			tableH.setResultData(li);
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}


}
