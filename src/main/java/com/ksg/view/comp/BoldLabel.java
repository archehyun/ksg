package com.ksg.view.comp;

import java.awt.Font;

import javax.swing.JLabel;

/**

  * @FileName : BoldLabel.java

  * @Date : 2021. 2. 26. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� : ����� ���� ��, ���� ����

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
