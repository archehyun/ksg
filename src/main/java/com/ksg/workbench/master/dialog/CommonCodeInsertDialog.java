package com.ksg.workbench.master.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.dtp.api.control.CodeController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.dialog.MainTypeDialog;

/**

 * @FileName : CommonCodeInsertPop.java

 * @Date : 2021. 3. 18. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
@SuppressWarnings("serial")
public class CommonCodeInsertDialog extends MainTypeDialog{

	private JTextField txfCodeID;

	private JTextField txfCodeNM;

	private JTextField txfCodeENG;	

	public CommonCodeInsertDialog() {
		
		this.setController( new CodeController());
	}

	public KSGPanel buildCenter()
	{
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

			param.put("cd_id", codeID);

			param.put("cd_nm", codeNM);

			param.put("cd_eng", codeENG);

			callApi("insertCode", param);
		}
	}	

	@Override
	public void createAndUpdateUI() {

		this.titleInfo="코드 정보 추가";

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
		
		txfCodeID 	= new JTextField(15);

		txfCodeNM 	= new JTextField(15);

		txfCodeENG 	= new JTextField(15);
	}

	@Override
	public void updateView() {

		CommandMap resultMap= this.getModel();

		String serviceId=(String) resultMap.get("serviceId");

		if("insertCode".equals(serviceId))
		{	
			result = KSGDialog.SUCCESS;

			close();
		}


	}
}

