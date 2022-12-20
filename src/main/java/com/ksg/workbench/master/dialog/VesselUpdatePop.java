package com.ksg.workbench.base.vessel.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.workbench.base.code.comp.PnCommonCode;
import com.ksg.workbench.base.dialog.BasePop;


/**

  * @FileName : VesselUpdatePop.java

  * @Date : 2021. 3. 19. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� :

  */
@SuppressWarnings("serial")
public class VesselUpdatePop extends BasePop implements ActionListener{

	VesselServiceImpl vesselService;

	JButton butOk;

	JButton butCancel;

	JTextField txfVesselName;

	JTextField txfMMSI;

	JTextField txfCompany;
	
	JTextField txfVesselUse;
	
	JTextField txfInputDate;
	
	JComboBox cbxType;

	private PnCommonCode pnCommonCode;

	public VesselUpdatePop() {
		vesselService = new VesselServiceImpl();
		this.getContentPane().add(createCenter());

		this.getContentPane().add(createNorth(),BorderLayout.SOUTH);
		this.setTitle("�������� ���� ����");

	}

	public VesselUpdatePop(HashMap<String, Object> item) {

		this();
		txfVesselName.setText((String)item.get("CD_ID"));
		txfMMSI.setText((String)item.get("CD_NM"));
		txfCompany.setText((String)item.get("CD_ENG"));

	}

	public JPanel createCenter()
	{
		JPanel pnMain = new JPanel();

		BoxLayout boxLayout = new BoxLayout(pnMain, BoxLayout.Y_AXIS);

		pnMain.setLayout(boxLayout);

		txfVesselName = new JTextField(15);
		
		txfMMSI = new JTextField(15);
		
		txfCompany = new JTextField(15);

		txfVesselName.setEditable(false);

		txfCompany.setEditable(false);

		pnMain.add(addComp("���ڸ�", txfVesselName));
		pnMain.add(addComp("�ڵ��", txfMMSI));
		pnMain.add(addComp("��ǥ�����", txfCompany));

		return pnMain;
	}

	private JPanel addComp(String title, JComponent comp)
	{
		JPanel pnMain = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lblTitle = new JLabel(title,JLabel.RIGHT);

		lblTitle.setPreferredSize(new Dimension(100,25));

		pnMain.add(lblTitle);
		pnMain.add(comp);
		return pnMain;
	}

	private JPanel createNorth()
	{

		JPanel pnMain = new JPanel(new BorderLayout());

		JPanel pnCenter = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		butOk = new JButton("Ȯ��");

		butCancel = new JButton("���");

		butOk.addActionListener(this);
		butCancel.addActionListener(this);

		pnCenter.add(butOk);

		pnCenter.add(butCancel);

		pnMain.add(pnCenter,BorderLayout.LINE_END);

		return pnMain;


	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("���"))
		{
			
			result = BasePop.CANCEL;
			close();
		}
		else if(command.equals("Ȯ��"))
		{

			String codeID = txfVesselName.getText();
			String codeNM = txfMMSI.getText();
			String codeENG = txfCompany.getText();

			if(codeNM.equals("")) {
				JOptionPane.showMessageDialog(this, "�׸��� �Է��Ͻʽÿ�");
				return;
			}


			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("CD_NM", codeNM);
			param.put("CD_ID", codeID);


			try {
				vesselService.updateDetail(param);
				result = BasePop.OK;
				pnCommonCode.fnSearch();
				close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				result = BasePop.CANCEL;
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, e1.getMessage());
			}
		}

	}

	public void showPop(PnCommonCode pnCommonCode) {
		super.showPop();
		this.pnCommonCode=pnCommonCode;
		
	}


}
