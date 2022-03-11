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
package com.ksg.workbench.base.dialog;

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

import com.ksg.common.util.ViewUtil;
import com.ksg.domain.AreaInfo;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

public class InsertAreaInfodialog extends BaseInfoDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel lblInfo;

	private JTextField txfAreaName;
	private JTextField txfAreaCode;
	private JTextField txfAreaBookCode;
	String title;
	AreaInfo selectedInfo;
	private BaseServiceImpl baseService;
	public InsertAreaInfodialog(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		this.baseInfoUI=baseInfoUI;
		title = "Area 정보 추가";
		baseService = new BaseServiceImpl(); 
	}

	public InsertAreaInfodialog(BaseInfoUI infoUI, AreaInfo info) {
		this(infoUI);
		title = "Area 정보 수정";
		selectedInfo= info;
	}


	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle(title);
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfAreaName = new JTextField(21);
		txfAreaCode = new JTextField(5);
		txfAreaBookCode = new JTextField(5);
		lblInfo = new JLabel("",JLabel.RIGHT);
		lblInfo.setFont(defaultFont);
		JPanel pnName = new JPanel();
		pnName.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAreaName = new JLabel("Area Name");
		lblAreaName.setPreferredSize(new Dimension(80,25));
		pnName.add(lblAreaName);
		pnName.add(txfAreaName);
		JPanel pnCode = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAreaCode = new JLabel("Area Code");
		lblAreaCode.setPreferredSize(new Dimension(80,25));
		pnCode.add(lblAreaCode);	
		pnCode.add(txfAreaCode);
		
		JLabel lblAreaBookCode = new JLabel("Area Book Code");
		lblAreaBookCode.setPreferredSize(new Dimension(100,25));
		pnCode.add(lblAreaBookCode);
		pnCode.add(txfAreaBookCode);
		
//		cbxGroup = new JComboBox();


		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		JPanel pnControl =  new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("확인");
		butOK.setFont(defaultFont);
		
		butOK.addActionListener(this);
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(this);
		butCancel.setFont(defaultFont);

		pnControl.add(butOK);
		pnControl.add(butCancel);

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		JLabel label = new JLabel("Add a Area Field");
		label.setFont(new Font("돋음",0,16));
		pnTitle.add(label);


		JPanel pnInfo = new JPanel();
		pnInfo.setLayout( new FlowLayout(FlowLayout.LEFT));
		pnInfo.add(lblInfo);

		pnCenter.add(pnName);
		pnCenter.add(pnCode);
		pnCenter.add(pnInfo);
		pnCenter.add(pnS);
		pnCenter.add(pnControl);

		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension(15,0));
		JPanel right = new JPanel();
		right.setPreferredSize(new Dimension(15,0));
		if(selectedInfo!=null)
		{
			txfAreaCode.setText(selectedInfo.getArea_code());
			txfAreaName.setText(selectedInfo.getArea_name());
			txfAreaBookCode.setText(String.valueOf(selectedInfo.getArea_book_code()));
		}

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
			if(txfAreaName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "지역명을 적으세요");
				return;
			}
			if(txfAreaCode.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "코드번호을 적으세요");
				return;
			}
			
			if(txfAreaBookCode.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "코드번호을 적으세요");
				return;
			}
			try{
				
				
				AreaInfo insert = new AreaInfo();
				
				insert.setArea_name(txfAreaName.getText());
				insert.setArea_code(txfAreaCode.getText());
				insert.setArea_book_code(Integer.parseInt(txfAreaBookCode.getText()));
				baseService.insertAreaInfo(insert);
				result = KSGDialog.SUCCESS;
				JOptionPane.showMessageDialog(this, "추가 했습니다.");
				close();
				//baseInfoUI.updateTable();
				
			}catch (NumberFormatException nume) {
				JOptionPane.showMessageDialog(this, "숫자 형식이 잘못 되었습니다.");				
			}catch(SQLException sqle)
			{
				if(sqle.getErrorCode()==2627)//mssql 중복키 오류코드
				{
					int option=JOptionPane.showConfirmDialog(this, "코드가 존재합니다. 업데이트 하시겠습니까?", "코드 존재", JOptionPane.YES_NO_OPTION);
					if(option==JOptionPane.YES_OPTION)
					{
						AreaInfo insert = new AreaInfo();
						insert.setArea_name(txfAreaName.getText());
						insert.setArea_code(txfAreaCode.getText());
						insert.setBase_area_name(txfAreaName.getText());
						insert.setArea_book_code(Integer.parseInt(txfAreaBookCode.getText()));
						try {
							
							baseService.updateAreaInfo(insert);
							result = KSGDialog.SUCCESS;
							//baseInfoUI.updateTable();
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(this,sqle.getErrorCode()+","+ sqle.getMessage());	
						}
						JOptionPane.showMessageDialog(this, "추가 했습니다.");
						close();
					}
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
