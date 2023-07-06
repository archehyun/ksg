package com.ksg.view.comp.tabpane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import com.ksg.view.comp.panel.KSGPanel;

public class KSGTabedPane extends JTabbedPane implements ActionListener{

	public KSGTabedPane()
	{ 
		setOpaque(false);
		this.setUI(new TabUI());

	}
	@Override
	public void addTab(String title, Component component) {

		Icon closeIcon = new CloseIcon(); 
		JButton btClose=       new JButton(closeIcon);
		btClose.setPreferredSize(new Dimension( closeIcon.getIconWidth(), closeIcon.getIconHeight()));
		btClose.addActionListener(this);

		JLabel lbl = new JLabel(title);
		
		lbl.setForeground(Color.black.brighter());

		// Add some spacing between text and icon, and position text to the RHS.
		lbl.setIconTextGap(5);
		lbl.setHorizontalTextPosition(SwingConstants.RIGHT);       

		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.LEFT))
		{
			@Override
			public void setBackground(Color bg)
			{
				Component comps[]=this.getComponents();
				for(Component comp:comps)
				{
					//comp.setBackground(bg);
				}

				super.setBackground(bg);
			}
		};
		pnMain.add(lbl);
		pnMain.add(btClose);

		// pnMain.setBackground(pnMain.getParent().getBackground());
		//pnMain.setBackground(getBackground());
		btClose.setBackground(btClose.getParent().getBackground());
		// btClose.setBackground(new Color(0X2d5ff5));

		//lbl.setForeground( Color.white);

		super.addTab(title, component);

		setTabComponentAt(this.getTabCount()-1,pnMain);

		setBackgroundAt(this.getTabCount()-1, pnMain.getBackground());
	}

	// @Override
	// protected void paintTabBackground(Graphics g, int tabPlacement,
	//              int tabIndex, int x, int y, int w, int h, boolean isSelected) {
	// }





	public void actionPerformed(ActionEvent e) {
		int i = indexOfTabComponent(((Component) e.getSource()).getParent());
		if (i != -1) {
			remove(i);
		}
	}

	class TabUI extends BasicTabbedPaneUI
	{
		private int inclTab = 4;
		private int anchoFocoV = inclTab;
		private int anchoFocoH = 4;
		private int anchoCarpetas = 18;
		private Polygon shape;
		Color selectColor;
		Color deSelectColor;
		public TabUI()
		{

		}

		@Override
		protected void installDefaults() {
			super.installDefaults();
//			deSelectColor= new Color(0X2d5ff5);

//			selectColor = new Color(0X2d5ff5).brighter();
			
			deSelectColor 	= Color.decode("#ebebeb");

			selectColor 	= Color.white;

			highlight 		= new Color(0, 0, 0, 0);
			
			lightHighlight 	= new Color(0, 0, 0, 0);
			
			shadow 			= new Color(0, 0, 0, 0);
			
			darkShadow 		= new Color(0, 0, 0, 0);
			
			focus 			= new Color(0, 0, 0, 0);
		}

		@Override//    ww   w   .d  e    m  o  2  s  . c o    m
		protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
			return 32;
		}

		@Override
		protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
				Rectangle iconRect, Rectangle textRect) {
			// if (tabIndex == 0) {
			//     rects[tabIndex].height = 30 + 1;
			//     rects[tabIndex].y = 32 - rects[tabIndex].height + 1;
			// } else if (tabIndex == 1) {
			//     rects[tabIndex].height = 26 + 1;
			//     rects[tabIndex].y = 32 - rects[tabIndex].height + 1;
			// }

			// rects[tabIndex].height = 36 + 1;
			//     rects[tabIndex].y = 32 - rects[tabIndex].height + 1;
			super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
		}
		@Override
		protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
			if (tabPane.hasFocus() && isSelected) {
				g.setColor(UIManager.getColor("ScrollBar.thumbShadow"));
				g.drawPolygon(shape);
			}
		}

		@Override
		protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
			Graphics2D g2D = (Graphics2D) g;
			
			GradientPaint gradientShadow;
			
			int xp[] = new int[]{x, x, x + 3, x + w - inclTab - 6, x + w - inclTab - 2, x + w - inclTab, x + w - inclTab, x};
			
			int yp[] = new int[]{y + h, y + 3, y, y, y + 1, y + 3, y + h, y + h};;
			
			gradientShadow = new GradientPaint(0, 0, Color.ORANGE, 0, y + h / 2, new Color(240, 255, 210));
			
			shape = new Polygon(xp, yp, xp.length);
			
			Component comp= getTabComponentAt(tabIndex);

			if (isSelected) {
				g2D.setColor(Color.decode("#f0f5f5"));
				comp.setBackground(selectColor);
				g2D.fillRect(x, y, w, h);
//				g2D.setPaint(gradientShadow);

			} else {
				// if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
				//     g2D.setColor(deSelectColor);
				//     GradientPaint gradientShadowTmp = new GradientPaint(0, 0, new Color(255, 255, 200), 0, y + h / 2, new Color(240, 255, 210));
				//     g2D.setPaint(gradientShadowTmp);
				// } else {
				//     GradientPaint gradientShadowTmp = new GradientPaint(0, 0, new Color(240, 255, 210), 0, y + 15 + h / 2, new Color(204, 204, 204));
				//     g2D.setPaint(gradientShadowTmp);
				// }
				g2D.setColor(deSelectColor);
				comp.setBackground(deSelectColor);
				g2D.fillRect(x, y, w, h);
				g2D.setColor(Color.lightGray);
				g2D.drawRect(x, y, w, h);
			}

			// g2D.fill(shape);
			// if (runCount > 1) {
			//     g2D.setColor(hazAlfa(getRunForTab(tabPane.getTabCount(), tabIndex) - 1));
			//     g2D.fill(shape);
			// }
			// g2D.fill(shape);

		}
		protected Color hazAlfa(int fila) {
			int alfa = 0;
			if (fila >= 0) {
				alfa = 50 + (fila > 7 ? 70 : 10 * fila);
			}
			return new Color(0, 0, 0, alfa);
		}
	}
	class CloseIcon implements Icon {
		public CloseIcon()
		{

			// this.setBackground(Color.white);
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			//  g.setColor(Color.white);
			//  g.fillRect(0, 0, getIconWidth(), getHeight());
//			g.setColor(Color.white);
			g.drawLine(6, 6, getIconWidth() - 7, getIconHeight() - 7);
			g.drawLine(getIconWidth() - 7, 6, 6, getIconHeight() - 7);
		}
		public int getIconWidth() {
			return 17;
		}
		public int getIconHeight() {
			return 17;
		}
	}

	class TabPanel extends KSGPanel
	{

		public TabPanel(LayoutManager layout) {
			super(layout);


		}
		@Override
		public void setBackground(Color bg)
		{
			Component comps[]=this.getComponents();
			for(Component comp:comps)
			{
				comp.setBackground(bg);
			}

			super.setBackground(bg);
		}
	}

}