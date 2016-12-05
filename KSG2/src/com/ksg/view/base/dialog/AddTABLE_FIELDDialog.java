package com.ksg.view.base.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.ksg.view.base.BaseInfoUI;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.util.ViewUtil;

public class AddTABLE_FIELDDialog  extends KSGDialog implements ActionListener{

	public AddTABLE_FIELDDialog(BaseInfoUI baseInfoUI) {
	}

	public void createAndUpdateUI() {
		
		this.setTitle("���̺� �ʵ� ���");
		JPanel pnMain = new JPanel();
		
		this.getContentPane().add(pnMain,BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.NORTH);
		this.getContentPane().add(createMargin(),BorderLayout.EAST);
		this.getContentPane().add(createMargin(),BorderLayout.WEST);
		this.setModal(true);
		this.setSize(400, 400);
		ViewUtil.center(this, false);
		this.setVisible(true);
		
	}

	

	private Component buildControl() {
		
		JButton butOK = new JButton("Ȯ��");
		butOK.addActionListener(this);
		JButton butCancel = new JButton("���");
		butCancel.addActionListener(this);
		
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.RIGHT));
		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Ȯ��"))
		{
			this.setVisible(false);
			this.dispose();
		}else
		{
			this.setVisible(false);
			this.dispose();
		}
		
	}

}
