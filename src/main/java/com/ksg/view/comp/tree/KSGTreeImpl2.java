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
package com.ksg.view.comp.tree;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.model.KSGObserver;
import com.ksg.common.util.CharUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.service.ADVService;
import com.ksg.service.TableService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.TableServiceImpl;

/**
 * @author 박창현
 *
 */
public class KSGTreeImpl2 extends KSGTree implements KSGObserver{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int _NODE_COUNT = 10;

	private String groupBy;
	
	private KSGModelManager manager = KSGModelManager.getInstance();
	
	private int subDate=1;

	private ADVService advService = new ADVServiceImpl();
	
	private TableService tableService =new TableServiceImpl();
	
	private Vector subPageList;
	
	public DefaultMutableTreeNode root;
	
	public DefaultMutableTreeNode getRoot() {
		return root;
	}


	public void setRoot(DefaultMutableTreeNode root) {
		this.root = root;
	}


	public KSGTreeImpl2(String compName) 
	{
		super();
		this.setName("KSGTree1");
		manager.addObservers(this);
		this.groupBy=GroupByPage;
	} 

	
	private DefaultMutableTreeNode createNode(DefaultMutableTreeNode root,Object firstNode,List li)
	{
		DefaultMutableTreeNode sub = new DefaultMutableTreeNode(firstNode);
		int count=0;
		if(li.size()>subDate)
		{
			count=subDate;
		}else
		{
			count=li.size();
		}
		for(int i=0;i<count;i++)
		{
			ShippersTable shippersTable=(ShippersTable) li.get(i);
			DefaultMutableTreeNode sub0 = new DefaultMutableTreeNode(shippersTable.getDate_isusse());
			sub.add(sub0);
		}

		return sub;
	}
	public void createCharSortTree()
	{
		
		for(int i=0;i<ALPA.length;i++)
			
		{
			DefaultMutableTreeNode subPage = new CharNode(ALPA[i]);

			root.add(subPage);
			subPageList.add(subPage);
		}
		for(int i=0;i<CHOSEONG.length;i++)
			
		{
			DefaultMutableTreeNode subPage = new CharNode(CHOSEONG[i]);

			root.add(subPage);
			subPageList.add(subPage);
		}
	}
	public void createPageSortTree()
	{
		for(int i=0;i<_NODE_COUNT;i++)
		{
			DefaultMutableTreeNode subPage = new CountNode(i*10);

			root.add(subPage);
			subPageList.add(subPage);
		}
	}
	public static final char[] CHOSEONG = { 
		//ㄱ  ㄲ  ㄴ  ㄷ  ㄸ  ㄹ  ㅁ  ㅂ  ㅃ  ㅅ
		'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 
		//ㅆ  ㅇ  ㅈ  ㅉ  ㅊ  ㅋ  ㅌ  ㅍ  ㅎ
		'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ','ㅌ', 'ㅍ', 'ㅎ' /*,
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S',
		'T','U','X','Y','Z'*/
	};
	public static final char[] ALPA = {		
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S',
		'T','U','X','Y','Z'
	};


	@SuppressWarnings("unchecked")
	
	public void update(KSGModelManager manager) {
		try {
			subPageList = new Vector();
			List<ShippersTable> li = advService.getPageList(this.groupBy);
			//int totalCount =li.size();
			//int page =totalCount/_NODE_COUNT;

			root = new DefaultMutableTreeNode("전체테이블:"+li.size());
			int count=0;
			if (this.groupBy.equals(GroupByPage))
			{
				createPageSortTree();
			
			}
			else if(this.groupBy.equals(GroupByCompany))
			{
				createCharSortTree();
			}
			for(ShippersTable company: li)
			{
				if (this.groupBy.equals(GroupByPage))
				{
					addPageNode(company);
				}
				else if(this.groupBy.equals(GroupByCompany))
				{
					addCharNode(company);
				}
				count++;
			}//

			DefaultTreeModel model = new DefaultTreeModel(root);
			this.setModel(model);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addCharNode(ShippersTable company) {
		Iterator iter = subPageList.iterator();
		while(iter.hasNext())
		{
			CharNode subPage=(CharNode) iter.next();
			if(subPage.getChar()==CharUtil.split(company.getCompany_abbr()).charAt(0))// 페이지의 십자리수가 같으면
			{
				try {
					List subcompany = advService.getTableByShipperByDate(company.getCompany_abbr());

					subPage.add(createNode(root, company.getPage()+":"+company.getCompany_abbr(), subcompany));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}


	private void addPageNode(ShippersTable company)
	{
		Iterator iter = subPageList.iterator();
		while(iter.hasNext())
		{
			CountNode subPage=(CountNode) iter.next();
			if(subPage.getCount()/_NODE_COUNT==company.getPage()/_NODE_COUNT)// 페이지의 십자리수가 같으면
			{
				try {
					List subcompany = advService.getTableByShipperByDate(company.getCompany_abbr());					
					subPage.add(createNode(root, company.getPage()+":"+company.getCompany_abbr(), subcompany));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

	public void setGroupBy(String groupBy) {

		this.groupBy=groupBy;
	}


	public String getGroupBy() {
		return groupBy;
	}
	class CountNode extends DefaultMutableTreeNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int count;
		public CountNode(int count) {
			super(count+"~"+(count+9));
			this.count=count;
		}
		public int getCount() {
			return count;
		}
	}
	
	class CharNode extends DefaultMutableTreeNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int count;
		char ch;
		public CharNode(char ch) {
			super(ch);
			this.ch = ch;
			
		}
		public char getChar() {
			// TODO Auto-generated method stub
			return this.ch;
		}
	}
	
}
