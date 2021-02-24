package com.ksg.adv.view.xls;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.ksg.adv.view.comp.SheetModel;

public class XLSSearchOptionPn extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private JList fileLi;
	private JTable			_tblSheetNameList;
	private static final long serialVersionUID = 1L;
	public XLSSearchOptionPn()
	{
		this.setLayout(new BorderLayout());

		add(buildNorth(),BorderLayout.NORTH);
		add(buildCenter(),BorderLayout.CENTER);

	}
	private Component buildCenter() {
		JPanel pnMain = new JPanel(new BorderLayout());
		
		
		Box pnFileAndSheetInfo = Box.createVerticalBox();
		pnFileAndSheetInfo.add(buildFileListPn());
		pnFileAndSheetInfo.add(buildSheetListPn());
		pnMain.add(pnFileAndSheetInfo,BorderLayout.WEST);
		pnMain.add(buildCompanyInfo(),BorderLayout.CENTER);
		

		return pnMain;
	}
	private Component buildCompanyInfo() {
		JPanel pnMain = new JPanel(new BorderLayout());
		pnMain.setBorder(BorderFactory.createEtchedBorder());
		JTable tblCompany = new JTable();
		DefaultTableModel defaultTableModel = new DefaultTableModel();
		defaultTableModel.addColumn("선택");
		defaultTableModel.addColumn("선사명");
		defaultTableModel.addColumn("Page");
		
		defaultTableModel.setRowCount(15);
		tblCompany.setModel(defaultTableModel);
		
		
		JTable tblXLS = new JTable();
		DefaultTableModel defaultTableModelXLS = new DefaultTableModel();
		defaultTableModelXLS.addColumn("선택");
		defaultTableModelXLS.addColumn("선사명");
		defaultTableModelXLS.addColumn("Page");
		
		defaultTableModelXLS.setRowCount(15);
		tblXLS.setModel(defaultTableModelXLS);
		
		
		
		
		
		pnMain.add(new JScrollPane(tblCompany));
		pnMain.add(new JScrollPane(tblXLS),BorderLayout.WEST);
		
		return pnMain;
	}
	private Component buildSheetListPn() {
		JPanel pnMain = new JPanel(new BorderLayout());
		pnMain.setBorder(BorderFactory.createTitledBorder("엑셀 Sheet 선택"));
		
		
		_tblSheetNameList= new JTable();
		DefaultTableModel defaultTableModel = new DefaultTableModel();
		defaultTableModel.setRowCount(10);
		defaultTableModel.addColumn("File 이름");

		defaultTableModel.addColumn("Sheet 이름");
		defaultTableModel.addColumn("사용");
		
		_tblSheetNameList.setModel(defaultTableModel);
		JPanel pnControl = new JPanel();
		pnControl.setLayout(new BorderLayout());

		JButton butOK = new JButton("확인");
		
		JButton butUp = new JButton("▲");
		butUp.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int row=_tblSheetNameList.getSelectedRow();
				int col=_tblSheetNameList.getSelectedColumn();
				if(row==-1)
					return ;
				SheetModel model = (SheetModel) _tblSheetNameList.getModel();
				model.upRow(row);
				if(row!=0)
					_tblSheetNameList.changeSelection(row-1, col, false, false);
					_tblSheetNameList.updateUI();
			}});
		
		JButton butDown = new JButton("▼");
		butDown.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				int row=_tblSheetNameList.getSelectedRow();
				int col=_tblSheetNameList.getSelectedColumn();
				if(row==-1)
					return ;
				SheetModel model = (SheetModel) _tblSheetNameList.getModel();
				model.downRow(row);
				
				if(row<_tblSheetNameList.getRowCount()-1)
				_tblSheetNameList.changeSelection(row+1, col, false, false);
				_tblSheetNameList.updateUI();
			}});
		JPanel pnRight = new JPanel();
		pnRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnRight.add(butUp);
		pnRight.add(butDown);
		pnRight.add(butOK);
		pnControl.add(pnRight,BorderLayout.EAST);
		JCheckBox cbxtotal = new JCheckBox("전체선택");
		cbxtotal.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				System.out.println(e.getStateChange());
				switch (e.getStateChange()) {
				case ItemEvent.DESELECTED:
					int row =_tblSheetNameList.getRowCount();
					for(int i=0;i<row;i++)
					{
						Boolean use=(Boolean) _tblSheetNameList.getValueAt(i, 3);
						
						_tblSheetNameList.setValueAt(false, i, 3);
					}
					break;
				case ItemEvent.SELECTED:
					int row2 =_tblSheetNameList.getRowCount();
					for(int i=0;i<row2;i++)
					{
						Boolean use=(Boolean) _tblSheetNameList.getValueAt(i, 3);
						
						_tblSheetNameList.setValueAt(true, i, 3);
					}
					break;
				default:
					break;
				}
				_tblSheetNameList.updateUI();

			}

		});
		pnControl.add(cbxtotal,BorderLayout.WEST);
		
		
		pnMain.add(new JLabel("불러올 엑셀 Sheet를 선택 하십시요"),BorderLayout.NORTH);
		JScrollPane spSheetList = new JScrollPane(_tblSheetNameList);
		JPanel pnTemp = new JPanel();
		pnTemp.add(spSheetList);
		pnMain.add(pnTemp,BorderLayout.CENTER);
		pnMain.add(pnControl,BorderLayout.SOUTH);
		
		return pnMain;
	}
	private Component buildNorth()
	{
		JPanel pnMain = new JPanel(new BorderLayout());

		JPanel pnSearOption1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnSearOption2 = new JPanel(new BorderLayout());

		JLabel lblCompany = new JLabel("선사명 : ");		
		JTextField txfCompany = new JTextField(12);		
		JLabel lblPage = new JLabel("페이지 : ");
		JTextField txfPage = new JTextField(12);		
		JButton butSearch1 =new JButton("찾기");
		butSearch1.setActionCommand("Company Search");
		JButton butSearch2 =new JButton("찾기");
		butSearch2.setActionCommand("Page Search");
		JButton butXLS =new JButton("엑셀파일(E)");
		butXLS.setActionCommand("엑셀파일");

		JButton butXLSSheet =new JButton("Sheet 선택(E)");
		butXLSSheet.setActionCommand("Sheet 선택");
		JTextField txfXLLInfo = new JTextField(35);

		pnSearOption1.add(lblCompany);		
		pnSearOption1.add(txfCompany);
		pnSearOption1.add(butSearch1);
		pnSearOption1.add(lblPage);
		pnSearOption1.add(txfPage);
		pnSearOption1.add(butSearch2);

		pnSearOption2.add(txfXLLInfo,BorderLayout.CENTER);

		JPanel pnRight = new JPanel();
		pnRight.add(butXLS);
		pnRight.add(butXLSSheet);
		pnSearOption2.add(pnRight,BorderLayout.EAST);


		pnMain.add(pnSearOption1,BorderLayout.NORTH);
//		pnMain.add(pnSearOption2,BorderLayout.SOUTH);
		
		

		return pnMain;
	}
	
	private JPanel buildFileListPn() {
		JPanel pnMain = new JPanel();
		pnMain.setBorder(BorderFactory.createTitledBorder("파일 선택"));

		JButton butFile = new JButton("추가(A)");
		JButton butDel = new JButton("삭제(D)");
		JButton butUp = new JButton("위로");
		JButton butDown = new JButton("아래로");
		
		
		butFile.setMnemonic(KeyEvent.VK_A);
		butDel.setMnemonic(KeyEvent.VK_D);
		
		butFile.setActionCommand("파일추가");
		butDel.setActionCommand("파일삭제");
		butUp.setActionCommand("파일위로");
		butDown.setActionCommand("파일아래");
		
		butFile.addActionListener(this);
		butDel.addActionListener(this);
		butUp.addActionListener(this);
		butDown.addActionListener(this);

		JPanel pnButList = new JPanel();

		pnButList.setLayout(new GridLayout(0,1));

		pnButList.add(butFile);		
		
		pnButList.add(butDel);		
		
		pnButList.add(butUp);		

		pnButList.add(butDown);

		JPanel pnFile = new JPanel();
		pnFile.setLayout(new BorderLayout());
		fileLi = new JList();

		fileLi.setModel(new DefaultListModel());
		JScrollPane scrollPane = new JScrollPane(fileLi);
		pnFile.add(scrollPane);

		pnFile.add(pnButList,BorderLayout.EAST);

		
		pnMain.add(pnFile);
		return pnMain;
	}

	
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Company Search"))
		{

		}else if(command.equals("Page Search"))
		{

		}
		else if(command.equals("Page Search"))
		{

		}
		else if(command.equals("엑셀파일"))
		{

		}
		else if(command.equals("Sheet 선택"))
		{

		}
		else if(command.equals("파일추가"))
		{
			fileAddAction(fileLi,_tblSheetNameList);
		}
		else if(command.equals("파일삭제"))
		{
			fileDelAction(fileLi,_tblSheetNameList);
		}
		else if(command.equals("파일위로"))
		{
			fileUPAction(fileLi);
		}
		else if(command.equals("파일아래로"))
		{
			fileDownAction(fileLi);
		}



	}
	private void fileDownAction(JList fileLi2) {
		// TODO Auto-generated method stub
		
	}
	private void fileUPAction(JList fileLi2) {
		// TODO Auto-generated method stub
		
	}
	private void fileDelAction(JList fileLi2, JTable sheetNameList) {
		// TODO Auto-generated method stub
		
	}
	private void fileAddAction(JList fileLi2, JTable sheetNameList) {
		// TODO Auto-generated method stub
		
	}

}

