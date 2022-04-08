package com.ksg.workbench.adv.comp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.Icon;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class DiamondIcon implements Icon{
	private static final int DEFAULT_HEIGHT = 10;

	private static final int DEFAULT_WIDTH = 10;

	private Color color;

	private int height;

	private Polygon poly;

	private boolean selected;

	private int width;

	public DiamondIcon(Color color) {
		this(color, true, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public DiamondIcon(Color color, boolean selected) {
		this(color, selected, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public DiamondIcon(Color color, boolean selected, int width, int height) {
		this.color = color;
		this.selected = selected;
		this.width = width;
		this.height = height;
		initPolygon();
	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

	private void initPolygon() {
		poly = new Polygon();
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		poly.addPoint(0, halfHeight);
		poly.addPoint(halfWidth, 0);
		poly.addPoint(width, halfHeight);
		poly.addPoint(halfWidth, height);
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.translate(x, y);
		if (selected) {
			g.fillPolygon(poly);
		} else {
			g.drawPolygon(poly);
		}
		g.translate(-x, -y);
	}
}
