package com.ksg.workbench.base;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.view.comp.IconData;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.base.code.comp.PnCommonCode;
import com.ksg.workbench.base.comp.PnArea;
import com.ksg.workbench.base.comp.PnBase;
import com.ksg.workbench.base.company.PnCompany;
import com.ksg.workbench.base.port.PnPort;
import com.ksg.workbench.base.vessel.comp.PnVessel;
import com.ksg.workbench.common.comp.AbstractMgtUI;

/**

  * @FileName : BaseInfoUI.java

  * @Project : KSG2

  * @Date : 2022. 3. 11. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 마스터 정보관리

  */
public class BaseInfoUI extends AbstractMgtUI{
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
	private KSGPanel pnMain;
	private CardLayout cardLayout;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private HashMap<String, PnBase> panelList;// 패널 저장 객체
	
	private CodeServiceImpl codeService;

	/**
	 * 
	 */

	public BaseInfoUI() {

		super();
		logger.debug("create view start");
		
		codeService = new CodeServiceImpl();

		panelList = new HashMap<String, PnBase>();

		this.title ="기초정보관리";
		this.borderColor = new Color(107,138,15);

		createAndUpdateUI();
		logger.debug("create view end");
	}
	public void createAndUpdateUI() {
		this.setLayout(new BorderLayout(10,10));
		this.add(buildNorthPn(),BorderLayout.NORTH);
		this.add(buildCenter(),BorderLayout.CENTER);
		this.add(buildLeftMenu(),BorderLayout.WEST);

		this.setName("BaseInfoUI");
		cardLayout.show(pnMain, STRING_AREA_INFO);
	}

	public static void expandAll(JTree tree, TreePath parent, boolean expand) {
		
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

		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout(new BorderLayout());
		JScrollPane comp = new JScrollPane(tree);
		comp.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		comp.setBackground(Color.white);
		pnMain.add(comp,BorderLayout.CENTER);
		pnMain.setPreferredSize(new Dimension(250,100));
		pnMain.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

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

		pnMain = new KSGPanel();

		cardLayout = new CardLayout();

		pnMain.setLayout(cardLayout);	

		addBasePanel(pnMain,new PnCommonCode(this),STRING_COMMONCODE_INFO);

		addBasePanel(pnMain,new PnArea(this),		STRING_AREA_INFO);
		addBasePanel(pnMain,new PnVessel(this),		STRING_VESSEL_INFO);
		//addBasePanel(pnMain,new PnVesselAbbr(this),	STRING_VESSEL_ABBR);
		addBasePanel(pnMain,new PnCompany(this),	STRING_COMPANY_INFO);
		addBasePanel(pnMain,new PnPort(this),		STRING_PORT_INFO);
		//addBasePanel(pnMain,new PnPortAbbr(this),	STRING_PORT_ABBR);
		//addBasePanel(pnMain,new PnMember(this),	"사용자");

		return pnMain;
	}

	private void addBasePanel(KSGPanel pnMain, PnBase insertPanel, String panelName)
	{
		pnMain.add(insertPanel,panelName);

		panelList.put(panelName, insertPanel);

	}
	private void updateTree() throws SQLException
	{
		logger.debug("update tree model");
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("code_type", "table");
		
		HashMap<String, Object> resultMap=(HashMap<String, Object>) codeService.selectCodeDList(param);
		
		List master = (List) resultMap.get("master");
	
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new IconData(new ImageIcon("images/db_table16.png"),null,"기초정보"));

		DefaultMutableTreeNode code = new DefaultMutableTreeNode(STRING_CODE_INFO);
		
		DefaultMutableTreeNode table = new DefaultMutableTreeNode("기초 정보");

		
		Iterator<HashMap> iter2 =master.iterator();

		DefaultMutableTreeNode commonCode = new DefaultMutableTreeNode(STRING_COMMONCODE_INFO);
	


		code.add(commonCode);

		while(iter2.hasNext())
		{
			HashMap<String, Object> d =iter2.next(); 
			DefaultMutableTreeNode sub = new DefaultMutableTreeNode(new IconData(new ImageIcon("images/db_table16_2.png"),null,d.get("code_name_kor")));
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


}
