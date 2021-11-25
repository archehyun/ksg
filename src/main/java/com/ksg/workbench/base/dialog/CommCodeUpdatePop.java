package com.ksg.workbench.base.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.service.CodeService;
import com.ksg.workbench.base.comp.PnCommonCode;

/**

 * @FileName : CommcodeUpdatePop.java

 * @Date : 2021. 3. 19. 

 * @�ۼ��� : ��â��

 * @�����̷� :

 * @���α׷� ���� :

 */
@SuppressWarnings("serial")
public class CommCodeUpdatePop extends BasePop implements ActionListener{

	CodeService codeService;

	JButton butOk;

	JButton butCancel;

	JTextField txfCodeID;

	JTextField txfCodeNM;

	JTextField txfCodeENG;

	private PnCommonCode pnCommonCode;

	public CommCodeUpdatePop() {
		codeService = new CodeService();
		this.getContentPane().add(createCenter());

		this.getContentPane().add(createNorth(),BorderLayout.SOUTH);
		this.setTitle("�ڵ� ����");

	}

	public CommCodeUpdatePop(HashMap<String, Object> item) {

		this();
		txfCodeID.setText((String)item.get("CD_ID"));
		txfCodeNM.setText((String)item.get("CD_NM"));
		txfCodeENG.setText((String)item.get("CD_ENG"));

	}

	public JPanel createCenter()
	{
		JPanel pnMain = new JPanel();

		BoxLayout boxLayout = new BoxLayout(pnMain, BoxLayout.Y_AXIS);

		pnMain.setLayout(boxLayout);

		txfCodeID = new JTextField(15);
		txfCodeNM = new JTextField(15);
		txfCodeENG = new JTextField(15);

		txfCodeID.setEditable(false);

		txfCodeENG.setEditable(false);

		pnMain.add(addComp("�ڵ�ID", txfCodeID));
		pnMain.add(addComp("�ڵ��", txfCodeNM));
		pnMain.add(addComp("�ڵ念����", txfCodeENG));

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

			String codeID = txfCodeID.getText();
			String codeNM = txfCodeNM.getText();
			String codeENG = txfCodeENG.getText();

			if(codeNM.equals("")) {
				JOptionPane.showMessageDialog(this, "�׸��� �Է��Ͻʽÿ�");
				return;
			}


			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("CD_NM", codeNM);
			param.put("CD_ID", codeID);


			try {
				codeService.updateCodeH(param);
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
