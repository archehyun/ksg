package com.ksg.view.comp;

import java.awt.Color;

public class ColorData {
	public Color m_color;

	  public Object m_data;

	  public static Color GREEN = new Color(0, 128, 0);

	  public static Color RED = Color.red;
	  public static Color Black = Color.black;

	  public ColorData(Color color, Object data) {
	    m_color = color;
	    m_data = data;
	  }

	  public ColorData(Double data) {
	    m_color = data.doubleValue() >= 0 ? GREEN : RED;
	    m_data = data;
	  }

	  public String toString() {
	    return m_data.toString();
	  }
}
