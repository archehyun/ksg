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
package com.ksg.common.view.comp;

import java.awt.Component;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.model.KSGObserver;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class KSGTreeType2 extends JTree implements KSGObserver{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private class MyRenderer extends DefaultTreeCellRenderer {
	    Icon tutorialIcon;

	    public MyRenderer(Icon icon) {
	      tutorialIcon = icon;
	    }

	    public Component getTreeCellRendererComponent(JTree tree, Object value,
	        boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

	      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
	          hasFocus);
	      if (leaf && isTutorialBook(value)) {
	        setIcon(tutorialIcon);
	        setToolTipText("This book is in the Tutorial series.");
	      } else {
	        setToolTipText(null); // no tool tip
	      }

	      return this;
	    }

	    protected boolean isTutorialBook(Object value) {
	      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	      BookInfo nodeInfo = (BookInfo) (node.getUserObject());
	      String title = nodeInfo.bookName;
	      if (title.indexOf("Tutorial") >= 0) {
	        return true;
	      }

	      return false;
	    }
	  }
	private class BookInfo {
	    public String bookName;
	    public URL bookURL;

	    public BookInfo(String book, String filename) {
	      bookName = book;
	      bookURL = KSGTreeType2.class.getResource(filename);
	      if (bookURL == null) {
	        System.err.println("Couldn't find file: " + filename);
	      }
	    }

	    public String toString() {
	      return bookName;
	    }
	  }
	public void update(KSGModelManager manager) {
		// TODO Auto-generated method stub
		
	}



}
