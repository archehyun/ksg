package com.ksg.workbench.common.comp.button;

import java.awt.Color;

import com.ksg.view.comp.KSGViewUtil;

import mycomp.comp.GradientButton;

@SuppressWarnings("serial")
public class KSGGradientButton extends GradientButton{
	
	
	private KSGViewUtil propeties = KSGViewUtil.getInstance();
	
	private String style = "Button.background";
	

	public void setStyle()
	{
		String buttonColors=propeties.getProperty(style);
		
		Color colors[] = KSGViewUtil.getGradientColor(buttonColors);
		
		this.color1 = colors[0];
		this.color2 = colors[1];
	}
	public KSGGradientButton() {
		super();
		setStyle();
	}
	
	
	public KSGGradientButton(String string) {
		this();
		
		this.setText(string);
	}
	
	public KSGGradientButton(String string, String imgFilePath) {
		this(string, imgFilePath, 15,15);
	}
	
	public KSGGradientButton(String string, String imgFilePath, int w, int h) {
		this(string);
		this.setImgIcon(imgFilePath, w, h);
	}
}
