package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.service.BaseService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.view.comp.KSGViewUtil;
import com.ksg.view.comp.panel.KSGPanel;

public class PnXLS extends PnOption{
	
	private JComboBox cbbUnderPort;
	
	private JComboBox cboUpDown;
	
	private JCheckBox cbxDoubleLine;
	
	JCheckBox cbxUnderPort;
	
	private JCheckBox cbxVoy;
	
	private JCheckBox chxDoubleKeyword;
	
	private JCheckBox chxETAETD;
	
	private Font defaultfont;

	private PreferenceDialog preferenceDialog;

	private KSGPropertis propertis = KSGPropertis.getIntance();
	
	private KSGViewUtil viewPropeties = KSGViewUtil.getInstance();
	
	BaseService baseService;
	
	private JCheckBox cbxVesselVoyage;
	private JCheckBox chxDivider;
	private JRadioButton radioButNoaml;
	private JRadioButton radioButSlash;
	private JRadioButton radioButGyu;
	private JRadioButton radioButDot;

	private JCheckBox cbxShowLeft;
	public PnXLS(PreferenceDialog preferenceDialog) 
	{
		super(preferenceDialog);
		
		this.setName("�����Է¿ɼ�");
		
		baseService = new BaseServiceImpl();
		
		defaultfont = KSGModelManager.getInstance().defaultFont;
		
		initComponent();
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.add(buildCenter());
		

		
	}
	
