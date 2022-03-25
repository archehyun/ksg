package com.ksg.workbench.schedule.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.ksg.common.model.KSGModelManager;
import com.ksg.service.PortService;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**

  * @FileName : SearchPortDialog.java

  * @Project : KSG2

  * @Date : 2022. 3. 25. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 항구 정보 조회 팝업

  */
public class SearchPortDialog extends KSGDialog implements ActionListener{
	
	PortService service;
	private JLabel lblTitle;
	private JButton butOK;
	private JButton butCancel;
	
	public SearchPortDialog() {
		service = new PortServiceImpl();
	}

	@Override
	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle(title);

		this.getContentPane().add(buildTitle("항구 조회"),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setResizable(false);
		this.setVisible(true);
		
	}
	
	public KSGPanel buildTitle(String title)
	{
		KSGPanel pnTitle = new KSGPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("area",Font.BOLD,16));
		pnTitle.add(lblTitle);
		return pnTitle;
	}
	
	public KSGPanel buildCenter()
	{
		return new KSGPanel();
	}
	
	protected KSGPanel buildControl()
	{
		KSGPanel pnMain =  new KSGPanel(new BorderLayout());
		KSGPanel pnControl =  new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		

		butOK = new JButton("저장");

		butCancel = new JButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);
		
		
		KSGPanel pnS = new KSGPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		KSGPanel pnS1 = new KSGPanel();
		pnS1.setPreferredSize(new Dimension(0,15));
		
		pnMain.add(pnControl);
		
		pnMain.add(pnS,BorderLayout.NORTH);
		
		return pnMain;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(command.equals("취소"))
		{
			this.close();
		}
		
	}

}
