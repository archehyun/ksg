package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Code;
import com.ksg.service.BaseService;
import com.ksg.workbench.common.comp.panel.KSGPanel;

public class PnCheckPort extends PnOption{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JList listKeyword;
	
	private BaseService baseService;

	public PnCheckPort(PreferenceDialog preferenceDialog) {
		
		super(preferenceDialog);
		
		this.setName("확인 항구명 목록");
		
		baseService =DAOManager.getInstance().createBaseService();
		
		this.addComponentListener(this);
		
		this.setLayout(new BorderLayout());
		
		this.add(buildCenter(),BorderLayout.CENTER);
		
		
	}
	
	public KSGPanel buildCenter()
	{
		
		listKeyword = new JList();
		
		KSGPanel pnKeyWordTypeOption = new KSGPanel();
		
		pnKeyWordTypeOption.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		pnKeyWordTypeOption.add(new JLabel("광고정보 입력시에 확인하는 항구명을 입력하십시요 "));
		

		JButton butADD = new JButton("확인 항구  추가");
		
		JButton butDel = new JButton("확인 항구  삭제");
		
		butADD.addActionListener(this);
		
		butDel.addActionListener(this);
		
		Box pnBox =Box.createVerticalBox();
		
		pnBox.add(pnKeyWordTypeOption);
		
		KSGPanel pnKeyList = new KSGPanel();
		
		pnKeyList.setLayout(new BorderLayout());
		
		pnKeyList.add(new JScrollPane(listKeyword));
		
		Box pnKeyControl = Box.createVerticalBox();
		
		
		
		pnKeyControl.add(butADD);
		
		pnKeyControl.add(Box.createGlue());
		
		pnKeyControl.add(butDel);
		
		pnKeyControl.add(Box.createGlue());
		
		KSGPanel pn1 = new KSGPanel();
		
		pn1.add(pnKeyControl);
		
		pnKeyList.add(pn1,BorderLayout.EAST);
		
		pnBox.add(pnKeyList);
		
		
		KSGPanel pnMain=new KSGPanel(new BorderLayout());
		
		pnMain.add(pnBox);
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,15, 5,5));
		
		return pnMain;
	}
	private void updateKeyWordList(List keyList) {

		Iterator ite = keyList.iterator();
		DefaultListModel listModel = new DefaultListModel();
		while(ite.hasNext())
		{
			listModel.addElement(ite.next());
		}


		listKeyword.setModel(listModel);
	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인 항구  추가"))
		{
			String result=JOptionPane.showInputDialog(preferenceDialog, "확인 항구명을 를 입력하세요");
			if(result!=null&&result.length()>0)
			{
				
				Code code_info = new Code();
				code_info.setCode_type("port_check");
				code_info.setCode_field(result);
				code_info.setCode_name(result);
				code_info.setCode_name_kor(result);
				
				try {
					baseService.insertCode(code_info);
					DefaultListModel model =(DefaultListModel) listKeyword.getModel();

					model.addElement(result);
				} catch (SQLException e1) {
					if(e1.getErrorCode()==2627)
					{
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "존재하는 확인 항구명  입니다.");
					}

					e1.printStackTrace();
				}
			}else
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "확인 항구명를 입력하세요");
			}
		}
		else if(command.equals("확인 항구  삭제"))
		{
			if(listKeyword.getSelectedValue()!=null)
			{
				int result=JOptionPane.showConfirmDialog(preferenceDialog, listKeyword.getSelectedValue()+" 항목을 삭제 하시겠습니까?","Key Word 삭제",JOptionPane.YES_NO_OPTION);
				if(result==JOptionPane.OK_OPTION)
				{
					try {
						Code code_info = new Code();
						code_info.setCode_type("port_check");
						code_info.setCode_field(listKeyword.getSelectedValue().toString());
						
						baseService.deleteCode(code_info);
						DefaultListModel model = (DefaultListModel) listKeyword.getModel();
						model.removeElement(listKeyword.getSelectedValue());
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "에러:"+e1.getMessage());
					}
				}
			}else
			{
				JOptionPane.showMessageDialog(preferenceDialog, "선택된 Key Word가 없습니다");
			}
		}
		
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		
		Code code_info = new Code();
		code_info.setCode_type("port_check");
		try {
			updateKeyWordList(baseService.getCodeInfoList(code_info));
		} catch (SQLException ee) {
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "에러:"+ee.getMessage());
			ee.printStackTrace();
		}
		
	}

	public void saveAction() {
		// TODO Auto-generated method stub
		
	}

}
