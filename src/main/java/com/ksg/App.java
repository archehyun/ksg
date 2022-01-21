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
package com.ksg;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ksg.common.util.PropertiManager;
import com.ksg.workbench.KSGLogin;
import com.ksg.workbench.KSGMainFrame;


/**

  * @FileName : App.java

  * @Project : KSG2

  * @Date : 2021. 12. 13. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 메인 클래스

  */
public class App 
{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	PropertiManager manager = PropertiManager.getInstance();

	public App() 
	{	
		
	}


	private ServerSocket serverSocket;

	KSGMainFrame frame;



	public void start() {
		try{
	
			logger.info("PROGRAM START");
			logger.info("DB Connected..");


			try {
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		

			KSGLogin login = new KSGLogin();
			javax.swing.SwingUtilities.invokeLater(new Runnable(){

				public void run() {

				}

			});

			KSGMainFrame frame = new KSGMainFrame(login);
			frame.createAndUpdateUI();


		}catch(Exception e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();

		}
	}

	public static void main(String[] args) {
		new App().start();

	}

	public void process(int port) {
		try {
			serverSocket = new ServerSocket(port);
			} catch (IOException e)
		{
			//port가 이미 점유되어 있는 경우 Exception 발생 }
			close();
			JOptionPane.showMessageDialog(null, "동일 프로그램이 실행중입니다.");
			System.exit(1);

		}
	}

	public void close() {
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException e) {
		}
	}
	

}
