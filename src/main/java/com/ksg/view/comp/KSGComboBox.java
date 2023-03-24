package com.ksg.view.comp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.view.comp.table.KSGTableColumn;

/**

  * @FileName : KSGComboBox.java

  * @Project : KSG2

  * @Date : 2022. 3. 25. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class KSGComboBox extends JComboBox<KSGTableColumn>{

	String codeType;

	CodeServiceImpl service;
	
	private boolean isShowTotal = false;
	
	
	
	public void setShowTotal(boolean isShowTotal) {
		this.isShowTotal = isShowTotal;
	}

	public KSGComboBox()
	{
		service = new CodeServiceImpl();
		this.setUI(new CustomComboUI());
		
	}

	public KSGComboBox(String codeType)
	{
		this();
		this.codeType = codeType;
	}

	public void initComp()
	{  
		this.setBackground(Color.white);
		
		
		if(codeType==null) return;
		
		HashMap<String, Object> param = new HashMap<String, Object>();

		try {
			this.removeAllItems();
			param.put("code_type", codeType);
			HashMap<String,Object> resullt = (HashMap<String, Object>) service.selectCodeDList(param);
			List<HashMap<String,Object> > li = (List<HashMap<String, Object>>) resullt.get("master");
			
			if(isShowTotal) 			addItem(new KSGTableColumn("","전체"));
			
			for(HashMap<String, Object> item:li)
			{
				this.addItem(new KSGTableColumn(String.valueOf(item.get("code_field")), String.valueOf(item.get("code_name"))));
			}
		} catch (Exception e) {

			e.printStackTrace();
		}


	}
	class CustomComboUI extends BasicComboBoxUI
	{
		public void paint(Graphics grphcs, JComponent js)
		{
			super.paint(grphcs, js);
		}
		
		@Override
		protected JButton createArrowButton() {
			return new ArrowButton();
		}
		
	}
	private class ArrowButton extends JButton
	
	{
		public ArrowButton()
		{
			setContentAreaFilled(false);
			setBorder(new EmptyBorder(5,5,5,5));
			setBackground(new Color(150, 150, 150));
		}
		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			
			Graphics2D g2 =(Graphics2D)g;
			
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int width = getWidth();
			int height = getHeight();
			int size = 9;
			int x =(width -size )/2;
			int y =(height -size )/2+3;
			int px[] = {x, x+size, x+size/2};
			int py[]= {y, y, y+size};
			g2.setColor(getBackground());
			g2.fillPolygon(px, py, px.length);
			g2.dispose();
		}
	}
}
