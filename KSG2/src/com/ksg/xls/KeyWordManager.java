package com.ksg.xls;

import java.util.List;

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.BaseService;

public class KeyWordManager {

	private BaseService baseService;
	private static KeyWordManager instance;
	private List vesselKeyList;
	private List voyageKeyList;
	private List bothKeyList;
	public List getBothKeyList() {
		return bothKeyList;
	}
	private String vesselKeyWord[];
	public String[] getVesselKeyWord() {
		return vesselKeyWord;
	}
	public String bothKeyWord[];
	
	public String[] getBothKeyWord() {
		return bothKeyWord;
	}
	private KeyWordManager()
	{
		baseService = DAOManager.getInstance().createBaseService();
		try{
			vesselKeyList = baseService.getKeywordList("VESSEL");
			voyageKeyList = baseService.getKeywordList("VOYAGE");
			bothKeyList = baseService.getKeywordList("BOTH");
			
			vesselKeyWord = new String[vesselKeyList.size()];
			bothKeyWord = new String[bothKeyList.size()];

			for(int i=0;i<vesselKeyList.size();i++)
			{
				vesselKeyWord[i]= vesselKeyList.get(i).toString();
			}
			for(int i=0;i<bothKeyList.size();i++)
			{
				bothKeyWord[i]= bothKeyList.get(i).toString();
			}
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
