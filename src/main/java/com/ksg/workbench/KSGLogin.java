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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.common.resources.Resources;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.model.Configure;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.service.MemberService;
import com.ksg.service.impl.MemberServiceImpl;


/**

  * @FileName : KSGLogin.java

  * @Project : KSG2

  * @Date : 2022. 3. 9. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class KSGLogin extends JDialog implements ComponentListener{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = -7969440705693961226L;
	
	private JTextField txfID, txfPassword;
	
	private MemberService service;
	
	private JProgressBar bar;
	
	private JPanel pnProgress;
	
	private JPanel pnMain;
	
	private Properties properties = new Properties();
	
	private Properties db_properties = new Properties();
	private String url,db;
	
	Configure config; 
	public KSGLogin() {
		try
		{
			
			config = Configure.getInstance();
			String resource = "config/db.properties";
			Reader reader = Resources.getResourceAsReader(resource);
			
			
			properties.load(Resources.getResourceAsReader("config/ksg.properties"));
			db_properties.load(reader);			

			url = config.getProperty("mssql.ip");
			if(url.startsWith("$"))
			{
				String newUrl =url.substring(2,url.length()-1);
				url = db_properties.getProperty(newUrl);
			}
			db = (String) db_properties.get("mssql.db");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		service = new MemberServiceImpl();
		createAndUpdateUI();
	}

	private void createAndUpdateUI() {
		
		this.addComponentListener(this);

		bar = new JProgressBar();
		
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

			if(service.login(id, pw))
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
		}
		
		catch (ResourceNotFoundException e1) {

			JOptionPane.showMessageDialog(KSGLogin.this, id+":id가 존재하지 않습니다.");
			logger.error("no exist id");
			return;
		}
		
		catch (Exception e1) {
			e1.getStackTrace();
			JOptionPane.showMessageDialog(KSGLogin.this, "error:"+e1.getMessage());
			
			return;
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		bar.setIndeterminate(true);
		pnMain.setVisible(false);
		pnProgress.setVisible(true);
		System.out.println("frame:"+KSGModelManager.getInstance().frame);
		

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


}
