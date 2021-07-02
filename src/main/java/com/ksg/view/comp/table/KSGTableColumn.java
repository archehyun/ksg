package com.ksg.view.comp.table;

import javax.swing.table.TableColumn;

/**

  * @FileName : KSGTableColumn.java

  * @Date : 2021. 2. 24. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 사용자 정의 테이블 컬럼

  */
@SuppressWarnings("serial")
public class KSGTableColumn extends TableColumn{
	
	public int size = 0;
	
	public int minSize = 0;
	
	public int maxSize = 0;

	public String columnName;

	public String columnField;

	public int cloumIndex;
	
	public KSGTableColumn() {

	}
	public KSGTableColumn(String columnField, String columnName) {
		this.columnField = columnField;
		this.columnName = columnName;
	}

	public KSGTableColumn(String columnField, String columnName, int size) {
		this.columnField = columnField;
		this.columnName = columnName;
		this.size = size;
	}
	
	public Object getValue(Object obj)
	{
		return obj;
	}


	@Override
	public String toString() {
		return columnName;
	}

}
