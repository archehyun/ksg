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
package com.ksg.workbench;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Member;
import com.ksg.service.MemberService;
import com.ksg.service.impl.MemberServiceImpl;

import java.io.Reader;


public class KSGLogin extends JDialog {
	protected Logger logger = Logger.getLogger(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = -7969440705693961226L;
	
	private KSGMainFrame mainFrame;
	
	private JTextField txfID, txfPassword;
	
	private MemberService service;
	private JProgressBar bar;
	private JPanel pnProgress;
	private JPanel pnCenter;
	private JPanel pnMain;
	private SqlMapClient sqlMap;
	private Properties properties = new Properties();
	private Properties db_properties = new Properties();
	private String url,db;
	public KSGLogin() {
		try
		{
			String resource = "db.properties";
			Reader reader = Resources.getResourceAsReader(resource);
			properties.load(new FileInputStream("ksg.properties.txt"));
			db_properties.load(reader);			

			url = db_properties.getProperty("mssql.ip");
			if(url.startsWith("$"))
			{
				String newUrl =url.substring(2,url.length()-1);
				url = db_properties.getProperty(newUrl);
			}
			db = (String) db_properties.get("mssql.db");
		} 
		catch (FileNotFoundException e) 
		{			
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			sqlMap=SqlMapManager.getSqlMapInstance();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		service = new MemberServiceImpl();
		createAndUpdateUI();
	}

	private void createAndUpdateUI() {

		bar = new JProgressBar();
		pnCenter = new JPanel();
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon("images/loginlogo2.png"));

		JPanel pnStart = new JPanel();
		pnStart.setLayout(new BorderLayout());
		JPanel pnSubLogin = new JPanel();

		pnStart.add(label,BorderLayout.NORTH);
		pnStart.add(pnSubLogin,BorderLayout.CENTER);

		pnProgress = new JPanel();
		pnProgress.add(new JLabel("화면 초기화..."));
		pnProgress.add(bar);
		pnStart.add(pnProgress,BorderLayout.SOUTH);
		pnProgress.setVisible(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.getContentPane().add(label,BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(pnProgress,BorderLayout.SOUTH);
		this.setResizable(false);

		ViewUtil.center(this, true);
		this.setVisible(true);


	}

	private Component buildCenter() {
		pnMain = new JPanel();
		pnMain.setLayout(new GridLayout(0,1));

		txfID = new JTextField(20);
		txfPassword = new JPasswordField(20);
		txfID.setText("test");
		txfPassword.setText("test");

		txfPassword.addKeyListener(new KeyAdapter(){

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					loginAction();
				}
			}
		});
		JPanel pnControl = new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("확인");

		butOK.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				loginAction();

			}});
		butOK.setPreferredSize(new Dimension(100,25));
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				txfID.setText("");
				txfPassword.setText("");

			}});
		butCancel.setPreferredSize(new Dimension(100,25));
		pnControl.add(butOK);
		pnControl.add(butCancel);

		//pnMain.add(createItem("DB Server : ", new JLabel(url+":"+db)));
		pnMain.add(createItem("사용자 이름 : ", txfID));
		pnMain.add(createItem("암호 : ", txfPassword));
		JPanel pnControlMain = new JPanel(new BorderLayout());

		JPanel pnControlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));		 
		pnControlLeft.add(new JLabel(KSGModelManager.getInstance().version));
		pnControlMain.add(pnControlLeft,BorderLayout.WEST);
		pnControlMain.add(pnControl,BorderLayout.EAST);
		pnMain.add(pnControlMain);

		return pnMain;
	}

	private JComponent createItem(String label,JComponent comp)
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label2 = new JLabel(label);
		label2.setHorizontalAlignment(JLabel.LEFT);
		label2.setPreferredSize(new Dimension(125,25));
		JPanel pnGap = new JPanel();
		pnGap.setPreferredSize(new Dimension(50,25));
		panel.add(pnGap);
		panel.add(label2);
		panel.add(comp);
		return panel; 
	}

	private void loginAction() {

		String id = txfID.getText();
		String pw = txfPassword.getText();

		if(id==""||id==null||id.length()<1)
		{
			JOptionPane.showMessageDialog(KSGLogin.this, "ID를 입력하세요");
			return;
		}

		if(pw==""||pw==null||pw.length()<1)
		{
			JOptionPane.showMessageDialog(KSGLogin.this, "Password를 입력하세요");
			return;
		}
		try {
			Member member = service.selectMember(id);
			if(member==null)
			{
				JOptionPane.showMessageDialog(KSGLogin.this, id+":정보가 존재하지 않습니다.");
				logger.error("no exist id");
				return;
			}
			if(member.isMatchPassword(pw))
			{
				logger.info("login...");
				bar.setIndeterminate(true);
				pnMain.setVisible(false);
				pnProgress.setVisible(true);

				((KSGMainFrame) KSGModelManager.getInstance().frame).completeCardLayout();

			}else
			{
				JOptionPane.showMessageDialog(KSGLogin.this, "Password가 다릅니다.");
				
				logger.error("not match password:"+pw);

				return;
			}
		} catch (SQLException e1) {

			JOptionPane.showMessageDialog(null, "Database에 연결할수 없습니다. \n\n\t이유 : "+e1.getMessage());
			logger.error("do not connected database");


			e1.printStackTrace();
		}
	}


}
