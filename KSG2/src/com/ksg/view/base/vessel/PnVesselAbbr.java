package com.ksg.view.base.vessel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.ksg.domain.Vessel;
import com.ksg.view.KSGViewParameter;
import com.ksg.view.base.PnBase;
import com.ksg.view.base.vessel.dialog.InsertVesselAbbrInfoDialog;
import com.ksg.view.base.vessel.dialog.UpdateVesselAbbrInfoDialog;
import com.ksg.view.comp.EvenOddRenderer;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.comp.KSGTableModel;


/**
 * 선박 약어 정보 관리
 * @author archehyun
 *
 */
public class PnVesselAbbr extends PnBase implements ActionListener{
	private String[] columName = {"선박명", "선박명 약어"};
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox cbxField;
	private JTextField txfSearch;
	private JLabel lblTable,lblTotal;
	public PnVesselAbbr() {

		super();


		this.add(buildCenter());

	}

	private JPanel buildCenter()
	{
		JPanel pnMain = new JPanel(new BorderLayout());
		_tblTable = new JTable();

		_tblTable.addMouseListener(new TableSelectListner());
		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_tblTable.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		pnMain.add(new JScrollPane(_tblTable));
		pnMain.add(buildButton(),BorderLayout.SOUTH);
		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);
		return pnMain;

	}
	private JPanel buildSearchPanel() {
		JPanel pnSearch = new JPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lblTotal = new JLabel();
		lblTable = new JLabel("선박 약어 정보");
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("돋움",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));
		JLabel lbl = new JLabel("필드명 : ");
		cbxField = new JComboBox();
		cbxField.addItem("전체");
		cbxField.addItem("선박명");
		cbxField.addItem("선박명 약어");
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
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("검색"))
		{
			searchData();
		}
		else if(command.equals("신규"))
		{
			if(_tblTable.getSelectedRow()==-1)
			{
				JOptionPane.showMessageDialog(PnVesselAbbr.this, "선박명을 선택하십시요");
			}
			else
			{
				String VesselName=(String) _tblTable.getValueAt(_tblTable.getSelectedRow(), 0);
				KSGDialog dialog = new InsertVesselAbbrInfoDialog(VesselName);
				dialog.createAndUpdateUI();
				if(dialog.result==KSGDialog.SUCCESS)
				{
					searchData();
				}
			}
		}
		else if(command.equals("삭제"))
		{
			int row=_tblTable.getSelectedRow();
			if(row<0)
				return;

			String data= (String) _tblTable.getValueAt(row, 1);
			String vessel_name = (String) _tblTable.getValueAt(row, 0);
			String vessel_abbr = (String) _tblTable.getValueAt(row, 1);
			Vessel op = new Vessel();
			op.setVessel_name(vessel_name);
			op.setVessel_abbr(vessel_abbr);

			int result=JOptionPane.showConfirmDialog(null, data+"를 삭제 하시겠습니까?", "선박명 약어 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{	
				try {
					int count=baseService.deleteVesselAbbr(op);
					if(count>0)
						searchData();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void updateTable(String query) {
		searchData();	

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
			Vessel vesselInfo = (Vessel) iter.next();
			model.addRow(new Object[]{	vesselInfo.getVessel_name(),
					vesselInfo.getVessel_abbr()
			});
		}	
		RowSorter<TableModel> sorter= new TableRowSorter<TableModel>(model);
		_tblTable.setRowSorter(sorter);
		_tblTable.setModel(model);

		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnModel colmodel = _tblTable.getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new EvenOddRenderer();
			//if(i==1)
			{
				renderer.setHorizontalAlignment(SwingConstants.LEFT);
			}
			namecol.setCellRenderer(renderer);	
		}
		colmodel.getColumn(0).setPreferredWidth(400);
		colmodel.getColumn(1).setPreferredWidth(400);
		lblTotal.setText(searchTotalSize+"/"+totalSize);
	}

	private void searchData() {

		try {
			if(cbxField.getSelectedIndex()>0)
			{
				String field=(String) cbxField.getSelectedItem();


				if(field.equals("선박명"))
				{
					query="vessel_name";
				}else if(field.equals("선박명 약어"))
				{
					query="vessel_abbr";
				}

				query+=" like '%"+txfSearch.getText()+"%'";
			}
			else
			{
				query=null;
				txfSearch.setText("");
			}


			List li =baseService.getSearchedVesselAbbrList(query);
			searchTotalSize=li.size();
			totalSize = baseService.getVesselAbbrCount();
			createTable(li);
		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(PnVesselAbbr.this, e.getMessage());
		}

	}

	@Override
	public void updateTable() {

		searchData();	
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

					String VesselName2=(String) _tblTable.getValueAt(row, 0);
					String VesselAbbr=(String) _tblTable.getValueAt(row, 1);
					Vessel vessel2 = new Vessel();
					vessel2.setVessel_name(VesselName2);
					vessel2.setVessel_abbr(VesselAbbr);
					dialog = new UpdateVesselAbbrInfoDialog(vessel2);
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

}
