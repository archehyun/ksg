package com.ksg.base.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.ibatis.sqlmap.client.SqlMapException;
import com.ksg.base.view.comp.PnArea;
import com.ksg.base.view.comp.PnBase;
import com.ksg.base.view.comp.PnCommonCode;
import com.ksg.base.view.comp.PnCompany;
import com.ksg.base.view.comp.PnPort;
import com.ksg.base.view.comp.PnPortAbbr;
import com.ksg.base.view.comp.PnVessel;
import com.ksg.base.view.comp.PnVesselAbbr;
import com.ksg.base.view.comp.TableListener;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Code;
import com.ksg.view.comp.CurvedBorder;
import com.ksg.view.comp.IconData;
import com.ksg.view.comp.panel.KSGPanel;

/**
 * 마스터 정보 관리
 * @author 박창현
 *
 */
public class BaseInfoUI extends KSGPanel{
	public static final String STRING_CODE_INFO 	= "코드정보";
	public static final String STRING_VESSEL_ABBR 	= "선박 약어";
	public static final String STRING_PORT_ABBR 	= "항구 약어";
	public static final String STRING_AREA_INFO 	= "지역";
	public static final String STRING_PORT_INFO 	= "항구";
	public static final String STRING_COMPANY_INFO 	= "선사";
	public static final String STRING_VESSEL_INFO 	= "선박";
	public static final String STRING_COMMONCODE_INFO 	= "공통코드";
	public static final String CODE_TYPE_INBOUND_PORT = "국내 항구 코드";
	public static final String CODE_TYPE_INBOUND_PORT_ORDER = "Inbound 항구 출발 순서";
	public static final String CODE_TYPE_CON_TYPE = "컨테이너 타입";
	
	
	private JTree tree;
	private JPanel pnMain;
	private CardLayout cardLayout;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private HashMap<String, Object> panelList;// 패널 저장 객체
	
	/**
	 * 
	 */
	
