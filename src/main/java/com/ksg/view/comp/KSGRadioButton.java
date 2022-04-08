package com.ksg.view.comp;

import java.awt.Color;

import javax.swing.JRadioButton;

/**

  * @FileName : KSGRadioButton.java

  * @Project : KSG2

  * @Date : 2022. 3. 14. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class KSGRadioButton extends JRadioButton{

	public KSGRadioButton(String string) {
		super(string);
		this.setBackground(Color.white);
	}

	public KSGRadioButton(String string, boolean b) {
		super(string, b);
		this.setBackground(Color.white);
	}
	
	

}
