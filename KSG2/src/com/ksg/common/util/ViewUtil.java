/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.common.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JComponent;

public class ViewUtil {
	
	public static void center(Window w) {
		ViewUtil.center(w, false);
	}
	
	
	public static void center(Window w,boolean packFrame) {
		// After packing a Frame or Dialog, centre it on the screen.
		if(packFrame)
		{
			w.pack();
		}else
		{
			w.validate();
		}
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = w.getSize();
		if(frameSize.height>screenSize.height)
		{
			frameSize.height=screenSize.height;
		}
		if(frameSize.width>screenSize.width)
		{
			frameSize.width=screenSize.width;
		}	
		
		w.setLocation((screenSize.width-frameSize.width)/2, (screenSize.height-frameSize.height)/2);
	}
	public static Component smartRequestFocus(Component component) {
	    if (requestFocus(component))
	      return component;

	    if (component instanceof JComponent) {
	      FocusTraversalPolicy policy = ((JComponent) component).getFocusTraversalPolicy();

	      if (policy != null) {
	        Component focusComponent = policy.getDefaultComponent((Container) component);

	        if (focusComponent != null && requestFocus(focusComponent)) {
	          return focusComponent;
	        }
	      }
	    }

	    if (component instanceof Container) {
	      Component[] children = ((Container) component).getComponents();

	      for (int i = 0; i < children.length; i++) {
	        component = smartRequestFocus(children[i]);

	        if (component != null)
	          return component;
	      }
	    }

	    return null;
	  }
	  /**
	   * Requests focus unless the component already has focus. For some weird
	   * reason calling {@link Component#requestFocusInWindow()}when the
	   * component is focus owner changes focus owner to another component!
	   *
	   * @param component the component to request focus for
	   * @return true if the component has focus or probably will get focus,
	   *         otherwise false
	   */
	  public static boolean requestFocus(Component component) {
	    /*
	     * System.out.println("Owner: " +
	     * System.identityHashCode(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()) + ", " +
	     * System.identityHashCode(component) + ", " +
	     * (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() ==
	     * component));
	     */
	    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == component ||
	           component.requestFocusInWindow();
	  }

}