	public BaseInfoUI() {
		
		_baseSearvice = DAOManager.getInstance().createBaseService();
		
		panelList = new HashMap<String, Object>();
		
		createAndUpdateUI();
	}
	public void createAndUpdateUI() {
		this.setLayout(new BorderLayout());

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEADING));
		JLabel lblTitle = new JLabel("기초정보관리");
		
		lblTitle.setForeground(new Color(61,86,113));
		Font titleFont = new Font("명조",Font.BOLD,18);		
		lblTitle.setFont(titleFont);

		pnTitle.add(lblTitle);		
		pnTitle.setBorder(new CurvedBorder(8,new Color(107,138,15)));
		this.add(pnTitle,BorderLayout.NORTH);
		this.add(buildCenter(),BorderLayout.CENTER);
		this.add(buildLeftMenu(),BorderLayout.WEST);
		this.setName("BaseInfoUI");
		cardLayout.show(pnMain, STRING_AREA_INFO);
	}
	
	 public static void expandAll(JTree tree, TreePath parent, boolean expand) {
		    // Traverse children
		    TreeNode node = (TreeNode) parent.getLastPathComponent();
		    if (node.getChildCount() >= 0) {
		      for (Enumeration e = node.children(); e.hasMoreElements();) {
		        TreeNode n = (TreeNode) e.nextElement();
		        TreePath path = parent.pathByAddingChild(n);
		        expandAll(tree, path, expand);
		      }
		    }
		    // Expansion or collapse must be done bottom-up
		    if (expand) {
		      tree.expandPath(parent);
		    } else {
		      tree.collapsePath(parent);
		    }
		  }

	private Component buildLeftMenu() {
		JTree tree = createTreeMenu();

		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnTitle.setPreferredSize( new Dimension(0,22));
		pnMain.add(pnTitle,BorderLayout.NORTH);
		pnMain.add(new JScrollPane(tree),BorderLayout.CENTER);
		JPanel pnTableMainLeft = new JPanel();
		pnTableMainLeft.setPreferredSize(new Dimension(15,0));
		pnMain.add(pnTableMainLeft,BorderLayout.WEST);
		pnMain.setPreferredSize(new Dimension(200,100));
		return pnMain;
	}
	/**
	 * 
	 * 왼쪽 트리 메뉴 생성
	 * @return
	 */
	private JTree createTreeMenu() {
		tree = new JTree();
		try {
			this.updateTree();
			
			tree.setExpandsSelectedPaths(true);
			tree.addTreeSelectionListener(new TreeSelectionListener(){

				private String _selectedTable;


				public void valueChanged(TreeSelectionEvent e) {

					TreePath path=e.getNewLeadSelectionPath();
					if(path!=null&&path.getLastPathComponent()!=null)
					{
						try {
							
							_selectedTable = path.getLastPathComponent().toString();
							
							showBaseInfo(_selectedTable);
							
						}
						
						catch(SqlMapException e2)
						{
							JOptionPane.showMessageDialog(BaseInfoUI.this, _selectedTable+"에 대한 쿼리가 없습니다.");
						}
					}
				}
			});
			
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,"트리 정보생성에 실패 했습니다.=>"+e.getMessage()+", "+e.getErrorCode() );
			e.printStackTrace();
		}
		return tree;
	}
	
	public void showBaseInfo(String baseName)
	{
		
		if(panelList.containsKey(baseName))
		{	
			cardLayout.show(pnMain, baseName);
		}
		
	}
	private Component buildCenter() {
		pnMain = new JPanel();
		
		cardLayout = new CardLayout();
		
		pnMain.setLayout(cardLayout);

	
		
		addBasePanel(pnMain,new PnCommonCode(this),STRING_COMMONCODE_INFO);
		//addBasePanel(pnMain,new PnCodeInboundPortOrder(this),CODE_TYPE_INBOUND_PORT_ORDER);
		//addBasePanel(pnMain,new PnCodeInboundPort(this),CODE_TYPE_INBOUND_PORT);
		//addBasePanel(pnMain,new PnCodeConType(this),CODE_TYPE_CON_TYPE);
		
		addBasePanel(pnMain,new PnArea(this),		STRING_AREA_INFO);
		addBasePanel(pnMain,new PnVessel(this),		STRING_VESSEL_INFO);
		addBasePanel(pnMain,new PnVesselAbbr(this),	STRING_VESSEL_ABBR);
		addBasePanel(pnMain,new PnCompany(this),	STRING_COMPANY_INFO);
		addBasePanel(pnMain,new PnPort(this),		STRING_PORT_INFO);
		addBasePanel(pnMain,new PnPortAbbr(this),	STRING_PORT_ABBR);
		
		return pnMain;
	}
	
	private void addBasePanel(JPanel pnMain, PnBase insertPanel, String panelName)
	{
		pnMain.add(insertPanel,panelName);
		
		panelList.put(panelName, insertPanel);
		
	}
	private void updateTree() throws SQLException
	{
		logger.debug("update tree model");
		List<Code> limenu = _baseSearvice.getCodeInfo("table");
		Code code_info =new Code();
		code_info.setCode_type("code_menu");
		List codetype = _baseSearvice.getCodeInfoList(code_info);

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new IconData(new ImageIcon("images/db_table16.png"),null,"기초정보"));

		DefaultMutableTreeNode code = new DefaultMutableTreeNode(STRING_CODE_INFO);
		DefaultMutableTreeNode table = new DefaultMutableTreeNode("기초 정보");

		Iterator<Code> iter1 =limenu.iterator();
		
		DefaultMutableTreeNode commonCode = new DefaultMutableTreeNode(STRING_COMMONCODE_INFO);
		DefaultMutableTreeNode codeConType = new DefaultMutableTreeNode(CODE_TYPE_CON_TYPE);
		DefaultMutableTreeNode codeInboundPort = new DefaultMutableTreeNode(CODE_TYPE_INBOUND_PORT);
		DefaultMutableTreeNode codeInboundPortOrder = new DefaultMutableTreeNode(CODE_TYPE_INBOUND_PORT_ORDER);
		
		code.add(commonCode);
		/*code.add(codeConType);
		code.add(codeInboundPort);
		code.add(codeInboundPortOrder);*/
		

		while(iter1.hasNext())
		{
			Code d =iter1.next(); 
			DefaultMutableTreeNode sub = new DefaultMutableTreeNode(new IconData(new ImageIcon("images/db_table16_2.png"),null,d.getCode_name_kor()));
			table.add(sub);
		}

		root.add(code);
		root.add(table);
		tree.setModel(new DefaultTreeModel(root));
		tree.putClientProperty("JTree.lineStyle", "Angled");
		TreeCellRenderer renderer = new IconCellRenderer();
		tree.setCellRenderer(renderer);
		tree.setRowHeight(25);
		expandAll(tree, true);
		
		tree.setRootVisible(false);
	}
	 public static void expandAll(JTree tree, boolean expand) {
		    TreeNode root = (TreeNode) tree.getModel().getRoot();
		    // Traverse tree from root
		    expandAll(tree, new TreePath(root), expand);
		  }

	class IconCellRenderer 
	extends    JLabel 
	implements TreeCellRenderer
	{
		protected Color m_bkNonSelectionColor;
		protected Color m_bkSelectionColor;
		protected Color m_borderSelectionColor;
		protected boolean m_selected;
		protected Color m_textNonSelectionColor;

		protected Color m_textSelectionColor;

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

			if (obj instanceof Boolean)
				setText("Retrieving data...");

			if (obj instanceof IconData)
			{
				IconData idata = (IconData)obj;
				if (expanded)
					setIcon(idata.getExpandedIcon());
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

		public void paintComponent(Graphics g) 
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
			super.paintComponent(g);
		}
	}

	@Override
	public void update(KSGModelManager manager) {
		// TODO Auto-generated method stub
		
	}

}
