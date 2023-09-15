package com.ksg.workbench.admin.menu;

/**
 *
 * @author Raven
 */
public interface MenuEvent {

    public void menuSelected(int index, int subIndex, String menuId, MenuAction action);
 
}

