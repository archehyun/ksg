package com.ksg.workbench.common.comp.treetable.node;

import java.text.SimpleDateFormat;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.view.comp.treetable.TreeTableNode;

public class ScheduleTreeNode extends DefaultMutableTreeNode
{
	String vessel;
	String company;
	String fromDate, toDate;
	private SimpleDateFormat inputDateFormat 	= KSGDateUtil.createInputDateFormat();

	private SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();
	
	public ScheduleTreeNode() {
		super();
	}
	public ScheduleTreeNode(String string) {
		super(string);
	}

	public ScheduleTreeNode(TreeTableNode treeTableNode) {
		super(treeTableNode);
		setInfo(treeTableNode);

	}
	public String toString()
	{
		return fromDate+"  "+vessel+"("+company +")"+toDate;
	}
	@Override
	public void add(MutableTreeNode newChild) {
		super.add(newChild);

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) newChild;

		TreeTableNode item= (TreeTableNode) node.getUserObject();
		setInfo(item);

	}
	public void setInfo(TreeTableNode item)		

	{
		vessel=String.valueOf(item.get("vessel"));
		company =String.valueOf(item.get("company_abbr"));


		try {

			fromDate =outputDateFormat.format(inputDateFormat.parse(String.valueOf(item.get("dateF"))));
			toDate =outputDateFormat.format(inputDateFormat.parse(String.valueOf(item.get("dateT"))));
		}catch(Exception e)
		{
			System.err.println("item:"+item);
			e.printStackTrace();
		}
	}
}