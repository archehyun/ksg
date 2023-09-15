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
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.dialog.MainTypeDialog;


/**

  * @FileName : CommonCodeDetailInsertPop.java

  * @Date : 2021. 3. 19. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :

  */
@SuppressWarnings("serial")
public class InsertCommonCodeDetailDialog extends MainTypeDialog {
	
	private JTextField txfCodeField;
	
	private JTextField txfCodeNameEng;
	
	private JTextField txfCodeType;
	
	private JTextField txfCodeNameKor;

	
	HashMap<String, Object> param;
	
	private String codeType;
	
	public InsertCommonCodeDetailDialog() {
		
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

	@Override
	public void createAndUpdateUI() {
		
		this.titleInfo="상세 코드 정보 추가";
		
		initComp();
		
		this.setModal(true);

		this.getContentPane().add(buildHeader(titleInfo),BorderLayout.NORTH);

		this.addComp(buildCenter(),BorderLayout.CENTER);

		this.addComp(buildControl(),BorderLayout.SOUTH);

		ViewUtil.center(this,true);

		this.setResizable(false);

		this.setVisible(true);
	}
	
	private void initComp() {
		
		txfCodeField 	= new JTextField(15);
		
		txfCodeNameEng 	= new JTextField(15);
		
		txfCodeType 	= new JTextField(15);
		
		txfCodeNameKor 	= new JTextField(15);
		
		txfCodeType.setEditable(false);
		
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

