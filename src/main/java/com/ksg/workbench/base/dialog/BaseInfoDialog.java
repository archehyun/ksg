package com.ksg.workbench.base.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**

  * @FileName : BaseInfoDialog.java

  * @Project : KSG2

  * @Date : 2022. 3. 14. 

  * @�ۼ��� : pch

  * @�����̷� : 

  * @���α׷� ���� : ���̾�α� �߻� Ŭ����

  */
public abstract class BaseInfoDialog extends KSGDialog implements ActionListener{
	
	
	protected static String INSERT_ACTION = "�߰�";
	protected static String UPDATE_ACTION = "����";
	protected static String CANCEL_ACTION = "���";
	
	
	protected JLabel lblTitle;

	protected String titleInfo;
	
	protected JButton butOK;
	
	protected JButton butCancel;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseInfoDialog(BaseInfoUI baseInfoUI) {
		super();
		this.baseInfoUI = baseInfoUI;
	}
	
	public BaseInfoDialog() {
		super();
		
	}
	
	protected KSGPanel createFormItem(JComponent comp, String title) {
		KSGPanel pnCompany_abbr = new KSGPanel();
		pnCompany_abbr.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany_abbr = new JLabel(title);
		lblCompany_abbr.setPreferredSize(new Dimension(100,25));
		pnCompany_abbr.add(lblCompany_abbr);	
		pnCompany_abbr.add(comp);
		return pnCompany_abbr;
	}

	protected BaseInfoUI baseInfoUI;
	
	protected KSGPanel buildControl()
	{
		KSGPanel pnMain =  new KSGPanel(new BorderLayout());
		KSGPanel pnControl =  new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		

		butOK = new JButton("����");

		butCancel = new JButton("���");
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
	

}
