package com.ksg.workbench.member.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.table.TableColumnModel;

import com.ksg.service.MemberService;
import com.ksg.service.impl.MemberServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.comp.PnBase;
import com.ksg.workbench.common.comp.button.PageAction;
import com.ksg.workbench.schedule.comp.KSGPageTablePanel;

/**

  * @FileName : PnMember.java

  * @Project : KSG2

  * @Date : 2022. 3. 10. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class PnMember extends PnBase implements ActionListener{
	
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
		
		tableH.addColumn(new KSGTableColumn("member_id", "사용자 ID",100));
		
		tableH.addColumn(new KSGTableColumn("member_name", "사용자명",100));
		
		tableH.setShowControl(true);
		
		tableH.addPageActionListener(new PageAction(tableH, service));
		
		tableH.addContorlListener(this);		
		
		tableH.initComp();
		
		pnMain.add(tableH);		
		
		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
		
		return pnMain;
	}

	private Component buildSearchPanel() {
		KSGPanel pnSearch = new KSGPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butSerach = new JButton("조회");
		butSerach.addActionListener(this);
		pnSearch.add(butSerach);
		return pnSearch;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("조회"))			
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



}
