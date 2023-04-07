package com.ksg.schedule.execute;

import java.sql.SQLException;

import org.junit.Test;

import com.ksg.schedule.logic.print.ScheduleBuildUtil;

public class CreateNormalSchdeduleCommandNewTest {

	@Test
	public void test() throws SQLException {
		
		
		try
		{
			int [] index=ScheduleBuildUtil.makePortArraySub("1#2t");
			for(int i:index)
			{
				System.out.println(i);
			}
		}catch(NumberFormatException e)
		{
			
			System.out.println(e.getMessage());
		}
		
		
			
	}

}
