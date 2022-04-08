package com.ksg.view.comp;

import java.awt.Color;

import javax.swing.JCheckBox;

/**

  * @FileName : KSGCheckBox.java

  * @Project : KSG2

  * @Date : 2022. 3. 12. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class KSGCheckBox extends JCheckBox{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KSGCheckBox(String name)
	{
		super(name);
		this.setBackground(Color.white);
	}

	public KSGCheckBox(String name, boolean isSamePageSelect) {
		super(name, isSamePageSelect);
		this.setBackground(Color.white);
	}
}
