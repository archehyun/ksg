package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Code;
import com.ksg.service.BaseService;

public class PnPortExcpetion extends PnOption{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JList listKeyword;
	private BaseService baseService;
	private Font defaultfont;
	
	DAOManager dao;
	
	public PnPortExcpetion(PreferenceDialog preferenceDialog) {
		super(preferenceDialog);
		
		dao = DAOManager.getInstance();
		
		this.setName("���� �ױ��� ����");
		listKeyword = new JList();
		baseService =DAOManager.getInstance().createBaseService();
		JPanel pnKeyWordTypeOption = new JPanel();
		pnKeyWordTypeOption.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnKeyWordTypeOption.add(new JLabel("�������� �Է½ÿ��� ���ǰ� ������ �����ÿ��� ������ �ʴ� �ױ����� �Է��Ͻʽÿ� "));
		

		JButton butADD = new JButton("���� �ױ�  �߰�");
		butADD.addActionListener(this);
		JButton butDel = new JButton("���� �ױ�  ����");
		butDel.addActionListener(this);


		butADD.setFont(defaultfont);
		butDel.setFont(defaultfont);

		
		Box pnMain =Box.createVerticalBox();
		
		pnMain.add(pnKeyWordTypeOption);
		
		JPanel pnKeyList = new JPanel();
		pnKeyList.setLayout(new BorderLayout());
		pnKeyList.add(new JScrollPane(listKeyword));
		Box pnKeyControl = Box.createVerticalBox();
		
		pnKeyControl.add(butADD);		
		pnKeyControl.add(Box.createGlue());
		pnKeyControl.add(butDel);
		pnKeyControl.add(Box.createGlue());
		
		JPanel pn1 = new JPanel();
		pn1.add(pnKeyControl);
		
		pnKeyList.add(pn1,BorderLayout.EAST);
		JPanel pnWest = new JPanel();
		pnWest.setPreferredSize(new Dimension(15,0));
		pnKeyList.add(pnWest,BorderLayout.WEST);
		pnMain.add(pnKeyList);
		
		this.setLayout(new BorderLayout());
		this.add(pnMain,BorderLayout.CENTER);
		Code code_info = new Code();
		code_info.setCode_type("port_exception");
		try {
			updateKeyWordList(baseService.getCodeInfoList(code_info));
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "����:"+e.getMessage());
			e.printStackTrace();
		}
		
	}
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		if(command.equals("���� �ױ�  �߰�"))
		{
			String result=JOptionPane.showInputDialog(preferenceDialog, "���� �ױ����� �� �Է��ϼ���");
			if(result!=null&&result.length()>0)
			{
				Code code_info = new Code();
				code_info.setCode_type("port_exception");
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
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�����ϴ� �����ױ���  �Դϴ�.");
					}

					e1.printStackTrace();
				}
			}else
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�����ױ��� �Է��ϼ���");
			}
		}
		else if(command.equals("���� �ױ�  ����"))
		{
			if(listKeyword.getSelectedValue()!=null)
			{
				int result=JOptionPane.showConfirmDialog(preferenceDialog, listKeyword.getSelectedValue()+" �׸��� ���� �Ͻðڽ��ϱ�?","Key Word ����",JOptionPane.YES_NO_OPTION);
				if(result==JOptionPane.OK_OPTION)
				{
					try {
						Code selectedPortInfo = (Code) listKeyword.getSelectedValue();
						Code code_info = new Code();
						code_info.setCode_type("port_exception");
						code_info.setCode_field(selectedPortInfo.getCode_field());
						
						int deleteCount=baseService.deleteCode(code_info);
						
						if(deleteCount>0)
						{
						DefaultListModel model = (DefaultListModel) listKeyword.getModel();
						model.removeElement(listKeyword.getSelectedValue());
						}
						else
						{
							JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "���� �Ǽ� 0,input:"+code_info.getCode_field());
						}
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "����:"+e1.getMessage());
					}
				}
			}else
			{
				JOptionPane.showMessageDialog(preferenceDialog, "���õ� Key Word�� �����ϴ�");
			}
		}
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
	

	public void saveAction() {}

}
