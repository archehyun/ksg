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

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� :

  */
@SuppressWarnings("serial")
public class CommonCodeInsertPop extends BaseInfoDialog{
	
	CodeServiceImpl codeService;
	
	private JTextField txfCodeID;
	
	private JTextField txfCodeNM;
	
	private JTextField txfCodeENG;	
	
	public CommonCodeInsertPop() {
		
		this.setTitle("�ڵ� ���� �߰�");
		
		codeService = new CodeServiceImpl();
		
		this.setController( new CodeController());

	}
	
	public KSGPanel buildCenter()
	{
			
		txfCodeID = new JTextField(15);
		
		txfCodeNM = new JTextField(15);
		
		txfCodeENG = new JTextField(15);
		
		
		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		pnCenter.add(createFormItem(txfCodeID,"�ڵ�ID"));
		
		pnCenter.add(createFormItem(txfCodeNM, "�ڵ��"));
		
		pnCenter.add(createFormItem(txfCodeENG,"�ڵ念����"));

		KSGPanel pnMain = new KSGPanel();
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		pnMain.add(pnCenter);
		
		return pnMain;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if(command.equals("���"))
		{
			close();
			result = BasePop.CANCEL;
		}
		else if(command.equals("����"))
		{
			String codeID = txfCodeID.getText();
			String codeNM = txfCodeNM.getText();
			String codeENG = txfCodeENG.getText();
			
			if(codeID.equals("")||codeNM.equals("")||codeENG.equals("")) {
				JOptionPane.showMessageDialog(this, "�׸��� �Է��Ͻʽÿ�");
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
		
		label.setFont(new Font("����",0,16));
		
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

