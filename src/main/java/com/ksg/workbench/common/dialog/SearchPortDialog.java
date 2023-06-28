package com.ksg.workbench.common.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dtp.api.control.PortController;
import com.dtp.api.service.impl.CodeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.Code;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.master.dialog.BaseInfoDialog;
import com.ksg.workbench.shippertable.dialog.ManagePortDialog;


/**

 * @FileName : SearchPortDialog.java

 * @Date : 2021. 3. 31. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 항구 조회

 */
@SuppressWarnings("serial")
public class SearchPortDialog extends BaseInfoDialog implements ActionListener{

	public String result;

	public boolean isSamePort=false;

	private Component main;

	private KSGTablePanel nTable;

	private KSGTablePanel tblCurrent;

	private KSGTablePanel eTable;	

	private SelectEventHandler selectEventHandler = new SelectEventHandler();

	private JCheckBox cbxSamePort;

	private boolean isSamePortView;

	public SearchPortDialog(ManagePortDialog main, List<String> portli) {
		this(main);
	}

	public SearchPortDialog(JDialog main,List<HashMap<String,Object>> portli2, boolean samePort) {

		this(main);

		this.isSamePortView = samePort;
	}

	public SearchPortDialog(JDialog main,List<HashMap<String,Object>> portli2) {

		this(main);

		this.isSamePortView = true;
	}

	public SearchPortDialog(JDialog main) {

		super(main);

		this.setController(new PortController());

		this.main = main;
	}

	public void createAndUpdateUI()
	{
		this.setTitle("항구 선택");

		this.setModal(true);

		this.addComponentListener(this);

		this.getContentPane().add(createCenter());

		int x=main.getX()+main.getWidth();

		int y=main.getY();

		int h=main.getHeight();

		this.setSize(new Dimension(300,h));

		this.setLocation(x, y);

		this.setVisible(true);
	}

	private KSGPanel createNomalPort()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		nTable = new KSGTablePanel();

		nTable.addColumn(new KSGTableColumn("port_name", "항구명"));

		nTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		nTable.initComp();

		nTable.addMouseListener(selectEventHandler);

		nTable.addKeyListener(selectEventHandler);

		pnMain.add(nTable);

		pnMain.add(createSerch("port_name",nTable),BorderLayout.NORTH);

		return pnMain;
	}

	private KSGPanel createExcpetionPort()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		eTable = new KSGTablePanel();

		eTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		eTable.addColumn(new KSGTableColumn("code_name", "항구명"));		

		eTable.initComp();			

		eTable.addMouseListener(selectEventHandler);

		eTable.addKeyListener(selectEventHandler);

		pnMain.add(eTable);

		pnMain.add(createSerch("code_type",eTable),BorderLayout.NORTH);

		return pnMain;
	}

	private KSGPanel createCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		KSGPanel pnContorl = createControl();

		JTabbedPane pane = new JTabbedPane();

		pane.addTab("일반", createNomalPort());

		pane.addTab("예외", createExcpetionPort());

		pane.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) 
			{
				JTabbedPane tp = (JTabbedPane) e.getSource();
				int index=tp.getSelectedIndex();
				switch (index) {
				case 0:
					tblCurrent = nTable; // 일반항구

					if(isSamePortView) cbxSamePort.setVisible(true);

					break;
				case 1:

					tblCurrent = eTable; // 예외항구
					
					cbxSamePort.setSelected(false);
					
					break;

				default:
					break;
				}

			}});

		pnMain.add(pane);

		pnMain.add(pnContorl,BorderLayout.SOUTH);

		return pnMain;
	}

	private KSGPanel createControl() {

		KSGPanel pnMain 		= new KSGPanel(new BorderLayout());

		KSGPanel pnLeft 		= new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		KSGPanel pnRight 		= new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butOk 			= new KSGGradientButton("확인");

		JButton butCancel 		= new KSGGradientButton("취소");

		cbxSamePort 			= new JCheckBox("동일항구",false);
		
		cbxSamePort.setBackground(Color.white);

		butOk.addActionListener(this);

		butCancel.addActionListener(this);

		pnRight.add(butOk);

		pnRight.add(butCancel);

		pnLeft.add(cbxSamePort);

		pnMain.add(pnLeft,BorderLayout.LINE_START);

		pnMain.add(pnRight,BorderLayout.LINE_END);

		return pnMain;
	}

	private KSGPanel createSerch(String tabType, KSGTablePanel table)
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		JTextField txfInput = new JTextField();

		txfInput.addKeyListener(new PortNameKeyAdapter(tabType, table));

		pnMain.add(new JLabel("검색:"),BorderLayout.WEST);

		pnMain.add(txfInput);

		return pnMain;
	}
	public void close()
	{
		this.setVisible(false);

		this.dispose();
	}

	class PortNameKeyAdapter extends KeyAdapter
	{
		private String type;

		private KSGTablePanel table;

		public PortNameKeyAdapter(String tabType, KSGTablePanel table) {

			this.type = tabType;

			this.table = table;
		}

		@Override
		public void keyTyped(KeyEvent e) {


		}
		@Override
		public void keyReleased(KeyEvent e) {
			JTextField txf = (JTextField) e.getSource();
			String val = txf.getText();

			if(!"".equals(val))
			{

				CommandMap param = new CommandMap();

				if(type.equals("code_type"))
				{
					param.put("code_type", "port_exception");

					param.put("code_name", val);

					callApi("searchPortDialog.searchPortException", param);
				}
				else
				{
					param.put("port_name", val);
					
					callApi("searchPortDialog.searchPort", param);
				}
			}
			else
			{
				this.table.clearResult();
			}
		}
	}

	class SelectEventHandler extends MouseAdapter implements KeyListener
	{
		private void selectPort()
		{
			int ro = tblCurrent.getSelectedRow();

			if(ro==-1) return;
			
			result = String.valueOf(tblCurrent.getValueAt(ro, 0));

			close();
		}
		public void mouseClicked(MouseEvent e) {
			
			if(e.getClickCount()>1)
			{
				selectPort();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {

			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				selectPort();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals("확인"))
		{
			int ro=tblCurrent.getSelectedRow();
			if(ro==-1)
			{
				JOptionPane.showMessageDialog(null, "선택된 항구명이 없습니다.");
				return;
			}

			isSamePort 	= cbxSamePort.isSelected();

			result		= String.valueOf(tblCurrent.getValueAt(ro, 0));
			
			close();
		}
		else if(command.equals("취소"))
		{
			close();
		}
	}
	
	@Override
	public void componentShown(ComponentEvent e) {

		tblCurrent = nTable;

		callApi("searchPortDialog.init");
	}

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("searchPortDialog.init".equals(serviceId)) {

			List portExceptionList = (List )result.get("portExceptionList");

			this.eTable.setResultData(portExceptionList);
		}

		else if("searchPortDialog.searchPortException".equals(serviceId)) {

			List portExceptionList = (List )result.get("portExceptionList");

			this.eTable.setResultData(portExceptionList);
		}
		
		else if("searchPortDialog.searchPort".equals(serviceId)) {

			List portList = (List )result.get("portList");

			this.nTable.setResultData(portList);
		}
	}
}