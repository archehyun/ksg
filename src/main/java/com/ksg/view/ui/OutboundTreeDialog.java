package com.ksg.view.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ksg.common.model.KSGModelManager;
import com.ksg.view.comp.KSGPanel;

public class OutboundTreeDialog extends KSGPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String vessel="vessel";
	String fromPort="fromPort";
	private DefaultMutableTreeNode treeroot;
	private JTree tree;
	public OutboundTreeDialog() {
		this.setLayout(new BorderLayout());

	}
	public static ImageIcon ICON_SELF = 
		new ImageIcon("images/db_table16.png");
	public static ImageIcon ICON_SELF2 = 
		new ImageIcon("images/db_table16_2.png");

	@SuppressWarnings("unchecked")
	public void createAndUpdateUI() {

		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());

		treeroot = new DefaultMutableTreeNode("root");
		tree = new JTree(treeroot);

		TreeCellRenderer renderer = new OutboundRenderer();
		tree.setCellRenderer(renderer);

		tree.setRowHeight(20);
		tree.setExpandsSelectedPaths(true);

		SAXBuilder b = new SAXBuilder();  // true -> validate

		// Create a JDOM document.
		logger.debug("Create a JDOM document");
		try {
			Document doc = b.build(new File("outbound_source.xml"));
			Element root = doc.getRootElement();

			List<Element> rows = root.getChildren("schedule-row");
			logger.debug("schedule-row size:"+rows.size());


			for (int i = 0; i < rows.size(); i++) {	

				Element row = (Element) rows.get(i);



				DefaultMutableTreeNode schedule


				= new DefaultMutableTreeNode(row.getChild("toPort").getAttributeValue("name")+" , "+row.getChild("toPort").getAttributeValue("nationality"));
				treeroot.add(schedule);

				List columns = row.getChildren(fromPort);

				for (int j = 0; j < columns.size(); j++) {
					Element column = (Element) columns.get(j);
					
					
					String name = column.getAttribute("name").getValue();
					String isTS=column.getAttributeValue("ts");
					String TS="";
					if(isTS!=null)
						TS="T/S ";
					DefaultMutableTreeNode fromPort = new DefaultMutableTreeNode(TS+name);

					schedule.add(fromPort);

					List vessellist=column.getChildren(vessel);
					for (int vesselcount = 0; vesselcount < vessellist.size(); vesselcount++) 
					{
						Element vessel = (Element) vessellist.get(vesselcount);
						String use = vessel.getAttributeValue("use");
						if(Boolean.valueOf(use))
						{
							String dateF="";
							try{
								dateF =vessel.getAttributeValue("dateF").substring(5);
							}catch (Exception e) {

								System.err.println("paser error:"+vessel.getAttributeValue("dateF"));
								dateF =vessel.getAttributeValue("dateF");
							}


							String vesselname =vessel.getAttributeValue("name");

							String company =vessel.getAttributeValue("company");

							String agent =vessel.getAttributeValue("agent");
							String dateT="";
							try
							{
								dateT =vessel.getAttributeValue("dateT").substring(5);;
							}
							catch(Exception e)
							{
								dateT =vessel.getAttributeValue("dateT");;
							}



							DefaultMutableTreeNode vesseltree =null;
							if(company.equals(agent))
							{
								vesseltree = new DefaultMutableTreeNode(new OutboundResult (new DiamondIcon(Color.green),dateF,vesselname+" ("+
										company+")",dateT));	
							}else
							{
								vesseltree = new DefaultMutableTreeNode(new OutboundResult (new DiamondIcon(Color.green),dateF,vesselname+" ("+
										company+"/"+agent+")",dateT));	
							}

							fromPort.add(vesseltree);
						}else
						{
							List<Element> commonlist=vessel.getChildren("common-shipping");
							Vector t = new Vector();
							for (int commoncount = 0; commoncount < commonlist.size(); commoncount++) 
							{
								Element vessel1 = (Element) commonlist.get(commoncount);

								String dateF =vessel1.getAttributeValue("common-dateF").substring(5);;

								String vesselname =vessel1.getAttributeValue("name");

								String company =vessel1.getAttributeValue("company");
								

								String agent =vessel1.getAttributeValue("agent");
								String dateT ="";
								try{

									dateT =vessel1.getAttributeValue("common-dateT").substring(5);;
								}catch(Exception e)
								{
									dateT =vessel1.getAttributeValue("common-dateT");;
								}
								
								
								t.add(company);

								DefaultMutableTreeNode vesseltree =null;

								List vlist=vessel1.getChildren("common-vessel");
								for(int vv=0;vv<vlist.size();vv++)
								{
									Element vve = (Element) vlist.get(vv);
									boolean flag=true;
									for(int c=0;c<t.size();c++)
									{
										if(t.get(c).equals(vve.getAttributeValue("company")))
										{
											flag=false;
										}
									}
									if(flag)
									{
										company+=","+vve.getAttributeValue("company");
										t.add(vve.getAttributeValue("company"));
									}
									
								}

								if(company.equals(agent))
								{
									vesseltree = new DefaultMutableTreeNode(new OutboundResult (new DiamondIcon(Color.green),dateF,vesselname+"  [C]("+
											company+")",dateT));	
								}else
								{
									vesseltree = new DefaultMutableTreeNode(new OutboundResult (new DiamondIcon(Color.green),dateF,vesselname+"  [C]("+
											company+")",dateT));	
								}
								fromPort.add(vesseltree);
							}
						}
					}
					/*List commonlist=column.getChildren("common-shipping");
					for (int commoncount = 0; commoncount < commonlist.size(); commoncount++) 
					{
						Element vessel = (Element) commonlist.get(commoncount);

						String dateF =vessel.getAttributeValue("common-dateF").substring(5);;

						String vesselname =vessel.getAttributeValue("name");

						String company =vessel.getAttributeValue("company");

						String agent =vessel.getAttributeValue("agent");
						String dateT ="";
						try{

						dateT =vessel.getAttributeValue("common-dateT").substring(5);;
						}catch(Exception e)
						{
							dateT =vessel.getAttributeValue("common-dateT");;
						}

						DefaultMutableTreeNode vesseltree =null;

						List vlist=vessel.getChildren("common-vessel");
						for(int vv=0;vv<vlist.size();vv++)
						{
							Element vve = (Element) vlist.get(vv);
							company+=","+vve.getAttributeValue("company");
						}

						if(company.equals(agent))
						{
							vesseltree = new DefaultMutableTreeNode(new OutboundResult (new DiamondIcon(Color.green),dateF,vesselname+"  [C]("+
									company+")",dateT));	
						}else
						{
							vesseltree = new DefaultMutableTreeNode(new OutboundResult (new DiamondIcon(Color.green),dateF,vesselname+"  [C]("+
									company+")",dateT));	
						}
						fromPort.add(vesseltree);
					}
					 */
				}
			}
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		pnMain.add(new JScrollPane(tree));

		JPanel pnControl = new JPanel();
		JButton butExpand = new JButton("Expand All");
		butExpand.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				expandAll(tree);

			}});

		JTextField field = new JTextField(10);
		field.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					JTextField f =(JTextField) e.getSource();
					String prefix = f.getText();
					if(prefix.length()==0)
						return;
					int startRow = 0;
					TreePath path = tree.getNextMatch(prefix, startRow, Position.Bias.Backward);
					System.out.println(path);


					if(path==null)
						return;
					tree.scrollPathToVisible(path);

					tree.setSelectionPath(path);

					tree.requestFocus();
					f.setText("");
				}
			}
			public void keyTyped(KeyEvent e) {}});

		/*	JButton butCancel = new JButton("´Ý±â(C)");
		butCancel.setMnemonic(KeyEvent.VK_C);
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				OutboundTreeDialog.this.setVisible(false);
			}});*/
		JComboBox cbxExpand = new JComboBox();
		cbxExpand.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox) e.getSource();
				if(box.getSelectedItem().equals("Expand All"))
				{
					expandAll(tree);
				}else if(box.getSelectedItem().equals("Expand ToPort"))
				{
					expand(tree);
				}

			}});
		cbxExpand.addItem("Expand All");
		cbxExpand.addItem("Expand ToPort");
		cbxExpand.addItem("Expand FromPort");

		pnControl.add(new JLabel("Ç×±¸¸í: "));

		pnControl.add(field);
		pnControl.add(cbxExpand);
		pnControl.add(butExpand);
		//		pnControl.add(butCancel);

		expandAll(tree);
		this.add(pnMain);
		this.add(pnControl,BorderLayout.SOUTH);		

	}
	private String getFoattedValue(Element vessel) {


		String dateF = vessel.getAttributeValue("dateF");
		String vesselname =vessel.getAttributeValue("name");
		String di1 = vessel.getAttributeValue("dateF").length()>4?"     ":"        ";

		String company =vessel.getAttributeValue("company");

		String agent =vessel.getAttributeValue("agent");

		String fg= vesselname+"  "+company;

		String di2=" ";
		if(fg.length()<35)
		{
			for(int i=0;i<35-fg.length();i++)
				di2+=" ";
		}

		String dateT =vessel.getAttributeValue("dateT");

		return dateF+
		di1+vesselname+"  [C]("+
		company+"/"+agent+")"+di2+dateT;
	}
	public void expand(JTree tree) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expand(tree, new TreePath(root));
	}

	private void expand(JTree tree, TreePath parent) {
		Enumeration e = ((DefaultMutableTreeNode)tree.getModel().getRoot()).preorderEnumeration();
		while(e.hasMoreElements()){
			TreeNode node= (TreeNode) e.nextElement();
			System.out.println(node);
		}
	}

	public void expandAll(JTree tree) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root));
	}

	private void expandAll(JTree tree, TreePath parent) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();

		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path);
			}
		}
		tree.expandPath(parent);
		// tree.collapsePath(parent);
	}
	class IconCellRenderer 
	extends    JPanel 
	implements TreeCellRenderer
	{
		protected Color m_textSelectionColor;
		protected Color m_textNonSelectionColor;
		protected Color m_bkSelectionColor;
		protected Color m_bkNonSelectionColor;
		protected Color m_borderSelectionColor;
		private JLabel lblFromDate;
		private JLabel lblToDate;
		private JLabel lblContent;

		protected boolean m_selected;

		public IconCellRenderer()
		{
			super();
			m_textSelectionColor = UIManager.getColor(
			"Tree.selectionForeground");
			m_textNonSelectionColor = UIManager.getColor(
			"Tree.textForeground");
			m_bkSelectionColor = UIManager.getColor(
			"Tree.selectionBackground");
			m_bkNonSelectionColor = UIManager.getColor(
			"Tree.textBackground");
			m_borderSelectionColor = UIManager.getColor(
			"Tree.selectionBorderColor");

			this.setLayout(new BorderLayout());
			//			renderer = new JPanel(new BorderLayout());

			lblFromDate = new JLabel();
			lblToDate = new JLabel();
			lblContent = new JLabel();
			this.add(lblFromDate,BorderLayout.WEST);
			this.add(lblContent,BorderLayout.CENTER);
			this.add(lblToDate,BorderLayout.EAST);;


			setOpaque(false);
		}

		public Component getTreeCellRendererComponent(JTree tree, 
				Object value, boolean sel, boolean expanded, boolean leaf, 
				int row, boolean hasFocus) 

		{
			DefaultMutableTreeNode node = 
				(DefaultMutableTreeNode)value;
			Object obj = node.getUserObject();

			if (obj instanceof IconData)
			{
				IconData idata = (IconData)obj;
				if (expanded)
					lblFromDate.setIcon(idata.getOpenIcon());
				else
					lblFromDate.setIcon(idata.getIcon());
			}
			else
				lblFromDate.setIcon(null);

			setFont(tree.getFont());
			setForeground(sel ? m_textSelectionColor : 
				m_textNonSelectionColor);
			setBackground(sel ? m_bkSelectionColor : 
				m_bkNonSelectionColor);
			m_selected = sel;
			return this;
		}

		public void paint(Graphics g) 
		{
			Color bColor = getBackground();
			Icon icon = lblFromDate.getIcon();

			g.setColor(bColor);
			int offset = 0;
			if(icon != null && lblFromDate.getText() != null) 
				offset = (icon.getIconWidth() + lblFromDate.getIconTextGap());
			g.fillRect(offset, 0, getWidth() - 1 - offset,
					getHeight() - 1);

			if (m_selected) 
			{
				g.setColor(m_borderSelectionColor);
				g.drawRect(offset, 0, getWidth()-1-offset, getHeight()-1);
			}

			super.paint(g);
		}
	}
	public class IconData
	{
		protected Icon   m_icon;
		protected Icon   m_openIcon;
		protected Object m_data;
		public IconData(Icon icon)
		{
			m_icon = icon;
			m_openIcon = null;
		}
		String fromDate;
		String toDate;
		String content;
		public String getFromDate() {
			return fromDate;
		}
		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}
		public String getToDate() {
			return toDate;
		}
		public void setToDate(String toDate) {
			this.toDate = toDate;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}


		public IconData(Icon icon, Object data)
		{
			m_icon = icon;
			m_openIcon = null;
			m_data = data;
		}

		public IconData(Icon icon, Icon openIcon, Object data)
		{
			m_icon = icon;
			m_openIcon = openIcon;
			m_data = data;
		}

		public Icon getIcon() 
		{ 
			return m_icon;
		}

		public Icon getOpenIcon() 
		{ 
			return m_openIcon!=null ? m_openIcon : m_icon;
		}

		public Object getObject() 
		{ 
			return m_data;
		}		
	}
	class DiamondIcon implements Icon {
		private static final int DEFAULT_HEIGHT = 10;

		private static final int DEFAULT_WIDTH = 10;

		private Color color;

		private int height;

		private Polygon poly;

		private boolean selected;

		private int width;

		public DiamondIcon(Color color) {
			this(color, true, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		}

		public DiamondIcon(Color color, boolean selected) {
			this(color, selected, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		}

		public DiamondIcon(Color color, boolean selected, int width, int height) {
			this.color = color;
			this.selected = selected;
			this.width = width;
			this.height = height;
			initPolygon();
		}

		public int getIconHeight() {
			return height;
		}

		public int getIconWidth() {
			return width;
		}

		private void initPolygon() {
			poly = new Polygon();
			int halfWidth = width / 2;
			int halfHeight = height / 2;
			poly.addPoint(0, halfHeight);
			poly.addPoint(halfWidth, 0);
			poly.addPoint(width, halfHeight);
			poly.addPoint(halfWidth, height);
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);
			g.translate(x, y);
			if (selected) {
				g.fillPolygon(poly);
			} else {
				g.drawPolygon(poly);
			}
			g.translate(-x, -y);
		}
	}
	class OutboundResult extends IconData
	{

		public OutboundResult(Icon icon,String fromDate, String content, String toDate) {
			super(icon);

			this.fromDate=fromDate;
			this.content=content;
			this.toDate=toDate;
		}
		public OutboundResult(Icon icon, Object data) {
			super(icon, data);
		}
		String fromDate;
		String toDate;
		String content;
		public String getFromDate() {
			return fromDate;
		}
		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}
		public String getToDate() {
			return toDate;
		}
		public void setToDate(String toDate) {
			this.toDate = toDate;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}

	}
	class OutboundRenderer implements TreeCellRenderer {
		private JPanel renderer;
		private JLabel lblFromDate;
		private JLabel lblToDate;
		private JLabel lblContent;

		DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

		public OutboundRenderer() {
			renderer = new JPanel(new BorderLayout());
			renderer.setPreferredSize(new Dimension(700,25));

			lblFromDate = new JLabel();
			lblFromDate.setPreferredSize(new Dimension(80,25));
			lblToDate = new JLabel();
			lblToDate.setHorizontalTextPosition(JLabel.LEFT);
			lblContent = new JLabel();
			renderer.add(lblFromDate,BorderLayout.WEST);
			renderer.add(lblContent,BorderLayout.CENTER);
			renderer.add(lblToDate,BorderLayout.EAST);
		}



		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Component returnValue = null;
			if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
				Object userObject = ((DefaultMutableTreeNode) value)
				.getUserObject();
				if (userObject instanceof OutboundResult) {
					OutboundResult result = (OutboundResult) userObject;
					lblFromDate.setText(result.getFromDate());
					lblToDate.setText(result.getToDate());
					lblContent.setText(result.getContent());
					if (selected) {
						renderer.setBackground(defaultRenderer.getBackgroundSelectionColor());
					} else {
						renderer.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
					}
					renderer.setEnabled(tree.isEnabled());
					returnValue = renderer;
				}
			}
			if (returnValue == null) {
				returnValue = defaultRenderer.getTreeCellRendererComponent(tree,
						value, selected, expanded, leaf, row, hasFocus);
			}
			return returnValue;
		}
	}
	@Override
	public void update(KSGModelManager manager) {
		// TODO Auto-generated method stub
		
	}



}
