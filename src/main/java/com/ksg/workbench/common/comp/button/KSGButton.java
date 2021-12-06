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
package com.ksg.workbench.common.comp.button;

import java.awt.Dimension;

import javax.swing.JButton;

/**

  * @FileName : KSGButton.java

  * @Project : KSG2

  * @Date : 2021. 12. 6. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class KSGButton extends JButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KSGButton() {
		super();
		this.setPreferredSize(new Dimension(100,25));
	}

}
