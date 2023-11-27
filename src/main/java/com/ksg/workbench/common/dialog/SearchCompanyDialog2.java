package com.ksg.workbench.common.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.dtp.api.control.CompanyController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.notification.Notification;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;


/**

 * @FileName : SearchPortDialog.java
 * @Date : 2021. 3. 31. 
 * @작성자 : 박창현
 * @변경이력 :
 * @프로그램 설명 : 항구 조회

 */
@SuppressWarnings("serial")
public class SearchCompanyDialog2 extends MainTypeDialog implements ActionListener{

	

	private KSGTablePanel tableH;

	private SelectEventHandler selectEventHandler = new SelectEventHandler();

	private JTextField txfSearchCompanyName;

	private JTextField txfSearchCompanyAbbr;

	public SearchCompanyDialog2() {

		super();

		this.setController(new CompanyController());
		this.addComponentListener(this);

	}

	public void createAndUpdateUI() {
		
		this.setModal(true);
		
		this.getContentPane().add(buildHeader(),BorderLayout.NORTH);
		
		this.addComp(buildCenter(),BorderLayout.CENTER);
		
		this.addComp(buildControl(),BorderLayout.SOUTH);

		setSize(new Dimension(600,600));
		
		ViewUtil.center(this, false);
		
		this.setVisible(true);
	}

	private KSGPanel buildCenter()
	{
		// table init
		
		tableH = new KSGTablePanel();

		tableH.addColumn(new KSGTableColumn("company_name", "선사명"));
		
		tableH.addColumn(new KSGTableColumn("company_abbr", "선사약어"));
		
		tableH.addColumn(new KSGTableColumn("agent_name", "에이전트명"));
		
		tableH.addColumn(new KSGTableColumn("agent_abbr", "에이전트 약어"));

		tableH.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		tableH.initComp();

		tableH.addMouseListener(selectEventHandler);

		tableH.addKeyListener(selectEventHandler);

		KSGPanel pnMainDetail = new KSGPanel(new BorderLayout(5,5));
		
		KSGPanel pnMainNorth =  new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel label = new JLabel("상세정보",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		
		pnMainNorth.add(label);
		
		pnMainNorth.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		
		pnMainDetail.add(pnMainNorth,BorderLayout.NORTH);
		
		pnMainDetail.add(tableH);

		pnMainDetail.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		KSGPanel pnMain =  new KSGPanel(new BorderLayout(10,10));
		
		pnMain.add(buildNorth(),BorderLayout.NORTH);
		
		pnMain.add(pnMainDetail);
		
		return pnMain;
	}
	
	public KSGPanel buildHeader()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		pnMain.add(buildTitle("선사 조회"));
		
		return pnMain;
	}

	public KSGPanel buildTitle(String title)
	{
		KSGPanel pnTitle = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnTitle.setBorder(BorderFactory.createEmptyBorder(5,15,5,0));
//		#000046, #1cb5e0	
		pnTitle.setBackground(titleColor);
		
		lblTitle = new JLabel(title);
		
		lblTitle.setFont(new Font("area",Font.BOLD,20));
		
		lblTitle.setForeground(Color.white);
		
		pnTitle.add(lblTitle);
		
		return pnTitle;
	}
	
	private Component buildNorth() {
		
		KSGGradientButton butSearch = new KSGGradientButton("검색");
		
		butSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		
		txfSearchCompanyName = new JTextField(15);
		
		txfSearchCompanyAbbr = new JTextField(15);
		
		txfSearchCompanyName.addKeyListener(new CompanyNameKeyAdapter("company_name", "company_abbr", txfSearchCompanyAbbr));
		
		txfSearchCompanyAbbr.addKeyListener(new CompanyNameKeyAdapter("company_abbr", "company_name", txfSearchCompanyName));
		
		KSGPanel pnSearchLabel = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel label = new JLabel("검색조건",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		
		pnSearchLabel.add(label);
		
		KSGPanel pnSearchOptionMain = new KSGPanel(new BorderLayout());
		
		KSGPanel pnSearchOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT,5,5));
		
		pnSearchOption.add(new JLabel("선사명"));
		
		pnSearchOption.add(txfSearchCompanyName);
		
		pnSearchOption.add(new JLabel("선사명약어"));
	
		pnSearchOption.add(txfSearchCompanyAbbr);
		
		KSGPanel pnSearchControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));		
		
		pnSearchOptionMain.add(pnSearchOption);
		
		pnSearchOptionMain.add(pnSearchControl,BorderLayout.EAST);
		
		pnSearchOptionMain.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.lightGray,1),BorderFactory.createEmptyBorder(10,15,10,15)));
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		pnMain.add(pnSearchLabel, BorderLayout.NORTH);
		
		pnMain.add(pnSearchOptionMain);
		
		return pnMain;
	}

	class CompanyNameKeyAdapter extends KeyAdapter
	{	
		JTextField txfSub;
		
		String searchType;
		
		String subType;

		public CompanyNameKeyAdapter(String searchType,String subType, JTextField txfSub) {
			
			this.searchType = searchType;
			
			this.subType = subType;
			
			this.txfSub = txfSub;
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			
			JTextField txf = (JTextField) e.getSource();
			
			String paramOne = txf.getText();
			
			String paramTwo = txfSub.getText();
			
			CommandMap param = new CommandMap();
			
			if(paramOne.isEmpty()&&paramTwo.isEmpty())
			{
//				tableH.clearResult();
				
			}
			else
			{
				
				
				param.put(searchType, paramOne);
				
				param.put(subType, paramTwo);
				
				
			}
			callApi("selectCompanyListByCondition", param);

		}
	}
	
	private void selectValue()
	{
		int ro = tableH.getSelectedRow();

		if(ro==-1) return;
		
		CommandMap resultItem = new CommandMap();
		
		resultItem.put("company_name", tableH.getValueAt(ro, 0));
		resultItem.put("company_abbr", tableH.getValueAt(ro, 1));
		
		resultItem.put("agent_name", tableH.getValueAt(ro, 2));
		resultItem.put("agent_abbr", tableH.getValueAt(ro, 3));
		
		resultObj = resultItem;

		close();
	}

	class SelectEventHandler extends MouseAdapter implements KeyListener
	{
		
		public void mouseClicked(MouseEvent e) {
			
			if(e.getClickCount()>1)
			{
				selectValue();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {

			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				selectValue();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals("저장"))
		{
			int ro=tableH.getSelectedRow();
			
			if(ro==-1)
			{
				NotificationManager.showNotification(Notification.Type.WARNING,"선택된 항구명이 없습니다.");
				return;
			}
			
			selectValue();
		}
		else if(command.equals("취소"))
		{
			close();
		}
	}
	
	@Override
	public void componentShown(ComponentEvent e) {

		callApi("searchCompanyDialog2.init");
	}

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("selectCompanyListByCondition".equals(serviceId)) {

			List portList = (List )result.get("data");

			this.tableH.setResultData(portList);
		}
		if("searchCompanyDialog2.init".equals(serviceId)) {

			List portList = (List )result.get("data");

			this.tableH.setResultData(portList);
		}
	}
}