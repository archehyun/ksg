/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.workbench.master.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.service.AreaService;
import com.ksg.service.PortService;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;

/**
 * �ױ� ���� ���� ���̾�׷�
 * 
 * @author ��â��
 *
 */
public class UpdatePortInfoDialog extends BaseInfoDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, Object> param;

	private JTextField txfPort_name;

	private JTextField txfPort_nationailty;

	private JTextField txfPort_area;

	private JTextField txfArea_code;	


	SearchAreaDialog searchAreaDailog;

	private PortService portService;

	private AreaService areaService;

	CommandMap areaMap;

	public UpdatePortInfoDialog(int type)
	{
		super();
		portService = new PortServiceImpl();
		areaService = new AreaServiceImpl();
		this.type = type;
		areaMap = new CommandMap();
	}

	public UpdatePortInfoDialog(int type, HashMap<String, Object> param) {
		this(type);
		this.param = param;


	}
	public void createAndUpdateUI() {

		this.addComponentListener(this);

		this.setModal(true);

		this.setTitle("�ױ� ���� ����");

		this.getContentPane().add(buildTitle("�ױ� ���� ����"),BorderLayout.NORTH);

		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);

		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);

		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setVisible(true);

	}

	public KSGPanel buildCenter()
	{
		txfPort_name = new JTextField(20);
		txfPort_nationailty = new JTextField(20);
		txfPort_area = new JTextField(20);
		txfArea_code = new JTextField(5);



		Box pnCenter = new Box(BoxLayout.Y_AXIS);

		txfArea_code.setEditable(false);
		txfPort_area.setEditable(false);





		JButton butSearchCode = new JButton("�ڵ� �˻�");
		butSearchCode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				searchAreaDailog = new SearchAreaDialog(UpdatePortInfoDialog.this);
				searchAreaDailog.createAndUpdateUI();

			}});

	

		pnCenter.add(createFormItem(txfPort_name, "�ױ���"));
		pnCenter.add(createFormItem(txfPort_nationailty, "����"));
		pnCenter.add(createFormItem(txfPort_area, "����"));
		pnCenter.add(createFormItem(txfArea_code, butSearchCode, "�����ڵ�"));	


		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		pnMain.add(pnCenter);
		return pnMain;
	}




	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("����"))
		{

			if(txfPort_name.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ױ����� �����ϴ�.");
				return;
			}
			if(txfPort_nationailty.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�������� �����ϴ�.");
				return;
			}
			if(txfArea_code.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ڵ��ȣ�� �����ϼ���");
				return;
			}


			CommandMap param = new CommandMap();


			param.put("area_code", txfArea_code.getText());

			param.put("port_name", txfPort_name.getText());

			param.put("port_nationality", txfPort_nationailty.getText());

			param.put("port_area", txfPort_area.getText());

			param.put("base_port_name", this.param.get("port_name"));

			try {
				portService.update(param);
				this.setVisible(false);
				this.dispose();
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�ױ��� �����߽��ϴ�.");

			} catch (SQLException e1) {


				if(e1.getErrorCode()==2627)
				{
					JOptionPane.showMessageDialog(this, "�ױ����� �����մϴ�.");


				}else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());
					e1.printStackTrace();
				}
			}

		}
		else if(command.equals("�߰�"))
		{

			if(txfPort_name.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ױ����� �����ϴ�.");
				return;
			}
			if(txfPort_nationailty.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�������� �����ϴ�.");
				return;
			}
			if(txfArea_code.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ڵ��ȣ�� �����ϼ���");
				return;
			}

			try {
				CommandMap param = new CommandMap();


				param.put("area_code", txfArea_code.getText());

				param.put("port_name", txfPort_name.getText());

				param.put("port_nationality", txfPort_nationailty.getText());

				param.put("port_area", txfPort_area.getText());

				portService.insert(param);

				this.setVisible(false);

				this.dispose();

				this.result=KSGDialog.SUCCESS;
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�ױ��� �߰��߽��ϴ�.");

			} 
			catch (AlreadyExistException e1)
			{
				JOptionPane.showMessageDialog(this, "�ױ����� �����մϴ�.");
			}
			catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, e1.getMessage());
			}

		}
		else if(command.equals("���"))
		{
			this.result=KSGDialog.FAILE;
			this.setVisible(false);
			this.dispose();
		}

	}

	@Override
	public void componentShown(ComponentEvent e) {

		if(param!=null)
		{	
			txfArea_code.setText((String) param.get("area_code"));
			txfPort_area.setText((String) param.get("port_area"));
			txfPort_name.setText((String) param.get("port_name"));
			txfPort_nationailty.setText((String) param.get("port_nationality"));
		}

		try {
			List<CommandMap> li = areaService.selectAreaInfoList();

			for(CommandMap item:li)
			{
				areaMap.put(String.valueOf(item.get("area_name")), item);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		switch (type) {
		case UPDATE:

			lblTitle.setText("�ױ����� ����");
			butOK.setActionCommand("����");

			break;
		case INSERT:

			lblTitle.setText("�ױ����� �߰�");
			butOK.setActionCommand("�߰�");

			break;

		}

	}

	class SearchAreaDialog extends KSGDialog
	{

		private JButton butOk;
		private JButton butCancel;
		
		public SearchAreaDialog(JDialog dialog)		
		{
			super(dialog);
		}

		private KSGPanel buildCenter()
		{

			KSGPanel pnMain = new KSGPanel();

			JTable tblAreaList = new JTable();

			DefaultMutableTreeNode root = new DefaultMutableTreeNode("��ü����");
			Iterator<String> iter =areaMap.keySet().iterator();
			while(iter.hasNext())
			{
				String area_name = (String) iter.next();
				DefaultMutableTreeNode areaGroup = new DefaultMutableTreeNode(area_name);
				root.add(areaGroup);

			}
			final JTree tree = new JTree(root);
			tree.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent arg0) {
					if(arg0.getClickCount()>=0)
					{
						TreePath path=tree.getSelectionPath();
						if(path==null)
							return;
						switch (path.getPathCount()) {

						case 2:
							String area1=path.getLastPathComponent().toString();


							CommandMap areaInfo = (CommandMap) areaMap.get(area1);
							txfArea_code.setText(String.valueOf(areaInfo.get("area_code")));
							txfPort_area.setText(String.valueOf(areaInfo.get("area_name")));

							butOk.setEnabled(true);
							break;

						case 3:
							String area2=path.getLastPathComponent().toString();

							CommandMap areaInfo2 = (CommandMap) areaMap.get(area2);
							txfArea_code.setText(String.valueOf(areaInfo2.get("area_code")));
							txfPort_area.setText(String.valueOf(areaInfo2.get("area_name")));



							butOk.setEnabled(true);
							break;

						default:
							butOk.setEnabled(false);
							txfArea_code.setText("");
							txfPort_area.setText("");

							break;
						}

					}

				}
			});


			pnMain.add(new JScrollPane(tree),BorderLayout.CENTER);
			
			return pnMain;
		}

		private KSGPanel buildControl()
		{

			KSGPanel pnControl = new KSGPanel();
			pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
			butOk = new JButton("Ȯ��");
			butOk.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					dispose();

				}});
			butOk.setEnabled(false);
			butCancel = new JButton("���");
			butCancel.addActionListener(new ActionListener(){


				public void actionPerformed(ActionEvent e) {
					txfPort_area.setText((String) param.get("port_area"));
					txfArea_code.setText((String) param.get("area_code"));
					setVisible(false);
					dispose();

				}});
			pnControl.add(butOk);
			pnControl.add(butCancel);


			return pnControl;
		}

		@Override
		public void createAndUpdateUI() {
			getContentPane().add(buildControl(),BorderLayout.SOUTH);
			getContentPane().add(buildCenter(),BorderLayout.CENTER);
			setSize(400,400);
			setLocation(UpdatePortInfoDialog.this.getX()+UpdatePortInfoDialog.this.getWidth(), UpdatePortInfoDialog.this.getY());
			setVisible(true);
		}

	}




}
