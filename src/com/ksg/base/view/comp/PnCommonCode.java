package com.ksg.base.view.comp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

import com.ksg.base.service.CodeService;
import com.ksg.base.view.BaseInfoUI;
import com.ksg.common.comp.KSGPanel;
import com.ksg.common.comp.KSGTableColumn;
import com.ksg.common.comp.KSGTablePanel;


@SuppressWarnings("serial")
public class PnCommonCode extends PnBase implements ActionListener{
	
	JTextField txfCodeName;
	KSGTablePanel tableH;
	
	KSGTablePanel tableD;
	
	CodeService codeService;

	public PnCommonCode(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		
		codeService = new CodeService();
		
		
		this.add(createCenter());
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
		Hcolumns[2].columnName = "코드영문명";
		Hcolumns[2].size = 100;
		
		tableH.setColumnName(Hcolumns);
		
		tableH.initComp();
		
		
		tableH.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				if(!e.getValueIsAdjusting())
				{
					String CDID=(String) tableH.getValueAt(e.getFirstIndex(), 0);
					
					String CDNM = (String) tableH.getValueAt(e.getFirstIndex(), 1);
					
					String CDENG = (String) tableH.getValueAt(e.getFirstIndex(), 2);					
					
					
					/*lblPortName.setText(portName);
					lblPationality.setText(pationality);
					lblArea.setText(area);
					lblAreaCode.setText(areaCode);*/
					
					HashMap<String, Object> commandMap = new HashMap<String, Object>();
					
					commandMap.put("code_type", CDENG);
					
					try {
						HashMap<String, Object> result = (HashMap<String, Object>) codeService.selectCodeDList(commandMap);
						
						int total = (Integer) result.get("total");
						System.out.println(CDENG+", total:"+total);
						
						tableD.setResultData(result);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		});
		
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
		
		pnMain.add(tableH,BorderLayout.WEST);
		
		pnMain.add(tableD);
		
		pnMain.add(createSerch(),BorderLayout.NORTH);
		
		
		return pnMain;
	}
	
	private JPanel createSerch()
	{		
		JPanel pnMain = new JPanel();
		
		pnMain.setLayout(new BorderLayout());
		
		JPanel pnLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnLeft.add(new JLabel("코드명"));
		
		txfCodeName = new JTextField(10);
		
		pnLeft.add(txfCodeName);
		
		JPanel pnRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton butSearch = new JButton("조회");
		butSearch.addActionListener(this);
		
		pnRight.add(butSearch);
		
		pnMain.add(pnLeft,BorderLayout.LINE_START);
		
		pnMain.add(pnRight,BorderLayout.LINE_END);
		
		return pnMain;
	}

	@Override
	public void updateTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("조회"))
		{
			this.fnSearch();		
			
		}
		
	}
	
	public void fnSearchDetail()
	{
		
	}

	@Override
	public void updateTable(String query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getOrderBy(TableColumnModel columnModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fnSearch() {
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		
		if(!"".equals(txfCodeName.getText()))
		{
			param.put("CD_NM", txfCodeName.getText());
		}	
		
		try {
		HashMap<String, Object> result = (HashMap<String, Object>) codeService.selectCodeHList(param);
		
		tableH.setResultData(result);
		
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		
		
	}

}
