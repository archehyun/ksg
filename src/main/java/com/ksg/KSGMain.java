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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.ksg.common.util.PropertiManager;
import com.ksg.view.KSGLogin;
import com.ksg.view.KSGMainFrame;

public class KSGMain 
{
	protected Logger logger = Logger.getLogger(getClass());
	
	PropertiManager manager = PropertiManager.getInstance();

	public KSGMain() 
	{
		System.out.println(manager.getProperties());
		//process(8000);
		
	}


	private ServerSocket serverSocket;

	KSGMainFrame frame;



	public void start() {
		try{
			System.out.println("start");
			logger.info("PROGRAM START");
			logger.info("DB Connected..");

			if(new File("ksg.jar").isFile())
			{
				
				logger.debug("load jar file");
				JarFile file = new JarFile("ksg.jar");
				JarEntry en = file.getJarEntry("db.properties");
				InputStream input=file.getInputStream(en);
				Properties pp = new Properties();
				pp.load(input);
			}
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
		new KSGMain().start();

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
