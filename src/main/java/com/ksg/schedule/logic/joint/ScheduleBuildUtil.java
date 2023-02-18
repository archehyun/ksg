package com.ksg.schedule.logic.joint;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ������ ������ �ʿ��� ��� ����
 * @author ��â��
 *
 */
public class ScheduleBuildUtil {

	
	public static int getNumericVoyage(String voyage_num)
	{	
		try{
			return Integer.valueOf(voyage_num.replaceAll("[^0-9]", ""));
		}catch(Exception e)
		{
			return 0;
		}
	}
	

	/**
	 * @param field
	 * @param table
	 * @return
	 */
	public static int[] makePortArraySub(String field) throws NumberFormatException{
		if(field==null||field.equals("")||field.equals(" "))
			return null;		

		field=field.trim();

		// #�� �������� �ױ� �ε����� ����
		StringTokenizer st = new StringTokenizer(field,"#");

		List<Integer> indexList = new ArrayList<Integer>();

		while(st.hasMoreTokens())
		{	
			indexList.add(Integer.parseInt(st.nextToken().trim()));
		}

		return indexList.stream().mapToInt(i -> i).toArray();

	}
	
}
