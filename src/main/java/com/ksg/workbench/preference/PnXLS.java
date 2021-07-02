package com.ksg.workbench.preference;

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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;

public class PnXLS extends JPanel implements ActionListener,PreferencePn{
	
	protected Logger 		logger = Logger.getLogger(this.getClass());
	private JComboBox cbbUnderPort;
	private JComboBox cboUpDown;
	private JCheckBox cbxDoubleLine;
	JCheckBox cbxUnderPort;
	private JCheckBox cbxVoy;
	private JCheckBox chxDoubleKeyword;
	private JCheckBox chxETAETD;
	private Font defaultfont;


	PreferenceDialog preferenceDialog;

	private KSGPropertis propertis = KSGPropertis.getIntance();
	BaseService baseService;
	private JCheckBox cbxVesselVoyage;
	private JCheckBox chxDivider;
	private JRadioButton radioButNoaml;
	private JRadioButton radioButSlash;
	private JRadioButton radioButGyu;
	private JRadioButton radioButDot;
	public PnXLS(final PreferenceDialog preferenceDialog) 
	{
		this.setName("엑셀입력옵션");
		this.preferenceDialog=preferenceDialog;
		initComponent();
		
		Box pnMain =Box.createVerticalBox();
		JPanel pnSub = new JPanel();
		pnSub.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		Box pnInfo = Box.createVerticalBox();
		
		JPanel pnInfo1 = new JPanel();
		pnInfo1.setBorder(BorderFactory.createTitledBorder("항구 지정"));
		pnInfo1.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		pnInfo1.add(new JLabel("적용안함 :  항구 형태가 일반적일 때"));		
		JPanel pnInfo2 = new JPanel();
		pnInfo2.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnInfo2.add(new JLabel("일괄적용 :  위에는 대표명이 있고 아래 셀에 항구명이 있을 때 "));
				
		
		JPanel pnInfo3 = new JPanel();
		pnInfo3.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnInfo3.add(new JLabel("부분적용 :  일반적인 형태와 혼합되어 있을 때"));
		pnInfo.add(pnInfo1);
		pnInfo.add(pnInfo2);
		pnInfo.add(pnInfo3);
		
		pnSub.add(new JLabel("하위항구: "));
		pnSub.add(cbbUnderPort);
		
		pnMain.add(pnInfo);
		pnMain.add(pnSub);
		
		
		pnMain.add(createComp(cbxDoubleLine));
		pnMain.add(createComp(cbxVoy));
		pnMain.add(createComp(cbxVesselVoyage));
		pnMain.add(createComp(chxDoubleKeyword));
		pnMain.add(createComp(chxETAETD));
		
		JPanel panel = new JPanel();
		panel.add(radioButNoaml);
		panel.add(radioButDot);
		panel.add(radioButSlash);
		panel.add(radioButGyu);
		pnMain.add(createComp(new JLabel("이중항구 등록시:")));
		pnMain.add(createComp(panel));
		
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(pnMain);
		
	}
	public Component createComp(Component comp)
	{
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnMain.add(comp);
		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		/*if(command.equals("일반"))
		{
			KSGPropertis.PROPERTIES_DIVIDER_COUNT=KSGPropertis.PROPERTIES_DIVIDER_NOMAL;
		}else if(command.equals("/"))
		{
			KSGPropertis.PROPERTIES_DIVIDER_COUNT=KSGPropertis.PROPERTIES_DIVIDER_SLASH;
			
		}else if(command.equals("항구명(항구명)"))
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
		baseService = new BaseServiceImpl();
		defaultfont = KSGModelManager.getInstance().defaultFont;
		cbxUnderPort = new JCheckBox();
		cbxVoy = new JCheckBox("Voyage가 생략 되어 있을 경우 선택");
		cbxDoubleLine = new JCheckBox("Keyword또는 항구가 아래,위 동일한 형태로 있을 경우 선택");
		cbxVesselVoyage = new JCheckBox("Vessel&Voyage 키워드가  혼재 되어 있을 경우 선택");
		
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
				if(cbbUnderPort.getSelectedItem().equals("적용안함"))
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
		cbbUnderPort.addItem("적용안함");
		cbbUnderPort.addItem("일괄적용");
		cbbUnderPort.addItem("부분적용");
		chxDoubleKeyword = new JCheckBox("이중 키워드");
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
		if(cbbUnderPort.getSelectedItem().equals("적용안함"))
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
		
		
		
		chxDivider = new JCheckBox("/ 구분 무시",KSGPropertis.PROPERTIES_DIVIDER);
		chxDivider.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				KSGPropertis.PROPERTIES_DIVIDER=chxDivider.isSelected();		
			}});
		
		ButtonGroup bg = new ButtonGroup();
		
		radioButNoaml = new JRadioButton("일반");
		radioButNoaml.addActionListener(this);
		radioButDot = new JRadioButton(",");
		radioButDot.addActionListener(this);
		radioButSlash = new JRadioButton("/");
		radioButSlash.addActionListener(this);
		radioButGyu = new JRadioButton("항구명(항구명)");
		radioButGyu.addActionListener(this);
		bg.add(radioButNoaml);
		bg.add(radioButDot);
		bg.add(radioButSlash);
		bg.add(radioButGyu);
		
		
		/*switch (KSGPropertis.PROPERTIES_DIVIDER_COUNT) {
		case KSGPropertis.PROPERTIES_DIVIDER_NOMAL:
			radioButNoaml.setSelected(true);
			break;
		case KSGPropertis.PROPERTIES_DIVIDER_SLASH:
			radioButSlash.setSelected(true);
			break;
		case KSGPropertis.PROPERTIES_DIVIDER_BRACKETS:
			radioButGyu.setSelected(true);
			break;

		default:
			break;
		}*/
	}
	
	public void saveAction() {
		logger.debug("xls saveAction:");
		
		propertis.setProperty(KSGPropertis.PROPERTIES_UNDERPORT, (String)cbbUnderPort.getSelectedItem());
		propertis.setProperty(KSGPropertis.PROPERTIES_VOY, String.valueOf(cbxVoy.isSelected()));
		propertis.setProperty(KSGPropertis.PROPERTIES_DOUBLEKEY, chxDoubleKeyword.isSelected()+"|"+cboUpDown.getSelectedItem());
		
		
	}
}
