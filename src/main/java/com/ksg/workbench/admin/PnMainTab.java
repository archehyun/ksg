package com.ksg.workbench.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JTabbedPane;

import com.dtp.api.control.MainController;
import com.ksg.common.model.CommandMap;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.tabpane.KSGTabedPane;
import com.ksg.workbench.admin.menu.Menu;
import com.ksg.workbench.admin.menu.MenuAction;
import com.ksg.workbench.common.comp.KSGView;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class PnMainTab extends KSGView{
	
	private List<KSGPanel> viewList;
	
	private JTabbedPane pane;
	
	private Menu pnLeft;
	
	String menu_array[][]= {
							{"광고정보 조회"},
							{"광고정보 입력"},
							{"광고정보 출력"},
							{"Schedule 확인","Schedule 확인", "Schedule 확인"},
							{"Schedule 확인"},
							{"공통정보"},
							{"지역정보"},
							{"항구정보"},
							{"선사정보"},
							{"선박정보"},
							{"기초정보관리"}
							};
	
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
    	pnLeft.addMenuEvent((int index, int subIndex, String menuId, MenuAction action) -> {
    		
    		System.out.println("index:"+index+",subIndex:"+subIndex+",menuId:"+menuId);
    		showPanel(menu_array[index][subIndex]);
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
	
	/**
	 * 메뉴 표시
	 * @param menuId
	 * @param menuSubId
	 */
	public void showPanel(String menuId, String menuSubId) {
		
		log.info("show Panel");
		
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

	public void updateView() {
		
		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("showMenu".equals(serviceId)) {

			String menuId=(String) result.get("menuId");
			
			KSGPanel pn= (KSGPanel) result.get("view");
			
			pn.setName(menuId);

			addView(menuId, pn);
			
//			pnLeft.setSelectedMenu(0, 0);

		}
	};
}
