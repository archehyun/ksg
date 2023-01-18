package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.dtp.api.control.AbstractController;
import com.ksg.common.dao.DAOImplManager;
import com.ksg.common.model.CommandMap;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.comp.View;
import com.ksg.workbench.master.BaseInfoUI;

/**
 * 마스터 화면 추상 클래스
 * 
 * @author 박창현
 *
 */
public abstract class PnBase extends KSGPanel implements ComponentListener, View{
	/**
	 * 
	 */

	protected CommandMap model;

	// 초기 데이터 표시 여부
	protected boolean isShowData=true;	

	private AbstractController controller;

	private static final long serialVersionUID = 1L;

	DAOImplManager daoImplManager = DAOImplManager.getInstance();

	protected BaseInfoUI baseInfoUI;	

	public BaseInfoUI getBaseInfoUI() {
		return baseInfoUI;
	}


	public PnBase(BaseInfoUI baseInfoUI) {

		this.baseInfoUI = baseInfoUI;

		this.setLayout(new BorderLayout());		

		this.setBorder(BorderFactory.createLineBorder(Color.gray));
	}
	
	
	public void setController(AbstractController constroller)
	{
		this.controller =constroller;
	}




	protected KSGPanel buildTitleIcon(String title)
	{
		KSGPanel pnCount = new KSGPanel();
		pnCount.setLayout(new FlowLayout(FlowLayout.LEFT));


		JLabel lblTable = new JLabel(title);
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("돋움",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));


		pnCount.add(lblTable);
		return pnCount;
	}

	protected JComponent buildLine()
	{
		KSGPanel pnS = new KSGPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		KSGPanel pnS1 = new KSGPanel();
		pnS1.setPreferredSize(new Dimension(0,15));
		Box info = new Box(BoxLayout.Y_AXIS);
		info.add(pnS);
		info.add(pnS1);
		return info;
	}



	public abstract void fnSearch();

	@Override
	public  void createAndUpdateUI() {};

	@Override
	public void componentResized(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

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



}
