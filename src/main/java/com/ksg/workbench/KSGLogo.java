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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class KSGLogo extends JPanel{
	 public void paint(Graphics g) {
		    try {
		      Graphics2D g2D;
		      g2D = (Graphics2D) g;
		      g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		      String fileName = "images/loginlogos2.png";
		      Image img = getToolkit().getImage(fileName);
		      g2D.drawImage(img,0,0,null);
		    } catch (Exception e) {
		    }
		  }


}
