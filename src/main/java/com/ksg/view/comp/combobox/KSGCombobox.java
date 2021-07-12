package com.ksg.view.comp.combobox;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

import com.ksg.base.service.CodeService;
import com.ksg.view.comp.table.KSGTableColumn;

@SuppressWarnings("serial")
public class KSGCombobox extends JComboBox<KSGTableColumn>{
	
	CodeService codeService;
	public KSGCombobox(String type) {
		
	}
	
	private void init(String param)
	{
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		try {
			HashMap<String, Object>re =(HashMap<String, Object>) codeService.selectCodeDList(params);
			
			List<Map<String, Object>> li = (List<Map<String, Object>>) re.get("master");
			
			
			for(Map<String, Object> item:li)
			{
				
				this.addItem(new KSGTableColumn((String)item.get("code_name"), (String)item.get("code_name_kor")));
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
