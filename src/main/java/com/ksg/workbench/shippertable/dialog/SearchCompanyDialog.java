package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.service.CompanyService;
import com.ksg.service.impl.CompanyServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;


/**
 * 

 * @FileName : SearchCompanyDialog.java

 * @Project : KSG2

 * @Date : 2022. 11. 20. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 선사 정보 선택
 */
@SuppressWarnings("serial")
public class SearchCompanyDialog extends KSGDialog{

	private CompanyService companyService;

	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("선사명 약어");

	private JTree tree;

	public String result;
	
	public SearchCompanyDialog()
	{
		super();
		
		this.setModal(true);
		
		this.setTitle("선사 선택");
		
		companyService = new CompanyServiceImpl();
		
		this.addComponentListener(this);
	}

	public SearchCompanyDialog(JDialog comp)
	{
		super(comp);
		
		this.setModal(true);
		
		this.setTitle("선사 선택");
		
		companyService = new CompanyServiceImpl();
		
		this.addComponentListener(this);
	}

	@Override
	public void createAndUpdateUI() {

		tree = new JTree(root);
		tree.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()>1)
				{
					TreePath path=tree.getSelectionPath();
					if(path.getPathCount()!=1)
					{
						result =path.getLastPathComponent().toString();
						close();
					}
				}
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener(){

			public void valueChanged(TreeSelectionEvent e) {
				TreePath path=e.getNewLeadSelectionPath();

				if(path!=null&&path.getPathCount()!=1)

					result = path.getLastPathComponent().toString();


			}});

		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		KSGPanel pnTitleInfo = new KSGPanel();

		pnTitleInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitleInfo.add(new JLabel("선사명을 선택하세요"));
		JTextField txfSearch = new JTextField(20);
		txfSearch.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {


			}

			@Override
			public void keyReleased(KeyEvent e) {

				try {
					String str = txfSearch.getText();

					CommandMap param = new CommandMap();

					List<CommandMap> data = new ArrayList<CommandMap>();
					if("".equals(str)||str==null) {
						param.put("company_abbr", str);
						CommandMap result= (CommandMap) companyService.selectListByCondition(param);
						data = (List<CommandMap>) result.get("master");
					}
					else
					{
						param.put("company_abbr", str);
						CommandMap result= (CommandMap) companyService.selectListByLike(param);
						data = (List<CommandMap>) result.get("master");
					}
					if(data==null) data = new ArrayList<CommandMap>();

					updateTree(data);

				}catch(Exception err)
				{
					err.printStackTrace();
					JOptionPane.showMessageDialog(SearchCompanyDialog.this, err.getMessage());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		pnTitleInfo.add(txfSearch);

		pnMain.add(new JScrollPane(tree));
		pnMain.add(pnTitleInfo,BorderLayout.NORTH);
		pnMain.add(buildControl(),BorderLayout.SOUTH);

		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.getContentPane().add(pnMain);

		setSize(400, 400);
		ViewUtil.center(this);
		this.setVisible(true);

	}

	private KSGPanel buildControl()
	{
		KSGPanel pnSubPnControl = new KSGPanel();
		pnSubPnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("확인");

		butOK.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				TreePath path=tree.getSelectionPath();
				if(path!=null&&path.getPathCount()!=1)
				{
					result=path.getLastPathComponent().toString();
					close();
				}
				else
				{
					JOptionPane.showMessageDialog(SearchCompanyDialog.this, "선택된 선사명 약어가 없습니다.");
				}


			}});
		butOK.setPreferredSize(new Dimension(80,28));
		pnSubPnControl.add(butOK);
		JButton butCancel = new JButton("취소");

		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				result = null;
				close();

			}});
		pnSubPnControl.add(butCancel);
		butCancel.setPreferredSize(new Dimension(80,28));
		return pnSubPnControl;
	}
	private void updateTree(List list) throws SQLException	
	{


		this.root.removeAllChildren();

		

		Iterator<CommandMap> iter =list.iterator();
		while(iter.hasNext())
		{
			CommandMap company = (CommandMap) iter.next();
			String company_abbr = (String) company.get("company_abbr");
			DefaultMutableTreeNode sub = new DefaultMutableTreeNode(company_abbr);			
			root.add(sub);						
		}

		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), true);
		
		tree.clearSelection();
		tree.updateUI();

	}
	@Override
	public void componentShown(ComponentEvent e) {


		try {
			CommandMap param = new CommandMap();
			CommandMap result= (CommandMap) companyService.selectListByCondition(param);
			List<CommandMap> data = (List<CommandMap>) result.get("master");
			updateTree(data);

		}catch(Exception err)
		{
			JOptionPane.showMessageDialog(this, err.getMessage());
		}

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

}
