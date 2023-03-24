package com.ksg.view.comp;

import java.awt.Color;
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
			//TODO ���� ��ġ Ȯ��
			 URL url=Resources.getResourceURL(resource);
			 System.out.println("url:"+url);
			this.store(new FileOutputStream(url.getPath()), "no commments");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 public static Color[] getGradientColor(String param)
		{
			String index[] = param.split(",");
			Color colors[] = new Color[index.length];
			for(int i=0;i<index.length;i++)
			{
				colors[i] = Color.decode(index[i].trim());
			}
			return colors;
		}
	
    
}
