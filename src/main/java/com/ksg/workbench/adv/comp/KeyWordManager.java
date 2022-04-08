package com.ksg.workbench.adv.comp;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.DAOManager;
import com.ksg.service.BaseService;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class KeyWordManager {

	private BaseService baseService;
	private static KeyWordManager instance;
	
	private String vesselKeyWord[];
	private String voyageKeyWord[];
	public String bothKeyWord[];
	
	public String[] getVoyageKeyWord() {
		return voyageKeyWord;
	}
	
	public String[] getVesselKeyWord() {
		return vesselKeyWord;
	}

	
	public String[] getBothKeyWord() {
		return bothKeyWord;
	}
	
	private String[] initKeyword(String type) throws SQLException
	{
		List keyList = baseService.getKeywordList(type);
		String[] keyWord = new String[keyList.size()];
		for(int i=0;i<keyList.size();i++)
		{
			keyWord[i]= keyList.get(i).toString();
		}
		return keyWord;
	}
	private KeyWordManager()
	{
		baseService = DAOManager.getInstance().createBaseService();
		try{
		
			vesselKeyWord 	= initKeyword("VESSEL");
			voyageKeyWord 	= initKeyword("VOYAGE");
			bothKeyWord 	= initKeyword("BOTH");

			
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public static KeyWordManager getInstance()
	{
		if(instance==null)
			instance = new KeyWordManager();
		return instance;
	}

}
