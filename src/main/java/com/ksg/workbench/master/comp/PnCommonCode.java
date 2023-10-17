package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.dtp.api.control.CodeController;
import com.ksg.common.model.CommandMap;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.BasePop;
import com.ksg.workbench.master.dialog.CommCodeUpdatePop;
import com.ksg.workbench.master.dialog.CommonCodeInsertDialog;
import com.ksg.workbench.master.dialog.InsertCommonCodeDetailDialog;


/**

 * @FileName : PnCommonCode.java

 * @Date : 2021. 3. 18. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 공통 코드 관리

 */
@SuppressWarnings("serial")
public class PnCommonCode extends PnBase implements ActionListener{

	private JTextField txfCodeName;

	private KSGTablePanel tableH;

	private KSGTablePanel tableD;

	private SelectionListner selectionListner = new SelectionListner();

	private KSGGradientButton butSearch;

	public PnCommonCode(BaseInfoUI baseInfoUI) {

		super(baseInfoUI);
		
		this.initComp();

		this.setController(new CodeController());

		this.add(createCenter());

		this.addComponentListener(this);

		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}
	
	public PnCommonCode() {

		super();
		
		this.initComp();

		this.setController(new CodeController());

		this.add(createCenter());

		this.addComponentListener(this);

		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}

	private void initComp() {
		txfCodeName = new JTextField(10);
		
		butSearch = new KSGGradientButton("조회", "images/search3.png");

		butSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butSearch.addActionListener(this);
		
	}

