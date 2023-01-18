package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;

import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.master.BaseInfoUI;

public class PnPortNew extends PnBase{
	
	KSGTablePanel tableH;

	public PnPortNew(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		
		this.setLayout(new BorderLayout());

		this.addComponentListener(this);

		add(buildSearch(),BorderLayout.NORTH);

		add(buildCenter());
		
	}
	
	private KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel();
		
		
		tableH = new KSGTablePanel("항구 목록");		
		tableH.addColumn(new KSGTableColumn("port_name", "구분"));
		tableH.initComp();
		pnMain.add(tableH);
		//tableH.
		return pnMain;
	}
	private KSGPanel buildSearch()
	{
		KSGPanel pnMain = new KSGPanel();
		return pnMain;
	}
	private KSGPanel buildTreeMenu()
	{
		KSGPanel pnMain = new KSGPanel();
		return pnMain;
	}
	
	private KSGPanel buildDetailTable()
	{
		KSGPanel pnMain = new KSGPanel();
		return pnMain;
	}

	@Override
	public void updateView() {
		 
		
	}

	@Override
	public void fnSearch() {
		
		
	}

}
