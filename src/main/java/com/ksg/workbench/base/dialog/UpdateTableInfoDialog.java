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
package com.ksg.workbench.base.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.service.ADVServiceImpl;
import com.ksg.service.TableServiceImpl;
import com.ksg.view.comp.dialog.KSGDialog;

/**
 * 테이블 정보 수정 다이어그램
 * 
 * @author 박창현
 *
 */
public class UpdateTableInfoDialog extends KSGDialog implements ActionListener,FocusListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField 	txfTable_id;
	private JTextField 	txfCompany;
	private JTextField 	txfAgent;
	private JTextField 	txfOutPort;
	private JTextField 	txfOutToPort;
	private JTextField 	txfInPort;
	private JTextField 	txfInToPort;
	private JTextField 	txfGubun;
	private JTextArea	txaCommon;
	private JTextArea 	txaQuark;
	private JTextField 	txfTitle;
	private JTextField 	txfPage;
	private JTextField 	txfCompany_Abbr;
	private JTextField 	txfPageOrder;
	private JTextField  txfPortCount;
	private JTextField  txfVesselCount;
	ShippersTable tableData;

	private JTextField txfOther;

	private JRadioButton butNNN;

	private JRadioButton butTS;

	private JRadioButton butNomal;
	public UpdateTableInfoDialog(ShippersTable tableData) {
		super();
		advservice = new ADVServiceImpl();
		tableService= new TableServiceImpl();
		this.tableData=tableData;

	}
	public void setShipperTableData(ShippersTable tableData) {


		try {
			ShippersTable shippersTable= tableService.getTableById(tableData.getTable_id());
			txfTable_id.setText(shippersTable.getTable_id());
			txaQuark.setText(shippersTable.getQuark_format());
			txfAgent.setText(shippersTable.getAgent());
			txfGubun.setText(shippersTable.getGubun());
			txfTitle.setText(shippersTable.getTitle());
			txfPortCount.setText(String.valueOf(shippersTable.getPort_col()));
			txfVesselCount.setText(String.valueOf(shippersTable.getVsl_row()));
			txfCompany_Abbr.setText(shippersTable.getCompany_abbr());
			txfPage.setText(shippersTable.getPage()+"");
			txfPageOrder.setText(shippersTable.getTable_index()+"");
			txfInPort.setText(shippersTable.getIn_port());
			txfInToPort.setText(shippersTable.getIn_to_port());
			txfOutPort.setText(shippersTable.getOut_port());
			txfOutToPort.setText(shippersTable.getOut_to_port());
			txfOther.setText(String.valueOf(shippersTable.getOthercell()));
			txaCommon.setText(shippersTable.getCommon_shipping());
			if(shippersTable.getGubun().equals("NNN"))
			{
				butNNN.setSelected(true);
			}else if(shippersTable.getGubun().equals("TS"))
			{
				butTS.setSelected(true);
			}else
			{
				butNomal.setSelected(true);
			}

		} catch (NumberFormatException e) {
			this.dispose();
			JOptionPane.showMessageDialog(null, "항목이 없습니다."+tableData.getTable_id());

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}
	public Component buildButtom()
	{
		JPanel pnButtom = new JPanel();
		JPanel pnPass = new JPanel();


		JButton btnNext = new JButton("등록");
		btnNext.addActionListener(this);

		pnPass.add(btnNext);
		JButton btnCancel = new JButton("취소");
		btnCancel.addActionListener(this);

		pnPass.add(btnCancel);

		pnButtom.add(pnPass);
		return pnButtom;
	}
	private JPanel createForm(String label,Component comp)
	{
		return createForm(label,comp, 80,25); 

	}
	private JPanel createForm(String label,Component comp,int w)
	{
		return createForm(label,comp, w,25); 

	}
	private JPanel createForm(String label,Component comp, int w, int h)
	{

		JLabel label2 = new JLabel(label,JLabel.RIGHT);

		label2.setPreferredSize(new Dimension(w,h));

		JPanel pnComp = new JPanel();
		pnComp.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnComp.add(label2);
		pnComp.add(comp);
		pnComp.setPreferredSize(new Dimension(300,40));
		return pnComp; 

	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("등록"))
		{
			try{
				ShippersTable table = new ShippersTable();
				table.setTable_id(txfTable_id.getText());
				table.setCompany_abbr(txfCompany_Abbr.getText());
				table.setCompany_name(txfCompany.getText());
				table.setQuark_format(txaQuark.getText());
				table.setAgent(txfAgent.getText());
				table.setTable_index(Integer.parseInt(txfPageOrder.getText()));
				table.setPage(Integer.parseInt(txfPage.getText()));
				table.setPort_col(Integer.parseInt(txfPortCount.getText()));
				table.setVsl_row(Integer.parseInt(txfVesselCount.getText()));
				table.setTitle(txfTitle.getText());
				table.setOut_port(txfOutPort.getText());
				table.setOut_to_port(txfOutToPort.getText());
				table.setOthercell(Integer.parseInt(txfOther.getText()));
				table.setIn_port(txfInPort.getText());
				table.setIn_to_port(txfInToPort.getText());				
				table.setGubun(txfGubun.getText());
				try 
				{
					int result=tableService.updateTable(table);

					if(result==1)
					{
						JOptionPane.showMessageDialog(null, "반영 되었습니다.");
					}
				}
				catch (SQLException e1) 
				{
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
				finally
				{
					this.dispose();
				}
			}
			catch(NumberFormatException ee)
			{
				JOptionPane.showMessageDialog(null, "입력형식이 잘못 되었습니다.");
			}

		}
		else
		{
			this.dispose();
		}

	}
	public void createAndUpdateUI() {

		this.txfTable_id		= new JTextField(20);
		txfTable_id.addFocusListener(this);
		this.txfCompany	 		= new JTextField(20);
		txfCompany.addFocusListener(this);
		this.txfCompany_Abbr 	= new JTextField(20);
		txfCompany_Abbr.addFocusListener(this);
		this.txfAgent 			= new JTextField(20);
		txfAgent.addFocusListener(this);
		this.txfPage 			= new JTextField(5);
		this.txfPage.addFocusListener(this);
		this.txfOutPort 		= new JTextField(20);
		this.txfOutPort.addFocusListener(this);
		this.txfOutToPort		= new JTextField(20);
		this.txfOutToPort.addFocusListener(this);
		this.txfInPort 			= new JTextField(20);
		this.txfInPort.addFocusListener(this);
		this.txfInToPort 		= new JTextField(20);
		this.txfInToPort.addFocusListener(this);
		this.txfGubun 			= new JTextField(4);
		this.txfGubun.addFocusListener(this);
		this.txfTitle 			= new JTextField(20);
		this.txfTitle.addFocusListener(this);
		this.txfPageOrder		= new JTextField(5);
		this.txfPageOrder.addFocusListener(this);
		this.txaCommon 			= new JTextArea(5,25);
		this.txaQuark 			= new JTextArea(5,25);
		this.txfPortCount 		= new JTextField(2);
		this.txfPortCount.addFocusListener(this);
		this.txfVesselCount 	= new JTextField(2);
		this.txfVesselCount.addFocusListener(this);
		this.txfOther			= new JTextField(2);
		this.txfOther.addFocusListener(this);


		this.setTitle("테이블 정보 수정");
		Box pnMain = new Box(BoxLayout.Y_AXIS);
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEADING);
		flowLayout.setVgap(2);
		JPanel pnTable = new JPanel();
		TitledBorder tableInfoborder = BorderFactory.createTitledBorder("테이블 정보");
		GridLayout gridLayout = new GridLayout(1,0);
		gridLayout.setVgap(4);
		pnTable.setLayout(gridLayout);

		pnTable.setBorder(tableInfoborder);
		pnMain.add(createForm("테이블 ID : ", txfTable_id));
		pnTable.setLayout(new GridLayout(0,1));
		pnTable.add(createForm("에이전트 : ", txfAgent));
		pnTable.add(createForm("선사명 : ", txfCompany));
		pnTable.add(createForm("선사명 약어 : ", txfCompany_Abbr));
		pnTable.add(createForm("제목 : ", txfTitle));
		JPanel pnPage = new JPanel();
		pnPage.setLayout(new  FlowLayout(FlowLayout.LEADING));
		//pnPage.add(createForm("페이지 : ", txfPage));		

		pnPage.add(txfPage);


		JLabel lblPageOrder = new JLabel("인덱스 : ");
		pnPage.add(lblPageOrder);
		pnPage.add(txfPageOrder);
		pnTable.add(createForm("페이지 : ", pnPage));
		JPanel pnPage2 = new JPanel();
		pnPage2.setLayout(new  FlowLayout(FlowLayout.LEADING));
		pnPage2.add(txfPortCount);		

		JLabel lblVslCount = new JLabel("선박수 : ");
		pnPage2.add(lblVslCount);
		pnPage2.add(txfVesselCount);

		JLabel lblOther = new JLabel("기타 : ");
		pnPage2.add(lblOther);
		pnPage2.add(txfOther);
		pnTable.add(createForm("항구수 : ",pnPage2));

		pnMain.add(pnTable);


		JTabbedPane pnBound = new JTabbedPane();
		JPanel pnOutBound = new JPanel();
		TitledBorder inoutBorder = BorderFactory.createTitledBorder("수출/수입항 등록");

		pnBound.setBorder(inoutBorder);
		pnOutBound.setLayout(new GridLayout(0,1));
		pnOutBound.add(createForm("국내항", txfOutPort));
		pnOutBound.add(createForm("외국항", txfOutToPort));

		JPanel pnInBound = new JPanel();
		pnInBound.setLayout(new GridLayout(0,1));
		pnInBound.add(createForm("국내항", txfInPort));
		pnInBound.add(createForm("외국항", txfInToPort));

		pnBound.addTab("수출", pnOutBound);
		pnBound.addTab("수입", pnInBound);
		pnMain.add(pnBound);

		JPanel pnSub = new JPanel();
		pnSub.setLayout(new BorderLayout());
		TitledBorder otherborder = BorderFactory.createTitledBorder("기타");
		pnSub.setBorder(otherborder);

		JPanel pnGubun = new JPanel();
		pnGubun.setLayout(new FlowLayout(FlowLayout.LEFT));
		butNNN = new JRadioButton("NNN");
		butNNN.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				txfGubun.setText("NNN");
				
			}});
		butTS = new JRadioButton("TS");
		butTS.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				txfGubun.setText("TS");
				
			}});
		
		butNomal = new JRadioButton("처리");
		butNomal.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				txfGubun.setText("");
				
			}});

		pnGubun.add(txfGubun);
		pnGubun.add(butNomal);
		pnGubun.add(butNNN);
		pnGubun.add(butTS);
		ButtonGroup bg = new ButtonGroup();
		bg.add(butNomal);
		bg.add(butNNN);
		bg.add(butTS);

		pnSub.add(createForm("구분", pnGubun),BorderLayout.NORTH);

		JTabbedPane pane =new JTabbedPane();
		pane.addTab("공동배선", new JScrollPane(txaCommon));
		pane.addTab("QuarkFormat", new JScrollPane(txaQuark));
		pnSub.add(pane,BorderLayout.CENTER);

		pnMain.add(pnSub);

		txfTable_id.setEditable(false);

		this.getContentPane().add(buildInfo(), BorderLayout.NORTH);
		this.getContentPane().add(pnMain, BorderLayout.CENTER);
		this.getContentPane().add(buildButtom(), BorderLayout.SOUTH);
		this.getContentPane().add(createMargin(), BorderLayout.EAST);
		this.getContentPane().add(createMargin(), BorderLayout.WEST);
		this.setSize(375, 700);
		ViewUtil.center(this, false);
		this.setShipperTableData(tableData);
		this.setVisible(true);
	}


	private Component buildInfo() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout( new FlowLayout(FlowLayout.LEFT));
		JLabel lbl =  new JLabel("광고 테이블에  대한 정보를 수정합니다.");
		pnMain.add(lbl);
		pnMain.setBackground(Color.white);
		return pnMain;
	}
	public void focusGained(FocusEvent e) {
		Object f =e.getSource();
		if(f instanceof JTextField)
		{
			((JTextField) f).selectAll();
			if(f.equals(txfOutToPort))
			{
				StringTokenizer stringTokenizer = new StringTokenizer(txfOutPort.getText(),"#");
				if(stringTokenizer.countTokens()>0)
				{
					String portCount=txfPortCount.getText();
					Vector intCount = new Vector();
					while(stringTokenizer.hasMoreTokens())
					{
						try
						{
							int in = Integer.parseInt(stringTokenizer.nextToken());
							intCount.add(in);
						}catch(NumberFormatException nume)
						{
							System.err.println(nume.getMessage());
						}
					}



					int pCount = Integer.parseInt(portCount);
					String dd="";
					for(int i=1;i<pCount+1;i++)
					{
						boolean flag=true;
						for(int j=0;j<intCount.size();j++)
						{
							int a = (Integer)intCount.get(j);
							if(a==i)
							{
								flag=false;
							}
						}

						if(flag)
						{
							dd+=String.valueOf(i);
							if(i<pCount)
								dd+="#";
						}

					}
					txfOutToPort.setText(dd);
				}

			}
			if(f.equals(txfInToPort))
			{
				StringTokenizer stringTokenizer = new StringTokenizer(txfInPort.getText(),"#");
				if(stringTokenizer.countTokens()>0)
				{
					String portCount=txfPortCount.getText();
					Vector intCount = new Vector();
					while(stringTokenizer.hasMoreTokens())
					{
						try
						{
							int in = Integer.parseInt(stringTokenizer.nextToken());
							intCount.add(in);
						}catch(NumberFormatException nume)
						{
							System.err.println(nume.getMessage());
						}
					}



					int pCount = Integer.parseInt(portCount);
					String dd="";
					for(int i=1;i<pCount+1;i++)
					{
						boolean flag=true;
						for(int j=0;j<intCount.size();j++)
						{
							int a = (Integer)intCount.get(j);
							if(a==i)
							{
								flag=false;
							}
						}

						if(flag)
						{
							dd+=String.valueOf(i);
							if(i<pCount)
								dd+="#";
						}

					}
					txfInToPort.setText(dd);
				}
			}
		}else if (f instanceof JTextArea)
		{
			((JTextArea) f).selectAll();
		}

	}
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}
}
