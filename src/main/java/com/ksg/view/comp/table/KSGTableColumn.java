package com.ksg.view.comp.table;

import javax.swing.table.TableColumn;

import mycomp.comp.table.MyTableColumn;

/**

  * @FileName : KSGTableColumn.java

  * @Date : 2021. 2. 24. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 사용자 정의 테이블 컬럼

  */
@SuppressWarnings("serial")
public class KSGTableColumn extends MyTableColumn{
	
	public KSGTableColumn() {

	}
	public KSGTableColumn(String columnField, String columnName) {
		this.columnField = columnField;
		this.columnName = columnName;
	}

	public KSGTableColumn(String columnField, String columnName, int size) {
		this(columnField, columnName);
		
		this.size = size;
	}
	
	public KSGTableColumn(String columnField, String columnName, int size, int alignment) {
		this(columnField, columnName, size);
		
		this.ALIGNMENT = alignment;
	}
	
	public Object getValue(Object obj)
	{	return obj;
	}
}
