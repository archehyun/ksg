
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
package com.ksg.adv.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.ksg.adv.logic.model.TableLocation;
import com.ksg.adv.logic.xml.KSGXMLManager;
import com.ksg.adv.view.comp.VesselListComp;
import com.ksg.adv.view.dialog.SearchVesselDialog;
import com.ksg.adv.view.dialog.TableInfoDialog;
import com.ksg.adv.view.xls.XLSTableInfo;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.model.KSGObserver;
import com.ksg.common.util.KSGPropertis;
import com.ksg.common.util.KSGPropertyManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Property;
import com.ksg.shippertable.service.TableService;
import com.ksg.shippertable.service.impl.TableServiceImpl;
import com.ksg.view.comp.table.KSGTable;
import com.ksg.view.comp.table.KSGXMLTable;

/**
 * @author 박창현
 * 엑셀에서 가져오 테이블에 대한 정보 표시
 */
public class KSGXLSImportPanel extends JPanel implements KSGObserver, ActionListener{

	private static final String ACTION_COMMAND_XML = "XML";

	class MYKeyApater extends KeyAdapter
	{
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				int portcount=Integer.parseInt(txfPortCount.getText());
				int vesselcount = Integer.parseInt(txfVesselCount.getText());

				try {
					tableService.updateTablePortCount(KSGXLSImportPanel.this.getTable_id(),portcount);
					
					tableService.updateTableVesselCount(KSGXLSImportPanel.this.getTable_id(),vesselcount);

					KSGModelManager.getInstance().execute(KSGXLSImportPanel.this.getName());


				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				catch (NumberFormatException e2) {
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "숫자를 입력하십시요");
				}
			}
		}
	}
	
	private String ACTION_COMMAND_TABLE_INFO_SEARCH = "테이블 정보";

	private static final long serialVersionUID = 1L;

	private CardLayout cardLayout;

	private JComboBox cbxCount,cbxDivider;

	private ActionListener cbxUnderPortAction,voyageActioon,keyAction,dividerAction,countAction;

	private JCheckBox cbxVesselVoyage,cbxVoyage,cbxUnderPort;

	private String data,isUnderPort,TEXT="text",TABLE="table";

	private Font defaultFont = new Font("돋음",0,10);

	private int index;	

	private JLabel lblCount,lblDivider,lblTableID,lblPortCount,lblVesselCount;

	private VesselListComp litVessel;

	protected Logger logger = Logger.getLogger(this.getClass());

	private JPanel pnCard,pnDivider;

	private KSGPropertis propertis = KSGPropertis.getIntance();

	private String table_id;

	private ShippersTable tableInfo;

	private Table_Property tableProperty;

	private TableService tableService;

	private KSGXMLTable tblADV;

	//private PortTable tblPort;
	
	private SearchedPortTable tblPort;

	private JTextArea txaADV;

	private JTextField txfOther,txfPage,txfPort,txfVessel,txfPortCount,txfVesselCount;

	private JMenuItem vesselAddItem;

	private XLSTableInfo xlstableinfo; // 엑셀 정보

	private int tableIndex;

	private JSplitPane pnMain;

	private JPanel pnCenter;

	private JScrollPane jScrollPane;

	public KSGXLSImportPanel() 
	{		
		isUnderPort = (String)propertis.getValues(KSGPropertis.PROPERTIES_UNDERPORT).toString();
		
		tableService = new TableServiceImpl();
		
		createAndUpdatePN();

	}
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		if(command.equals("적용하기"))
		{
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "반영 되었습니다.");
		}
		
		else if(command.equals(ACTION_COMMAND_TABLE_INFO_SEARCH))
		{
			TableInfoDialog dialog = new TableInfoDialog(KSGXLSImportPanel.this);
			dialog.createAndUpdateUI();
		}
		else if(command.equals(ACTION_COMMAND_XML))
		{
			JDialog dialog = new JDialog();
			
			dialog.setModal(true);
			
			JPanel pnMain = new JPanel(new BorderLayout());

			JTextArea area = new JTextArea();
			
			pnMain.add(new JScrollPane(area));

			dialog.getContentPane().add(pnMain);

			String data = tblADV.getXMLModel();
			
			area.setText(data);
			
			try 
			{
				KSGXMLManager manager = new KSGXMLManager();
				DefaultTableModel model = manager.createXLSTableModel(data);
				JTable table = new JTable();

				table.setModel(model);
				pnMain.add(new JScrollPane(table),BorderLayout.SOUTH);

			} catch (JDOMException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			dialog.setSize(new Dimension(800,800));
			ViewUtil.center(dialog);
			dialog.setVisible(true);
		}
	}
	/**
	 * @param label
	 * @param comp
	 * @return
	 */
	public Component addForm(String label,Component comp)
	{
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lbllabel = new JLabel(label);
		lbllabel.setFont(KSGModelManager.getInstance().defaultFont);
		comp.setFont(KSGModelManager.getInstance().defaultFont);
		pnMain.add(lbllabel);
		pnMain.add(comp);
		return pnMain;
	}
	/**
	 * @return
	 */
	public JPanel buildControlPn()
	{
		JPanel pnCenterControl = new JPanel(new BorderLayout());
		pnCard = new JPanel();
		

		//JButton butReload = new JButton("다시 불러오기");
		//butReload.setEnabled(false);

		JToggleButton butShowText = new JToggleButton("텍스트 보기");
		butShowText.setVisible(false);

		butShowText.setFont(defaultFont);
		butShowText.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JToggleButton ee=(JToggleButton) e.getSource();

				ButtonModel model =ee.getModel();
				if(model.isSelected())
				{
					cardLayout.show(pnCard, TEXT);

				}else
				{
					cardLayout.show(pnCard, TABLE);
				}
			}});


		JButton butXML = new JButton(ACTION_COMMAND_XML);
		butXML.setActionCommand(ACTION_COMMAND_XML);
		butXML.addActionListener(this);

		JPanel pnCenterControlRight = new JPanel();
		pnCenterControlRight.add(butShowText);
		//pnCenterControlRight.add(butReload);
		pnCenterControlRight.add(butXML);
		JPanel pnCenterControlLeft = new JPanel();

		
		JButton butInfo = new JButton(ACTION_COMMAND_TABLE_INFO_SEARCH);
		butInfo.setActionCommand(ACTION_COMMAND_TABLE_INFO_SEARCH);
		butInfo.addActionListener(this);

		pnCenterControlLeft.add(butInfo);


		pnCenterControl.add(pnCenterControlRight,BorderLayout.EAST);
		pnCenterControl.add(pnCenterControlLeft,BorderLayout.WEST);
		return pnCenterControl;
	}
	private JPanel buildInfo() {
		JPanel pnPortVesselCount = new JPanel(new BorderLayout());
		JPanel pnPortCount = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnPortCount.add(new JLabel("항구수 : "));
		txfPortCount = new JTextField(5);
		txfPortCount.addKeyListener(new MYKeyApater());
		txfVesselCount = new JTextField(5);
		txfVesselCount.addKeyListener(new MYKeyApater());
		pnPortCount.add(txfPortCount);

		pnPortCount.add(new JLabel("선박수 : "));		
		pnPortCount.add(txfVesselCount);


		JPanel pnVesselCount = new JPanel(new FlowLayout(FlowLayout.LEFT));

		Box bb = Box.createVerticalBox();
		bb.add(pnPortCount);	
		bb.add(pnVesselCount);

		JPanel pnIsUnder = new JPanel(new FlowLayout(FlowLayout.LEFT));

		cbxUnderPort = new JCheckBox("하위 항구");
		cbxUnderPortAction = new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();
				xlstableinfo.setUnderPort(box.isSelected());
			}
		};
		cbxUnderPort.addActionListener(cbxUnderPortAction);
		pnIsUnder.add(cbxUnderPort);
		bb.add(pnIsUnder);

		JPanel pnIsVoyage= new JPanel(new FlowLayout(FlowLayout.LEFT));

		cbxVoyage = new JCheckBox("Voyage 항목 생략 됨");
		voyageActioon = new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();
				xlstableinfo.setHasVoy(box.isSelected());

			}
		};
		cbxVoyage.addActionListener(voyageActioon);
		pnIsVoyage.add(cbxVoyage);
		bb.add(pnIsVoyage);

		JPanel pnKeyType= new JPanel(new FlowLayout(FlowLayout.LEFT));

		JComboBox cbbKey =new JComboBox();
		cbbKey.addItem("VESSEL");
		cbbKey.addItem("VOYAGE");
		cbbKey.addItem("BOTH");

		keyAction = new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox) e.getSource();


				if(box.getSelectedItem().equals("VESSEL"))
				{
					xlstableinfo.setTableType(TableLocation.VESSEL);
					pnDivider.setVisible(false);


				}else if (box.getSelectedItem().equals("VOYAGE")) 
				{
					xlstableinfo.setTableType(TableLocation.VOYAGE);
					pnDivider.setVisible(false);
				}else
				{
					pnDivider.setVisible(true);
					xlstableinfo.setTableType(TableLocation.BOTH);
				}
			}
		};
		cbbKey.addActionListener(keyAction);
		pnKeyType.add(new JLabel("KeyWord 형식"));
		pnKeyType.add(cbbKey);

		bb.add(pnKeyType);
		pnDivider = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnDivider.setVisible(false);
		cbxVesselVoyage = new JCheckBox("KeywordOption");
		cbxVesselVoyage.setVisible(false);


		lblDivider = new JLabel("구분자");
		cbxDivider = new JComboBox();


		dividerAction = new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
			}
		};
		cbxDivider.addActionListener(dividerAction);

		cbxDivider.addItem("/");
		cbxDivider.addItem("공백");
		cbxDivider.addItem("Enter");



		lblCount = new JLabel("위치");
		cbxCount = new JComboBox();
		cbxCount.addItem(1);
		cbxCount.addItem(2);
		cbxCount.addItem(3);
		countAction = new ActionListener()
		{

			public void actionPerformed(ActionEvent e) 
			{
				if(xlstableinfo==null)
					return;

			}
		};
		cbxCount.addActionListener(countAction);
		
		cbxCount.setSelectedItem(Integer.parseInt(propertis.getProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_COUNT).toString()));

		cbxDivider.setSelectedItem(propertis.getProperty(KSGPropertis.PROPERTIES_VESSEL_VOY_DIVIDER));

		pnDivider.add(lblDivider);
		
		pnDivider.add(cbxDivider);
		
		pnDivider.add(lblCount);
		
		pnDivider.add(cbxCount);

		bb.add(pnDivider);

		pnPortVesselCount.add(bb,BorderLayout.NORTH);

		JPanel pncontrl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton butApply = new JButton("적용");
		butApply.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				KSGXLSImportPanel.this.update(KSGModelManager.getInstance());
			}}
		);
		pncontrl.add(butApply);

		pnPortVesselCount.add(pncontrl,BorderLayout.SOUTH);
		return pnPortVesselCount;
	}
	private void createAndUpdatePN() {
		
		tblADV = new KSGXMLTable();	

		tblADV .setComponentPopupMenu(createPopup());
		
		tblADV.addMouseListener(new MouseAdapter(){


			public void mouseClicked(MouseEvent e) 
			{
				KSGTable table=(KSGTable) e.getSource();
				if(table.getSelectedRow()==-1)
					return;
				if(table.getSelectedColumn()==-1)
					return;
				if(table.getSelectedColumn()==0)
				{
					vesselAddItem.setVisible(true);
				}else
				{
					vesselAddItem.setVisible(false);
				}
			}
		}
		);
		txaADV = new JTextArea();
		litVessel =new VesselListComp(this);
//		tblPort = new PortTable(txaADV, this);
		tblPort = new SearchedPortTable();
		tblPort.setToolTipText("기본: 검정색, 신규 항구:노란색, 위치가  다른 항구:빨간색,");
		JPanel pnLeft = new JPanel();
		JSplitPane tpPortAndVessel = new JSplitPane();
		JPanel pnPort = new JPanel();
		JPanel pnVessel = new JPanel();
		pnCenter = new JPanel(new BorderLayout());

		JPanel pnCenterControl = buildControlPn();

		pnPort.setLayout(new BorderLayout());

		pnPort.add(new JScrollPane(tblPort),BorderLayout.CENTER);

		pnVessel.setLayout(new BorderLayout());
		
		pnVessel.add(litVessel,BorderLayout.CENTER);

		// 추후 제거
		buildInfo();

		pnPort.setPreferredSize(new Dimension(200,200));

		pnPort.setMinimumSize(new Dimension(200,200));
		
		//tpPortAndVessel.setPreferredSize(new Dimension(500,200));
		//tpPortAndVessel.setMinimumSize(new Dimension(500,200));
		
		tpPortAndVessel.setLeftComponent(pnPort);
		tpPortAndVessel.setRightComponent(new JScrollPane(pnVessel));		
		

		pnLeft.setLayout(new BorderLayout());
		pnLeft.add(tpPortAndVessel,BorderLayout.CENTER);

		cardLayout = new CardLayout();
		pnCard.setLayout( cardLayout);
		JPanel pnCardText = new JPanel();

		pnCardText.setLayout( new BorderLayout());
		pnCardText.add(new JScrollPane(txaADV),BorderLayout.CENTER);

		JPanel pnData = new JPanel(new BorderLayout());
		JTabbedPane tapPN = new JTabbedPane();
		jScrollPane = new JScrollPane(tblADV);
		pnData.add(jScrollPane,BorderLayout.CENTER);

		JPanel pnDataNorth = new JPanel(new FlowLayout(FlowLayout.LEADING));
		pnDataNorth.setPreferredSize(new Dimension(0,35));

		pnDataNorth.add(new JLabel("테이블 아이디 : "));

		lblTableID = new JLabel();
		lblPortCount = new JLabel();
		lblVesselCount = new JLabel();
		lblTableID.setPreferredSize(new Dimension(100,25));
		lblPortCount.setPreferredSize(new Dimension(50,25));
		lblVesselCount.setPreferredSize(new Dimension(100,25));

		pnDataNorth.add(lblTableID);
		pnDataNorth.add(new JLabel("항구 수 : "));
		pnDataNorth.add(lblPortCount);
		pnDataNorth.add(new JLabel("선박 수 : "));
		pnDataNorth.add(lblVesselCount);

		pnData.add(pnDataNorth,BorderLayout.NORTH);

		Box pnTableOption = new Box(BoxLayout.Y_AXIS);
		txfPort = new JTextField(8);
		txfPort.setHorizontalAlignment(JTextField.RIGHT);
		txfPage = new JTextField(6);
		txfPage.setHorizontalAlignment(JTextField.RIGHT);
		txfPage.setEditable(false);
		txfVessel = new JTextField(8);
		txfVessel.setHorizontalAlignment(JTextField.RIGHT);
		txfOther = new JTextField(8);
		txfOther.setHorizontalAlignment(JTextField.RIGHT);

		JPanel pnInfo = new JPanel();
		pnInfo.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnTableOption.add(addForm("페이지 : ", txfPage));
		pnInfo.add(addForm("항구수: ", txfPort));
		pnInfo.add(addForm("선박수: ", txfVessel));
		pnInfo.add(addForm("기타: ", txfOther));
		pnTableOption.add(pnInfo);
		tapPN.addTab("광고정보",pnData);

		tapPN.setFont(defaultFont);

		pnCard.add(pnData,TABLE);
		pnCard.add(pnCardText,TEXT);

		pnCenter.add(pnCenterControl,BorderLayout.SOUTH);
		pnCenter.add(pnCard,BorderLayout.CENTER);

		tblADV.setName("adv");
		pnLeft.setPreferredSize(new Dimension(350,200));
		pnLeft.setMinimumSize(new Dimension(350,200));

		
		pnMain = new JSplitPane();
		pnMain.setLeftComponent(pnLeft);
		pnMain.setRightComponent(pnCenter);
		this.setLayout(new BorderLayout());
		this.add(pnMain);
	}


	public JPopupMenu createPopup()
	{
		JPopupMenu popupMenu = new JPopupMenu();
		JCheckBoxMenuItem boxMenuItem = new JCheckBoxMenuItem("Scroll 표시",false);
		boxMenuItem.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				JCheckBoxMenuItem m =(JCheckBoxMenuItem) arg0.getSource();
				if(m.isSelected())
				{
					tblADV.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				}else
				{
					tblADV.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				}
			}
		});

		vesselAddItem = new JMenuItem("선박명 추가");
		vesselAddItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int row=tblADV.getSelectedRow();
				if(row==-1)
					return;
				int col = tblADV.getSelectedColumn();
				if(col>0)
					return;


				SearchVesselDialog dialog = new SearchVesselDialog(tblADV.getValueAt(row, col).toString());
				dialog.createAndUpdateUI();
				if(dialog.OPTION==SearchVesselDialog.OK_OPTION)
				{
					DefaultListModel model=(DefaultListModel) litVessel.getModel();
					model.setElementAt(dialog.info, row);
					updateVesseFulllName(row, dialog.info.vesselName);


				}
			}});
		JMenuItem itemTableInfo = new JMenuItem("테이블 정보");
		itemTableInfo.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				TableInfoDialog dialog = new TableInfoDialog(KSGXLSImportPanel.this);
				dialog.createAndUpdateUI();
			}});
		popupMenu.add(boxMenuItem);
		popupMenu.add(vesselAddItem);
		popupMenu.add(itemTableInfo);
		return popupMenu;
	}
	public int getIndex() {
		return index;
	}
	public List<HashMap<String,Object>> getPortList() {

		return tblPort.getPortList();
	}
	public Vector getNullVesselList() {

		return litVessel.getNullVesselList();
	}
	public KSGTable getTable() {
		return tblADV;
	}

	public String getTable_id()
	{
		return xlstableinfo.getTable_id();
	}
	public int getTableIndex()
	{
		return this.tableIndex;
	}

	public String getTitle()
	{
		if(xlstableinfo!=null)
		{
			return this.getTableIndex()+1+"번 테이블 : 	"+xlstableinfo.getTableInfo().getTitle();	
		}else
		{
			return this.getTableIndex()+"번 테이블";
		}

	}
	private String getXLSData(KSGModelManager manager) {
		Vector advDataList =manager.getTempXLSTableInfoList();

		if(advDataList==null)
			return null;
		if(advDataList.size()==0)
			return null;

		xlstableinfo = (XLSTableInfo)advDataList.get(this.getTableIndex());

		return "";
	}
	public String getTableXMLInfo()
	{
		return tblADV.getXMLModel();
	}
	
	public XLSTableInfo getXlstableinfo() {
		return xlstableinfo;
	}
	public void setTableIndex(int index)
	{
		this.tableIndex= index;
		tblADV.setTableIndex(index);
	}

	public void update(KSGModelManager manager) {
		
		logger.info("start:"+this.getTableIndex());
		
		if(manager.selectedInput.equals("File"))
		{
			Vector advDataList =manager.getTempXLSTableInfoList();

			if(advDataList==null)
				return ;
			if(advDataList.size()==0)
				return ;

			xlstableinfo = (XLSTableInfo)advDataList.get(this.getTableIndex());
			
			data = xlstableinfo.getTableXMLInfo();
			
		}

		if(data==null)
		{
			throw new RuntimeException(this.getTableIndex()+"'s table data is null");
		}		
		
		tableInfo = xlstableinfo.getTableInfo();
		
		lblTableID.setText(tableInfo.getTable_id());

		lblPortCount.setText(String.valueOf(tableInfo.getPort_col()));
		
		lblVesselCount.setText(String.valueOf(tableInfo.getVsl_row()));
		
		txfPortCount.setText(String.valueOf(tableInfo.getPort_col()));
		
		txfVesselCount.setText(String.valueOf(tableInfo.getVsl_row()));

		tblADV.setRootElement(xlstableinfo.getRootElement());

		tblADV.update(KSGModelManager.getInstance());


		litVessel.setModel(xlstableinfo.getVesselElement());
		
		if(litVessel.getVesselSize()!=tableInfo.getVsl_row())
		{
			lblVesselCount.setText(lblVesselCount.getText()+"(확인 필요)");
		}			
		
		tblPort.setModel(xlstableinfo.getPortElement(), this.getTable_id());
//		tblPort.setModel(xlstableinfo.getPortElement());

		this.table_id = xlstableinfo.getTable_id();
		
		try {
			tableProperty= KSGPropertyManager.getInstance().getKSGTableProperty(table_id);
		} catch (SQLException e) {

			logger.error("error:"+e.getErrorCode()+",insert tableproperty");
			Table_Property property = new Table_Property();
			property.setVoyage(0);
			property.setCompany_abbr(tableInfo.getCompany_abbr());
			property.setPage(tableInfo.getPage());
			property.setVesselvoycount(1);
			property.setTable_id(this.table_id);
			property.setVesselvoydivider("/");
			property.setUnder_port(1);
			property.setTable_type(TableLocation.VESSEL);

			try {
				KSGPropertyManager.getInstance().insert(property);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error : "+e.getMessage()+",code:"+e.getErrorCode());
				e1.printStackTrace();
			}
			tableProperty=property;
		}
		logger.info("end");
	}

	/**
	 * @param row
	 * @param vesselName
	 */
	public void updateVesselName(int row, String vesselName) {
		tblADV.updateVesselName(row, vesselName);
	}
	/**
	 * @param row
	 * @param vesselName
	 */
	public void updateVesseFulllName(int row, String vesselName) {
		tblADV.updateVesseFulllName(row, vesselName);
		
	}
	
	public void addMouseWheelListener(MouseWheelListener listener)	
	{
		super.addMouseWheelListener(listener);
		pnCenter.addMouseWheelListener(listener);
		tblADV.addMouseWheelListener(listener);
		jScrollPane.addMouseWheelListener(listener);	
		
	}
	

}

