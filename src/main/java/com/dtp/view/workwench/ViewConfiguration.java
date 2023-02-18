package com.dtp.view.workwench;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.ksg.view.workbench"})
public class ViewConfiguration {
	
	public ViewConfiguration()
	{
		System.out.println("hello");
	}


}
