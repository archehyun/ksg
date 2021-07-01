package com.ksg.base.view.dialog;

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

import com.ksg.base.service.CodeService;
import com.ksg.base.view.comp.PnCommonCode;


/**

  * @FileName : CommonCodeDetailInsertPop.java

  * @Date : 2021. 3. 19. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :

  */
@SuppressWarnings("serial")
public class CommonCodeDetailInsertPop extends BasePop implements ActionListener{
	
	CodeService codeService;
	
	JButton butOk;
	
	JButton butCancel;
	
	JTextField txfCodeField;
	
	JTextField txfCodeNameEng;
	
	JTextField txfCodeType;
	
	JTextField txfCodeNameKor;

	private PnCommonCode pnCommonCode;
	
	public CommonCodeDetailInsertPop() {
		codeService = new CodeService();
		this.getContentPane().add(createCenter());
		this.getContentPane().add(createNorth(),BorderLayout.SOUTH);
		this.setTitle("코드 추가");
	}
	
	public CommonCodeDetailInsertPop(HashMap<String, Object> item) {
		this();
		txfCodeType.setText((String)item.get("CD_ENG"));
	}
	
	public JPanel createCenter()
	{
		JPanel pnMain = new JPanel();
		
		BoxLayout boxLayout = new BoxLayout(pnMain, BoxLayout.Y_AXIS);
		
		pnMain.setLayout(boxLayout);
		
		txfCodeField = new JTextField(15);
		txfCodeNameEng = new JTextField(15);
		txfCodeType = new JTextField(15);
		txfCodeNameKor = new JTextField(15);
		
		txfCodeType.setEditable(false);
		
		pnMain.add(addComp("코드Field", txfCodeField));
		pnMain.add(addComp("코드영문명", txfCodeNameEng));	
			
		pnMain.add(addComp("코드타입", txfCodeType));
		pnMain.add(addComp("코드명", txfCodeNameKor));
		
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
		
		butOk = new JButton("확인");
		
		butCancel = new JButton("취소");
		
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
			close();
			result = BasePop.CANCEL;
		}
		else if(command.equals("확인"))
		{
			String codeField = txfCodeField.getText();
			String codeNameEng = txfCodeNameEng.getText();
			String codeType = txfCodeType.getText();
			String codeNameKor = txfCodeNameKor.getText();
			
			if(codeField.equals("")||codeNameEng.equals("")||codeNameKor.equals("")) {
				JOptionPane.showMessageDialog(this, "항목을 입력하십시요");
				return;
			}
			
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("code_field", codeField);
			param.put("code_name", codeNameEng);
			param.put("code_type", codeType);
			param.put("code_name_kor", codeNameKor);
			
			
			try {
				codeService.insertCodeD(param);
				result = BasePop.OK;
				close();
				pnCommonCode.fnSearchDetail(codeType);
				
			} catch (SQLException e1) {
				
				if(e1.getErrorCode()==2627)
				{
					JOptionPane.showMessageDialog(this, "동일한 코드가 존재합니다.");	
				}
				else
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, e1.getMessage());	
				}
				
				
				
			}
			
			
		}
		
	}

	public void showPop(PnCommonCode pnCommonCode) {
		super.showPop();
		this.pnCommonCode =pnCommonCode;
		
	}

}

