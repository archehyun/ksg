package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.ksg.dao.impl.AreaDAOImpl;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPageTablePanel;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.UpdateAreaInfodialog;



/**

  * @FileName : PnArea.java

  * @Date : 2021. 2. 25. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 지역 정보관리 화면

  */
public class PnArea extends PnBase implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox cbxField;

	private JTextField txfSearch;

	private JLabel lblTable,lblTotal;
	
	KSGTablePanel tableH;

	private String columName[] = {"코드","지역명","지역코드"};
	
	AreaServiceImpl areaService = new AreaServiceImpl();
	
	AreaDAOImpl areaDAO = new AreaDAOImpl();


	public PnArea(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		
		this.addComponentListener(this);
		this.add(buildCenter());	
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}

	private KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		tableH = new KSGTablePanel("지역 정보");
		tableH.addMouseListener(new TableSelectListner());
		
		
		//  TODO 컬럼 정렬
		KSGTableColumn columns[] = new KSGTableColumn[3];

		columns[0] = new KSGTableColumn();
		columns[0].columnField = "area_code";
		columns[0].columnName = "지역코드";
		columns[0].size = 75;

		columns[1] = new KSGTableColumn();
		columns[1].columnField = "area_name";
		columns[1].columnName = "지역명";
		columns[1].size = 300;

		columns[2] = new KSGTableColumn();
		columns[2].columnField = "area_book_code";
		columns[2].columnName = "북코드";
		columns[2].size = 75;

		tableH.setColumnName(columns);
		
		
		
		tableH.initComp();
		tableH.setShowControl(true);
		tableH.addContorlListener(this);
		
		
		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);
		pnMain.add(tableH);
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));
		
		return pnMain;

	}
	private KSGPanel buildSearchPanel() {
		KSGPanel pnSearch = new KSGPanel();
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

		KSGPanel pnCountInfo = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		pnCountInfo.add(lblTotal);
		pnCountInfo.add(label);
		pnSearchAndCount.add(pnCountInfo);

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
		String command =e.getActionCommand();
		if(command.equals("검색"))
		{
			fnSearch();
		}
		else if(command.equals(KSGPageTablePanel.DELETE))
		{
			
			
			int row =tableH.getSelectedRow();
			if(row<0)
				return;
			String data = (String) tableH.getValueAt(row, 1);
			int result=JOptionPane.showConfirmDialog(null, data+"를 삭제 하시겠습니까?", "지역 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{						
				try {
					HashMap<String, Object> param = new HashMap<String, Object>();
					
					param.put("area_name", data);
					int count=areaDAO.deleteArea(param);
					if(count>0)
					{
						fnSearch();
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else if(command.equals(KSGPageTablePanel.INSERT))
		{
			KSGDialog dialog = new UpdateAreaInfodialog(UpdateAreaInfodialog.INSERT);
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
				
				HashMap<String , Object> param = (HashMap<String, Object>) tableH.getValueAt(row);
				dialog = new UpdateAreaInfodialog(UpdateAreaInfodialog.UPDATE,param);
				dialog.createAndUpdateUI();
				
				int result = dialog.result;
				if(result== KSGDialog.SUCCESS)
				{
					fnSearch();	
				}
			}
		}
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
	public void fnSearch() {
		try {
			List li = areaService.selectAreaList(null);
			tableH.setResultData(li);
			lblTotal.setText(li.size()+" ");
		} catch (SQLException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		}	
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		fnSearch();
		
	}

	@Override
	public void updateView() {
		
		
	}



}
