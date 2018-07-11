package com.ksg.schedule.logic.joint;

/**
 * 스케줄 생성시 필요한 기능 제공
 * @author 박창현
 *
 */
public class ScheduleBuildUtil {
	
	
	private ScheduleBuildUtil()
	{
		
	}
	
	public static int getNumericVoyage(String voyage_num)
	{
		int result=0;

		String temp="";
		
		if(voyage_num==null)
			return 0;
		for(int i=0;i<voyage_num.length();i++)
		{
			try{
				temp+=Integer.parseInt(String.valueOf(voyage_num.charAt(i)));
			}catch(NumberFormatException e)
			{
				//				return 0;
			}
		}
		try{
			result=Integer.valueOf(temp);
		}catch(Exception e)
		{
			return 0;
		}

		return result;
	}
}
