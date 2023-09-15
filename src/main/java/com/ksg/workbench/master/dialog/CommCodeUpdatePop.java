package com.ksg.workbench.master.dialog;

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

import com.dtp.api.control.CodeController;
import com.dtp.api.service.impl.CodeServiceImpl;
import com.ksg.common.model.CommandMap;
import com.ksg.workbench.master.comp.PnCommonCode;

/**

 * @FileName : CommcodeUpdatePop.java

 * @Date : 2021. 3. 19. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
@SuppressWarnings("serial")
public class CommCodeUpdatePop extends BaseInfoDialog implements ActionListener{

	private	CodeServiceImpl codeService;
	
	private	JButton butOk;
	
	private	JButton butCancel;
	
	private	JTextField txfCodeID;
	
	private	JTextField txfCodeNM;
	
	private	JTextField txfCodeENG;

	private PnCommonCode pnCommonCode;

	public CommCodeUpdatePop() {
		
		this.setController(new CodeController());
		
		codeService = new CodeServiceImpl();
		


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

		pnMain.add(addComp("코드ID", txfCodeID));
		pnMain.add(addComp("코드명", txfCodeNM));
		pnMain.add(addComp("코드영문명", txfCodeENG));

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

		butOk 		= new JButton("확인");

		butCancel 	= new JButton("취소");

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
		if(command.equals("취소"))
		{
			
			result = BasePop.CANCEL;
			close();
		}
		else if(command.equals("확인"))
		{

			String codeID = txfCodeID.getText();
			String codeNM = txfCodeNM.getText();
			String codeENG = txfCodeENG.getText();

			if(codeNM.equals("")) {
				JOptionPane.showMessageDialog(this, "항목을 입력하십시요");
				return;
			}


			CommandMap param = new CommandMap();
			
			param.put("CD_NM", codeNM);
			param.put("CD_ID", codeID);


//			try {
				
				callApi("updateCode",param);
//				codeService.updateCodeH(param);
				
				result = BasePop.OK;
				
				pnCommonCode.fnSearch();
				
				close();
				
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				result = BasePop.CANCEL;
//				e1.printStackTrace();
//				JOptionPane.showMessageDialog(this, e1.getMessage());
//			}
		}
	}

	public void showPop(PnCommonCode pnCommonCode) {
		this.pnCommonCode=pnCommonCode;
		
	}

	@Override
	public void createAndUpdateUI() {
		this.getContentPane().add(createCenter());

		this.getContentPane().add(createNorth(),BorderLayout.SOUTH);
		
		this.setTitle("코드 수정");
	}


}
