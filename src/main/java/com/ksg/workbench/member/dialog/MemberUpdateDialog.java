package com.ksg.workbench.member.dialog;



import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

import com.ksg.common.util.ViewUtil;
import com.ksg.service.MemberService;
import com.ksg.service.impl.MemberServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.base.dialog.BaseInfoDialog;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

public class MemberUpdateDialog extends BaseInfoDialog implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTextField txfMemberId;
	JTextField txfMemberName;
	JTextField txfMemberPw;
	
	MemberService service;
	
	HashMap<String, Object> param;

	public MemberUpdateDialog(HashMap<String, Object> param)	
	{
		title ="사용자 정보 수정";
		
		this.addComponentListener(this);
		service = new MemberServiceImpl();
		this.param = param;
		
	}

	
	
	@Override
	public void createAndUpdateUI() {
		this.add(buildTitle(title),BorderLayout.NORTH);
		this.add(buildCenter());
		this.add(buildControl(),BorderLayout.SOUTH);
		
		ViewUtil.center(this, true);
		this.setVisible(true);
		
	}
	
	
	
	private KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		txfMemberId = new JTextField(15);
		txfMemberName = new JTextField(15);
		txfMemberPw = new JTextField(15);
		
		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);		
		pnCenter.add( createFormItem(txfMemberId,"사용자 ID"));
		pnCenter.add( createFormItem(txfMemberName,"사용자 명"));
		pnCenter.add(createFormItem(txfMemberPw,"사용자 패스워드"));
		
		
		pnMain.add(pnCenter);
		return pnMain;
	}
	
	



	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			String member_id = txfMemberId.getText();
			String member_name = txfMemberName.getText();
			String member_password = txfMemberPw.getText();
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			
			param.put("member_id",member_id);
			param.put("member_name",member_name);
			param.put("member_password", member_password);
			try {
				service.insertMember(param);
				this.result =KSGDialog.SUCCESS;
				this.close();
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		else  if(command.equals("취소"))
		{
			this.result =KSGDialog.FAILE;
			this.close();
		}
				
		
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		txfMemberId.setText((String) param.get("member_id"));
		txfMemberName.setText((String) param.get("member_name"));
		txfMemberPw.setText((String) param.get("member_password"));
		
	}
	


}
