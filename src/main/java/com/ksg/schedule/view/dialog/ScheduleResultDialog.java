package com.ksg.schedule.view.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.ui.InboundTreeDialog;
import com.ksg.view.ui.OutboundTreeDialog;
import com.ksg.view.ui.WorldTreeDialog;

public class ScheduleResultDialog extends KSGDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	InboundTreeDialog inboundTreeDialog;
	OutboundTreeDialog outboundTreeDialog;
	WorldTreeDialog worldTreeDialog;
	public ScheduleResultDialog() {
		super();
		inboundTreeDialog = new InboundTreeDialog();
		outboundTreeDialog = new OutboundTreeDialog();
		worldTreeDialog = new WorldTreeDialog();
	}

	public void createAndUpdateUI() {
		
		
		this.setTitle("Schedule 생성 결과");
		JTabbedPane pane = new JTabbedPane();
		inboundTreeDialog.createAndUpdateUI();
		outboundTreeDialog.createAndUpdateUI();
		worldTreeDialog.createAndUpdateUI();
		pane.addTab("INBOUND", inboundTreeDialog);
		pane.addTab("OUTBOUND",outboundTreeDialog);
		pane.addTab("WORLD SCHEDULE",worldTreeDialog);
		
		
		JPanel pnControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butCancel = new JButton("닫기(C)");
		butCancel.setMnemonic(KeyEvent.VK_C);
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				ScheduleResultDialog.this.setVisible(false);
			}});
		
		
		pnControl.add(butCancel);
		
		
		
		this.getContentPane().add(pane);
		this.getContentPane().add(pnControl,BorderLayout.SOUTH);
		
		
		this.setSize(800, 600);
		
		ViewUtil.center(this,false);
		this.setVisible(true);

	}

}
