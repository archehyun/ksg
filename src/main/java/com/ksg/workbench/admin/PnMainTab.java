package com.ksg.workbench.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JTabbedPane;

import com.dtp.api.control.MainController;
import com.ksg.common.model.CommandMap;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.tabpane.KSGTabedPane;
import com.ksg.workbench.common.comp.KSGView;


public class PnMainTab extends KSGView{
	
	private List<KSGPanel> viewList;
	
	private JTabbedPane pane;
	
	private Menu pnLeft;
	
	public PnMainTab()
	{
		super();
		
		boolean s =Boolean.valueOf( viewPropeties.getProperty("view.showleft"));
		
		this.setLayout(new BorderLayout());
		
		pane = new KSGTabedPane();
		
		pnLeft = new Menu();
		
		pnLeft.setPreferredSize(new Dimension(250,1000));
//		
//		pnLeft.setBackground(Color.decode("#f5ffff"));

		initMenuEvent();
		
		this.add(pane);
		
		if(s) this.add(pnLeft, BorderLayout.WEST);
		
		viewList = new ArrayList<KSGPanel>();
		
		this.setController(new MainController());
	}
	
    private void initMenuEvent() {
    	pnLeft.addMenuEvent((int index, int subIndex, MenuAction action) -> {
    		System.out.println(1);
        });
    }
	
	public void showPanel(String menuId) {
		
		int index =pane.indexOfTab(menuId);
		
		if(index== -1)
		{
			Optional<KSGPanel> pn = viewList.stream()
					.filter(o -> menuId.equals( o.getName()))
					.findFirst();
			
			if(!pn.isPresent())
			{
				CommandMap param = new CommandMap();

				param.put("menuId", menuId);

				callApi("showMenu", param);	
			}
			else
			{
				addTab(menuId, pn.get());
			}
		}
		else
		{
			pane.setSelectedIndex(index);
		}
	}
	
	public void showPanel(String menuId, String menuSubId) {
		
		int index =pane.indexOfTab(menuId);
		
		if(index== -1)
		{
			Optional<KSGPanel> pn = viewList.stream()
					.filter(o -> menuId.equals( o.getName()))
					.findFirst();
			
			if(!pn.isPresent())
			{
				CommandMap param = new CommandMap();

				param.put("menuId", menuId);
				param.put("menuSumId", menuSubId);

				callApi("showMenu", param);	
			}
			else
			{
				addTab(menuId, pn.get());
			}
		}
		else
		{
			pane.setSelectedIndex(index);
		}
	}
	
	private void addTab(String title, Component comp)
	{
		pane.addTab(title, comp);
		
		pane.setSelectedIndex(pane.getTabCount()-1);
	}
	
	private int getIndex(String name)	
	{
		 int count = pane.getTabCount();
		 
		 for (int i = 0; i < count; i++) {
			 String label = pane.getTitleAt(i);
			 if(name.equals(label)) return i;
		 }
		 return -1;
	}

	public void addView(String menuId,KSGPanel pn) {
		
		this.viewList.add(pn);
		
		addTab(menuId, pn);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	public void updateView() {
		
		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("showMenu".equals(serviceId)) {

			String menuId=(String) result.get("menuId");
			
			KSGPanel pn= (KSGPanel) result.get("view");
			
			pn.setName(menuId);

			addView(menuId, pn);
			
			pnLeft.setSelectedMenu(0, 0);

		}
	};
}
