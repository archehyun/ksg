package com.ksg.workbench.common.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.ksg.view.comp.KSGViewUtil;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.BaseInfoDialog;

@SuppressWarnings("serial")
public abstract class MainTypeDialog extends BaseInfoDialog{
	
	
	KSGViewUtil viewUtil = KSGViewUtil.getInstance();
	
	private  KSGPanel pnMain;
	
	public Object resultObj;
	
	public Color titleColor;
	
	public MainTypeDialog(Dialog dialog) {
		
		super(dialog);
		
		titleColor = viewUtil.getColor("dialog.title.color");
		
		initMainComp();
		
	}

	public MainTypeDialog() {
		super();
		titleColor = viewUtil.getColor("dialog.title.color");
		initMainComp();
	}
	
	public MainTypeDialog(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		titleColor = viewUtil.getColor("dialog.title.color");
		initMainComp();
	}
	
	private void initMainComp()
	{
		pnMain = new KSGPanel(new BorderLayout());
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
		this.getContentPane().add(pnMain);
	}
	
	public void addComp(Component comp, Object constraints)
	{
		this.pnMain.add(comp,constraints);
	}
	
	public KSGPanel buildHeader(String titleInfo)
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		//		pnMain.setBackground(Color.lightGray);

		pnMain.add(buildTitle(titleInfo));

		return pnMain;
	}

	public KSGPanel buildTitle(String title)
	{
		KSGPanel pnTitle = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		pnTitle.setBorder(BorderFactory.createEmptyBorder(5,15,5,0));
		//		#000046, #1cb5e0	
		pnTitle.setBackground(titleColor);

		lblTitle = new JLabel(title);

		lblTitle.setFont(new Font("area",Font.BOLD,18));

		lblTitle.setForeground(Color.white);

		pnTitle.add(lblTitle);

		return pnTitle;
	}

}
