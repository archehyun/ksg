package com.ksg.workbench.schedule.comp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.treetable.AbstractTreeTableModel;
import com.ksg.view.comp.treetable.TreeTableModel;
import com.ksg.view.comp.treetable.TreeTableNode;



public class ScheduleTreeTableModel extends AbstractTreeTableModel{
	
	private List<KSGTableColumn> columnNames;
	
	public ScheduleTreeTableModel()
	{
		columnNames = new LinkedList<KSGTableColumn>();
	}
	
	public ScheduleTreeTableModel(Object root)
    {
        super(root);
        columnNames = new LinkedList<KSGTableColumn>();
    }

    /**
     * Error in AbstractTreeTableModel !!!
     * Without overriding this method you can't expand the tree!
     */
    public Class getColumnClass(int column) {
          switch (column)
          {
              case 0:
                    return TreeTableModel.class;
              default:
                    return Object.class;
          }
          
    }
    
	public void addColumn(KSGTableColumn column) {

		columnNames.add(column);

	}


    public Object getChild(Object parent, int index)
    {
          assert parent instanceof MutableTreeNode;
          MutableTreeNode treenode = (MutableTreeNode) parent;
          return treenode.getChildAt(index);
    }

    public int getChildCount(Object parent)
    {
          assert parent instanceof MutableTreeNode;
          MutableTreeNode treenode = (MutableTreeNode) parent;
          return treenode.getChildCount();
    }

    public int getColumnCount()
    {
		if (columnNames == null)
			return 0;

		return columnNames.size();
    }
    
	public void setColumns(KSGTableColumn columns[]) {

		columnNames = Arrays.asList(columns);
	}

    public String getColumnName(int index)
    {
    	KSGTableColumn column = columnNames.get(index);
    
		return column.columnName;

    }
    
    public String getColumnField(int index)
    {
    	KSGTableColumn column = columnNames.get(index);
    	
		return column.columnField;

    }

    public Object getValueAt(Object node, int column)
    {
          assert node instanceof DefaultMutableTreeNode;
          DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) node;
          
          try {
        	  
        	  
  			Object item =  treenode.getUserObject();
  			
  			if(item instanceof TreeTableNode)
  			{
  				
  				return ((TreeTableNode)item).get(this.getColumnField(column));	
  			}
  			else
  			{
  				return "";
  			}
  			
  			
  			
  			

  		
  			
  		} catch (Exception e) {
  			
  			return null;
  		}
    
  

    }
    
    
    
}