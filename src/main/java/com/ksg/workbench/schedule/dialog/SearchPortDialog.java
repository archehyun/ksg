package com.ksg.workbench.schedule.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.service.PortService;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.dialog.UpdatePortInfoDialog;

/**

  * @FileName : SearchPortDialog.java

  * @Project : KSG2

  * @Date : 2022. 3. 25. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 항구 정보 조회 팝업

  */
public class SearchPortDialog extends KSGDialog implements ActionListener{
	
	PortService service;
	private JLabel lblTitle;
	private JButton butOK;
	private JButton butCancel;
	private KSGAbstractTable tableH;
	private KSGAbstractTable tableD;
	private JTextField txfInput;
	
	public String result;
	public SearchPortDialog() {
		service = new PortServiceImpl();
	}

	@Override
	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle(title);

		this.getContentPane().add(buildTitle("항구 조회"),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setResizable(false);
		this.setVisible(true);
		
	}
	
	public KSGPanel buildTitle(String title)
	{
		KSGPanel pnTitle = new KSGPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("area",Font.BOLD,16));
		pnTitle.add(lblTitle);
		return pnTitle;
	}
	
	public KSGPanel buildCenter()
	{
		
		tableH = new KSGAbstractTable();
		tableH.addColumn(new KSGTableColumn("port_name","항구명"));
		tableH.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableH.initComp();
		
		tableH.addMouseListener(new MouseAdapter() {
			
			
			public void mouseClicked(MouseEvent e) 
			{			
				
				JTable es = (JTable) e.getSource();
				
				int row=es.getSelectedRow();
				if(row<0)
					return;
				
				if(e.getClickCount()>0)
				{
					
					HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);
					
					HashMap<String, Object> commandMap = new HashMap<String, Object>();
					
					commandMap.put("port_name", param.get("port_name"));
					
					try {
						result = (String) param.get("port_name");
						List li=service.selectPortAbbrList(commandMap);
						
						tableD.setResultData(li);
						
					} catch (SQLException e1) {
						
						e1.printStackTrace();
					}
				}
			}

		});
		
		tableD = new KSGAbstractTable();
		tableD.addColumn(new KSGTableColumn("port_abbr","약어"));
		tableD.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableD.initComp();
		tableD.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) 
			{	
				JTable es = (JTable) e.getSource();
				
				int row=es.getSelectedRow();
				if(row<0)
					return;
				
				tableH.clearSelection();
				HashMap<String, Object> param = (HashMap<String, Object>) tableD.getValueAt(row);
				if(e.getClickCount()>0)
				{					
					System.out.println("close1");
					result = (String) param.get("port_abbr");
				}
				else if(e.getClickCount()>1)
				{
					System.out.println("close");
					result = (String) param.get("port_abbr");
					SearchPortDialog.this.close();
				}
			}
			
		});
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));
		
		txfInput = new JTextField();
		
		txfInput.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				char keycode = e.getKeyChar();
				
				System.out.println("key inpuit:"+keycode);
				if(keycode == KeyEvent.VK_ENTER)
				{	
					fnSearch();
				}
			}
			
		});
		pnMain.add(new JScrollPane(tableH));
		tableH.getParent().setBackground(Color.white);
		JScrollPane compDetail = new JScrollPane(tableD);
		tableD.setBackground(Color.white);
		compDetail.setPreferredSize(new Dimension(200,200));
		pnMain.add(compDetail,BorderLayout.EAST);
		pnMain.add(txfInput,BorderLayout.NORTH);
		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		return pnMain;
	}
	
	protected KSGPanel buildControl()
	{
		KSGPanel pnMain =  new KSGPanel(new BorderLayout());
		KSGPanel pnControl =  new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		

		butOK = new JButton("저장");

		butCancel = new JButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);
		
		
		KSGPanel pnS = new KSGPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		KSGPanel pnS1 = new KSGPanel();
		pnS1.setPreferredSize(new Dimension(0,15));
		
		pnMain.add(pnControl);
		
		pnMain.add(pnS,BorderLayout.NORTH);
		
		return pnMain;
	}
	private void fnSearch()
	{
		try {
			
			
		HashMap<String, Object> param = new HashMap<String, Object>();
		System.out.println("search");
		String input =txfInput.getText();
		if(!"".equals(input))
		{
			param.put("port_name", input);
		}
		HashMap<String, Object> result = (HashMap<String, Object>)service.selectListByCondition(param);
		tableH.setResultData((List) result.get("master"));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(command.equals("저장"))
		{	
			this.close();
		}
		
		if(command.equals("취소"))
		{
			result = "";
			this.close();
		}
		
	}

}
