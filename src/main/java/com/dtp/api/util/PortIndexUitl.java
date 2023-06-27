package com.dtp.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

public class PortIndexUitl {
	
	/**
	 * 1. portCount�� �̿��� �ű� �ε��� ����
	 * 2. fromIndexArray�� ���� �� �ű� �ε��� ����
	 * 3. ������(#)�� ������ ���� ����
	 * 
	 * @param fromIndexArray
	 * @param pCount
	 * @return
	 */
	public static String creatIndexString(List<Integer> fromIndexArray, int pCount) {

		List<Integer> new_index=IntStream.rangeClosed(1, pCount)
										.boxed()
										.collect(Collectors.toList());

		List toIndexArray = (List) new_index.stream()
											.filter(o -> fromIndexArray.stream()
																		.noneMatch(n -> o==n))
											.collect(Collectors.toList());

		return StringUtils.join(toIndexArray, "#");
	}

	public static List extractIntArray(JTextField txf) throws NumberFormatException
	{
		String txt = txf.getText();

		StringTokenizer stringTokenizer = new StringTokenizer(txt,"#");

		ArrayList<Integer> intCountArray = new ArrayList<Integer>();

		while(stringTokenizer.hasMoreTokens())
		{
			int in = Integer.parseInt(stringTokenizer.nextToken());

			intCountArray.add(in);
		}

		return intCountArray;
	}

}
