package com.ksg.view.comp.button;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import com.ksg.service.PageService;
import com.ksg.view.comp.panel.KSGPageTablePanel;

public class PageAction extends AbstractPageAction
{
	HashMap<String, Object> commandMap;
	
	private PageService pageService;
	
	private KSGPageTablePanel tableH;
	
	public PageAction(KSGPageTablePanel tableH,PageService pageService)
	{
		this.tableH = tableH;
		this.pageService = pageService;
	}
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		int page = tableH.getPage();
		
		int endPage = tableH.getTotalPage();

		int page_size = tableH.getPageSize();

		if(commandMap == null) commandMap = new HashMap<String, Object>();
		
		commandMap.put("PAGE_SIZE", page_size);

		if (command.equals("Next")) {

			if (page < endPage) {
				commandMap.put("PAGE_NO", page + 1);

			} else {
				return;
			}

		} else if (command.equals(FORWORD)) {
			if (page > 1) {
				commandMap.put("PAGE_NO", page - 1);

			} else {
				return;
			}
		} else if (command.equals(GO_PAGE)) {
			//				commandMap.put("PAGE_NO", page;
		}

		else if (command.equals(FIRST)) {
			commandMap.put("PAGE_NO", 1);
		}

		else if (command.equals(LAST)) {
			commandMap.put("PAGE_NO", endPage);
		}

		try {
			
			HashMap<String, Object> resultMap = (HashMap<String, Object>) pageService.selectListByPage(commandMap);
			resultMap.put("PAGE_NO", commandMap.get("PAGE_NO"));

			tableH.setResultData(resultMap);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	public void setSearchPram(HashMap<String, Object> searchParam) {
		this.commandMap = searchParam;
		
	}
	
}
