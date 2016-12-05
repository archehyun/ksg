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
package com.ksg.view.schedule.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.ksg.commands.LongTask;
import com.ksg.model.KSGModelManager;
import com.ksg.view.comp.KSGDialog;

public class ScheduleBuildMessageDialog extends KSGDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int ONE_SECOND = 100;
	
	private JProgressBar bar;
	
	private JLabel lblMessage,lblCurrentMessage;
	
	private JButton butClose;
	
	private ArrayList<String> errorList;
	
	private JTable tblErrorList;

	private LongTask task;
	
	private Timer timer;// 화면 갱신을 위한 타이머
	
	private String message = "스케줄 생성 중";
	
	DefaultTableModel defaultTableModel = new DefaultTableModel();
	
	public ScheduleBuildMessageDialog() {
		
		super();
		
		this.setName(this.getClass().toString());
		
	}
	public void end()
	{
		butClose.setEnabled(true);
	}
	public ScheduleBuildMessageDialog(LongTask task)	
	{
		this();		
		
		this.task=task;
		
		errorList = new ArrayList<String>();
		

	}


	public void createAndUpdateUI() {

		JPanel pnMain = new JPanel();
		
		pnMain.setLayout(new BorderLayout());
		
		this.setTitle("스케줄 파일 출력");
		
		JPanel pnProcess = new JPanel(new BorderLayout());
		
		tblErrorList = new JTable();
		
		DefaultTableModel defaultTableModel = new DefaultTableModel();
		
		defaultTableModel.addColumn("에러");
		
		tblErrorList.setModel(defaultTableModel);
		
		bar = new JProgressBar();
		
		lblCurrentMessage = new JLabel(message);
		
		pnProcess.add(lblCurrentMessage,BorderLayout.WEST);
		
		pnProcess.add(bar,BorderLayout.EAST);

		JPanel pnMessage = new JPanel(new FlowLayout(FlowLayout.LEFT));

		lblMessage = new JLabel();

		timer = new Timer(ONE_SECOND, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				bar.setMaximum(task.getLengthOfTask());
				
				bar.setValue(task.getCurrent());
				
				lblMessage.setText(task.getMessage());
				
				lblCurrentMessage.setText(task.getMessage());
				if (task.isDone()) {
					Toolkit.getDefaultToolkit().beep();
					timer.stop();
					setCursor(null); //turn off the wait cursor
					bar.setValue(bar.getMinimum());
				}
			}
		});
		
		
		JPanel pnSouth=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnSouth.setBorder(BorderFactory.createEtchedBorder());
		
		butClose = new JButton("완료");
		
		butClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ScheduleBuildMessageDialog.this.setVisible(false);
				
				ScheduleBuildMessageDialog.this.dispose();
				
			}
		});
		
		pnSouth.add(butClose);
		
		butClose.setEnabled(false);

		pnMessage.add(lblMessage);

		pnMain.add(pnProcess,BorderLayout.NORTH);
		
		JScrollPane comp = new JScrollPane(tblErrorList);
		comp.setVisible(false);
		pnMain.add(comp);
		
		pnMain.add(pnSouth,BorderLayout.SOUTH);

		pnMain.setOpaque(true); 
		
		this.setContentPane(pnMain);
		
		this.setSize(400, 150);
		
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		
		this.setVisible(true);
	}

	public void setMessage(String message) {
		
		this.message=message;

	}
	
	public void addErrorMessage(String errro) {
		
		errorList.add(errro);
		
		defaultTableModel = new DefaultTableModel();
		
		defaultTableModel.addColumn("설명");		
		
		for(int i=0;i<errorList.size();i++)
		{
			defaultTableModel.addRow(new Object[]{errorList.get(i)});
		}
		
		tblErrorList.setModel(defaultTableModel);
		
	}
	public void setTask(LongTask task) {
		this.task = task;
		bar.setMaximum(task.getLengthOfTask());
		bar.setValue(0);
		bar.setStringPainted(true);
		
		timer.start();
		
	}

}
