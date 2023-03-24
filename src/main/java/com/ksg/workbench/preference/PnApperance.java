package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
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

	private JColorChooser tcc;
	
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
		
		JButton butEdit = new JButton("EDIT");
		
		butEdit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Object node=tree.getLastSelectedPathComponent();
				
				if(node instanceof ColorNode)
				{
					ColorNode colorNode = (ColorNode) node;
					
					String key = colorNode.key;
					 
					 Color selectedColor =  Color.decode(propeties.getProperty(key));
					 
					 Color newColor = JColorChooser.showDialog(
		                     PnApperance.this,
		                     "Choose Background Color",
		                     selectedColor);
					
					if(newColor!=null)
					{	 
						 
						int red =newColor.getRed();
						
						int green=newColor.getGreen();
						
						int blue = newColor.getBlue();
						
						propeties.put(key, red+","+green+","+blue);
						
						propeties.store();
					}
				}
				
				
				
				
			}
		});
		
		pnLight.add(butEdit);
		
		
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
																		);
		for(String keyGroup:apperanceGroupMap.keySet())
		{
			List<ApperanceKeyGroup> group = apperanceGroupMap.get(keyGroup);
			
			DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(keyGroup);
			
			group.forEach(o -> groupNode.add(new ColorNode(o.value, o.key)));
			
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
			return new ApperanceKeyGroup(keyArray[0], String.join(".", Arrays.copyOfRange(keyArray, 1, keyArray.length)),key);
		}
		else
		{
			return new ApperanceKeyGroup(key, key, key);
		}
	}
	
	class ApperanceKeyGroup
	{
		private String group;
		
		private String value;
		
		private String key;
		
		public ApperanceKeyGroup(String key)
		{
			this.group = key;
			this.value = key;
			this.key =key;
			
		}
		
		public ApperanceKeyGroup(String group, String value)
		{
			this.group = group;
			this.value = value;
			
		}
		public ApperanceKeyGroup(String group, String value, String key)
		{
			this(group, value);
			this.key = key;
			
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
	
	class ColorNode extends DefaultMutableTreeNode
	{
		private String key;
		
		public ColorNode(String name, String key)
		{
			super(name);
			this.key = key;
		}
		
	}

	

}
