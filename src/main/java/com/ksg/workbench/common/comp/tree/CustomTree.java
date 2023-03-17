package com.ksg.workbench.common.comp.tree;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.ShippersTable;

public class CustomTree extends JTree{
	
	
	public static final String PAGE_LIST = "pageList";
	
	public static final String COMPAY_LIST = "compayList";
	
	private CommandMap treeModel;
	
	private String stateType;
	
	private DefaultMutableTreeNode root;
	
	public CustomTree()
	{
		super();
		
		stateType = PAGE_LIST;
	}
	
	
	public void loadModel(CommandMap treeModel)
	{
		this.treeModel = treeModel;
		
		changeState(stateType);
	}
	
	public void changeState(String stateType)
	{
		
		this. stateType = stateType;
		
		if(stateType.equals(PAGE_LIST))
		{
			setModel(getTreeModeByPage());
		}
		else if(stateType.equals(COMPAY_LIST))
		{
			
			setModel(getTreeModeByCompany());
		}
	}
	
	private DefaultTreeModel getTreeModeByPage()
	{
		CommandMap result= treeModel;
		
		root = new DefaultMutableTreeNode("전체:");
		
		Map<Integer,List<ShippersTable>> pageMap= (Map<Integer, List<ShippersTable>>) result.get("pageList");

		Object keys[]=pageMap.keySet().toArray();

		Arrays.sort(keys);

		for(Object keyItem : keys)
		{
			int startPage = (int)keyItem * 10;

			int endPage = (int)keyItem * 10 + 9;

			DefaultMutableTreeNode pageGroup = new DefaultMutableTreeNode(startPage +"~"+endPage);

			List<ShippersTable> table = pageMap.get(keyItem);

			Collections.sort(table, new SortByPage() );

			table.stream().forEach(item -> pageGroup.add(new DefaultMutableTreeNode(item.getPage()+":"+item.getCompany_abbr())));

			root.add(pageGroup);
		}	

		DefaultTreeModel model = new DefaultTreeModel(root);
		
		return model;
	}
	
	private DefaultTreeModel getTreeModeByCompany()
	{
		CommandMap result= this.treeModel;
		
		root = new DefaultMutableTreeNode("전체:");
		
		Map<String,List<String>> companyMap= (Map<String, List<String>>) result.get("companyList");

		Object keys[]=companyMap.keySet().toArray();

		Arrays.sort(keys);

		for(Object keyItem : keys)
		{
			DefaultMutableTreeNode pageGroup = new DefaultMutableTreeNode(keyItem);

			List<String> table = companyMap.get(keyItem);

			table.stream().forEach(item -> pageGroup.add(new DefaultMutableTreeNode(item)));

			root.add(pageGroup);
		}	

		DefaultTreeModel model = new DefaultTreeModel(root);
		return model;
	}
	
	class SortByPage implements Comparator<ShippersTable> {

		@Override
		public int compare(ShippersTable f1, ShippersTable f2) {
			if (f1.getPage() > f2.getPage()) {
				return 1;
			} else if (f1.getPage() < f2.getPage()) {
				return -1;
			}
			return 0;
		}
	}
	public DefaultMutableTreeNode searchNode(Object param)
	{
		DefaultMutableTreeNode node = null;
		
		Enumeration e = root.breadthFirstEnumeration();
		
		while(e.hasMoreElements())
		{
			node =(DefaultMutableTreeNode)e.nextElement();
			
			StringTokenizer st = new StringTokenizer(node.getUserObject().toString(),":");
			
			if(st.countTokens()!=2) continue;
			
			if(param instanceof Integer)
			{
				int page= Integer.parseInt(st.nextToken());
				
				if((int)param==page)
				{
					return node;
				}
			}
			else
			{
				st.nextToken();
				
				String company = st.nextToken();
				
				if(String.valueOf(param).equalsIgnoreCase(company.toString()))
				{
					return node;
				}
			}
		}
		
		return null;
	}
	

}
