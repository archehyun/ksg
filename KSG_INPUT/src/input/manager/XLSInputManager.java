package input.manager;

import java.util.Vector;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Sheet;
import input.model.InputLocation;

/**
 * @author archehyun
 *
 */
public class XLSInputManager extends InputManager{
	
	Sheet sheet;
	Vector<InputLocation> initInfos = new Vector<InputLocation>();
	@Override
	public void readFile() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * ���̺� ��ġ Ž��
	 * ��Ʈ ���� ��� ��ġ �˻�
	 */
	private void searchTable()
	{
		int lastRow = sheet.getPhysicalNumberOfRows();
		for(int rowIndex=0;rowIndex<lastRow;rowIndex++)
		{
			HSSFRow row= (HSSFRow) sheet.getRow(rowIndex);
			
			// row �˻�
			if(row==null||row.getZeroHeight())
				continue;
			
			for(int colIndex=0;colIndex<row.getPhysicalNumberOfCells();colIndex++)
			{
				HSSFCell firstCell=row.getCell(colIndex);

				if(firstCell==null||firstCell.getCellType()==HSSFCell.CELL_TYPE_BLANK)
					continue;	
			}

			
		}
	}
	private void extractData()
	{
		
	}
	private void createTableDataList()
	{
		
	}
	public void execute()
	{
		//1. ���̺� ��ȸ
		searchTable();
		//2. ������ ����
		extractData();
		//3. ���̺� ���� ����
		createTableDataList();
	}

}