	private Component createCenter() {

		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));	

		tableH = new KSGTablePanel("코드 목록");		

		KSGTableColumn Hcolumns[] = new KSGTableColumn[3];

		Hcolumns[0] = new KSGTableColumn();
		
		Hcolumns[0].columnField = "code_field";
		
		Hcolumns[0].columnName = "코드ID";
		
		Hcolumns[0].size = 100;

		Hcolumns[1] = new KSGTableColumn();
		
		Hcolumns[1].columnField = "code_name_kor";
		
		Hcolumns[1].columnName = "코드명";
		
		Hcolumns[1].size = 100;
		Hcolumns[1].ALIGNMENT = SwingConstants.LEFT;

		Hcolumns[2] = new KSGTableColumn();
		
		Hcolumns[2].columnField = "code_name";
		
		Hcolumns[2].columnName = "코드타입";
		Hcolumns[2].ALIGNMENT = SwingConstants.LEFT;
		
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
		Dcolumns[1].ALIGNMENT = SwingConstants.LEFT;
		
		Dcolumns[1].size = 200;	


		Dcolumns[2] = new KSGTableColumn();
		Dcolumns[2].columnField = "code_name";
		Dcolumns[2].columnName = "코드영문명";
		Dcolumns[2].size = 200;
		Dcolumns[2].ALIGNMENT = SwingConstants.LEFT;

		tableD.setColumnName(Dcolumns);

		tableD.initComp();

		tableD.setShowControl(true);

		tableD.addContorlListener(new CommonCodeDetileAction());		

		pnMain.add(tableH,BorderLayout.WEST);

		pnMain.add(tableD);

		pnMain.add(createSearch(),BorderLayout.NORTH);

		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));

		return pnMain;
	}

	private KSGPanel createSearch()
	{
		KSGPanel pnSearchAndCount = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		pnSearchAndCount.add(new JLabel("코드명:"));

		pnSearchAndCount.add(txfCodeName);

		pnSearchAndCount.add(butSearch);

		KSGPanel pnMain= new KSGPanel(new BorderLayout());

		pnMain.add(buildLine(),BorderLayout.SOUTH);

		pnMain.add(pnSearchAndCount,BorderLayout.EAST);

		pnMain.add(buildTitleIcon("코드 정보"),BorderLayout.WEST);

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
		CommandMap param = new CommandMap();

		HashMap<String, Object> commandMap = new HashMap<String, Object>();

		commandMap.put("code_type", CDENG);

		param.put("code_type", CDENG);

		callApi("selectCodeDetailList", param);
	}

	@Override
	public void fnSearch() {

		CommandMap param = new CommandMap();

		if(!"".equals(txfCodeName.getText()))
		{
			param.put("code_name_kor", txfCodeName.getText());
		}

		callApi("selectCodeList", param);
	}

	class CommonCodeAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			if(command.equals(KSGTablePanel.INSERT))
			{
				CommonCodeInsertDialog codeInsertPop = new CommonCodeInsertDialog();

				codeInsertPop.createAndUpdateUI();

				switch (codeInsertPop.result) {
				
				case BasePop.OK:
					
					fnSearch();
					
					break;
				case BasePop.CANCEL:					
					break;	

				default:
					break;
				}
			}

			else if(command.equals(KSGTablePanel.UPDATE))
			{
				int row = tableH.getSelectedRow();

				if(row<0) return;

				HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(row);

				CommCodeUpdatePop codeInsertPop = new CommCodeUpdatePop(item);

				codeInsertPop.showPop(PnCommonCode.this);

				switch (codeInsertPop.result) {
				
				case BasePop.OK:

					fnSearch();

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
				
				if(row<0)return;
				
				HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(row);

				CommandMap param = new CommandMap();
				
				param.put("code_field", item.get("code_field"));

				callApi("deleteCode", param);
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
				int tableHrow = tableH.getSelectedRow();
				
				if(tableHrow<0) return;

				InsertCommonCodeDetailDialog codeInsertPop = new InsertCommonCodeDetailDialog((HashMap<String, Object>) tableH.getValueAt(tableHrow));

				codeInsertPop.createAndUpdateUI();

				if(codeInsertPop.result == KSGDialog.SUCCESS) fnSearchDetail();
			}

			else if(command.equals(KSGTablePanel.DELETE))
			{
				int tableDrow = tableD.getSelectedRow();

				if(tableDrow<0) return;

				HashMap<String, Object> item=(HashMap<String, Object>) tableD.getValueAt(tableDrow);

				CommandMap param = new CommandMap();

				param.put("code_field",item.get("code_field"));
				
				param.put("code_name",item.get("code_name"));

				callApi("deleteCodeDetail", param);
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


	public void fnSearchDetail()
	{
		int row = tableH.getSelectedRow();

		logger.info("select row:{}",row);

		String CDENG = (String) tableH.getValueAt(tableH.getSelectedRow(), 2);
		
		fnSearchDetail(CDENG);
	}

	@Override
	public void componentShown(ComponentEvent e) {
		fnSearch();
	}

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId=(String) result.get("serviceId");

		List data = (List )result.get("data");

		if("selectCodeList".equals(serviceId))
		{	
			tableH.getSelectionModel().removeListSelectionListener(selectionListner);

			tableH.setResultData(data);

			tableH.setTotalCount(String.valueOf(data.size()));

			if(data.size()==0)tableH.changeSelection(0,0,false,false);

			if(data.size()==0)
			{
				//					lblArea.setText("");
				//					lblAreaCode.setText("");
				//					lblPationality.setText("");
				//					lblPortName.setText("");
				//					tableD.clearReslult();
			}
			else
			{
				tableH.changeSelection(0,0,false,false);
			}
			
			tableH.getSelectionModel().addListSelectionListener(selectionListner);
		}
		else if("deleteCode".equals(serviceId))
		{
			String code_filed = (String) result.get("code_field");

			NotificationManager.showNotification(String.format("(%s) 삭제 되었습니다.", code_filed));

			fnSearch();
		}

		else if("selectCodeDetailList".equals(serviceId))
		{	
			tableD.setResultData(data);
		}
		else if("deleteCodeDetail".equals(serviceId))
		{
			String code_name = (String) result.get("code_name");

			NotificationManager.showNotification(String.format("(%s) 삭제 되었습니다.", code_name));

			fnSearchDetail();
		}
	}
}