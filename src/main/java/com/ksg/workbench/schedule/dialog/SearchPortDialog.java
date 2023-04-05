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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.dtp.api.control.PortController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.service.PortService;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.button.GradientButton;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.dialog.BaseInfoDialog;
import com.ksg.workbench.master.dialog.UpdatePortInfoDialog;

/**

 * @FileName : SearchPortDialog.java

 * @Project : KSG2

 * @Date : 2022. 3. 25. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 항구 정보 조회 팝업

 */
public class SearchPortDialog extends BaseInfoDialog{

	PortService service;

	private JLabel lblTitle;

	private JButton butOK;

	private JButton butCancel;

	private KSGAbstractTable tableH;

	private KSGAbstractTable tableD;

	private JTextField txfInput;

	public String result;

	public SearchPortDialog() {
		this.setController(new PortController());

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

		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		txfInput = new JTextField();

		txfInput.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				
				fnSearch();
			}

		});
		pnMain.add(new JScrollPane(tableH));

		tableH.getParent().setBackground(Color.white);

		JScrollPane compDetail = new JScrollPane(tableD);

		compDetail.setBackground(Color.white);

		tableD.getParent().setBackground(Color.white);

		compDetail.setPreferredSize(new Dimension(200,200));

		pnMain.add(compDetail,BorderLayout.EAST);

		pnMain.add(txfInput,BorderLayout.NORTH);

		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		return pnMain;
	}

	
	private void fnSearch()
	{
		//		try {


		CommandMap param = new CommandMap();

		String input =txfInput.getText();

		if(!"".equals(input)) param.put("port_name", input);
		
		
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
