package com.ksg.view.comp;

import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;

import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.view.comp.table.KSGTableColumn;

/**

  * @FileName : KSGComboBox.java

  * @Project : KSG2

  * @Date : 2022. 3. 25. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class KSGComboBox extends JComboBox<KSGTableColumn>{

	String codeType;

	CodeServiceImpl service;
	
	private boolean isShowTotal = false;
	
	
	
	public void setShowTotal(boolean isShowTotal) {
		this.isShowTotal = isShowTotal;
	}

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
			this.removeAllItems();
			param.put("code_type", codeType);
			HashMap<String,Object> resullt = (HashMap<String, Object>) service.selectCodeDList(param);
			List<HashMap<String,Object> > li = (List<HashMap<String, Object>>) resullt.get("master");
			
			if(isShowTotal) 			addItem(new KSGTableColumn("","전체"));
			
			for(HashMap<String, Object> item:li)
			{
				this.addItem(new KSGTableColumn(String.valueOf(item.get("code_field")), String.valueOf(item.get("code_name"))));
			}
		} catch (Exception e) {

			e.printStackTrace();
		}


	}


}
