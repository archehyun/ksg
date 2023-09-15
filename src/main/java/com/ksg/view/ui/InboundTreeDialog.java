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

import javax.swing.Icon;
import javax.swing.JButton;
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
import com.ksg.view.comp.panel.KSGPanel;

public class InboundTreeDialog extends KSGPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String vessel="vessel";
	String fromPort="fromPort";
	private DefaultMutableTreeNode treeroot;
	private JTree tree;
	private IconCellRenderer m_renderer;
	public InboundTreeDialog() {
		this.setLayout(new BorderLayout());
	}

	public void createAndUpdateUI() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());

		treeroot = new DefaultMutableTreeNode("root");
		tree = new JTree(treeroot);
		m_renderer = new IconCellRenderer();
		tree.setCellRenderer(new InboundRenderer()); 
		tree.setRowHeight(20);
		tree.setExpandsSelectedPaths(true);
		SAXBuilder b = new SAXBuilder();  // true -> validate

		// Create a JDOM document.
		logger.debug("Create a JDOM document");
		try {
			Document doc = b.build(new File("inbound_source.xml"));
			Element root = doc.getRootElement();

			List rows = root.getChildren("schedule-row");
			logger.debug("schedule-row size:"+rows.size());


			for (int i = 0; i < rows.size(); i++) {

				Element row = (Element) rows.get(i);
				DefaultMutableTreeNode schedule = 
					new DefaultMutableTreeNode(row.getAttributeValue("name")+", "+row.getAttributeValue("nationality"));

				treeroot.add(schedule);

				List datas = row.getChildren("data");
				
				for(int j=0;j<datas.size();j++)
				{
					Element vessel_data = (Element) datas.get(j);
					String vessel= vessel_data.getAttributeValue("vessel");
					String dateF ="";
					try{
						dateF = vessel_data.getAttributeValue("dateF").substring(5);	
					}
					catch(Exception e)
					{
						dateF = vessel_data.getAttributeValue("dateF");
						logger.error("error date:"+dateF);
					}
					
					String company_abbr =vessel_data.getAttributeValue("company");
					String common =vessel_data.getAttributeValue("common");
					String agent =vessel_data.getAttributeValue("agent");
					
					StringBuffer row_s = new StringBuffer();
					row_s.append(dateF+"   ");
					row_s.append(vessel+"   ");
					row_s.append("("+(company_abbr.equals(agent)?company_abbr:company_abbr+"/"+agent)+")   ");
					row_s.append(common);
					
					DefaultMutableTreeNode VESSEL =new DefaultMutableTreeNode(new InboundResult (new DiamondIcon(Color.green),dateF,vessel+"  "+"("+company_abbr+")",common));
					schedule.add(VESSEL);
					
				}		
			}
		} catch (JDOMException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		pnMain.add(new JScrollPane(tree));

		JPanel pnControl = new JPanel();
		JButton butExpand = new JButton("Expand All");
		butExpand.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				expandAll(tree);

			}});
		JButton butCollapase = new JButton("Collapase All");
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

		/*JButton butCancel = new JButton("´Ý±â(C)");
		butCancel.setMnemonic(KeyEvent.VK_C);
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				InboundTreeDialog.this.setVisible(false);
//				InboundTreeDialog.this.dispose();
			}});*/
		pnControl.add(new JLabel("Ç×±¸¸í: "));
		pnControl.add(field);
		pnControl.add(butExpand);
//		pnControl.add(butCancel);

		expandAll(tree);
		
		
		this.add(pnMain);
		this.add(pnControl,BorderLayout.SOUTH);
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
	}
	class BookCellRenderer implements TreeCellRenderer {


		JLabel renderer;

		DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

		Color backgroundSelectionColor;

		Color backgroundNonSelectionColor;

		public BookCellRenderer() {
			renderer = new JLabel();

			backgroundSelectionColor = defaultRenderer
			.getBackgroundSelectionColor();
			backgroundNonSelectionColor = defaultRenderer
			.getBackgroundNonSelectionColor();
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			if ((value != null) && (value instanceof DefaultMutableTreeNode)) {				
				
				
				if (selected) {
					renderer.setBackground(backgroundSelectionColor);
				} else {
					renderer.setBackground(backgroundNonSelectionColor);
					
				}
				renderer.setEnabled(tree.isEnabled());



			}
			if (renderer == null) {
				renderer = (JLabel) defaultRenderer.getTreeCellRendererComponent(tree,
						value, selected, expanded, leaf, row, hasFocus);
			}

			return renderer;
		}
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
		public IconData(Icon icon)
		{
			m_icon = icon;
			m_openIcon = null;
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
	class InboundResult extends IconData
	{
		
		public InboundResult(Icon icon,String fromDate, String content, String toDate) {
			super(icon);
			
			this.fromDate=fromDate;
			this.content=content;
			this.toDate=toDate;
		}
		public InboundResult(Icon icon, Object data) {
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
	class InboundRenderer implements TreeCellRenderer {
		private JPanel renderer;
		private JLabel lblFromDate;
		private JLabel lblToDate;
		private JLabel lblContent;

		DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

		  

		public InboundRenderer() {
			renderer = new JPanel(new BorderLayout());
			renderer.setPreferredSize(new Dimension(700,25));
			
			lblFromDate = new JLabel();
			lblFromDate.setPreferredSize(new Dimension(100,25));
			lblFromDate.setHorizontalTextPosition(JLabel.RIGHT);
			lblToDate = new JLabel();
			lblToDate.setPreferredSize(new Dimension(300,25));
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
			      if (userObject instanceof InboundResult) {
			    	  InboundResult result = (InboundResult) userObject;
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



}
