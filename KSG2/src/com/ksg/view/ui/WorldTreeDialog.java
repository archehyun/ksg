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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class WorldTreeDialog extends JPanel {

	String vessel="vessel";
	String fromPort="fromPort";
	private DefaultMutableTreeNode treeroot;
	private JTree tree;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	private IconCellRenderer m_renderer;
	public WorldTreeDialog() {
		super(new BorderLayout());

	}
	public static ImageIcon ICON_SELF = 
		new ImageIcon("images/db_table16.png");
	public static ImageIcon ICON_SELF2 = 
		new ImageIcon("images/db_table16_2.png");

	public void createAndUpdateUI() {

		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());

		treeroot = new DefaultMutableTreeNode("root");
		tree = new JTree(treeroot);
		tree.setExpandsSelectedPaths(true);

		m_renderer = new IconCellRenderer();
		tree.setCellRenderer(new RouteRenderer()); 

		SAXBuilder b = new SAXBuilder();  // true -> validate

		// Create a JDOM document.
		logger.debug("Create a JDOM document");
		try {
			Document doc = b.build(new File("world_source.xml"));
			Element root = doc.getRootElement();

			List rows = root.getChildren("schedule-row");
			logger.debug("schedule-row size:"+rows.size());


			for (int i = 0; i < rows.size(); i++) {	

				Element row = (Element) rows.get(i);
				DefaultMutableTreeNode schedule = new DefaultMutableTreeNode(row.getChild("area").getAttributeValue("name"));
				treeroot.add(schedule);

				List datas = row.getChildren("data");

				for (int j = 0; j < datas.size(); j++) 
				{
					Element xmlData = (Element) datas.get(j);

					List company=xmlData.getChildren("company_abbr");					
					String company_abbr="tt";
					if(company.size()>1)
					{
						for(int z=0;z<company.size();z++)
						{
							Element e = (Element) company.get(z);
							company_abbr+=e.getText();
							if(z<company.size()-1)
								company_abbr+=",";
						}
					}else if(company.size()==1)
					{
						Element c = (Element) company.get(0);
						company_abbr = c.getText();
					}
					List ports = xmlData.getChildren("port");
					Iterator ii = ports.iterator();
					String portlist="";
					while(ii.hasNext())
					{
						Element e = (Element) ii.next();
						portlist+=e.getAttributeValue("name")+" "+e.getAttributeValue("date");
						if(ii.hasNext())
							portlist+=" * ";
					}

					DefaultMutableTreeNode treeData2 = new DefaultMutableTreeNode(new RouteResult (new DiamondIcon(Color.green),xmlData.getAttributeValue("vessel")+"-"+
							xmlData.getAttributeValue("voy"),"("+company_abbr+")",portlist));
					schedule.add(treeData2);
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
/*
		JButton butCancel = new JButton("´Ý±â(C)");
		butCancel.setMnemonic(KeyEvent.VK_C);
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				WorldTreeDialog.this.setVisible(false);
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
	extends    JLabel 
	implements TreeCellRenderer
	{
		protected Color m_textSelectionColor;
		protected Color m_textNonSelectionColor;
		protected Color m_bkSelectionColor;
		protected Color m_bkNonSelectionColor;
		protected Color m_borderSelectionColor;

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
			setOpaque(false);
		}

		public Component getTreeCellRendererComponent(JTree tree, 
				Object value, boolean sel, boolean expanded, boolean leaf, 
				int row, boolean hasFocus) 

		{
			DefaultMutableTreeNode node = 
				(DefaultMutableTreeNode)value;
			Object obj = node.getUserObject();
			setText(obj.toString());
			if (obj instanceof IconData)
			{
				IconData idata = (IconData)obj;
				if (expanded)
					setIcon(idata.getOpenIcon());
				else
					setIcon(idata.getIcon());
			}
			else
				setIcon(null);

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
			Icon icon = getIcon();

			g.setColor(bColor);
			int offset = 0;
			if(icon != null && getText() != null) 
				offset = (icon.getIconWidth() + getIconTextGap());
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
	class IconData
	{
		protected Icon   m_icon;
		protected Icon   m_openIcon;
		protected Object m_data;

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

		public IconData(Icon icon) {
			m_icon = icon;
			m_openIcon = null;
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
	class RouteResult extends IconData
	{

		public RouteResult(Icon icon,String vessel,String company, String portList ) {
			super(icon);

			this.vessel=vessel;
			this.company=company;
			this.portList=portList;

		}
		public RouteResult(Icon icon, Object data) {
			super(icon, data);
		}
		String vessel;
		String company;
		String portList;
		public String getVessel() {
			return vessel;
		}
		public void setVessel(String vessel) {
			this.vessel = vessel;
		}
		public String getCompany() {
			return company;
		}
		public void setCompany(String company) {
			this.company = company;
		}
		public String getPortList() {
			return portList;
		}
		public void setPortList(String portList) {
			this.portList = portList;
		}

	}
	class RouteRenderer implements TreeCellRenderer {
		private Box renderer;
		private JLabel lblVessel;
		private JLabel lblCompany;
		private JTextArea txaPortList;

		DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
		private JPanel pnLeft;



		public RouteRenderer() {
			//renderer = new JPanel();
			//renderer.setLayout(new BorderLayout());
//			renderer.setPreferredSize(new Dimension(700,600));
			renderer = Box.createHorizontalBox();
			txaPortList = new JTextArea();
			txaPortList.setPreferredSize(new Dimension(1000,300));
			lblVessel = new JLabel(" ");
			lblVessel.setPreferredSize(new Dimension(200,60));


			lblCompany = new JLabel(" ");
			lblCompany.setPreferredSize(new Dimension(100,60));
			renderer.add(lblVessel);
			renderer.add(lblCompany);
			renderer.add(txaPortList);
		}



		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Component returnValue = null;
			if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
				Object userObject = ((DefaultMutableTreeNode) value)
				.getUserObject();
				if (userObject instanceof RouteResult) {
					RouteResult result = (RouteResult) userObject;


					lblCompany.setText(result.getCompany());
					lblVessel.setText(result.getVessel());



					StringTokenizer st = new StringTokenizer(result.getPortList(),"*");
					int count=0;
					txaPortList.setText("");
					while(st.hasMoreTokens())
					{
						txaPortList.append(st.nextToken());
						if(count!=0&&count%5==0)
						{
							txaPortList.append("\n");
						}
						if(st.hasMoreTokens())
						{
							txaPortList.append(" * ");
						}
						count++;
					}
//					txaPortList.setText(result.getContent());
					if (selected) {
						renderer.setBackground(defaultRenderer.getBackgroundSelectionColor());
						txaPortList.setBackground(defaultRenderer.getBackgroundSelectionColor());
//						pnLeft.setBackground(defaultRenderer.getBackgroundSelectionColor());
						lblCompany.setBackground(defaultRenderer.getBackgroundSelectionColor());
						lblVessel.setBackground(defaultRenderer.getBackgroundSelectionColor());


					} else {
						renderer.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
						txaPortList.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
						lblCompany.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
						lblVessel.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
//						pnLeft.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
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


}
