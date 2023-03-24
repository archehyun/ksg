package com.ksg.workbench.common.comp.button;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.ksg.view.comp.KSGViewUtil;

public class GradientButton extends JButton{
	
	
	private KSGViewUtil propeties = KSGViewUtil.getInstance();
	
			
	private Color color1;
	private Color color2;
	private Image img;
	private Timer timer;
	private Timer timerPressed;
	private float alpha  =0.3f;
	private boolean mouseOver;
	private boolean pressed;
	private Point pressedLocation;
	private float pressedSize;
	private float sizeSpeed = 0.5f;
	
	private float alphaPressed = 0.5f;
	
	private String style = "Button.background";
	
	public void setGradientColor(Color color1, Color color2)
	{
		this.color1 = color1;
		this.color2 = color2;
	}
	public void setStyle()
	{
		String buttonColors=propeties.getProperty(style);
		Color colors[] = KSGViewUtil.getGradientColor(buttonColors);
		this.color1 = colors[0];
		this.color2 = colors[1];
	}
	public GradientButton()
	{	
		setStyle();
		setContentAreaFilled(false);
		setForeground(Color.white); 
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setBorder(new EmptyBorder(4,15,4,15));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				mouseOver = true ;
				timer.start();
				
			}
			@Override
			 public void mouseExited(MouseEvent e) {
				 mouseOver = false ;
				timer.start(); 
			 }

		    
		    public void mousePressed(MouseEvent e) {
		    	pressedSize =0;
		    	alphaPressed =0.5f;
		    	pressed = true;
		    	pressedLocation = e.getPoint();
		    	timerPressed.setDelay(0);
		    	timerPressed.start();
		    }

		   
		});
		timer = new Timer(40, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mouseOver)
				{
					if(alpha<0.6f)
					{
						alpha += 0.05f;
					}else
					{
						alpha =0.6f;
						timer.stop();
					}
				}
				else
				{
					if(alpha>0.3f)
					{
						alpha -= 0.05f;
					}else
					{
						alpha =0.3f;
						timer.stop();
						
					}
				}
				repaint();
				
			}
		});
		timerPressed = new Timer(0, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pressedSize+=sizeSpeed;
				if(alphaPressed<=0)
				{
					pressed = false;
					timerPressed.stop();
				}
				else
				{
					repaint();
				}
			}
		});
	}
	
	public GradientButton(String string) {
		this();
		
		this.setText(string);
	}
	
	public GradientButton(String string, String imgFilePath) {
		this(string, imgFilePath, 15,15);
	}
	
	public GradientButton(String string, String imgFilePath, int w, int h) {
		this();
		
		this.setText(string);
		
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(imgFilePath));
        
        img = icon.getImage();

        Image newImg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        
        ImageIcon newIcon = new ImageIcon(newImg);    
        
        this.setIcon(newIcon);
	}
	
	

	@Override
	protected void paintComponent(Graphics g) {
		
		int width = getWidth();
		
		int heigth = getHeight();
		
		BufferedImage img = new BufferedImage(width,heigth, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2= img.createGraphics();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		GradientPaint gra = new GradientPaint(0, 0, color1, width, 0, color2);
		
		g2.setPaint(gra);
		
		g2.fillRoundRect(0, 0, width, heigth, heigth/2, heigth/2);
		
		createStyle(g2);
		
		if(pressed)
		{
			paintPressed(g2);
		}
		g.drawImage(img, 0,0, null);
		super.paintComponent(g);
	}
	
	private void createStyle(Graphics2D g)
	{
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		int width = getWidth();
		int height = getHeight();
		GradientPaint gra = new GradientPaint(0, 0, Color.white, width, 0, new Color(255,255,255,60));
		g.setPaint(gra);
		Path2D.Float f = new Path2D.Float();
		f.moveTo(0, 0);
		int controll = height +height/2;
		f.curveTo(0, 0, width/2, controll, width, 0);
		g.fill(f);
		
		
	}
	private void paintPressed(Graphics2D g)
	{
		if(pressedLocation.x - (pressedSize/2)<0&&pressedLocation.x+(pressedSize/2)> getWidth())
		{
			timerPressed.setDelay(20);
			alphaPressed-=0.05f;
			if(alphaPressed<0) alphaPressed=0;
			
		}
		g.setColor(Color.white);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alphaPressed));
		float x = pressedLocation.x -(pressedSize/2);
		float y = pressedLocation.y -(pressedSize/2);
		g.fillOval((int)x, (int)y, (int)pressedSize, (int)pressedSize);
	}
	
	

}
