package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dtp.api.service.impl.CodeServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;


/**

  * @FileName : SearchPortDialog.java

  * @Date : 2021. 3. 31. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 항구 조회

  */
@SuppressWarnings("serial")
public class SearchPortDialog extends KSGDialog implements ActionListener{

	public String result;
	
	public boolean isSamePort=false;

	private Component main;

	private PortServiceImpl portService = new PortServiceImpl();

	private CodeServiceImpl codeService = new CodeServiceImpl();

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

	private JPanel createNomalPort()
	{
		JPanel pnMain = new JPanel(new BorderLayout(5,5));

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

	private JPanel createExcpetionPort()
	{
		JPanel pnMain = new JPanel(new BorderLayout(5,5));

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

	private JPanel createCenter()
	{
		JPanel pnMain = new JPanel(new BorderLayout(5,5));
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		JPanel pnContorl = createControl();

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
					
					if(isSamePortView)
					cbxSamePort.setVisible(true);
					
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

	private JPanel createControl() {
		
		JPanel pnMain = new JPanel(new BorderLayout());
		
		JPanel pnLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JPanel pnRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butOk = new JButton("확인");

		JButton butCancel = new JButton("취소");
		
		cbxSamePort = new JCheckBox("동일항구",false);

		butOk.addActionListener(this);

		butCancel.addActionListener(this);

		pnRight.add(butOk);

		pnRight.add(butCancel);
		
		
		pnLeft.add(cbxSamePort);
		
		pnMain.add(pnLeft,BorderLayout.LINE_START);
		pnMain.add(pnRight,BorderLayout.LINE_END);
		
		
		return pnMain;
	}

	private JPanel createSerch(String type, KSGTablePanel table)
	{
		JPanel pnMain = new JPanel(new BorderLayout(5,5));

		JTextField txfInput = new JTextField();
		
		txfInput.addKeyListener(new PortNameKeyAdapter(type, table));
		
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
		String type;
		KSGTablePanel table;
		public PortNameKeyAdapter(String type, KSGTablePanel table) {

			this.type = type;

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
				try {
					
					HashMap<String, Object> param = new HashMap<String, Object>();
					
					if(type.equals("code_type"))
					{
						param.put("code_type", "port_exception");
						
						param.put("code_name", val);
						
						HashMap<String, Object> result=(HashMap<String, Object>) codeService.selectCodeDList(param);

						this.table.setResultData(result);
					}
					else
					{
						param.put(this.type, val);
						
						HashMap<String, Object> result=(HashMap<String, Object>) portService.selectList(param);

						this.table.setResultData(result);	
					}


				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}

	}

	class SelectEventHandler extends MouseAdapter implements KeyListener
	{

		private void selectPort()
		{
			int ro=tblCurrent.getSelectedRow();

			if(ro==-1)
			{
				return;
			}
			result= String.valueOf(tblCurrent.getValueAt(ro, 0));
			
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
		public void keyReleased(KeyEvent e) {
			
		}
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
			
			isSamePort = cbxSamePort.isSelected();
			
			result= String.valueOf(tblCurrent.getValueAt(ro, 0));
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

		try {
			
			cbxSamePort.setVisible(isSamePortView);
			HashMap<String, Object> commandMap = new HashMap<String, Object>();

			commandMap.put("code_type", "port_exception");
			
			HashMap<String, Object> resultMap=(HashMap<String, Object>) codeService.selectCodeDList(commandMap);

			eTable.setResultData(resultMap);		

			HashMap<String, Object> param = new HashMap<String, Object>();		

			HashMap<String, Object> result=(HashMap<String, Object>) portService.selectList(param);

			this.nTable.setResultData(result);

		}catch(Exception ee)
		{
			ee.printStackTrace();
		}

	}
}
