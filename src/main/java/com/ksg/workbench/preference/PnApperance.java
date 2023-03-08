package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.ksg.view.comp.KSGViewUtil;
import com.ksg.workbench.common.comp.panel.KSGPanel;

/**

  * @FileName : PnApperance.java

  * @Project : KSG2

  * @Date : 2022. 3. 9. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class PnApperance extends PnOption {	
	
	private KSGViewUtil propeties = KSGViewUtil.getInstance();
	
	private int HEADER_HEIGHT;
	
	private int ROW_HEIGHT;
	
	private Color GRID_COLOR;
	
	private int FONT_SIZE;
	
	 private DefaultMutableTreeNode root;

	private JTree tree;
	
	public PnApperance(PreferenceDialog preferenceDialog) {
		
		super(preferenceDialog);
		
		this.addComponentListener(this);
		
		this.setLayout(new BorderLayout());
		
		this.setName("Apperance");
		
		this.add(buildCenter());
		
	}
	
	private JTree createTree()
	{
		tree = new JTree();
		TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
		      public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
		    	  
		    	  TreePath path=treeSelectionEvent.getNewLeadSelectionPath();
		    	  
		    	  if(path == null) return;
		    	  
		    	  String selectedKey = path.getLastPathComponent().toString();
		    	  
		    	  String value = (String) propeties.get(selectedKey);
		    	  
		    	  System.out.println(value);
		      }

			
		    };
		    
		    tree.addTreeSelectionListener(treeSelectionListener);
		    
		    tree.setRowHeight(20);
		    return tree;
	}
	private Component buildCenter() {
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		tree = createTree();
		
        HEADER_HEIGHT=Integer.parseInt(propeties.getProperty("table.header.height"));

		ROW_HEIGHT=Integer.parseInt(propeties.getProperty("table.row.height"));
		
		GRID_COLOR = getColor(propeties.getProperty("table.girdcolor"));
		
		FONT_SIZE = Integer.parseInt(propeties.getProperty("table.font.size"));
		
		KSGPanel pnTitle = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnTitle.add(new JLabel("색상을 설정"));
		
		KSGPanel pnLight = new KSGPanel();
		
		pnLight.add(new JButton("EDIT"));
		
		
		pnMain.add(pnTitle, BorderLayout.NORTH);
		
		pnMain.add(new JScrollPane(tree));
		
		pnMain.add(pnLight,BorderLayout.EAST);
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,15, 5,5));
		
		return pnMain;
	}

	
	private void loadTree()
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		
		List<ApperanceKeyGroup> apperanceGroup=propeties.keySet()
														.stream()
														.map(key -> makeApperanceKeyGroup((String)key))
														.collect(Collectors.toList());
		
		Map<String, List<ApperanceKeyGroup>> apperanceGroupMap =  apperanceGroup.stream()
																.collect(
																		Collectors.groupingBy(ApperanceKeyGroup::getGroup)
																		);// 도착항
		for(String keyGroup:apperanceGroupMap.keySet())
		{
			List<ApperanceKeyGroup> group = apperanceGroupMap.get(keyGroup);
			
			DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(keyGroup);
			
			group.forEach(o -> groupNode.add(new DefaultMutableTreeNode(o.value)));
			
			root.add(groupNode);
			
		}

		
	    tree.setModel(new DefaultTreeModel( root));
	    
	    tree.setRootVisible(false);
	    
	}
	
	private ApperanceKeyGroup makeApperanceKeyGroup(String key)
	{
		String keyArray[] = key.split("[.]");
		if(keyArray.length>1)
		{
			return new ApperanceKeyGroup(keyArray[0], String.join(".", Arrays.copyOfRange(keyArray, 1, keyArray.length)));
		}
		else
		{
			return new ApperanceKeyGroup(key, key);
		}
	}
	
	class ApperanceKeyGroup
	{
		private String group;
		private String value;
		public ApperanceKeyGroup(String group, String value)
		{
			this.group = group;
			this.value = value;
			
		}
		public String getGroup()
		{
			return group;
		}
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveAction() {
		// TODO Auto-generated method stub
		
	}

	
	private Color getColor(String param)
	{
		String index[] = param.split(",");
		return new Color(Integer.parseInt(index[0].trim()),Integer.parseInt(index[1].trim()), Integer.parseInt(index[2].trim()));
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		
		loadTree();
		
	}
	
	class Branch {
		  private DefaultMutableTreeNode r;

		  public Branch(String[] data) {
		    r = new DefaultMutableTreeNode(data[0]);
		    for (int i = 1; i < data.length; i++)
		      r.add(new DefaultMutableTreeNode(data[i]));
		  }

		  public DefaultMutableTreeNode node() {
		    return r;
		  }
		}



	

}
