package com.ksg.common.view.comp;

import javax.swing.JCheckBox;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class PageInfo extends JCheckBox {
	
	private static final long serialVersionUID = 1L;
	
	public Object chekInfo;
	
	public PageInfo() {
		super();
	}

	public PageInfo(int page) {
		this();
		this.setText(String.valueOf(page));
		this.chekInfo=page;
	}
	public PageInfo(String company) {
		this();
		this.setText(company);
		this.chekInfo=company;
	}
	public String toString ()
	{
		return this.getText();
	}
}
