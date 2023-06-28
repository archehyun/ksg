package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.dtp.api.control.PortController;
import com.ksg.common.model.CommandMap;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.label.BoldLabel;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.InsertPortAbbrInfoDialog;
import com.ksg.workbench.master.dialog.UpdatePortInfoDialog;

import lombok.extern.slf4j.Slf4j;
import mycomp.comp.MyTable;


/**

 * @FileName : PnPort.java

 * @Date : 2021. 2. 25. 

 * @�ۼ��� : ��â��

 * @�����̷� :

 * @���α׷� ���� :�ױ� ���� ���� ȭ��

 */
@Slf4j
public class PnPort extends PnBase implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox cbxPortArea,cbxAreaCode,cbxField;

	private JTextField txfSearch;

	private AreaServiceImpl areaService = new AreaServiceImpl();

	private KSGTablePanel tableH;

	private MyTable tableD;

	private JLabel lblPortName;

	private JLabel lblArea;

	private JLabel lblAreaCode;

	private JLabel lblPationality;

	public PnPort(BaseInfoUI baseInfoUI) {

		super(baseInfoUI);

		this.addComponentListener(this);

		this.setController(new PortController());

		this.add(buildCenter());

		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

	}
	/**
	 * @return
	 */
	private JComponent buildSearchPanel() {

		KSGPanel pnSearch = new KSGPanel();

		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JLabel lbl = new JLabel("�ʵ�� : ");

		cbxField = new KSGComboBox();

		cbxField.setPreferredSize(new Dimension(100,23));

		cbxField.addItem("�ױ���");

		cbxField.addItem("����");

		txfSearch = new JTextField(15);
		txfSearch.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					fnSearch();
				}
			}
		});

		KSGGradientButton butUpSearch = new KSGGradientButton("�˻�", "images/search3.png");

		butUpSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butUpSearch.addActionListener(this);



		KSGGradientButton butCancel = new KSGGradientButton("",  "images/init.png");

		butCancel.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cbxAreaCode.setSelectedIndex(0);
				cbxPortArea.setSelectedIndex(0);
				cbxField.setSelectedIndex(0);
				txfSearch.setText("");
			}
		});

		cbxField.setPreferredSize(new Dimension(150,23));

		JLabel lblArea = new JLabel("����:");

		JLabel lblAreaCode = new JLabel("�����ڵ�:");

		cbxPortArea = new KSGComboBox();

		cbxAreaCode = new KSGComboBox();

		cbxPortArea.setPreferredSize(new Dimension(300,25));

		cbxAreaCode.setPreferredSize(new Dimension(80,25));

		pnSearch.add(lblArea);

		pnSearch.add(cbxPortArea);

		pnSearch.add(lblAreaCode);

		pnSearch.add(cbxAreaCode);

		pnSearch.add(lbl);

		pnSearch.add(cbxField);

		pnSearch.add(txfSearch);

		pnSearch.add(butUpSearch);

		pnSearch.add(butCancel);

		Box pnSearchAndCount = Box.createVerticalBox();

		pnSearchAndCount.add(pnSearch);

		KSGPanel pnMain= new KSGPanel(new BorderLayout());

		pnMain.add(buildLine(),BorderLayout.SOUTH);

		pnMain.add(pnSearchAndCount,BorderLayout.EAST);

		pnMain.add(buildTitleIcon("�ױ� ����"),BorderLayout.WEST);

		return pnMain;
	}


	private KSGPanel buildButton()
	{
		KSGPanel pnButtom = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		KSGPanel pnButtomRight = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		JButton butNewAbbr = new JButton("�߰�");

		butNewAbbr.setActionCommand("��� ���");

		JButton butDelAbbr = new JButton("����");

		butDelAbbr.setActionCommand("��� ����");

		pnButtomRight.setBorder(BorderFactory.createEtchedBorder());		

		butNewAbbr.addActionListener(this);

		butDelAbbr.addActionListener(this);

		pnButtom.add(pnButtomRight);

		return pnButtom;
	}

	private KSGPanel addComp(String name, JComponent comp)
	{
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);

		layout.setHgap(5);		

		KSGPanel pnMain = new KSGPanel(layout);

		pnMain.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		JLabel lblName = new JLabel(name);

		Font font = lblName.getFont();

		Font newFont = new Font(font.getFontName(),Font.BOLD,font.getSize());

		lblName.setFont(newFont);

		Dimension siz = lblName.getPreferredSize();

		lblName.setPreferredSize(new Dimension(75, (int) siz.getHeight()));

		pnMain.add(lblName);

		pnMain.add(comp);

		return pnMain;


	}
	private KSGPanel createPortDetail()
	{		
		lblPortName 	= new JLabel();

		lblArea 		= new JLabel();

		lblAreaCode 	= new JLabel();

		lblPationality 	= new JLabel();

		lblPortName.setBackground(Color.WHITE);

		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));		

		pnMain.setPreferredSize(new Dimension(400, 0));

		KSGPanel pnTitle = new KSGPanel(new BorderLayout());

		pnTitle.setBackground(Color.WHITE);

		pnTitle.add(new BoldLabel("�ױ�������"),BorderLayout.WEST);

		KSGPanel pnControl = new KSGPanel(new FlowLayout());

		JButton butNewAbbr = new KSGGradientButton("�߰�");

		butNewAbbr.setActionCommand("��� ���");

		JButton butDelAbbr = new KSGGradientButton("����");

		butDelAbbr.setActionCommand("��� ����");

		pnControl.add(butNewAbbr);

		pnControl.add(butDelAbbr);

		butNewAbbr.addActionListener(this);

		butDelAbbr.addActionListener(this);

		pnTitle.add(pnControl,BorderLayout.EAST);

		KSGPanel pnSubMain = new KSGPanel(new BorderLayout(5,5));

		KSGPanel pnPortInfo = new KSGPanel(new GridLayout(4,1,2,2));

		pnPortInfo.add(addComp("�ױ���",lblPortName));

		pnPortInfo.add(addComp("����",lblPationality));

		pnPortInfo.add(addComp("����",lblArea));

		pnPortInfo.add(addComp("�����ڵ�",lblAreaCode));

		tableD = new KSGAbstractTable();

		tableD.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		KSGTableColumn dcolumns = new KSGTableColumn();

		dcolumns.columnField = "port_abbr";

		dcolumns.columnName = "�ױ��� ���";

		tableD.addColumn(dcolumns);

		tableD.initComp();

		pnSubMain.add(pnPortInfo,BorderLayout.NORTH);

		pnSubMain.add(new JScrollPane(tableD));

		pnMain.add(pnTitle,BorderLayout.NORTH);

		pnMain.add(pnSubMain);

		tableD.getParent().setBackground(Color.white);

		return pnMain;
	}

	private JComponent buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		tableH = new KSGTablePanel("�ױ����");

		tableH.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if(!e.getValueIsAdjusting())
				{
					if(e.getFirstIndex()<0) return;

					String portName=(String) tableH.getValueAt(e.getFirstIndex(), 0);

					String pationality = (String) tableH.getValueAt(e.getFirstIndex(), 1);

					String area = (String) tableH.getValueAt(e.getFirstIndex(), 2);

					String areaCode = (String) tableH.getValueAt(e.getFirstIndex(), 3);

					lblPortName.setText(portName);

					lblPationality.setText(pationality);

					lblArea.setText(area);

					lblAreaCode.setText(areaCode);

					CommandMap commandMap = new CommandMap();

					commandMap.put("port_name", portName);

					callApi("selectPortDetailList", commandMap);					

				}
			}
		});

		KSGPanel pnMainCenter = new KSGPanel(new BorderLayout(5,5));

		pnMainCenter.add(tableH);

		pnMainCenter.add(createPortDetail(),BorderLayout.EAST);	

		KSGTableColumn columns[] = new KSGTableColumn[5];

		columns[0] = new KSGTableColumn();
		columns[0].columnField = "port_name";
		columns[0].columnName = "�ױ���";
		columns[0].size = 300;
		columns[0].ALIGNMENT = SwingConstants.LEFT;

		columns[1] = new KSGTableColumn();
		columns[1].columnField = "port_nationality";
		columns[1].columnName = "����";
		columns[1].size = 300;
		columns[1].ALIGNMENT = SwingConstants.LEFT;

		columns[2] = new KSGTableColumn();
		columns[2].columnField = "port_area";
		columns[2].columnName = "����";
		columns[2].size = 300;
		columns[2].ALIGNMENT = SwingConstants.LEFT;

		columns[3] = new KSGTableColumn();
		columns[3].columnField = "area_code";
		columns[3].columnName = "�����ڵ�";
		columns[3].size = 100;

		columns[4] = new KSGTableColumn();
		columns[4].columnField = "abbr_count";
		columns[4].columnName = "�󼼼�";
		columns[4].size = 50;

		tableH.setColumnName(columns);

		tableH.initComp();

		tableH.setShowControl(true);

		tableH.addMouseListener(new TableSelectListner());

		tableH.addContorlListener(this);

		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);

		pnMain.add(pnMainCenter);

		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));

		return pnMain;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("�˻�"))
		{
			this.fnSearch();
		}
		else if(command.equals(KSGTablePanel.INSERT))
		{
			KSGDialog dialog = new UpdatePortInfoDialog(UpdatePortInfoDialog.INSERT);

			dialog.createAndUpdateUI();

			if(dialog.result==KSGDialog.SUCCESS)
			{
				this.fnSearch();
			}
		}
		else if(command.equals("��� ���"))
		{
			int row=tableH.getSelectedRow();

			if(row<0) return;

			String port_name=(String) tableH.getValueAt(row, 0);

			KSGDialog dialog = new InsertPortAbbrInfoDialog(getBaseInfoUI(),port_name);

			dialog.createAndUpdateUI();

			if(dialog.result==KSGDialog.SUCCESS)
			{
				CommandMap param = new CommandMap();

				param.put("port_name", port_name);

				callApi("selectPortDetailList", param);
			}
		}
		else if(command.equals(KSGTablePanel.DELETE))
		{
			int row=tableH.getSelectedRow();
			if(row<0)
				return;

			String data = (String) tableH.getValueAt(row, 0);

			int result=JOptionPane.showConfirmDialog(PnPort.this, data+"�� ���� �Ͻðڽ��ϱ�?", "�ױ� ���� ����", JOptionPane.YES_NO_OPTION);

			if(result==JOptionPane.OK_OPTION)
			{						

				CommandMap param = new CommandMap();

				param.put("port_name", data);

				callApi("deletePort", param);

			}
			else
			{
				System.out.println("no select");
			}
		}
		else if(command.equals("��� ����"))
		{
			int row=tableD.getSelectedRow();

			if(row<0) return;

			HashMap<String, Object> data = (HashMap<String, Object>) tableD.getValueAt(row);

			String port_name = (String)data.get("port_name");
			String port_abbr = (String)data.get("port_abbr");

			int result=JOptionPane.showConfirmDialog(this, port_abbr+"�� ���� �Ͻðڽ��ϱ�?", "�ױ� ��� ���� ����", JOptionPane.YES_NO_OPTION);

			if(result==JOptionPane.OK_OPTION)
			{	
				CommandMap param = new CommandMap();

				param.put("port_name", port_name);

				param.put("port_abbr", port_abbr);

				callApi("deletePortDetail", param);
			}
		}
	}
	class TableSelectListner extends MouseAdapter
	{
		KSGDialog dialog;

		String portName;

		String pationality;

		String area;

		String areaCode;

		public void mouseClicked(MouseEvent e) 
		{	
			try
			{
				JTable es = (JTable) e.getSource();

				int row=es.getSelectedRow();
				if(row<0)
					return;

				if(e.getClickCount()>0)
				{
					CommandMap param = (CommandMap) tableH.getValueAt(row);

					lblPortName.setText((String) param.get("port_name"));

					lblPationality.setText((String) param.get("port_nationality"));

					lblArea.setText((String) param.get("port_area"));

					lblAreaCode.setText((String) param.get("area_code"));

					callApi("selectPortDetailList", param);
				}


				if(e.getClickCount()>1)
				{				
					HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);

					dialog = new UpdatePortInfoDialog(UpdatePortInfoDialog.UPDATE,param);

					dialog.createAndUpdateUI();

					if(dialog.result==KSGDialog.SUCCESS)
					{
						fnSearch();
					}
				}

			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(PnPort.this, ee.getMessage());
			}
		}
	}


	@Override
	public void fnSearch() {

		CommandMap param = new CommandMap();

		if(cbxAreaCode.getSelectedIndex()>0)
		{
			param.put("area_code", cbxAreaCode.getSelectedItem());
		}

		if(cbxPortArea.getSelectedIndex()>0)
		{
			param.put("port_area", cbxPortArea.getSelectedItem());
		}

		String field = (String) cbxField.getSelectedItem();

		String searchParam = txfSearch.getText();

		if(!"".equals(searchParam))
		{
			if(field.equals("�ױ���"))
			{
				param.put("port_name", searchParam);

			}else if(field.equals("����"))
			{
				param.put("port_nationality", searchParam);
			}	
		}			

		log.info("param:"+param);

		callApi("selectPort", param);

	}

	@Override
	public void componentShown(ComponentEvent e) {

		try {

			initComboBox(cbxPortArea, areaService.getAreaListGroupByAreaName());

			initComboBox(cbxAreaCode, areaService.getAreaListGroupByAreaCode());


		} catch (Exception ee) {

			JOptionPane.showMessageDialog(PnPort.this, ee.getMessage());
			ee.printStackTrace();
		}

		if(isShowData) fnSearch();

	}
	private void initComboBox(JComboBox combox, List list)	
	{
		combox.removeAllItems();

		combox.addItem("��ü");

		list.stream().forEach(item -> combox.addItem(item));


	}
	@Override
	public void updateView() {
		CommandMap result= this.getModel();


		String serviceId=(String) result.get("serviceId");

		if("selectPort".equals(serviceId))
		{	
			List data = (List )result.get("data");

			tableH.setResultData(data);

			tableH.setTotalCount(String.valueOf(data.size()));

			if(data.size()==0)tableH.changeSelection(0,0,false,false);

			if(data.size()==0)
			{
				lblArea.setText("");
				lblAreaCode.setText("");
				lblPationality.setText("");
				lblPortName.setText("");
				tableD.clearReslult();
			}
			else
			{
				tableH.changeSelection(0,0,false,false);
			}
		}
		else if("selectPortDetailList".equals(serviceId))
		{
			List data = (List )result.get("data");
			tableD.setResultData(data);

		}
		else if("deletePort".equals(serviceId))
		{
			fnSearch();

		}
		else if("deletePortDetail".equals(serviceId))
		{
			int row=tableH.getSelectedRow();

			String port_name=(String) tableH.getValueAt(row, 0);

			CommandMap param = new CommandMap();

			param.put("port_name", port_name);

			NotificationManager.showNotification("�����Ǿ����ϴ�.");

			callApi("selectPortDetailList", param);
		}



	}


}