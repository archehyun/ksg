package com.ksg.workbench.master.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.dtp.api.control.AbstractController;
import com.ksg.common.model.CommandMap;
import com.ksg.workbench.common.comp.View;
import com.ksg.workbench.common.comp.button.GradientButton;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;

/**

  * @FileName : BaseInfoDialog.java

  * @Project : KSG2

  * @Date : 2022. 3. 14. 

  * @작성자 : pch

  * @변경이력 : 

  * @프로그램 설명 : 다이어로그 추상 클래스

  */
public abstract class BaseInfoDialog extends KSGDialog implements ActionListener, View{
	
	
	protected static String INSERT_ACTION = "추가";
	protected static String UPDATE_ACTION = "저장";
	protected static String CANCEL_ACTION = "취소";
	
	
	protected JLabel lblTitle;

	protected String titleInfo;
	
	protected JButton butOK;
	
	protected JButton butCancel;
	
	protected CommandMap model;
	
	private AbstractController controller;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseInfoDialog(BaseInfoUI baseInfoUI) {
		super();
		this.baseInfoUI = baseInfoUI;
	}
	
	public BaseInfoDialog(Dialog dialog) {
		super(dialog);
		
	}
	public BaseInfoDialog() {
		super();
		
	}
	
	public void setController(AbstractController constroller)
	{
		this.controller =constroller;
	}

	private Color labelColor = new Color(230,230,230);
	
	protected KSGPanel createFormItem(JComponent comp, String title) {
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		KSGPanel pnLabel = new KSGPanel(new BorderLayout());
		
		pnLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		JLabel lblCompany_abbr = new JLabel(title);
		
		lblCompany_abbr.setVerticalAlignment(JLabel.CENTER);
		
		pnLabel.add(lblCompany_abbr);
		
		pnLabel.setBackground(labelColor);
		
		lblCompany_abbr.setPreferredSize(new Dimension(120,15));
		
		KSGPanel pnComp = new KSGPanel(new BorderLayout());
		
		pnComp.add(comp);
		
		pnComp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		pnMain.add(pnLabel,BorderLayout.WEST);	
		
		pnMain.add(pnComp);
		
		pnMain.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		
		return pnMain;
	}
	
	protected KSGPanel createFormItem(JComponent comp1, JComponent comp2, String title) {
		
		KSGPanel pnComp = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnComp.add(comp1);
		
		pnComp.add(comp2);
		
		
		return createFormItem(pnComp, title);
	}

	protected BaseInfoUI baseInfoUI;
	
	protected KSGPanel buildControl()
	{	
		KSGPanel pnControl =  new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		butOK = new GradientButton("저장");

		butCancel = new GradientButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);
		
		
		KSGPanel pnLine = new KSGPanel();
		pnLine.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnLine.setPreferredSize(new Dimension(0,1));		
		
		KSGPanel pnMain =  new KSGPanel(new BorderLayout());
		pnMain.add(pnControl);
		pnMain.add(pnLine,BorderLayout.NORTH);		
		
		
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
	
	@Override
	public void setModel(CommandMap model) {
		this. model = model;

	}
	public CommandMap getModel() {

		return model;
	}

	public void callApi(String serviceId, CommandMap param)
	{
		if(this.controller!=null)
			this.controller.call(serviceId, param, this);
	}
	public void callApi(String serviceId)
	{
		if(this.controller!=null)
			this.controller.call(serviceId, new CommandMap(),this);
	}
	
	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		
	}

	

}
