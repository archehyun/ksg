package com.ksg.schedule.build;

public class SortUtil {
	
	/**
	 * @param intArray
	 */
	public static void bubbleSort(String[] intArray) {
		int out, in;
		for (out = intArray.length - 1; out > 0; out--) {
			for (in = 0; in < out; in++) {

				if (intArray[in].compareTo(intArray[in + 1])>0) {
					swap(intArray, in, in + 1);
				}
			}
		}
	}
	public static void swap(String[] intArray, int one, int two) {
		String temp = intArray[one];
		intArray[one] = intArray[two];
		intArray[two] = temp;
	}


}
