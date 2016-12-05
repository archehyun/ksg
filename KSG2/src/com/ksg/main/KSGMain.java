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
package com.ksg.main;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.ksg.view.KSGLogin;
import com.ksg.view.KSGMainFrame;

public class KSGMain 
{
	protected Logger logger = Logger.getLogger(getClass());

	public KSGMain() 
	{
		try{
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				// TODO Auto-generated catch block
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

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		KSGMain main= new KSGMain();


	}

}
