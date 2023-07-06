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

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.PropertiManager;
import com.ksg.workbench.KSGLogin;
import com.ksg.workbench.admin.KSGMainFrame;

import lombok.extern.slf4j.Slf4j;


/**

  * @FileName : App.java

  * @Project : KSG2

  * @Date : 2021. 12. 13. 

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� : ���� Ŭ����

  */
@Slf4j

public class App 
{
	
	PropertiManager manager = PropertiManager.getInstance();

	public App() 
	{	
//		ApplicationContext ac = new AnnotationConfigApplicationContext(ViewConfiguration.class);
	}


	private ServerSocket serverSocket;


	public void start() {
		try{
	
			log.info("PROGRAM START");
			log.info("DB Connected..");

			

			//UIManager.put("Button.background", new ColorUIResource(Color.DARK_GRAY));
			//UIManager.put("Button.forground", new ColorUIResource(Color.white));
			
			try {
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
				
//				for (Map.Entry<Object, Object> entry : javax.swing.UIManager.getDefaults().entrySet()) {
//				    Object key = entry.getKey();
//				    Object value = javax.swing.UIManager.get(key);
//				    if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
//				        javax.swing.plaf.FontUIResource fr=(javax.swing.plaf.FontUIResource)value;
//				        javax.swing.plaf.FontUIResource f = new javax.swing.plaf.FontUIResource("����ü", fr.getStyle(), 12);
//				        javax.swing.UIManager.put(key, f);
//				    }
//				}
	 
				 
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		
			UIManager.put("ComboBox.background", new ColorUIResource(Color.white));
			
			KSGLogin login = new KSGLogin();		

			KSGMainFrame frame = new KSGMainFrame(login);
			
			KSGModelManager.getInstance().frame=frame;
			
			frame.createAndUpdateUI();
			
			frame.completeCardLayout();


		}catch(Exception e)
		{
			log.error(e.getMessage());
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
			//port�� �̹� �����Ǿ� �ִ� ��� Exception �߻� }
			close();
			JOptionPane.showMessageDialog(null, "���� ���α׷��� �������Դϴ�.");
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
