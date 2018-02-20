package com.ksg.view.adv.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.ksg.model.KSGModelManager;
import com.ksg.view.adv.SearchPanel;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.comp.KSGTree;
import com.ksg.view.comp.KSGTree1;
import com.ksg.view.comp.KSGTreeDefault;

public class SearchCompanyAndPageDialog extends KSGDialog{
	private String selectedParam;// 검색된 정보
	private JTextField _txfSearchByCompany;
	private KSGTreeDefault tree1;
	private JTree _treeMenu;
	private JPanel 			pnLeftMenu;
	
	private SearchPanel searchPanel;
	
	public SearchCompanyAndPageDialog(SearchPanel searchPanel) {
		this.searchPanel = searchPanel;
		
	}

	@Override
	public void createAndUpdateUI() {

		this.setTitle("검색");
		pnLeftMenu = new JPanel();
		JPanel pnSearch =  new JPanel();
		pnSearch.setLayout(new BorderLayout());

		_treeMenu = createTreeMenu();
		
		_txfSearchByCompany = new JTextField(8);

		JPanel pnSearchByCompany = new JPanel();
		
		JLabel lblCompany = new JLabel("선사 검색");
		
		lblCompany.setPreferredSize( new Dimension(60,15));
		
		pnSearchByCompany .add(lblCompany);
		
		pnSearchByCompany .add(_txfSearchByCompany);
		
		_txfSearchByCompany.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					String text=_txfSearchByCompany.getText();
					if(!searchPanel.isPageSearch)
					{
						DefaultMutableTreeNode node = KSGTree.searchNodeByCompany(tree1,text);
						if(node!=null)
						{
							TreeNode[] nodes = ((DefaultTreeModel)tree1.getModel()).getPathToRoot(node);
							TreePath path = new TreePath(nodes);
							tree1.scrollPathToVisible(path);
							tree1.setSelectionPath(path);
						}else
						{
							JOptionPane.showMessageDialog(null, "해당선사가 없습니다.");
							_txfSearchByCompany.setText("");
						}
						_txfSearchByCompany.setText("");
					}else
					{
						try{
							int page= Integer.parseInt(text);
							DefaultMutableTreeNode node = KSGTree.searchNodeByPage(tree1,page);
							if(node!=null)
							{
								TreeNode[] nodes = ((DefaultTreeModel)tree1.getModel()).getPathToRoot(node);
								TreePath path = new TreePath(nodes);
								tree1.scrollPathToVisible(path);
								tree1.setSelectionPath(path);
								_txfSearchByCompany.setText("");
							}else
							{
								JOptionPane.showMessageDialog(null, "해당 Page가 없습니다.");
								_txfSearchByCompany.setText("");
							}
						}catch (NumberFormatException ee) {
							JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, text+" <== 정확한 숫자를 입력하세요");
							//							ee.printStackTrace();
							logger.error(ee.getMessage());
							_txfSearchByCompany.setText("");
						}
					}

				}
			}
		});
		JCheckBox box = new JCheckBox("선사",true);
		box.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box =(JCheckBox) e.getSource();
				searchPanel.isPageSearch=box.isSelected();

			}});

		pnSearchByCompany.add(box);

		pnSearch.add(pnSearchByCompany);

		pnLeftMenu.setLayout(new BorderLayout());


		JPanel pnContorl = new JPanel();
		ButtonGroup group = new ButtonGroup();


		JRadioButton button = new JRadioButton("선사별");
		JRadioButton button1 = new JRadioButton("페이지별",true);
		group.add(button);
		group.add(button1);

		//pnContorl.add(button);
		//pnContorl.add(button1);
		JButton butClose = new JButton("닫기");
		butClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SearchCompanyAndPageDialog.this.setVisible(false);
				SearchCompanyAndPageDialog.this.dispose();

			}
		});

		pnContorl.add(butClose);
		ItemListener itemListener= new ItemListener(){

			public void itemStateChanged(ItemEvent e) {
				AbstractButton but = (AbstractButton) e.getSource();
				if(ItemEvent.SELECTED==e.getStateChange())
				{
					String te = but.getActionCommand();
					logger.debug("selected "+te);
					if(te.equals("선사별"))
					{
						tree1.setGroupBy(KSGTree1.GroupByCompany);
					}
					else if(te.equals("페이지별"))
					{
						tree1.setGroupBy(KSGTree1.GroupByPage);
					}
					searchPanel.manager.execute(tree1.getName());
				}
			}};
			button.addItemListener(itemListener);
			button1.addItemListener(itemListener);
			pnContorl.add(new JSeparator(JSeparator.HORIZONTAL));
			JButton butADDTable = new JButton(new ImageIcon("images/plus.gif"));
			/*
			butADDTable.setPreferredSize(new Dimension(35,25));
			butADDTable.setFocusPainted(false);
			butADDTable.setActionCommand("신규등록");
			butADDTable.setToolTipText("신규 테이블 등록");
			butADDTable.addActionListener(this);*/

			/*				pnContorl.add(butADDTable);
			JButton butDelTable = new JButton(new ImageIcon("images/minus.gif"));
			butDelTable.setPreferredSize(new Dimension(35,25));
			butDelTable.setFocusPainted(false);
			butDelTable.setActionCommand("삭제");
			butDelTable.addActionListener(this);
			pnContorl.add(butDelTable);*/

			JPanel pnTitle = new JPanel();
			pnTitle.setBackground(new Color(88,141,250));
			pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel("테이블 목록");
			label.setForeground(Color.white);
			pnTitle.add(label);
			pnTitle.setPreferredSize( new Dimension(0,22));

			pnSearch.add(pnSearchByCompany,BorderLayout.NORTH);
			pnSearch.add(new JScrollPane(_treeMenu),BorderLayout.CENTER);
			pnSearch.add(pnContorl,BorderLayout.SOUTH);


			JPanel pnMain = new JPanel(new BorderLayout());

			pnMain.add(pnSearch,BorderLayout.CENTER);

			this.getContentPane().add(pnMain);
			this.getContentPane().add(KSGDialog.createMargin(10, 0),BorderLayout.WEST);

			this.setSize(400,400);
			//this.setLocationRelativeTo(SearchPanel.this);

			this.setVisible(true);


	}
	/**
	 * @return
	 */
	private JTree createTreeMenu() 
	{
		tree1 = new KSGTreeDefault("tree1");
		tree1.setComponentPopupMenu(createTreePopuomenu());
		try {

			tree1.addTreeSelectionListener(new TreeSelectionListener(){

				public void valueChanged(TreeSelectionEvent e) {

					TreePath path=e.getNewLeadSelectionPath();
					if(path!=null)
					{

						try{
							searchPanel.updateViewByTree(path);
						}catch(Exception e2)
						{
							e2.printStackTrace();
							return;
						}
					}
				}
			});
			tree1.update(KSGModelManager.getInstance());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return tree1;
	}
	/**
	 * @return
	 */
	private JPopupMenu createTreePopuomenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenu newMenu = new JMenu("새로 만들기");
		JMenuItem itemCompany = new JMenuItem("선사");

		JMenuItem itemTable = new JMenuItem("테이블");
		itemTable.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				/*AddTableInfoDialog addTableInfoDialog = new AddTableInfoDialog(ADVManageUI.this,manager.selectedCompany);
				addTableInfoDialog.createAndUpdateUI();*/
			}});
		JMenuItem xlsMenu = new JMenuItem("파일 불러오기");
		newMenu.add(itemCompany);
		newMenu.add(itemTable);


		menu.add(newMenu);
		menu.add(xlsMenu);
		return menu;
	}
}
