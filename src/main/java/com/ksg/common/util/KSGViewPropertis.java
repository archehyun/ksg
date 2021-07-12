package com.ksg.common.util;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;

/**

  * @FileName : KSGViewPropertis.java

  * @Project : KSG2

  * @Date : 2021. 7. 8. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 화면 관련 설정 관리

  */
@SuppressWarnings("serial")
public class KSGViewPropertis extends Properties{
	
	private static KSGViewPropertis ksgPropeties;
	
	String resource = "config/table.properties";

	private KSGViewPropertis()
	{	
		
        
        
        try {
            Reader reader = Resources.getResourceAsReader(resource);
            load(reader);
     
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public static KSGViewPropertis getInstance()
	{
		if(ksgPropeties == null)

		{
			ksgPropeties = new KSGViewPropertis();
		}

		return ksgPropeties;
	}	
	

}
