package com.ksg.workbench.master.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.dtp.api.control.CodeController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;

/**

  * @FileName : CommonCodeInsertPop.java

  * @Date : 2021. 3. 18. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :

  */
@SuppressWarnings("serial")
public class CommonCodeInsertPop extends BaseInfoDialog{
	
	CodeServiceImpl codeService;
	
	private JTextField txfCodeID;
	
	private JTextField txfCodeNM;
	
	private JTextField txfCodeENG;	
	
	public CommonCodeInsertPop() {
		
		this.setTitle("코드 정보 추가");
		
		codeService = new CodeServiceImpl();
		
		this.setController( new CodeController());

	}
	
	public KSGPanel buildCenter()
	{
			
		txfCodeID = new JTextField(15);
		
		txfCodeNM = new JTextField(15);
		
		txfCodeENG = new JTextField(15);
		
		
		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		pnCenter.add(createFormItem(txfCodeID,"코드ID"));
		
		pnCenter.add(createFormItem(txfCodeNM, "코드명"));
		
		pnCenter.add(createFormItem(txfCodeENG,"코드영문명"));

		KSGPanel pnMain = new KSGPanel();
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		pnMain.add(pnCenter);
		
		return pnMain;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if(command.equals("취소"))
		{
			close();
			result = BasePop.CANCEL;
		}
		else if(command.equals("저장"))
		{
			String codeID = txfCodeID.getText();
			String codeNM = txfCodeNM.getText();
			String codeENG = txfCodeENG.getText();
			
			if(codeID.equals("")||codeNM.equals("")||codeENG.equals("")) {
				JOptionPane.showMessageDialog(this, "항목을 입력하십시요");
				return;
			}
			
			
			CommandMap param = new CommandMap();
			
			
			param.put("CD_ID", codeID);
			param.put("CD_NM", codeNM);
			param.put("CD_ENG", codeENG);
			
//			callApi("insertCode", param);
			
			try {
				codeService.insertCodeH(param);
				
				result = BasePop.OK;
				
				close();
				
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				result = BasePop.CANCEL;
				JOptionPane.showMessageDialog(this, e1.getMessage());
				
			}
		}
		
	}	
	
	private KSGPanel buildTitle() {
		
		KSGPanel pnTitle = new KSGPanel();
		
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		pnTitle.setBackground(Color.white);
		
		JLabel label = new JLabel(title);
		
		label.setFont(new Font("돋움",0,16));
		
		pnTitle.add(label);

		return pnTitle;
	}

	@Override
	public void createAndUpdateUI() {
		this.setModal(true);

		this.getContentPane().add(buildTitle(),BorderLayout.NORTH);
		
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);

		this.pack();

		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		
		this.setResizable(false);
		
		this.setVisible(true);
	}
	
	@Override
	public void updateView() {


		CommandMap resultMap= this.getModel();

		boolean success = (boolean) resultMap.get("success");

		if(success)
		{

			String serviceId=(String) resultMap.get("serviceId");

			if("insertCode".equals(serviceId))
			{	
				result = KSGDialog.SUCCESS;
				
				close();

			}

		}
		else{  
			String error = (String) resultMap.get("error");
			
			JOptionPane.showMessageDialog(this, error);
		}

	}
}

