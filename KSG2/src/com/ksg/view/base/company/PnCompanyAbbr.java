package com.ksg.view.base.company;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.domain.Company;
import com.ksg.view.KSGViewParameter;
import com.ksg.view.base.PnBase;
import com.ksg.view.comp.EvenOddRenderer;
import com.ksg.view.comp.KSGTableModel;


public class PnCompanyAbbr extends PnBase implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox cbxField;
	private JTextField txfSearch;
	private JLabel lblTable,lblTotal;
	private JTable	_tblTable;
	private String[] columName = {"항구명","항구명 약어"};
	public PnCompanyAbbr() {

		super();

		this.add(buildSearchPanel(),BorderLayout.NORTH);
		this.add(buildCenter());
		this.add(buildButton(),BorderLayout.SOUTH);
	}

	private JPanel buildCenter()
	{
		JPanel pnMain = new JPanel(new BorderLayout());
		_tblTable = new JTable();

		//tblTable.addMouseListener(new TableSelectListner());
		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_tblTable.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		pnMain.add(new JScrollPane(_tblTable));
		return pnMain;

	}
	private JPanel buildSearchPanel() {
		JPanel pnSearch = new JPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lblTotal = new JLabel();
		lblTable = new JLabel("선사 약어 정보");
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("돋움",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));
		JLabel lbl = new JLabel("필드명 : ");
		cbxField = new JComboBox();
		txfSearch = new JTextField(15);
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
	/**
	 * @return
	 */
	private JPanel buildButton()
	{
		JPanel pnButtom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel pnButtomRight = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton butDel = new JButton("삭제");
		JButton butArrange = new JButton("정렬");
		JButton butNew = new JButton("신규");
		pnButtomRight.setBorder(BorderFactory.createEtchedBorder());		
		butDel.addActionListener(this);
		butNew.addActionListener(this);
		butArrange.addActionListener(this);

		pnButtomRight.add(butArrange);
		pnButtomRight.add(butNew);
		pnButtomRight.add(butDel);
		pnButtom.add(pnButtomRight);
		return pnButtom;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("검색"))
		{
			searchData();
		}

	}
	@Override
	public void updateTable(String query) {
		

	}
	private void searchData()
	{
		try {
			if(cbxField.getSelectedIndex()>0)
			{
				String field=(String) cbxField.getSelectedItem();

				if(field.equals("지역명"))
				{
					query="company_name";
				}else if(field.equals("지역코드"))
				{
					query="company_abbr";
				}

				query+=" like '"+txfSearch.getText()+"%'";

			}
			else
			{
				query=null;
				txfSearch.setText("");;
			}


			List li =baseService.getSearchedCompanyList(query);

			searchTotalSize=li.size();
			totalSize = baseService.getCompanyCount();
			createTable(li);
		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(PnCompanyAbbr.this, e.getMessage());
		}
	}
	private void createTable(List li)
	{
		model = new KSGTableModel();

		for(int i=0;i<columName.length;i++)
		{
			model.addColumn(columName[i]);
		}

		Iterator iter = li.iterator();

		while(iter.hasNext())
		{
			Company companyInfo = (Company) iter.next();
			model.addRow(new Object[]{	companyInfo.getCompany_name(),
					companyInfo.getCompany_abbr(),
					companyInfo.getAgent_name(),
					companyInfo.getAgent_abbr(),
					companyInfo.getContents()});
		}	

		_tblTable.setModel(model);

		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumnModel colmodel = _tblTable.getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new EvenOddRenderer();
			if(i==1)
			{
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
			}
			namecol.setCellRenderer(renderer);	
		}
		lblTotal.setText(searchTotalSize+"/"+totalSize);
	}

	@Override
	public void updateTable() {
		searchData();


	}

	@Override
	public String getOrderBy(TableColumnModel columnModel) {
		// TODO Auto-generated method stub
		return null;
	}

}
