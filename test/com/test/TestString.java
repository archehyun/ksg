package com.test;

import static org.junit.Assert.fail;

import org.junit.Test;

public class TestString {
	
	@Test
	public void testSelectList() {
		String testfile = "t.t";
		
		String[] a = testfile.split(".");
		
		System.out.println(a.length);
	}

}
