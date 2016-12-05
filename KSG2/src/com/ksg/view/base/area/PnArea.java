package com.ksg.view.base.area;

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
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.domain.AreaInfo;
import com.ksg.view.KSGViewParameter;
import com.ksg.view.base.PnBase;
import com.ksg.view.base.dialog.InsertAreaInfodialog;
import com.ksg.view.base.dialog.UpdateAreaInfodialog;
import com.ksg.view.comp.EvenOddRenderer;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.comp.KSGTableModel;


public class PnArea extends PnBase implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JComboBox cbxField;
	
	private JTextField txfSearch;
	
	private JLabel lblTable,lblTotal;
	
	private String columName[] = {"코드","지역명","지역코드"};
	
	public PnArea() {
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
		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);
		pnMain.add(buildButton(),BorderLayout.SOUTH);
		return pnMain;

	}
	private JPanel buildSearchPanel() {
		JPanel pnSearch = new JPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lblTotal = new JLabel();		
		lblTable = new JLabel("지역 정보");
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("돋움",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));
		
		JLabel lbl = new JLabel("필드명 : ");
		cbxField = new JComboBox();
		txfSearch = new JTextField(15);
		JLabel label = new JLabel("개 항목");
		JButton butUpSearch = new JButton("검색");

		cbxField.setPreferredSize(new Dimension(150,23));
		
		lbl.setVisible(false);	
		cbxField.setVisible(false);
		txfSearch.setVisible(false);
		butUpSearch.setVisible(false);
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
		String command =e.getActionCommand();
		if(command.equals("검색"))
		{
			searchData(null);
		}
		else if(command.equals("삭제"))
		{
			int row=_tblTable.getSelectedRow();
			if(row<0)
				return;

			String data = (String) _tblTable.getValueAt(row, 1);
			int result=JOptionPane.showConfirmDialog(null, data+"를 삭제 하시겠습니까?", "지역 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{						
				try {
					int count=baseService.deleteArea(data);
					if(count>0)
					{
						searchData(null);
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			
		}
		else if(command.equals("신규"))
		{
			KSGDialog dialog = new InsertAreaInfodialog();
			dialog.createAndUpdateUI();
			if(dialog.result==KSGDialog.SUCCESS)
			{
				searchData(null);
			}
		}

	}
	public void updateTable(String query)
	{
		
		searchData(query);

	}

	private void searchData(String query) {
		
		DefaultTableModel model = new KSGTableModel();
		
		for(int i=0;i<columName.length;i++)
		{
			model.addColumn(columName[i]);
		}
		
		try {
			List li =baseService.getSearchedAreaList(query);
			
			Iterator iter = li.iterator();
			searchTotalSize=li.size();
			totalSize = baseService.getAreaCount();
			while(iter.hasNext())
			{
				AreaInfo areaInfo = (AreaInfo) iter.next();
				model.addRow(new Object[]{	areaInfo.getArea_book_code(),
											areaInfo.getArea_name(),
											areaInfo.getArea_code()});
		
		
		_tblTable.setModel(model);
		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumnModel colmodel = _tblTable.getColumnModel();
		TableColumn areacol;

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new EvenOddRenderer();
			if(i==1)
			{
				renderer.setHorizontalAlignment(SwingConstants.LEFT);
			}
			else
			{
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
			}
			namecol.setCellRenderer(renderer);	
		}
		areacol = colmodel.getColumn(1);
		areacol.setPreferredWidth(400);
		areacol = colmodel.getColumn(2);
		areacol.setPreferredWidth(100);
		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		lblTotal.setText(searchTotalSize+"/"+totalSize);
	}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					int areaBookCode=(Integer) _tblTable.getValueAt(row, 0);
					String area_name=(String) _tblTable.getValueAt(row, 1);
					String area_code=(String) _tblTable.getValueAt(row, 2);

					AreaInfo info = new AreaInfo();
					info.setArea_name(area_name);
					info.setArea_code(area_code);
					info.setArea_book_code(areaBookCode);
					dialog = new UpdateAreaInfodialog(info);
					dialog.createAndUpdateUI();;
					if(dialog.result==KSGDialog.SUCCESS)
					{
						searchData(null);
					}
				}
				
			}
		}

	}

	@Override
	public void updateTable() {
		searchData(null);
		
	}
	class MyTableModelListener implements TableModelListener {
		  JTable table;

		  MyTableModelListener(JTable table) {
		    this.table = table;
		  }

		  public void tableChanged(TableModelEvent e) {
		    int firstRow = e.getFirstRow();
		    int lastRow = e.getLastRow();
		    int index = e.getColumn();

		    
		    switch (e.getType()) {
		    case TableModelEvent.INSERT:
		      for (int i = firstRow; i <= lastRow; i++) {
		        System.out.println(i);
		      }
		      break;
		    case TableModelEvent.UPDATE:
		      if (firstRow == TableModelEvent.HEADER_ROW) {
		        if (index == TableModelEvent.ALL_COLUMNS) {
		          System.out.println("A column was added");
		        } else {
		          System.out.println(index + "in header changed");
		        }
		      } else {
		        for (int i = firstRow; i <= lastRow; i++) {
		          if (index == TableModelEvent.ALL_COLUMNS) {
		            System.out.println("All columns have changed");
		          } else {
		            System.out.println(index);
		          }
		        }
		      }
		      break;
		    case TableModelEvent.DELETE:
		      for (int i = firstRow; i <= lastRow; i++) {
		        System.out.println(i);
		      }
		      break;
		    }
		  }
		}

	@Override
	public String getOrderBy(TableColumnModel columnModel) {
		// TODO Auto-generated method stub
		return null;
	}


}
