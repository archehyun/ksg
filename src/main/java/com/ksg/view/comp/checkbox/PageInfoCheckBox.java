package com.ksg.view.comp.checkbox;

import javax.swing.JCheckBox;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class PageInfoCheckBox extends JCheckBox {
	
	private static final long serialVersionUID = 1L;
	
	public Object chekInfo;
	
	public PageInfoCheckBox() {
		super();
	}

	public PageInfoCheckBox(int page) {
		this();
		this.setText(String.valueOf(page));
		this.chekInfo=page;
	}
	public PageInfoCheckBox(String company) {
		this();
		this.setText(company);
		this.chekInfo=company;
	}
	public String toString ()
	{
		return this.getText();
	}
}
