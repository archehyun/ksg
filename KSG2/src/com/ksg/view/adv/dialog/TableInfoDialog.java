package com.ksg.view.adv.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.ksg.dao.DAOManager;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Property;
import com.ksg.model.KSGModelManager;
import com.ksg.view.adv.KSGXLSImportPanel;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.util.ViewUtil;

/**
 * ���� ���� �Է½� ��ȸ�� ���̺� ���� ���� ǥ�� 
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
	private JCheckBox cbxUnderPort;
	private JComboBox cbxDivider;
	private JComboBox cbxCount;
	private JLabel lblCount;
	private JCheckBox cbxVoyage;
	private String port_type[]={"���о���","������(/)","��Ʈ(.)","��ȣ(())"};
	private JComboBox cbbKey;
	private ButtonGroup bg;
	private JRadioButton rbGroups[];
	KSGXLSImportPanel base;
	private ShippersTable shippersTable;
	private Table_Property table_Property;
	private JCheckBox cbxETD_ETA;
	private JTextField txfInboundIn;
	private JTextField txfInboundOut;
	private JTextField txfOutboundIn;
	private JTextField txfOutboundOut;
	public TableInfoDialog(KSGXLSImportPanel base) {
		try{
			this.base=base;
			
			this.table_id =base.getTable_id();
			
			tableService = DAOManager.getInstance().createTableService();
			
			shippersTable = tableService.getTableById(table_id);
			
			table_Property = tableService.getTableProperty(table_id);
			
			logger.info("tableinfo : "+table_id+","+table_Property);
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle("���̺� ���� �� �ɼ�");

		JPanel pnMain = new JPanel(new BorderLayout());

		Box pnList =Box.createVerticalBox();

		JPanel pn0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label0 = new JLabel("���̺� ���̵�");
		JTextField txfTable_id = new JTextField(15);
		txfTable_id.setEditable(false);
		pn0.add(label0);
		pn0.add(txfTable_id);
		txfTable_id.setText(table_id);

		Box pnTableInfoBack= Box.createVerticalBox();
		JPanel pnVesselAndPortCount = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnTableInfoBack.setBorder(BorderFactory.createTitledBorder("���̺� ����"));
		lblPortCount = new JLabel("�ױ���");
		txfPortCount = new JTextField(3);						
		lblVesselCount = new JLabel("���ڼ�");
		txfVesselCount = new JTextField(3);


		pnVesselAndPortCount.add(lblPortCount);
		pnVesselAndPortCount.add(txfPortCount);
		pnVesselAndPortCount.add(lblVesselCount);
		pnVesselAndPortCount.add(txfVesselCount);

		JPanel pnInboundIn = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnInboundOut = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnOutboundIn = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnOutboundOut = new JPanel(new FlowLayout(FlowLayout.LEFT));

		txfInboundIn = new JTextField(20);
		txfInboundOut = new JTextField(20);
		txfOutboundIn = new JTextField(20);
		txfOutboundOut = new JTextField(20);
		JLabel lblInboundIn = new JLabel("�ιٿ�� ������   ");
		pnInboundIn.add(lblInboundIn);
		pnInboundIn.add(txfInboundIn);
		JLabel lblInboundOut = new JLabel("�ιٿ�� �ܱ���   ");
		pnInboundOut.add(lblInboundOut);
		pnInboundOut.add(txfInboundOut);

		JLabel lblOutboundIn = new JLabel("�ƿ��ٿ�� ������");
		pnOutboundIn.add(lblOutboundIn);
		pnOutboundIn.add(txfOutboundIn);
		JLabel lblOutboundOut = new JLabel("�ƿ��ٿ�� �ܱ���");
		pnOutboundOut.add(lblOutboundOut);
		pnOutboundOut.add(txfOutboundOut);

		pnTableInfoBack.add(pnVesselAndPortCount);


		pnTableInfoBack.add(pnInboundIn);
		
		pnTableInfoBack.add(pnInboundOut);
		
		pnTableInfoBack.add(pnOutboundIn);
		
		pnTableInfoBack.add(pnOutboundOut);


		JPanel pnTableOption = new JPanel(new GridLayout(0,1));
		pnTableOption.setBorder(BorderFactory.createTitledBorder("���̺� �ɼ�"));

		cbxUnderPort = new JCheckBox("���� �ױ� ����");
		cbxVoyage = new JCheckBox("Voyage �׸� ���� ��");
		cbxETD_ETA = new JCheckBox("ETA/ETD ����");
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
		cbxDivider.addItem("����");
		cbxDivider.addItem("Enter");



		lblCount = new JLabel("��ġ");
		lblCount.setVisible(false);
		cbxCount = new JComboBox();
		cbxCount.setVisible(false);
		cbxCount.setPreferredSize(new Dimension(80,20));
		cbxCount.addItem(1);
		cbxCount.addItem(2);
		cbxCount.addItem(3);

		JPanel pnUnderPort = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnDivider= new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnKeyword= new JPanel(new FlowLayout(FlowLayout.LEFT));		

		pnUnderPort.add(cbxUnderPort);
		pnUnderPort.add(cbxVoyage);
		pnUnderPort.add(cbxETD_ETA);
		JLabel label = new JLabel("������");
		label.setPreferredSize(new Dimension(100, 20));
		pnDivider.add(label);
		pnDivider.add(cbxDivider);
		pnDivider.add(lblCount);
		pnDivider.add(cbxCount);

		JLabel label2 = new JLabel("KeyWord ����");
		label2.setPreferredSize(new Dimension(100, 20));
		pnKeyword.add(label2);
		pnKeyword.add(cbbKey);


		pnTableOption.add(pnUnderPort);
		
		pnTableOption.add(pnKeyword);

		pnTableOption.add(pnDivider);

		JLabel label3 = new JLabel("�ױ� ����");
		pnTableOption.add(label3);

		JPanel pnPortOption = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bg = new ButtonGroup();
		Vector butList = new Vector();
		rbGroups = new JRadioButton[port_type.length];
		for(int i=0;i<port_type.length;i++)
		{
			rbGroups[i] = new JRadioButton(port_type[i]);
			pnPortOption.add(rbGroups[i]);
			bg.add(rbGroups[i]);
		}

		pnTableOption.add(pnPortOption);

		pnList.add(pn0);
		pnList.add(pnTableInfoBack);
		pnList.add(pnTableOption);

		pnMain.add(pnList);

		pnMain.add(pnList);


		JPanel pnControl =new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butApply = new JButton("����(A)");
		butApply.setActionCommand("����");
		butApply.setMnemonic(KeyEvent.VK_A);
		butApply.addActionListener(this);
		pnControl.add(butApply);

		JButton butClose = new JButton("�ݱ�(C)");
		butClose.setActionCommand("�ݱ�");
		butClose.setMnemonic(KeyEvent.VK_C);
		butClose.addActionListener(this);
		pnControl.add(butClose);

		getContentPane().add(pnMain);
		getContentPane().add(pnControl,BorderLayout.SOUTH);
		getContentPane().add(KSGDialog.createMargin(),BorderLayout.WEST);
		getContentPane().add(KSGDialog.createMargin(),BorderLayout.EAST);

		logger.debug(table_Property);

		// �� �Ҵ�

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

		ViewUtil.center(this,true);
		this.setResizable(false);

		setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("����"))
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

				tableService.updateTable(table);
				tableService.updateTableProperty(property);

				base.update(KSGModelManager.getInstance());
				base.updateUI();

			} catch (SQLException e1) 
			{
				e1.printStackTrace();
			}

		}
		else if(command.equals("�ݱ�"))
		{
			setVisible(false);
			dispose();
			base.update(KSGModelManager.getInstance());

		}

	}

}
