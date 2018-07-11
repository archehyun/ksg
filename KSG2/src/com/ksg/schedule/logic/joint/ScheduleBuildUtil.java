package com.ksg.schedule.logic.joint;

/**
 * ������ ������ �ʿ��� ��� ����
 * @author ��â��
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
