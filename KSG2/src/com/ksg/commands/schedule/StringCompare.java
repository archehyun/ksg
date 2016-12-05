package com.ksg.commands.schedule;

import java.util.Comparator;

public class StringCompare implements Comparator<String> {

	public int compare(String s1, String s2) {
		return s1.compareToIgnoreCase(s2);
	}

}
