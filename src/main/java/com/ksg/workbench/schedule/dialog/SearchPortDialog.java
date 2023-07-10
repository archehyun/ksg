package com.ksg.workbench.schedule.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.dtp.api.control.PortController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.dialog.MainTypeDialog;

import mycomp.comp.MyTable;

/**

 * @FileName : SearchPortDialog.java

 * @Project : KSG2

 * @Date : 2022. 3. 25. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 항구 정보 조회 팝업

 */
public class SearchPortDialog extends MainTypeDialog{

	private JLabel lblTitle;

	private MyTable tableH;

	private MyTable tableD;

	private JTextField txfInput;

	public String result;

	public SearchPortDialog() {
		super();
		this.setController(new PortController());
		
	}

	@Override
	public void createAndUpdateUI() {
		
		this.setModal(true);
		
//		this.setTitle(title);
		
		this.getContentPane().add(buildHeader(),BorderLayout.NORTH);

		this.addComp(buildCenter(),BorderLayout.CENTER);

		this.addComp(buildControl(),BorderLayout.SOUTH);

		this.pack();
		
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		
		this.setResizable(false);
		
		this.setVisible(true);
	}
	
	public KSGPanel buildHeader()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
//		pnMain.setBackground(Color.lightGray);
		
		pnMain.add(buildTitle("항구조회"));
		
		return pnMain;
	}

	public KSGPanel buildTitle(String title)
	{
		KSGPanel pnTitle = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnTitle.setBorder(BorderFactory.createEmptyBorder(5,15,5,0));
//		#000046, #1cb5e0	
		pnTitle.setBackground(titleColor);
		
		lblTitle = new JLabel(title);
		
		lblTitle.setFont(new Font("area",Font.BOLD,20));
		lblTitle.setForeground(Color.white);
		
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
				
				if(row<0) return;

				if(e.getClickCount()>0)
				{
					HashMap<String, Object> selectedItem = (HashMap<String, Object>) tableH.getValueAt(row);

					CommandMap param = new CommandMap();

					param.put("port_name", selectedItem.get("port_name"));

					result = (String) selectedItem.get("port_name");

					callApi("selectPortDetailList", param);
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
				
				if(row<0) return;

				tableH.clearSelection();

				HashMap<String, Object> param = (HashMap<String, Object>) tableD.getValueAt(row);

				if(e.getClickCount()>0)
				{					
					result = (String) param.get("port_abbr");
				}
				else if(e.getClickCount()>1)
				{
					result = (String) param.get("port_abbr");
					SearchPortDialog.this.close();
				}
			}
		});

		KSGPanel pnMainDetail = new KSGPanel(new BorderLayout(5,5));
		
		
		JScrollPane comp = new JScrollPane(tableH);
		
		tableH.getParent().setBackground(Color.white);

		JScrollPane compRight = new JScrollPane(tableD);

		compRight.setBackground(Color.white);

		tableD.getParent().setBackground(Color.white);

		compRight.setPreferredSize(new Dimension(200,200));
		
		KSGPanel pnMainNorth =  new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel label = new JLabel("상세정보",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		
		pnMainNorth.add(label);
		
		pnMainNorth.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		
		pnMainDetail.add(pnMainNorth,BorderLayout.NORTH);

		pnMainDetail.add(compRight,BorderLayout.EAST);
		
		pnMainDetail.add(comp);

		pnMainDetail.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		KSGPanel pnMain =  new KSGPanel(new BorderLayout(10,10));
		
		pnMain.add(buildNorth(),BorderLayout.NORTH);
		
		pnMain.add(pnMainDetail);

		return pnMain;
	}
	
	private KSGPanel buildNorth()
	{
		txfInput = new JTextField(15);

		txfInput.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				
				fnSearch();	
			}

		});
		
		
		KSGPanel pnSearchLabel = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("검색조건",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		pnSearchLabel.add(label);
		
		KSGPanel pnSearchOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		pnSearchOption.add(new JLabel("항구명"));
		pnSearchOption.add(txfInput);
		pnSearchOption.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.lightGray,1),BorderFactory.createEmptyBorder(10,15,10,15)));
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		pnMain.add(pnSearchLabel, BorderLayout.NORTH);
		
		pnMain.add(pnSearchOption);
		
		return pnMain;
		
	}

	private void fnSearch()
	{
		CommandMap param = new CommandMap();

		String input =txfInput.getText();
		
		if(input.isEmpty())
		{
			tableH.clearReslult();
			
			tableD.clearReslult();
			
			return;
		}
		
		
		param.put("port_name", input);
		
		callApi("selectPort", param);
		
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

	@Override
	public void updateView() {

		CommandMap resultMap= this.getModel();

		boolean success = (boolean) resultMap.get("success");

		if(success)
		{
			String serviceId=(String) resultMap.get("serviceId");

			if("selectPort".equals(serviceId))
			{	
				List data = (List )resultMap.get("data");

				tableH.setResultData(data);
			}
			else if("selectPortDetailList".equals(serviceId))
			{	
				List data = (List )resultMap.get("data");

				tableD.setResultData(data);

			}
		}
		else{  
			String error = (String) resultMap.get("error");

			JOptionPane.showMessageDialog(this, error);
		}
	}
}
