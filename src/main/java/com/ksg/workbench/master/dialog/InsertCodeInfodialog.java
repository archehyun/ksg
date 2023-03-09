/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.workbench.master.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Code;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;

/**
 * 코드 정보 추가 다이어그램
 * 
 * @author 박창현
 *
 */
public class InsertCodeInfodialog extends BaseInfoDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField txfCodeName;
	
	private JTextField txtKorCode;
	
	private JTextField txfField;
	
	private String code_type;
	
	private String title;
	
	private BaseServiceImpl baseService;
	
	public InsertCodeInfodialog(BaseInfoUI baseInfoUI) 
	{
		super(baseInfoUI);
		this.addComponentListener(this);
		baseService = new BaseServiceImpl();
		this.baseInfoUI=baseInfoUI;
		
	}

	public InsertCodeInfodialog(BaseInfoUI base, String type) {
		this(base);
		this.code_type=type;
		title =type;
		if(type.equals("Inbound 출발 항구"))
		{
			code_type="fromPort";
		}
		if(type.equals("컨테이너 타입"))
		{
			code_type="conType";
		}
		if(type.equals("IN항구"))
		{
			code_type="inPort";
		}
		
	}



	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle("Code 정보 추가");

		this.getContentPane().add(buildTitle(title+" 코드 추가"),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		ViewUtil.center(this, true);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public KSGPanel buildCenter()
	{
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		txfCodeName = new JTextField(20);
		txtKorCode = new JTextField(20);
		txfField = new JTextField(20);	
		


		pnCenter.add(createFormItem(txfCodeName, "Name"));
		pnCenter.add(createFormItem(txtKorCode, "한글"));
		pnCenter.add(createFormItem(txfField, "Field"));
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			if(txfCodeName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "Name을 적으세요");
				return;
			}
			if(txtKorCode.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "한글명을 적으세요");
				return;
			}
			try{
				String codeField = txfField.getText();
				
				Code op = new Code();
				op.setCode_field(codeField	);
				op.setCode_type(code_type);
				op.setCode_name(txfCodeName.getText());
				op.setCode_name_kor(txtKorCode.getText());
				
				baseService.insertCode(op);
				
				JOptionPane.showMessageDialog(this, "추가 했습니다.");
				close();
				
				
			}catch(SQLException sqle)
			{
				if(sqle.getErrorCode()==2627)//mssql 중복키 오류코드
				{	
					JOptionPane.showMessageDialog(InsertCodeInfodialog.this, "코드가 존재합니다.");
					/*int option=JOptionPane.showConfirmDialog(this, "코드가 존재합니다. 업데이트 하시겠습니까?", "코드 존재", JOptionPane.YES_NO_OPTION);
					if(option==JOptionPane.YES_OPTION)
					{
						AreaInfo insert = new AreaInfo();
						insert.setArea_name(txfCodeName.getText());
						insert.setArea_code(txtKorCode.getText());
						try {
							baseService.updateAreaInfo(insert);
							baseInfoUI.updateTable();
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(this,sqle.getErrorCode()+","+ sqle.getMessage());	
						}
						JOptionPane.showMessageDialog(this, "수정 했습니다.");
						close();
					}*/
				}else
				{
					JOptionPane.showMessageDialog(this,sqle.getErrorCode()+","+ sqle.getMessage());	
				}
			}
		}
		else
		{
			close();
		}
	}

	

	

}
