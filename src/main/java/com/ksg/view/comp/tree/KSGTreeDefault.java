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

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.CharUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.service.TableService;
import com.ksg.service.impl.TableServiceImpl;
/**
 * @author archehyun
 * @ 기본 트리 컴포넌트
 * 알바벳 정렬 지원
 *
 */
@SuppressWarnings("unchecked")
public class KSGTreeDefault extends KSGTree{

	class CharNode extends DefaultMutableTreeNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private char ch;
		public CharNode(char ch) {
			super(ch);
			this.ch = ch;

		}
		public char getChar() {
			return this.ch;
		}
	}


	class CountNode extends DefaultMutableTreeNode
	{		
		protected int max;
		protected int min;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected int count;
	
		public CountNode(String name)
		{
			super(name);
		}
		public int getCount() {
			return count;
		}
		public boolean isIn(int num)
		{			
			return (min<=num&&num<=max)?true:false;

		}

	}
	class Count10Node extends CountNode
	{		

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Count10Node(int count) {
			super(count*10+"~"+(count*10+9));
			this.count=count;
			min=count*10;
			max =min+9;

		}		

	}
	class Count1000Node extends CountNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Count1000Node(int min, int max) {
			super(min+"~"+max);
			this.min=min;
			this.max =max;
		}		

	}

	public static final char[] ALPA = {		
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S',
		'T','U','X','Y','Z'
	};
	public static final char[] CHOSEONG = { 
		//ㄱ  ㄲ  ㄴ  ㄷ  ㄸ  ㄹ  ㅁ  ㅂ  ㅃ  ㅅ
		'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄹ' , 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 
		//ㅆ  ㅇ  ㅈ  ㅉ  ㅊ  ㅋ  ㅌ  ㅍ  ㅎ
		'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ','ㅌ', 'ㅍ', 'ㅎ' 
	};
	private static final long serialVersionUID = 1L;
	
	//ADVService advService = new ADVServiceImpl();
	
//	DAOManager daomanager = DAOManager.getInstance();
	
	private String groupBy;
	
	private KSGModelManager manager = KSGModelManager.getInstance();

	private Vector pageGroupList;
	
	public DefaultMutableTreeNode root;
	
	private Vector subPageList;

	TableService tableService;
	
	public KSGTreeDefault(String string) {
		super();

		this.setName("KSGTree");
		
		this.groupBy=GroupByPage;
		
//		advService = daomanager.createADVService();
		
		tableService  =new TableServiceImpl();
		

	}
	private void addCharNode(ShippersTable company) {
		
		Iterator iter = subPageList.iterator();
		
		while(iter.hasNext())			
		{
			CharNode subPage=(CharNode) iter.next();
			if(subPage.getChar()==CharUtil.split(company.getCompany_abbr()).charAt(0))// 페이지의 십자리수가 같으면
			{
				try {

					List subcompany = tableService.getTableListByCompany(company.getCompany_abbr());

					subPage.add(createNode(root, company.getPage()+":"+company.getCompany_abbr(), subcompany));
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}
	private boolean addPageNode(ShippersTable table) {
		
		Iterator pageGroup = pageGroupList.iterator();

		while(pageGroup.hasNext())
		{
			CountNode pageGroupNode = (CountNode) pageGroup.next();

			if(pageGroupNode.isIn(table.getPage()))
			{
				pageGroupNode.add(createNode(root, table.getPage()+":"+table.getCompany_abbr()));
				return true;
			}

		}
		return false;
	}
	private DefaultMutableTreeNode createNode(DefaultMutableTreeNode root,Object firstNode)
	{
		DefaultMutableTreeNode sub = new DefaultMutableTreeNode(firstNode);
		return sub;
	}
	private MutableTreeNode createNode(DefaultMutableTreeNode root2,
			String firstNode, List li) {
		DefaultMutableTreeNode sub = new DefaultMutableTreeNode(firstNode);

		return sub;
	}
	public DefaultMutableTreeNode getRoot() {
		return root;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy=groupBy;

	}
	public void update() {

		try {
			pageGroupList = new Vector();
			// 테이블 정보
			List<ShippersTable>  li=tableService.selectCompanyListGroupByPage();

			Iterator iter = li.iterator();
			root = new DefaultMutableTreeNode("전체:"+li.size());

			if (KSGTreeDefault.this.groupBy.equals(GroupByPage))
			{

				while(iter.hasNext())
				{
					ShippersTable table = (ShippersTable) iter.next();
					
					if(!addPageNode(table))
					{
						DefaultMutableTreeNode pageGroup = null;
						if(table.getPage()<1000)
						{
							pageGroup = new Count10Node(table.getPage()/10);
						}else
						{
							pageGroup = new Count1000Node(1000,10000);
						}
						DefaultMutableTreeNode  node
						= new DefaultMutableTreeNode(table.getPage()+":"+table.getCompany_abbr());
						pageGroup.add(node);
						root.add(pageGroup);
						pageGroupList.add(pageGroup);
						
					}
					
				}

			}
			else if(KSGTreeDefault.this.groupBy.equals(GroupByCompany))
			{
				subPageList =  new Vector();
				for(int i=0;i<ALPA.length;i++)

				{
					DefaultMutableTreeNode subPage = new CharNode(ALPA[i]);

					root.add(subPage);
					subPageList.add(subPage);
				}

				for(ShippersTable company: li)
				{

					addCharNode(company);

				}//
			}

			DefaultTreeModel model = new DefaultTreeModel(root);
			
			KSGTreeDefault.this.setModel(model);

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e.getMessage());
			e.printStackTrace();
		}


	}


}
