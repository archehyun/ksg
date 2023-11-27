package com.ksg.workbench.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import com.dtp.api.control.MainController;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
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
	
	private String img_url = "images/icon/";
	
	private JButton menuButton;
	
	private List<KSGPanel> viewList;
	
	private JTabbedPane pane;
	
	private JPanel pnMain;
	
	LayeredPanel layeredPanel;
	
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
		
		layeredPanel = new LayeredPanel();
		
		initMenuArrowIcon();
		
		   menuButton.putClientProperty(FlatClientProperties.STYLE, ""
	                + "background:$Menu.button.background;"
	                + "arc:999;"
	                + "focusWidth:0;"
	                + "borderWidth:0");
		   
	        menuButton.addActionListener((ActionEvent e) -> {
	            setMenuFull(!pnLeft.isMenuFull());
	        });
	        
		initMenuEvent();
		
		viewList = new ArrayList<KSGPanel>();
		
		this.setController(new MainController());
		
		
        initMenuEvent();
        
        
        layeredPanel.setLayer(menuButton, JLayeredPane.POPUP_LAYER);
        layeredPanel.add(menuButton);
        layeredPanel.add(pnLeft);
        layeredPanel.add(pnCenter());
        
        this.add(s?layeredPanel:pane);
	}
	
	 private void setMenuFull(boolean full) {
	        String icon;
	        if (getComponentOrientation().isLeftToRight()) {
	            icon = (full) ? "menu_left.svg" : "menu_right.svg";
	        } else {
	            icon = (full) ? "menu_right.svg" : "menu_left.svg";
	        }
	        menuButton.setIcon(new FlatSVGIcon(img_url + icon, 0.8f));
	        
	        pnLeft.setMenuFull(full);
	        
	        revalidate();
	    }
	
	
	
    private void initMenuArrowIcon() {
        if (menuButton == null) {
            menuButton = new JButton();
        }
        String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
        
        menuButton.setIcon(new FlatSVGIcon(img_url + icon, 0.8f));
    }
	
	public JComponent pnCenter()
	{
		pnMain = new JPanel(new BorderLayout());
		
		pane = new KSGTabedPane();
		
		pnMain.add(pane);
		
		return pnMain;
		
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
			
			if(pn == null) return;
			
			pn.setName(menuId);

			addView(menuId, pn);
			
		}
	}
	 private class MainFormLayout implements LayoutManager {

	        @Override
	        public void addLayoutComponent(String name, Component comp) {
	        }

	        @Override
	        public void removeLayoutComponent(Component comp) {
	        }

	        @Override
	        public Dimension preferredLayoutSize(Container parent) {
	            synchronized (parent.getTreeLock()) {
	                return new Dimension(5, 5);
	            }
	        }

	        @Override
	        public Dimension minimumLayoutSize(Container parent) {
	            synchronized (parent.getTreeLock()) {
	                return new Dimension(0, 0);
	            }
	        }

	        @Override
	        public void layoutContainer(Container parent) {
	            synchronized (parent.getTreeLock()) {
	                boolean ltr = parent.getComponentOrientation().isLeftToRight();
	                Insets insets = UIScale.scale(parent.getInsets());
	                int x = insets.left;
	                int y = insets.top;
	                int width = parent.getWidth() - (insets.left + insets.right);
	                int height = parent.getHeight() - (insets.top + insets.bottom);
	                int menuWidth = UIScale.scale(pnLeft.isMenuFull() ? pnLeft.getMenuMaxWidth() : pnLeft.getMenuMinWidth());
	                int menuX = ltr ? x : x + width - menuWidth;
	                pnLeft.setBounds(menuX, y, menuWidth, height);
	                int menuButtonWidth = menuButton.getPreferredSize().width;
	                int menuButtonHeight = menuButton.getPreferredSize().height;
	                int menubX;
	                if (ltr) {
	                    menubX = (int) (x + menuWidth - (menuButtonWidth * (pnLeft.isMenuFull() ? 0.5f : 0.3f)));
	                } else {
	                    menubX = (int) (menuX - (menuButtonWidth * (pnLeft.isMenuFull() ? 0.5f : 0.7f)));
	                }
	                menuButton.setBounds(menubX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
	                int gap = UIScale.scale(5);
	                int bodyWidth = width - menuWidth - gap;
	                int bodyHeight = height;
	                int bodyx = ltr ? (x + menuWidth + gap) : x;
	                int bodyy = y;
	                pnMain.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
	            }
	        }
	    }
	 
	 class LayeredPanel extends JLayeredPane
	 {
		 public LayeredPanel() {
			 setBorder(new EmptyBorder(5, 5, 5, 5));
		        
		     setLayout(new MainFormLayout());
		}
	 }
}
