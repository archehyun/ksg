package com.ksg.common.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.view.comp.dialog.KSGDialog;

@SuppressWarnings("serial")
public class PortSearchDialog extends KSGDialog implements ActionListener{
	protected Logger 		logger = Logger.getLogger(this.getClass());
	public String portName;
	int x,y,h;
	public PortSearchDialog(Dialog dialog) {	
		super(dialog);
		x=dialog.getX()+dialog.getWidth();
		y=dialog.getY();
		h=dialog.getHeight();
		baseService = new BaseServiceImpl();
	}
	
	JList portLi;
	public void createAndUpdateUI() {
		this.setModal(true);
		
		this.getContentPane().add(buildCenter());
		this.getContentPane().add(createMargin(),BorderLayout.WEST);
		this.getContentPane().add(createMargin(),BorderLayout.EAST);
		this.getContentPane().add(buildInfo(),BorderLayout.NORTH);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		this.setSize(250, h);
		this.setLocation(x, y);
		this.setVisible(true);
		
	}
	private Component buildCenter() {
		
		
		Box pnMain = new Box(BoxLayout.Y_AXIS);
		txfPortName = new JTextField(10);
		txfPortName.addKeyListener(new KeyAdapter() {
			
		
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					JTextField txf =(JTextField) e.getSource();
					String keyword=txf.getText();
					try {
						List li=baseService.getPortListByPatten(keyword);
						logger.debug("search : "+keyword+","+li.size());
						DefaultListModel defaultListModel = new DefaultListModel();
						for(int i=0;i<li.size();i++)
						{
							defaultListModel.addElement(li.get(i));
						}
						portLi.setModel(defaultListModel);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
				}				
				
			}
		});
		JPanel pn0 =  new JPanel();
		pn0.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lbl = new JLabel("항구명 : ");
		

		pn0.add(lbl);
		pn0.add(txfPortName);
		pnMain.add(pn0);
		JPanel pn1 = new JPanel();
		pn1.setLayout( new BorderLayout());
		portLi = new JList();
		DefaultListModel defaultListModel = new DefaultListModel();
		portLi.setModel(defaultListModel);
		
		pn1.add(new JScrollPane(portLi));
		pnMain.add(pn1);
		
		if(portName!=null&&portName.length()>0)
		{
			this.setPortName(portName);
			this.txfPortName.setText(portName);
			String keyword=txfPortName.getText();
			try {
				List li=baseService.getPortListByPatten(keyword);
				logger.debug("search : "+keyword+","+li.size());
				DefaultListModel defaultListModel2 = new DefaultListModel();
				for(int i=0;i<li.size();i++)
				{
					defaultListModel2.addElement(li.get(i));
				}
				portLi.setModel(defaultListModel2);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}					
		}
		
		
		return pnMain;
	}
	public Component buildControl()
	{
		JPanel pnMain = new JPanel();
		
		
		JButton butOk = new JButton("확인");
		butOk.addActionListener(this);
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(this);
		pnMain.add(butOk);
		pnMain.add(butCancel);
		
		return pnMain;
	}
	public Component buildInfo()
	{
		JPanel pnMain = new JPanel();
		pnMain.setBackground(Color.white);
		pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnMain.add(new JLabel("항구 명을 검색합니다."));
		
		return pnMain;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		if(command.equals("확인"))
		{
			if(portLi.getSelectedIndex()==-1)
			{
				JOptionPane.showMessageDialog(null, "선택된 항구명이 없습니다.");
				return;
			}
			this.portName= (String) portLi.getSelectedValue();
			setVisible(false);
			dispose();
		}else if(command.equals("취소"))
		{
			this.portName=null;
			setVisible(false);
			dispose();
		}
		
	}
	int row;
	private JTextField txfPortName;
	public void setRow(int row) {
		this.row=row;
		
	}
	public void setPortName(String portname) {
		this.portName = portname;
		
		
	}

}
