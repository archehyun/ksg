package com.ksg.workbench.adv.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertyManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Property;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.KSGCheckBox;
import com.ksg.view.comp.KSGRadioButton;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.adv.KSGXLSImportPanel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**
 * 엑섹 정보 입력시 조회된 테이블에 대한 정보 표시 
 * @author archehyun
 *
 */
public class TableInfoDialog extends KSGDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String table_id;
	private JTextField txfPortCount;
	private JTextField txfVesselCount;
	private JLabel lblVesselCount;
	private JLabel lblPortCount;
	private KSGCheckBox cbxUnderPort,cbxVoyage;
	private JComboBox cbxDivider;
	private JComboBox cbxCount;
	private JLabel lblCount;
	
	private String port_type[]={"구분없음","슬래쉬(/)","도트(.)","괄호(())"};
	private JComboBox cbbKey;
	private ButtonGroup bg;
	private KSGRadioButton rbGroups[];
	KSGXLSImportPanel base;
	private ShippersTable shippersTable;
	private Table_Property table_Property;
	private KSGCheckBox cbxETD_ETA;
	private JTextField txfInboundIn;
	private JTextField txfInboundOut;
	private JTextField txfOutboundIn;
	private JTextField txfOutboundOut;
	public TableInfoDialog(KSGXLSImportPanel base) {

		this.base=base;

		this.table_id =base.getTable_id();

		tableService = new TableServiceImpl();

		this.addComponentListener(this);




	}

	public void createAndUpdateUI() {
		
		
		this.setModal(true);
		
		this.setTitle("테이블 정보 및 옵션");		

		getContentPane().add(buildCenter());
		
		
		getContentPane().add(buildControl(),BorderLayout.SOUTH);
	

		ViewUtil.center(this,true);
		
		
		this.setResizable(false);

		setVisible(true);

	}
	
	private KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		Box pnList =Box.createVerticalBox();

		KSGPanel pn0 = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label0 = new JLabel("테이블 아이디");
		JTextField txfTable_id = new JTextField(15);
		txfTable_id.setEditable(false);
		pn0.add(label0);
		pn0.add(txfTable_id);
		txfTable_id.setText(table_id);

		Box pnTableInfoBack= Box.createVerticalBox();
		KSGPanel pnVesselAndPortCount = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		pnTableInfoBack.setBorder(BorderFactory.createTitledBorder("테이블 정보"));
		lblPortCount = new JLabel("항구수");
		txfPortCount = new JTextField(3);						
		lblVesselCount = new JLabel("선박수");
		txfVesselCount = new JTextField(3);


		pnVesselAndPortCount.add(lblPortCount);
		pnVesselAndPortCount.add(txfPortCount);
		pnVesselAndPortCount.add(lblVesselCount);
		pnVesselAndPortCount.add(txfVesselCount);

		KSGPanel pnInboundIn = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		KSGPanel pnInboundOut = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		KSGPanel pnOutboundIn = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		KSGPanel pnOutboundOut = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		txfInboundIn = new JTextField(20);
		txfInboundOut = new JTextField(20);
		txfOutboundIn = new JTextField(20);
		txfOutboundOut = new JTextField(20);
		JLabel lblInboundIn = new JLabel("인바운드 국내항   ");
		pnInboundIn.add(lblInboundIn);
		pnInboundIn.add(txfInboundIn);
		JLabel lblInboundOut = new JLabel("인바운드 외국항   ");
		pnInboundOut.add(lblInboundOut);
		pnInboundOut.add(txfInboundOut);

		JLabel lblOutboundIn = new JLabel("아웃바운드 국내항");
		pnOutboundIn.add(lblOutboundIn);
		pnOutboundIn.add(txfOutboundIn);
		JLabel lblOutboundOut = new JLabel("아웃바운드 외국항");
		pnOutboundOut.add(lblOutboundOut);
		pnOutboundOut.add(txfOutboundOut);

		pnTableInfoBack.add(pnVesselAndPortCount);


		pnTableInfoBack.add(pnInboundIn);

		pnTableInfoBack.add(pnInboundOut);

		pnTableInfoBack.add(pnOutboundIn);

		pnTableInfoBack.add(pnOutboundOut);


		KSGPanel pnTableOption = new KSGPanel(new GridLayout(0,1));
		pnTableOption.setBorder(BorderFactory.createTitledBorder("테이블 옵션"));

		cbxUnderPort = new KSGCheckBox("하위 항구 존재");
		cbxVoyage = new KSGCheckBox("Voyage 항목 생략 됨");
		cbxETD_ETA = new KSGCheckBox("ETA/ETD 적용");
		cbxDivider = new JComboBox();
		cbxDivider.setPreferredSize(new Dimension(80,20));


		cbbKey = new JComboBox();
		cbbKey.addItem("VESSEL");
		cbbKey.addItem("VOYAGE");
		cbbKey.addItem("BOTH");

		cbbKey.addItemListener(new ItemListener(){


			public void itemStateChanged(ItemEvent e) {
				String item=(String) e.getItem();
				if(item.equals("BOTH"))
				{
					cbxDivider.setVisible(true);
					lblCount.setVisible(true);
					cbxCount.setVisible(true);
				}else
				{
					cbxDivider.setVisible(false);
					lblCount.setVisible(false);
					cbxCount.setVisible(false);
				}

			}});			

		cbxDivider.addItem("/");
		cbxDivider.addItem("공백");
		cbxDivider.addItem("Enter");



		lblCount = new JLabel("위치");
		lblCount.setVisible(false);
		cbxCount = new JComboBox();
		cbxCount.setVisible(false);
		cbxCount.setPreferredSize(new Dimension(80,20));
		cbxCount.addItem(1);
		cbxCount.addItem(2);
		cbxCount.addItem(3);

		KSGPanel pnUnderPort = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		KSGPanel pnDivider= new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		KSGPanel pnKeyword= new KSGPanel(new FlowLayout(FlowLayout.LEFT));		

		pnUnderPort.add(cbxUnderPort);
		pnUnderPort.add(cbxVoyage);
		pnUnderPort.add(cbxETD_ETA);
		JLabel label = new JLabel("구분자");
		label.setPreferredSize(new Dimension(100, 20));
		pnDivider.add(label);

		label.setVisible(false);
		pnDivider.add(cbxDivider);
		pnDivider.add(lblCount);
		pnDivider.add(cbxCount);

		JLabel label2 = new JLabel("KeyWord 형식");
		label2.setPreferredSize(new Dimension(100, 20));
		pnKeyword.add(label2);
		pnKeyword.add(cbbKey);


		pnTableOption.add(pnUnderPort);

		pnTableOption.add(pnKeyword);

		pnTableOption.add(pnDivider);

		JLabel lblPortdivider = new JLabel("항구 구분");

		pnTableOption.add(lblPortdivider);

		KSGPanel pnPortOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		bg = new ButtonGroup();
		Vector butList = new Vector();
		rbGroups = new KSGRadioButton[port_type.length];
		for(int i=0;i<port_type.length;i++)
		{
			rbGroups[i] = new KSGRadioButton(port_type[i]);
			pnPortOption.add(rbGroups[i]);
			bg.add(rbGroups[i]);
		}

		pnTableOption.add(pnPortOption);

		pnList.add(pn0);
		pnList.add(pnTableInfoBack);
		pnList.add(pnTableOption);

		pnMain.add(pnList);

		pnMain.add(pnList);
		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return pnMain;
	}
	
	private KSGPanel buildControl()
	{
		KSGPanel pnControl =new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butApply = new JButton("적용(A)");
		butApply.setActionCommand("적용");
		butApply.setMnemonic(KeyEvent.VK_A);
		butApply.addActionListener(this);
		pnControl.add(butApply);

		JButton butClose = new JButton("취소(C)");
		butClose.setActionCommand("취소");
		butClose.setMnemonic(KeyEvent.VK_C);
		butClose.addActionListener(this);
		pnControl.add(butClose);
		return pnControl;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("적용"))
		{

			Table_Property property = new Table_Property();
			property.setTable_id(table_id);
			property.setTable_type(cbbKey.getSelectedIndex()+1);


			if(cbxVoyage.isSelected())
			{
				property.setVoyage(1);
			}else
			{
				property.setVoyage(0);
			}
			if(cbxUnderPort.isSelected())
			{
				property.setUnder_port(1);
			}else
			{
				property.setUnder_port(0);
			}
			if(cbxETD_ETA.isSelected())
			{
				property.setEta(1);
			}else
			{
				property.setEta(0);
			}

			if(rbGroups[0].isSelected())
			{
				property.setPort_type(0);

			}else if(rbGroups[1].isSelected())
			{
				property.setPort_type(1);
			}
			else if(rbGroups[2].isSelected())
			{
				property.setPort_type(2);
			}
			else if(rbGroups[3].isSelected())
			{
				property.setPort_type(3);
			}



			if(cbxCount.getSelectedItem()!=null)			
				property.setVesselvoycount((Integer)cbxCount.getSelectedItem());

			int d=cbxDivider.getSelectedIndex();

			switch (d) {
			case 0:
				property.setVesselvoydivider("/");
				break;
			case 1:
				property.setVesselvoydivider(" ");
				break;
			case 2:
				property.setVesselvoydivider("\n");
				break;


			default:
				break;
			}

			try 
			{
				ShippersTable op =tableService.getTableById(table_id);

				ShippersTable table = new ShippersTable();

				table.setTable_id(this.table_id);

				table.setPort_col(Integer.valueOf(txfPortCount.getText()));

				table.setVsl_row(Integer.valueOf(txfVesselCount.getText()));

				table.setGubun(op.getGubun());

				table.setIn_port(txfInboundIn.getText().equals("")?" ":txfInboundIn.getText());
				table.setIn_to_port(txfInboundOut.getText().equals("")?" ":txfInboundOut.getText());
				table.setOut_port(txfOutboundIn.getText().equals("")?" ":txfOutboundIn.getText());
				table.setOut_to_port(txfOutboundIn.getText().equals("")?" ":txfOutboundOut.getText());

				tableService.update(table);
				tableService.updateTableProperty(property);

				KSGPropertyManager.getInstance().init();

				base.update(KSGModelManager.getInstance());
				base.updateUI();
				setVisible(false);
				dispose();


			} catch (SQLException e1) 
			{
				e1.printStackTrace();
			}
		}
		else if(command.equals("취소"))
		{
			setVisible(false);
			dispose();
			//base.update(KSGModelManager.getInstance());

		}

	}

	@Override
	public void componentShown(ComponentEvent e) {

		try{
			shippersTable = tableService.getTableById(table_id);

			table_Property = tableService.getTableProperty(table_id);
			

			// 값 할당

			if(table_Property.getVoyage()==0)

			{
				cbxVoyage.setSelected(false);
			}
			else
			{
				cbxVoyage.setSelected(true);
			}
			if(table_Property.getEta()==0)

			{
				cbxETD_ETA.setSelected(false);
			}
			else
			{
				cbxETD_ETA.setSelected(true);
			}
			txfPortCount.setText(String.valueOf(shippersTable.getPort_col()));
			txfVesselCount.setText(String.valueOf(shippersTable.getVsl_row()));

			if(table_Property.getVesselvoydivider().equals("/"))
			{
				cbxDivider.setSelectedIndex(0);
			}else if(table_Property.getVesselvoydivider().equals(" "))
			{
				cbxDivider.setSelectedIndex(1);
			}
			else if(table_Property.getVesselvoydivider().equals("\n"))
			{
				cbxDivider.setSelectedIndex(2);
			}

			if(table_Property.getUnder_port()==0)
			{
				cbxUnderPort.setSelected(false);	
			}else
			{
				cbxUnderPort.setSelected(true);
			}

			cbbKey.setSelectedIndex(table_Property.getTable_type()-1);

			if(cbbKey.getSelectedIndex()==2)
			{
				cbxDivider.setVisible(true);
			}else
			{
				cbxDivider.setVisible(false);
			}

			cbxCount.setSelectedIndex(table_Property.getVesselvoycount()-1);
			txfInboundIn.setText(shippersTable.getIn_port().trim());
			txfInboundOut.setText(shippersTable.getIn_to_port().trim());
			txfOutboundIn.setText(shippersTable.getOut_port().trim());
			txfOutboundOut.setText(shippersTable.getOut_to_port().trim());


			bg.setSelected(rbGroups[table_Property.getPort_type()].getModel(),true);		

			logger.info("tableinfo : "+table_id+","+table_Property);
		}
		catch (Exception ee) 
		{
			ee.printStackTrace();
		}
	}

}
