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
package com.ksg.base.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.base.view.BaseInfoUI;
import com.ksg.base.view.comp.PnCode;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Code;
import com.ksg.view.comp.dialog.KSGDialog;

/**
 * 컨테이너 타입 추가 다이어그램
 * 
 * @author 박창현
 *
 */
public class InsertConTypeCodeInfodialog extends KSGDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblInfo
	;

	private JTextField txfCodeName;
	private JTextField txtKorCode;
	
	PnCode baseCode;
	private JTextField txfField;
	
	String code_type;
	String title;
	
	
	public InsertConTypeCodeInfodialog(BaseInfoUI baseInfoUI) 
	{
		super();
		this.baseInfoUI=baseInfoUI;
		
	}

	public InsertConTypeCodeInfodialog(BaseInfoUI base, String type) {
		this(base);
		this.code_type="conType";
		title ="컨테이너 타입";
	}

	public InsertConTypeCodeInfodialog(BaseInfoUI base, String string,PnCode code) {
		this(base,string);
		this.baseCode = code;
	}
	public InsertConTypeCodeInfodialog() {
		
		this.code_type="conType";
		title ="컨테이너 타입";
	}

	public InsertConTypeCodeInfodialog(String codeTypeConType) {
		this();
		this.code_type=codeTypeConType;
	}

	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle("Code 정보 추가");
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfCodeName = new JTextField(20);
		txtKorCode = new JTextField(20);
		txfField = new JTextField(20);
		lblInfo = new JLabel("",JLabel.RIGHT);
		//lblInfo.setFont(defaultFont);
		
		JPanel pnName = new JPanel();
		pnName.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCodeName = new JLabel("영문");
		lblCodeName.setPreferredSize(new Dimension(80,25));
		pnName.add(lblCodeName);
		pnName.add(txfCodeName);
		
		JPanel pnCode = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblKorName = new JLabel("한글");
		lblKorName.setPreferredSize(new Dimension(80,25));
		pnCode.add(lblKorName);	
		pnCode.add(txtKorCode);
		
		
		JPanel pnField = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblField = new JLabel("코드");
		lblField.setPreferredSize(new Dimension(80,25));
		pnField.add(lblField);	
		pnField.add(txfField);


		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		JPanel pnControl =  new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("확인");
		//butOK.setFont(defaultFont);
		
		butOK.addActionListener(this);
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(this);
		//butCancel.setFont(defaultFont);

		pnControl.add(butOK);
		pnControl.add(butCancel);

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		JLabel label = new JLabel(title+" 코드 추가");
		label.setFont(new Font("돋음",0,16));
		pnTitle.add(label);


		JPanel pnInfo = new JPanel();
		pnInfo.setLayout( new FlowLayout(FlowLayout.LEFT));
		pnInfo.add(lblInfo);

		pnCenter.add(pnName);
		pnCenter.add(pnCode);
		pnCenter.add(pnField);
		pnCenter.add(pnInfo);
		pnCenter.add(pnS);
		pnCenter.add(pnControl);

		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension(15,0));
		JPanel right = new JPanel();
		right.setPreferredSize(new Dimension(15,0));


		this.getContentPane().add(pnTitle,BorderLayout.NORTH);
		this.getContentPane().add(pnCenter,BorderLayout.CENTER);
		this.getContentPane().add(left,BorderLayout.WEST);
		this.getContentPane().add(right,BorderLayout.EAST);
		ViewUtil.center(this, true);
		this.setResizable(false);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			if(txfCodeName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "영문명을 적으세요");
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
				op.setCode_type("conType");
				op.setCode_name(txfCodeName.getText());
				op.setCode_name_kor(txtKorCode.getText());
				
				baseService.insertCode(op);
				
				result = KSGDialog.SUCCESS;		
				close();
				
				
			}catch(SQLException sqle)
			{
				if(sqle.getErrorCode()==2627)//mssql 중복키 오류코드
				{	
					JOptionPane.showMessageDialog(InsertConTypeCodeInfodialog.this, "코드가 존재합니다.");
	
				}else
				{
					JOptionPane.showMessageDialog(this,sqle.getErrorCode()+","+ sqle.getMessage());	
				}
			}
		}
		else
		{
			result = KSGDialog.FAILE;
			close();
		}
	}

}