	private KSGPanel buildCenter()
	{
		Box pnBox =Box.createVerticalBox();
		
		KSGPanel pnSub = new KSGPanel();
		
		pnSub.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		Box pnInfo = Box.createVerticalBox();
		
		KSGPanel pnInfo1 = new KSGPanel();
		pnInfo1.setBorder(BorderFactory.createTitledBorder("�ױ� ����"));
		pnInfo1.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		pnInfo1.add(new JLabel("������� :  �ױ� ���°� �Ϲ����� ��"));		
		KSGPanel pnInfo2 = new KSGPanel();
		pnInfo2.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnInfo2.add(new JLabel("�ϰ����� :  ������ ��ǥ���� �ְ� �Ʒ� ���� �ױ����� ���� �� "));
				
		
		KSGPanel pnInfo3 = new KSGPanel();
		pnInfo3.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnInfo3.add(new JLabel("�κ����� :  �Ϲ����� ���¿� ȥ�յǾ� ���� ��"));
		pnInfo.add(pnInfo1);
		pnInfo.add(pnInfo2);
		pnInfo.add(pnInfo3);
		
		pnSub.add(new JLabel("�����ױ�: "));
		pnSub.add(cbbUnderPort);
		
		pnBox.add(pnInfo);
		pnBox.add(pnSub);
		
		
		pnBox.add(createComp(cbxDoubleLine));
		pnBox.add(createComp(cbxVoy));
		pnBox.add(createComp(cbxVesselVoyage));
		pnBox.add(createComp(chxDoubleKeyword));
		pnBox.add(createComp(chxETAETD));
		
		KSGPanel panel = new KSGPanel();
		panel.add(radioButNoaml);
		panel.add(radioButDot);
		panel.add(radioButSlash);
		panel.add(radioButGyu);
		pnBox.add(createComp(new JLabel("�����ױ� ��Ͻ�:")));
		pnBox.add(createComp(panel));
		
		KSGPanel pnShowLeft = new KSGPanel();
		
//		cbxShowLeft = new JCheckBox("���� �޴� ǥ��", true);
//		
//		pnBox.add(createComp(cbxShowLeft));
		
		
		KSGPanel pnMain=new KSGPanel(new BorderLayout());
		
		pnMain.add(pnBox);
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,15, 5,5));
		return pnMain;
	}
	public Component createComp(Component comp)
	{
		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnMain.add(comp);
		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		/*if(command.equals("�Ϲ�"))
		{
			KSGPropertis.PROPERTIES_DIVIDER_COUNT=KSGPropertis.PROPERTIES_DIVIDER_NOMAL;
		}else if(command.equals("/"))
		{
			KSGPropertis.PROPERTIES_DIVIDER_COUNT=KSGPropertis.PROPERTIES_DIVIDER_SLASH;
			
		}else if(command.equals("�ױ���(�ױ���)"))
		{
			KSGPropertis.PROPERTIES_DIVIDER_COUNT=KSGPropertis.PROPERTIES_DIVIDER_BRACKETS;	
		}
		else if(command.equals(","))
		{
			KSGPropertis.PROPERTIES_DIVIDER_COUNT=KSGPropertis.PROPERTIES_DIVIDER_DOT;	
		}*/
	}
	private void initComponent()
	{

		cbxUnderPort = new JCheckBox();
		
		cbxVoy = new JCheckBox("Voyage�� ���� �Ǿ� ���� ��� ����");
		
		cbxDoubleLine = new JCheckBox("Keyword�Ǵ� �ױ��� �Ʒ�,�� ������ ���·� ���� ��� ����");
		
		cbxVesselVoyage = new JCheckBox("Vessel&Voyage Ű���尡  ȥ�� �Ǿ� ���� ��� ����");
		
		cbxVesselVoyage.setSelected(KSGModelManager.getInstance().seperatedVesselvoy);
		
		cbxVesselVoyage.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				KSGModelManager.getInstance().seperatedVesselvoy=cbxVesselVoyage.isSelected();
			}
		});
		cbxDoubleLine.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				KSGPropertis.DOUBLE_LINE=cbxDoubleLine.isSelected();
			}
		});
		cbbUnderPort = new JComboBox();
		cbbUnderPort.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if(cbbUnderPort.getSelectedItem().equals("�������"))
				{
					cbxDoubleLine.setEnabled(true);
					cbxDoubleLine.setSelected(KSGPropertis.DOUBLE_LINE);
				}else
				{
					cbxDoubleLine.setEnabled(false);
					cbxDoubleLine.setSelected(false);
				}
			}
		});
		cbbUnderPort.addItem("�������");
		cbbUnderPort.addItem("�ϰ�����");
		cbbUnderPort.addItem("�κ�����");
		chxDoubleKeyword = new JCheckBox("���� Ű����");
		chxDoubleKeyword.setEnabled(false);
		chxDoubleKeyword.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JCheckBox box=(JCheckBox) e.getSource();
				if(box.isSelected())
				{
					cboUpDown.setEnabled(true);
				}else
				{
					cboUpDown.setEnabled(false);
				}
			}
		});
		cboUpDown = new JComboBox();
		cboUpDown.setEnabled(false);


		cboUpDown.addItem("Up");
		cboUpDown.addItem("Down");
		
		cbbUnderPort.setSelectedItem(propertis.getProperty(KSGPropertis.PROPERTIES_UNDERPORT));
		if(cbbUnderPort.getSelectedItem().equals("�������"))
		{
			cbxDoubleLine.setEnabled(true);
			cbxDoubleLine.setSelected(KSGPropertis.DOUBLE_LINE);
		}else
		{
			cbxDoubleLine.setEnabled(false);
			cbxDoubleLine.setSelected(false);
		}
		boolean voy = Boolean.parseBoolean(propertis.getProperty(KSGPropertis.PROPERTIES_VOY));
		cbxVoy.setSelected(voy);
		chxETAETD = new JCheckBox("ETA/ETD",KSGPropertis.ETA_ETD);
		chxETAETD.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				KSGPropertis.ETA_ETD=chxETAETD.isSelected();		
			}});
		
		
		
		chxDivider = new JCheckBox("/ ���� ����",KSGPropertis.PROPERTIES_DIVIDER);
		chxDivider.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				KSGPropertis.PROPERTIES_DIVIDER=chxDivider.isSelected();		
			}});
		
		ButtonGroup bg = new ButtonGroup();
		
		radioButNoaml = new JRadioButton("�Ϲ�");
		radioButNoaml.addActionListener(this);
		radioButDot = new JRadioButton(",");
		radioButDot.addActionListener(this);
		radioButSlash = new JRadioButton("/");
		radioButSlash.addActionListener(this);
		radioButGyu = new JRadioButton("�ױ���(�ױ���)");
		radioButGyu.addActionListener(this);
		bg.add(radioButNoaml);
		bg.add(radioButDot);
		bg.add(radioButSlash);
		bg.add(radioButGyu);
		
		cbxVoy.setBackground(Color.white);
		cbxDoubleLine.setBackground(Color.white);
		cbxVesselVoyage.setBackground(Color.white);
		
		chxDoubleKeyword.setBackground(Color.white);
		chxETAETD.setBackground(Color.white);
		
		radioButNoaml.setBackground(Color.white);
		radioButDot.setBackground(Color.white);
		radioButSlash.setBackground(Color.white);
		radioButGyu.setBackground(Color.white);

	}
	
	public void saveAction() {
		
		logger.debug("xls saveAction:");
		
//		propertis.setProperty(KSGPropertis.PROPERTIES_UNDERPORT, (String)cbbUnderPort.getSelectedItem());
//		
//		propertis.setProperty(KSGPropertis.PROPERTIES_VOY, String.valueOf(cbxVoy.isSelected()));
//		
//		propertis.setProperty(KSGPropertis.PROPERTIES_DOUBLEKEY, chxDoubleKeyword.isSelected()+"|"+cboUpDown.getSelectedItem());
		
//		viewPropeties.setProperty("view.showleft",  Boolean.toString( cbxShowLeft.isSelected()));
//
//		viewPropeties.store();
		
	}
}
