package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.ksg.service.MemberService;
import com.ksg.service.impl.MemberServiceImpl;
import com.ksg.view.comp.button.PageAction;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPageTablePanel;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.MemberInsertDialog;
import com.ksg.workbench.master.dialog.MemberUpdateDialog;

/**

  * @FileName : PnMember.java

  * @Project : KSG2

  * @Date : 2022. 3. 10. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class PnMember extends PnBase implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1373848499609251147L;

	private KSGPageTablePanel tableH;
	
	private MemberService service = new MemberServiceImpl();
	
	public PnMember(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		this.setLayout(new BorderLayout());
		this.add(buildCenter());
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}

	private JComponent buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		tableH = new KSGPageTablePanel("사용자목록");
		
		tableH.addColumn(new KSGTableColumn("member_id", "사용자 ID",200));
		
		tableH.addColumn(new KSGTableColumn("member_name", "사용자명",200));
		
		tableH.setShowControl(true);
		
		tableH.addPageActionListener(new PageAction(tableH, service));
		
		tableH.addMouseListener(new TableSelectListner());
		
		tableH.addContorlListener(this);		
		
		tableH.initComp();
		
		pnMain.add(tableH);		
		
		pnMain.add(buildNorthPanel(),BorderLayout.NORTH);
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));
		
		return pnMain;
	}

	private Component buildNorthPanel() {
		KSGPanel pnSearch = new KSGPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butSerach = new JButton("조회");
		butSerach.addActionListener(this);
		pnSearch.add(butSerach);
		
		
		KSGPanel pnMain= new KSGPanel(new BorderLayout());
		pnMain.add(buildLine(),BorderLayout.SOUTH);
		pnMain.add(pnSearch,BorderLayout.EAST);
		pnMain.add(buildTitleIcon("사용자 정보"),BorderLayout.WEST);
		return pnMain;
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("조회"))			
		{
			fnSearch();
		}
		else if(command.equals(KSGPageTablePanel.INSERT))
		{
			insertAction();
		}
		else
		{
			int row=tableH.getSelectedRow();
			if(row<0)
				return;


			HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(row);

			String vessel_name = (String) item.get("member_id");
			int result=JOptionPane.showConfirmDialog(this,vessel_name+"를 삭제 하시겠습니까?", " 정보 삭제", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.OK_OPTION)
			{	
				try {

					service.deleteMember(item);

					JOptionPane.showMessageDialog(this, "삭제되었습니다.");

					fnSearch();					


				} catch (SQLException e1) {

					e1.printStackTrace();

					JOptionPane.showConfirmDialog(this, e1.getMessage());
				}
			}
		}
		
	}





	private void insertAction() {
		KSGDialog dialog = new MemberInsertDialog();
		dialog.createAndUpdateUI();
		if(dialog.result==KSGDialog.SUCCESS)
		{
			fnSearch();
		}
		
	}

	@Override
	public void fnSearch() {

		try {
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		

		int page_size = tableH.getPageSize();
		
		param.put("PAGE_SIZE", page_size);
		
		param.put("PAGE_NO", 1);
		
		HashMap<String, Object> result = (HashMap<String, Object>) service.selectListByPage(param);
		
		result.put("PAGE_NO", 1);

		
		
		tableH.setResultData(result);
		
		}catch(Exception e)
		
		{
			e.printStackTrace();
		}
		
	}
	
	class TableSelectListner extends MouseAdapter
	{
		KSGDialog dialog;
		
		public void mouseClicked(MouseEvent e) 
		{
			
			if(e.getClickCount()>1)
			{
				JTable es = (JTable) e.getSource();
				int row=es.getSelectedRow();

				if(row<0)
					return;
				
				
				HashMap<String, Object> member=(HashMap<String, Object>) tableH.getValueAt(row);

				dialog = new MemberUpdateDialog(member);
				
				dialog .createAndUpdateUI();
				
				int result = dialog.result;
				
				if(result==MemberUpdateDialog.SUCCESS)
				{
					fnSearch();
				}
				


			}
		}

	}

	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		
	}



}
