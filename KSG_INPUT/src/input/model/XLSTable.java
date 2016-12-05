package input.model;

import org.apache.poi.ss.usermodel.Sheet;

public class XLSTable extends InputLocation{
	
	private Sheet sheet;

	public XLSTable(Sheet sheet) {
		
		this.sheet = sheet;
	}

}
