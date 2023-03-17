package com.ksg.view.comp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
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
	public void store()
	{
		try {
			//TODO 저장 위치 확인
			 URL url=Resources.getResourceURL(resource);
			 System.out.println("url:"+url);
			this.store(new FileOutputStream(url.getPath()), "no commments");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    
}
