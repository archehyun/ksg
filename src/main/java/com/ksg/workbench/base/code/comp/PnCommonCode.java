package com.ksg.workbench.base.code.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.comp.PnBase;
import com.ksg.workbench.base.dialog.BasePop;
import com.ksg.workbench.base.dialog.CommCodeUpdatePop;
import com.ksg.workbench.base.dialog.CommonCodeDetailInsertPop;
import com.ksg.workbench.base.dialog.CommonCodeInsertPop;


/**

  * @FileName : PnCommonCode.java

  * @Date : 2021. 3. 18. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 공통 코드 관리

  */
@SuppressWarnings("serial")
public class PnCommonCode extends PnBase implements ActionListener, ComponentListener{
	
	JTextField txfCodeName;
	
	KSGTablePanel tableH;
	
	KSGTablePanel tableD;
	
	CodeServiceImpl codeService;
	
	SelectionListner selectionListner = new SelectionListner();

	private JLabel lblTable;
	
	private List<HashMap<String, Object>> master;

	public PnCommonCode(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		
		codeService = new CodeServiceImpl();		
		
		this.add(createCenter());
		this.addComponentListener(this);
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		
		
	}

	private Component createCenter() {
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));	
		
		tableH = new KSGTablePanel("코드 목록");		
		
		KSGTableColumn Hcolumns[] = new KSGTableColumn[3];

		Hcolumns[0] = new KSGTableColumn();
		Hcolumns[0].columnField = "CD_ID";
		Hcolumns[0].columnName = "코드ID";
		Hcolumns[0].size = 100;
		
		Hcolumns[1] = new KSGTableColumn();
		Hcolumns[1].columnField = "CD_NM";
		Hcolumns[1].columnName = "코드명";
		Hcolumns[1].size = 100;		
		
		Hcolumns[2] = new KSGTableColumn();
		Hcolumns[2].columnField = "CD_ENG";
		Hcolumns[2].columnName = "코드타입";
		Hcolumns[2].size = 100;
		
		tableH.setColumnName(Hcolumns);
		
		tableH.initComp();
		
		tableH.addContorlListener(new CommonCodeAction());
		
		tableH.setShowControl(true);
		
		tableH.getSelectionModel().addListSelectionListener(selectionListner);
		
		tableD = new KSGTablePanel("코드 상세 목록");
		
		
		KSGTableColumn Dcolumns[] = new KSGTableColumn[3];

		
		Dcolumns[0] = new KSGTableColumn();
		Dcolumns[0].columnField = "code_field";
		Dcolumns[0].columnName = "코드";
		Dcolumns[0].size = 200;
		
		Dcolumns[1] = new KSGTableColumn();
		Dcolumns[1].columnField = "code_name_kor";
		Dcolumns[1].columnName = "코드명";
		Dcolumns[1].size = 200;	
			
		
		Dcolumns[2] = new KSGTableColumn();
		Dcolumns[2].columnField = "code_name";
		Dcolumns[2].columnName = "코드영문명";
		Dcolumns[2].size = 200;
		tableD.setColumnName(Dcolumns);
		
		tableD.initComp();
		tableD.setShowControl(true);
		tableD.addContorlListener(new CommonCodeDetileAction());		
		
		pnMain.add(tableH,BorderLayout.WEST);
		
		pnMain.add(tableD);
		
		pnMain.add(createSerch(),BorderLayout.NORTH);
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));
		
		return pnMain;
	}
	
	private KSGPanel createSerch()
	{		
		KSGPanel pnMain = new KSGPanel();
		
		
		lblTable = new JLabel("코드정보");
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("돋움",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));
		
		pnMain.setLayout(new BorderLayout());
		
		KSGPanel pnLeft = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		txfCodeName = new JTextField(10);
		
		pnLeft.add(lblTable);
		pnLeft.add(new JLabel("코드명:"));
		pnLeft.add(txfCodeName);
		
		
		KSGPanel pnRight = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		
		pnRight.add(new JLabel("코드명:"));
		pnRight.add(txfCodeName);
		
		JButton butSearch = new JButton("조회");
		butSearch.addActionListener(this);
		
		pnRight.add(butSearch);
		
		pnMain.add(pnLeft,BorderLayout.LINE_START);
		
		
		pnMain.add(pnRight,BorderLayout.LINE_END);
		
		return pnMain;
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("조회"))
		{	
			this.fnSearch();
		}
		
	}
	
	public void fnSearchDetail(String CDENG)
	{

		try {
			HashMap<String, Object> commandMap = new HashMap<String, Object>();
			
			commandMap.put("code_type", CDENG);
			
			HashMap<String, Object> result = (HashMap<String, Object>) codeService.selectCodeDList(commandMap);
			
			int total = (Integer) result.get("total");
			
			tableD.setResultData(result);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	

	@Override
	public void fnSearch() {
		
		
		tableH.getSelectionModel().removeListSelectionListener(selectionListner);
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		
		if(!"".equals(txfCodeName.getText()))
		{
			param.put("CD_NM", txfCodeName.getText());
		}	
		
		try {
			
			logger.info("param:{}",param);
		HashMap<String, Object> result = (HashMap<String, Object>) codeService.selectCodeHList(param);
		
		tableH.setResultData(result);
		
		tableH.getSelectionModel().addListSelectionListener(selectionListner);
		
		
		
		master = (List) result.get("master");

		if(master.size()==0)
		{
			/*lblArea.setText("");
			lblAreaCode.setText("");
			lblPationality.setText("");
			lblPortName.setText("");
			tableD.clearReslult();*/
			
			tableD.clearResult();
		}
		else
		{
			tableH.changeSelection(0,0,false,false);
		}
		
		
		}catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	class CommonCodeAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			
			if(command.equals(KSGTablePanel.INSERT))
			{
				CommonCodeInsertPop codeInsertPop = new CommonCodeInsertPop();
				
				codeInsertPop.showPop(PnCommonCode.this);
				switch (codeInsertPop.result) {
				case BasePop.OK:
					
					break;
				case BasePop.CANCEL:					
					break;	

				default:
					//fnSearch();
					break;
				}
			}
			
			else if(command.equals(KSGTablePanel.UPDATE))
			{
				
				int row = tableH.getSelectedRow();
				if(row<0)
					return;
				HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(row);
				
				CommCodeUpdatePop codeInsertPop = new CommCodeUpdatePop(item);
				codeInsertPop.showPop(PnCommonCode.this);
				
				switch (codeInsertPop.result) {
				case BasePop.OK:
					
					//tableH.changeSelection(row, 0, false, extend);
					break;
				case BasePop.CANCEL:					
					break;	

				default:
					//fnSearch();
					break;
				}
			}
			
			else if(command.equals(KSGTablePanel.DELETE))
			{
				int row = tableH.getSelectedRow();
				if(row<0)
					return;
				HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(row);
				try {
				codeService.deleteCodeH(item);
				
				fnSearch();
				
				}catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getMessage());
					
				}
			}
			
		}
		
	}
	
	class CommonCodeDetileAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			
			if(command.equals(KSGTablePanel.INSERT))
			{
				int row=tableH.getSelectedRow();
				if(row<0)
					return;
				CommonCodeDetailInsertPop codeInsertPop = new CommonCodeDetailInsertPop((HashMap<String, Object>) tableH.getValueAt(row));
				
				codeInsertPop.showPop(PnCommonCode.this);
			}
			
			else if(command.equals(KSGTablePanel.DELETE))
			{
				int row = tableD.getSelectedRow();
				if(row<0)
					return;
				HashMap<String, Object> item=(HashMap<String, Object>) tableD.getValueAt(row);
				try {
				codeService.deleteCodeD(item);
				
				fnSearchDetail((String) item.get("code_type"));
				
				}catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getMessage());
					
				}
				
			}
			
			else if(command.equals(KSGTablePanel.UPDATE))
			{
				
			}
			
		}
		
	}
	class SelectionListner implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			if(!e.getValueIsAdjusting())
			{
				
				int row = tableH.getSelectedRow();
				logger.info("select row:{}",row);
					
				String CDENG = (String) tableH.getValueAt(tableH.getSelectedRow(), 2);
				
				fnSearchDetail(CDENG);
				
				
			}
		}
	}


	@Override
	public void componentShown(ComponentEvent e) {
		fnSearch();
		
	}



}
