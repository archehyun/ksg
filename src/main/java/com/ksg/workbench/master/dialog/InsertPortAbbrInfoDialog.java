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
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.dtp.api.control.PortController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.service.PortService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;

/**
 * 항구 약어 정보 추가 다이어그램
 * 
 * @author 박창현
 *
 */
public class InsertPortAbbrInfoDialog extends BaseInfoDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField txfPortName = new JTextField(20);
	
	private JTextField txfPortAbbr = new JTextField(20);
	
	private String port_name,port_abbr="";
	


	public InsertPortAbbrInfoDialog(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		
		this.setController(new PortController());
		
		this.baseInfoUI=baseInfoUI;
		
		this.addComponentListener(this);
	}
	
	public InsertPortAbbrInfoDialog(BaseInfoUI baseInfoUI,String port_name) {
		
		this(baseInfoUI);
		
		
		this.port_name =port_name;
		
	}

	public void createAndUpdateUI() {
		
		this.setModal(true);
		
		this.setTitle("항구명 약어 추가");

		this.getContentPane().add(buildTitle("항구명: "+port_name),BorderLayout.NORTH);
		
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		this.pack();
		
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		
		this.setVisible(true);
	}
	
	public KSGPanel buildCenter() {
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		pnCenter.add(createFormItem(txfPortAbbr, "항구명 약어"));
		
		pnMain.add(pnCenter);
		
		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("저장"))
		{
			if(txfPortAbbr.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this, "항구명 약어를 입력하십시요");
				return;
			}
			String port_abbr = txfPortAbbr.getText(); 
			
			CommandMap param = new CommandMap();
			
			param.put("port_name", port_name);
			
			param.put("port_abbr", port_abbr);
			
			callApi("insertPortDetail", param);
			

		}else
		{
			close();
		}
		
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		this.txfPortName.setText(port_name);
		this.txfPortAbbr.setText(port_abbr);
		
	}
	
	@Override
	public void updateView() {
		
		CommandMap resultMap= this.getModel();

		boolean success = (boolean) resultMap.get("success");

		if(success)
		{
			String serviceId=(String) resultMap.get("serviceId");

			if("insertPortDetail".equals(serviceId))
			{	
				result = SUCCESS;

				JOptionPane.showMessageDialog(InsertPortAbbrInfoDialog.this,"추가했습니다.");

				close();

			}

			
		}
		else{  
			String error = (String) resultMap.get("error");

			JOptionPane.showMessageDialog(this, error);
		}

	}

}
