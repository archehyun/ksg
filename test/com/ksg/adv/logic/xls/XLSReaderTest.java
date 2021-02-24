package com.ksg.adv.logic.xls;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class XLSReaderTest {

	XLSReader reader;

	@Test
	public void testRead() {

		try {
			reader = new XLSReader();
			String sheetNameList[]= {"sheet1","sheet2"};

			reader.read(sheetNameList, "D://test.xls");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
