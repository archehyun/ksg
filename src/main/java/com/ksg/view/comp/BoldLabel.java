package com.ksg.view.comp;

import java.awt.Font;

import javax.swing.JLabel;

/**

  * @FileName : BoldLabel.java

  * @Date : 2021. 2. 26. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 사용자 정의 라벨, 볼드 지정

  */
@SuppressWarnings("serial")
public class BoldLabel extends JLabel{
	/**
	 * @param text
	 */
	public BoldLabel(String text) {
		super(text);
		Font font = this.getFont();
		Font newFont = new Font(font.getFontName(),Font.BOLD,font.getSize());
		setFont(newFont);
		
	}

}
