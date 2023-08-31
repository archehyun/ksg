package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
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

import com.dtp.api.control.CodeController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Code;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.panel.KSGPanel;

public class PnPortExcpetion2 extends PnOption {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JList<Code> listKeyword;

	private JButton butADD;

	private JButton butDel;

	public PnPortExcpetion2(PreferenceDialog preferenceDialog) {

		super(preferenceDialog);

		this.setName("예외 항구명 지정");		

		this.setController(new CodeController());

		this.addComponentListener(this);

		this.initComp();

		this.setLayout(new BorderLayout());

		this.add(buildCenter(),BorderLayout.CENTER);
	}

	private void initComp()
	{
		listKeyword = new JList<Code>();

		butADD = new KSGGradientButton("예외 항구  추가");

		butDel = new KSGGradientButton("예외 항구  삭제");

		butADD.addActionListener(this);

		butDel.addActionListener(this);
	}

	public KSGPanel buildCenter()
	{
		KSGPanel pnKeyWordTypeOption = new KSGPanel();

		pnKeyWordTypeOption.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnKeyWordTypeOption.add(new JLabel("광고정보 입력시에는 사용되고 스케줄 생성시에는 사용되지 않는 항구명을 입력하십시요 "));

		Box pnBox =Box.createVerticalBox();

		pnBox.add(pnKeyWordTypeOption);

		KSGPanel pnKeyList = new KSGPanel();

		pnKeyList.setLayout(new BorderLayout());

		pnKeyList.add(new JScrollPane(listKeyword));

		Box pnKeyControl = Box.createVerticalBox();

		pnKeyControl.add(butADD);

		pnKeyControl.add(Box.createVerticalStrut(15));

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

	private void fnInsert(String code_value)
	{
		CommandMap param = new CommandMap();
		
		param.put("code_type", "port_exception");
		
		param.put("code_field", code_value);
		
		param.put("code_name", code_value);
		
		param.put("code_name_kor", code_value);
		
		callApi("insertCodeDetail", param);	
	}
	private void fnDelete(String code_field, String code_name)
	{
		CommandMap param = new CommandMap();
		
		param.put("code_name", code_name);
		
		param.put("code_field", code_field);
		
		callApi("deleteCodeDetail", param);	
	}

	private void fnSearch()
	{
		CommandMap param = new CommandMap();
		
		param.put("code_type", "port_exception");
		
		callApi("pnCheckPort.selectCodeDetailList", param);	
	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals("예외 항구  추가"))
		{
			String result=JOptionPane.showInputDialog(preferenceDialog, "예외 항구명을 를 입력하세요");

			if(result!=null&&result.length()>0)
			{
				fnInsert(result);
			}else
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "예외 항구명를 입력하세요");
			}
		}
		else if(command.equals("예외 항구  삭제"))
		{
			if(listKeyword.getSelectedValue()!=null)
			{
				int result=JOptionPane.showConfirmDialog(preferenceDialog, listKeyword.getSelectedValue()+" 항목을 삭제 하시겠습니까?","Key Word 삭제",JOptionPane.YES_NO_OPTION);

				if(result==JOptionPane.OK_OPTION)
				{

					Code selectedCode=(Code) listKeyword.getSelectedValue();

					fnDelete(selectedCode.getCode_field(), selectedCode.getCode_name());
				}
			}else
			{
				JOptionPane.showMessageDialog(preferenceDialog, "선택된 Key Word가 없습니다");
			}
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {

		fnSearch();
	}

	public void saveAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("pnCheckPort.selectCodeDetailList".equals(serviceId)) {

			List<Code> data = (List) result.get("data");

			DefaultListModel listModel = new DefaultListModel();

			Iterator<Code> iter = data.iterator();

			while(iter.hasNext())
			{
				listModel.addElement(iter.next());
			}

			listKeyword.setModel(listModel);
		}
		else  {

			fnSearch();
			// 정렬 추가
		}
	}
}
