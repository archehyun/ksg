package com.ksg.workbench.master.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.dtp.api.control.CodeController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;


/**

  * @FileName : CommonCodeDetailInsertPop.java

  * @Date : 2021. 3. 19. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :

  */
@SuppressWarnings("serial")
public class InsertCommonCodeDetailDialog extends BaseInfoDialog {
	
	private JTextField txfCodeField;
	
	private JTextField txfCodeNameEng;
	
	private JTextField txfCodeType;
	
	private JTextField txfCodeNameKor;

	
	HashMap<String, Object> param;
	
	private String codeType;
	
	public InsertCommonCodeDetailDialog() {
		
		this.setTitle("상세 코드 정보 추가");
		
		this.addComponentListener(this);
		
		this.setController(new CodeController());
	}
	
	public InsertCommonCodeDetailDialog(HashMap<String, Object> item) {
		this();
		param = item;
		
	}
	
	public InsertCommonCodeDetailDialog(String codeType) {
		this();
		this.codeType = codeType;
		
	}
	
	public KSGPanel buildCenter()
	{
		
		txfCodeField 	= new JTextField(15);
		
		txfCodeNameEng 	= new JTextField(15);
		
		txfCodeType 	= new JTextField(15);
		
		txfCodeNameKor 	= new JTextField(15);
		
		txfCodeType.setEditable(false);
		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		pnCenter.add(createFormItem(txfCodeType,"코드타입"));
		
		pnCenter.add(createFormItem(txfCodeField,"코드Field"));
		
		pnCenter.add(createFormItem(txfCodeNameEng, "코드영문명"));
		
		pnCenter.add(createFormItem(txfCodeNameKor,"코드명"));

		KSGPanel pnMain = new KSGPanel();
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		pnMain.add(pnCenter);
		
		return pnMain;
	}
	
	private KSGPanel addComp(String title, JComponent comp)
	{
		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel lblTitle = new JLabel(title,JLabel.RIGHT);
		
		lblTitle.setPreferredSize(new Dimension(100,25));
		
		pnMain.add(lblTitle);
		
		pnMain.add(comp);
		
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
			String codeField = txfCodeField.getText();
			
			String codeNameEng = txfCodeNameEng.getText();
			
			String codeType = txfCodeType.getText();
			
			String codeNameKor = txfCodeNameKor.getText();
			
			if(codeField.equals("")||codeNameEng.equals("")||codeNameKor.equals("")) {
				JOptionPane.showMessageDialog(this, "항목을 입력하십시요");
				return;
			}
			
			
			CommandMap param = new CommandMap();
			
			param.put("code_field", codeField);
			
			param.put("code_name", codeNameEng);
			
			param.put("code_type", codeType);
			
			param.put("code_name_kor", codeNameKor);
			
			callApi("insertCodeDetail", param);

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
	public void componentShown(ComponentEvent e) {
		
		txfCodeType.setText((String)param.get("code_name"));
		
	}
	
	@Override
	public void updateView() {


		CommandMap resultMap= this.getModel();

		boolean success = (boolean) resultMap.get("success");

		if(success)
		{

			String serviceId=(String) resultMap.get("serviceId");

			if("insertCodeDetail".equals(serviceId))
			{	
				
				NotificationManager.showNotification("추가했습니다.");
				
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

