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
package com.ksg.workbench.common.comp.tree;

import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public abstract class KSGTree extends JTree{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String GroupByCompany="company_abbr";
	public static final String GroupByPage="page";
	public abstract DefaultMutableTreeNode getRoot();
	public static TreePath find(JTree tree, TreePath parent, Object[] nodes, int depth)
	{
		TreeNode node = (TreeNode)parent.getLastPathComponent();
		Object o= node;
		if(o.equals(nodes[depth]))
		{
			if(depth==nodes.length-1)
				return parent;
			if(node.getChildCount()>=0)
			{
				for(Enumeration e = node.children();e.hasMoreElements();)
				{
					TreeNode n =(TreeNode)e.nextElement();
					TreePath path = parent.pathByAddingChild(n);
					TreePath result = find(tree,path, nodes, depth+1);
					if(result!=null)
						return result;
				}
			}
		}
		return null;
	}
	public static  DefaultMutableTreeNode searchNodeByCompany (KSGTree tree1,String nodeStr)
	{
		DefaultMutableTreeNode node = null;
		Enumeration e = tree1.getRoot().breadthFirstEnumeration();
		while(e.hasMoreElements())
		{
			node =(DefaultMutableTreeNode)e.nextElement();
			StringTokenizer st = new StringTokenizer(node.getUserObject().toString(),":");
			if(st.countTokens()==2)
			{
				String page= st.nextToken();
				String company = st.nextToken();
				if(nodeStr.equalsIgnoreCase(company.toString()))
				{
					return node;
				}
			}
		}
		return null;
	}
	public static DefaultMutableTreeNode searchNodeByPage (KSGTree tree1,int nodeStr)
	{
		DefaultMutableTreeNode node = null;
		Enumeration e = tree1.getRoot().breadthFirstEnumeration();
		while(e.hasMoreElements())
		{
			node =(DefaultMutableTreeNode)e.nextElement();
			StringTokenizer st = new StringTokenizer(node.getUserObject().toString(),":");
			if(st.countTokens()==2)
			{
				try{
					int page= Integer.parseInt(st.nextToken());
					//String company = st.nextToken();
					if(nodeStr==page)
					{
						return node;
					}
				}catch(NumberFormatException es)
				{
					continue;
				}
			}
		}
		return null;
	}

}
