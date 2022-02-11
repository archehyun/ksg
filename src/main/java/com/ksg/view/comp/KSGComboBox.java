package com.ksg.view.comp;

import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;

import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.view.comp.table.KSGTableColumn;

public class KSGComboBox extends JComboBox<KSGTableColumn>{

	String codeType;

	CodeServiceImpl service;
	
	public KSGComboBox()
	{
		service = new CodeServiceImpl();
	}

	public KSGComboBox(String codeType)
	{
		this();
		this.codeType = codeType;
	}

	public void initComp()
	{  
		
		if(codeType==null) return;
		
		HashMap<String, Object> param = new HashMap<String, Object>();

		try {


			param.put("code_type", codeType);
			HashMap<String,Object> resullt = (HashMap<String, Object>) service.selectCodeDList(param);
			List<HashMap<String,Object> > li = (List<HashMap<String, Object>>) resullt.get("master");
			for(HashMap<String, Object> item:li)
			{
				this.addItem(new KSGTableColumn(String.valueOf(item.get("code_field")), String.valueOf(item.get("code_name"))));
			}
		} catch (Exception e) {

			e.printStackTrace();
		}


	}


}
