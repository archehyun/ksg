package com.ksg.view.comp;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;

public class KSGViewUtil extends Properties{

    private static KSGViewUtil ksgPropeties;
	
	String resource = "config/setting.properties";

	private KSGViewUtil()
	{   
        try {
            Reader reader = Resources.getResourceAsReader(resource);
            load(reader);
     
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public static KSGViewUtil getInstance()
	{
		if(ksgPropeties == null)

		{
			ksgPropeties = new KSGViewUtil();
		}

		return ksgPropeties;
	}	
	
    
}
